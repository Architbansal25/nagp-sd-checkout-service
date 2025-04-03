package com.nagp.webcart.checkout.controller;

import com.nagp.webcart.checkout.model.CartItem;
import com.nagp.webcart.checkout.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartItem> addCartItem(@RequestBody CartItem cartItem) {
        return ResponseEntity.ok(cartService.addCartItem(cartItem));
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getAllCartItems() {
        return ResponseEntity.ok(cartService.getAllCartItems());
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<List<Map<String, Object>>> getCartItemsByUser(@PathVariable String userName) {
        return ResponseEntity.ok(cartService.getCartItemsByUser(userName));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long id, @RequestBody CartItem cartItem) {
        return ResponseEntity.ok(cartService.updateCartItem(id, cartItem));
    }

    @PutMapping("/update/quantity/{id}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable Long id, @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateCartQuanity(id, quantity));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Long id) {
        cartService.deleteCartItem(id);
        return ResponseEntity.ok("Item deleted successfully");
    }
}

