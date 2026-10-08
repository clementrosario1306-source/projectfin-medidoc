package medidoc.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.Patient;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    // Find patient by unique ID (e.g. MDID20250601000001)
    Optional<Patient> findByUniqueId(String uniqueId);

    // Find patient by mobile number
    Optional<Patient> findByMobileNumber(String mobileNumber);

    // Check if unique ID already exists
    boolean existsByUniqueId(String uniqueId);

    // Check if mobile number already registered
    boolean existsByMobileNumber(String mobileNumber);

    long countByRegistrationDateGreaterThanEqual(LocalDateTime date);

    default long countPatientsRegisteredToday() {
        return countByRegistrationDateGreaterThanEqual(LocalDate.now().atStartOfDay());
    }
}