package cscie97.smartcity.model;

/**
 * The location class is a helper class for all entities in the system
 * All entities contain location and this class manages the location properties for them
 * @author Mariam Gogia
 */
public class Location {
    // class properties
    private float Lat;
    private float Long;

    public Location (float Lat, float Long) {
        this.Lat = Lat;
        this.Long = Long;
    }
    // code snippet is is borrowed from: https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
    // following elements are used to calculate the distance between two lat long points in distance function
    // r2d - radian to degree, d2r - degree to radian, d2km - degree to kilometer
    private static final float r2d = (float)180.0D /(float) 3.141592653589793D;
    private static final float d2r = (float) 3.141592653589793D /(float) 180.0D;
    private static final float d2km = (float) 111189.57696D * r2d;

    /**
     * @param location1 location of the first object (e.g. city)
     * @param location2 location of the second object (e.g person, device)
     * @return The distance between 2 lat long points
     */
    public static double distance(Location location1, Location location2) {
        final float x = location1.Lat * d2r;
        final float y = location2.Lat * d2r;
        return Math.acos( Math.sin(x) * Math.sin(y) + Math.cos(x) * Math.cos(y) * Math.cos(d2r * (location1.Long - location2.Long))) * d2km /1000;
    }

    /**
     * @return String populated by location details
     */
    public String toString() {
        return  " Lat: " + Lat + " Long: " + Long ;
    }
}
