package medidoc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import medidoc.model.Medicine;

@Repository
public interface MedicineRepository extends MongoRepository<Medicine, String> {

    List<Medicine> findByCategory(String category);

    // Get medicines with low stock
    @Query("{ '$expr': { '$lte': ['$stock_quantity', '$min_stock_level'] } }")
    List<Medicine> findLowStockMedicines();

    // Search by name
    List<Medicine> findByNameContainingIgnoreCase(String name);
}
