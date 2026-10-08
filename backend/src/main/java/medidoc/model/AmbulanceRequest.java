package medidoc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "ambulance_requests")
public class AmbulanceRequest {

    @Id
    private String id;

    @Field("patient_id")
    private String patientId;

    @Field("ambulance_id")
    private String ambulanceId;

    @Field("requester_name")
    private String requesterName;

    @Field("requester_phone")
    private String requesterPhone;

    @Field("pickup_address")
    private String pickupAddress;

    private String destination;

    private Status status = Status.REQUESTED;

    private Priority priority = Priority.NORMAL;

    @Field("requested_at")
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Field("completed_at")
    private LocalDateTime completedAt;

    public enum Status { REQUESTED, ASSIGNED, EN_ROUTE, COMPLETED, CANCELLED }
    public enum Priority { NORMAL, EMERGENCY }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getAmbulanceId() { return ambulanceId; }
    public void setAmbulanceId(String ambulanceId) { this.ambulanceId = ambulanceId; }

    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }

    public String getRequesterPhone() { return requesterPhone; }
    public void setRequesterPhone(String requesterPhone) { this.requesterPhone = requesterPhone; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
