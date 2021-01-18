package cscie97.smartcity.controller;

import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.ledger.Transaction;
import cscie97.smartcity.authentication.AccessDeniedException;
import cscie97.smartcity.authentication.AuthToken;
import cscie97.smartcity.authentication.AuthenticationImpl;
import cscie97.smartcity.model.*;

/**
 * Class responsible to respond to boarding bus event
 * @author Mariam Gogia
 */
public class BoardBusCmd implements Command {
    private cscie97.smartcity.model.Event event;

    public BoardBusCmd(cscie97.smartcity.model.Event event) {
        this.event = event;
    }

    /***
     * Look up a person, check if a person is a resident and if
     * he/she has sufficient balance to ride
     * @throws LedgerException
     */
    @Override
    public void execute(AuthToken token) throws LedgerException, AccessDeniedException {
        Person temp_person = SmartCityModelImpl.getInstance().getPeopleIDMap().get(event.getSubject());
        // check if the person has right to ride a bus
        if(AuthenticationImpl.getInstance().checkAccess("scms_ride_bus", temp_person.getID())) {
            Vehicle vehicle = (Vehicle) event.getDevice();
        try {
            if (temp_person.getClass().getName() == "cscie97.smartcity.model.Resident") {
                Resident person = (Resident) temp_person;
                if (vehicle.getFee() < Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(person.getAccount()).getBalance()) {
                    Ledger.getInstance().processTransaction(new Transaction(this.toString(), vehicle.getFee(), 10, "Bus Fee",
                            Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(person.getAccount()),
                            Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(vehicle.getAccount())));

                    SmartCityModelImpl.getInstance().createSensorOutput("Incoming event: " +event.toString() +"\nEvent response:\n" +
                            "Hello, good to see you, " +person.getName() + "\n" +person.getName() +" is charged "
                            + vehicle.getFee() +" units plus the transaction fee.\n");
                } else {
                    throw new ControllerException("Controller Exception: BoardBus ", "Insufficient Funds");
                }
            }
            else {
                SmartCityModelImpl.getInstance().createSensorOutput("Incoming event: " +event.toString() +"\nEvent response:\n" +
                        "Enjoy your free ride " +event.getSubject() +"\n");
            }
        } catch (ControllerException e) {
            SmartCityModelImpl.getInstance().createSensorOutput("Incoming event: " +event.toString() +"\nEvent response:\n" + e.getMessage() + "\n");
        }
    } else {
            throw new AccessDeniedException("BoardBus - checkAccess", temp_person.getID() + " has no right to ride the bus");
        }
    }
}

