package cscie97.smartcity.authentication;

import java.util.HashSet;

/**
 * Class represents the users in the module
 * @author Mariam Gogia
 */
public class User implements Visitable{
    // Attributes
    private String id;
    private String name;
    private AuthToken token;
    private HashSet<String> credentials = new HashSet<>();
    private HashSet<Privilege> privileges =  new HashSet<>();

    // User tokens.
    /** User constructor
     * @param id - unique identifier
     * @param name - the nme
     */
    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }
    @Override
    public void acceptVisitor(Visitor visitor) {
        visitor.visitUser(this);
    }
    public void addCredential(String credential) {
        credentials.add(credential);
    }

    public void addPrivilege(Privilege privilege) {
        privileges.add(privilege);
    }

    public HashSet<String> getCredentials() {
        return credentials;
    }

    public HashSet<Privilege> getPrivileges() {
        return privileges;
    }

    // Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public AuthToken getToken() {
        return token;
    }

    public void setToken(AuthToken token) {
        this.token = token;
    }

    public void setName(String name) {
        this.name = name;
    }
}
