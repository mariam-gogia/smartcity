package cscie97.smartcity.controller;

import com.cscie97.ledger.Account;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.ledger.Transaction;
import cscie97.smartcity.authentication.*;
import cscie97.smartcity.model.*;

import java.util.Map;
/**
 * Class resposible for managing littering events
 * @author Mariam Gogia
 */
public class LitterCmd implements Command {

    private Event event;
    private final int littering_fee = 50;

    public LitterCmd (Event event) {
        this.event = event;
    }

    /***
     * Speaker - Do not litter,
     * Robot goes and cleans
     * Person gets charged 50 units + transaction fee
     */
    @Override
    public void execute(AuthToken token) throws LedgerException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (AuthenticationImpl.getInstance().checkAccess(token, "scms_charge_users", "")) {

            String[] city_device_pair = event.getDevice().getDeviceID().split(":", 3);
            String city = city_device_pair[0];
            Robot robot_to_send = new Robot();
            for (Map.Entry<String, IoT> entry : SmartCityModelImpl.getInstance().getIoTMap().entrySet()) {
                if (entry.getValue().getClass().getName() == "cscie97.smartcity.model.Robot"
                        && entry.getValue().isEnabled() && entry.getValue().getDeviceID().contains(city_device_pair[0])) {
                    robot_to_send = (Robot) entry.getValue();
                    break;
                }
            }
            robot_to_send.setActivity("cleaning garbage");
            robot_to_send.setLocation(event.getDevice().getLocation());
            robot_to_send.setLatestEvent(event);

            String output = "Incoming event: " + event.toString() + "\nEvent response:\n";
            if (SmartCityModelImpl.getInstance().getPeopleIDMap().containsKey(event.getSubject())) {
                Person person = SmartCityModelImpl.getInstance().getPeopleIDMap().get(event.getSubject());
                if (person.getClass().getName() == "cscie97.smartcity.model.Resident") {
                    Resident resident = (Resident) person;
                    if (littering_fee + 10 < Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(resident.getAccount()).getBalance()) {
                        // Get the account for the city
                        String city_acc = SmartCityModelImpl.getInstance().getCityList().get(city).getAccount();
                        // To ensure uniqueness of the transaction id. Using this.toString(), which will be the reference of the current
                        // instance of littering command
                        Ledger.getInstance().processTransaction(new Transaction(this.toString(), littering_fee, 10, "littering fine",
                                Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(resident.getAccount()),
                                Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(city_acc)));
                        //System.out.println(Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(city_acc).getBalance());
                        output = output + "Resident " + resident.getName() + " is charged 50 units plus the transaction fee.\n" +
                                robot_to_send.getDeviceID() + " is sent to clean up the garbage.\n";
                    }
                } else {
                    output = output + "Garbage was thrown by visitor, so he/she cannot be charged\n" +
                            robot_to_send.getDeviceID() + " is sent to clean up the garbage.\n";
                }
            }
            SmartCityModelImpl.getInstance().createSensorOutput(output);
        } else {
            throw new AccessDeniedException("LitterCmd - checkAccess","token owner is not authorized to charge users");
        }
    }
}
