package medidoc.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import medidoc.model.Appointment;

@Repository
public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    // Get all appointments for a doctor on a specific date
    List<Appointment> findByDoctorIdAndAppointmentDate(
            Long doctorId, LocalDate date);

    // Get all appointments for a patient
    List<Appointment> findByPatientId(Long patientId);

    // Get today's appointments for a doctor ordered by token
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId " +
           "AND a.appointmentDate = :date ORDER BY a.tokenNumber ASC")
    List<Appointment> findTodayQueueByDoctor(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date);

    // Count appointments for a doctor on a date
    // Used for token number generation
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctorId = :doctorId " +
           "AND a.appointmentDate = :date")
    long countByDoctorIdAndDate(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date);

    // Get upcoming appointments for a patient
    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId " +
           "AND a.appointmentDate >= :today ORDER BY a.appointmentDate ASC")
    List<Appointment> findUpcomingByPatient(
            @Param("patientId") Long patientId,
            @Param("today") LocalDate today);
}