package cscie97.smartcity.model;

import com.cscie97.ledger.LedgerException;
import cscie97.smartcity.authentication.AccessDeniedException;
import cscie97.smartcity.authentication.AuthToken;
import cscie97.smartcity.authentication.AuthenticationException;
import cscie97.smartcity.authentication.InvalidAuthTokenException;

import java.lang.reflect.InvocationTargetException;

/**
 * The interface for the SmartCityModel.
 * All the methods to be utilized by SmartCityModel are defined below
 * @author Mariam Gogia
 */
public interface SmartCityModelInterface {
    // abstract methods
    void defineCity(City city, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
    String showCity(String cityID, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
    void definePerson(Person person, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
    String showPerson(String id, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
    void updatePerson(Person person, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
    void defineDevice(IoT device, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
    String showDevice(String deviceID, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
    void updateDevice(IoT device, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
    void createSensorEvent(String deviceID, Event event, AuthToken auth_token) throws SmartCityModelException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, LedgerException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
    String createSensorOutput( String deviceID, Event event, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
}
