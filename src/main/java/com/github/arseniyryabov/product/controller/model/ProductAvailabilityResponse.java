package com.github.arseniyryabov.product.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// DTO для ответа с информацией о доступности товара
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAvailabilityResponse {
    private UUID productId;
    private Boolean available;           // Доступность товара в запрошенном количестве
    private Integer stockQuantity;       // Текущее количество на складе
    private Integer requestedQuantity;   // Запрошенное количество
}