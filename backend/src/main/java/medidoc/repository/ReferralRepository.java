package medidoc.repository;

import medidoc.model.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReferralRepository
        extends JpaRepository<Referral, Long> {

    List<Referral> findByPatientId(Long patientId);

    List<Referral> findByStatus(Referral.Status status);
}
