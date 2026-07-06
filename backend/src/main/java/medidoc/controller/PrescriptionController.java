package medidoc.controller;

import medidoc.dto.ApiResponse;
import medidoc.dto.PrescriptionRequest;
import medidoc.model.Prescription;
import medidoc.model.PrescriptionMedicine;
import medidoc.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    // POST /api/prescriptions/add
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addPrescription(
            @RequestBody PrescriptionRequest request) {
        try {
            Prescription prescription =
                prescriptionService.addPrescription(request);
            return ResponseEntity.ok(ApiResponse.success(
                "Prescription added successfully", prescription));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/prescriptions/patient/{uniqueId}
    @GetMapping("/patient/{uniqueId}")
    public ResponseEntity<ApiResponse> getPatientPrescriptions(
            @PathVariable String uniqueId) {
        try {
            List<Prescription> prescriptions =
                prescriptionService.getPatientPrescriptions(uniqueId);
            return ResponseEntity.ok(ApiResponse.success(
                "Prescriptions found: " + prescriptions.size(),
                prescriptions));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/prescriptions/patient/{uniqueId}/active
    @GetMapping("/patient/{uniqueId}/active")
    public ResponseEntity<ApiResponse> getActivePrescriptions(
            @PathVariable String uniqueId) {
        try {
            List<Prescription> prescriptions =
                prescriptionService.getActivePrescriptions(uniqueId);
            return ResponseEntity.ok(ApiResponse.success(
                "Active prescriptions: " + prescriptions.size(),
                prescriptions));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/prescriptions/{id}/medicines
    @GetMapping("/{id}/medicines")
    public ResponseEntity<ApiResponse> getPrescriptionMedicines(
            @PathVariable Long id) {
        List<PrescriptionMedicine> medicines =
            prescriptionService.getPrescriptionMedicines(id);
        return ResponseEntity.ok(ApiResponse.success(
            "Medicines in prescription", medicines));
    }
}
