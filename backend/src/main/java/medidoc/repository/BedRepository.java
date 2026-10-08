package medidoc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.Bed;

@Repository
public interface BedRepository extends MongoRepository<Bed, String> {

    Optional<Bed> findByBedNumber(String bedNumber);

    List<Bed> findByStatus(Bed.Status status);

    List<Bed> findByWard(String ward);

    List<Bed> findByPatientId(String patientId);

    long countByStatus(Bed.Status status);
}
