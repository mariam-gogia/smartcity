package cscie97.smartcity.model;

/**
 * Event class is used to model an event instance simulated for IoT sensors
 * @author Mariam Gogia
 */
public class Event {
    // Event class properties
    private IoT.SensorType sensorType;
    private String action;
    private String subject;
    private IoT device;

    // constructor #1 for events without subjects
    public Event(IoT.SensorType sensorType, String action) {
        this.sensorType = sensorType;
        this.action = action;
    }
    // constructor #2 for events with provided subjects
    public Event(IoT.SensorType sensorType, String action, String subject) {
        this.sensorType = sensorType;
        this.action = action;
        this.subject = subject;
    }

    /**
     * @return String with event details
     */
    public String toString() {
        String str = "";
        str = getDevice().getDeviceID()  + " " + sensorType + " value " + action;
        if(subject != null ) {
          str += " subject: " + subject;
        }
        return str;
    }

    // Setters & Getters
    public IoT.SensorType getSensorType() { return sensorType; }
    public void setSensorType(IoT.SensorType sensorType) { this.sensorType = sensorType; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public IoT getDevice() { return device; }

    public void setDevice(IoT device) { this.device = device; }
}
