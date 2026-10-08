package medidoc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "patient_sessions")
public class PatientSession {

    @Id
    private String id;

    @Field("patient_unique_number")
    private String patientUniqueNumber;

    private String otp;

    @Field("otp_expiry")
    private LocalDateTime otpExpiry;

    @Field("is_logged_in")
    private Boolean isLoggedIn = false;

    @Field("last_login")
    private LocalDateTime lastLogin;

    @Field("jwt_token")
    private String jwtToken;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientUniqueNumber() { return patientUniqueNumber; }
    public void setPatientUniqueNumber(String patientUniqueNumber) { this.patientUniqueNumber = patientUniqueNumber; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public LocalDateTime getOtpExpiry() { return otpExpiry; }
    public void setOtpExpiry(LocalDateTime otpExpiry) { this.otpExpiry = otpExpiry; }

    public Boolean getIsLoggedIn() { return isLoggedIn; }
    public void setIsLoggedIn(Boolean isLoggedIn) { this.isLoggedIn = isLoggedIn; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public String getJwtToken() { return jwtToken; }
    public void setJwtToken(String jwtToken) { this.jwtToken = jwtToken; }
}