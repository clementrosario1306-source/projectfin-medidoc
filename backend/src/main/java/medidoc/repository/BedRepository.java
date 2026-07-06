package medidoc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.Bed;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {

    List<Bed> findByStatus(Bed.Status status);

    List<Bed> findByWard(String ward);

    List<Bed> findByWardAndStatus(String ward, Bed.Status status);

    Optional<Bed> findByBedNumber(String bedNumber);

    long countByStatus(Bed.Status status);

    List<Bed> findByPatientId(Long patientId);
}
