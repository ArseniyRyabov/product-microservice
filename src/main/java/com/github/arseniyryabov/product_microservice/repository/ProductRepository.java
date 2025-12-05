package com.github.arseniyryabov.product_microservice.repository;

import com.github.arseniyryabov.product_microservice.entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface  ProductRepository extends JpaRepository<ProductsEntity, UUID> {
}
