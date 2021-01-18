package cscie97.smartcity.test;
import com.cscie97.ledger.LedgerException;
import cscie97.smartcity.authentication.AccessDeniedException;
import cscie97.smartcity.authentication.AuthenticationException;
import cscie97.smartcity.authentication.InvalidAuthTokenException;
import cscie97.smartcity.model.CommandLineInterface;
import cscie97.smartcity.model.CommandLineInterfaceException;

import java.security.NoSuchAlgorithmException;

/**
 * TestDriver Class accepts a single command line parameter - command file
 * and calls the CommandProcessor
 *
 * @author Mariam Gogia
 */
public class TestDriver {
    public static void main(String[] args) throws CommandLineInterfaceException, LedgerException, NoSuchAlgorithmException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        //check if the file path is passed as a command line argument
        try {
            String file = "";
            if (args.length > 0) {
                file = args[0];
            }
            // call to CommandProcessor to process the passed file
            CommandLineInterface commandProcessor = new CommandLineInterface();
            commandProcessor.processCommandFile(file);
        } catch (CommandLineInterfaceException e) {
            System.out.println(e.getMessage());
        }
    }
}
