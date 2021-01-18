package cscie97.smartcity.model;

/**
 * A class to model a street lights in the city
 * @author Mariam Gogia
 */
public class StreetSign extends IoT {
    // class properties
    private String signMessage;

    public StreetSign (String deviceID, Location location, boolean enabled,
                        boolean status, String signMessage) {
        this.setDeviceID(deviceID);
        this.setLocation(location);
        this.setEnabled(enabled);
        this.setStatus(status);
        this.signMessage = signMessage;
    }
    // empty constructor
    public StreetSign() {}

    // Getters & Setters
    public String getSignMessage() {
        return signMessage;
    }
    public void setSignMessage(String signMessage) {
        this.signMessage = signMessage;
    }

    /**
     * @return details of the street light object as a string
     */
    public String toString() {
        return "\nDevice ID: " + this.getDeviceID() + "\nLocation: " + this.getLocation().toString() + "\nEnabled: " + this.isEnabled() +
                "\nStatus On: " + this.isStatus() + "\nLatest event: " + this.getLatestEvent() + "\nSign Message: " + signMessage;
    }
}
