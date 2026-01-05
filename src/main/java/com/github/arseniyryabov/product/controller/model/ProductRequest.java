package com.github.arseniyryabov.product.controller.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Название продукта обязательно")
    @Size(max = 255, message = "Название продукта не должно превышать 255 символов")
    private String name;

    @NotNull(message = "Цена продукта обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    private BigDecimal price;

    @NotNull(message = "Количество на складе обязательно")
    @Min(value = 0, message = "Количество не может быть отрицательным")
    private Integer stockQuantity;

    private String description;
}
