package com.github.arseniyryabov.product.controller.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO для запроса увеличения количества товара
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncreaseStockRequest {

    @NotNull(message = "Количество обязательно")
    @Min(value = 1, message = "Количество должно быть положительным")
    private Integer quantity;
}
