package medidoc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "beds")
public class Bed {

    @Id
    private String id;

    @Field("bed_number")
    private String bedNumber;

    private String ward;

    @Field("room_number")
    private String roomNumber;

    private String floor;

    @Field("bed_type")
    private BedType bedType = BedType.GENERAL;

    private Status status = Status.AVAILABLE;

    @Field("patient_id")
    private String patientId;

    @Field("admitted_at")
    private LocalDateTime admittedAt;

    public enum BedType {
        GENERAL, SEMI_PRIVATE, PRIVATE, ICU, HDU
    }

    public enum Status {
        AVAILABLE, OCCUPIED, MAINTENANCE, CLEANING
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public BedType getBedType() { return bedType; }
    public void setBedType(BedType bedType) { this.bedType = bedType; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public LocalDateTime getAdmittedAt() { return admittedAt; }
    public void setAdmittedAt(LocalDateTime admittedAt) { this.admittedAt = admittedAt; }
}