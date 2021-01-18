package cscie97.smartcity.authentication;

/**
 * Exception class for Invalid auth token
 * @author Mariam Gogia
 */

public class InvalidAuthTokenException extends Exception{
    private String action;
    private String reason;

    /**
     * InvalidAuthTokenException constructor
     * @param action - action that caused error
     * @param reason - the reason it failed
     */
    public InvalidAuthTokenException(String action, String reason){
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
