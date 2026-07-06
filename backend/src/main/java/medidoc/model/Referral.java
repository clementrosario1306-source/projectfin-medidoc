package medidoc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "referrals")
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "referring_doctor_id", nullable = false)
    private Long referringDoctorId;

    @Column(name = "from_hospital")
    private String fromHospital = "MediDoc Hospital";

    @Column(name = "to_hospital", nullable = false)
    private String toHospital;

    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency")
    private Urgency urgency = Urgency.ROUTINE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.PENDING;

    @Column(name = "referred_at")
    private LocalDateTime referredAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    protected void onCreate() {
        referredAt = LocalDateTime.now();
    }

    public enum Urgency { ROUTINE, URGENT, EMERGENCY }
    public enum Status { PENDING, ACCEPTED, COMPLETED, REJECTED }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getReferringDoctorId() { return referringDoctorId; }
    public void setReferringDoctorId(Long referringDoctorId) { this.referringDoctorId = referringDoctorId; }

    public String getFromHospital() { return fromHospital; }
    public void setFromHospital(String fromHospital) { this.fromHospital = fromHospital; }

    public String getToHospital() { return toHospital; }
    public void setToHospital(String toHospital) { this.toHospital = toHospital; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Urgency getUrgency() { return urgency; }
    public void setUrgency(Urgency urgency) { this.urgency = urgency; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getReferredAt() { return referredAt; }
    public void setReferredAt(LocalDateTime referredAt) { this.referredAt = referredAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}