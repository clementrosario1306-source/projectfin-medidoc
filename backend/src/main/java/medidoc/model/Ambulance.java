package medidoc.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "ambulances")
public class Ambulance {

    @Id
    private String id;

    @Field("vehicle_number")
    private String vehicleNumber;

    @Field("driver_name")
    private String driverName;

    @Field("driver_mobile")
    private String driverMobile;

    @Field("ambulance_type")
    private AmbulanceType ambulanceType = AmbulanceType.BASIC;

    private Status status = Status.AVAILABLE;

    @Field("current_location")
    private String currentLocation;

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum AmbulanceType { BASIC, ADVANCED, NEONATAL, MORTUARY }
    public enum Status { AVAILABLE, ON_DUTY, MAINTENANCE }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverMobile() { return driverMobile; }
    public void setDriverMobile(String driverMobile) { this.driverMobile = driverMobile; }

    public AmbulanceType getAmbulanceType() { return ambulanceType; }
    public void setAmbulanceType(AmbulanceType ambulanceType) { this.ambulanceType = ambulanceType; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
