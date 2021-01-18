package cscie97.smartcity.controller;

import com.cscie97.ledger.Ledger;
import cscie97.smartcity.authentication.AuthToken;
import cscie97.smartcity.model.Event;
import cscie97.smartcity.model.InfoKiosk;
import cscie97.smartcity.model.SmartCityModelImpl;
/**
 * Class resposible for responding to the movie info request
 * @author Mariam Gogia
 */
public class MovieInfoCmd implements Command {
    private Event event;

    public MovieInfoCmd(Event event) {
        this.event = event;
    }

    /**
     * Updates the kiosk with correct display image,
     * Tells resident about tonight's showing
     */
    @Override
    public void execute(AuthToken token) {
        InfoKiosk temp = (InfoKiosk)SmartCityModelImpl.getInstance().getIoTMap().get(event.getDevice().getDeviceID());
        temp.setDisplayImage("“https://en.wikipedia.org/wiki/Casablanca_(film)#/media/File:CasablancaPoster-Gold.jpg”");

        SmartCityModelImpl.getInstance().createSensorOutput("Incoming event:" + event.toString() +"\nEevent response:\n" +
                "Casablanca is showing at 9pm.\n");
    }
}
