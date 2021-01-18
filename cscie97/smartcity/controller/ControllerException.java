package cscie97.smartcity.controller;
/**
 * Exception class for Controller Exception
 * @author Mariam Gogia
 */
public class ControllerException extends Exception{

    // SmartCityModelException properties
    private String action;
    private String reason;

    /**
     * ControllerException constructor
     * @param action - action that caused error
     * @param reason - the reason it failed
     */
    public ControllerException(String action, String reason){
        super(action + ": " + reason);
        this.action = action;
        this.reason = reason;
    }

    // Getters and Setters
    public String getReason() {
        return this.reason;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) { this.action = action; }

    public void setReason(String reason) { this.reason = reason; }
}

