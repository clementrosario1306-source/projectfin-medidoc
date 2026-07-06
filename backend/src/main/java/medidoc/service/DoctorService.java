package medidoc.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medidoc.dto.DoctorDTO;
import medidoc.model.Doctor;
import medidoc.repository.DoctorRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    // -----------------------------------------------
    // GET ALL DOCTORS
    // -----------------------------------------------
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // -----------------------------------------------
    // GET DOCTOR BY DOCTOR ID
    // -----------------------------------------------
    public Optional<Doctor> getDoctorByDoctorId(String doctorId) {
        return doctorRepository.findByDoctorId(doctorId);
    }

    // -----------------------------------------------
    // GET DOCTORS BY DEPARTMENT
    // -----------------------------------------------
    public List<Doctor> getDoctorsByDepartment(String department) {
        return doctorRepository.findByDepartment(department);
    }

    // -----------------------------------------------
    // GET ACTIVE DOCTORS
    // -----------------------------------------------
    public List<Doctor> getActiveDoctors() {
        return doctorRepository.findByStatus(Doctor.Status.ACTIVE);
    }

    // -----------------------------------------------
    // ADD NEW DOCTOR (Admin only)
    // -----------------------------------------------
    public Doctor addDoctor(DoctorDTO dto) {

        // Check if doctor ID already exists
        if (doctorRepository.existsByDoctorId(dto.getDoctorId())) {
            throw new RuntimeException(
                "Doctor ID already exists: " + dto.getDoctorId());
        }

        Doctor doctor = new Doctor();
        doctor.setDoctorId(dto.getDoctorId());
        doctor.setName(dto.getName());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setDepartment(dto.getDepartment());
        doctor.setMobile(dto.getMobile());
        doctor.setEmail(dto.getEmail());
        doctor.setQualification(dto.getQualification());
        doctor.setExperienceYears(dto.getExperienceYears());
        doctor.setSchedule(dto.getSchedule());
        doctor.setEmployeePassword(dto.getEmployeePassword());

        if (dto.getStatus() != null) {
            doctor.setStatus(Doctor.Status.valueOf(dto.getStatus()));
        } else {
            doctor.setStatus(Doctor.Status.ACTIVE);
        }

        Doctor saved = doctorRepository.save(doctor);
        System.out.println("New doctor added: " + saved.getName());
        return saved;
    }

    // -----------------------------------------------
    // UPDATE DOCTOR (Admin only)
    // -----------------------------------------------
    public Doctor updateDoctor(String doctorId, DoctorDTO dto) {

        Optional<Doctor> doctorOpt = doctorRepository.findByDoctorId(doctorId);

        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found: " + doctorId);
        }

        Doctor doctor = doctorOpt.get();

        if (dto.getName() != null) doctor.setName(dto.getName());
        if (dto.getSpecialization() != null) doctor.setSpecialization(dto.getSpecialization());
        if (dto.getDepartment() != null) doctor.setDepartment(dto.getDepartment());
        if (dto.getMobile() != null) doctor.setMobile(dto.getMobile());
        if (dto.getEmail() != null) doctor.setEmail(dto.getEmail());
        if (dto.getQualification() != null) doctor.setQualification(dto.getQualification());
        if (dto.getExperienceYears() != null) doctor.setExperienceYears(dto.getExperienceYears());
        if (dto.getSchedule() != null) doctor.setSchedule(dto.getSchedule());
        if (dto.getStatus() != null) doctor.setStatus(Doctor.Status.valueOf(dto.getStatus()));
        if (dto.getEmployeePassword() != null) doctor.setEmployeePassword(dto.getEmployeePassword());

        Doctor updated = doctorRepository.save(doctor);
        System.out.println("Doctor updated: " + updated.getName());
        return updated;
    }

    // -----------------------------------------------
    // DELETE DOCTOR (Admin only)
    // -----------------------------------------------
    public void deleteDoctor(String doctorId) {

        Optional<Doctor> doctorOpt = doctorRepository.findByDoctorId(doctorId);

        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found: " + doctorId);
        }

        doctorRepository.delete(doctorOpt.get());
        System.out.println("Doctor deleted: " + doctorId);
    }

    // -----------------------------------------------
    // UPDATE DOCTOR SCHEDULE
    // -----------------------------------------------
    public Doctor updateSchedule(String doctorId, String schedule) {

        Optional<Doctor> doctorOpt = doctorRepository.findByDoctorId(doctorId);

        if (doctorOpt.isEmpty()) {
            throw new RuntimeException("Doctor not found: " + doctorId);
        }

        Doctor doctor = doctorOpt.get();
        doctor.setSchedule(schedule);
        return doctorRepository.save(doctor);
    }
}