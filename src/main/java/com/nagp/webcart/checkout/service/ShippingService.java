package com.nagp.webcart.checkout.service;

import com.nagp.webcart.checkout.model.Shipping;
import com.nagp.webcart.checkout.repository.ShippingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShippingService {

    private final ShippingRepository shippingRepository;

    public ShippingService(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }

    public Shipping createShipping(Shipping shipping) {
        return shippingRepository.save(shipping);
    }

    public List<Shipping> getAllShipping() {
        return shippingRepository.findAll();
    }

    public List<Shipping> getShippingByFirstName(String userName) {
        return shippingRepository.findByUserName(userName);
    }

    public Optional<Shipping> getShippingById(Long id) {
        return shippingRepository.findById(id);
    }

    public Shipping updateShipping(Long id, Shipping shipping) {
        Shipping existingShipping = shippingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shipping record not found"));

        existingShipping.setFirstName(shipping.getFirstName());
        existingShipping.setLastName(shipping.getLastName());
        existingShipping.setCountry(shipping.getCountry());
        existingShipping.setState(shipping.getState());
        existingShipping.setCity(shipping.getCity());
        existingShipping.setPincode(shipping.getPincode());
        existingShipping.setContactNo(shipping.getContactNo());
        existingShipping.setFullAddress(shipping.getFullAddress());

        return shippingRepository.save(existingShipping);
    }

    public void deleteShipping(Long id) {
        if (!shippingRepository.existsById(id)) {
            throw new RuntimeException("Shipping record not found");
        }
        shippingRepository.deleteById(id);
    }
}
