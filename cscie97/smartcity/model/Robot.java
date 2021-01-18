package cscie97.smartcity.model;

/**
 * A class to model robots in the city
 * @author Mariam Gogia
 */
public class Robot extends IoT {
    // class properties
    private String activity;

    public Robot (String deviceID, Location location, boolean enabled,
                        boolean status,  String activity) {
        this.setDeviceID(deviceID);
        this.setLocation(location);
        this.setEnabled(enabled);
        this.setStatus(status);
        this.setActivity(activity);
    }
    // empty constructor
    public Robot () {}

    // Getters & Setters
    public String getActivity() {
        return activity;
    }
    public void setActivity(String activity) {
        this.activity = activity;
    }

    /**
     * @return details of the robot object as a string
     */
    public String toString() {
        return "\nDevice ID: " + this.getDeviceID() + "\nLocation: " + this.getLocation().toString() + "\nEnabled: " + this.isEnabled() +
                "\nStatus On: " + this.isStatus() + "\nLatest event: " + this.getLatestEvent() + "\nActivity: " + activity;
    }
}
