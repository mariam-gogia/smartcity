package cscie97.smartcity.model;

/**
 * A class to model a vehicles in the city
 * @author Mariam Gogia
 */
public class Vehicle extends IoT {
    // class properties
    private int capacity;
    private int fee;
    private String type;
    private String activity;

    public Vehicle (String deviceID, Location location, boolean enabled,
                        boolean status, String type, String activity, int capacity, int fee) {
        this.setDeviceID(deviceID);
        this.setLocation(location);
        this.setEnabled(enabled);
        this.setStatus(status);
        this.type = type;
        this.fee = fee;
        this.activity = activity;
        this.capacity = capacity;
    }
    // empty constructor
    public Vehicle () {}

    // Getters & Setters
    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getFee() {
        return fee;
    }
    public void setFee(int fee) {
        this.fee = fee;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getActivity() {
        return activity;
    }
    public void setActivity(String activity) {
        this.activity = activity;
    }

    /**
     * @return details of the vehicle as a string
     */
    public String toString() {
        return "\nDevice ID: " + this.getDeviceID() + "\nType: " + type + "\nLocation: " + this.getLocation().toString() + "\nEnabled: " + this.isEnabled() +
                "\nStatus On: " + this.isStatus() + "\nActivity: " + activity+  "\nLatest event: " + this.getLatestEvent() + "\nMax Capacity: " + capacity +
                "\nFee: " + fee;
    }
}
