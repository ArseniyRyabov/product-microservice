package com.github.arseniyryabov.product.service;

import com.github.arseniyryabov.product.controller.model.ProductRequest;
import com.github.arseniyryabov.product.entity.Product;
import java.util.List;
import java.util.UUID;


public interface ProductService {
    Product createProduct(ProductRequest request);
    Product getProductById(UUID productId);
    List<Product> getAllProducts();
    Product updateProduct(UUID productId, ProductRequest request);
    void deleteProduct(UUID productId);
    List<Product> getProductsByIds(List<UUID> productIds); // Добавленный метод
}
