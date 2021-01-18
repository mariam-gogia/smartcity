package cscie97.smartcity.authentication;

import java.util.ArrayList;

/**
 * Class represents the roles that users may have
 * @author Mariam Gogia
 */
public class Role extends Privilege {
    ArrayList<Privilege> privilegeComponents = new ArrayList<Privilege>();

    public Role(String id, String name, String description) {
        super(id, name, description);
    }
    public void add(Privilege privilege) {
        privilegeComponents.add(privilege);
    }
    public void remove(Privilege privilege) {
        privilegeComponents.remove(privilege);
    }

    public ArrayList getPrivilegeComponents() {
        return privilegeComponents;
    }

    public Privilege getPrivilegeComponents(int i) {
        return privilegeComponents.get(i);
    }
}
