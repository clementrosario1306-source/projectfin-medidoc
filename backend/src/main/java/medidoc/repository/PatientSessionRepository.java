package medidoc.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.PatientSession;

@Repository
public interface PatientSessionRepository extends MongoRepository<PatientSession, String> {

    Optional<PatientSession> findByPatientUniqueNumber(String patientUniqueNumber);

    Optional<PatientSession> findByJwtToken(String jwtToken);
}