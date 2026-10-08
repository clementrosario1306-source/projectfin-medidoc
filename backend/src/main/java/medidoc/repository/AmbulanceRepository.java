package medidoc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.Ambulance;

@Repository
public interface AmbulanceRepository extends MongoRepository<Ambulance, String> {

    Optional<Ambulance> findByVehicleNumber(String vehicleNumber);

    List<Ambulance> findByStatus(Ambulance.Status status);
}
