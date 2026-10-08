package medidoc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "family_members")
public class FamilyMember {

    @Id
    private String id;

    @Field("primary_patient_id")
    private String primaryPatientId;

    @Field("member_patient_id")
    private String memberPatientId;

    private String relationship;

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPrimaryPatientId() { return primaryPatientId; }
    public void setPrimaryPatientId(String primaryPatientId) { this.primaryPatientId = primaryPatientId; }

    public String getMemberPatientId() { return memberPatientId; }
    public void setMemberPatientId(String memberPatientId) { this.memberPatientId = memberPatientId; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
