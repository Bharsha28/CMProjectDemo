package com.CardMaster.exceptions.cau;

import com.CardMaster.dto.cau.ResponseStructure;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.CardMaster.controller.cau")
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleNotFound(EntityNotFoundException ex) {
        ResponseStructure<String> body = new ResponseStructure<>();
        body.setStatus("error");
        body.setMessage(ex.getMessage());
        body.setData(null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseStructure<String>> handleValidation(ValidationException ex) {
        ResponseStructure<String> body = new ResponseStructure<>();
        body.setStatus("error");
        body.setMessage(ex.getMessage());
        body.setData(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ResponseStructure<String>> handleUnauthorized(UnauthorizedActionException ex) {
        ResponseStructure<String> body = new ResponseStructure<>();
        body.setStatus("error");
        body.setMessage(ex.getMessage());
        body.setData(null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }



}