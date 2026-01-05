package com.github.arseniyryabov.product.controller.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

// DTO для пакетного запроса проверки доступности
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAvailabilityRequest {

    @NotEmpty(message = "Список товаров не может быть пустым")
    private List<ProductQuantity> items;

    // Вложенный класс для информации о товаре и количестве
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductQuantity {

        @NotNull(message = "ID товара обязателен")
        private UUID productId;

        @NotNull(message = "Количество товара обязательно")
        @Min(value = 1, message = "Количество должно быть не менее 1")
        private Integer quantity;
    }
}
