package medidoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.Prescription;

@Repository
public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatientIdOrderByPrescribedDateDesc(Long patientId);

    List<Prescription> findByPatientIdAndStatus(
        Long patientId, Prescription.Status status);
}
