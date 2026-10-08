package medidoc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.BloodInventory;

@Repository
public interface BloodInventoryRepository extends MongoRepository<BloodInventory, String> {

    Optional<BloodInventory> findByBloodGroup(BloodInventory.BloodGroup bloodGroup);

    // Get blood groups with low stock (below 10 units)
    List<BloodInventory> findByUnitsAvailableLessThan(int threshold);

    default List<BloodInventory> findLowStockBlood() {
        return findByUnitsAvailableLessThan(10);
    }
}