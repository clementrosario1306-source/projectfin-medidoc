package medidoc.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import medidoc.model.PrescriptionMedicine;

@Repository
public interface PrescriptionMedicineRepository extends MongoRepository<PrescriptionMedicine, String> {

    List<PrescriptionMedicine> findByPrescriptionId(String prescriptionId);
}