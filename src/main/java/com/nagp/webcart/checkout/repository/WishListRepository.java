package com.nagp.webcart.checkout.repository;

import com.nagp.webcart.checkout.model.WishListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishListItem, Long> {
    List<WishListItem> findByUserName(String userName);
    boolean existsByUserNameAndProductId(String userName, Long productId);
}
