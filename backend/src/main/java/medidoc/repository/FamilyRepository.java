package medidoc.repository;

import medidoc.model.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FamilyRepository
        extends JpaRepository<FamilyMember, Long> {

    List<FamilyMember> findByPrimaryPatientId(Long primaryPatientId);
}
