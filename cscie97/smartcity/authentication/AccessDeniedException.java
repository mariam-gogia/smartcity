package cscie97.smartcity.authentication;
/**
 * Exception class for Access Denied
 * @author Mariam Gogia
 */
public class AccessDeniedException extends Exception{
    private String action;
    private String reason;

    /**
     * AccessDeniedException constructor
     * @param action - action that caused error
     * @param reason - the reason it failed
     */
    public AccessDeniedException(String action, String reason){
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

