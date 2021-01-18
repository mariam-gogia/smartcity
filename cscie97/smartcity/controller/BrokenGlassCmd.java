package cscie97.smartcity.controller;

import com.cscie97.ledger.Ledger;
import cscie97.smartcity.authentication.*;
import cscie97.smartcity.model.Event;
import cscie97.smartcity.model.IoT;
import cscie97.smartcity.model.Robot;
import cscie97.smartcity.model.SmartCityModelImpl;
import java.util.Map;

/**
 * Class responsible to respond to broken glass event
 * @author Mariam Gogia
 */
public class BrokenGlassCmd implements Command {

    private Event event;

    public BrokenGlassCmd(Event event) {
        this.event = event;
    }

    /**
     * Finds the enabled robot in the city and sends it
     * to clean up the class at the location of the event reporting device
     */
    @Override
    public void execute(AuthToken token) throws AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        // check if the token owner has right to manage device
        if (AuthenticationImpl.getInstance().checkAccess(token, "scms_manage_device", "")) {
            // find enabled robot and send to clean up the glass
            try {
                Robot robot_to_send = new Robot();
                for (Map.Entry<String, IoT> entry : SmartCityModelImpl.getInstance().getIoTMap().entrySet()) {
                    if (entry.getValue().getClass().getName() == "cscie97.smartcity.model.Robot"
                            && entry.getValue().isEnabled()) {
                        robot_to_send = (Robot) entry.getValue();
                        break;
                    }
                }
                if (robot_to_send != null) {
                    robot_to_send.setActivity("cleaning up broken glass");
                    robot_to_send.setLocation(event.getDevice().getLocation());
                    robot_to_send.setLatestEvent(event);

                    SmartCityModelImpl.getInstance().createSensorOutput("Incoming event: " + event.toString() + "\nEvent response:\n" + "The Robot: " + robot_to_send.getDeviceID() + " is sent to location" + "" +
                            event.getDevice().getLocation().toString() + " to clean up the broken glass.\n");
                } else {
                    throw new ControllerException("Controller Exception: BrokenGlass", "No robots to send");
                }
            } catch (ControllerException e) {
                System.out.println("Incoming event: " + event.toString() + "\nEvent response:\n" + e.getMessage());
            }
        } else {
            throw new AccessDeniedException("BrokenGlassCmd - checkAccess", "token owner is not authorized to deploy robots");
        }
    }
}
