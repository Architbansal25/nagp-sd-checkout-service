package com.nagp.webcart.checkout.Exceptions;

public class DuplicateWishListItemException extends RuntimeException {
    public DuplicateWishListItemException(String message) {
        super(message);
    }
}
