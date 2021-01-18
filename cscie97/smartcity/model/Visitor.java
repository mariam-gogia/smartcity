package cscie97.smartcity.model;

/**
 * A class to model a Visitor type of Person in the system
 * @author Mariam Gogia
 */
public class Visitor extends Person {
    // constructor
    public Visitor(String ID, String biometricID, Location location) {
        this.setID(ID);
        this.setBiometricID(biometricID);
        this.setLocation(location);
    }
    /**
     * @return details of a visitor as a string
     */
    public String toString () {
        return "\nID: " + this.getID() + "\nBiometrics: " + this.getBiometricID() + "\nLocation: " + this.getLocation().toString() + "\n";

    }
}
