package com.CardMaster.exceptions.cias;

public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String message) {

        super(message);
    }
}
