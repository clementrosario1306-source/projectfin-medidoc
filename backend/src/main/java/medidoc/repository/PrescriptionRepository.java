package medidoc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.Prescription;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    List<Prescription> findByPatientIdOrderByPrescribedDateDesc(String patientId);

    List<Prescription> findByPatientIdAndStatusOrderByPrescribedDateDesc(
        String patientId, Prescription.Status status);

    default List<Prescription> findByPatientIdAndStatus(String patientId, Prescription.Status status) {
        return findByPatientIdAndStatusOrderByPrescribedDateDesc(patientId, status);
    }
}
