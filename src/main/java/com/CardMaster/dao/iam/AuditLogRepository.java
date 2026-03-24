package com.CardMaster.dao.iam;
import com.CardMaster.model.iam.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}