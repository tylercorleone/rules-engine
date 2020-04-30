package it.sky.rulesengine.rbac.api;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @param <A> the facts type
 * @param <P> the permissions type
 */
public interface AccessManager<A, B extends Role<P>, P> {

    /**
     * Returns the {@link Role roles} for the given facts.
     *
     * @param facts the facts to check
     * @return the roles. Never null
     */
    Map<String, B> roles(A facts);

    /**
     * Returns the resulting capabilities from the given roles.
     *
     * @param roles the roles
     * @return the capabilities. Never null
     */
    Map<String, Set<P>> capabilities(Collection<B> roles);

    /**
     * Returns the capabilities for the given facts.
     *
     * @param facts the facts
     * @return the capabilities. Never null
     */
    default Map<String, Set<P>> capabilities(A facts) {
        return capabilities(roles(facts).values());
    }

    /**
     * Returns the permissions for the given resource and facts.
     *
     * @param resource the resource
     * @param facts    the facts
     * @return the permissions. Never null
     */
    default Set<P> permissions(String resource, A facts) {
        return capabilities(facts).get(resource);
    }

    /**
     * Returns the permissions for the given resource and roles.
     *
     * @param resource the resource
     * @param roles    the roles
     * @return the permissions. Never null
     */
    default Set<P> permissions(String resource, Collection<B> roles) {
        return capabilities(roles).get(resource);
    }

}
