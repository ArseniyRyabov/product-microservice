package com.github.arseniyryabov.product.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID productId) {
        super("Продукт с ID " + productId + " не найден");
    }
}
