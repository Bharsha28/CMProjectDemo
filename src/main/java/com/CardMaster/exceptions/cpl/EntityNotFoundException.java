package com.CardMaster.exceptions.cpl;

public class EntityNotFoundException extends RuntimeException {
    private final String entityName;
    private final Object entityId;

    public EntityNotFoundException(String entityName, Object entityId) {
        super(entityName + (entityId != null ? " not found with id=" + entityId : " not found"));
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public EntityNotFoundException(String message) {
        super(message);
        this.entityName = null;
        this.entityId = null;
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.entityName = null;
        this.entityId = null;
    }

    public String getEntityName() { return entityName; }
    public Object getEntityId() { return entityId; }
}