package medidoc.controller;

import medidoc.dto.ApiResponse;
import medidoc.repository.*;
import medidoc.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private BloodInventoryRepository bloodInventoryRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // -----------------------------------------------
    // GET /api/analytics/summary
    // Main dashboard summary - used by Admin dashboard
    // -----------------------------------------------
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getSummary() {

        Map<String, Object> summary = new HashMap<>();

        // Patient stats
        summary.put("totalPatients", patientRepository.count());

        // Doctor stats
        summary.put("totalDoctors", doctorRepository.count());
        summary.put("activeDoctors",
            doctorRepository.findByStatus(Doctor.Status.ACTIVE).size());

        // Bed stats
        long totalBeds = bedRepository.count();
        long availableBeds = bedRepository.countByStatus(Bed.Status.AVAILABLE);
        long occupiedBeds = bedRepository.countByStatus(Bed.Status.OCCUPIED);
        summary.put("totalBeds", totalBeds);
        summary.put("availableBeds", availableBeds);
        summary.put("occupiedBeds", occupiedBeds);

        // Blood stats
        List<BloodInventory> bloodInventory = bloodInventoryRepository.findAll();
        int totalBloodUnits = bloodInventory.stream()
            .mapToInt(BloodInventory::getUnitsAvailable)
            .sum();
        summary.put("totalBloodUnits", totalBloodUnits);
        summary.put("lowStockBloodGroups",
            bloodInventoryRepository.findLowStockBlood().size());

        // Medicine stats
        summary.put("totalMedicines", medicineRepository.count());
        summary.put("lowStockMedicines",
            medicineRepository.findLowStockMedicines().size());

        // Appointment stats
        summary.put("totalAppointments", appointmentRepository.count());

        return ResponseEntity.ok(ApiResponse.success(
            "Analytics summary fetched", summary));
    }

    // -----------------------------------------------
    // GET /api/analytics/bed-occupancy
    // For pie chart
    // -----------------------------------------------
    @GetMapping("/bed-occupancy")
    public ResponseEntity<ApiResponse> getBedOccupancy() {
        Map<String, Long> occupancy = new HashMap<>();
        occupancy.put("available",
            bedRepository.countByStatus(Bed.Status.AVAILABLE));
        occupancy.put("occupied",
            bedRepository.countByStatus(Bed.Status.OCCUPIED));
        occupancy.put("maintenance",
            bedRepository.countByStatus(Bed.Status.MAINTENANCE));
        return ResponseEntity.ok(ApiResponse.success(
            "Bed occupancy data", occupancy));
    }

    // -----------------------------------------------
    // GET /api/analytics/blood-levels
    // For bar chart
    // -----------------------------------------------
    @GetMapping("/blood-levels")
    public ResponseEntity<ApiResponse> getBloodLevels() {
        List<BloodInventory> inventory = bloodInventoryRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(
            "Blood inventory levels", inventory));
    }

    // -----------------------------------------------
    // GET /api/analytics/doctor-stats
    // For doctor-wise patient count chart
    // -----------------------------------------------
    @GetMapping("/doctor-stats")
    public ResponseEntity<ApiResponse> getDoctorStats() {
        List<Doctor> doctors = doctorRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(
            "Doctor statistics", doctors));
    }
}
