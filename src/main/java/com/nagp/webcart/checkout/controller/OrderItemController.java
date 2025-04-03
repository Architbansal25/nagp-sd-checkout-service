package com.nagp.webcart.checkout.controller;

import com.nagp.webcart.checkout.model.OrderItem;
import com.nagp.webcart.checkout.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/order")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping("/add")
    public ResponseEntity<OrderItem> createOrder(@RequestBody OrderItem orderItem) {
        return ResponseEntity.ok(orderItemService.createOrder(orderItem));
    }

    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrders() {
        return ResponseEntity.ok(orderItemService.getAllOrders());
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<List<Map<String, Object>>> getOrdersByUser(@PathVariable String userName) {
        return ResponseEntity.ok(orderItemService.getOrdersByUser(userName));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable Long orderId) {
        Map<String, Object> orderDetails = orderItemService.getOrderById(orderId);
        if (orderDetails.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(orderDetails);
        }
        return ResponseEntity.ok(orderDetails);
    }

    @PutMapping("/update/{orderId}")
    public ResponseEntity<OrderItem> updateOrder(@PathVariable Long orderId, @RequestBody OrderItem orderItem) {
        return ResponseEntity.ok(orderItemService.updateOrder(orderId, orderItem));
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        orderItemService.deleteOrder(orderId);
        return ResponseEntity.ok("Order deleted successfully");
    }
}