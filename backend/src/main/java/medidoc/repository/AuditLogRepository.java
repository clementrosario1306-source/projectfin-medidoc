package medidoc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.AuditLog;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

    List<AuditLog> findByUserId(String userId);

    List<AuditLog> findByPatientUniqueNumber(String patientUniqueNumber);

    List<AuditLog> findTop50ByOrderByTimestampDesc();
}
