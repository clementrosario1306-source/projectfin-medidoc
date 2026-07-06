package medidoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.BloodRequest;

@Repository
public interface BloodRequestRepository
        extends JpaRepository<BloodRequest, Long> {

    List<BloodRequest> findByStatus(BloodRequest.Status status);

    List<BloodRequest> findByPatientId(Long patientId);

    List<BloodRequest> findByPriority(BloodRequest.Priority priority);
}