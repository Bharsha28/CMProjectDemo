package com.CardMaster.dto.iam;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponseDto {
    private Long auditId;
    private String userEmail;
    private String action;
    private String resource;
    private LocalDateTime timestamp;
    private String metadata;
}
