package medidoc.controller;

import medidoc.dto.ApiResponse;
import medidoc.model.LabReport;
import medidoc.service.LabReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/lab-reports")
@CrossOrigin(origins = "*")
public class LabReportController {

    @Autowired
    private LabReportService labReportService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addLabReport(
            @RequestBody LabReport report) {
        LabReport saved = labReportService.addLabReport(report);
        return ResponseEntity.ok(ApiResponse.success(
            "Lab report added: " + saved.getTestName(), saved));
    }

    @GetMapping("/patient/{uniqueId}")
    public ResponseEntity<ApiResponse> getPatientLabReports(
            @PathVariable String uniqueId) {
        try {
            List<LabReport> reports =
                labReportService.getPatientLabReports(uniqueId);
            return ResponseEntity.ok(ApiResponse.success(
                "Lab reports found: " + reports.size(), reports));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}