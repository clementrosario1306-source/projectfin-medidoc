package medidoc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "blood_inventory")
public class BloodInventory {

    @Id
    private String id;

    @Field("blood_group")
    private BloodGroup bloodGroup;

    @Field("units_available")
    private Integer unitsAvailable = 0;

    @Field("units_reserved")
    private Integer unitsReserved = 0;

    @Field("last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

    public enum BloodGroup {
        A_POSITIVE, A_NEGATIVE,
        B_POSITIVE, B_NEGATIVE,
        O_POSITIVE, O_NEGATIVE,
        AB_POSITIVE, AB_NEGATIVE
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public BloodGroup getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(BloodGroup bloodGroup) { this.bloodGroup = bloodGroup; }

    public Integer getUnitsAvailable() { return unitsAvailable; }
    public void setUnitsAvailable(Integer unitsAvailable) { this.unitsAvailable = unitsAvailable; }

    public Integer getUnitsReserved() { return unitsReserved; }
    public void setUnitsReserved(Integer unitsReserved) { this.unitsReserved = unitsReserved; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
