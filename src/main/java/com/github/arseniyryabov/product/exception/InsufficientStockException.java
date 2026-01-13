package com.github.arseniyryabov.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(UUID productId, Integer available, Integer requested) {
        super("Недостаточно товара " + productId +
                ". Доступно: " + available + ", запрошено: " + requested);
    }
}
