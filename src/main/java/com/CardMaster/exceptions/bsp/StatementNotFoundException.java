package com.CardMaster.exceptions.bsp;


public class StatementNotFoundException extends RuntimeException {
    public StatementNotFoundException(Long id) {
        super("Statement not found with id: " + id);
    }
}