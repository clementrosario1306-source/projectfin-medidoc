package medidoc.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import medidoc.dto.ApiResponse;
import medidoc.model.BloodDonor;
import medidoc.model.BloodInventory;
import medidoc.model.BloodRequest;
import medidoc.service.BloodBankService;

@RestController
@CrossOrigin(origins = "*")
public class BloodBankController {

    @Autowired
    private BloodBankService bloodBankService;

    // GET /api/blood/inventory
    @GetMapping("/api/blood/inventory")
    public ResponseEntity<ApiResponse> getAllInventory() {
        List<BloodInventory> inventory =
            bloodBankService.getAllInventory();
        return ResponseEntity.ok(ApiResponse.success(
            "Blood inventory fetched", inventory));
    }

    // GET /api/blood/low-stock
    @GetMapping("/api/blood/low-stock")
    public ResponseEntity<ApiResponse> getLowStock() {
        List<BloodInventory> lowStock =
            bloodBankService.getLowStockBlood();
        return ResponseEntity.ok(ApiResponse.success(
            "Low stock blood groups: " + lowStock.size(), lowStock));
    }

    // POST /api/blood/add-stock
    @PostMapping("/api/blood/add-stock")
    public ResponseEntity<ApiResponse> addStock(
            @RequestParam String bloodGroup,
            @RequestParam Integer units) {
        try {
            BloodInventory inventory =
                bloodBankService.addStock(bloodGroup, units);
            return ResponseEntity.ok(ApiResponse.success(
                units + " units of " + bloodGroup + " added successfully",
                inventory));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // POST /api/blood/request
    @PostMapping("/api/blood/request")
    public ResponseEntity<ApiResponse> requestBlood(
            @RequestParam String bloodGroup,
            @RequestParam Integer units,
            @RequestParam(defaultValue = "NORMAL") String priority,
            @RequestParam(required = false) String purpose,
            @RequestParam(required = false) Long patientId) {
        try {
            BloodRequest request = bloodBankService.requestBlood(
                bloodGroup, units, priority, purpose, patientId);
            return ResponseEntity.ok(ApiResponse.success(
                "Blood request created successfully", request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // POST /api/blood/issue
    @PostMapping("/api/blood/issue")
    public ResponseEntity<ApiResponse> issueBlood(
            @RequestParam String bloodGroup,
            @RequestParam Integer units,
            @RequestParam(required = false) Long requestId) {
        try {
            BloodInventory inventory =
                bloodBankService.issueBlood(bloodGroup, units, requestId);
            return ResponseEntity.ok(ApiResponse.success(
                units + " units of " + bloodGroup + " issued successfully",
                inventory));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/blood/requests/pending
    @GetMapping("/api/blood/requests/pending")
    public ResponseEntity<ApiResponse> getPendingRequests() {
        List<BloodRequest> requests =
            bloodBankService.getPendingRequests();
        return ResponseEntity.ok(ApiResponse.success(
            "Pending requests: " + requests.size(), requests));
    }

    // GET /api/blood/requests/emergency
    @GetMapping("/api/blood/requests/emergency")
    public ResponseEntity<ApiResponse> getEmergencyRequests() {
        List<BloodRequest> requests =
            bloodBankService.getEmergencyRequests();
        return ResponseEntity.ok(ApiResponse.success(
            "Emergency requests: " + requests.size(), requests));
    }

    // GET /api/blood/search?bloodGroup=O_POSITIVE
    @GetMapping("/api/blood/search")
    public ResponseEntity<ApiResponse> searchBlood(
            @RequestParam String bloodGroup) {
        List<Map<String, Object>> results =
            bloodBankService.searchBloodAcrossHospitals(bloodGroup);
        return ResponseEntity.ok(ApiResponse.success(
            "Blood search results for " + bloodGroup, results));
    }

    // POST /api/donors/register
    @PostMapping("/api/donors/register")
    public ResponseEntity<ApiResponse> registerDonor(
            @RequestBody BloodDonor donor) {
        try {
            BloodDonor saved = bloodBankService.registerDonor(donor);
            return ResponseEntity.ok(ApiResponse.success(
                "Donor registered successfully: " + saved.getFullName(),
                saved));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/donors
    @GetMapping("/api/donors")
    public ResponseEntity<ApiResponse> getAllDonors() {
        List<BloodDonor> donors = bloodBankService.getAllDonors();
        return ResponseEntity.ok(ApiResponse.success(
            "Total donors: " + donors.size(), donors));
    }

    // GET /api/donors/search?bloodGroup=B_POSITIVE
    @GetMapping("/api/donors/search")
    public ResponseEntity<ApiResponse> getDonorsByBloodGroup(
            @RequestParam String bloodGroup) {
        List<BloodDonor> donors =
            bloodBankService.getDonorsByBloodGroup(bloodGroup);
        return ResponseEntity.ok(ApiResponse.success(
            "Donors with " + bloodGroup + ": " + donors.size(), donors));
    }
}
