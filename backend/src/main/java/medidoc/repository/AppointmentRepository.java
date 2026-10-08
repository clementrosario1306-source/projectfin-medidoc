package medidoc.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.Appointment;

@Repository
public interface AppointmentRepository
        extends MongoRepository<Appointment, String> {

    // Get all appointments for a doctor on a specific date
    List<Appointment> findByDoctorIdAndAppointmentDate(
            String doctorId, LocalDate date);

    // Get all appointments for a patient
    List<Appointment> findByPatientId(String patientId);

    // Get today's appointments for a doctor ordered by token
    List<Appointment> findByDoctorIdAndAppointmentDateOrderByTokenNumberAsc(
            String doctorId, LocalDate appointmentDate);

    default List<Appointment> findTodayQueueByDoctor(String doctorId, LocalDate date) {
        return findByDoctorIdAndAppointmentDateOrderByTokenNumberAsc(doctorId, date);
    }

    // Count appointments for a doctor on a date
    // Used for token number generation
    long countByDoctorIdAndAppointmentDate(String doctorId, LocalDate appointmentDate);

    default long countByDoctorIdAndDate(String doctorId, LocalDate date) {
        return countByDoctorIdAndAppointmentDate(doctorId, date);
    }

    // Get upcoming appointments for a patient
    List<Appointment> findByPatientIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAsc(
            String patientId, LocalDate today);

    default List<Appointment> findUpcomingByPatient(String patientId, LocalDate today) {
        return findByPatientIdAndAppointmentDateGreaterThanEqualOrderByAppointmentDateAsc(patientId, today);
    }
}