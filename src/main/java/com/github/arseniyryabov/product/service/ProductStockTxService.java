package com.github.arseniyryabov.product.service;

import com.github.arseniyryabov.product.entity.Product;
import com.github.arseniyryabov.product.exception.InsufficientStockException;
import com.github.arseniyryabov.product.exception.ProductNotFoundException;
import com.github.arseniyryabov.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductStockTxService {

    private final ProductRepository productRepository;

    @Transactional
    public void decrease(UUID productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                    productId, product.getStockQuantity(), quantity);
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        // createdAt/updatedAt — @PreUpdate в Product
        // save() не нужен: dirty checking + @Version сгенерируют UPDATE с version = ?
    }

    @Transactional
    public void increase(UUID productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.setStockQuantity(product.getStockQuantity() + quantity);
    }
}
