package medidoc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import medidoc.dto.ApiResponse;
import medidoc.dto.AppointmentRequest;
import medidoc.model.Appointment;
import medidoc.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // POST /api/appointments/book
    @PostMapping("/book")
    public ResponseEntity<ApiResponse> bookAppointment(
            @RequestBody AppointmentRequest request) {
        try {
            Appointment appt =
                appointmentService.bookAppointment(request);
            return ResponseEntity.ok(ApiResponse.success(
                "Appointment booked! Your token number is: "
                + appt.getTokenNumber(), appt));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/appointments/queue/{doctorId}
    @GetMapping("/queue/{doctorId}")
    public ResponseEntity<ApiResponse> getTodayQueue(
            @PathVariable String doctorId) {
        try {
            List<Appointment> queue =
                appointmentService.getTodayQueue(doctorId);
            return ResponseEntity.ok(ApiResponse.success(
                "Today's queue for doctor " + doctorId
                + " - " + queue.size() + " patients", queue));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/appointments/patient/{uniqueId}
    @GetMapping("/patient/{uniqueId}")
    public ResponseEntity<ApiResponse> getPatientAppointments(
            @PathVariable String uniqueId) {
        try {
            List<Appointment> appointments =
                appointmentService.getPatientAppointments(uniqueId);
            return ResponseEntity.ok(ApiResponse.success(
                "Appointments found: " + appointments.size(),
                appointments));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/appointments/upcoming/{uniqueId}
    @GetMapping("/upcoming/{uniqueId}")
    public ResponseEntity<ApiResponse> getUpcomingAppointments(
            @PathVariable String uniqueId) {
        try {
            List<Appointment> appointments =
                appointmentService.getUpcomingAppointments(uniqueId);
            return ResponseEntity.ok(ApiResponse.success(
                "Upcoming appointments: " + appointments.size(),
                appointments));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }

    // PUT /api/appointments/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            Appointment appt =
                appointmentService.updateStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success(
                "Status updated to " + status, appt));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        }
    }
}