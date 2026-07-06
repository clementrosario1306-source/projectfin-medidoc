package medidoc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import medidoc.dto.ApiResponse;
import medidoc.model.Ambulance;
import medidoc.model.AmbulanceRequest;
import medidoc.service.AmbulanceService;

@RestController
@RequestMapping("/api/ambulances")
@CrossOrigin(origins = "*")
public class AmbulanceController {

    @Autowired
    private AmbulanceService ambulanceService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllAmbulances() {
        List<Ambulance> ambulances = ambulanceService.getAllAmbulances();
        return ResponseEntity.ok(ApiResponse.success(
            "Total ambulances: " + ambulances.size(), ambulances));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse> getAvailable() {
        List<Ambulance> ambulances =
            ambulanceService.getAvailableAmbulances();
        return ResponseEntity.ok(ApiResponse.success(
            "Available ambulances: " + ambulances.size(), ambulances));
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse> requestAmbulance(
            @RequestBody AmbulanceRequest request) {
        AmbulanceRequest saved =
            ambulanceService.requestAmbulance(request);
        return ResponseEntity.ok(ApiResponse.success(
            "Ambulance request submitted successfully", saved));
    }

    @PutMapping("/assign")
    public ResponseEntity<ApiResponse> assignAmbulance(
            @RequestParam Long requestId,
            @RequestParam Long ambulanceId) {
        try {
            AmbulanceRequest request =
                ambulanceService.assignAmbulance(requestId, ambulanceId);
            return ResponseEntity.ok(ApiResponse.success(
                "Ambulance assigned successfully", request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{requestId}/status")
    public ResponseEntity<ApiResponse> updateStatus(
            @PathVariable Long requestId,
            @RequestParam String status) {
        try {
            AmbulanceRequest request =
                ambulanceService.updateRequestStatus(requestId, status);
            return ResponseEntity.ok(ApiResponse.success(
                "Status updated to " + status, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<ApiResponse> getPendingRequests() {
        List<AmbulanceRequest> requests =
            ambulanceService.getPendingRequests();
        return ResponseEntity.ok(ApiResponse.success(
            "Pending requests: " + requests.size(), requests));
    }
}
