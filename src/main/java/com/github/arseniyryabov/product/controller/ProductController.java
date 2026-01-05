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

    // Создание товара (POST /products)
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        // @Valid включает валидацию данных запроса
        Product product = productService.createProduct(request);
        ProductResponse response = mapToResponse(product);
        // Возвращается статус 201 Created и созданный товар
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Получение товара по ID (GET /products/{id})
    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable UUID productId) {
        Product product = productService.getProductById(productId);
        return mapToResponse(product);
    }

    // Получение всех товаров (GET /products)
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        // Преобразование списка товаров в список DTO
        List<ProductResponse> response = products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Обновление товара (PUT /products/{id})
    @PutMapping("/{productId}")
    public ProductResponse updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductRequest request) {
        Product product = productService.updateProduct(productId, request);
        return mapToResponse(product);
    }

    // Удаление товара (DELETE /products/{id})
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    // Пакетное получение товаров (POST /products/batch)
    @PostMapping("/batch")
    public List<ProductResponse> getProductsBatch(@RequestBody List<UUID> productIds) {
        List<Product> products = productService.getProductsByIds(productIds);
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Преобразование Product в ProductResponse (DTO)
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

    // Уменьшение количества товара на складе (POST /products/{id}/decrease-stock)
    @PostMapping("/{productId}/decrease-stock")
    public ResponseEntity<Void> decreaseStock(
            @PathVariable UUID productId,
            @RequestBody @Valid DecreaseStockRequest request) {

        productService.decreaseStockQuantity(productId, request.getQuantity());
        return ResponseEntity.ok().build();
    }

    // Увеличение количества товара на складе (POST /products/{id}/increase-stock)
    @PostMapping("/{productId}/increase-stock")
    public ResponseEntity<Void> increaseStock(
            @PathVariable UUID productId,
            @RequestBody @Valid IncreaseStockRequest request) {

        productService.increaseStockQuantity(productId, request.getQuantity());
        return ResponseEntity.ok().build();
    }

    // Проверка доступности товара (GET /products/{id}/availability?quantity=...)
    @GetMapping("/{productId}/availability")
    public ResponseEntity<ProductAvailabilityResponse> checkAvailability(
            @PathVariable UUID productId,
            @RequestParam Integer quantity) {

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

    // Пакетная проверка доступности товаров (POST /products/batch/availability)
    @PostMapping("/batch/availability")
    public ResponseEntity<Map<UUID, Boolean>> checkBatchAvailability(
            @RequestBody @Valid BatchAvailabilityRequest request) {

        Map<UUID, Boolean> availabilityMap = new HashMap<>();

        for (BatchAvailabilityRequest.ProductQuantity item : request.getItems()) {
            boolean isAvailable = productService.isProductAvailable(
                    item.getProductId(),
                    item.getQuantity()
            );
            availabilityMap.put(item.getProductId(), isAvailable);
        }

        return ResponseEntity.ok(availabilityMap);
    }
}