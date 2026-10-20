package com.github.arseniyryabov.product.service;


import com.github.arseniyryabov.product.controller.model.ProductRequest;
import com.github.arseniyryabov.product.entity.Product;
import com.github.arseniyryabov.product.exception.ProductNotFoundException;
import com.github.arseniyryabov.product.repository.ProductRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductStockTxService stockTxService;

    // Создание нового товара
    public Product createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .stockQuantity(request.getStockQuantity())
                // createdAt/updatedAt — @PrePersist в Product
                .build();

        return productRepository.save(product);
    }

    // Получение товара по ID
    public Product getProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    // Получение всех товаров
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Обновление информации о товаре
    public Product updateProduct(UUID productId, ProductRequest request) {
        Product product = getProductById(productId);

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setStockQuantity(request.getStockQuantity());
        // updatedAt — @PreUpdate в Product

        return productRepository.save(product);
    }

    // Удаление товара
    public void deleteProduct(UUID productId) {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }

    // Получение списка товаров по списку ID
    public List<Product> getProductsByIds(List<UUID> productIds) {
        return productRepository.findAllById(productIds);
    }

    // Проверка доступности товара (кол-во на складе)
    public boolean isProductAvailable(UUID productId, Integer requestedQuantity) {
        Product product = getProductById(productId);
        return product.getStockQuantity() >= requestedQuantity;
    }

    // Уменьшение количества товара на складе — обёрнуто в retry
    @Retryable(
            retryFor = {
                    OptimisticLockingFailureException.class,
                    OptimisticLockException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    public void decreaseStockQuantity(UUID productId, Integer quantity) {
        stockTxService.decrease(productId, quantity);
    }

    // Увеличение кол-ва товара на складе — обёрнуто в retry
    @Retryable(
            retryFor = {
                    OptimisticLockingFailureException.class,
                    OptimisticLockException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    public void increaseStockQuantity(UUID productId, Integer quantity) {
        stockTxService.increase(productId, quantity);
    }

    // Альтернативный метод, который проверяет наличие всех товаров
    public List<Product> getProductsByIdsWithValidation(List<UUID> productIds) {
        List<Product> products = productRepository.findAllById(productIds);

        Set<UUID> foundIds = products.stream()
                .map(Product::getProductId)
                .collect(Collectors.toSet());

        List<UUID> notFoundIds = productIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());

        if (!notFoundIds.isEmpty()) {
            throw new ProductNotFoundException("Товары с ID " + notFoundIds + " не найдены");
        }

        return products;
    }
}