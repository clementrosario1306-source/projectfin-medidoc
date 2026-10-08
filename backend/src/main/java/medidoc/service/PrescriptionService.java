package medidoc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medidoc.dto.PrescriptionRequest;
import medidoc.model.Doctor;
import medidoc.model.Patient;
import medidoc.model.Prescription;
import medidoc.model.PrescriptionMedicine;
import medidoc.repository.DoctorRepository;
import medidoc.repository.PatientRepository;
import medidoc.repository.PrescriptionMedicineRepository;
import medidoc.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PrescriptionMedicineRepository prescriptionMedicineRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // ADD NEW PRESCRIPTION
    public Prescription addPrescription(PrescriptionRequest request) {

        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(request.getPatientUniqueId());
        if (patientOpt.isEmpty()) {
            throw new RuntimeException(
                "Patient not found: " + request.getPatientUniqueId());
        }

        Optional<Doctor> doctorOpt =
            doctorRepository.findByDoctorId(request.getDoctorId());
        if (doctorOpt.isEmpty()) {
            throw new RuntimeException(
                "Doctor not found: " + request.getDoctorId());
        }

        // Create prescription
        Prescription prescription = new Prescription();
        prescription.setPatientId(patientOpt.get().getId());
        prescription.setDoctorId(doctorOpt.get().getId());
        prescription.setHistoryId(request.getHistoryId());
        prescription.setNotes(request.getNotes());
        prescription.setStatus(Prescription.Status.ACTIVE);

        Prescription saved = prescriptionRepository.save(prescription);

        // Add medicines to prescription
        if (request.getMedicines() != null) {
            for (PrescriptionRequest.MedicineItem item : request.getMedicines()) {
                PrescriptionMedicine pm = new PrescriptionMedicine();
                pm.setPrescriptionId(saved.getId());
                pm.setMedicineId(item.getMedicineId());
                pm.setDosage(item.getDosage());
                pm.setFrequency(item.getFrequency());
                pm.setDurationDays(item.getDurationDays());
                pm.setInstructions(item.getInstructions());
                prescriptionMedicineRepository.save(pm);
            }
        }

        System.out.println("Prescription added for patient: "
            + request.getPatientUniqueId());
        return saved;
    }

    // GET ALL PRESCRIPTIONS FOR A PATIENT
    public List<Prescription> getPatientPrescriptions(String uniqueId) {
        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(uniqueId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found: " + uniqueId);
        }
        return prescriptionRepository
            .findByPatientIdOrderByPrescribedDateDesc(
                patientOpt.get().getId());
    }

    // GET ACTIVE PRESCRIPTIONS FOR A PATIENT
    public List<Prescription> getActivePrescriptions(String uniqueId) {
        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(uniqueId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found: " + uniqueId);
        }
        return prescriptionRepository.findByPatientIdAndStatus(
            patientOpt.get().getId(), Prescription.Status.ACTIVE);
    }

    // GET MEDICINES FOR A PRESCRIPTION
    public List<PrescriptionMedicine> getPrescriptionMedicines(
            String prescriptionId) {
        return prescriptionMedicineRepository
            .findByPrescriptionId(prescriptionId);
    }
}