package medidoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.AmbulanceRequest;

@Repository
public interface AmbulanceRequestRepository
        extends JpaRepository<AmbulanceRequest, Long> {

    List<AmbulanceRequest> findByStatus(AmbulanceRequest.Status status);

    List<AmbulanceRequest> findByPriority(AmbulanceRequest.Priority priority);
}
