package cscie97.smartcity.controller;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.ledger.Transaction;
import cscie97.smartcity.authentication.*;
import cscie97.smartcity.model.*;
/**
 * Class resposible for managing parking events 
 * @author Mariam Gogia
 */
public class ParkingCmd implements Command {

    private Event event;

    public ParkingCmd (Event event) {
        this.event = event;
    }

    /***
     * Find the the account of the vehicle, ensure sufficient funds
     * charge vehicle the parking rate for parking
     * @throws LedgerException
     */
    @Override
    public void execute(AuthToken token) throws LedgerException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (AuthenticationImpl.getInstance().checkAccess(token, "scms_charge_users", "")) {
            // retrieving the parking object
            ParkingSpace temp = (ParkingSpace) SmartCityModelImpl.getInstance().getIoTMap().get(event.getDevice().getDeviceID());
            // retrieve the vehicle_id from event value
            String[] event_value = event.getAction().split(" ");
            String vehicle_id = event_value[1];

            try {
                Vehicle temp_vehicle_obj = (Vehicle) SmartCityModelImpl.getInstance().getIoTMap().get(vehicle_id);
                if (temp_vehicle_obj == null) {
                    throw new ControllerException("Controller Exception: ParkingEvent", "No Such Vehicle in the system");
                } else {
                    if (temp.getHourlyRate() > Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(vehicle_id).getBalance()) {
                        throw new ControllerException("Controller Exception: ParkingEvent", "Insufficient funds");
                    }
                    temp.setAvailable(false);
                    Ledger.getInstance().processTransaction(new Transaction(this.toString(), temp.getHourlyRate(), 10, "Parking Fee",
                            Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(temp_vehicle_obj.getAccount()),
                            Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(temp.getAccount())));

                    temp_vehicle_obj.setLocation(temp.getLocation());
                    temp_vehicle_obj.setActivity("Parked");
                    temp_vehicle_obj.setLatestEvent(event);

                    SmartCityModelImpl.getInstance().createSensorOutput("Incoming event: " + event.toString() + "\nEvent response:\n" +
                            vehicle_id + " was charged " + temp.getHourlyRate() + " units plus the transaction fee by " + temp.getDeviceID() + "\n");
                    //System.out.println(Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(vehicle_id).getBalance());
                    // System.out.println(Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(temp.getAccount()).getBalance());
                }
            } catch (ControllerException e) {
                SmartCityModelImpl.getInstance().createSensorOutput("Incoming event: " + event.toString() + "\nEvent response:\n" +
                        e.getMessage());
            }
        }
        else {
            throw new AccessDeniedException("ParkingCmd - checkAccess", "token owner is not authorized to charge users");
        }
    }

}
