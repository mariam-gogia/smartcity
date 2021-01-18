package cscie97.smartcity.authentication;
import java.time.*;
/**
 * Class represent the authentication tokens of the module
 * @author Mariam Gogia
 */
public class AuthToken implements Visitable{
    private String id;
    private LocalDateTime lastUsed;
    private boolean isActive;

    public AuthToken(){}
    public AuthToken(String id) {
        this.id = id;
        this.isActive = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return lastUsed;
    }

    public void setTime(LocalDateTime time) {
        this.lastUsed = time;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public void acceptVisitor(Visitor visitor) {
        visitor.visitAuthToken(this);
    }
    public String toString() {
        return("Token id: " +id + "\nLast used: " +lastUsed + "\nisActive: " + isActive);
    }
}
