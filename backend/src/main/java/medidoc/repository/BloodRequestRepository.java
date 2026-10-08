package medidoc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.BloodRequest;

@Repository
public interface BloodRequestRepository extends MongoRepository<BloodRequest, String> {

    List<BloodRequest> findByPatientId(String patientId);

    List<BloodRequest> findByStatus(BloodRequest.Status status);

    List<BloodRequest> findByPriority(BloodRequest.Priority priority);
}