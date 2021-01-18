package cscie97.smartcity.authentication;

public interface Visitor {
    void visitPrivilege(Privilege privilege);
    void visitAuthToken(AuthToken authToken);
    void visitUser(User user);
    void visitResource(Resource resource);
    void visitAuthenticationModule(AuthenticationImpl authModule);
}
