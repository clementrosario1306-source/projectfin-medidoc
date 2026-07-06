package medidoc.dto;

import medidoc.model.Patient;

public class PatientRegistrationRequest {

    private String fullName;
    private Integer age;
    private Patient.Gender gender;
    private Patient.BloodGroup bloodGroup;
    private String mobileNumber;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String abhaNumber;
    private String pmjayId;
    private Boolean isPmjayEligible = false;

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Patient.Gender getGender() { return gender; }
    public void setGender(Patient.Gender gender) { this.gender = gender; }

    public Patient.BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(Patient.BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }

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
}