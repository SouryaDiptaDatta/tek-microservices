package com.tp.orderservice.service;

import com.tp.orderservice.client.UserClient;
import com.tp.orderservice.model.Order;
import com.tp.orderservice.model.UserResponse;
import com.tp.orderservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final UserClient userClient;

    public OrderService(
            OrderRepository orderRepository,
            UserClient userClient
    ) {
        this.orderRepository = orderRepository;
        this.userClient = userClient;
    }

    @Retry(name = "userService")
    @CircuitBreaker(
            name = "userService",
            fallbackMethod = "userServiceFallback"
    )
    public Order createOrder(Order order) {

        UserResponse user =
                userClient.getUserById(order.getUserId());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return orderRepository.save(order);
    }

    private Order userServiceFallback(
            Order order,
            Throwable throwable
    ) {

        throw new RuntimeException(
                "User Service is currently unavailable. Order cannot be created.",
                throwable
        );
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found with id: " + id
                        )
                );
    }

    public Order updateOrder(
            Long id,
            Order order
    ) {

        Order existingOrder = getOrderById(id);

        existingOrder.setProduct(order.getProduct());
        existingOrder.setPrice(order.getPrice());
        existingOrder.setUserId(order.getUserId());

        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(Long id) {

        Order existingOrder = getOrderById(id);

        orderRepository.delete(existingOrder);
    }
}