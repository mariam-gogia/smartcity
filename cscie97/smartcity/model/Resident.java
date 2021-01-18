package cscie97.smartcity.model;

/** A class to model a Resident type of Person in the system
 * @author Mariam Gogia
 */
public class Resident extends Person {
    // class properties
    private String name;
    private String phoneNum;
    private String role;
    private String account;

    public Resident(String ID, String name, String biometricID, String phoneNum,
                      String role, Location location, String account) {
        this.setID(ID);
        this.setBiometricID(biometricID);
        this.setLocation(location);
        this.name = name;
        this.phoneNum = phoneNum;
        this.role = role;
        this.account = account;
    }
    // empty constructor
    public Resident() {}

    // Getters & Setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return details of the resident object as a string
     */
    public String toString () {
        return "\nID: " + this.getID() + "\nBiometrics: " + this.getBiometricID() + "\nLocation: " + this.getLocation().toString() +
                "\nName: " + name + "\nPhone Number: " + phoneNum + "\nRole: " + role + "\nAccount: " + account;
    }
}
