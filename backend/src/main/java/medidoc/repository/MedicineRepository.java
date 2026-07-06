package medidoc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import medidoc.model.Medicine;

@Repository
public interface MedicineRepository
        extends JpaRepository<Medicine, Long> {

    List<Medicine> findByCategory(String category);

    // Get medicines with low stock
    @Query("SELECT m FROM Medicine m WHERE m.stockQuantity <= m.minStockLevel")
    List<Medicine> findLowStockMedicines();

    // Search by name
    List<Medicine> findByNameContainingIgnoreCase(String name);
}
