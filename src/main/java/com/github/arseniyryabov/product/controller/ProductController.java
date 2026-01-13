package com.github.arseniyryabov.product.controller;

import com.github.arseniyryabov.product.controller.model.*;
import com.github.arseniyryabov.product.entity.Product;
import com.github.arseniyryabov.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// REST контроллер для работы с товарами
@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Получение товара по ID (GET /products/{id})
    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable UUID productId) {
        // Если товар не найден, ProductService выбросит ProductNotFoundException,
        // которое будет обработано в GlobalExceptionHandler
        Product product = productService.getProductById(productId);
        return mapToResponse(product);
    }

    // Уменьшение количества товара на складе (POST /products/{id}/decrease-stock)
    @PostMapping("/{productId}/decrease-stock")
    public ResponseEntity<Void> decreaseStock(
            @PathVariable UUID productId,
            @RequestBody @Valid DecreaseStockRequest request) {

        // Если товар не найден или недостаточно товара, будут выброшены исключения
        productService.decreaseStockQuantity(productId, request.getQuantity());
        return ResponseEntity.ok().build();
    }

    // Проверка доступности товара (GET /products/{id}/availability?quantity=...)
    @GetMapping("/{productId}/availability")
    public ResponseEntity<ProductAvailabilityResponse> checkAvailability(
            @PathVariable UUID productId,
            @RequestParam Integer quantity) {

        // Если товар не найден, будет ProductNotFoundException
        boolean isAvailable = productService.isProductAvailable(productId, quantity);
        Product product = productService.getProductById(productId);

        ProductAvailabilityResponse response = ProductAvailabilityResponse.builder()
                .productId(productId)
                .available(isAvailable)
                .stockQuantity(product.getStockQuantity())
                .requestedQuantity(quantity)
                .build();

        return ResponseEntity.ok(response);
    }

    // Пакетное получение товаров (POST /products/batch)
    @PostMapping("/batch")
    public List<ProductResponse> getProductsBatch(@RequestBody List<UUID> productIds) {
        // Некоторые товары могут не существовать - будет возвращен пустой список для них
        List<Product> products = productService.getProductsByIds(productIds);
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Для пакетного получения мы можем также создать специальный метод, который выбрасывает исключение
    @PostMapping("/batch/validate")
    public List<ProductResponse> getProductsBatchWithValidation(@RequestBody List<UUID> productIds) {
        // Проверяем каждый товар - если хоть один не найден, выбрасываем исключение
        productIds.forEach(productService::getProductById);

        List<Product> products = productService.getProductsByIds(productIds);
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .stockQuantity(product.getStockQuantity())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}