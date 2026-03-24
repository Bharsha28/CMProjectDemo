package com.CardMaster.exceptions.cpl;

import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;

public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static ApiError of(HttpStatus status, String message, String path) {
        return new ApiError(OffsetDateTime.now(), status.value(), status.getReasonPhrase(), message, path);
    }
}