package cscie97.smartcity.model;

/** A class to model Parking Space objects in the city
 * @author Mariam Gogia
 */
public class ParkingSpace extends IoT {
    // class properties
    private boolean isAvailable= true;
    private int hourlyRate;

    public ParkingSpace (String deviceID, Location location, boolean enabled,
                        boolean status, boolean isAvailable, int hourlyRate) {
        this.setDeviceID(deviceID);
        this.setLocation(location);
        this.setEnabled(enabled);
        this.setStatus(status);
        this.isAvailable = isAvailable;
        this.hourlyRate = hourlyRate;

    }
    // empty constructor
    public ParkingSpace() {}

    // Getters & Setters
    public boolean isAvailable() {
        return isAvailable;
    }
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getHourlyRate() {
        return hourlyRate;
    }
    public void setHourlyRate(int hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    /**
     * @return details of the parking space object as a string
     */
    public String toString() {
        return "\nDevice ID: " + this.getDeviceID() + "\nLocation: " + this.getLocation().toString() + "\nEnabled: " + this.isEnabled() +
                "\nStatus On: " + this.isStatus() + "\nLatest event: " + this.getLatestEvent() + "\nIs available: " + isAvailable +
                "\nRate: " + hourlyRate;
    }
}
