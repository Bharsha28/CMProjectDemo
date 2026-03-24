package com.CardMaster.dto.cau;

import lombok.Data;

@Data
public class ResponseStructure<T> {
    private String status;   // success / error
    private String message;  // info message
    private T data;          // actual data
}
