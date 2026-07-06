package medidoc.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medidoc.dto.AuthResponse;
import medidoc.dto.OtpRequest;
import medidoc.dto.PatientLoginRequest;
import medidoc.dto.StaffLoginRequest;
import medidoc.model.Doctor;
import medidoc.model.Patient;
import medidoc.model.PatientSession;
import medidoc.model.Staff;
import medidoc.repository.DoctorRepository;
import medidoc.repository.PatientRepository;
import medidoc.repository.PatientSessionRepository;
import medidoc.repository.StaffRepository;
import medidoc.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientSessionRepository patientSessionRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // -----------------------------------------------
    // SEND OTP TO PATIENT
    // -----------------------------------------------
    public AuthResponse sendOtp(OtpRequest request) {

        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(request.getUniqueId());

        if (patientOpt.isEmpty()) {
            return AuthResponse.error(
                "Patient not found with ID: " + request.getUniqueId());
        }

        Patient patient = patientOpt.get();

        if (!patient.getMobileNumber().equals(request.getMobileNumber())) {
            return AuthResponse.error(
                "Mobile number does not match our records.");
        }

        String otp = "123456";

        PatientSession session;
        Optional<PatientSession> existingSession =
            patientSessionRepository.findByPatientUniqueNumber(
                request.getUniqueId());

        if (existingSession.isPresent()) {
            session = existingSession.get();
        } else {
            session = new PatientSession();
            session.setPatientUniqueNumber(request.getUniqueId());
        }

        session.setOtp(otp);
        session.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        session.setIsLoggedIn(false);
        patientSessionRepository.save(session);

        System.out.println("======================================");
        System.out.println("SMS SENT TO: " + request.getMobileNumber());
        System.out.println("OTP: " + otp);
        System.out.println("(Simulated - use 123456 to login)");
        System.out.println("======================================");

        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setMessage("OTP sent to " + request.getMobileNumber()
            + " (Simulated OTP: 123456)");
        return response;
    }

    // -----------------------------------------------
    // PATIENT LOGIN WITH OTP
    // -----------------------------------------------
    public AuthResponse patientLogin(PatientLoginRequest request) {

        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(request.getUniqueId());

        if (patientOpt.isEmpty()) {
            return AuthResponse.error("Patient not found.");
        }

        Patient patient = patientOpt.get();

        if (!patient.getMobileNumber().equals(request.getMobileNumber())) {
            return AuthResponse.error("Mobile number does not match.");
        }

        Optional<PatientSession> sessionOpt =
            patientSessionRepository.findByPatientUniqueNumber(
                request.getUniqueId());

        if (sessionOpt.isEmpty()) {
            return AuthResponse.error("Please request OTP first.");
        }

        PatientSession session = sessionOpt.get();

        if (!session.getOtp().equals(request.getOtp())) {
            return AuthResponse.error("Invalid OTP. Please try again.");
        }

        if (session.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return AuthResponse.error(
                "OTP expired. Please request a new OTP.");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(
            patient.getUniqueId(), "PATIENT", patient.getFullName());

        session.setIsLoggedIn(true);
        session.setLastLogin(LocalDateTime.now());
        session.setJwtToken(token);
        patientSessionRepository.save(session);

        System.out.println("Patient logged in: " + patient.getFullName());

        return AuthResponse.success(
            "Login successful! Welcome " + patient.getFullName(),
            token, "PATIENT",
            patient.getUniqueId(),
            patient.getFullName(),
            patient
        );
    }

    // -----------------------------------------------
    // STAFF LOGIN (Doctor, Receptionist, Admin, Blood Bank)
    // -----------------------------------------------
    public AuthResponse staffLogin(StaffLoginRequest request) {

        String role = request.getRole().toUpperCase();

        // Doctor login
        if (role.equals("DOCTOR")) {
            Optional<Doctor> doctorOpt =
                doctorRepository.findByDoctorId(request.getEmployeeId());

            if (doctorOpt.isEmpty()) {
                return AuthResponse.error("Doctor not found with ID: "
                    + request.getEmployeeId());
            }

            Doctor doctor = doctorOpt.get();

            if (!doctor.getEmployeePassword().equals(request.getPassword())) {
                return AuthResponse.error("Invalid password.");
            }

            if (doctor.getStatus() == Doctor.Status.INACTIVE) {
                return AuthResponse.error("Your account is inactive.");
            }

            String token = jwtUtil.generateToken(
                doctor.getDoctorId(), "DOCTOR", doctor.getName());

            System.out.println("Doctor logged in: " + doctor.getName());

            return AuthResponse.success(
                "Login successful! Welcome " + doctor.getName(),
                token, "DOCTOR",
                doctor.getDoctorId(),
                doctor.getName(),
                doctor
            );
        }

        // Staff login (Receptionist, Admin, Blood Bank Officer)
        Optional<Staff> staffOpt =
            staffRepository.findByEmployeeId(request.getEmployeeId());

        if (staffOpt.isEmpty()) {
            return AuthResponse.error("Staff not found with ID: "
                + request.getEmployeeId());
        }

        Staff staff = staffOpt.get();

        if (!staff.getPassword().equals(request.getPassword())) {
            return AuthResponse.error("Invalid password.");
        }

        if (staff.getStatus() == Staff.Status.INACTIVE) {
            return AuthResponse.error("Your account is inactive.");
        }

        String token = jwtUtil.generateToken(
            staff.getEmployeeId(),
            staff.getRole().toString(),
            staff.getFullName());

        System.out.println("Staff logged in: " + staff.getFullName()
            + " Role: " + staff.getRole());

        return AuthResponse.success(
            "Login successful! Welcome " + staff.getFullName(),
            token,
            staff.getRole().toString(),
            staff.getEmployeeId(),
            staff.getFullName(),
            staff
        );
    }
}