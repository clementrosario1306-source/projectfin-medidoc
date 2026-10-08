package medidoc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medidoc.model.Medicine;
import medidoc.repository.MedicineRepository;

@Service
public class PharmacyService {

    @Autowired
    private MedicineRepository medicineRepository;

    // GET ALL MEDICINES
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    // GET LOW STOCK MEDICINES
    public List<Medicine> getLowStockMedicines() {
        return medicineRepository.findLowStockMedicines();
    }

    // ADD MEDICINE STOCK
    public Medicine addStock(String medicineId, Integer quantity) {
        Optional<Medicine> medicineOpt =
            medicineRepository.findById(medicineId);
        if (medicineOpt.isEmpty()) {
            throw new RuntimeException(
                "Medicine not found: " + medicineId);
        }
        Medicine medicine = medicineOpt.get();
        medicine.setStockQuantity(
            medicine.getStockQuantity() + quantity);
        Medicine saved = medicineRepository.save(medicine);
        System.out.println("Stock added: " + quantity
            + " units of " + medicine.getName());
        return saved;
    }

    // ISSUE MEDICINE TO PATIENT
    public Medicine issueMedicine(String medicineId, Integer quantity) {
        Optional<Medicine> medicineOpt =
            medicineRepository.findById(medicineId);
        if (medicineOpt.isEmpty()) {
            throw new RuntimeException(
                "Medicine not found: " + medicineId);
        }
        Medicine medicine = medicineOpt.get();
        if (medicine.getStockQuantity() < quantity) {
            throw new RuntimeException(
                "Insufficient stock. Available: "
                + medicine.getStockQuantity());
        }
        medicine.setStockQuantity(
            medicine.getStockQuantity() - quantity);
        return medicineRepository.save(medicine);
    }

    // ADD NEW MEDICINE
    public Medicine addMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    // SEARCH MEDICINE
    public List<Medicine> searchMedicine(String name) {
        return medicineRepository
            .findByNameContainingIgnoreCase(name);
    }
}
