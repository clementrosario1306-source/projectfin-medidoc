package medidoc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medidoc.dto.ApiResponse;
import medidoc.model.MedicalHistory;
import medidoc.service.MedicalHistoryService;

@RestController
@RequestMapping("/api/medical-history")
@CrossOrigin(origins = "*")
public class MedicalHistoryController {

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    // POST /api/medical-history/add
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addHistory(
            @RequestBody MedicalHistory history) {
        try {
            MedicalHistory saved =
                medicalHistoryService.addHistory(history);
            return ResponseEntity.ok(ApiResponse.success(
                "Medical history added successfully", saved));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/medical-history/patient/{uniqueId}
    @GetMapping("/patient/{uniqueId}")
    public ResponseEntity<ApiResponse> getPatientHistory(
            @PathVariable String uniqueId) {
        try {
            List<MedicalHistory> history =
                medicalHistoryService.getPatientHistory(uniqueId);
            return ResponseEntity.ok(ApiResponse.success(
                "Medical history found: " + history.size() + " records",
                history));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}