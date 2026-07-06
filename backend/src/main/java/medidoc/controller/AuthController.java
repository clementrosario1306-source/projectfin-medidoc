package medidoc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medidoc.dto.AuthResponse;
import medidoc.dto.OtpRequest;
import medidoc.dto.PatientLoginRequest;
import medidoc.dto.StaffLoginRequest;
import medidoc.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /api/auth/send-otp
    @PostMapping("/send-otp")
    public ResponseEntity<AuthResponse> sendOtp(
            @RequestBody OtpRequest request) {
        AuthResponse response = authService.sendOtp(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // POST /api/auth/patient-login
    @PostMapping("/patient-login")
    public ResponseEntity<AuthResponse> patientLogin(
            @RequestBody PatientLoginRequest request) {
        AuthResponse response = authService.patientLogin(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // POST /api/auth/staff-login
    @PostMapping("/staff-login")
    public ResponseEntity<AuthResponse> staffLogin(
            @RequestBody StaffLoginRequest request) {
        AuthResponse response = authService.staffLogin(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    // GET /api/auth/test
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth Controller is working!");
    }
}