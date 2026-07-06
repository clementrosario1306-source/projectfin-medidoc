package medidoc.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medidoc.dto.PatientRegistrationRequest;
import medidoc.model.Patient;
import medidoc.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    // -----------------------------------------------
    // UNIQUE ID GENERATION
    // Format: MDID + YYYYMMDD + 6 digit sequence
    // Example: MDID20260604000001
    // -----------------------------------------------
    public String generateUniqueId() {
        // Get today's date in YYYYMMDD format
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Count how many patients registered today
        long todayCount = patientRepository.countPatientsRegisteredToday();

        // Next sequence number
        long nextSeq = todayCount + 1;

        // Format sequence as 6 digits (000001, 000002 etc.)
        String seqStr = String.format("%06d", nextSeq);

        // Final unique ID
        return "MDID" + dateStr + seqStr;
    }

    // -----------------------------------------------
    // REGISTER NEW PATIENT
    // -----------------------------------------------
    public Patient registerPatient(PatientRegistrationRequest request) {

        // Check if mobile already registered
        if (patientRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new RuntimeException("Mobile number already registered. Please login.");
        }

        // Generate unique ID
        String uniqueId = generateUniqueId();

        // Create patient object
        Patient patient = new Patient();
        patient.setUniqueId(uniqueId);
        patient.setFullName(request.getFullName());
        patient.setAge(request.getAge());
        patient.setGender(request.getGender());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setMobileNumber(request.getMobileNumber());
        patient.setAddress(request.getAddress());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        patient.setAbhaNumber(request.getAbhaNumber());
        patient.setPmjayId(request.getPmjayId());
        patient.setIsPmjayEligible(request.getIsPmjayEligible());

        // Save to database
        Patient savedPatient = patientRepository.save(patient);

        System.out.println("New patient registered: " + uniqueId + " - " + request.getFullName());

        return savedPatient;
    }

    // -----------------------------------------------
    // GET PATIENT BY UNIQUE ID
    // -----------------------------------------------
    public Optional<Patient> getPatientByUniqueId(String uniqueId) {
        return patientRepository.findByUniqueId(uniqueId);
    }

    // -----------------------------------------------
    // GET PATIENT BY MOBILE
    // -----------------------------------------------
    public Optional<Patient> getPatientByMobile(String mobile) {
        return patientRepository.findByMobileNumber(mobile);
    }
}