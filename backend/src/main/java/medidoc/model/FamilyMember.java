package medidoc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "family_members")
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "primary_patient_id", nullable = false)
    private Long primaryPatientId;

    @Column(name = "member_patient_id", nullable = false)
    private Long memberPatientId;

    @Column(name = "relationship")
    private String relationship;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPrimaryPatientId() { return primaryPatientId; }
    public void setPrimaryPatientId(Long primaryPatientId) { this.primaryPatientId = primaryPatientId; }

    public Long getMemberPatientId() { return memberPatientId; }
    public void setMemberPatientId(Long memberPatientId) { this.memberPatientId = memberPatientId; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
