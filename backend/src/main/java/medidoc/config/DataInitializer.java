package medidoc.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import medidoc.model.Alert;
import medidoc.model.Ambulance;
import medidoc.model.Bed;
import medidoc.model.BloodInventory;
import medidoc.model.Doctor;
import medidoc.model.Medicine;
import medidoc.model.Staff;
import medidoc.repository.AlertRepository;
import medidoc.repository.AmbulanceRepository;
import medidoc.repository.BedRepository;
import medidoc.repository.BloodInventoryRepository;
import medidoc.repository.DoctorRepository;
import medidoc.repository.MedicineRepository;
import medidoc.repository.StaffRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private BloodInventoryRepository bloodInventoryRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private AmbulanceRepository ambulanceRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=================================================");
        System.out.println(" Checking MongoDB Atlas Connection & Sample Data ");
        System.out.println("=================================================");

        try {
            // 1. DOCTORS SEED DATA
            if (doctorRepository.count() == 0) {
                System.out.println("Seeding Doctors into MongoDB Atlas...");
                
                Doctor d1 = new Doctor();
                d1.setDoctorId("DOC-101");
                d1.setName("Dr. Ramesh Sharma");
                d1.setSpecialization("General Physician / Internal Medicine");
                d1.setDepartment("OPD Department");
                d1.setMobile("9876543210");
                d1.setEmail("ramesh.sharma@medidoc.com");
                d1.setQualification("MBBS, MD (Internal Medicine)");
                d1.setExperienceYears(15);
                d1.setSchedule("Mon-Sat 09:00 AM - 01:00 PM");
                d1.setStatus(Doctor.Status.ACTIVE);
                d1.setEmployeePassword("password123");
                doctorRepository.save(d1);

                Doctor d2 = new Doctor();
                d2.setDoctorId("DOC-102");
                d2.setName("Dr. Ananya Roy");
                d2.setSpecialization("Cardiology");
                d2.setDepartment("Cardiology");
                d2.setMobile("9876543211");
                d2.setEmail("ananya.roy@medidoc.com");
                d2.setQualification("MBBS, MD, DM (Cardiology)");
                d2.setExperienceYears(12);
                d2.setSchedule("Mon-Fri 10:00 AM - 02:00 PM");
                d2.setStatus(Doctor.Status.ACTIVE);
                d2.setEmployeePassword("password123");
                doctorRepository.save(d2);

                Doctor d3 = new Doctor();
                d3.setDoctorId("DOC-103");
                d3.setName("Dr. Suresh Kumar");
                d3.setSpecialization("Pediatrics");
                d3.setDepartment("Pediatrics");
                d3.setMobile("9876543212");
                d3.setEmail("suresh.kumar@medidoc.com");
                d3.setQualification("MBBS, MD (Pediatrics)");
                d3.setExperienceYears(10);
                d3.setSchedule("Mon-Sat 09:00 AM - 01:00 PM");
                d3.setStatus(Doctor.Status.ACTIVE);
                d3.setEmployeePassword("password123");
                doctorRepository.save(d3);

                System.out.println("-> Doctors seeded successfully!");
            }

            // 2. STAFF SEED DATA
            if (staffRepository.count() == 0) {
                System.out.println("Seeding Staff into MongoDB Atlas...");

                Staff s1 = new Staff();
                s1.setEmployeeId("EMP-001");
                s1.setFullName("Priya Sharma");
                s1.setRole(Staff.Role.RECEPTIONIST);
                s1.setMobile("9876500001");
                s1.setEmail("priya.reception@medidoc.com");
                s1.setPassword("password123");
                s1.setStatus(Staff.Status.ACTIVE);
                staffRepository.save(s1);

                Staff s2 = new Staff();
                s2.setEmployeeId("EMP-002");
                s2.setFullName("Karan Singh");
                s2.setRole(Staff.Role.BLOOD_BANK_OFFICER);
                s2.setMobile("9876500002");
                s2.setEmail("karan.bloodbank@medidoc.com");
                s2.setPassword("password123");
                s2.setStatus(Staff.Status.ACTIVE);
                staffRepository.save(s2);

                Staff s3 = new Staff();
                s3.setEmployeeId("EMP-003");
                s3.setFullName("Admin Officer");
                s3.setRole(Staff.Role.ADMIN);
                s3.setMobile("9876500003");
                s3.setEmail("admin@medidoc.com");
                s3.setPassword("password123");
                s3.setStatus(Staff.Status.ACTIVE);
                staffRepository.save(s3);

                System.out.println("-> Staff seeded successfully!");
            }

            // 3. BLOOD INVENTORY SEED DATA
            if (bloodInventoryRepository.count() == 0) {
                System.out.println("Seeding Blood Inventory into MongoDB Atlas...");

                for (BloodInventory.BloodGroup bg : BloodInventory.BloodGroup.values()) {
                    BloodInventory bi = new BloodInventory();
                    bi.setBloodGroup(bg);
                    if (bg == BloodInventory.BloodGroup.O_POSITIVE) bi.setUnitsAvailable(25);
                    else if (bg == BloodInventory.BloodGroup.B_POSITIVE) bi.setUnitsAvailable(20);
                    else if (bg == BloodInventory.BloodGroup.A_POSITIVE) bi.setUnitsAvailable(15);
                    else if (bg == BloodInventory.BloodGroup.O_NEGATIVE) bi.setUnitsAvailable(2);
                    else bi.setUnitsAvailable(8);
                    bi.setUnitsReserved(0);
                    bloodInventoryRepository.save(bi);
                }

                System.out.println("-> Blood Inventory seeded successfully!");
            }

            // 4. MEDICINES SEED DATA
            if (medicineRepository.count() == 0) {
                System.out.println("Seeding Medicines into MongoDB Atlas...");

                Medicine m1 = new Medicine();
                m1.setName("Paracetamol 500mg");
                m1.setGenericName("Paracetamol");
                m1.setCategory("Analgesic / Antipyretic");
                m1.setUnit("Tablet");
                m1.setStockQuantity(500);
                m1.setMinStockLevel(50);
                m1.setPricePerUnit(new BigDecimal("2.50"));
                m1.setExpiryDate(LocalDate.now().plusYears(1));
                m1.setManufacturer("Cipla Ltd");
                medicineRepository.save(m1);

                Medicine m2 = new Medicine();
                m2.setName("Amoxicillin 500mg");
                m2.setGenericName("Amoxicillin");
                m2.setCategory("Antibiotic");
                m2.setUnit("Capsule");
                m2.setStockQuantity(200);
                m2.setMinStockLevel(30);
                m2.setPricePerUnit(new BigDecimal("8.00"));
                m2.setExpiryDate(LocalDate.now().plusMonths(6));
                m2.setManufacturer("Sun Pharma");
                medicineRepository.save(m2);

                Medicine m3 = new Medicine();
                m3.setName("Metformin 500mg");
                m3.setGenericName("Metformin Hydrochloride");
                m3.setCategory("Antidiabetic");
                m3.setUnit("Tablet");
                m3.setStockQuantity(300);
                m3.setMinStockLevel(40);
                m3.setPricePerUnit(new BigDecimal("4.00"));
                m3.setExpiryDate(LocalDate.now().plusYears(2));
                m3.setManufacturer("Dr. Reddy's");
                medicineRepository.save(m3);

                System.out.println("-> Medicines seeded successfully!");
            }

            // 5. BEDS SEED DATA
            if (bedRepository.count() == 0) {
                System.out.println("Seeding Beds into MongoDB Atlas...");

                Bed b1 = new Bed();
                b1.setBedNumber("BED-G-101");
                b1.setWard("General Ward 1");
                b1.setRoomNumber("101");
                b1.setFloor("1st Floor");
                b1.setBedType(Bed.BedType.GENERAL);
                b1.setStatus(Bed.Status.AVAILABLE);
                bedRepository.save(b1);

                Bed b2 = new Bed();
                b2.setBedNumber("BED-ICU-01");
                b2.setWard("ICU Ward");
                b2.setRoomNumber("ICU-1");
                b2.setFloor("2nd Floor");
                b2.setBedType(Bed.BedType.ICU);
                b2.setStatus(Bed.Status.AVAILABLE);
                bedRepository.save(b2);

                System.out.println("-> Beds seeded successfully!");
            }

            // 6. AMBULANCES SEED DATA
            if (ambulanceRepository.count() == 0) {
                System.out.println("Seeding Ambulances into MongoDB Atlas...");

                Ambulance a1 = new Ambulance();
                a1.setVehicleNumber("TN-01-AM-1001");
                a1.setDriverName("Ramasamy");
                a1.setDriverMobile("9876111111");
                a1.setAmbulanceType(Ambulance.AmbulanceType.BASIC);
                a1.setStatus(Ambulance.Status.AVAILABLE);
                a1.setCurrentLocation("Central Station");
                ambulanceRepository.save(a1);

                System.out.println("-> Ambulances seeded successfully!");
            }

            // 7. ALERTS SEED DATA
            if (alertRepository.count() == 0) {
                System.out.println("Seeding Alerts into MongoDB Atlas...");

                Alert al1 = new Alert();
                al1.setTitle("Seasonal Flu Advisory");
                al1.setMessage("Increased cases of viral fever reported. Please stay hydrated.");
                al1.setAlertType(Alert.AlertType.GENERAL);
                al1.setSeverity(Alert.Severity.MEDIUM);
                al1.setIsActive(true);
                alertRepository.save(al1);

                System.out.println("-> Alerts seeded successfully!");
            }

            System.out.println("=================================================");
            System.out.println("  MongoDB Atlas Database Connected & Populated!  ");
            System.out.println("=================================================");

        } catch (Exception e) {
            System.err.println("Error connecting/seeding MongoDB Atlas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
