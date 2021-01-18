package cscie97.smartcity.controller;

import com.cscie97.ledger.Ledger;
import cscie97.smartcity.authentication.AuthToken;
import cscie97.smartcity.model.Event;
import cscie97.smartcity.model.SmartCityModelImpl;

public class BusRouteCmd implements Command{

    private Event event;

    public BusRouteCmd(Event event) {
        this.event = event;
    }

    /***
     * Bus speaker confirms the route
     * to the resident
     */
    @Override
    public void execute(AuthToken token) {
        SmartCityModelImpl.getInstance().createSensorOutput("Incoming event: " + event.toString() + "\nEvent response:\n"+
                "Yes, this bus goes to Central Square.\n");
    }
}
