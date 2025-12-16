package com.github.arseniyryabov.product_microservice.service;

import com.github.arseniyryabov.product_microservice.dto.external.OrderResponse;
import com.github.arseniyryabov.product_microservice.dto.external.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalServiceClient {

    private final WebClient userWebClient;
    private final WebClient orderWebClient;

    public Mono<UserResponse> getUserById(Long userId) {
        log.info("Получение пользователя с ID: {}", userId);

        return userWebClient.get()
                .uri("/users/{id}", userId)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(e -> {
                    log.error("Ошибка при получении пользователя: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    public Flux<OrderResponse> getOrdersContainingProduct(UUID productId) {
        log.info("Получение заказов содержащих продукт с ID: {}", productId);

        // Здесь нужен дополнительный endpoint в OrderService
        // Для примера получаем все заказы и фильтруем
        return orderWebClient.get()
                .uri("/api/orders")
                .retrieve()
                .bodyToFlux(OrderResponse.class)
                .filter(order -> order.getItems().stream()
                        .anyMatch(item -> item.getProductId().equals(productId)));
    }
}
