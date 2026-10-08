package medidoc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    private String role;

    private String action;

    @Field("patient_unique_number")
    private String patientUniqueNumber;

    private String description;

    @Field("ip_address")
    private String ipAddress;

    private LocalDateTime timestamp = LocalDateTime.now();

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getPatientUniqueNumber() { return patientUniqueNumber; }
    public void setPatientUniqueNumber(String p) { this.patientUniqueNumber = p; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
