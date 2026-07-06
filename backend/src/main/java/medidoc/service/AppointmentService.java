package medidoc.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medidoc.dto.AppointmentRequest;
import medidoc.model.Appointment;
import medidoc.model.Doctor;
import medidoc.model.Patient;
import medidoc.repository.AppointmentRepository;
import medidoc.repository.DoctorRepository;
import medidoc.repository.PatientRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // -----------------------------------------------
    // BOOK APPOINTMENT + GENERATE TOKEN NUMBER
    // -----------------------------------------------
    public Appointment bookAppointment(AppointmentRequest request) {

        // Step 1: Validate patient exists
        Optional<Patient> patientOpt = patientRepository
                .findByUniqueId(request.getPatientUniqueId());
        if (patientOpt.isEmpty()) {
            throw new RuntimeException(
                "Patient not found: " + request.getPatientUniqueId());
        }

        // Step 2: Validate doctor exists
        Optional<Doctor> doctorOpt = doctorRepository
                .findByDoctorId(request.getDoctorId());
        if (doctorOpt.isEmpty()) {
            throw new RuntimeException(
                "Doctor not found: " + request.getDoctorId());
        }

        Patient patient = patientOpt.get();
        Doctor doctor = doctorOpt.get();

        // Step 3: Generate token number
        // Count appointments for this doctor on this date
        long existingCount = appointmentRepository
                .countByDoctorIdAndDate(
                    doctor.getId(),
                    request.getAppointmentDate());

        int tokenNumber = (int) existingCount + 1;

        // Step 4: Create appointment
        Appointment appointment = new Appointment();
        appointment.setPatientId(patient.getId());
        appointment.setDoctorId(doctor.getId());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setTimeSlot(request.getTimeSlot());
        appointment.setTokenNumber(tokenNumber);
        appointment.setQueuePosition(tokenNumber);
        appointment.setNotes(request.getNotes());
        appointment.setStatus(Appointment.Status.BOOKED);

        if (request.getBookedBy() != null &&
                request.getBookedBy().equals("RECEPTIONIST")) {
            appointment.setBookedBy(Appointment.BookedBy.RECEPTIONIST);
        } else {
            appointment.setBookedBy(Appointment.BookedBy.PATIENT);
        }

        Appointment saved = appointmentRepository.save(appointment);

        System.out.println("Appointment booked! Token #"
            + tokenNumber + " for " + patient.getFullName()
            + " with " + doctor.getName());

        return saved;
    }

    // -----------------------------------------------
    // GET TODAY'S QUEUE FOR A DOCTOR
    // -----------------------------------------------
    public List<Appointment> getTodayQueue(String doctorId) {

        Optional<Doctor> doctorOpt =
            doctorRepository.findByDoctorId(doctorId);

        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found: " + doctorId);
        }

        return appointmentRepository.findTodayQueueByDoctor(
            doctorOpt.get().getId(), LocalDate.now());
    }

    // -----------------------------------------------
    // GET ALL APPOINTMENTS FOR A PATIENT
    // -----------------------------------------------
    public List<Appointment> getPatientAppointments(String uniqueId) {

        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(uniqueId);

        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found: " + uniqueId);
        }

        return appointmentRepository
            .findByPatientId(patientOpt.get().getId());
    }

    // -----------------------------------------------
    // GET UPCOMING APPOINTMENTS FOR A PATIENT
    // -----------------------------------------------
    public List<Appointment> getUpcomingAppointments(String uniqueId) {

        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(uniqueId);

        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found: " + uniqueId);
        }

        return appointmentRepository.findUpcomingByPatient(
            patientOpt.get().getId(), LocalDate.now());
    }

    // -----------------------------------------------
    // UPDATE APPOINTMENT STATUS
    // -----------------------------------------------
    public Appointment updateStatus(Long id, String status) {

        Optional<Appointment> apptOpt =
            appointmentRepository.findById(id);

        if (apptOpt.isEmpty()) {
            throw new RuntimeException("Appointment not found: " + id);
        }

        Appointment appt = apptOpt.get();
        appt.setStatus(Appointment.Status.valueOf(status.toUpperCase()));
        return appointmentRepository.save(appt);
    }
}
