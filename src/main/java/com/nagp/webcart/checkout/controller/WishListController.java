package com.nagp.webcart.checkout.controller;

import com.nagp.webcart.checkout.model.WishListItem;
import com.nagp.webcart.checkout.service.WishListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/wishlist")
public class WishListController {

    private final WishListService wishListService;

    public WishListController(WishListService wishListService) {
        this.wishListService = wishListService;
    }

    @PostMapping("/add")
    public ResponseEntity<WishListItem> addItem(@RequestBody WishListItem item) {
        return ResponseEntity.ok(wishListService.addWishListItem(item));
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<List<Map<String, Object>>> getByUser(@PathVariable String userName) {
        return ResponseEntity.ok(wishListService.getWishListByUser(userName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WishListItem> getById(@PathVariable Long id) {
        return wishListService.getWishListItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        wishListService.deleteWishListItem(id);
        return ResponseEntity.ok("Wishlist item deleted successfully");
    }
}
