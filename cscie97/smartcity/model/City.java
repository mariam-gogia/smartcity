package cscie97.smartcity.model;

/**
 * City class is used to model a city instance in the system
 * @author Mariam Gogia
 */
public class City {
    // class properties
    private String cityID;
    private String name;
    private Location location;
    private String account;
    private int radius;

    // Class instance constructor
    public City(String cityID, String name, String account, Location location,  int radius) {
        this.cityID = cityID;
        this.name = name;
        this.location = location;
        this.account = account;
        this.radius = radius;
    }

    // Getters and Setters
    public String getCityID() {
        return cityID;
    }
    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }

    public int getRadius() {
        return radius;
    }
    public void setRadius(int radius) {
        this.radius = radius;
    }


    /**
     * toString participates in 'show city' command
     * @return City properties as string
     */
    public String toString() {
        String city = "City ID: " + cityID + "\nCity Name: " + name + "\nCity Location: " + location.toString() +
                "\nCity Radius: " + radius + "\nCity Account: " + account;
        return city;
    }
}
