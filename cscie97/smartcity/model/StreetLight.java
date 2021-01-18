package cscie97.smartcity.model;

/**
 * A class to model a street lights in the city
 * @author Mariam Gogia
 */
public class StreetLight extends IoT {
    // class properties
    private int brightnessLevel;

    public StreetLight (String deviceID, Location location, boolean enabled,
                        boolean status, int brightnessLevel) {
        this.setDeviceID(deviceID);
        this.setLocation(location);
        this.setEnabled(enabled);
        this.setStatus(status);
        this.brightnessLevel = brightnessLevel;
    }
    // empty constructor
    public StreetLight(){}

    // Getters & Setters
    public int getBrightnessLevel() {
        return brightnessLevel;
    }
    public void setBrightnessLevel(int brightnessLevel) {
        this.brightnessLevel = brightnessLevel;
    }

    /**
     * @return details of the street light object as a string
     */
    public String toString() {
        return "\nDevice ID: " + this.getDeviceID() + "\nLocation: " + this.getLocation().toString() + "\nEnabled: " + this.isEnabled() +
                "\nStatus On: " + this.isStatus() + "\nLatest event: " + this.getLatestEvent() + "\nBrightness level: " + brightnessLevel;
    }

}
