package cscie97.smartcity.authentication;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Module API
 * @author Mariam Gogia
 */
public class AuthenticationImpl implements Authentication, Visitable {
    private final Map<String, String> userCredentials = new HashMap<>();
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, Resource> resources = new HashMap<>();
    private final Map<String, Privilege> privileges = new HashMap<>();
    // currently logged in user
    private User currentUser;

    private static AuthenticationImpl authenticationSingleton = new AuthenticationImpl();
    /**
     * Authentication constructor
     */
    private AuthenticationImpl() {}

    /** Singleton Pattern support
     * @return AuthenticationImpl instance
     */
    public static AuthenticationImpl getInstance() {
        return authenticationSingleton;
    }

    /**
     *
     * @param id - resource id
     * @param description - resource description
     * @param token - auth token
     * @throws AuthenticationException
     * @throws InvalidAuthTokenException
     * @throws AccessDeniedException
     */
    @Override
    public void createResource(String id,  String description, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException {
        if(!checkToken(token)){
            throw new InvalidAuthTokenException("createResource", "expired token");
        }
        if(!checkAccess(token, "auth_resource_admin", "")){
            throw new AccessDeniedException("createResource - checkAccess", "access denied");
        }
        if (!resources.containsKey(id)) {
            Resource resource = new Resource(id, description);
            resources.put(id, resource);
            System.out.println("Resource " + id + " defined");
        } else {
            throw new AuthenticationException("createResource", "Resource" + id+ "already exists");
        }
    }

    /**
     * Defines user in the module
     * @param id - user id
     * @param name - user name
     * @param token - auth token
     * @throws AuthenticationException
     * @throws InvalidAuthTokenException
     * @throws AccessDeniedException
     */
    @Override
    public void createUser(String id, String name, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException {
        if(!checkToken(token)){
            throw new InvalidAuthTokenException("createUser", "expired token");
        }
        if(!checkAccess(token, "auth_user_admin", "")){
            throw new AccessDeniedException("createUser - checkAccess", "access denied");
        }
        if (!userCredentials.containsKey(id)) {
            User user = new User(id, name);
            userCredentials.put(id, "");
            users.put(id, user);
            System.out.println("User " + id + " defined");
        }
        else {
            throw new AuthenticationException("createUser", "User "+id +" already exists");
        }
    }

    /**
     * Defines Permission in the module
     * @param id - permission id
     * @param name - permission name
     * @param description - permission description
     * @param token - auth token
     * @throws AuthenticationException
     * @throws InvalidAuthTokenException
     * @throws AccessDeniedException
     */
    @Override
    public void createPermission(String id, String name, String description, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException {
        if(!checkToken(token)){
            throw new InvalidAuthTokenException("createPermission", "expired token");
        }
        if(!checkAccess(token, "auth_role_entitlement_admin", "")){
            throw new AccessDeniedException("createPermission - checkAccess", "access denied");
        }
        if(!privileges.containsKey(id)) {
            Permission permission = new Permission(id, name, description);
            privileges.put(id, permission);
            System.out.println("Permission " + id + " defined");
        }
        else {
            throw new AuthenticationException("createPermission", "Permission "+id+" Already exists");
        }
    }

    /**
     * Defines the role
     * @param id - role id
     * @param name - role name
     * @param description - role description
     * @param token - auth token
     * @throws AuthenticationException
     * @throws InvalidAuthTokenException
     * @throws AccessDeniedException
     */
    @Override
    public void createRole(String id, String name, String description, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException {
        if(!checkToken(token)){
            throw new InvalidAuthTokenException("createRole", "expired token");
        }
        if(!checkAccess(token, "auth_role_entitlement_admin", "")){
            throw new AccessDeniedException("createRole - checkAccess", "access denied");
        }
        if(!privileges.containsKey(id)) {
            Role role = new Role(id, name, description);
            privileges.put(id, role);
            System.out.println("Role " + id + " defined");
        }
        else {
            throw new AuthenticationException("createRole", "Role "+id+" Already exists");
        }
    }

    /**
     * Defines resource role in the module
     * @param id - resource role id
     * @param roleID - role id
     * @param resourceID - resource id
     * @param token - auth token
     * @throws AuthenticationException
     * @throws InvalidAuthTokenException
     * @throws AccessDeniedException
     */
    @Override
    public void createResourceRole(String id, String roleID, String resourceID, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException {
        if(!checkToken(token)){
            throw new InvalidAuthTokenException("createResourceRole", "expired token");
        }
        if(!checkAccess(token, "auth_role_entitlement_admin", "")){
            throw new AccessDeniedException("createResourceRole - checkAccess", "access denied");
        }
        if(!resources.containsKey(resourceID)) {
            throw new AuthenticationException("createResourceRole " + resourceID, "Such resource does not exist");
        }
        else if(!privileges.containsKey(roleID)) {
            throw new AuthenticationException("createResourceRole " +roleID, "Such role does not exist");
        }
        Role role = (Role)privileges.get(roleID);
        Resource resource = resources.get(resourceID);
        String resourceRoleName = role.getName() + "-" + resourceID;
        String description = "resource role for " +  resourceID;
        ResourceRole resourceRole = new ResourceRole(id, resourceRoleName, description, resource, role);
        System.out.println("ResourceRole " + id + " defined");
        privileges.put(id, resourceRole);

    }

    /**
     * add the hashed credentials to the user
     * @param id - user id
     * @param credentials - provided credentials
     * @param token - auth token
     * @throws AuthenticationException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAuthTokenException
     * @throws AccessDeniedException
     */
    @Override
    public void addCredentials(String id, String credentials, AuthToken token) throws AuthenticationException, NoSuchAlgorithmException, InvalidAuthTokenException, AccessDeniedException {
        if(!checkToken(token)){
            throw new InvalidAuthTokenException("addCredentials", "expired token");
        }
        if(!checkAccess(token, "auth_user_admin", "")){
            throw new AccessDeniedException("addCredentials - checkAccess", "access denied");
        }
        if(users.containsKey(id)) {
            String hash_credentials = computeHash(id, credentials);
            users.get(id).addCredential(hash_credentials);
            userCredentials.replace(id, "", hash_credentials);
            System.out.println("Credentials to " + id + " added");
        }
        else {
            throw new AuthenticationException("addCredentials " + id, "No such user");
        }
    }

    /**
     * Adds permission to specific role
     * @param roleID - role id
     * @param permissionID - permission id
     * @param token - auth token
     * @throws AuthenticationException
     * @throws InvalidAuthTokenException
     * @throws AccessDeniedException
     */
    @Override
    public void addPermissionToRole(String roleID, String permissionID, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException {
        if(!checkToken(token)){
            throw new InvalidAuthTokenException("addPermissionToRole", "expired token");
        }
        if(!checkAccess(token, "auth_role_entitlement_admin", "")){
            throw new AccessDeniedException("checkAccess", "access denied");
        }
        if (privileges.containsKey(roleID) && privileges.containsKey(permissionID)) {
                Role role = (Role) privileges.get(roleID);
                role.add(privileges.get(permissionID));
                System.out.println("Permission " + permissionID +  " to " + roleID + " added");
            }
        if(!privileges.containsKey(roleID)) {
            throw new AuthenticationException("addPermissionToRole role: " +roleID, "Role does not exist");
        }
        if(!privileges.containsKey(permissionID)) {
            throw new AuthenticationException("addPermissionToRole role: " +permissionID, "Permission does not exist");
        }
    }

    /**
     * Adds role to specific user
     * @param id - user ID
     * @param roleId - role ID
     * @param token - Auth token
     * @throws AuthenticationException
     * @throws InvalidAuthTokenException
     * @throws AccessDeniedException
     */
    @Override
    public void addRoleToUser(String id, String roleId, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException {
        if(!checkToken(token)){
            throw new InvalidAuthTokenException("addRoletoUser", "expired token");
        }
        if(!checkAccess(token, "auth_user_admin", "")){
            throw new AccessDeniedException("checkAccess", "access denied");
        }

        if(!users.containsKey(id)) {
            throw new AuthenticationException("addRoleToUser " + id, "user does not exist");
        }
        if(!privileges.containsKey(roleId)) {
            throw new AuthenticationException("addRoleToUser " + roleId, "role does not exist");
        }
        User user = users.get(id);
        Role role = (Role)privileges.get(roleId);
        user.addPrivilege(role);
        System.out.println("Role " + roleId +  " to " + id + " added");
    }

    /**
     * Controller admin uses super_admin's token
     * @return
     */
    public AuthToken controller_login() {
        return users.get("super_admin").getToken();
    }

    /**
     * login - if super_admin credentials are provided, super_admin privileges and the role
     * is initialized and super admin token is issued. Otherwise, it processes user log in.
     * @param userId
     * @param credentials
     * @return
     * @throws AuthenticationException
     * @throws NoSuchAlgorithmException
     * @throws AccessDeniedException
     */
    @Override
    public AuthToken login(String userId, String credentials) throws AuthenticationException, NoSuchAlgorithmException, AccessDeniedException {
        if(userId.equals("super_admin") && credentials.equals("initializeservice")) {
            User admin = new User(userId, "admin");
            Permission admin_perm_1 = new Permission("auth_user_admin", "User Administrator", "Create, Update, Delete Users");
            Permission admin_perm_2 = new Permission("auth_role_entitlement_admin", "User Administrator" ,"Create, Update, Delete Entitlements");
            Permission admin_perm_3 = new Permission("auth_resource_admin", "User Administrator" ,"Create, Update, Delete Resources");
            Permission admin_perm_4 = new Permission("scms_simulate_event", "Simulate event" ,"Simulate events");
            Permission admin_perm_5 = new Permission("scms_charge_users", "Charge accounts" ,"Charge ledger accounts");

            Role admin_role = new Role("admin_role","Admin Role", "Has all permissions of an administrator");
            admin_role.add(admin_perm_1);
            admin_role.add(admin_perm_2);
            admin_role.add(admin_perm_3);
            admin_role.add(admin_perm_4);
            admin_role.add(admin_perm_5);
            admin.addPrivilege(admin_role);

            AuthToken admin_token = new AuthToken(userId);
            admin_token.setTime(LocalDateTime.now());
            admin.setToken(admin_token);
            users.put(userId, admin);
            privileges.put("auth_user_admin", admin_perm_1);
            privileges.put("auth_role_entitlement_admin", admin_perm_2);
            privileges.put("auth_resource_admin", admin_perm_3);
            privileges.put("scms_simulate_event", admin_perm_4);
            privileges.put("scms_charge_users", admin_perm_5);
            privileges.put("admin_role", admin_role);

            System.out.println("Super admin login successful");
            currentUser = admin;
            return admin_token;
        }
        User user = users.get(userId);
        String hashed_credentials = computeHash(userId, credentials);

        if (user == null) {
            throw new AuthenticationException("login", "Such user does not exist");
        } else if (!user.getCredentials().contains(hashed_credentials)) {
            throw new AccessDeniedException("login", "invalid credentials");
        } else {
            AuthToken token = new AuthToken(userId + "-" + "token: " + LocalDateTime.now());
            token.setTime(LocalDateTime.now());
            user.setToken(token);
            System.out.println(userId + " login successful");
            currentUser = user;
            return token;
        }
    }

    /**
     * Prints inventory of all Privileges, Users and Resources
     * @param token
     * @throws AuthenticationException
     */
    public void printInventory(AuthToken token) throws AuthenticationException {
        if(checkToken(token)) {
            PrintInventoryVisitor visitor = new PrintInventoryVisitor();
            this.acceptVisitor(visitor);
        }
    }

    /**
     * Checks validity of the token
     * @param token - auth_token
     * @return
     */
    private boolean checkToken(AuthToken token){
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime timeToken = token.getTime();
        int t1 = timeNow.getHour()*60 +timeNow.getMinute();
        int t2 = timeToken.getHour()*60 +timeToken.getMinute();
        // if the token has been active for more than 60minutes, expire it
        if(t1-t2 > 60) {
            return false;
        }
        return true;
    }

    /**
     * logsout user
     * @param token
     */
    @Override
    public void logout(AuthToken token) {
        token.setActive(false);
        System.out.println(currentUser.getId() + " logged out");
    }

    /**
     * Using the visitor pattern iteration, it checks whether the logged in user is authorized
     * to execute requested method
     * @param auth_token - token
     * @param permissionId - permission id
     * @param resourceID - it is empty if there is no request to check access to the resource
     * @return accessOK
     * @throws AccessDeniedException
     * @throws AuthenticationException
     * @throws InvalidAuthTokenException
     */
    @Override
    public boolean checkAccess(AuthToken auth_token, String permissionId, String resourceID) throws AccessDeniedException, AuthenticationException, InvalidAuthTokenException {
        if (!auth_token.isActive()){
            throw new InvalidAuthTokenException("checkAccess", "Expired token");
        }
        Permission permission = (Permission) privileges.get(permissionId);
        CheckAccessVisitor visitor = new CheckAccessVisitor();
        visitor.setPermission(permission);
        visitor.setToken(auth_token);

        if(resourceID!="" && resources.containsKey(resourceID)){
            visitor.setResource(resources.get(resourceID));
        }
        if(resourceID!="" && !resources.containsKey(resourceID)){
            throw new AuthenticationException("Resource " + resourceID, "is not defined");
        }
        this.acceptVisitor(visitor);
        if (visitor.isAccessOK()) {
            return true;
        }
        return false;
    }

    /**
     * Method to check privileges of the user who IS NOT logged in
     * and therefore has no token
     * @param permissionId
     * @param userID
     * @return access OK
     */
    public boolean checkAccess(String permissionId, String userID){
        User user = users.get(userID);
        if(user == null) {
            // such user does not exist exception
            return false;
        }
        for(Privilege p : user.getPrivileges()) {
            Role role = (Role) p;
            for (int i = 0; i < role.getPrivilegeComponents().size(); i++) {
                if(role.getPrivilegeComponents(i).getId().equals(permissionId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Computes hash for credentials
     * @param id - userID
     * @param credentials - user credentials
     * @return
     * @throws NoSuchAlgorithmException
     */
    private String computeHash(String id, String credentials) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(id.getBytes(StandardCharsets.UTF_8));
        byte[] hashArray = digest.digest(credentials.getBytes());

        // converting to Hex String
        BigInteger num = new BigInteger(1, hashArray);
        StringBuilder hex = new StringBuilder(num.toString());
        while(hex.length() < 32) {
            hex.insert(0,'0');
        }
        return hex.toString();
    }

    @Override
    public void acceptVisitor(Visitor visitor) {
        visitor.visitAuthenticationModule(this);
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
