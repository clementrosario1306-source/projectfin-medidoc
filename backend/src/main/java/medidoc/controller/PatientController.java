package medidoc.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import medidoc.dto.ApiResponse;
import medidoc.dto.PatientFullReport;
import medidoc.dto.PatientRegistrationRequest;
import medidoc.model.Patient;
import medidoc.service.PatientFullReportService;
import medidoc.service.PatientService;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientFullReportService patientFullReportService;

    // POST /api/patients/register
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request) {
        try {
            Patient patient = patientService.registerPatient(request);
            return ResponseEntity.ok(ApiResponse.success(
                "Registration successful! Your Unique ID is: "
                + patient.getUniqueId(), patient));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/patients/{uniqueId}
    @GetMapping("/{uniqueId}")
    public ResponseEntity<ApiResponse> getPatient(
            @PathVariable String uniqueId) {
        Optional<Patient> patient =
            patientService.getPatientByUniqueId(uniqueId);
        if (patient.isPresent()) {
            return ResponseEntity.ok(
                ApiResponse.success("Patient found", patient.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(
                "Patient not found with ID: " + uniqueId));
    }

    // GET /api/patients/{uniqueId}/full-report
    // THE MAIN DOCTOR API - Returns complete 360 degree patient report
    @GetMapping("/{uniqueId}/full-report")
    public ResponseEntity<ApiResponse> getFullReport(
            @PathVariable String uniqueId) {
        try {
            PatientFullReport report =
                patientFullReportService.getFullReport(uniqueId);
            return ResponseEntity.ok(ApiResponse.success(
                "Full patient report fetched successfully", report));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/patients/mobile/{mobile}
    @GetMapping("/mobile/{mobile}")
    public ResponseEntity<ApiResponse> getPatientByMobile(
            @PathVariable String mobile) {
        Optional<Patient> patient =
            patientService.getPatientByMobile(mobile);
        if (patient.isPresent()) {
            return ResponseEntity.ok(
                ApiResponse.success("Patient found", patient.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(
                "No patient found with mobile: " + mobile));
    }
}