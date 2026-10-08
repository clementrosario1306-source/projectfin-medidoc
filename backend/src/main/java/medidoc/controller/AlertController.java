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
import org.springframework.web.bind.annotation.RestController;

import medidoc.dto.ApiResponse;
import medidoc.model.Alert;
import medidoc.service.AlertService;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @PostMapping
    public ResponseEntity<ApiResponse> broadcastAlert(
            @RequestBody Alert alert) {
        Alert saved = alertService.broadcastAlert(alert);
        return ResponseEntity.ok(ApiResponse.success(
            "Alert broadcast successfully: " + saved.getTitle(), saved));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getActiveAlerts() {
        List<Alert> alerts = alertService.getActiveAlerts();
        return ResponseEntity.ok(ApiResponse.success(
            "Active alerts: " + alerts.size(), alerts));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllAlerts() {
        List<Alert> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(ApiResponse.success(
            "Total alerts: " + alerts.size(), alerts));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse> deactivateAlert(
            @PathVariable String id) {
        try {
            Alert alert = alertService.deactivateAlert(id);
            return ResponseEntity.ok(ApiResponse.success(
                "Alert deactivated", alert));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse> getAlertsByType(
            @PathVariable String type) {
        List<Alert> alerts = alertService.getAlertsByType(type);
        return ResponseEntity.ok(ApiResponse.success(
            "Alerts of type " + type + ": " + alerts.size(), alerts));
    }
}
