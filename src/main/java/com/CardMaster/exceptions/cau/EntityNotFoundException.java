package com.CardMaster.exceptions.cau;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entity, Long id) {

        super(entity + " not found with ID: " + id);
    }

}