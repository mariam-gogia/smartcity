package cscie97.smartcity.model;

/**
 * IoT provides an interface for various types of different IoT devices in the system
 * This interface contains all properties that they have in common
 * @ Mariam Gogia
 */
public abstract class IoT {
    // class properties
    private String deviceID;
    private Location location;
    private boolean enabled = true;
    private boolean status = true;
    private Event latestEvent;
    private String account;

    // Each type of IoT device contains the following sensors
    public enum SensorType {
        camera,
        microphone,
        thermometer,
        co2meter,
        speaker
    }

    // Setters & Getters
    public String getDeviceID() {
        return deviceID;
    }
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }

    public Event getLatestEvent() {
        return latestEvent;
    }
    public void setLatestEvent(Event latestEvent) {
        this.latestEvent = latestEvent;
    }

    public String getAccount() { return account; }

    public void setAccount(String account) {
        this.account = account;
    }
}
