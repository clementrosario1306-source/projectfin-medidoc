package medidoc.service;

import medidoc.model.LabReport;
import medidoc.model.Patient;
import medidoc.repository.LabReportRepository;
import medidoc.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LabReportService {

    @Autowired
    private LabReportRepository labReportRepository;

    @Autowired
    private PatientRepository patientRepository;

    // UPLOAD/ADD LAB REPORT
    public LabReport addLabReport(LabReport report) {
        // Simulate OCR extraction
        if (report.getOcrText() == null && report.getFilePath() != null) {
            report.setOcrText(
                "OCR Extracted: " + report.getTestName()
                + " - Result: " + report.getResult());
        }
        report.setStatus(LabReport.Status.COMPLETED);
        LabReport saved = labReportRepository.save(report);
        System.out.println("Lab report added: " + report.getTestName());
        return saved;
    }

    // GET PATIENT LAB REPORTS
    public List<LabReport> getPatientLabReports(String uniqueId) {
        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(uniqueId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found: " + uniqueId);
        }
        return labReportRepository
            .findByPatientIdOrderByTestDateDesc(patientOpt.get().getId());
    }
}