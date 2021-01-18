package cscie97.smartcity.authentication;

/**
 * Class represents the permission objects of the module
 * @author Mariam Gogia
 */
public class Permission extends Privilege {
    public Permission(String id, String name, String description) {
        super(id, name, description);
    }

    @Override
    void add(Privilege privilege) { }

    @Override
    void remove(Privilege privilege) {}

}
