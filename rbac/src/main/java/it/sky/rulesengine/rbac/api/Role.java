package it.sky.rulesengine.rbac.api;

import java.util.Map;
import java.util.Set;

/**
 * A role defines a set of capabilities, that is a set of permissions
 * on a given resource or operation.
 *
 * <p>For example, a role named "admin" could have the following capabilities:
 * <pre>
 *       RESOURCE    |       PERMISSIONS
 *     --------------------------------------------
 *      usersTable   | CREATE, READ, UPDATE, DELETE
 *      auditLogs    |         READ
 * </pre>
 *
 * @param <P> the permissions type
 */
public interface Role<P> {

//    /**
//     * Returns the role's permissions for the given resource.
//     *
//     * @return the permissions
//     */
//    Set<P> getPermissions(String resource);

    Map<String, Set<P>> getCapabilities();

}
