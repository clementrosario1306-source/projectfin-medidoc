package medidoc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import medidoc.model.BloodInventory;

@Repository
public interface BloodInventoryRepository
        extends JpaRepository<BloodInventory, Long> {

    Optional<BloodInventory> findByBloodGroup(
        BloodInventory.BloodGroup bloodGroup);

    // Get blood groups with low stock (below 10 units)
    @Query("SELECT b FROM BloodInventory b WHERE b.unitsAvailable < 10")
    List<BloodInventory> findLowStockBlood();
}