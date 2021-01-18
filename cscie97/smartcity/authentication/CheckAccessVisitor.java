package cscie97.smartcity.authentication;


/**
 * Visitor class used to check access of the user given the token
 * @author Mariam Gogia
 */
public class CheckAccessVisitor implements Visitor {
    private User user;
    private Resource resource = null;
    private AuthToken token;
    private Privilege permission;
    private boolean accessOK = false;

    @Override
    public void visitPrivilege(Privilege privilege) {
        if(permission.getId().equals(privilege.getId())){
            setAccessOK(true);
        }
        if(privilege.getClass().getName() == "cscie97.smartcity.authentication.Role") {
            Role role = (Role) privilege;
            for(Privilege user_permission : role.privilegeComponents) {
                if(user_permission.getId() == permission.getId()) {
                    setAccessOK(true);
                }
            }
        }
        if(privilege.getClass().getName() == "cscie97.smartcity.authentication.ResourceRole"){
            ResourceRole resRole = (ResourceRole) privilege;
            resRole.getResource().acceptVisitor(this);
        }
    }

    @Override
    public void visitAuthToken(AuthToken authToken) {
        if(user.getToken() == token) {
            for(Privilege privilege : user.getPrivileges()) {
                privilege.acceptVisitor(this);
            }
        }
    }

    @Override
    public void visitUser(User user) {
        this.setUser(user);
        token.acceptVisitor(this);
    }

    @Override
    public void visitResource(Resource resource) {
        if(this.resource != null && resource.getId().equals(this.resource.getId())){
            setAccessOK(true);
        }
    }

    @Override
    public void visitAuthenticationModule(AuthenticationImpl authModule) {
        if(token.getId().equals("super_admin")){
            User user = authModule.getUsers().get("super_admin");
            user.acceptVisitor(this);
        }
        else {
            authModule.getCurrentUser().acceptVisitor(this);
        }
    }

    // Getters & Setters
    public void setUser(User user) {
        this.user = user;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public boolean isAccessOK() {
        return accessOK;
    }

    public void setToken(AuthToken token) {
        this.token = token;
    }

    public void setPermission(Privilege permission) {
        this.permission = permission;
    }

    public void setAccessOK(boolean accessOK) {
        this.accessOK = accessOK;
    }

    public User getUser() {
        return user;
    }

    public AuthToken getToken() {
        return token;
    }

    public Privilege getPermission() {
        return permission;
    }
}
