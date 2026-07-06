package medidoc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import medidoc.dto.ApiResponse;
import medidoc.model.Medicine;
import medidoc.service.PharmacyService;

@RestController
@RequestMapping("/api/pharmacy")
@CrossOrigin(origins = "*")
public class PharmacyController {

    @Autowired
    private PharmacyService pharmacyService;

    @GetMapping("/stock")
    public ResponseEntity<ApiResponse> getAllMedicines() {
        List<Medicine> medicines = pharmacyService.getAllMedicines();
        return ResponseEntity.ok(ApiResponse.success(
            "Total medicines: " + medicines.size(), medicines));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse> getLowStock() {
        List<Medicine> medicines =
            pharmacyService.getLowStockMedicines();
        return ResponseEntity.ok(ApiResponse.success(
            "Low stock medicines: " + medicines.size(), medicines));
    }

    @PostMapping("/add-stock")
    public ResponseEntity<ApiResponse> addStock(
            @RequestParam Long medicineId,
            @RequestParam Integer quantity) {
        try {
            Medicine medicine =
                pharmacyService.addStock(medicineId, quantity);
            return ResponseEntity.ok(ApiResponse.success(
                "Stock added successfully", medicine));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/issue")
    public ResponseEntity<ApiResponse> issueMedicine(
            @RequestParam Long medicineId,
            @RequestParam Integer quantity) {
        try {
            Medicine medicine =
                pharmacyService.issueMedicine(medicineId, quantity);
            return ResponseEntity.ok(ApiResponse.success(
                "Medicine issued successfully", medicine));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/add-medicine")
    public ResponseEntity<ApiResponse> addMedicine(
            @RequestBody Medicine medicine) {
        Medicine saved = pharmacyService.addMedicine(medicine);
        return ResponseEntity.ok(ApiResponse.success(
            "Medicine added: " + saved.getName(), saved));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchMedicine(
            @RequestParam String name) {
        List<Medicine> medicines =
            pharmacyService.searchMedicine(name);
        return ResponseEntity.ok(ApiResponse.success(
            "Search results: " + medicines.size(), medicines));
    }
}
