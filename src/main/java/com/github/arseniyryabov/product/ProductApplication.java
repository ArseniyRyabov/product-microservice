package com.github.arseniyryabov.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class ProductApplication {

	static void main(String[] args) {
		SpringApplication.run(ProductApplication.class, args);
	}

}