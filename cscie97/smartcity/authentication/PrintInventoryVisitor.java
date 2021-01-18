package cscie97.smartcity.authentication;

import java.util.Map;

public class PrintInventoryVisitor implements Visitor{
    @Override
    public void visitPrivilege(Privilege privilege) {
        System.out.println("Privilege id: " + privilege.getId() + "\nName: " + privilege.getName() + "\nDescription: " +privilege.getDescription());
        // same logic(order) as in CheckAccessVisitor
        if(privilege.getClass().getName() == "cscie97.smartcity.authentication.Role") {
            Role role = (Role) privilege;
            for(Privilege user_permission : role.privilegeComponents) {
                user_permission.acceptVisitor(this);
            }
        }
        if(privilege.getClass().getName() == "cscie97.smartcity.authentication.ResourceRole"){
            ResourceRole resRole = (ResourceRole) privilege;
            resRole.getResource().acceptVisitor(this);
            for (Privilege resourceRole : resRole.privilegeComponents) {
                resourceRole.acceptVisitor(this);
            }
        }
    }

    @Override
    public void visitAuthToken(AuthToken authToken) {
        authToken.toString();
    }

    @Override
    public void visitUser(User user) {
        System.out.println("User id: " + user.getId() + "\nName: " + user.getName());
        if (user.getToken() != null) {
            user.getToken().acceptVisitor(this);
        }
        for(Privilege p : user.getPrivileges()) {
            p.acceptVisitor(this);
        }
    }

    @Override
    public void visitResource(Resource resource) {
        System.out.println("Resource id: " + resource.getId() + "\nDescription: " + resource.getDescription());
    }

    @Override
    public void visitAuthenticationModule(AuthenticationImpl authModule) {
        System.out.println("Instance of the Authentication Module");
        for (Map.Entry<String, User> user : authModule.getUsers().entrySet()) {
            user.getValue().acceptVisitor(this);
        }
    }
}
