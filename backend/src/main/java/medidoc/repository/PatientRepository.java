package medidoc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import medidoc.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Find patient by unique ID (e.g. MDID20250601000001)
    Optional<Patient> findByUniqueId(String uniqueId);

    // Find patient by mobile number
    Optional<Patient> findByMobileNumber(String mobileNumber);

    // Check if unique ID already exists
    boolean existsByUniqueId(String uniqueId);

    // Check if mobile number already registered
    boolean existsByMobileNumber(String mobileNumber);

    // Count patients registered today
    @Query("SELECT COUNT(p) FROM Patient p WHERE DATE(p.registrationDate) = CURRENT_DATE")
    long countPatientsRegisteredToday();
}