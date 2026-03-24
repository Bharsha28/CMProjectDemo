package com.CardMaster.exceptions.cias;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {

        super("Customer not found with ID: " + id);
    }
}
