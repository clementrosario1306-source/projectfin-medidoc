-- ============================================================
-- MEDIDOC - Smart e-OPD, AI Health Record & Blood Bank System
-- DATABASE SCHEMA
-- Run this ENTIRE file in MySQL Workbench
-- ============================================================

CREATE DATABASE IF NOT EXISTS medidoc_db;
USE medidoc_db;

-- ============================================================
-- TABLE 1: patients
-- ============================================================
CREATE TABLE IF NOT EXISTS patients (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    unique_id               VARCHAR(25) NOT NULL UNIQUE,
    full_name               VARCHAR(100) NOT NULL,
    age                     INT NOT NULL,
    gender                  ENUM('MALE','FEMALE','OTHER') NOT NULL,
    blood_group             ENUM('A+','A-','B+','B-','O+','O-','AB+','AB-'),
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

-- ============================================================
-- TABLE 2: doctors
-- ============================================================
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

-- ============================================================
-- TABLE 3: staff
-- ============================================================
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

-- ============================================================
-- TABLE 4: medical_history
-- ============================================================
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

-- ============================================================
-- TABLE 5: prescriptions
-- ============================================================
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

-- ============================================================
-- TABLE 6: medicines
-- ============================================================
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

-- ============================================================
-- TABLE 7: prescriptions_medicines
-- ============================================================
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

-- ============================================================
-- TABLE 8: appointments
-- ============================================================
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

-- ============================================================
-- TABLE 9: blood_inventory
-- ============================================================
CREATE TABLE IF NOT EXISTS blood_inventory (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    blood_group     ENUM('A+','A-','B+','B-','O+','O-','AB+','AB-') NOT NULL UNIQUE,
    units_available INT DEFAULT 0,
    units_reserved  INT DEFAULT 0,
    last_updated    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE 10: blood_requests
-- ============================================================
CREATE TABLE IF NOT EXISTS blood_requests (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id      BIGINT,
    requested_by    BIGINT,
    blood_group     ENUM('A+','A-','B+','B-','O+','O-','AB+','AB-') NOT NULL,
    units_required  INT NOT NULL,
    units_issued    INT DEFAULT 0,
    priority        ENUM('NORMAL','URGENT','EMERGENCY') DEFAULT 'NORMAL',
    status          ENUM('PENDING','APPROVED','ISSUED','REJECTED','CANCELLED') DEFAULT 'PENDING',
    purpose         TEXT,
    requested_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    issued_at       DATETIME,
    FOREIGN KEY (patient_id) REFERENCES patients(id)
);

-- ============================================================
-- TABLE 11: blood_donors
-- ============================================================
CREATE TABLE IF NOT EXISTS blood_donors (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(100) NOT NULL,
    blood_group     ENUM('A+','A-','B+','B-','O+','O-','AB+','AB-') NOT NULL,
    mobile          VARCHAR(15) NOT NULL,
    age             INT,
    address         TEXT,
    last_donation   DATE,
    donation_count  INT DEFAULT 0,
    is_eligible     BOOLEAN DEFAULT TRUE,
    registered_at   DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE 12: beds
-- ============================================================
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

-- ============================================================
-- TABLE 13: ambulances
-- ============================================================
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

-- ============================================================
-- TABLE 14: ambulance_requests
-- ============================================================
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

-- ============================================================
-- TABLE 15: doctor_performance
-- ============================================================
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

-- ============================================================
-- TABLE 16: referrals
-- ============================================================
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

-- ============================================================
-- TABLE 17: lab_reports
-- ============================================================
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

-- ============================================================
-- TABLE 18: family_members
-- ============================================================
CREATE TABLE IF NOT EXISTS family_members (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    primary_patient_id BIGINT NOT NULL,
    member_patient_id  BIGINT NOT NULL,
    relationship       VARCHAR(50),
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (primary_patient_id) REFERENCES patients(id),
    FOREIGN KEY (member_patient_id)  REFERENCES patients(id)
);

-- ============================================================
-- TABLE 19: alerts
-- ============================================================
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

-- ============================================================
-- TABLE 20: alert_responses
-- ============================================================
CREATE TABLE IF NOT EXISTS alert_responses (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_id     BIGINT NOT NULL,
    responded_by BIGINT NOT NULL,
    response_text TEXT,
    responded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (alert_id) REFERENCES alerts(id)
);

-- ============================================================
-- TABLE 21: audit_logs
-- ============================================================
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

-- ============================================================
-- TABLE 22: patient_sessions
-- ============================================================
CREATE TABLE IF NOT EXISTS patient_sessions (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_unique_number VARCHAR(25) NOT NULL,
    otp                   VARCHAR(10),
    otp_expiry            DATETIME,
    is_logged_in          BOOLEAN DEFAULT FALSE,
    last_login            DATETIME,
    jwt_token             TEXT
);

-- ============================================================
-- TABLE 23: staff_sessions
-- ============================================================
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
-- DEMO DATA - Insert after all tables created
-- ============================================================

-- Blood Inventory starting stock
INSERT INTO blood_inventory (blood_group, units_available, units_reserved) VALUES
('A+',  45, 5),
('A-',  12, 2),
('B+',  38, 4),
('B-',   8, 1),
('O+',  60, 8),
('O-',  15, 3),
('AB+', 20, 2),
('AB-',  6, 0);

-- Demo Staff
INSERT INTO staff (employee_id, full_name, role, mobile, email, password) VALUES
('REC001',   'Priya Sharma',  'RECEPTIONIST',       '9876543211', 'priya@medidoc.in',  'rec123'),
('BBO001',   'Raju Verma',    'BLOOD_BANK_OFFICER',  '9876543212', 'raju@medidoc.in',   'blood123'),
('ADMIN001', 'Suresh Kumar',  'ADMIN',               '9876543213', 'suresh@medidoc.in', 'admin123');

-- Demo Doctors
INSERT INTO doctors (doctor_id, name, specialization, department, mobile, email, qualification, experience_years, schedule, employee_password) VALUES
('DOC001', 'Dr. Rajesh Sharma', 'General Medicine', 'OPD',       '9876500001', 'rajesh@medidoc.in', 'MBBS, MD',        15, '{"mon":"9-13","tue":"9-13","wed":"9-13","thu":"9-13","fri":"9-13"}', 'doctor123'),
('DOC002', 'Dr. Anita Patel',   'Gynecology',       'Maternity', '9876500002', 'anita@medidoc.in',  'MBBS, MS',        12, '{"mon":"10-14","wed":"10-14","fri":"10-14"}',                        'doctor123'),
('DOC003', 'Dr. Mohan Singh',   'Pediatrics',       'Pediatric', '9876500003', 'mohan@medidoc.in',  'MBBS, DCH',        8, '{"tue":"9-13","thu":"9-13","sat":"9-13"}',                          'doctor123'),
('DOC004', 'Dr. Kavitha Reddy', 'Orthopedics',      'Surgery',   '9876500004', 'kavitha@medidoc.in','MBBS, MS Ortho',  10, '{"mon":"14-18","wed":"14-18","fri":"14-18"}',                        'doctor123'),
('DOC005', 'Dr. Arjun Nair',    'Cardiology',       'Cardiology','9876500005', 'arjun@medidoc.in',  'MBBS, DM Cardio', 18, '{"mon":"9-13","thu":"9-13"}',                                       'doctor123');

-- Demo Beds
INSERT INTO beds (bed_number, ward, room_number, floor, bed_type, status) VALUES
('GEN-001','General Ward',  'G-101','Ground','GENERAL',     'AVAILABLE'),
('GEN-002','General Ward',  'G-101','Ground','GENERAL',     'AVAILABLE'),
('GEN-003','General Ward',  'G-102','Ground','GENERAL',     'AVAILABLE'),
('GEN-004','General Ward',  'G-102','Ground','GENERAL',     'AVAILABLE'),
('GEN-005','General Ward',  'G-103','Ground','GENERAL',     'AVAILABLE'),
('ICU-001','ICU',           'I-101','First', 'ICU',         'AVAILABLE'),
('ICU-002','ICU',           'I-101','First', 'ICU',         'AVAILABLE'),
('ICU-003','ICU',           'I-102','First', 'ICU',         'AVAILABLE'),
('MAT-001','Maternity Ward','M-101','Second','GENERAL',     'AVAILABLE'),
('MAT-002','Maternity Ward','M-101','Second','GENERAL',     'AVAILABLE'),
('MAT-003','Maternity Ward','M-102','Second','GENERAL',     'AVAILABLE'),
('PED-001','Pediatric Ward','P-101','Second','GENERAL',     'AVAILABLE'),
('PED-002','Pediatric Ward','P-101','Second','GENERAL',     'AVAILABLE'),
('PED-003','Pediatric Ward','P-102','Second','GENERAL',     'AVAILABLE'),
('SUR-001','Surgical Ward', 'S-101','Third', 'SEMI_PRIVATE','AVAILABLE'),
('SUR-002','Surgical Ward', 'S-101','Third', 'SEMI_PRIVATE','AVAILABLE'),
('SUR-003','Surgical Ward', 'S-102','Third', 'PRIVATE',     'AVAILABLE'),
('HDU-001','HDU',           'H-101','First', 'HDU',         'AVAILABLE'),
('HDU-002','HDU',           'H-101','First', 'HDU',         'AVAILABLE'),
('HDU-003','HDU',           'H-102','First', 'HDU',         'AVAILABLE');

-- Demo Medicines
INSERT INTO medicines (name, generic_name, category, unit, stock_quantity, min_stock_level, price_per_unit) VALUES
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

-- Demo Ambulances
INSERT INTO ambulances (vehicle_number, driver_name, driver_mobile, ambulance_type, status, current_location) VALUES
('TN-01-AM-1001','Ramesh Kumar', '9900001111','BASIC',    'AVAILABLE','Main Gate'),
('TN-01-AM-1002','Suresh Rao',   '9900002222','ADVANCED', 'AVAILABLE','Emergency Bay'),
('TN-01-AM-1003','Mahesh Singh', '9900003333','BASIC',    'ON_DUTY',  'City Center'),
('TN-01-AM-1004','Ganesh Patil', '9900004444','NEONATAL', 'AVAILABLE','Hospital Parking');

-- ============================================================
-- VERIFY - Check all tables created
-- ============================================================
SELECT TABLE_NAME, TABLE_ROWS
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'medidoc_db'
ORDER BY TABLE_NAME;