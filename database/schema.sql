-- ============================================================
-- MEDIDOC - Smart e-OPD, AI Health Record & Blood Bank System
-- UNIFIED DATABASE SCHEMA & DEMO DATA SCRIPT
-- Run this ENTIRE file in MySQL Workbench
-- ============================================================

CREATE DATABASE IF NOT EXISTS medidoc_db;
USE medidoc_db;

-- ============================================================
-- PHASE 1: SCHEMA CREATION
-- ============================================================

-- TABLE 1: patients
CREATE TABLE IF NOT EXISTS patients (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    unique_id               VARCHAR(25) NOT NULL UNIQUE,
    full_name               VARCHAR(100) NOT NULL,
    age                     INT NOT NULL,
    gender                  ENUM('MALE','FEMALE','OTHER') NOT NULL,
    blood_group             ENUM('A+','A-','B+','B-','O+','O-','AB+','AB-', 'A_POSITIVE', 'A_NEGATIVE', 'B_POSITIVE', 'B_NEGATIVE', 'O_POSITIVE', 'O_NEGATIVE', 'AB_POSITIVE', 'AB_NEGATIVE'),
    mobile_number           VARCHAR(15) NOT NULL,
    address                 TEXT,
    emergency_contact_name  VARCHAR(100),
    emergency_contact_phone VARCHAR(15),
    abha_number             VARCHAR(20),
    pmjay_id                VARCHAR(20),
    is_pmjay_eligible       BOOLEAN DEFAULT FALSE,
    registration_date       DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_at              DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- TABLE 2: doctors
CREATE TABLE IF NOT EXISTS doctors (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id           VARCHAR(20) NOT NULL UNIQUE,
    name                VARCHAR(100) NOT NULL,
    specialization      VARCHAR(100) NOT NULL,
    department          VARCHAR(100) NOT NULL,
    mobile              VARCHAR(15),
    email               VARCHAR(100),
    qualification       VARCHAR(200),
    experience_years    INT DEFAULT 0,
    schedule            TEXT,
    status              ENUM('ACTIVE','INACTIVE','ON_LEAVE') DEFAULT 'ACTIVE',
    employee_password   VARCHAR(255) NOT NULL,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- TABLE 3: staff
CREATE TABLE IF NOT EXISTS staff (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id     VARCHAR(20) NOT NULL UNIQUE,
    full_name       VARCHAR(100) NOT NULL,
    role            ENUM('RECEPTIONIST','BLOOD_BANK_OFFICER','ADMIN') NOT NULL,
    mobile          VARCHAR(15),
    email           VARCHAR(100),
    password        VARCHAR(255) NOT NULL,
    status          ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- TABLE 4: medical_history
CREATE TABLE IF NOT EXISTS medical_history (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id      BIGINT NOT NULL,
    doctor_id       BIGINT NOT NULL,
    visit_date      DATETIME DEFAULT CURRENT_TIMESTAMP,
    chief_complaint TEXT,
    diagnosis       TEXT NOT NULL,
    notes           TEXT,
    follow_up_date  DATE,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id)  REFERENCES doctors(id)
);

-- TABLE 5: prescriptions
CREATE TABLE IF NOT EXISTS prescriptions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id      BIGINT NOT NULL,
    doctor_id       BIGINT NOT NULL,
    history_id      BIGINT,
    prescribed_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    notes           TEXT,
    status          ENUM('ACTIVE','COMPLETED','CANCELLED') DEFAULT 'ACTIVE',
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id)  REFERENCES doctors(id),
    FOREIGN KEY (history_id) REFERENCES medical_history(id)
);

-- TABLE 6: medicines
CREATE TABLE IF NOT EXISTS medicines (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    generic_name    VARCHAR(150),
    category        VARCHAR(100),
    unit            VARCHAR(50),
    stock_quantity  INT DEFAULT 0,
    min_stock_level INT DEFAULT 10,
    price_per_unit  DECIMAL(10,2) DEFAULT 0,
    expiry_date     DATE,
    manufacturer    VARCHAR(150),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- TABLE 7: prescriptions_medicines
CREATE TABLE IF NOT EXISTS prescriptions_medicines (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    medicine_id     BIGINT NOT NULL,
    dosage          VARCHAR(100),
    frequency       VARCHAR(100),
    duration_days   INT,
    instructions    TEXT,
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id),
    FOREIGN KEY (medicine_id)     REFERENCES medicines(id)
);

-- TABLE 8: appointments
CREATE TABLE IF NOT EXISTS appointments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id      BIGINT NOT NULL,
    doctor_id       BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    time_slot       VARCHAR(20),
    token_number    INT NOT NULL,
    queue_position  INT,
    status          ENUM('BOOKED','IN_PROGRESS','COMPLETED','CANCELLED','NO_SHOW') DEFAULT 'BOOKED',
    booked_by       ENUM('PATIENT','RECEPTIONIST') DEFAULT 'PATIENT',
    notes           TEXT,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id)  REFERENCES doctors(id)
);

-- TABLE 9: blood_inventory
CREATE TABLE IF NOT EXISTS blood_inventory (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    blood_group     VARCHAR(20) NOT NULL UNIQUE,
    units_available INT DEFAULT 0,
    units_reserved  INT DEFAULT 0,
    last_updated    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- TABLE 10: blood_requests
CREATE TABLE IF NOT EXISTS blood_requests (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id      BIGINT,
    requested_by    BIGINT,
    blood_group     VARCHAR(20) NOT NULL,
    units_required  INT NOT NULL,
    units_issued    INT DEFAULT 0,
    priority        ENUM('NORMAL','URGENT','EMERGENCY') DEFAULT 'NORMAL',
    status          ENUM('PENDING','APPROVED','ISSUED','REJECTED','CANCELLED') DEFAULT 'PENDING',
    purpose         TEXT,
    requested_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    issued_at       DATETIME,
    FOREIGN KEY (patient_id) REFERENCES patients(id)
);

-- TABLE 11: blood_donors
CREATE TABLE IF NOT EXISTS blood_donors (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(100) NOT NULL,
    blood_group     VARCHAR(20) NOT NULL,
    mobile          VARCHAR(15) NOT NULL,
    age             INT,
    address         TEXT,
    last_donation   DATE,
    donation_count  INT DEFAULT 0,
    is_eligible     BOOLEAN DEFAULT TRUE,
    registered_at   DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- TABLE 12: beds
CREATE TABLE IF NOT EXISTS beds (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    bed_number      VARCHAR(20) NOT NULL UNIQUE,
    ward            VARCHAR(100) NOT NULL,
    room_number     VARCHAR(20),
    floor           VARCHAR(10),
    bed_type        ENUM('GENERAL','SEMI_PRIVATE','PRIVATE','ICU','HDU') DEFAULT 'GENERAL',
    status          ENUM('AVAILABLE','OCCUPIED','MAINTENANCE','CLEANING') DEFAULT 'AVAILABLE',
    patient_id      BIGINT,
    admitted_at     DATETIME,
    FOREIGN KEY (patient_id) REFERENCES patients(id)
);

-- TABLE 13: ambulances
CREATE TABLE IF NOT EXISTS ambulances (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_number  VARCHAR(20) NOT NULL UNIQUE,
    driver_name     VARCHAR(100),
    driver_mobile   VARCHAR(15),
    ambulance_type  ENUM('BASIC','ADVANCED','NEONATAL','MORTUARY') DEFAULT 'BASIC',
    status          ENUM('AVAILABLE','ON_DUTY','MAINTENANCE') DEFAULT 'AVAILABLE',
    current_location VARCHAR(255),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- TABLE 14: ambulance_requests
CREATE TABLE IF NOT EXISTS ambulance_requests (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id      BIGINT,
    ambulance_id    BIGINT,
    requester_name  VARCHAR(100),
    requester_phone VARCHAR(15),
    pickup_address  TEXT NOT NULL,
    destination     VARCHAR(255),
    status          ENUM('REQUESTED','ASSIGNED','EN_ROUTE','COMPLETED','CANCELLED') DEFAULT 'REQUESTED',
    priority        ENUM('NORMAL','EMERGENCY') DEFAULT 'NORMAL',
    requested_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    completed_at    DATETIME,
    FOREIGN KEY (patient_id)   REFERENCES patients(id),
    FOREIGN KEY (ambulance_id) REFERENCES ambulances(id)
);

-- TABLE 15: doctor_performance
CREATE TABLE IF NOT EXISTS doctor_performance (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id            BIGINT NOT NULL,
    record_date          DATE NOT NULL,
    patients_seen        INT DEFAULT 0,
    avg_consultation_min INT DEFAULT 0,
    prescriptions_given  INT DEFAULT 0,
    follow_ups_scheduled INT DEFAULT 0,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);

-- TABLE 16: referrals
CREATE TABLE IF NOT EXISTS referrals (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id          BIGINT NOT NULL,
    referring_doctor_id BIGINT NOT NULL,
    from_hospital       VARCHAR(200) DEFAULT 'MediDoc Hospital',
    to_hospital         VARCHAR(200) NOT NULL,
    reason              TEXT NOT NULL,
    urgency             ENUM('ROUTINE','URGENT','EMERGENCY') DEFAULT 'ROUTINE',
    status              ENUM('PENDING','ACCEPTED','COMPLETED','REJECTED') DEFAULT 'PENDING',
    referred_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    notes               TEXT,
    FOREIGN KEY (patient_id)          REFERENCES patients(id),
    FOREIGN KEY (referring_doctor_id) REFERENCES doctors(id)
);

-- TABLE 17: lab_reports
CREATE TABLE IF NOT EXISTS lab_reports (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id      BIGINT NOT NULL,
    doctor_id       BIGINT,
    test_name       VARCHAR(200) NOT NULL,
    test_date       DATE NOT NULL,
    result          TEXT,
    normal_range    VARCHAR(200),
    file_path       VARCHAR(500),
    ocr_text        TEXT,
    status          ENUM('PENDING','COMPLETED') DEFAULT 'PENDING',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_id)  REFERENCES doctors(id)
);

-- TABLE 18: family_members
CREATE TABLE IF NOT EXISTS family_members (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    primary_patient_id BIGINT NOT NULL,
    member_patient_id  BIGINT NOT NULL,
    relationship       VARCHAR(50),
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (primary_patient_id) REFERENCES patients(id),
    FOREIGN KEY (member_patient_id)  REFERENCES patients(id)
);

-- TABLE 19: alerts
CREATE TABLE IF NOT EXISTS alerts (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    message     TEXT NOT NULL,
    alert_type  ENUM('EPIDEMIC','BLOOD_SHORTAGE','BED_SHORTAGE','GENERAL','EMERGENCY') NOT NULL,
    severity    ENUM('LOW','MEDIUM','HIGH','CRITICAL') DEFAULT 'MEDIUM',
    created_by  BIGINT,
    is_active   BOOLEAN DEFAULT TRUE,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    expires_at  DATETIME
);

-- TABLE 20: alert_responses
CREATE TABLE IF NOT EXISTS alert_responses (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_id     BIGINT NOT NULL,
    responded_by BIGINT NOT NULL,
    response_text TEXT,
    responded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (alert_id) REFERENCES alerts(id)
);

-- TABLE 21: audit_logs
CREATE TABLE IF NOT EXISTS audit_logs (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id               VARCHAR(50),
    role                  VARCHAR(50),
    action                VARCHAR(200) NOT NULL,
    patient_unique_number VARCHAR(25),
    description           TEXT,
    ip_address            VARCHAR(50),
    timestamp             DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- TABLE 22: patient_sessions
CREATE TABLE IF NOT EXISTS patient_sessions (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_unique_number VARCHAR(25) NOT NULL,
    otp                   VARCHAR(10),
    otp_expiry            DATETIME,
    is_logged_in          BOOLEAN DEFAULT FALSE,
    last_login            DATETIME,
    jwt_token             TEXT
);

-- TABLE 23: staff_sessions
CREATE TABLE IF NOT EXISTS staff_sessions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL,
    role        VARCHAR(50) NOT NULL,
    is_logged_in BOOLEAN DEFAULT FALSE,
    login_time  DATETIME,
    logout_time DATETIME,
    jwt_token   TEXT
);


-- ============================================================
-- PHASE 2: CLEANUP (Allows re-running safely)
-- ============================================================
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM prescriptions_medicines;
DELETE FROM prescriptions;
DELETE FROM medical_history;
DELETE FROM lab_reports;
DELETE FROM appointments;
DELETE FROM patient_sessions;
DELETE FROM blood_requests;
DELETE FROM blood_donors;
DELETE FROM alerts;
DELETE FROM ambulance_requests;
DELETE FROM family_members;
DELETE FROM referrals;
DELETE FROM audit_logs;
SET FOREIGN_KEY_CHECKS = 1;


-- ============================================================
-- PHASE 3: HOSPITAL INFRASTRUCTURE SETUP
-- ============================================================

-- Blood Inventory starting stock
INSERT IGNORE INTO blood_inventory (blood_group, units_available, units_reserved) VALUES
('A+',  45, 5),
('A-',  12, 2),
('B+',  38, 4),
('B-',   8, 1),
('O+',  60, 8),
('O-',  15, 3),
('AB+', 20, 2),
('AB-',  6, 0),
('A_POSITIVE', 45, 5),
('A_NEGATIVE', 12, 2),
('B_POSITIVE', 38, 3),
('B_NEGATIVE', 6, 1),
('O_POSITIVE', 60, 8),
('O_NEGATIVE', 15, 2),
('AB_POSITIVE', 20, 3),
('AB_NEGATIVE', 4, 1);

-- Demo Staff
INSERT IGNORE INTO staff (employee_id, full_name, role, mobile, email, password) VALUES
('REC001',   'Priya Sharma',  'RECEPTIONIST',       '9876543211', 'priya@medidoc.in',  'rec123'),
('BBO001',   'Raju Verma',    'BLOOD_BANK_OFFICER',  '9876543212', 'raju@medidoc.in',   'blood123'),
('ADMIN001', 'Suresh Kumar',  'ADMIN',               '9876543213', 'suresh@medidoc.in', 'admin123');

-- Demo Doctors
INSERT IGNORE INTO doctors (doctor_id, name, specialization, department, mobile, email, qualification, experience_years, schedule, employee_password) VALUES
('DOC001', 'Dr. Rajesh Sharma', 'General Medicine', 'OPD',       '9876500001', 'rajesh@medidoc.in', 'MBBS, MD',        15, '{"mon":"9-13","tue":"9-13","wed":"9-13","thu":"9-13","fri":"9-13"}', 'doctor123'),
('DOC002', 'Dr. Anita Patel',   'Gynecology',       'Maternity', '9876500002', 'anita@medidoc.in',  'MBBS, MS',        12, '{"mon":"10-14","wed":"10-14","fri":"10-14"}',                        'doctor123'),
('DOC003', 'Dr. Mohan Singh',   'Pediatrics',       'Pediatric', '9876500003', 'mohan@medidoc.in',  'MBBS, DCH',        8, '{"tue":"9-13","thu":"9-13","sat":"9-13"}',                           'doctor123'),
('DOC004', 'Dr. Kavitha Reddy', 'Orthopedics',      'Surgery',   '9876500004', 'kavitha@medidoc.in','MBBS, MS Ortho',  10, '{"mon":"14-18","wed":"14-18","fri":"14-18"}',                        'doctor123'),
('DOC005', 'Dr. Arjun Nair',    'Cardiology',       'Cardiology','9876500005', 'arjun@medidoc.in',  'MBBS, DM Cardio', 18, '{"mon":"9-13","thu":"9-13"}',                                        'doctor123');

-- Demo Medicines
INSERT IGNORE INTO medicines (name, generic_name, category, unit, stock_quantity, min_stock_level, price_per_unit) VALUES
('Paracetamol 500mg',  'Paracetamol', 'Analgesic',        'Tablet', 500, 50, 1.50),
('Amoxicillin 500mg',  'Amoxicillin', 'Antibiotic',       'Capsule',300, 30, 8.00),
('Metformin 500mg',    'Metformin',   'Antidiabetic',     'Tablet', 400, 40, 3.00),
('Amlodipine 5mg',     'Amlodipine',  'Antihypertensive', 'Tablet', 250, 25, 5.00),
('Omeprazole 20mg',    'Omeprazole',  'Antacid',          'Capsule',350, 35, 4.50),
('Cetirizine 10mg',    'Cetirizine',  'Antihistamine',    'Tablet', 200, 20, 2.00),
('Ibuprofen 400mg',    'Ibuprofen',   'NSAID',            'Tablet', 450, 45, 3.50),
('Azithromycin 500mg', 'Azithromycin','Antibiotic',       'Tablet', 150, 15,15.00),
('Normal Saline 500ml','Sodium Chloride','IV Fluid',      'Bottle',  80, 10,45.00),
('Dextrose 5% 500ml',  'Dextrose',    'IV Fluid',         'Bottle',  60, 10,50.00);

-- Demo Beds
INSERT IGNORE INTO beds (bed_number, ward, room_number, floor, bed_type, status) VALUES
('GEN-001','General Ward',  'G-101','Ground','GENERAL',     'AVAILABLE'),
('GEN-002','General Ward',  'G-101','Ground','GENERAL',     'OCCUPIED'),
('GEN-003','General Ward',  'G-102','Ground','GENERAL',     'AVAILABLE'),
('GEN-004','General Ward',  'G-102','Ground','GENERAL',     'OCCUPIED'),
('GEN-005','General Ward',  'G-103','Ground','GENERAL',     'AVAILABLE'),
('GEN-006','General Ward',  'G-103','Ground','GENERAL',     'AVAILABLE'),
('GEN-007','General Ward',  'G-104','Ground','GENERAL',     'AVAILABLE'),
('GEN-008','General Ward',  'G-104','Ground','GENERAL',     'OCCUPIED'),
('ICU-001','ICU',           'I-101','First', 'ICU',         'AVAILABLE'),
('ICU-002','ICU',           'I-101','First', 'ICU',         'OCCUPIED'),
('ICU-003','ICU',           'I-102','First', 'ICU',         'MAINTENANCE'),
('HDU-001','HDU',           'H-101','First', 'HDU',         'AVAILABLE'),
('HDU-002','HDU',           'H-101','First', 'HDU',         'AVAILABLE'),
('HDU-003','HDU',           'H-102','First', 'HDU',         'AVAILABLE'),
('MAT-001','Maternity Ward','M-101','Second','GENERAL',     'AVAILABLE'),
('MAT-002','Maternity Ward','M-101','Second','GENERAL',     'OCCUPIED'),
('MAT-003','Maternity Ward','M-102','Second','GENERAL',     'AVAILABLE'),
('PED-001','Pediatric Ward','P-101','Second','GENERAL',     'AVAILABLE'),
('PED-002','Pediatric Ward','P-101','Second','GENERAL',     'AVAILABLE'),
('PED-003','Pediatric Ward','P-102','Second','GENERAL',     'AVAILABLE'),
('SUR-001','Surgical Ward', 'S-101','Third', 'SEMI_PRIVATE','AVAILABLE'),
('SUR-002','Surgical Ward', 'S-101','Third', 'SEMI_PRIVATE','AVAILABLE'),
('SUR-003','Surgical Ward', 'S-102','Third', 'PRIVATE',     'AVAILABLE'),
('PVT-001','Private Ward',  'P-201','Third', 'PRIVATE',     'AVAILABLE'),
('PVT-002','Private Ward',  'P-202','Third', 'PRIVATE',     'OCCUPIED');

-- Demo Ambulances
INSERT IGNORE INTO ambulances (vehicle_number, driver_name, driver_mobile, ambulance_type, status, current_location) VALUES
('TN-01-AM-1001','Ramesh Kumar', '9876540001','BASIC',    'AVAILABLE',     'Main Gate'),
('TN-01-AM-1002','Suresh Rao',   '9876540002','ADVANCED', 'AVAILABLE',     'Emergency Bay'),
('TN-01-AM-1003','Mahesh Singh', '9876540003','BASIC',    'ON_DUTY',       'City Center - En Route'),
('TN-01-AM-1004','Dinesh Babu',  '9876540004','NEONATAL', 'AVAILABLE',     'Maternity Block');


-- ============================================================
-- PHASE 4: DEMO TRANSACTIONS (Patients, Appointments, etc.)
-- ============================================================

INSERT IGNORE INTO patients 
(unique_id, full_name, age, gender, blood_group, mobile_number, address, emergency_contact_name, emergency_contact_phone, is_pmjay_eligible, registration_date, created_at, updated_at) 
VALUES
('MDID20260605000001', 'Clement Rosario',    22, 'MALE',   'B_POSITIVE',  '9876543210', 'Chennai, Tamil Nadu',        'Mary Rosario',    '9876543211', 0, NOW(), NOW(), NOW()),
('MDID20260605000002', 'Priya Sharma',       35, 'FEMALE', 'A_POSITIVE',  '9876543220', 'Mumbai, Maharashtra',        'Raj Sharma',      '9876543221', 1, NOW(), NOW(), NOW()),
('MDID20260605000003', 'Ravi Kumar',         58, 'MALE',   'O_POSITIVE',  '9876543230', 'Delhi, Delhi',               'Sunita Kumar',    '9876543231', 1, NOW(), NOW(), NOW()),
('MDID20260605000004', 'Lakshmi Devi',       45, 'FEMALE', 'AB_POSITIVE', '9876543240', 'Bangalore, Karnataka',       'Suresh Devi',     '9876543241', 0, NOW(), NOW(), NOW()),
('MDID20260605000005', 'Mohammed Farhan',    30, 'MALE',   'B_NEGATIVE',  '9876543250', 'Hyderabad, Telangana',       'Fatima Farhan',   '9876543251', 1, NOW(), NOW(), NOW()),
('MDID20260605000006', 'Ananya Krishnan',    28, 'FEMALE', 'O_NEGATIVE',  '9876543260', 'Kochi, Kerala',              'Arun Krishnan',   '9876543261', 0, NOW(), NOW(), NOW()),
('MDID20260605000007', 'Suresh Patel',       62, 'MALE',   'A_NEGATIVE',  '9876543270', 'Ahmedabad, Gujarat',         'Meena Patel',     '9876543271', 1, NOW(), NOW(), NOW()),
('MDID20260605000008', 'Kavitha Reddy',      40, 'FEMALE', 'AB_NEGATIVE', '9876543280', 'Hyderabad, Telangana',       'Ramesh Reddy',    '9876543281', 1, NOW(), NOW(), NOW()),
('MDID20260605000009', 'Arjun Nair',         25, 'MALE',   'O_POSITIVE',  '9876543290', 'Thiruvananthapuram, Kerala', 'Suma Nair',       '9876543291', 0, NOW(), NOW(), NOW()),
('MDID20260605000010', 'Deepa Murugan',      50, 'FEMALE', 'B_POSITIVE',  '9876543300', 'Coimbatore, Tamil Nadu',     'Murugan Selvam',  '9876543301', 1, NOW(), NOW(), NOW());

INSERT INTO medical_history (patient_id, doctor_id, visit_date, chief_complaint, diagnosis, notes, follow_up_date) VALUES
(1, 1, '2026-01-10 10:00:00', 'Fever and body pain', 'Viral Fever', 'Temperature 102F. Prescribed Paracetamol and rest. Advised hydration.', '2026-01-17'),
(1, 1, '2026-03-15 11:00:00', 'High BP reading at home', 'Hypertension Stage 1', 'BP 145/92 mmHg. Started Amlodipine 5mg. Salt restriction advised. Monthly monitoring required.', '2026-04-15'),
(1, 2, '2026-05-20 09:30:00', 'Routine checkup', 'Routine Health Checkup - Stable', 'BP controlled at 128/84 mmHg on medication. Blood sugar normal. Continue current medication.', '2026-08-20'),
(2, 2, '2026-02-05 10:30:00', 'Chest pain and breathlessness', 'Anxiety Disorder with Chest Discomfort', 'ECG normal. No cardiac involvement. Referred to psychiatrist for anxiety management.', '2026-03-05'),
(2, 1, '2026-04-10 14:00:00', 'Knee pain while walking', 'Osteoarthritis - Right Knee', 'X-ray shows mild joint space narrowing. Physiotherapy recommended. Diclofenac gel prescribed.', '2026-07-10'),
(3, 1, '2025-11-20 09:00:00', 'Increased thirst and frequent urination', 'Type 2 Diabetes Mellitus', 'Fasting sugar 210 mg/dL. HbA1c 8.2%. Started Metformin 500mg twice daily. Diabetic diet chart given.', '2025-12-20'),
(3, 1, '2026-01-25 10:00:00', 'Diabetes follow-up', 'Type 2 Diabetes - Partially Controlled', 'Fasting sugar 165 mg/dL. HbA1c improved to 7.4%. Increased Metformin to 1000mg. Continue monitoring.', '2026-04-25'),
(3, 3, '2026-04-28 11:30:00', 'Eye blurring for 2 months', 'Diabetic Retinopathy - Early Stage', 'Referred to ophthalmology. Strict blood sugar control essential. Added eye drops.', '2026-07-28'),
(4, 2, '2026-02-14 10:00:00', 'Irregular periods and weight gain', 'Polycystic Ovarian Syndrome (PCOS)', 'Ultrasound confirms PCOS. Started on Metformin and lifestyle modification. Diet and exercise plan given.', '2026-05-14'),
(4, 2, '2026-05-16 11:00:00', 'PCOS follow-up', 'PCOS - Improving with Treatment', 'Cycles more regular. Weight reduced by 3kg. Continue medication for 3 more months.', '2026-08-16'),
(5, 1, '2026-03-01 09:30:00', 'Severe headache for 3 days', 'Migraine without Aura', 'Classical migraine presentation. Prescribed Sumatriptan for acute attacks. Avoid triggers.', '2026-04-01'),
(5, 1, '2026-05-10 10:00:00', 'Migraine follow-up', 'Migraine - Well Controlled', 'Frequency reduced from weekly to monthly. Continue prophylactic medication. Good response.', '2026-08-10'),
(7, 1, '2026-01-05 09:00:00', 'Chest pain radiating to left arm', 'Stable Angina Pectoris', 'ECG shows ST changes. Started on Aspirin, Atenolol, and Nitrates. Cardiology referral done.', '2026-02-05'),
(7, 3, '2026-03-10 14:00:00', 'Joint pain in multiple joints', 'Rheumatoid Arthritis', 'Anti-CCP positive. Started on Hydroxychloroquine. Physiotherapy recommended. Monthly follow-up.', '2026-04-10');

INSERT INTO lab_reports (patient_id, doctor_id, test_name, test_date, result, normal_range, status) VALUES
(1, 1, 'Complete Blood Count (CBC)',  '2026-03-15', 'Normal - All parameters within range', 'Standard reference', 'COMPLETED'),
(1, 1, 'Blood Pressure Monitoring',  '2026-03-15', '145/92 mmHg',  '120/80 mmHg', 'COMPLETED'),
(1, 1, 'Lipid Profile',               '2026-05-20', 'Total Cholesterol 195 mg/dL - Borderline', 'Less than 200 mg/dL', 'COMPLETED'),
(2, 2, 'ECG',                         '2026-02-05', 'Normal Sinus Rhythm - No ST changes', 'Normal', 'COMPLETED'),
(2, 1, 'X-Ray Right Knee',            '2026-04-10', 'Mild joint space narrowing noted', 'Normal joint space', 'COMPLETED'),
(3, 1, 'Fasting Blood Sugar',        '2025-11-20', '210 mg/dL - High', '70-100 mg/dL', 'COMPLETED'),
(3, 1, 'HbA1c',                       '2025-11-20', '8.2% - Poorly Controlled', 'Below 7%', 'COMPLETED'),
(3, 1, 'Fasting Blood Sugar',        '2026-01-25', '165 mg/dL - Improving', '70-100 mg/dL', 'COMPLETED'),
(3, 1, 'HbA1c',                       '2026-01-25', '7.4% - Improving', 'Below 7%', 'COMPLETED'),
(3, 3, 'Fundus Examination',          '2026-04-28', 'Early diabetic changes in retina', 'Normal', 'COMPLETED'),
(4, 2, 'Pelvic Ultrasound',           '2026-02-14', 'Multiple follicles in both ovaries - PCOS confirmed', 'Normal ovarian morphology', 'COMPLETED'),
(4, 2, 'Hormonal Profile (LH/FSH)', '2026-02-14', 'LH:FSH ratio 2.5 - Elevated', 'Ratio less than 2', 'COMPLETED'),
(7, 1, 'ECG',                         '2026-01-05', 'ST depression in V4-V6 leads', 'Normal', 'COMPLETED'),
(7, 1, 'Troponin I',                  '2026-01-05', '0.02 ng/mL - Normal', 'Less than 0.04 ng/mL', 'COMPLETED'),
(7, 3, 'Anti-CCP Antibody',           '2026-03-10', 'Positive - 48 U/mL', 'Less than 20 U/mL', 'COMPLETED');

INSERT INTO prescriptions (patient_id, doctor_id, prescribed_date, notes, status) VALUES
(1, 1, '2026-01-10 10:05:00', 'Take Paracetamol after food. Complete 5 day course.', 'COMPLETED'),
(1, 1, '2026-03-15 11:10:00', 'Take Amlodipine once daily in morning. Monitor BP weekly. Low salt diet.', 'ACTIVE'),
(2, 2, '2026-04-10 14:05:00', 'Apply Diclofenac gel twice daily on knee. Take Physiotherapy sessions.', 'ACTIVE'),
(3, 1, '2025-11-20 09:10:00', 'Metformin 500mg twice daily after food. Monitor blood sugar weekly.', 'COMPLETED'),
(3, 1, '2026-01-25 10:10:00', 'Metformin 1000mg twice daily. Continue diabetic diet. Monthly HbA1c.', 'ACTIVE'),
(7, 1, '2026-01-05 09:15:00', 'Aspirin 75mg once daily. Atenolol 25mg once daily. Sorbitrate SOS.', 'ACTIVE');

INSERT INTO appointments (patient_id, doctor_id, appointment_date, time_slot, token_number, queue_position, status, booked_by) VALUES
(1, 1, '2026-07-15', '10:00-10:15', 1, 1, 'BOOKED',      'PATIENT'),
(2, 2, '2026-07-15', '10:15-10:30', 2, 2, 'BOOKED',      'PATIENT'),
(3, 1, '2026-07-15', '10:30-10:45', 3, 3, 'BOOKED',      'RECEPTIONIST'),
(4, 2, '2026-07-15', '10:45-11:00', 4, 4, 'BOOKED',      'PATIENT'),
(5, 1, '2026-07-14', '09:00-09:15', 1, 1, 'COMPLETED',   'PATIENT'),
(6, 3, '2026-07-14', '09:15-09:30', 2, 2, 'COMPLETED',   'PATIENT'),
(7, 1, '2026-07-16', '11:00-11:15', 1, 1, 'BOOKED',      'RECEPTIONIST'),
(8, 2, '2026-07-16', '11:15-11:30', 2, 2, 'BOOKED',      'PATIENT');

INSERT IGNORE INTO blood_donors (full_name, blood_group, mobile, age, address, donation_count, is_eligible, registered_at) VALUES
('Arjun Kumar',      'O_NEGATIVE',  '9876500101', 28, 'Chennai, Tamil Nadu',    3, 1, NOW()),
('Priya Nair',       'B_POSITIVE',  '9876500102', 24, 'Kochi, Kerala',          1, 1, NOW()),
('Ramesh Singh',     'A_POSITIVE',  '9876500103', 35, 'Delhi, Delhi',           5, 1, NOW()),
('Sunita Devi',      'O_POSITIVE',  '9876500104', 29, 'Mumbai, Maharashtra',    2, 1, NOW()),
('Karthik Raja',     'AB_POSITIVE', '9876500105', 32, 'Coimbatore, Tamil Nadu', 4, 1, NOW()),
('Meena Krishnan',   'B_NEGATIVE',  '9876500106', 26, 'Bangalore, Karnataka',   1, 1, NOW());

INSERT IGNORE INTO blood_requests (blood_group, units_required, units_issued, priority, status, purpose, requested_at) VALUES
('O_NEGATIVE', 2, 0, 'EMERGENCY', 'PENDING', 'Trauma surgery - road accident victim',  NOW()),
('B_POSITIVE', 1, 0, 'URGENT',    'PENDING', 'Pre-surgery transfusion - scheduled',    NOW()),
('A_POSITIVE', 3, 3, 'NORMAL',    'ISSUED',  'Anemia treatment - completed',           DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT IGNORE INTO alerts (title, message, alert_type, severity, is_active, created_at) VALUES
('Blood Shortage — B- and AB-', 'Critical shortage of B Negative (6 units) and AB Negative (4 units). Urgent donor registration required. Contact Blood Bank immediately.', 'BLOOD_SHORTAGE', 'HIGH', 1, NOW()),
('Seasonal Flu Alert — OPD Load Increased', 'OPD patient load has increased by 40% due to seasonal flu outbreak. All doctors on standby. Additional staff deployed.', 'EPIDEMIC', 'MEDIUM', 1, NOW()),
('ICU Bed ICU-003 Under Maintenance', 'ICU Bed ICU-003 is under maintenance for electrical repairs. Expected to be available in 2 days. Use HDU beds for overflow.', 'GENERAL', 'LOW', 1, NOW());

-- ============================================================
-- PHASE 5: VERIFICATION
-- ============================================================
SELECT 'PATIENTS'        AS module, COUNT(*) AS count FROM patients
UNION ALL
SELECT 'MEDICAL_HISTORY', COUNT(*) FROM medical_history
UNION ALL
SELECT 'PRESCRIPTIONS',   COUNT(*) FROM prescriptions
UNION ALL
SELECT 'LAB_REPORTS',     COUNT(*) FROM lab_reports
UNION ALL
SELECT 'APPOINTMENTS',    COUNT(*) FROM appointments
UNION ALL
SELECT 'BEDS',            COUNT(*) FROM beds
UNION ALL
SELECT 'BLOOD_INVENTORY', COUNT(*) FROM blood_inventory
UNION ALL
SELECT 'BLOOD_REQUESTS',  COUNT(*) FROM blood_requests
UNION ALL
SELECT 'BLOOD_DONORS',    COUNT(*) FROM blood_donors
UNION ALL
SELECT 'ALERTS',          COUNT(*) FROM alerts
UNION ALL
SELECT 'AMBULANCES',      COUNT(*) FROM ambulances;