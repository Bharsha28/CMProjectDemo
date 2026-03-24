package com.CardMaster.exceptions.paa;

/**
 * Collection of custom exceptions for Card Application and Document modules.
 * Each exception extends RuntimeException so they can be thrown from service layer
 * and handled globally in GlobalExceptionHandler.
 */
public class CustomExceptions {

    // --- Resource Not Found ---
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    // --- Invalid Status ---
    public static class InvalidStatusException extends RuntimeException {
        public InvalidStatusException(String message) {
            super(message);
        }
    }

    // --- Invalid Document Type ---
    public static class InvalidDocumentTypeException extends RuntimeException {
        public InvalidDocumentTypeException(String message) {
            super(message);
        }
    }


    // --- Duplicate Document ---
    public static class DuplicateDocumentException extends RuntimeException {
        public DuplicateDocumentException(String message) {
            super(message);
        }
    }

    // --- Limit Exceeded ---
    public static class LimitExceededException extends RuntimeException {
        public LimitExceededException(String message) {
            super(message);
        }
    }

    // --- File Storage Error ---
    public static class FileStorageException extends RuntimeException {
        public FileStorageException(String message) {
            super(message);
        }
    }
}
