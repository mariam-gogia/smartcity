package cscie97.smartcity.model;

import com.cscie97.ledger.LedgerException;
import cscie97.smartcity.authentication.AccessDeniedException;
import cscie97.smartcity.authentication.AuthToken;
import cscie97.smartcity.authentication.AuthenticationException;
import cscie97.smartcity.authentication.InvalidAuthTokenException;
import cscie97.smartcity.controller.Observer;

import java.lang.reflect.InvocationTargetException;

public interface Subject {
    void notify(Event event, AuthToken token) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, LedgerException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
    void register(Observer observer);
    void deregister(Observer observer);
}
