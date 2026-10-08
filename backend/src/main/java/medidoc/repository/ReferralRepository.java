package medidoc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.Referral;

@Repository
public interface ReferralRepository extends MongoRepository<Referral, String> {

    List<Referral> findByPatientId(String patientId);

    List<Referral> findByStatus(Referral.Status status);
}
