package medidoc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.PatientSession;

@Repository
public interface PatientSessionRepository extends JpaRepository<PatientSession, Long> {

    // Find session by patient unique number
    Optional<PatientSession> findByPatientUniqueNumber(String patientUniqueNumber);

    // Check if session exists
    boolean existsByPatientUniqueNumber(String patientUniqueNumber);

    // Delete old session
    void deleteByPatientUniqueNumber(String patientUniqueNumber);
}