package cscie97.smartcity.model;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.ledger.Transaction;
import cscie97.smartcity.authentication.*;
import cscie97.smartcity.controller.Controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.*;

/**
 * A class responsible for reading the scrip
 * processing the file and issuing commands to SmartCityModelImpl
 * @author Mariam Gogia
 */
public class CommandLineInterface {
    // instance of the model class
    SmartCityModelImpl model = SmartCityModelImpl.getInstance();
    AuthenticationImpl authentication = AuthenticationImpl.getInstance();
    Ledger ledger = Ledger.getInstance();
    Controller controller = new Controller();
    AuthToken controller_auth_token = new AuthToken();
    AuthToken auth_token = new AuthToken();
    private int transaction_counter = 0;

    /** Processes and formats the file, prepares it to issue commands
     * @param commandFile a file with commands to be read
     * @throws CommandLineInterfaceException
     */
    public void processCommandFile (String commandFile) throws CommandLineInterfaceException, LedgerException, NoSuchAlgorithmException, AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        ledger.initLedger("ledger","smart city ledger service", "1");
        // check if the file exists
        File file = new File(commandFile);
        if(!file.isFile()) {
            throw new CommandLineInterfaceException("processCommandFile", "File does not exist", 0);
        }
        // read and add lines to commandFileLines list
        List<String> commandFileLines = new ArrayList<String>();
        try(Stream<String> stream = Files.lines((Paths.get(commandFile)))) {
            stream.forEach(commandFileLines::add);
        }
        catch (IOException e){
            throw new CommandLineInterfaceException("processCommandFile", "Error reading the file", 0);
        }
        // Using sorted tree to add lines
        // filtering out comments and empty lines
        TreeMap<Integer, String> commands = new TreeMap<Integer, String>();
        for(int i = 0; i < commandFileLines.size(); i++) {
            if(commandFileLines.get(i).contains("#") || commandFileLines.get(i).length() == 0) {
                continue;
            }
            // line 0 is line 1
            commands.put(i+1, commandFileLines.get(i));
        }
        for(Map.Entry<Integer, String> entry : commands.entrySet()) {
            // format command string to include the line number
            // lineNumber command inputs
            processCommand(String.format("%d %s", entry.getKey(), entry.getValue()));
        }
    }

    /** Processes the commands from the script
     * @param command command to be executed
     * @throws CommandLineInterfaceException
     */
    public void processCommand (String command) throws CommandLineInterfaceException, LedgerException, NoSuchAlgorithmException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
        Matcher regexMatcher = regex.matcher(command);

        List<String> commandTokens = new ArrayList<>();
        while (regexMatcher.find()) {
            commandTokens.add(regexMatcher.group());
        }
        // If queried correctly, no line should be 3 lines or less
        if (commandTokens.size() < 3) {
            throw new CommandLineInterfaceException("Invalid input", "invalid input", Integer.parseInt(commandTokens.get(0)));
        }
        // first two words (e.g. define city) in the script correspond to commands
        String cmd = commandTokens.get(1) + commandTokens.get(2);

        //System.out.println(cmd);
      //System.out.println(token);
        try {
            switch (cmd) {
                case "login":
                    auth_token = authentication.login(commandTokens.get(3), commandTokens.get(4));
                    controller_auth_token = authentication.controller_login();
                    break;
                case "logout":
                    authentication.logout(auth_token);
                    break;
                case "createuser":
                    authentication.createUser(commandTokens.get(3), commandTokens.get(4), auth_token);
                    break;
                case "adduser_credential":
                    authentication.addCredentials(commandTokens.get(3), commandTokens.get(5), auth_token);
                    break;
                case "definepermission":
                    authentication.createPermission(commandTokens.get(3), commandTokens.get(4), commandTokens.get(5), auth_token);
                    break;
                case "definerole":
                    authentication.createRole(commandTokens.get(3), commandTokens.get(4), commandTokens.get(5), auth_token);
                    break;
                case "defineresource":
                    authentication.createResource(commandTokens.get(3), commandTokens.get(4), auth_token);
                    break;
                case "add_permissionto_role":
                    authentication.addPermissionToRole(commandTokens.get(3), commandTokens.get(4), auth_token);
                    break;
                case "add_roleto_user":
                    authentication.addRoleToUser(commandTokens.get(3), commandTokens.get(4), auth_token);
                    break;
                case "createresource_role":
                    authentication.createResourceRole(commandTokens.get(3), commandTokens.get(4),commandTokens.get(5), auth_token);
                    break;
                case "printinventory":
                    authentication.printInventory(auth_token);
                    break;
                case "definecity":
                    if (commandTokens.size() != 14) {
                        throw new CommandLineInterfaceException(cmd, "Invalid Number of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    City city = new City(commandTokens.get(3), commandTokens.get(5), commandTokens.get(7),
                            new Location((Float.parseFloat(commandTokens.get(9))), Float.parseFloat(commandTokens.get(11))), Integer.parseInt(commandTokens.get(13)));
                    model.defineCity(city, auth_token);
                    ledger.createAccount(city.getAccount());
                    break;
                case "showcity":
                    if (commandTokens.size() != 4) {
                        throw new CommandLineInterfaceException(cmd, "Invalid Number of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    System.out.println(model.showCity(commandTokens.get(3), auth_token));
                    break;
                case "definestreet-sign":
                    if (commandTokens.size() != 12) {
                        throw new CommandLineInterfaceException(cmd, "Invalid Number of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    StreetSign streetSign = new StreetSign(commandTokens.get(3), new Location((Float.parseFloat(commandTokens.get(5))), Float.parseFloat(commandTokens.get(7))), Boolean.parseBoolean(commandTokens.get(9)),
                            true, commandTokens.get(11));
                    streetSign.setAccount(commandTokens.get(3));
                    model.defineDevice(streetSign, auth_token);
                    ledger.createAccount(streetSign.getAccount());
                    break;
                case "defineinfo-kiosk":
                    if (commandTokens.size() != 12) {
                        throw new CommandLineInterfaceException(cmd, "Invalid Number of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    InfoKiosk kiosk = new InfoKiosk(commandTokens.get(3),
                            new Location((Float.parseFloat(commandTokens.get(5))), Float.parseFloat(commandTokens.get(7))), Boolean.parseBoolean(commandTokens.get(9)),
                            true, commandTokens.get(11));
                    kiosk.setAccount(commandTokens.get(3));
                    model.defineDevice(kiosk, auth_token);
                    ledger.createAccount(kiosk.getAccount());
                    break;
                case "definestreet-light":
                    if (commandTokens.size() != 12) {
                        throw new CommandLineInterfaceException(cmd, "Invalid Number of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    StreetLight streetLight = new StreetLight(commandTokens.get(3),
                            new Location((Float.parseFloat(commandTokens.get(5))), Float.parseFloat(commandTokens.get(7))), Boolean.parseBoolean(commandTokens.get(9)),
                            true, Integer.parseInt(commandTokens.get(11)));
                    streetLight.setAccount(commandTokens.get(3));
                    model.defineDevice(streetLight, auth_token);
                    ledger.createAccount(streetLight.getAccount());
                    break;
                case "defineparking-space":
                    if (commandTokens.size() != 12) {
                        throw new CommandLineInterfaceException(cmd, "Invalid Number of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    ParkingSpace parking = new ParkingSpace(commandTokens.get(3),
                            new Location((Float.parseFloat(commandTokens.get(5))), Float.parseFloat(commandTokens.get(7))), Boolean.parseBoolean(commandTokens.get(9)),
                            true, true, Integer.parseInt(commandTokens.get(11)));
                    parking.setAccount(commandTokens.get(3));
                    model.defineDevice(parking, auth_token);
                    ledger.createAccount(parking.getAccount());
                    break;
                case "definerobot":
                    if (commandTokens.size() != 12) {
                        throw new CommandLineInterfaceException(cmd, "Invalid Number of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    Robot robot = new Robot(commandTokens.get(3),
                            new Location((Float.parseFloat(commandTokens.get(5))), Float.parseFloat(commandTokens.get(7))), Boolean.parseBoolean(commandTokens.get(9)),
                            true, commandTokens.get(11));
                    robot.setAccount(commandTokens.get(3));
                    model.defineDevice(robot, auth_token);
                    ledger.createAccount(robot.getAccount());
                    break;
                case "definevehicle":
                    if (commandTokens.size() != 18) {
                        throw new CommandLineInterfaceException(cmd, "Invalid Number of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    Vehicle vehicle = new Vehicle(commandTokens.get(3),
                            new Location((Float.parseFloat(commandTokens.get(5))), Float.parseFloat(commandTokens.get(7))), Boolean.parseBoolean(commandTokens.get(9)),
                            true, commandTokens.get(11), commandTokens.get(13), Integer.parseInt(commandTokens.get(15)), Integer.parseInt(commandTokens.get(17)));
                    vehicle.setAccount(commandTokens.get(3));
                    model.defineDevice(vehicle, auth_token);
                    ledger.createAccount(vehicle.getAccount());
                    ledger.processTransaction(new Transaction(Integer.toString(transaction_counter), 1000, 10,
                            "fund account", ledger.getCurrentBlock().getAccountBalanceMap().get("master"),
                            ledger.getCurrentBlock().getAccountBalanceMap().get(vehicle.getAccount())));
                    break;
                case "showdevice":
                    System.out.println(model.showDevice(commandTokens.get(3), auth_token));
                    break;
                // for update commands, user can provide arguments in any order and any amount
                // looping is used to determine which arguments were provided
                case "updatestreet-sign":
                    if (commandTokens.size() % 2 != 0) {
                        throw new CommandLineInterfaceException(cmd, "Invalid amount of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    StreetSign updateSign = new StreetSign();
                    updateSign.setDeviceID(commandTokens.get(3));
                    for (int i = 4; i < commandTokens.size(); i++) {
                        if (commandTokens.get(i).equals("text")) {
                            updateSign.setSignMessage(commandTokens.get(i + 1));
                        }
                        if (commandTokens.get(i).equals("lat")) {
                            updateSign.setLocation(new Location((Float.parseFloat(commandTokens.get(i + 1))), Float.parseFloat(commandTokens.get(i + 3))));
                        }
                        if (commandTokens.get(i).equals("enabled")) {
                            updateSign.setEnabled(Boolean.parseBoolean(commandTokens.get(i + 1)));
                        }
                        if (commandTokens.get(i).equals("status")) {
                            updateSign.setStatus(Boolean.parseBoolean(commandTokens.get(i + 1)));
                        }
                    }
                    model.updateDevice(updateSign, auth_token);
                    break;
                case "updateinfo-kiosk":
                    if (commandTokens.size() % 2 != 0) {
                        throw new CommandLineInterfaceException(cmd, "Invalid amount of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    InfoKiosk updateKiosk = new InfoKiosk();
                    updateKiosk.setDeviceID(commandTokens.get(3));
                    for (int i = 4; i < commandTokens.size(); i++) {
                        if (commandTokens.get(i).equals("image")) {
                            updateKiosk.setDisplayImage(commandTokens.get(i + 1));
                        }
                        if (commandTokens.get(i).equals("lat")) {
                            updateKiosk.setLocation(new Location((Float.parseFloat(commandTokens.get(i + 1))), Float.parseFloat(commandTokens.get(i + 3))));
                        }
                        if (commandTokens.get(i).equals("enabled")) {
                            updateKiosk.setEnabled(Boolean.parseBoolean(commandTokens.get(i + 1)));
                        }
                        if (commandTokens.get(i).equals("status")) {
                            updateKiosk.setStatus(Boolean.parseBoolean(commandTokens.get(i + 1)));
                        }
                    }
                    model.updateDevice(updateKiosk, auth_token);
                    break;
                case "updatestreet-light":
                    if (commandTokens.size() % 2 != 0) {
                        throw new CommandLineInterfaceException(cmd, "Invalid amount of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    StreetLight updateLight = new StreetLight();
                    updateLight.setDeviceID(commandTokens.get(3));
                    for (int i = 4; i < commandTokens.size(); i++) {
                        if (commandTokens.get(i).equals("brightness")) {
                            updateLight.setBrightnessLevel(Integer.parseInt(commandTokens.get(i + 1)));
                        }
                        if (commandTokens.get(i).equals("lat")) {
                            updateLight.setLocation(new Location((Float.parseFloat(commandTokens.get(i + 1))), Float.parseFloat(commandTokens.get(i + 3))));
                        }
                        if (commandTokens.get(i).equals("enabled")) {
                            updateLight.setEnabled(Boolean.parseBoolean(commandTokens.get(i + 1)));
                        }
                        if (commandTokens.get(i).equals("status")) {
                            updateLight.setStatus(Boolean.parseBoolean(commandTokens.get(i + 1)));
                        }
                    }
                    model.updateDevice(updateLight, auth_token);
                    break;
                case "updateparking-space":
                    if (commandTokens.size() % 2 != 0) {
                        throw new CommandLineInterfaceException(cmd, "Invalid amount of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    ParkingSpace updateParking = new ParkingSpace();
                    updateParking.setDeviceID(commandTokens.get(3));
                    // if rate will not be provided, this will ensure that rate will stay the same
                    updateParking.setHourlyRate(-100);
                    for (int i = 4; i < commandTokens.size(); i++) {
                        if (commandTokens.get(i).equals("rate")) {
                            updateParking.setHourlyRate(Integer.parseInt(commandTokens.get(i + 1)));
                        }
                        if (commandTokens.get(i).equals("enabled")) {
                            updateParking.setEnabled(Boolean.parseBoolean(commandTokens.get(i + 1)));
                        }
                        if (commandTokens.get(i).equals("status")) {
                            updateParking.setStatus(Boolean.parseBoolean(commandTokens.get(i + 1)));
                        }
                        if (commandTokens.get(i).equals("available")) {
                            updateParking.setAvailable(Boolean.parseBoolean(commandTokens.get(i + 1)));
                        }
                    }
                    model.updateDevice(updateParking, auth_token);
                    break;
                case "updaterobot":
                    if (commandTokens.size() % 2 != 0) {
                        throw new CommandLineInterfaceException(cmd, "Invalid amount of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    Robot updateRobot = new Robot();
                    updateRobot.setDeviceID(commandTokens.get(3));

                    for (int i = 4; i < commandTokens.size(); i++) {
                        if (commandTokens.get(i).equals("activity")) {
                            updateRobot.setActivity(commandTokens.get(i + 1));
                        }
                        if (commandTokens.get(i).equals("lat")) {
                            updateRobot.setLocation(new Location((Float.parseFloat(commandTokens.get(i + 1))), Float.parseFloat(commandTokens.get(i + 3))));
                        }
                        if (commandTokens.get(i).equals("enabled")) {
                            updateRobot.setEnabled(Boolean.parseBoolean(commandTokens.get(i + 1)));
                        }
                        if (commandTokens.get(i).equals("status")) {
                            updateRobot.setStatus(Boolean.parseBoolean(commandTokens.get(i + 1)));
                        }
                    }
                    model.updateDevice(updateRobot, auth_token);
                    break;
                case "updatevehicle":
                    if (commandTokens.size() % 2 != 0) {
                        throw new CommandLineInterfaceException(cmd, "Invalid amount of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    Vehicle updateVehicle = new Vehicle();
                    updateVehicle.setDeviceID(commandTokens.get(3));
                    updateVehicle.setFee(-100);
                    updateVehicle.setCapacity(-100);

                    for (int i = 4; i < commandTokens.size(); i++) {
                        if (commandTokens.get(i).equals("fee")) {
                            updateVehicle.setFee(Integer.parseInt(commandTokens.get(i + 1)));
                        }
                        if (commandTokens.get(i).equals("capacity")) {
                            updateVehicle.setCapacity(Integer.parseInt(commandTokens.get(i + 1)));
                        }
                        if (commandTokens.get(i).equals("lat")) {
                            updateVehicle.setLocation(new Location((Float.parseFloat(commandTokens.get(i + 1))), Float.parseFloat(commandTokens.get(i + 3))));
                        }
                        if (commandTokens.get(i).equals("enabled")) {
                            updateVehicle.setEnabled(Boolean.parseBoolean(commandTokens.get(i + 1)));
                        }
                        if (commandTokens.get(i).equals("status")) {
                            updateVehicle.setStatus(Boolean.parseBoolean(commandTokens.get(i + 1)));
                        }
                        if (commandTokens.get(i).equals("activity")) {
                            updateVehicle.setActivity(commandTokens.get(i + 1));
                        }
                    }
                    model.updateDevice(updateVehicle, auth_token);
                    break;
                case "createsensor-event":
                    if (commandTokens.size() != 8 && commandTokens.size() != 10) {
                        throw new CommandLineInterfaceException(cmd, "Invalid amount of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    Event event = new Event(IoT.SensorType.valueOf(commandTokens.get(5)), commandTokens.get(7));
                    if (commandTokens.size() == 10) {
                        event = new Event(IoT.SensorType.valueOf(commandTokens.get(5)), commandTokens.get(7), commandTokens.get(9));
                    }
                    model.createSensorEvent(commandTokens.get(3), event, controller_auth_token);
                    break;
                case "createsensor-output":
                    if (commandTokens.size() == 7) {
                        model.createSensorOutput("Command: create sensor-output\n" + commandTokens.get(3) + " " + commandTokens.get(6));
                    } else if (commandTokens.size() != 8 && commandTokens.size() != 10) {
                        throw new CommandLineInterfaceException(cmd, "Invalid amount of arguments", Integer.parseInt(commandTokens.get(0)));
                    } else {
                        Event eventOutput = new Event(IoT.SensorType.valueOf(commandTokens.get(5)), commandTokens.get(7));
                        System.out.println(model.createSensorOutput(commandTokens.get(3), eventOutput, auth_token));
                    }
                    break;
                case "defineresident":
                    if (commandTokens.size() != 18) {
                        throw new CommandLineInterfaceException(cmd, "Invalid Number of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    Resident resident = new Resident(commandTokens.get(3), commandTokens.get(5), commandTokens.get(7),
                            commandTokens.get(9), commandTokens.get(11), new Location((Float.parseFloat(commandTokens.get(13))), Float.parseFloat(commandTokens.get(15))), commandTokens.get(17));
                    model.definePerson(resident, auth_token);
                    ledger.createAccount(resident.getAccount());
                    ledger.processTransaction(new Transaction(Integer.toString(transaction_counter), 1000, 10,
                            "fund account", ledger.getCurrentBlock().getAccountBalanceMap().get("master"),
                            ledger.getCurrentBlock().getAccountBalanceMap().get(resident.getAccount())));
                    transaction_counter += 1;

                    break;
                case "updateresident":
                    if (commandTokens.size() % 2 != 0) {
                        throw new CommandLineInterfaceException(cmd, "Invalid amount of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    Resident resident1 = new Resident();
                    resident1.setID(commandTokens.get(3));
                    for (int i = 4; i < commandTokens.size(); i++) {
                        if (commandTokens.get(i).equals("name")) {
                            resident1.setName(commandTokens.get(i + 1));
                        }
                        if (commandTokens.get(i).equals("lat")) {
                            resident1.setLocation(new Location((Float.parseFloat(commandTokens.get(i + 1))), Float.parseFloat(commandTokens.get(i + 3))));
                        }
                        if (commandTokens.get(i).equals("phone")) {
                            resident1.setPhoneNum(commandTokens.get(i + 1));
                        }
                        if (commandTokens.get(i).equals("role")) {
                            resident1.setRole(commandTokens.get(i + 1));
                        }
                        if (commandTokens.get(i).equals("account")) {
                            resident1.setAccount(commandTokens.get(i + 1));
                        }
                    }
                    model.updatePerson(resident1, auth_token);
                    break;
                case "definevisitor":
                    if (commandTokens.size() != 10) {
                        System.out.println("size " + commandTokens.size());
                        throw new CommandLineInterfaceException(cmd, "Invalid Number of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    Visitor visitor = new Visitor(commandTokens.get(3), commandTokens.get(5),
                            new Location((Float.parseFloat(commandTokens.get(7))), Float.parseFloat(commandTokens.get(9))));
                    model.definePerson(visitor, auth_token);
                    break;
                case "updatevisitor":
                    if (commandTokens.size() % 2 != 0) {
                        throw new CommandLineInterfaceException(cmd, "Invalid amount of arguments", Integer.parseInt(commandTokens.get(0)));
                    }
                    Visitor visitor1 = new Visitor(commandTokens.get(3), null,
                            new Location((Float.parseFloat(commandTokens.get(5))), Float.parseFloat(commandTokens.get(7))));
                    model.updatePerson(visitor1, auth_token);
                    break;
                case "showperson":
                    System.out.println(model.showPerson(commandTokens.get(3), auth_token));
                    break;
                default:
                    throw new CommandLineInterfaceException(cmd, "Invalid Command", Integer.parseInt(commandTokens.get(0)));
            }
        } catch (SmartCityModelException e) {
            System.out.println("SmartCityModelException: " + e.getMessage());
        } catch (CommandLineInterfaceException e) {
            System.out.println("CommnadLineInterfaceException " + e.getMessage());
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | LedgerException | ClassNotFoundException |
                NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        } catch (InvalidAuthTokenException e) {
            System.out.println("InvalidAuthTokenException: " + e.getMessage());
        } catch (AccessDeniedException e) {
            System.out.println("AccessDeniedException: " + e.getMessage());
        } catch (AuthenticationException e) {
            System.out.println("AuthenticationException: " + e.getMessage());
        }
    }
}
