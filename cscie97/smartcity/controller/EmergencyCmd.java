package cscie97.smartcity.controller;

import cscie97.smartcity.authentication.*;
import cscie97.smartcity.model.*;
import java.util.*;

/**
 * Class resposible for managing emergency events
 * @author Mariam Gogia
 */
public class EmergencyCmd implements Command {
    private Event event;

    public EmergencyCmd (Event event) {
        this.event = event;
    }

    @Override
    public void execute(AuthToken token) throws AccessDeniedException, AuthenticationException, InvalidAuthTokenException {

        if (AuthenticationImpl.getInstance().checkAccess(token,"scms_manage_device", "")) {
            String[] city_device_pair = event.getDevice().getDeviceID().split(":", 3);
            String emergency_type = event.getAction().replace("\"", "");
            // pull all robots of the city and place them in robots list
            ArrayList<Robot> robots = new ArrayList<>();
            try {
                for (Map.Entry<String, IoT> entry : SmartCityModelImpl.getInstance().getIoTMap().entrySet()) {
                    if (entry.getValue().getClass().getName() == "cscie97.smartcity.model.Robot" && entry.getValue().getDeviceID().contains(city_device_pair[0])) {
                        robots.add((Robot) entry.getValue());
                    }
                }
                if (robots.isEmpty()) {
                    throw new ControllerException("Controller Exception: EmergencyCmd:", "No robots in the city");
                }
            } catch (ControllerException e) {
                System.out.println(e.getMessage());
            }

            if (emergency_type.equals("traffic_accident")) {
                // store distance:robot pairs in TreeMap (Sorted Map)
                TreeMap<Double, Robot> distances = new TreeMap<>();
                for (int i = 0; i < robots.size(); i++) {
                    distances.put(Location.distance(event.getDevice().getLocation(),
                            robots.get(i).getLocation()), robots.get(i));
                }
                if (distances.size() < 2) {
                    System.out.println("Command exception: No 2 robots to deploy");
                } else {
                    // deploy first two robots from the sorted map.
                    Robot robot1 = distances.firstEntry().getValue();
                    robot1.setLocation(event.getDevice().getLocation());
                    robot1.setActivity("Helping out with the traffic accident");
                    robot1.setLatestEvent(event);

                    // remove the first item to make the second nearest robot first
                    distances.remove(distances.firstKey());
                    Robot robot2 = distances.firstEntry().getValue();
                    robot2.setLocation(event.getDevice().getLocation());
                    robot2.setLatestEvent(event);
                    robot2.setActivity("Helping out with the traffic accident");
                    //announce
                    SmartCityModelImpl.getInstance().createSensorOutput("Incoming event:" + event.toString() + "\nEvent response:\n" +
                            "Stay Calm, help is on its way! \n" +
                            "Robots: " + robot1.getDeviceID() + " and " + robot2.getDeviceID() + " are sent to the location.\n");
                }
            } else {
                // send 1/2 robots to the location and rest to help people find shelter
                for (int i = 0; i < robots.size() / 2; i++) {
                    robots.get(i).setLatestEvent(event);
                    robots.get(i).setLocation(event.getDevice().getLocation());
                    robots.get(i).setActivity("Helping with emergency at the location");
                }
                for (int i = robots.size() / 2; i < robots.size(); i++) {
                    robots.get(i).setLatestEvent(event);
                    robots.get(i).setActivity("Helping people find shelter");
                }
                //announce
                SmartCityModelImpl.getInstance().createSensorOutput("Incoming event:" + event.toString() + "\nEvent response:\n" +
                        "There is " + emergency_type + " emergency in " + city_device_pair[0] + " please find shelter immediately.\n" +
                        "1/2 of the robots have been sent to help with emergency and the other half are helping people find shelters.\n");
            }
        }
        else {
            throw new AccessDeniedException("EmergencyRule - checkAccess", "token owner is not authorized to deploy robots");
        }
    }

}
