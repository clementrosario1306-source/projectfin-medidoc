package medidoc.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medidoc.dto.PatientFullReport;
import medidoc.model.Appointment;
import medidoc.model.MedicalHistory;
import medidoc.model.Patient;
import medidoc.model.Prescription;
import medidoc.repository.AppointmentRepository;
import medidoc.repository.MedicalHistoryRepository;
import medidoc.repository.PatientRepository;
import medidoc.repository.PrescriptionRepository;

@Service
public class PatientFullReportService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // -----------------------------------------------
    // GET COMPLETE 360 DEGREE PATIENT REPORT
    // This is the main API for doctor dashboard
    // -----------------------------------------------
    public PatientFullReport getFullReport(String uniqueId) {

        // Step 1: Get patient
        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(uniqueId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found: " + uniqueId);
        }
        Patient patient = patientOpt.get();

        // Step 2: Get medical history
        List<MedicalHistory> history =
            medicalHistoryRepository
                .findByPatientIdOrderByVisitDateDesc(patient.getId());

        // Step 3: Get all prescriptions
        List<Prescription> prescriptions =
            prescriptionRepository
                .findByPatientIdOrderByPrescribedDateDesc(patient.getId());

        // Step 4: Get appointments
        List<Appointment> appointments =
            appointmentRepository.findByPatientId(patient.getId());

        // Step 5: Calculate risk level
        String riskLevel = calculateRiskLevel(patient, history);

        // Step 6: Generate AI summary
        String aiSummary = generateAiSummary(patient, history, prescriptions);

        // Step 7: Build full report
        PatientFullReport report = new PatientFullReport();
        report.setPatient(patient);
        report.setMedicalHistory(history);
        report.setPrescriptions(prescriptions);
        report.setAppointments(appointments);
        report.setRiskLevel(riskLevel);
        report.setAiSummary(aiSummary);

        System.out.println("Full report generated for: "
            + patient.getFullName());

        return report;
    }

    // -----------------------------------------------
    // RISK LEVEL CALCULATION
    // Low / Medium / High based on patient data
    // -----------------------------------------------
    private String calculateRiskLevel(Patient patient,
            List<MedicalHistory> history) {

        int score = 0;

        // Age factor
        if (patient.getAge() > 60) score += 30;
        else if (patient.getAge() > 40) score += 15;

        // Number of visits
        if (history.size() > 5) score += 20;
        else if (history.size() > 2) score += 10;

        // Days since last visit
        if (!history.isEmpty() && history.get(0).getVisitDate() != null) {
            long daysSinceVisit = ChronoUnit.DAYS.between(
                history.get(0).getVisitDate().toLocalDate(),
                LocalDate.now());
            if (daysSinceVisit > 90) score += 30;
            else if (daysSinceVisit > 30) score += 15;
        }

        if (score >= 50) return "HIGH";
        if (score >= 25) return "MEDIUM";
        return "LOW";
    }

    // -----------------------------------------------
    // AI SUMMARY GENERATION (Rule-based simulation)
    // In production: call Python Flask AI service
    // -----------------------------------------------
    private String generateAiSummary(Patient patient,
            List<MedicalHistory> history,
            List<Prescription> prescriptions) {

        StringBuilder summary = new StringBuilder();
        summary.append("Patient: ").append(patient.getFullName());
        summary.append(", Age: ").append(patient.getAge());
        if (patient.getBloodGroup() != null) {
            summary.append(", Blood Group: ")
                   .append(patient.getBloodGroup());
        }
        summary.append(". ");

        if (!history.isEmpty()) {
            summary.append("Total visits: ").append(history.size()).append(". ");
            summary.append("Latest diagnosis: ")
                   .append(history.get(0).getDiagnosis()).append(". ");
        } else {
            summary.append("No medical history recorded yet. ");
        }

        summary.append("Total prescriptions: ")
               .append(prescriptions.size()).append(". ");

        long activePrescriptions = prescriptions.stream()
            .filter(p -> p.getStatus() == Prescription.Status.ACTIVE)
            .count();
        summary.append("Active prescriptions: ")
               .append(activePrescriptions).append(".");

        return summary.toString();
    }
}
