package medidoc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "referrals")
public class Referral {

    @Id
    private String id;

    @Field("patient_id")
    private String patientId;

    @Field("referring_doctor_id")
    private String referringDoctorId;

    @Field("from_hospital")
    private String fromHospital = "MediDoc Hospital";

    @Field("to_hospital")
    private String toHospital;

    private String reason;

    private Urgency urgency = Urgency.ROUTINE;

    private Status status = Status.PENDING;

    @Field("referred_at")
    private LocalDateTime referredAt = LocalDateTime.now();

    private String notes;

    public enum Urgency { ROUTINE, URGENT, EMERGENCY }
    public enum Status { PENDING, ACCEPTED, COMPLETED, REJECTED }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getReferringDoctorId() { return referringDoctorId; }
    public void setReferringDoctorId(String referringDoctorId) { this.referringDoctorId = referringDoctorId; }

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