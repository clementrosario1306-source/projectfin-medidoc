package medidoc.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "blood_donors")
public class BloodDonor {

    @Id
    private String id;

    @Field("full_name")
    private String fullName;

    @Field("blood_group")
    private BloodInventory.BloodGroup bloodGroup;

    private String mobile;

    private Integer age;

    private String address;

    @Field("last_donation")
    private LocalDate lastDonation;

    @Field("donation_count")
    private Integer donationCount = 0;

    @Field("is_eligible")
    private Boolean isEligible = true;

    @Field("registered_at")
    private LocalDateTime registeredAt = LocalDateTime.now();

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public BloodInventory.BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(BloodInventory.BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getLastDonation() { return lastDonation; }
    public void setLastDonation(LocalDate lastDonation) { this.lastDonation = lastDonation; }

    public Integer getDonationCount() { return donationCount; }
    public void setDonationCount(Integer donationCount) { this.donationCount = donationCount; }

    public Boolean getIsEligible() { return isEligible; }
    public void setIsEligible(Boolean isEligible) { this.isEligible = isEligible; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
}
