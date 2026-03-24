package com.CardMaster.exceptions.paa;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandlerpaa implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        String path = request.getRequestURI();
        String method = request.getMethod();
        String message = "Access denied";

        // --- Customer endpoints ---
        if (path.startsWith("/customers")) {
            if ("POST".equals(method) || "DELETE".equals(method)) {
                message = "Only Admin can create or delete customers";
            } else if ("PUT".equals(method)) {
                message = "Only Customer or Admin can update customer data";
            } else if ("GET".equals(method)) {
                message = "Only Customer or Admin can view customer data";
            }
        }
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}

