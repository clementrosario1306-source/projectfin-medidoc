package medidoc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "alerts")
public class Alert {

    @Id
    private String id;

    private String title;

    private String message;

    @Field("alert_type")
    private AlertType alertType;

    private Severity severity = Severity.MEDIUM;

    @Field("created_by")
    private String createdBy;

    @Field("is_active")
    private Boolean isActive = true;

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field("expires_at")
    private LocalDateTime expiresAt;

    public enum AlertType { EPIDEMIC, BLOOD_SHORTAGE, BED_SHORTAGE, GENERAL, EMERGENCY }
    public enum Severity { LOW, MEDIUM, HIGH, CRITICAL }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public AlertType getAlertType() { return alertType; }
    public void setAlertType(AlertType alertType) { this.alertType = alertType; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
