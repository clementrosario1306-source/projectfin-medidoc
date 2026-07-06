package medidoc.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import medidoc.dto.ApiResponse;
import medidoc.model.Bed;
import medidoc.service.BedService;

@RestController
@RequestMapping("/api/beds")
@CrossOrigin(origins = "*")
public class BedController {

    @Autowired
    private BedService bedService;

    // GET /api/beds
    @GetMapping
    public ResponseEntity<ApiResponse> getAllBeds() {
        List<Bed> beds = bedService.getAllBeds();
        return ResponseEntity.ok(
            ApiResponse.success("All beds fetched", beds));
    }

    // GET /api/beds/available
    @GetMapping("/available")
    public ResponseEntity<ApiResponse> getAvailableBeds() {
        List<Bed> beds = bedService.getAvailableBeds();
        return ResponseEntity.ok(ApiResponse.success(
            "Available beds: " + beds.size(), beds));
    }

    // GET /api/beds/summary
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getBedSummary() {
        Map<String, Long> summary = bedService.getBedSummary();
        return ResponseEntity.ok(
            ApiResponse.success("Bed summary", summary));
    }

    // GET /api/beds/ward/{ward}
    @GetMapping("/ward/{ward}")
    public ResponseEntity<ApiResponse> getBedsByWard(
            @PathVariable String ward) {
        List<Bed> beds = bedService.getBedsByWard(ward);
        return ResponseEntity.ok(ApiResponse.success(
            "Beds in " + ward + ": " + beds.size(), beds));
    }

    // POST /api/beds/admit
    @PostMapping("/admit")
    public ResponseEntity<ApiResponse> admitPatient(
            @RequestParam String bedNumber,
            @RequestParam String patientUniqueId) {
        try {
            Bed bed = bedService.admitPatient(bedNumber, patientUniqueId);
            return ResponseEntity.ok(ApiResponse.success(
                "Patient admitted to bed " + bedNumber, bed));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // PUT /api/beds/discharge/{bedNumber}
    @PutMapping("/discharge/{bedNumber}")
    public ResponseEntity<ApiResponse> dischargePatient(
            @PathVariable String bedNumber) {
        try {
            Bed bed = bedService.dischargePatient(bedNumber);
            return ResponseEntity.ok(ApiResponse.success(
                "Patient discharged from bed " + bedNumber, bed));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // PUT /api/beds/status/{bedNumber}
    @PutMapping("/status/{bedNumber}")
    public ResponseEntity<ApiResponse> updateBedStatus(
            @PathVariable String bedNumber,
            @RequestParam String status) {
        try {
            Bed bed = bedService.updateBedStatus(bedNumber, status);
            return ResponseEntity.ok(ApiResponse.success(
                "Bed " + bedNumber + " status updated to " + status, bed));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
