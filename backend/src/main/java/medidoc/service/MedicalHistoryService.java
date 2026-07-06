package medidoc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medidoc.model.MedicalHistory;
import medidoc.model.Patient;
import medidoc.repository.MedicalHistoryRepository;
import medidoc.repository.PatientRepository;

@Service
public class MedicalHistoryService {

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    @Autowired
    private PatientRepository patientRepository;

    // ADD MEDICAL HISTORY
    public MedicalHistory addHistory(MedicalHistory history) {
        MedicalHistory saved = medicalHistoryRepository.save(history);
        System.out.println("Medical history added for patient: "
            + history.getPatientId());
        return saved;
    }

    // GET PATIENT HISTORY
    public List<MedicalHistory> getPatientHistory(String uniqueId) {
        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(uniqueId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found: " + uniqueId);
        }
        return medicalHistoryRepository
            .findByPatientIdOrderByVisitDateDesc(
                patientOpt.get().getId());
    }
}
