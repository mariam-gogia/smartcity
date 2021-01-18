package cscie97.smartcity.model;
import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import cscie97.smartcity.authentication.*;
import cscie97.smartcity.controller.Controller;
import cscie97.smartcity.controller.Observer;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

/**
 * SmartCityModelImpl implements the SmartCityModelInterface and provides an API for the client
 * @author Mariam Gogia
 */
public class SmartCityModelImpl implements SmartCityModelInterface, Subject {
    // Associations:

    // Map uniqueID:Person
    private final Map<String, Person> peopleIDMap = new HashMap<>();
    // Map city_unique_ID:City
    private final Map<String, City> cityList = new HashMap<>();
    // Map City : <Map deviceID:device>
    private final Map<City, Map<String, IoT>> cityIoTMap = new HashMap<>();
    // Map deviceID:device
    private final Map<String, IoT> IoTMap = new HashMap<>();

    // list to register/deregister observers
    private final ArrayList<Observer> observers = new ArrayList<>();

    private static SmartCityModelImpl modelSingleton = new SmartCityModelImpl();

    /**
     * Smart City constructor
     */
    private SmartCityModelImpl() {
    }

    public static SmartCityModelImpl getInstance() {
        return modelSingleton;
    }

    /** Checks the validity of the given city and adds to appropriate map objects
     * @param city city to be defined
     * @param auth_token to be implemented
     * @throws SmartCityModelException
     */
    @Override
    public void defineCity(City city, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (!checkAccess(auth_token, "scms_manage_city", city.getCityID())) {
            throw new AccessDeniedException("checkAccess - defineCity", "access denied");
        } else {
            // if such city already exists, it cannot be defined
            if (!cityList.isEmpty() && cityList.containsKey(city.getCityID())) {
                throw new SmartCityModelException("City " + city.getCityID() + " cannot be defined", "The city already exists");
            }
            // add the city to cityList and city IoT Map
            cityList.put(city.getCityID(), city);
            Map<String, IoT> IoTmap = new HashMap<>(); // for each city, new IoTmap is created (deviceID:device)
            cityIoTMap.put(city, IoTmap);
            System.out.println("Command: define-city " + city.getCityID() + " successful");
        }
    }

    /** Show the details of the city, its people, and devices
     * @param cityID unique id of the city to be shown
     * @param auth_token to be implemented
     * @return String containing the details of the city, its current inhabitants, and its devices
     */
    @Override
    public String showCity(String cityID, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (!checkAccess(auth_token, "scms_manage_city", cityID)) {
            throw new AccessDeniedException("checkAccess - showCity", "access denied");
        } else {
            System.out.println("Command: show city " + cityID);
            // Retrieve the city from the city list
            City city = cityList.get(cityID);
            if (city == null) {
                throw new SmartCityModelException("showCity " + cityID, "Such city does not exist");
            }

            // the objects to store city's people and IoTs
            ArrayList city_people = new ArrayList();
            ArrayList city_devices = new ArrayList();

            // Iterate through peoples and devices map and find the objects within the city by using
            // the 'distance' method from the Location class
            if (!peopleIDMap.isEmpty()) {
                for (Map.Entry<String, Person> entry : peopleIDMap.entrySet()) {
                    if (Location.distance(city.getLocation(), entry.getValue().getLocation()) < city.getRadius()) {
                        city_people.add("\n" + entry.getValue());
                    }
                }
            }
            if (!IoTMap.isEmpty()) {
                for (Map.Entry<String, IoT> entry : IoTMap.entrySet()) {
                    if (Location.distance(city.getLocation(), entry.getValue().getLocation()) < city.getRadius()) {
                        city_devices.add("\n" + entry.getValue());
                    }
                }
            }
            // print the city details and the results of 2 arrays created above
            return "[" + city.toString() + "\nThere are following devices in the city: " + Arrays.toString(city_devices.toArray()) +
                    "\nThere are following people in the city" + Arrays.toString(city_people.toArray()) + "\n";
        }
    }

    /** Validate the given person object and add it to the appropriate map object
     * @param person A person to be defined
     * @param auth_token to be implemented
     * @throws SmartCityModelException
     */
    @Override
    public void definePerson(Person person, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (!checkAccess(auth_token, "scms_manage_person", "")) {
            throw new AccessDeniedException("checkAccess - definePerson", "access denied");
        } else {
            // if allPeopleMap is not empty and the person with the same biometrics and the id already exists, throw exception
            if (!peopleIDMap.isEmpty() && peopleIDMap.containsValue(person.getBiometricID()) && peopleIDMap.containsValue(person.getID())) {
                throw new SmartCityModelException("Person " + person.getID() + "cannot be defined", "The person already exists");
            }
            peopleIDMap.put(person.getID(), person);
            System.out.println("Command: define-person " + person.getID() + " successful");
        }
    }

    /** Given the ID, look for a person in the map, if found, return his/her details
     * @param id person's unique ID
     * @param auth_token to be implemented
     * @return details of the person
     * @throws SmartCityModelException
     */
    @Override
    public String showPerson(String id, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (!checkAccess(auth_token, "scms_manage_person", "")) {
            throw new AccessDeniedException("checkAccess - showPerson", "access denied");
        } else {
            System.out.println("Command: show-person " + id);
            if (!peopleIDMap.isEmpty() && peopleIDMap.containsKey(id)) {
                return peopleIDMap.get(id).toString();
            }
            throw new SmartCityModelException("showPerson " + id, "Person cannot be found");
        }
    }

    /** Updates the user-indicated details of an existing person
     * @param person a person object containing the details to be copied to already existing person
     * @param auth_token to be implemented
     * @throws SmartCityModelException
     */
    @Override
    public void updatePerson(Person person, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (!checkAccess(auth_token, "scms_manage_person", "")) {
            throw new AccessDeniedException("checkAccess - updatePerson", "access denied");
        } else {
            if (!peopleIDMap.containsKey(person.getID())) {
                throw new SmartCityModelException("updatePerson " + person.getID(), "Such person does not exist");
            }
            // retrieve a person to be updated
            Person personToBeUpdated = peopleIDMap.get(person.getID());
            // if passed in location is not null, then the new locations has been provided and since
            // the location is the field both Visitor and Resident share - update it in Person
            if (person.getLocation() != null) {
                personToBeUpdated.setLocation(person.getLocation());
                System.out.println("Command: update-person " + person.getID() + " successful");
            }
            // If the Person's class is Resident, update its Resident relavant fields
            else if (person.getClass().getName() == "cscie97.smartcity.model.Resident") {
                // Cast both, passed in person and personToBeUpdated to resident and update fields

                if (((Resident) person).getName() != ((Resident) personToBeUpdated).getName()) {
                    ((Resident) personToBeUpdated).setName(((Resident) person).getName());
                }
                // if the person changed the role
                if (((Resident) person).getRole() != null) {
                    ((Resident) personToBeUpdated).setRole(((Resident) person).getRole());
                }
                // if the person changed the phone number, set it to new phone number
                if (((Resident) person).getPhoneNum() != null) {
                    ((Resident) personToBeUpdated).setPhoneNum(((Resident) person).getPhoneNum());
                }
                if (((Resident) person).getAccount() != null) {
                    ((Resident) personToBeUpdated).setAccount(((Resident) person).getAccount());
                }
                System.out.println("Command: update-person " + person.getID() + " successful");
            }
        }
    }

    /** Checks the validity of the device and adds it to the system
     * @param device IoT device to be defined
     * @param auth_token to be implemented
     * @throws SmartCityModelException
     */
    @Override
    public void defineDevice(IoT device, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (!checkAccess(auth_token, "scms_manage_device", "")) {
            throw new AccessDeniedException("checkAccess - defineDevice", "access denied");
        } else {
            // break the <cityID>:<deviceID> structure to cityID and deviceID
            String[] city_device_pair = device.getDeviceID().split(":", 3);

            if (!IoTMap.isEmpty() && IoTMap.containsKey(device.getDeviceID())) {
                throw new SmartCityModelException("Device " + device.getDeviceID() + "cannot be defined", "The device already exists");
            } // check if the passed in city is valid
            else if (!cityList.containsKey(city_device_pair[0])) {
                throw new SmartCityModelException("Device " + device.getDeviceID() + " cannot be defined in a given city", "City does not exist");
            } else {
                Map<String, IoT> temp = cityIoTMap.get(cityList.get(city_device_pair[0])); //retrieve the map from cityIoTMAp
                temp.put(device.getDeviceID(), device); // update the map
                cityIoTMap.put(cityList.get(city_device_pair[0]), temp); // put the map of deviceID:devices in appropriate city
                IoTMap.put(device.getDeviceID(), device); // put the device in IoTMap
                System.out.println("Command: define-device " + device.getDeviceID() + " successful");
            }
        }
    }

    /** Looks for the device in the map and if found, prints the details, if only the cityID is provided
     * It prints the details of all the devices in the city
     * @param deviceID unique ID of the device to be shown
     * @param auth_token to be implemented
     * @return A string containing the details of the device
     * @throws SmartCityModelException
     */
    @Override
    public String showDevice(String deviceID, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (!checkAccess(auth_token, "scms_manage_device", "")) {
            throw new AccessDeniedException("checkAccess - defineDevice", "access denied");
        } else {
            System.out.println("Command: show-device " + deviceID);
            String[] city_device_pair = deviceID.split(":", 3);

            // if the deviceID is not provided, show the list of devices assigned to the city
            if ((city_device_pair.length < 2) && !cityList.isEmpty() && cityList.containsKey(city_device_pair[0])) {
                Map<String, IoT> temp = cityIoTMap.get(cityList.get(city_device_pair[0]));
                String str = "The list of devices for " + city_device_pair[0];
                for (Map.Entry<String, IoT> entry : temp.entrySet()) {
                    str += ("\n" + entry.getValue().toString());
                }
                return "--------------------------------------\n"
                        + str + "\n--------------------------------------";
            } else if (IoTMap.containsKey(deviceID)) {
                return cityIoTMap.get(cityList.get(city_device_pair[0])).get(deviceID).toString() + "\n";
            } else {
                throw new SmartCityModelException("showDevice: " + deviceID, "Device or city-device pair does not exist");
            }
        }
    }

    /** Looks for the device and updates its modifiable fields
     * @param device temp device object who's field values to be copied to already existing device's field values
     * @param auth_token to be implemented
     * @throws SmartCityModelException
     */
    @Override
    public void updateDevice(IoT device, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (!checkAccess(auth_token, "scms_manage_device", "")) {
            throw new AccessDeniedException("checkAccess - updateDevice", "access denied");
        } else {
            // splitting the <cityID>:<deviceID> structure
            String[] city_device_pair = device.getDeviceID().split(":", 3);

            if (!IoTMap.containsKey(device.getDeviceID())) {
                throw new SmartCityModelException("updateDevice: ", "Such device or city does not exist");
            }
            // Retrieve the IoT object given the cityID and the deviceID
            IoT deviceToBeUpdated = cityIoTMap.get(cityList.get(city_device_pair[0])).get(device.getDeviceID());
            IoT deviceToBeUpdatedIoTMap = IoTMap.get(device.getDeviceID());

            // change the object in IoTMap too
            deviceToBeUpdatedIoTMap = deviceToBeUpdated;

            // If the object is null, such cityID:DeviceID pair does not exist
            if (deviceToBeUpdated == null) {
                throw new SmartCityModelException("updateDevice: " + device.getDeviceID(), "No such city:device pair");
            }
            // If the device's location field is not null, then the new location has been provided, so update it.
            // Updating all the common fields in IoT class
            if (device.getLocation() != null) {
                deviceToBeUpdated.setLocation(device.getLocation());
            }
            deviceToBeUpdated.setEnabled(device.isEnabled());
            deviceToBeUpdated.setStatus(device.isStatus());

            // determining which class the device belongs to and updating the class specific fields
            String className = device.getClass().getName();
            if (className == "cscie97.smartcity.model.StreetSign" && ((StreetSign) device).getSignMessage() != null) {
                ((StreetSign) deviceToBeUpdated).setSignMessage(((StreetSign) device).getSignMessage());
            } else if (className == "cscie97.smartcity.model.InfoKiosk" && ((InfoKiosk) device).getDisplayImage() != null) {
                ((InfoKiosk) deviceToBeUpdated).setDisplayImage(((InfoKiosk) device).getDisplayImage());
            } else if (className == "cscie97.smartcity.model.StreetLight" && ((StreetLight) device).getBrightnessLevel() != -100) {
                ((StreetLight) deviceToBeUpdated).setBrightnessLevel(((StreetLight) device).getBrightnessLevel());
            } else if (className == "cscie97.smartcity.model.ParkingSpace") {
                // update the is available if they differ
                if (((ParkingSpace) device).isAvailable() != (((ParkingSpace) deviceToBeUpdated).isAvailable())) {
                    ((ParkingSpace) deviceToBeUpdated).setAvailable(((ParkingSpace) device).isAvailable());
                }
                // if hourly rate has been provided (is not -100) then update the rate
                if (((ParkingSpace) device).getHourlyRate() != -100) {
                    ((ParkingSpace) deviceToBeUpdated).setHourlyRate(((ParkingSpace) device).getHourlyRate());
                }
            } else if (className == "cscie97.smartcity.model.Robot" && ((Robot) device).getActivity() != null) {
                ((Robot) deviceToBeUpdated).setActivity(((Robot) device).getActivity());
            } else if (className == "cscie97.smartcity.model.Vehicle") {
                if (((Vehicle) device).getActivity() != null) {
                    ((Vehicle) deviceToBeUpdated).setActivity(((Vehicle) device).getActivity());
                }
                if (((Vehicle) device).getCapacity() != -100) {
                    ((Vehicle) deviceToBeUpdated).setCapacity(((Vehicle) device).getCapacity());
                }
                if (((Vehicle) device).getFee() != -100) {
                    ((Vehicle) deviceToBeUpdated).setFee(((Vehicle) device).getFee());
                }
            }
            System.out.println("Command: update-device " + device.getDeviceID());
        }
    }

    /** Simulates the event for IoT sensors
     * @param deviceID the device a sensor event is directed to
     * @param event the event containing sensor type, action, and subject (optional)
     * @param auth_token
     * @throws SmartCityModelException
     */
    @Override
    public void createSensorEvent(String deviceID, Event event, AuthToken auth_token) throws SmartCityModelException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, LedgerException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (!checkAccess(auth_token, "scms_simulate_event", "")) {
            throw new AccessDeniedException("checkAccess - createSensorEvent", "access denied");
        } else {
            String[] city_device_pair = deviceID.split(":", 3);
            IoT device = cityIoTMap.get(cityList.get(city_device_pair[0])).get(deviceID); // look for the device
            if (device == null) {
                throw new SmartCityModelException("createSensorEvent", "No such city:device pair");
            }
            // if the event is directed/coming from a subject that does not exist, invalidate it.
            if (event.getSubject() != null && !peopleIDMap.containsKey(event.getSubject())) {
                throw new SmartCityModelException("createSensorEvent", "No such subject");
            }
            device.setLatestEvent(event);
            event.setDevice(device);
            notify(event, auth_token);
        }
    }

    /** Simulates the IoT speker output
     * @param deviceID deviceID the sensor output should come from
     * @param event the event the device should perform
     * @param auth_token to be implemented
     * @return
     * @throws SmartCityModelException
     */
    @Override
    public String createSensorOutput(String deviceID, Event event, AuthToken auth_token) throws SmartCityModelException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (!checkAccess(auth_token, "scms_manage_device", "")) {
            throw new AccessDeniedException("checkAccess - updateDevice", "access denied");
        } else {
            String[] city_device_pair = deviceID.split(":", 3);
            if (city_device_pair.length == 2 && !IoTMap.containsKey(deviceID)) {
                throw new SmartCityModelException("createSensorOutput", "No such device");
            } else {
                event.setDevice(IoTMap.get(deviceID));
                System.out.println("Command: create sensor-output " + deviceID + " successful");
                return event.toString();
            }
        }
    }
    public void createSensorOutput(String str) {
        System.out.println(str);
    }

    /**
     * Update issued for all observers in the list
     * @param event
     */
    @Override
    public void notify(Event event, AuthToken token) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, LedgerException, AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        // it is understood that currently we only have 1 observer
        // however, I built the system to support many
        for(Observer observer : observers) {
            observer.update(event, token);
        }
    }

    /**
     * Remove the observer from observers list
     * @param observer
     */
    @Override
    public void register(Observer observer) {
        observers.add(observer);
    }

    /**
     * Add the observer to observers list
     * @param observer
     */
    @Override
    public void deregister(Observer observer) {
        observers.remove(observer);
    }

    private boolean checkAccess(AuthToken token, String permissionID, String resourceID) throws AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        return AuthenticationImpl.getInstance().checkAccess(token,permissionID, resourceID);
    }

    public Map<String, IoT> getIoTMap() {
        return IoTMap;
    }

    public Map<String, Person> getPeopleIDMap() {
        return peopleIDMap;
    }

    public Map<String, City> getCityList() {
        return cityList;
    }
}
