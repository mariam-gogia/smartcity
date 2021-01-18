package cscie97.smartcity.controller;

import com.cscie97.ledger.Ledger;
import cscie97.smartcity.authentication.*;
import cscie97.smartcity.model.*;

import java.util.Map;
/**
 * Class responsible to respond to CO2 events
 * @author Mariam Gogia
 */
public class CO2Cmd implements Command {

    private int enableCars;
    private Event event;
    private String city;

    public CO2Cmd(int enable, Event event, String city) {
        this.event = event;
        this.enableCars = enable;
        this.city = city;
    }

    /***
     * If 3 unique devices report co2 levels above 1000, all cars are disabled,
     * if 3 unique devices report the decrease of co2 levels - the cars are enabled
     */
    @Override
    public void execute(AuthToken token) throws AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (AuthenticationImpl.getInstance().checkAccess(token, "scms_manage_device", "")) {
            for (Map.Entry<String, IoT> entry : SmartCityModelImpl.getInstance().getIoTMap().entrySet()) {
                if (entry.getValue().getClass().getName() == "cscie97.smartcity.model.Vehicle") {
                    Vehicle temp = (Vehicle) entry.getValue();
                    // get the city ID the device is assigned to
                    String[] city_device_pair = temp.getDeviceID().split(":", 3);
                    if (temp.getType().equals("car") && city.equals(city_device_pair[0])) {
                        if (enableCars == 1) {
                            temp.setEnabled(true);
                            temp.setActivity("Stopped");
                        } else if (enableCars == 0) {
                            temp.setEnabled(false);
                            temp.setActivity("Disabled due to high co2 levels in the city");
                        }
                        temp.setLatestEvent(event);
                    }
                }
            }
            if (enableCars == 1) {
                SmartCityModelImpl.getInstance().createSensorOutput("Incoming event: " + event.toString() + "\nEvent response:\n" +
                        "This is the 3rd unique device reporting CO2 levels below 1000 in the city:\n"
                        + "All cars in the city are enabled.\n");
            } else if (enableCars == 0) {
                SmartCityModelImpl.getInstance().createSensorOutput("Incoming event: " + event.toString() + "\nEvent response:\n" +
                        "This is the 3rd unique devices reporting CO2 levels above 1000:\n" + "All cars in the city are disabled.\n");
            }
        }
        else {
            throw new AuthenticationException("COCmd - checkAccess", "no authorization to disable/enable cars");
        }
    }
}

