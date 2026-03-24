package com.CardMaster.controller.iam;

import com.CardMaster.dto.iam.ResponseStructure;
import com.CardMaster.model.iam.AuditLog;
import com.CardMaster.service.iam.AuditLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auditlogs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<ResponseStructure<List<AuditLog>>> getAllAuditLogs() {
        List<AuditLog> logs = auditLogService.getAllLogs();

        ResponseStructure<List<AuditLog>> res = new ResponseStructure<>();
        res.setMsg("Audit Logs Retrieved Successfully");
        res.setData(logs);

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
