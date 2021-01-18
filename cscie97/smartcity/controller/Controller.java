package cscie97.smartcity.controller;
import com.cscie97.ledger.LedgerException;
import cscie97.smartcity.authentication.*;
import cscie97.smartcity.model.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Controller class monitors changes in IoTs and manages responses to those changes
 * @author Mariam Gogia
 */
public class Controller implements Observer {

    private final Map<String, String> eventMap = new HashMap<>();
    private final Map<String, Integer> co2counter = new HashMap<>();
    private final Map<String, HashSet<String>> co2Above = new HashMap<>();
    private final Map<String, HashSet<String>> co2Below = new HashMap<>();

    /**
     * Controller Constructor
     * preloads the map with event triggering strings
     */
    public Controller() {
        SmartCityModelImpl.getInstance().register(this);
        // loading events that trigger Rules, using class names as values
        eventMap.put("fire", "EmergencyCmd");
        eventMap.put("flood","EmergencyCmd");
        eventMap.put("earthquake", "EmergencyCmd");
        eventMap.put("severe weather","EmergencyCmd");
        eventMap.put("traffic_accident", "EmergencyCmd");

        eventMap.put("littering", "LitterCmd");
        eventMap.put("broken_glass_sound", "BrokenGlassCmd");
        eventMap.put("person_seen", "PersonSeenCmd");

        eventMap.put("Does this bus go to central square?", "BusRouteCmd");
        eventMap.put("Person boards bus", "BoardBusCmd");
        eventMap.put("what movies are showing tonight?", "MovieInfoCmd");
        eventMap.put("reserve 3 seats for the 9 pm showing of Casablanca.", "MovieReservationCmd");
    }

    /**
     * update the device according to the command.
     * co2 meter events are managed separately
     * @param event - received event
     */
    @Override
    public void update(Event event, AuthToken token) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, LedgerException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        Command cmd = null;
        if(event.getSensorType().equals(IoT.SensorType.co2meter)) {
            cmd = processCO2event(event);
        }
        else {
            String eventType = isMatch(event);
            // Check if received event is of interest
            if (!eventType.equals("")){
                cmd = createCommand(eventType, event);
            }
        }
        if(cmd!=null){
            cmd.execute(token);

        }
    }

    /**
     * cmd contains the class name of the command to be created,
     * given the cmd, find the class and create an instance of it
     * @param cmd - string with the class name to be created
     * @param event - event to passed in as a parameter of the command instance
     * @return - command to be executed
     */
    public Command createCommand(String cmd, Event event) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        // find class by its name and create an instance
        cmd = "cscie97.smartcity.controller." + cmd;
        Class<Command> commandClass = (Class<Command>) Class.forName(cmd);
        return commandClass.getConstructor(Event.class).newInstance(event);
    }

    /**
     * Goes through the preloaded map (trigger string: commandClassName) of triggering strings,
     * returns the string that corresponds to the triggering string's value - a name of the command class to be created
     * @param event - incoming event
     * @return - string representing the name of the command class to be created
     */
    private String isMatch(Event event) {
        // remove "" from the string
        String value = event.getAction().replace("\"", "");
        // string for Missing Person command varies as the sentence
        // can be followed by any resident's name, so it hasn't been loaded to the map
        if(value.contains("can you help me find ")) {
            return "MissingPersonCmd";
        }
        // parking event will also have varied value, so instead used the following check:
        else if (event.getDevice().getClass().getName().equals("cscie97.smartcity.model.ParkingSpace") && value.contains("parked")) {
            return "ParkingCmd";
        }
        // if event map contains the event value string as its key, return the corresponding value of the key
        else {
            if(eventMap.containsKey(value)) {
                return eventMap.get(value);
            }
        }
       return "";
    }

    /**
     * Issues CO2Cmd only if 3 unique devices in the city consecutively reported
     * the value > 1000. If 3 unique devices in the city consecutively report that the value went down
     * after it went up, new CO2 command is issued
     * @param event - incoming event
     */
    private CO2Cmd processCO2event(Event event) {
        // from deviceID get the name of the city the device is assigned to
        String[] city_device_pair = event.getDevice().getDeviceID().split(":",3);
        String city = city_device_pair[0];
        // place new city on the maps
        if(!co2Above.containsKey(city)) {
            co2Above.put(city, new HashSet<>());
        }
        if(!co2Below.containsKey(city)) {
            co2Below.put(city, new HashSet<>());
        }
        if(!co2counter.containsKey(city)) {
            co2counter.put(city, 0);
        }
        // get the CO2 meter value
        int reported_value = Integer.parseInt(event.getAction().replace("\"", ""));
        CO2Cmd cmd = null;
        // add the device to co2above 1000 reporting devices sets (no dupes), clear the set of "belows"
        if(reported_value > 1000) {
            co2Above.get(city).add(event.getDevice().getDeviceID());
            co2Below.get(city).clear();
        }
        // add the device to co2below 1000 reporting devices sets (no dupes), clear the set of "aboves"
        else if(reported_value < 1000) {
            co2Below.get(city).add(event.getDevice().getDeviceID());
            co2Above.get(city).clear();
        }
        if(co2Above.get(city).size() == 3) {
            cmd = new CO2Cmd(0, event, city);
            co2counter.put(city, co2counter.get(city) + 1);
            System.out.println("3 devices reported high CO2 levels - disabling cars in " + city);
            co2Above.get(city).clear();
        }
        // only report "co2 went down" if it went up the first place
        else if (co2Below.get(city).size() == 3 && co2counter.get(city) == 1){
            cmd = new CO2Cmd(1, event, city);
            co2Below.get(city).clear();
            System.out.println("3 devices reported low CO2 levels - enabling cars in " + city);
            co2counter.put(city, co2counter.get(city) -1);
        }
        return cmd;
    }
}
