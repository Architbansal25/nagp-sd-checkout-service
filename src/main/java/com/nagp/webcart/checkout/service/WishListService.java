package com.nagp.webcart.checkout.service;

import com.nagp.webcart.checkout.Exceptions.DuplicateWishListItemException;
import com.nagp.webcart.checkout.model.CartItem;
import com.nagp.webcart.checkout.model.WishListItem;
import com.nagp.webcart.checkout.repository.WishListRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class WishListService {

    private final WishListRepository wishListRepository;
    private final RestTemplate restTemplate;

    public WishListService(WishListRepository wishListRepository, RestTemplate restTemplate) {
        this.wishListRepository = wishListRepository;
        this.restTemplate = restTemplate;
    }

    public WishListItem addWishListItem(WishListItem item) {
        boolean exists = wishListRepository.existsByUserNameAndProductId(item.getUserName(), item.getProductId());
        if (exists) {
            throw new DuplicateWishListItemException("Product already exists in wishlist for user: " + item.getUserName());
        }
        return wishListRepository.save(item);

    }

        public List<Map<String, Object>> getWishListByUser(String userName) {
            List<WishListItem> wishListItems = wishListRepository.findByUserName(userName);
            List<Map<String, Object>> enrichedCartItems = new ArrayList<>();

            for (WishListItem item : wishListItems) {
                String productUrl = "http://product-service.default.svc.cluster.local/products/" + item.getProductId();
                Map<String, Object> result = new LinkedHashMap<>();
                try {
                    // Make REST call
                    ResponseEntity<Map> response = restTemplate.getForEntity(productUrl, Map.class);
                    Map<String, Object> productResponse = response.getBody();

                    // Populate required fields
                    result.put("name", productResponse.get("name"));
                    result.put("productId", item.getProductId());
                    result.put("brand", productResponse.get("brand"));
                    result.put("price", productResponse.get("price"));
                    result.put("originalPrice", productResponse.get("originalPrice"));
                    result.put("discount", productResponse.get("discount"));
                    result.put("image", productResponse.get("image"));
                    result.put("wishlishId", item.getWishlistId());
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

    public Optional<WishListItem> getWishListItemById(Long id) {
        return wishListRepository.findById(id);
    }

    public void deleteWishListItem(Long id) {
        if (!wishListRepository.existsById(id)) {
            throw new RuntimeException("Wishlist item not found");
        }
        wishListRepository.deleteById(id);
    }
}
