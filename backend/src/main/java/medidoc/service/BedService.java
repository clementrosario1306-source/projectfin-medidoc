package medidoc.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medidoc.model.Bed;
import medidoc.model.Patient;
import medidoc.repository.BedRepository;
import medidoc.repository.PatientRepository;

@Service
public class BedService {

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private PatientRepository patientRepository;

    // GET ALL AVAILABLE BEDS
    public List<Bed> getAvailableBeds() {
        return bedRepository.findByStatus(Bed.Status.AVAILABLE);
    }

    // GET ALL BEDS
    public List<Bed> getAllBeds() {
        return bedRepository.findAll();
    }

    // GET BEDS BY WARD
    public List<Bed> getBedsByWard(String ward) {
        return bedRepository.findByWard(ward);
    }

    // GET BED SUMMARY (counts per status)
    public Map<String, Long> getBedSummary() {
        Map<String, Long> summary = new HashMap<>();
        summary.put("total", bedRepository.count());
        summary.put("available",
            bedRepository.countByStatus(Bed.Status.AVAILABLE));
        summary.put("occupied",
            bedRepository.countByStatus(Bed.Status.OCCUPIED));
        summary.put("maintenance",
            bedRepository.countByStatus(Bed.Status.MAINTENANCE));
        return summary;
    }

    // ADMIT PATIENT TO BED
    public Bed admitPatient(String bedNumber, String patientUniqueId) {

        // Find bed
        Optional<Bed> bedOpt =
            bedRepository.findByBedNumber(bedNumber);
        if (bedOpt.isEmpty()) {
            throw new RuntimeException("Bed not found: " + bedNumber);
        }

        Bed bed = bedOpt.get();

        // Check bed is available
        if (bed.getStatus() != Bed.Status.AVAILABLE) {
            throw new RuntimeException(
                "Bed " + bedNumber + " is not available. Status: "
                + bed.getStatus());
        }

        // Find patient
        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(patientUniqueId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException(
                "Patient not found: " + patientUniqueId);
        }

        // Admit patient
        bed.setPatientId(patientOpt.get().getId());
        bed.setStatus(Bed.Status.OCCUPIED);
        bed.setAdmittedAt(LocalDateTime.now());

        Bed saved = bedRepository.save(bed);
        System.out.println("Patient " + patientUniqueId
            + " admitted to bed " + bedNumber);
        return saved;
    }

    // DISCHARGE PATIENT FROM BED
    public Bed dischargePatient(String bedNumber) {

        Optional<Bed> bedOpt =
            bedRepository.findByBedNumber(bedNumber);
        if (bedOpt.isEmpty()) {
            throw new RuntimeException("Bed not found: " + bedNumber);
        }

        Bed bed = bedOpt.get();

        if (bed.getStatus() != Bed.Status.OCCUPIED) {
            throw new RuntimeException(
                "Bed " + bedNumber + " is not occupied.");
        }

        bed.setPatientId(null);
        bed.setStatus(Bed.Status.AVAILABLE);
        bed.setAdmittedAt(null);

        Bed saved = bedRepository.save(bed);
        System.out.println("Patient discharged from bed " + bedNumber);
        return saved;
    }

    // UPDATE BED STATUS (for maintenance/cleaning)
    public Bed updateBedStatus(String bedNumber, String status) {

        Optional<Bed> bedOpt =
            bedRepository.findByBedNumber(bedNumber);
        if (bedOpt.isEmpty()) {
            throw new RuntimeException("Bed not found: " + bedNumber);
        }

        Bed bed = bedOpt.get();
        bed.setStatus(Bed.Status.valueOf(status.toUpperCase()));
        return bedRepository.save(bed);
    }
}