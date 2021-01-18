package cscie97.smartcity.controller;

import com.cscie97.ledger.Ledger;
import cscie97.smartcity.authentication.AuthToken;
import cscie97.smartcity.model.Event;
import cscie97.smartcity.model.SmartCityModelImpl;
/**
 * Class resposible for responding to seeing person
 * @author Mariam Gogia
 */
public class PersonSeenCmd implements Command {

    private Event event;

    public PersonSeenCmd(Event event) {
        this.event = event;
    }

    /***
     * Set the location of the seen person to wherever the person was seen
     */
    @Override
    public void execute(AuthToken token) {
        SmartCityModelImpl.getInstance().getPeopleIDMap().get(event.getSubject()).setLocation(
                event.getDevice().getLocation()
        );
        SmartCityModelImpl.getInstance().createSensorOutput("Incoming event: " + event.toString() + "\nEvent response:\n"+
                "Updating the location of " + event.getSubject() + " to" + event.getDevice().getLocation() + "\n");
    }
}
