package medidoc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.BloodDonor;
import medidoc.model.BloodInventory;

@Repository
public interface BloodDonorRepository extends MongoRepository<BloodDonor, String> {

    List<BloodDonor> findByBloodGroup(BloodInventory.BloodGroup bloodGroup);

    List<BloodDonor> findByBloodGroupAndIsEligibleTrue(BloodInventory.BloodGroup bloodGroup);

    List<BloodDonor> findByMobile(String mobile);
}
