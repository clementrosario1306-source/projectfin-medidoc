package medidoc.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "appointments")
public class Appointment {

    @Id
    private String id;

    @Field("patient_id")
    private String patientId;

    @Field("doctor_id")
    private String doctorId;

    @Field("appointment_date")
    private LocalDate appointmentDate;

    @Field("time_slot")
    private String timeSlot;

    @Field("token_number")
    private Integer tokenNumber;

    @Field("queue_position")
    private Integer queuePosition;

    private Status status = Status.BOOKED;

    @Field("booked_by")
    private BookedBy bookedBy = BookedBy.PATIENT;

    private String notes;

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        BOOKED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW
    }

    public enum BookedBy {
        PATIENT, RECEPTIONIST
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate d) { this.appointmentDate = d; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public Integer getTokenNumber() { return tokenNumber; }
    public void setTokenNumber(Integer tokenNumber) { this.tokenNumber = tokenNumber; }

    public Integer getQueuePosition() { return queuePosition; }
    public void setQueuePosition(Integer queuePosition) { this.queuePosition = queuePosition; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public BookedBy getBookedBy() { return bookedBy; }
    public void setBookedBy(BookedBy bookedBy) { this.bookedBy = bookedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}