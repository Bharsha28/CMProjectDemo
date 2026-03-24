package com.CardMaster.dto.paa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic response wrapper for all controllers.
 * Keeps responses consistent with a message and data payload.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStructure<T> {

    private String message;
    private T data;
}

