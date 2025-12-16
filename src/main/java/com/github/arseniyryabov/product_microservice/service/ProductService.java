package com.github.arseniyryabov.product_microservice.service;

import com.github.arseniyryabov.product_microservice.dto.request.ProductRequest;
import com.github.arseniyryabov.product_microservice.dto.response.ProductResponse;
import com.github.arseniyryabov.product_microservice.entity.Product;
import com.github.arseniyryabov.product_microservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Создание нового продукта: {}", request.getName());

        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Продукт создан с ID: {}", savedProduct.getProductId());

        return mapToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID productId) {
        log.info("Получение продукта по ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Продукт с ID {} не найден", productId);
                    return new RuntimeException("Продукт не найден");
                });

        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.info("Получение всех продуктов");

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId, ProductRequest request) {
        log.info("Обновление продукта с ID: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Продукт с ID {} не найден для обновления", productId);
                    return new RuntimeException("Продукт не найден");
                });

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());

        Product updatedProduct = productRepository.save(product);
        log.info("Продукт с ID {} успешно обновлен", productId);

        return mapToResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        log.info("Удаление продукта с ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            log.error("Продукт с ID {} не найден для удаления", productId);
            throw new RuntimeException("Продукт не найден");
        }

        productRepository.deleteById(productId);
        log.info("Продукт с ID {} успешно удален", productId);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
