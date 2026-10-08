package medidoc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "staff")
public class Staff {

    @Id
    private String id;

    @Field("employee_id")
    private String employeeId;

    @Field("full_name")
    private String fullName;

    private Role role;

    private String mobile;

    private String email;

    private String password;

    private Status status = Status.ACTIVE;

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Role Enum
    public enum Role {
        RECEPTIONIST, BLOOD_BANK_OFFICER, ADMIN
    }

    // Status Enum
    public enum Status {
        ACTIVE, INACTIVE
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}