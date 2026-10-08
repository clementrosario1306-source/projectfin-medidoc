package medidoc.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medidoc.model.BloodDonor;
import medidoc.model.BloodInventory;
import medidoc.model.BloodRequest;
import medidoc.repository.BloodDonorRepository;
import medidoc.repository.BloodInventoryRepository;
import medidoc.repository.BloodRequestRepository;

@Service
public class BloodBankService {

    @Autowired
    private BloodInventoryRepository bloodInventoryRepository;

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @Autowired
    private BloodDonorRepository bloodDonorRepository;

    // GET ALL BLOOD INVENTORY
    public List<BloodInventory> getAllInventory() {
        return bloodInventoryRepository.findAll();
    }

    // GET LOW STOCK BLOOD GROUPS
    public List<BloodInventory> getLowStockBlood() {
        return bloodInventoryRepository.findLowStockBlood();
    }

    // ADD BLOOD STOCK
    public BloodInventory addStock(String bloodGroup, Integer units) {

        BloodInventory.BloodGroup bg =
            BloodInventory.BloodGroup.valueOf(bloodGroup.toUpperCase());

        Optional<BloodInventory> inventoryOpt =
            bloodInventoryRepository.findByBloodGroup(bg);

        BloodInventory inventory;
        if (inventoryOpt.isPresent()) {
            inventory = inventoryOpt.get();
            inventory.setUnitsAvailable(
                inventory.getUnitsAvailable() + units);
        } else {
            inventory = new BloodInventory();
            inventory.setBloodGroup(bg);
            inventory.setUnitsAvailable(units);
            inventory.setUnitsReserved(0);
        }

        BloodInventory saved = bloodInventoryRepository.save(inventory);
        System.out.println("Blood stock added: " + units
            + " units of " + bloodGroup);
        return saved;
    }

    // REQUEST BLOOD
    public BloodRequest requestBlood(String bloodGroup,
            Integer units, String priority, String purpose,
            String patientId) {

        BloodInventory.BloodGroup bg =
            BloodInventory.BloodGroup.valueOf(bloodGroup.toUpperCase());

        BloodRequest request = new BloodRequest();
        request.setBloodGroup(bg);
        request.setUnitsRequired(units);
        request.setPriority(BloodRequest.Priority.valueOf(
            priority.toUpperCase()));
        request.setPurpose(purpose);
        request.setPatientId(patientId);
        request.setStatus(BloodRequest.Status.PENDING);

        BloodRequest saved = bloodRequestRepository.save(request);
        System.out.println("Blood request created: "
            + units + " units of " + bloodGroup
            + " Priority: " + priority);
        return saved;
    }

    // ISSUE BLOOD TO PATIENT
    public BloodInventory issueBlood(String bloodGroup, Integer units,
            String requestId) {

        BloodInventory.BloodGroup bg =
            BloodInventory.BloodGroup.valueOf(bloodGroup.toUpperCase());

        Optional<BloodInventory> inventoryOpt =
            bloodInventoryRepository.findByBloodGroup(bg);

        if (inventoryOpt.isEmpty()) {
            throw new RuntimeException(
                "No blood inventory found for: " + bloodGroup);
        }

        BloodInventory inventory = inventoryOpt.get();

        if (inventory.getUnitsAvailable() < units) {
            throw new RuntimeException(
                "Insufficient blood units. Available: "
                + inventory.getUnitsAvailable()
                + " Requested: " + units);
        }

        // Deduct units
        inventory.setUnitsAvailable(
            inventory.getUnitsAvailable() - units);
        bloodInventoryRepository.save(inventory);

        // Update request status if requestId provided
        if (requestId != null) {
            Optional<BloodRequest> requestOpt =
                bloodRequestRepository.findById(requestId);
            if (requestOpt.isPresent()) {
                BloodRequest req = requestOpt.get();
                req.setStatus(BloodRequest.Status.ISSUED);
                req.setUnitsIssued(units);
                req.setIssuedAt(LocalDateTime.now());
                bloodRequestRepository.save(req);
            }
        }

        System.out.println("Blood issued: " + units
            + " units of " + bloodGroup);
        return inventory;
    }

    // GET ALL PENDING REQUESTS
    public List<BloodRequest> getPendingRequests() {
        return bloodRequestRepository
            .findByStatus(BloodRequest.Status.PENDING);
    }

    // GET EMERGENCY REQUESTS
    public List<BloodRequest> getEmergencyRequests() {
        return bloodRequestRepository
            .findByPriority(BloodRequest.Priority.EMERGENCY);
    }

    // REGISTER BLOOD DONOR
    public BloodDonor registerDonor(BloodDonor donor) {

        // Check eligibility - must be 18-65 years old
        if (donor.getAge() < 18 || donor.getAge() > 65) {
            throw new RuntimeException(
                "Donor must be between 18 and 65 years old.");
        }

        donor.setIsEligible(true);
        BloodDonor saved = bloodDonorRepository.save(donor);
        System.out.println("Blood donor registered: "
            + saved.getFullName());
        return saved;
    }

    // GET ALL DONORS
    public List<BloodDonor> getAllDonors() {
        return bloodDonorRepository.findAll();
    }

    // GET DONORS BY BLOOD GROUP
    public List<BloodDonor> getDonorsByBloodGroup(String bloodGroup) {
        BloodInventory.BloodGroup bg =
            BloodInventory.BloodGroup.valueOf(bloodGroup.toUpperCase());
        return bloodDonorRepository.findByBloodGroup(bg);
    }

    // SEARCH BLOOD ACROSS HOSPITALS (simulated)
    public List<Map<String, Object>> searchBloodAcrossHospitals(
            String bloodGroup) {
        return List.of(
            Map.of("hospital", "MediDoc Main Hospital",
                   "bloodGroup", bloodGroup,
                   "units", 15,
                   "location", "Chennai"),
            Map.of("hospital", "City Government Hospital",
                   "bloodGroup", bloodGroup,
                   "units", 8,
                   "location", "Chennai North"),
            Map.of("hospital", "District Hospital",
                   "bloodGroup", bloodGroup,
                   "units", 3,
                   "location", "Chennai South")
        );
    }
}