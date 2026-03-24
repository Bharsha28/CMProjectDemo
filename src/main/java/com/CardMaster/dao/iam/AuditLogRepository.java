package com.CardMaster.dao.iam;
import com.CardMaster.model.iam.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findTop5ByOrderByTimestampDesc();
}