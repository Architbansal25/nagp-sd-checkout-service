package com.nagp.webcart.checkout.controller;

import com.nagp.webcart.checkout.model.Shipping;
import com.nagp.webcart.checkout.service.ShippingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/shipping")
public class ShippingController {

    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping("/add")
    public ResponseEntity<Shipping> createShipping(@RequestBody Shipping shipping) {
        return ResponseEntity.ok(shippingService.createShipping(shipping));
    }

    @GetMapping
    public ResponseEntity<List<Shipping>> getAllShipping() {
        return ResponseEntity.ok(shippingService.getAllShipping());
    }

    @GetMapping("user/{username}")
    public ResponseEntity<List<Shipping>> getByFirstName(@PathVariable String username) {
        return ResponseEntity.ok(shippingService.getShippingByFirstName(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipping> getById(@PathVariable Long id) {
        return shippingService.getShippingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Shipping> updateShipping(@PathVariable Long id, @RequestBody Shipping shipping) {
        return ResponseEntity.ok(shippingService.updateShipping(id, shipping));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteShipping(@PathVariable Long id) {
        shippingService.deleteShipping(id);
        return ResponseEntity.ok("Shipping record deleted successfully");
    }
}
