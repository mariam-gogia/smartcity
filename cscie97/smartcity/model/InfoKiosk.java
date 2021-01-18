package cscie97.smartcity.model;

/**
 * A class to model info kiosks in the city
 * @author Mariam Gogia
 */
public class InfoKiosk extends IoT {
    // class properties
    private String displayImage;
    private String purchaseDetails;

    // a constructor
    public InfoKiosk (String deviceID, Location location, boolean enabled,
                        boolean status,  String displayImage) {
        this.setDeviceID(deviceID);
        this.setLocation(location);
        this.setEnabled(enabled);
        this.setStatus(status);
        this.displayImage = displayImage;

    }
    // an empty constructor
    public InfoKiosk () {}

    // Setters & Getters
    public String getDisplayImage() {
        return displayImage;
    }
    public void setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
    }

    // to be implemented once Controller module is added
    public String getPurchaseDetails() { return purchaseDetails; }
    public void setPurchaseDetails(String purchaseDetails) { this.purchaseDetails = purchaseDetails; }

    /**
     * @return string containing the details of info kiosk object
     */
    public String toString() {
        return "\nDevice ID: " + this.getDeviceID() + "\nLocation: " + this.getLocation().toString() + "\nEnabled: " + this.isEnabled() +
                "\nStatus On: " + this.isStatus() + "\nLatest event: " + this.getLatestEvent() + "\nImage URL: " + displayImage;
    }
}
