package com.CardMaster.controller.iam;

import com.CardMaster.dto.iam.AuditLogResponseDto;
import com.CardMaster.dto.iam.ResponseStructure;
import com.CardMaster.service.iam.AuditLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auditlogs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<ResponseStructure<List<AuditLogResponseDto>>> getRecentAuditLogs() {
        List<AuditLogResponseDto> logs = auditLogService.getRecentLogs().stream()
                .map(log -> AuditLogResponseDto.builder()
                        .auditId(log.getAuditId())
                        .userEmail(log.getUser() != null ? log.getUser().getEmail() : "System")
                        .action(log.getAction())
                        .resource(log.getResource())
                        .timestamp(log.getTimestamp())
                        .metadata(log.getMetadata())
                        .build())
                .collect(Collectors.toList());

        ResponseStructure<List<AuditLogResponseDto>> res = new ResponseStructure<>();
        res.setMsg("Audit Logs Retrieved Successfully");
        res.setData(logs);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
