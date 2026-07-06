package medidoc.dto;

import java.time.LocalDate;

public class AppointmentRequest {

    private String patientUniqueId;
    private String doctorId;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String notes;
    private String bookedBy;

    // Getters and Setters
    public String getPatientUniqueId() { return patientUniqueId; }
    public void setPatientUniqueId(String patientUniqueId) { this.patientUniqueId = patientUniqueId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getBookedBy() { return bookedBy; }
    public void setBookedBy(String bookedBy) { this.bookedBy = bookedBy; }
}
