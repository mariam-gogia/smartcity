package cscie97.smartcity.controller;

import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.ledger.Transaction;
import cscie97.smartcity.authentication.*;
import cscie97.smartcity.model.*;
/**
 * Class resposible for responding to movie reservations requested by residents
 * @author Mariam Gogia
 */
public class MovieReservationCmd implements Command {
    private Event event;
    private final int movie_fee = 10;
    public MovieReservationCmd(Event event) {
        this.event = event;
    }

    @Override
    public void execute(AuthToken token) throws AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        // check if the token owner has authorization to charge users
        if (AuthenticationImpl.getInstance().checkAccess(token, "scms_charge_users", "")) {

            InfoKiosk kiosk = (InfoKiosk) event.getDevice();
            Person temp_person = SmartCityModelImpl.getInstance().getPeopleIDMap().get(event.getSubject());
            try {
                if (temp_person.getClass().getName() == "cscie97.smartcity.model.Resident") {
                    Resident person = (Resident) temp_person;
                    if (movie_fee + 10 < Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(person.getAccount()).getBalance()) {
                        Ledger.getInstance().processTransaction(new Transaction(this.toString(), movie_fee, 10, "Movie Ticket",
                                Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(person.getAccount()),
                                Ledger.getInstance().getCurrentBlock().getAccountBalanceMap().get(kiosk.getAccount())));

                        SmartCityModelImpl.getInstance().createSensorOutput("Incoming event: " + event.toString() + "\nEvent response:\n" +
                                "Your seats are reserved; please arrive a few minutes early.\n" + person.getName() + " is charged "
                                + movie_fee + " units plus the transaction fee.\n");
                    } else {
                        throw new ControllerException("Controller Exception: MovieReservation ", "Insufficient Funds");
                    }
                } else {
                    SmartCityModelImpl.getInstance().createSensorOutput("Incoming event: " + event.toString() + "\nEvent response:\n" + "Sorry, visitors without account addresses cannot make movie reservations\n");
                }
            } catch (ControllerException | LedgerException e) {
                SmartCityModelImpl.getInstance().createSensorOutput("Incoming event: " + event.toString() + "\nEvent response:\n" + e.getMessage());
            }
        } else {
            throw new AccessDeniedException("MovieReservationCmd - checkAccess", "token owner is not authorized to charge users");

        }
    }
}