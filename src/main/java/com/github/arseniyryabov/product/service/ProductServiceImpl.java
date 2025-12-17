package com.github.arseniyryabov.product.service;

import com.github.arseniyryabov.product.controller.model.ProductRequest;
import com.github.arseniyryabov.product.entity.Product;
import com.github.arseniyryabov.product.exception.ProductNotFoundException;
import com.github.arseniyryabov.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(ProductRequest request) {
        Product product = Product.builder()
                .productId(UUID.randomUUID())
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return productRepository.save(product);
    }

    @Override
    public Product getProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(UUID productId, ProductRequest request) {
        Product product = getProductById(productId);

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(UUID productId) {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }

    @Override
    public List<Product> getProductsByIds(List<UUID> productIds) {
        return productIds.stream()
                .map(this::getProductById)
                .collect(Collectors.toList());
    }
}
