package cscie97.smartcity.controller;

import com.cscie97.ledger.Ledger;
import cscie97.smartcity.authentication.*;
import cscie97.smartcity.model.*;

import java.util.Map;
/**
 * Class resposible for managing requests regarding missing person in the city
 * @author Mariam Gogia
 */
public class MissingPersonCmd implements Command {

    private Event event;

    public MissingPersonCmd(Event event) {
        this.event = event;
    }

    /***
     * Look up the person and confirm such resident exists,
     * Find the robot to send and bring the person to the missing-person-reporting device's location
     */
    @Override
    public void execute(AuthToken token) throws AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (AuthenticationImpl.getInstance().checkAccess(token, "scms_manage_device", "")) {
            // Get the ID of the missing person, remove spaces and "
            String person_id = event.getAction().substring(1 + event.getAction().lastIndexOf(' ')).replace("\"", "");
            // if such resident exists, find the robot and retrieve him/her
            try {
                if (SmartCityModelImpl.getInstance().getPeopleIDMap().containsKey(person_id)) {
                    // find the robot to send
                    Robot robot_to_send = null;
                    for (Map.Entry<String, IoT> entry : SmartCityModelImpl.getInstance().getIoTMap().entrySet()) {
                        if (entry.getValue().getClass().getName() == "cscie97.smartcity.model.Robot"
                                && entry.getValue().isEnabled()) {
                            robot_to_send = (Robot) entry.getValue();
                            break;
                        }
                    }
                    robot_to_send.setActivity("Retrieving a lost child " + person_id);
                    robot_to_send.setLocation(event.getDevice().getLocation());
                    robot_to_send.setLatestEvent(event);
                    // updating the person's location

                    SmartCityModelImpl.getInstance().createSensorOutput("Incoming Event: " + event.toString() + "\nEvent response:\n" +
                            "The person " + person_id + " was found at" + SmartCityModelImpl.getInstance().getPeopleIDMap().get(person_id).getLocation().toString()
                            + " robot " + robot_to_send.getDeviceID() + " is retrieving the resident, stay where you are.\n");

                    SmartCityModelImpl.getInstance().getPeopleIDMap().get(person_id).setLocation(event.getDevice().getLocation());
                } else {
                    throw new ControllerException("Controller Exception: MissingPerson", "Such person does not exist");
                }
            } catch (ControllerException e) {
                System.out.println("\nIncoming event: " + event.toString() + "\nEvent response:\n" + e.getMessage() + "\n");
            }
        } else {
            throw new AccessDeniedException("MissingPersonCmd - checkAccess", "token owner is not authorized to deploy robots");
        }
    }
}
