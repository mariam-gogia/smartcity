package cscie97.smartcity.model;
/**
 * CommandLineInterfaceException captures the command that was attempted and the reason for failure.
 * It includes the line number of the script causing the error
 *
 * @author Mariam Gogia
 */
public class CommandLineInterfaceException extends Exception {
    // CommandLineInterfaceException class properties
    private String command;
    private String reason;
    private int lineNumber;

    /** CommandLineInterfaceException constructor
     * @param command - command causing the exception
     * @param reason - the reason for exception
     * @param lineNumber - the line number of the script causing the exception
     */
    public CommandLineInterfaceException(String command, String reason, int lineNumber){
        super(command + ": " + reason + ", line: " + lineNumber); //maybe
        this.command = command;
        this.reason = reason;
        this.lineNumber = lineNumber;
    }

    // Getters & Setters
    public String getReason() {
        return this.reason;
    }
    public String getAction() {
        return this.command;
    }

    public int getLineNumber() { return this.lineNumber; }
    public void setCommand(String command) { this.command = command; }

    public void setReason(String reason) { this.reason = reason; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber;}

    public String getCommand() { return command; }
}
