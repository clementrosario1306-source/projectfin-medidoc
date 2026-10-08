package medidoc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "blood_requests")
public class BloodRequest {

    @Id
    private String id;

    @Field("patient_id")
    private String patientId;

    @Field("requested_by")
    private String requestedBy;

    @Field("blood_group")
    private BloodInventory.BloodGroup bloodGroup;

    @Field("units_required")
    private Integer unitsRequired;

    @Field("units_issued")
    private Integer unitsIssued = 0;

    private Priority priority = Priority.NORMAL;

    private Status status = Status.PENDING;

    private String purpose;

    @Field("requested_at")
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Field("issued_at")
    private LocalDateTime issuedAt;

    public enum Priority { NORMAL, URGENT, EMERGENCY }
    public enum Status { PENDING, APPROVED, ISSUED, REJECTED, CANCELLED }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public BloodInventory.BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(BloodInventory.BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }

    public Integer getUnitsRequired() { return unitsRequired; }
    public void setUnitsRequired(Integer unitsRequired) { this.unitsRequired = unitsRequired; }

    public Integer getUnitsIssued() { return unitsIssued; }
    public void setUnitsIssued(Integer unitsIssued) { this.unitsIssued = unitsIssued; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
}
