package com.CardMaster.service.iam;

import com.CardMaster.dao.iam.AuditLogRepository;
import com.CardMaster.dao.iam.UserRepository;
import com.CardMaster.model.iam.AuditLog;
import com.CardMaster.model.iam.User;
import com.CardMaster.exceptions.iam.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
    /**
     * Log an action by resolving the User entity from either userId or email.
     */
    public void log(String identifier, String action, String resource) {
        AuditLog log = new AuditLog();

        User user = resolveUser(identifier);
        if (user != null) {
            log.setUser(user);
            log.setMetadata("Performed by " + user.getName());
        } else {
            log.setUser(null);
            log.setMetadata("Performed by " + identifier);
        }

        log.setAction(action);
        log.setResource(resource);

        auditLogRepository.save(log);
    }

    private User resolveUser(String identifier) {
        try {
            // If identifier is numeric, treat it as userId
            Long userId = Long.parseLong(identifier);
            return userRepository.findById(userId).orElse(null);
        } catch (NumberFormatException e) {
            // Otherwise, treat identifier as email
            return userRepository.findByEmail(identifier).orElse(null);
        }
    }

}
