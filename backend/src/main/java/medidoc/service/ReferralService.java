package medidoc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medidoc.model.Doctor;
import medidoc.model.Patient;
import medidoc.model.Referral;
import medidoc.repository.DoctorRepository;
import medidoc.repository.PatientRepository;
import medidoc.repository.ReferralRepository;

@Service
public class ReferralService {

    @Autowired
    private ReferralRepository referralRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // CREATE REFERRAL
    public Referral createReferral(String patientUniqueId,
            String doctorId, String toHospital, String reason,
            String urgency) {

        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(patientUniqueId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException(
                "Patient not found: " + patientUniqueId);
        }

        Optional<Doctor> doctorOpt =
            doctorRepository.findByDoctorId(doctorId);
        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found: " + doctorId);
        }

        Referral referral = new Referral();
        referral.setPatientId(patientOpt.get().getId());
        referral.setReferringDoctorId(doctorOpt.get().getId());
        referral.setToHospital(toHospital);
        referral.setReason(reason);
        if (urgency != null) {
            referral.setUrgency(Referral.Urgency.valueOf(urgency.toUpperCase()));
        }

        Referral saved = referralRepository.save(referral);
        System.out.println("Referral created for patient: "
            + patientUniqueId + " to " + toHospital);
        return saved;
    }

    // GET PATIENT REFERRALS
    public List<Referral> getPatientReferrals(String uniqueId) {
        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(uniqueId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found: " + uniqueId);
        }
        return referralRepository.findByPatientId(patientOpt.get().getId());
    }

    // GET PENDING REFERRALS
    public List<Referral> getPendingReferrals() {
        return referralRepository.findByStatus(Referral.Status.PENDING);
    }

    // UPDATE REFERRAL STATUS
    public Referral updateStatus(String id, String status) {
        Optional<Referral> referralOpt = referralRepository.findById(id);
        if (referralOpt.isEmpty()) {
            throw new RuntimeException("Referral not found: " + id);
        }
        Referral referral = referralOpt.get();
        referral.setStatus(Referral.Status.valueOf(status.toUpperCase()));
        return referralRepository.save(referral);
    }
}
