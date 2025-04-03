package com.nagp.webcart.checkout.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @NotBlank
    private String userName;

    @NotNull
    private Long productId;

    @NotNull
    private int size;

    @NotBlank
    private String quantity;

    @NotBlank
    private String paymentStatus;
    @NotNull
    private int buyAtPrice;

    private String coupanCode;

    @NotNull
    private Long shippingId;
}
