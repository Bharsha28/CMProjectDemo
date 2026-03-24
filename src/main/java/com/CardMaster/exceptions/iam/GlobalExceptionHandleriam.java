package com.CardMaster.exceptions.iam;

import com.CardMaster.dto.iam.ResponseStructure;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandleriam {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleUserNotFound(UserNotFoundException ex) {
        ResponseStructure<String> res = new ResponseStructure<>();
        res.setMsg("User Not Found");
        res.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ResponseStructure<String>> handleInvalidCredentials(InvalidCredentialsException ex) {
        ResponseStructure<String> res = new ResponseStructure<>();
        res.setMsg("Login Failed");
        res.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    // Handle Access Denied (role mismatch)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseStructure<String>> handleAccessDenied(AccessDeniedException ex) {
        ResponseStructure<String> res = new ResponseStructure<>();
        res.setMsg("Access Denied");
        res.setData("Only admin can access this resource");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }

    //  Handle missing authentication (no token)
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleAuthMissing(AuthenticationCredentialsNotFoundException ex) {
        ResponseStructure<String> res = new ResponseStructure<>();
        res.setMsg("Authentication Required");
        res.setData("Please provide a valid token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseStructure<String>> handleGeneric(Exception ex) {
        ResponseStructure<String> res = new ResponseStructure<>();
        res.setMsg("Internal Server Error");
        res.setData(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
}
