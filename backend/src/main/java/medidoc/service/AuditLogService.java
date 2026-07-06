package medidoc.service;

import medidoc.model.AuditLog;
import medidoc.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    // LOG AN ACTION
    public void logAction(String userId, String role, String action,
            String patientUniqueNumber, String description) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setRole(role);
        log.setAction(action);
        log.setPatientUniqueNumber(patientUniqueNumber);
        log.setDescription(description);
        auditLogRepository.save(log);
    }

    // GET RECENT LOGS
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }

    // GET LOGS FOR A PATIENT
    public List<AuditLog> getPatientLogs(String uniqueId) {
        return auditLogRepository.findByPatientUniqueNumber(uniqueId);
    }

    // GET LOGS FOR A USER
    public List<AuditLog> getUserLogs(String userId) {
        return auditLogRepository.findByUserId(userId);
    }
}
