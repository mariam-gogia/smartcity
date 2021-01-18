package cscie97.smartcity.controller;

import com.cscie97.ledger.LedgerException;
import cscie97.smartcity.authentication.AccessDeniedException;
import cscie97.smartcity.authentication.AuthToken;
import cscie97.smartcity.authentication.AuthenticationException;
import cscie97.smartcity.authentication.InvalidAuthTokenException;
import cscie97.smartcity.model.Event;

import java.lang.reflect.InvocationTargetException;

/**
 * Interface to be implemented by observers
 */
public interface Observer {
    void update(Event event, AuthToken token) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, LedgerException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
}
