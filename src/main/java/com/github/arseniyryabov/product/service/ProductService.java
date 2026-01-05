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
import java.util.UUID;

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
        // Получение существующего товара
        Product product = getProductById(productId);

        // Обновление поля товара
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setStockQuantity(request.getStockQuantity());
        product.setUpdatedAt(LocalDateTime.now()); // Устанавливается время обновления

        // Сохранение обновленного товара
        return productRepository.save(product);
    }

    // Удаление товара
    public void deleteProduct(UUID productId) {
        // Проверка существования товара
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
}
