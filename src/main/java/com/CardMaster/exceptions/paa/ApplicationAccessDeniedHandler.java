package com.CardMaster.exceptions.paa;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("applicationAccessDeniedHandler")
public class ApplicationAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        String method = request.getMethod();
        String message = "Access denied";

        if ("POST".equals(method)) {
            message = "Only Customer or Admin can create applications";
        } else if ("PUT".equals(method)) {
            message = "Only Underwriter or Admin can update application status";
        } else if ("DELETE".equals(method)) {
            message = "Only Admin can delete applications";
        } else if ("GET".equals(method)) {
            message = "Only Underwriter, Admin, or Customer can view applications";
        }

        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
