// ============================================================
// MEDIDOC - Smart e-OPD, AI Health Record & Blood Bank System
// MONGODB INITIALIZATION AND SEED DATA SCRIPT
// Run with: mongosh "mongodb://localhost:27017/medidoc_db" database/mongo-init.js
// ============================================================

db = db.getSiblingDB('medidoc_db');

print("Initializing MongoDB for MediDoc...");

// 1. DOCTORS COLLECTION
db.doctors.drop();
db.doctors.insertMany([
  {
    doctorId: "DOC-101",
    name: "Dr. Ramesh Sharma",
    specialization: "General Physician / Internal Medicine",
    department: "OPD Department",
    mobile: "9876543210",
    email: "ramesh.sharma@medidoc.com",
    qualification: "MBBS, MD (Internal Medicine)",
    experienceYears: 15,
    schedule: "Mon-Sat 09:00 AM - 01:00 PM",
    status: "ACTIVE",
    employeePassword: "password123",
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    doctorId: "DOC-102",
    name: "Dr. Ananya Roy",
    specialization: "Cardiology",
    department: "Cardiology",
    mobile: "9876543211",
    email: "ananya.roy@medidoc.com",
    qualification: "MBBS, MD, DM (Cardiology)",
    experienceYears: 12,
    schedule: "Mon-Fri 10:00 AM - 02:00 PM",
    status: "ACTIVE",
    employeePassword: "password123",
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    doctorId: "DOC-103",
    name: "Dr. Suresh Kumar",
    specialization: "Pediatrics",
    department: "Pediatrics",
    mobile: "9876543212",
    email: "suresh.kumar@medidoc.com",
    qualification: "MBBS, MD (Pediatrics)",
    experienceYears: 10,
    schedule: "Mon-Sat 09:00 AM - 01:00 PM",
    status: "ACTIVE",
    employeePassword: "password123",
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

// 2. STAFF COLLECTION
db.staff.drop();
db.staff.insertMany([
  {
    employeeId: "EMP-001",
    fullName: "Priya Sharma",
    role: "RECEPTIONIST",
    mobile: "9876500001",
    email: "priya.reception@medidoc.com",
    password: "password123",
    status: "ACTIVE",
    createdAt: new Date()
  },
  {
    employeeId: "EMP-002",
    fullName: "Karan Singh",
    role: "BLOOD_BANK_OFFICER",
    mobile: "9876500002",
    email: "karan.bloodbank@medidoc.com",
    password: "password123",
    status: "ACTIVE",
    createdAt: new Date()
  },
  {
    employeeId: "EMP-003",
    fullName: "Admin Officer",
    role: "ADMIN",
    mobile: "9876500003",
    email: "admin@medidoc.com",
    password: "password123",
    status: "ACTIVE",
    createdAt: new Date()
  }
]);

// 3. BLOOD INVENTORY COLLECTION
db.blood_inventory.drop();
db.blood_inventory.insertMany([
  { bloodGroup: "A_POSITIVE", unitsAvailable: 15, unitsReserved: 2, lastUpdated: new Date() },
  { bloodGroup: "A_NEGATIVE", unitsAvailable: 5, unitsReserved: 0, lastUpdated: new Date() },
  { bloodGroup: "B_POSITIVE", unitsAvailable: 20, unitsReserved: 3, lastUpdated: new Date() },
  { bloodGroup: "B_NEGATIVE", unitsAvailable: 3, unitsReserved: 1, lastUpdated: new Date() },
  { bloodGroup: "O_POSITIVE", unitsAvailable: 25, unitsReserved: 5, lastUpdated: new Date() },
  { bloodGroup: "O_NEGATIVE", unitsAvailable: 2, unitsReserved: 1, lastUpdated: new Date() },
  { bloodGroup: "AB_POSITIVE", unitsAvailable: 8, unitsReserved: 0, lastUpdated: new Date() },
  { bloodGroup: "AB_NEGATIVE", unitsAvailable: 1, unitsReserved: 0, lastUpdated: new Date() }
]);

// 4. MEDICINES COLLECTION
db.medicines.drop();
db.medicines.insertMany([
  {
    name: "Paracetamol 500mg",
    genericName: "Paracetamol",
    category: "Analgesic / Antipyretic",
    unit: "Tablet",
    stockQuantity: 500,
    minStockLevel: 50,
    pricePerUnit: 2.50,
    expiryDate: new Date("2027-12-31"),
    manufacturer: "Cipla Ltd",
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    name: "Amoxicillin 500mg",
    genericName: "Amoxicillin",
    category: "Antibiotic",
    unit: "Capsule",
    stockQuantity: 200,
    minStockLevel: 30,
    pricePerUnit: 8.00,
    expiryDate: new Date("2026-10-31"),
    manufacturer: "Sun Pharma",
    createdAt: new Date(),
    updatedAt: new Date()
  },
  {
    name: "Metformin 500mg",
    genericName: "Metformin Hydrochloride",
    category: "Antidiabetic",
    unit: "Tablet",
    stockQuantity: 300,
    minStockLevel: 40,
    pricePerUnit: 4.00,
    expiryDate: new Date("2027-05-31"),
    manufacturer: "Dr. Reddy's",
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

// 5. BEDS COLLECTION
db.beds.drop();
db.beds.insertMany([
  { bedNumber: "BED-G-101", ward: "General Ward 1", roomNumber: "101", floor: "1st Floor", bedType: "GENERAL", status: "AVAILABLE" },
  { bedNumber: "BED-G-102", ward: "General Ward 1", roomNumber: "101", floor: "1st Floor", bedType: "GENERAL", status: "AVAILABLE" },
  { bedNumber: "BED-ICU-01", ward: "ICU Ward", roomNumber: "ICU-1", floor: "2nd Floor", bedType: "ICU", status: "AVAILABLE" },
  { bedNumber: "BED-ICU-02", ward: "ICU Ward", roomNumber: "ICU-2", floor: "2nd Floor", bedType: "ICU", status: "AVAILABLE" },
  { bedNumber: "BED-P-201", ward: "Private Ward", roomNumber: "201", floor: "3rd Floor", bedType: "PRIVATE", status: "AVAILABLE" }
]);

// 6. AMBULANCES COLLECTION
db.ambulances.drop();
db.ambulances.insertMany([
  { vehicleNumber: "TN-01-AM-1001", driverName: "Ramasamy", driverMobile: "9876111111", ambulanceType: "BASIC", status: "AVAILABLE", currentLocation: "Central Station", createdAt: new Date() },
  { vehicleNumber: "TN-01-AM-1002", driverName: "Karthik", driverMobile: "9876111122", ambulanceType: "ADVANCED", status: "AVAILABLE", currentLocation: "Emergency Block", createdAt: new Date() }
]);

// 7. ALERTS COLLECTION
db.alerts.drop();
db.alerts.insertMany([
  { title: "Seasonal Flu Advisory", message: "Increased cases of viral fever reported. Please stay hydrated.", alertType: "GENERAL", severity: "MEDIUM", isActive: true, createdAt: new Date() },
  { title: "O-Negative Blood Urgent Requirement", message: "Urgent need for O-Negative blood donors at Blood Bank.", alertType: "BLOOD_SHORTAGE", severity: "HIGH", isActive: true, createdAt: new Date() }
]);

print("MediDoc MongoDB database initialization complete!");
