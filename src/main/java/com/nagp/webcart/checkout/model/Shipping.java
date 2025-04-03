package com.nagp.webcart.checkout.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "shipping_details")
@Data
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String userName;
    @NotBlank
    private String firstName;

    private String lastName;

    @NotBlank
    private String country;

    @NotBlank
    private String state;

    @NotBlank
    private String city;

    @NotBlank
    private String pincode;

    @NotBlank
    private String contactNo;

    @NotBlank
    private String fullAddress;
}
