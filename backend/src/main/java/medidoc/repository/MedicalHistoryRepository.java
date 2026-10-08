package medidoc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.MedicalHistory;

@Repository
public interface MedicalHistoryRepository extends MongoRepository<MedicalHistory, String> {

    List<MedicalHistory> findByPatientIdOrderByVisitDateDesc(String patientId);

    List<MedicalHistory> findByDoctorId(String doctorId);
}
