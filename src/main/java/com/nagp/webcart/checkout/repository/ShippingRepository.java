package com.nagp.webcart.checkout.repository;

import com.nagp.webcart.checkout.model.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {
    List<Shipping> findByUserName(String userName);
}
