package com.nagp.webcart.checkout.service;

import com.nagp.webcart.checkout.model.OrderItem;
import com.nagp.webcart.checkout.model.Shipping;
import com.nagp.webcart.checkout.repository.OrderItemRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final RestTemplate restTemplate;
    private final ShippingService shippingService;

    public OrderItemService(OrderItemRepository orderItemRepository, RestTemplate restTemplate, ShippingService shippingService) {
        this.orderItemRepository = orderItemRepository;
        this.restTemplate = restTemplate;
        this.shippingService = shippingService;
    }

    public OrderItem createOrder(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public List<OrderItem> getAllOrders() {
        return orderItemRepository.findAll();
    }

    public List<Map<String, Object>> getOrdersByUser(String userName) {

        List<OrderItem> orderItems = orderItemRepository.findByUserName(userName);
        List<Map<String, Object>> enrichedCartItems = new ArrayList<>();

        for (OrderItem item : orderItems) {
            String productUrl = "http://product-service.default.svc.cluster.local:9091/products/" + item.getProductId();
            Map<String, Object> result = new LinkedHashMap<>();
            try {
                // Make REST call
                ResponseEntity<Map> response = restTemplate.getForEntity(productUrl, Map.class);
                Map<String, Object> productResponse = response.getBody();
                Optional<Shipping> shippingOpt = shippingService.getShippingById(item.getShippingId());


                // Populate required fields
                result.put("productId", item.getProductId());
                result.put("name", productResponse.get("name"));
                result.put("brand", productResponse.get("brand"));
                result.put("price", productResponse.get("price"));
                result.put("originalPrice", productResponse.get("originalPrice"));
                result.put("discount", productResponse.get("discount"));
                result.put("size", item.getSize());
                result.put("image", productResponse.get("image"));
                result.put("orderId", item.getOrderId());
                result.put("paymentStatus", item.getPaymentStatus());
                result.put("buyAtPrice", item.getBuyAtPrice());
                result.put("coupanCode", item.getCoupanCode());
                result.put("quantity", item.getQuantity());
                result.put("shippingDetails", shippingOpt.map(this::mapShippingDetails));



                result.put("specification", productResponse.get("specification"));

                // Validate available sizes

            } catch (HttpClientErrorException e) {
                // 4xx errors from Product Service
                result.put("error", "Product not found for productId: " + item.getProductId());
                result.put("available", false);
            } catch (ResourceAccessException e) {
                // Connection issue (e.g., Product Service down)
                result.put("error", "Product service unavailable for productId: " + item.getProductId());
                result.put("available", false);
            } catch (Exception e) {
                // Generic fallback
                result.put("error", "Unexpected error occurred for productId: " + item.getProductId());
                result.put("available", false);
            }

            enrichedCartItems.add(result);
        }

        return enrichedCartItems;
    }
    private Map<String, Object> mapShippingDetails(Shipping shipping) {
        Map<String, Object> map = new HashMap<>();
        map.put("firstName", shipping.getFirstName());
        map.put("lastName", shipping.getLastName());
        map.put("country", shipping.getCountry());
        map.put("state", shipping.getState());
        map.put("city", shipping.getCity());
        map.put("pincode", shipping.getPincode());
        map.put("contactNo", shipping.getContactNo());
        map.put("fullAddress", shipping.getFullAddress());
        return map;
    }

    public Map<String, Object> getOrderById(Long orderId) {
        Map<String, Object> result = new LinkedHashMap<>();

        Optional<OrderItem> orderOpt = orderItemRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            result.put("error", "Order not found for orderId: " + orderId);
            return result;
        }

        OrderItem item = orderOpt.get();
        String productUrl = "http://localhost:9091/products/" + item.getProductId();

        try {
            // Product service call
            ResponseEntity<Map> response = restTemplate.getForEntity(productUrl, Map.class);
            Map<String, Object> productResponse = response.getBody();

            // Shipping service call
            Optional<Shipping> shippingOpt = shippingService.getShippingById(item.getShippingId());

            // Populate order + product + shipping details
            result.put("productId", item.getProductId());
            result.put("name", productResponse.get("name"));
            result.put("brand", productResponse.get("brand"));
            result.put("price", productResponse.get("price"));
            result.put("originalPrice", productResponse.get("originalPrice"));
            result.put("discount", productResponse.get("discount"));
            result.put("size", item.getSize());
            result.put("image", productResponse.get("image"));
            result.put("orderId", item.getOrderId());
            result.put("paymentStatus", item.getPaymentStatus());
            result.put("buyAtPrice", item.getBuyAtPrice());
            result.put("coupanCode", item.getCoupanCode());
            result.put("quantity", item.getQuantity());
            result.put("specification", productResponse.get("specification"));
            result.put("shippingDetails", shippingOpt.map(this::mapShippingDetails));

        } catch (HttpClientErrorException e) {
            result.put("error", "Product not found for productId: " + item.getProductId());
            result.put("available", false);
        } catch (ResourceAccessException e) {
            result.put("error", "Product service unavailable for productId: " + item.getProductId());
            result.put("available", false);
        } catch (Exception e) {
            result.put("error", "Unexpected error occurred for productId: " + item.getProductId());
            result.put("available", false);
        }

        return result;
    }


    public OrderItem updateOrder(Long orderId, OrderItem updatedOrder) {
        OrderItem existingOrder = orderItemRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        existingOrder.setUserName(updatedOrder.getUserName());
        existingOrder.setProductId(updatedOrder.getProductId());
        existingOrder.setSize(updatedOrder.getSize());
        existingOrder.setQuantity(updatedOrder.getQuantity());
        existingOrder.setPaymentStatus(updatedOrder.getPaymentStatus());
        existingOrder.setCoupanCode(updatedOrder.getCoupanCode());
        existingOrder.setBuyAtPrice(updatedOrder.getBuyAtPrice());
        existingOrder.setShippingId(updatedOrder.getShippingId());

        return orderItemRepository.save(existingOrder);
    }

    public void deleteOrder(Long orderId) {
        if (!orderItemRepository.existsById(orderId)) {
            throw new RuntimeException("Order not found");
        }
        orderItemRepository.deleteById(orderId);
    }
}
