package medidoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.BloodDonor;
import medidoc.model.BloodInventory;

@Repository
public interface BloodDonorRepository
        extends JpaRepository<BloodDonor, Long> {

    List<BloodDonor> findByBloodGroup(BloodInventory.BloodGroup bloodGroup);

    List<BloodDonor> findByIsEligible(Boolean isEligible);
}
