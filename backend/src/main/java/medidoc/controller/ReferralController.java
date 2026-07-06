package medidoc.controller;

import medidoc.dto.ApiResponse;
import medidoc.model.Referral;
import medidoc.service.ReferralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/referrals")
@CrossOrigin(origins = "*")
public class ReferralController {

    @Autowired
    private ReferralService referralService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createReferral(
            @RequestParam String patientUniqueId,
            @RequestParam String doctorId,
            @RequestParam String toHospital,
            @RequestParam String reason,
            @RequestParam(required = false, defaultValue = "ROUTINE") String urgency) {
        try {
            Referral referral = referralService.createReferral(
                patientUniqueId, doctorId, toHospital, reason, urgency);
            return ResponseEntity.ok(ApiResponse.success(
                "Referral created successfully", referral));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/patient/{uniqueId}")
    public ResponseEntity<ApiResponse> getPatientReferrals(
            @PathVariable String uniqueId) {
        try {
            List<Referral> referrals =
                referralService.getPatientReferrals(uniqueId);
            return ResponseEntity.ok(ApiResponse.success(
                "Referrals found: " + referrals.size(), referrals));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getPendingReferrals() {
        List<Referral> referrals = referralService.getPendingReferrals();
        return ResponseEntity.ok(ApiResponse.success(
            "Pending referrals: " + referrals.size(), referrals));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            Referral referral = referralService.updateStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success(
                "Status updated to " + status, referral));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}