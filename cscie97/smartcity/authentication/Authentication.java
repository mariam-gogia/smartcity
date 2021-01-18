package cscie97.smartcity.authentication;

import java.security.NoSuchAlgorithmException;

public interface Authentication {
    void createResource(String id,  String description, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException;
    void createUser(String id, String name, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException;
    void createPermission(String id, String name, String description, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException;
    void createRole(String id, String name, String description, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException;
    void createResourceRole(String id, String name, String description, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException;
    AuthToken login(String userId, String credentials) throws AuthenticationException, NoSuchAlgorithmException, AccessDeniedException;
    void logout(AuthToken token);
    boolean checkAccess(AuthToken auth_token, String permissionID, String resourceID) throws AccessDeniedException, AuthenticationException, InvalidAuthTokenException;
    void addCredentials(String id, String credentials, AuthToken token) throws AuthenticationException, NoSuchAlgorithmException, InvalidAuthTokenException, AccessDeniedException;
    void addPermissionToRole(String roleID, String permissionID, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException;
    void addRoleToUser(String id, String roleId, AuthToken token) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException;

}
