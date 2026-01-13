package com.github.arseniyryabov.product.service;

import com.github.arseniyryabov.product.controller.model.ProductRequest;
import com.github.arseniyryabov.product.entity.Product;
import com.github.arseniyryabov.product.exception.InsufficientStockException;
import com.github.arseniyryabov.product.exception.ProductNotFoundException;
import com.github.arseniyryabov.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// Сервисный слой для работы с товарами
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // Создание нового товара
    public Product createProduct(ProductRequest request) {
        // Создание объекта товара
        Product product = Product.builder()
                .name(request.getName())          // Название товара
                .price(request.getPrice())        // Цена товара
                .description(request.getDescription())  // Описание товара
                .stockQuantity(request.getStockQuantity())  // Количество на складе
                .build();

        // Сохранение товара в БД и возврат сохраненного объекта
        return productRepository.save(product);
    }

    // Получение товара по ID
    public Product getProductById(UUID productId) {
        // Поиск товара в БД, если не найден - выбрасывается исключение
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    // Получение всех товаров
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Обновление информации о товаре
    public Product updateProduct(UUID productId, ProductRequest request) {
        // Получение существующего товара (если не найден, будет ProductNotFoundException)
        Product product = getProductById(productId);

        // Обновление поля товара
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setStockQuantity(request.getStockQuantity());
        product.setUpdatedAt(LocalDateTime.now());

        // Сохранение обновленного товара
        return productRepository.save(product);
    }

    // Удаление товара
    public void deleteProduct(UUID productId) {
        // Проверка существования товара (если не найден, будет ProductNotFoundException)
        Product product = getProductById(productId);
        productRepository.delete(product);
    }

    // Получение списка товаров по списку ID
    public List<Product> getProductsByIds(List<UUID> productIds) {
        return productRepository.findAllById(productIds);
        // Примечание: этот метод НЕ выбрасывает исключение, если некоторые товары не найдены
        // Он возвращает те, которые найдены
    }

    // Проверка доступности товара (кол-во на складе)
    public boolean isProductAvailable(UUID productId, Integer requestedQuantity) {
        Product product = getProductById(productId);  // Если не найден, будет ProductNotFoundException
        return product.getStockQuantity() >= requestedQuantity;
    }

    // Уменьшение количества товара на складе
    @Transactional
    public void decreaseStockQuantity(UUID productId, Integer quantity) {
        Product product = getProductById(productId);

        // Проверка, достаточно ли товара на складе
        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(productId, product.getStockQuantity(), quantity);
        }

        // Уменьшение кол-ва товара на складе
        product.setStockQuantity(product.getStockQuantity() - quantity);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    // Увеличение кол-ва товара на складе
    @Transactional
    public void increaseStockQuantity(UUID productId, Integer quantity) {
        Product product = getProductById(productId);
        product.setStockQuantity(product.getStockQuantity() + quantity);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    // Альтернативный метод, который проверяет наличие всех товаров
    public List<Product> getProductsByIdsWithValidation(List<UUID> productIds) {
        List<Product> products = productRepository.findAllById(productIds);

        // Проверяем, что все запрошенные товары найдены
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
