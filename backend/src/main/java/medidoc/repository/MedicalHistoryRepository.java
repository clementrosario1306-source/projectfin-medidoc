package medidoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.MedicalHistory;

@Repository
public interface MedicalHistoryRepository
        extends JpaRepository<MedicalHistory, Long> {

    List<MedicalHistory> findByPatientIdOrderByVisitDateDesc(Long patientId);

    List<MedicalHistory> findByDoctorId(Long doctorId);
}
