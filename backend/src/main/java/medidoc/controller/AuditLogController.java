package medidoc.controller;

import medidoc.dto.ApiResponse;
import medidoc.model.AuditLog;
import medidoc.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = "*")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse> getRecentLogs() {
        List<AuditLog> logs = auditLogService.getRecentLogs();
        return ResponseEntity.ok(ApiResponse.success(
            "Recent audit logs: " + logs.size(), logs));
    }

    @GetMapping("/patient/{uniqueId}")
    public ResponseEntity<ApiResponse> getPatientLogs(
            @PathVariable String uniqueId) {
        List<AuditLog> logs = auditLogService.getPatientLogs(uniqueId);
        return ResponseEntity.ok(ApiResponse.success(
            "Logs for patient: " + logs.size(), logs));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserLogs(
            @PathVariable String userId) {
        List<AuditLog> logs = auditLogService.getUserLogs(userId);
        return ResponseEntity.ok(ApiResponse.success(
            "Logs for user: " + logs.size(), logs));
    }
}