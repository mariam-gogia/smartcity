package cscie97.smartcity.controller;
import com.cscie97.ledger.LedgerException;
import cscie97.smartcity.authentication.AccessDeniedException;
import cscie97.smartcity.authentication.AuthToken;
import cscie97.smartcity.authentication.AuthenticationException;
import cscie97.smartcity.authentication.InvalidAuthTokenException;


/**
 * Interface for concrete command classes
 */
public interface Command {
    void execute(AuthToken token) throws LedgerException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
}
