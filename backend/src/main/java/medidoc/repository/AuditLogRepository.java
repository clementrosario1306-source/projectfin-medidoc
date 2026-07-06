package medidoc.repository;

import medidoc.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByPatientUniqueNumber(String patientUniqueNumber);

    List<AuditLog> findByUserId(String userId);

    List<AuditLog> findTop50ByOrderByTimestampDesc();
}
