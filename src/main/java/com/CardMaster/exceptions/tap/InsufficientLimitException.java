package com.CardMaster.exceptions.tap;
public class InsufficientLimitException extends RuntimeException {
    public InsufficientLimitException(Double amount) {
        super("Insufficient available limit for transaction amount: " + amount);
    }

}
