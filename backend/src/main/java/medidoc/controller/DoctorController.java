package medidoc.controller;

import medidoc.dto.ApiResponse;
import medidoc.dto.DoctorDTO;
import medidoc.model.Doctor;
import medidoc.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    // -----------------------------------------------
    // GET /api/doctors
    // Get all doctors
    // -----------------------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(
            ApiResponse.success("Doctors fetched successfully", doctors));
    }

    // -----------------------------------------------
    // GET /api/doctors/active
    // Get only active doctors
    // -----------------------------------------------
    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getActiveDoctors() {
        List<Doctor> doctors = doctorService.getActiveDoctors();
        return ResponseEntity.ok(
            ApiResponse.success("Active doctors fetched", doctors));
    }

    // -----------------------------------------------
    // GET /api/doctors/{doctorId}
    // Get doctor by doctor ID
    // -----------------------------------------------
    @GetMapping("/{doctorId}")
    public ResponseEntity<ApiResponse> getDoctorById(
            @PathVariable String doctorId) {

        Optional<Doctor> doctor = doctorService.getDoctorByDoctorId(doctorId);

        if (doctor.isPresent()) {
            return ResponseEntity.ok(
                ApiResponse.success("Doctor found", doctor.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("Doctor not found: " + doctorId));
    }

    // -----------------------------------------------
    // GET /api/doctors/department/{department}
    // Get doctors by department
    // -----------------------------------------------
    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse> getDoctorsByDepartment(
            @PathVariable String department) {

        List<Doctor> doctors = doctorService.getDoctorsByDepartment(department);
        return ResponseEntity.ok(
            ApiResponse.success("Doctors in " + department, doctors));
    }

    // -----------------------------------------------
    // POST /api/doctors
    // Add new doctor - Admin only
    // -----------------------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse> addDoctor(
            @RequestBody DoctorDTO dto) {
        try {
            Doctor doctor = doctorService.addDoctor(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    "Doctor added successfully: " + doctor.getName(), doctor));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // -----------------------------------------------
    // PUT /api/doctors/{doctorId}
    // Update doctor - Admin only
    // -----------------------------------------------
    @PutMapping("/{doctorId}")
    public ResponseEntity<ApiResponse> updateDoctor(
            @PathVariable String doctorId,
            @RequestBody DoctorDTO dto) {
        try {
            Doctor doctor = doctorService.updateDoctor(doctorId, dto);
            return ResponseEntity.ok(
                ApiResponse.success("Doctor updated successfully", doctor));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // -----------------------------------------------
    // DELETE /api/doctors/{doctorId}
    // Delete doctor - Admin only
    // -----------------------------------------------
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<ApiResponse> deleteDoctor(
            @PathVariable String doctorId) {
        try {
            doctorService.deleteDoctor(doctorId);
            return ResponseEntity.ok(
                ApiResponse.success("Doctor deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // -----------------------------------------------
    // PUT /api/doctors/{doctorId}/schedule
    // Update doctor schedule
    // -----------------------------------------------
    @PutMapping("/{doctorId}/schedule")
    public ResponseEntity<ApiResponse> updateSchedule(
            @PathVariable String doctorId,
            @RequestBody String schedule) {
        try {
            Doctor doctor = doctorService.updateSchedule(doctorId, schedule);
            return ResponseEntity.ok(
                ApiResponse.success("Schedule updated", doctor));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}
