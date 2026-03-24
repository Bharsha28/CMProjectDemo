package com.CardMaster.exceptions.cias;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {

        super("Product not found with ID: " + id);
    }
}
