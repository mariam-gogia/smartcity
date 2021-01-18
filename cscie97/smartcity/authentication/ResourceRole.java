package cscie97.smartcity.authentication;

/**
 * Class represents resource roles that users may have
 * @author Mariam Gogia
 */
public class ResourceRole extends Role {
    private Resource resource;
    public ResourceRole(String id, String name, String description, Resource resource,
                        Role role) {
        super(id, name, description);
        this.resource = resource;
        this.privilegeComponents.add(role);
    }

    public Resource getResource() {
        return resource;
    }
}
