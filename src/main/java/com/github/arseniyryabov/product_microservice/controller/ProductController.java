package com.github.arseniyryabov.product_microservice.controller;

import com.github.arseniyryabov.product_microservice.entity.ProductsEntity;
import com.github.arseniyryabov.product_microservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<ProductsEntity> createProduct(@RequestBody ProductsEntity product) {
        ProductsEntity savedProduct = productRepository.save(product);
        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping
    public ResponseEntity<List<ProductsEntity>> getAllProducts() {
        List<ProductsEntity> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }
}
