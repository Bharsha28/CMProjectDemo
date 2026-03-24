package com.CardMaster.dto.iam;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {
    private Long id;              // Primary key of the audit log
    private String username;      // Who performed the action
    private String action;        // e.g., LOGIN, LOGOUT, REGISTER
    private String description;   // Details about the action
    private LocalDateTime timestamp; // When the action occurred
}
