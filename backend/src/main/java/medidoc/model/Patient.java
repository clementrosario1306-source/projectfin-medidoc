package medidoc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "patients")
public class Patient {

    @Id
    private String id;

    @Field("unique_id")
    private String uniqueId;

    @Field("full_name")
    private String fullName;

    private Integer age;

    private Gender gender;

    @Field("blood_group")
    private BloodGroup bloodGroup;

    @Field("mobile_number")
    private String mobileNumber;

    private String address;

    @Field("emergency_contact_name")
    private String emergencyContactName;

    @Field("emergency_contact_phone")
    private String emergencyContactPhone;

    @Field("abha_number")
    private String abhaNumber;

    @Field("pmjay_id")
    private String pmjayId;

    @Field("is_pmjay_eligible")
    private Boolean isPmjayEligible = false;

    @Field("registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // -----------------------------------------------
    // Gender Enum
    // -----------------------------------------------
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    // -----------------------------------------------
    // Blood Group Enum - matches database ENUM values
    // -----------------------------------------------
    public enum BloodGroup {
        A_POSITIVE,
        A_NEGATIVE,
        B_POSITIVE,
        B_NEGATIVE,
        O_POSITIVE,
        O_NEGATIVE,
        AB_POSITIVE,
        AB_NEGATIVE
    }

    // -----------------------------------------------
    // Getters and Setters
    // -----------------------------------------------
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUniqueId() { return uniqueId; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public String getAbhaNumber() { return abhaNumber; }
    public void setAbhaNumber(String abhaNumber) { this.abhaNumber = abhaNumber; }

    public String getPmjayId() { return pmjayId; }
    public void setPmjayId(String pmjayId) { this.pmjayId = pmjayId; }

    public Boolean getIsPmjayEligible() { return isPmjayEligible; }
    public void setIsPmjayEligible(Boolean isPmjayEligible) { this.isPmjayEligible = isPmjayEligible; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}