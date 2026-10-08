package medidoc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.AmbulanceRequest;

@Repository
public interface AmbulanceRequestRepository extends MongoRepository<AmbulanceRequest, String> {

    List<AmbulanceRequest> findByStatus(AmbulanceRequest.Status status);

    List<AmbulanceRequest> findByPatientId(String patientId);
}
