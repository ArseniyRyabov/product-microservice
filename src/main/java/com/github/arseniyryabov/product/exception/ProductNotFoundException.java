package com.github.arseniyryabov.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(UUID productId) {
        super("Товар с ID " + productId + " не найден");
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
