package com.CardMaster.controller.iam;

import com.CardMaster.dto.iam.ResponseStructure;
import com.CardMaster.model.iam.AuditLog;
import com.CardMaster.model.iam.User;
import com.CardMaster.service.iam.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditLogControllerTest {

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuditLogController auditLogController;

    private AuditLog log;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User testUser = new User();
        testUser.setUserId(100L);
        testUser.setName("Priya");

        log = new AuditLog();
        log.setAuditId(1L);
        log.setAction("LOGIN_SUCCESS");
        log.setTimestamp(LocalDateTime.now());
        log.setUser(testUser); // assign User object, not long
    }


    @Test
    void testGetAllAuditLogs() {
        when(auditLogService.getAllLogs()).thenReturn(List.of(log));

        ResponseEntity<ResponseStructure<List<AuditLog>>> result =
                auditLogController.getAllAuditLogs();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(200, result.getStatusCode().value());

        assertEquals("Audit Logs Retrieved Successfully", result.getBody().getMsg());
        assertEquals(1, result.getBody().getData().size());
        assertEquals("LOGIN_SUCCESS", result.getBody().getData().get(0).getAction());

        verify(auditLogService, times(1)).getAllLogs();
    }
}
