package com.nagp.webcart.checkout.service;

import com.nagp.webcart.checkout.model.CartItem;
import com.nagp.webcart.checkout.repository.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final RestTemplate restTemplate;

    public CartService(CartRepository cartRepository, RestTemplate restTemplate) {
        this.cartRepository = cartRepository;
        this.restTemplate = restTemplate;
    }

    public CartItem addCartItem(CartItem cartItem) {
        System.out.println(cartItem);
        return cartRepository.save(cartItem);
    }

    public List<CartItem> getAllCartItems() {
        return cartRepository.findAll();
    }

    public List<Map<String, Object>> getCartItemsByUser(String userName) {
        List<CartItem> cartItems = cartRepository.findByUserName(userName);
        List<Map<String, Object>> enrichedCartItems = new ArrayList<>();

        for (CartItem item : cartItems) {
            String productUrl = "http://product-service:9091/products/" + item.getProductId();
            Map<String, Object> result = new LinkedHashMap<>();
            try {
                // Make REST call
                ResponseEntity<Map> response = restTemplate.getForEntity(productUrl, Map.class);
                Map<String, Object> productResponse = response.getBody();

                // Populate required fields
                result.put("name", productResponse.get("name"));
                result.put("brand", productResponse.get("brand"));
                result.put("price", productResponse.get("price"));
                result.put("originalPrice", productResponse.get("originalPrice"));
                result.put("discount", productResponse.get("discount"));
                result.put("size", item.getSize());
                result.put("quantity", item.getQuantity());
                result.put("image", productResponse.get("image"));
                result.put("cartId", item.getId());
                result.put("productId", item.getProductId());
                result.put("specification", productResponse.get("specification"));

                // Validate available sizes
                Integer availableStock = (Integer)productResponse.get("stock");
                boolean available = availableStock != null && availableStock>0;
                result.put("available", available);

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

    public CartItem updateCartItem(Long id, CartItem updatedItem) {
        CartItem existingItem = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        existingItem.setProductId(updatedItem.getProductId());
        existingItem.setSize(updatedItem.getSize());
        existingItem.setQuantity(updatedItem.getQuantity());
        existingItem.setUserName(updatedItem.getUserName());
        return cartRepository.save(existingItem);
    }
    public CartItem updateCartQuanity(Long id, int Quantity) {
        CartItem existingItem = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        existingItem.setQuantity(Quantity);
        return cartRepository.save(existingItem);
    }

    public void deleteCartItem(Long id) {
        if (!cartRepository.existsById(id)) {
            throw new RuntimeException("Cart item not found");
        }
        cartRepository.deleteById(id);
    }
}

