package medidoc.service;

import medidoc.model.FamilyMember;
import medidoc.model.Patient;
import medidoc.repository.FamilyRepository;
import medidoc.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FamilyService {

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private PatientRepository patientRepository;

    // LINK FAMILY MEMBER
    public FamilyMember linkFamilyMember(String primaryUniqueId,
            String memberUniqueId, String relationship) {

        Optional<Patient> primaryOpt =
            patientRepository.findByUniqueId(primaryUniqueId);
        if (primaryOpt.isEmpty()) {
            throw new RuntimeException(
                "Primary patient not found: " + primaryUniqueId);
        }

        Optional<Patient> memberOpt =
            patientRepository.findByUniqueId(memberUniqueId);
        if (memberOpt.isEmpty()) {
            throw new RuntimeException(
                "Member patient not found: " + memberUniqueId);
        }

        FamilyMember family = new FamilyMember();
        family.setPrimaryPatientId(primaryOpt.get().getId());
        family.setMemberPatientId(memberOpt.get().getId());
        family.setRelationship(relationship);

        FamilyMember saved = familyRepository.save(family);
        System.out.println("Family member linked: "
            + memberUniqueId + " to " + primaryUniqueId);
        return saved;
    }

    // GET FAMILY MEMBERS
    public List<FamilyMember> getFamilyMembers(String uniqueId) {
        Optional<Patient> patientOpt =
            patientRepository.findByUniqueId(uniqueId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Patient not found: " + uniqueId);
        }
        return familyRepository
            .findByPrimaryPatientId(patientOpt.get().getId());
    }
}
