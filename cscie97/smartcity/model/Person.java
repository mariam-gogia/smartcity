package cscie97.smartcity.model;

/** A Person is an interface for different types of people in the city e.g. residetns & visitors
 * It contains all properties that people in the city must have
 * @author Mariam Gogia
 */
public abstract class Person {
    // class properties
    private String ID;
    private String biometricID;
    private Location location;

    // Getters & Setters
    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getBiometricID() {
        return biometricID;
    }
    public void setBiometricID(String biometricID) {
        this.biometricID = biometricID;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

}
