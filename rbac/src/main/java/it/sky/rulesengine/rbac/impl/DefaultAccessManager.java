package it.sky.rulesengine.rbac.impl;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.api.RulesEngine;
import it.sky.rulesengine.core.impl.exception.RulesEngineException;
import it.sky.rulesengine.factory.api.RulesFactory;
import it.sky.rulesengine.rbac.api.AccessManager;
import it.sky.rulesengine.rbac.api.Role;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An {@link AccessManager} based on {@link Rule rules}, that is
 * on facts and conditions, with a customizable permission accumulation strategy.
 *
 * @param <A> the facts type
 * @param <P> the permissions type
 */
@RequiredArgsConstructor
public class DefaultAccessManager<A, B extends Role<P>, P> implements AccessManager<A, B, P> {

    @NonNull
    protected final RulesFactory<A, B> rulesFactory;
    @NonNull
    protected final RulesEngine rulesEngine;
    @NonNull
    protected final Set<P> defaultPermissions;
    @NonNull
    protected final BinaryOperator<Set<P>> permissionsAccumulator;

    @Override
    public Map<String, B> roles(A facts) {
        return rulesEngine.applyAll(rulesFactory.get(), facts);
    }

    /**
     * {@inheritDoc}
     *
     * @throws RulesEngineException if the roles collection is empty
     */
    @Override
    public Map<String, Set<P>> capabilities(@NonNull Collection<B> roles) {
        if (roles.isEmpty()) {
            throw new RulesEngineException("no role present. Consider using a 'default' one");
        }
        return roles.stream().map(Role::getCapabilities).reduce(identity(roles), this::accumulate);
    }

    protected Map<String, Set<P>> accumulate(Map<String, Set<P>> a, Map<String, Set<P>> b) {
        return Stream.concat(a.keySet().stream(), b.keySet().stream())
                .distinct()
                .collect(Collectors.toMap(Function.identity(), key -> permissionsAccumulator.apply(
                        a.getOrDefault(key, defaultPermissions), b.getOrDefault(key, defaultPermissions))));
    }

    protected Map<String, Set<P>> identity(Collection<B> roles) {
        return roles.stream()
                .map(Role::getCapabilities)
                .map(Map::keySet)
                .flatMap(Set::stream)
                .distinct()
                .collect(Collectors.toMap(Function.identity(), key -> defaultPermissions));
    }

    public static <T> Set<T> intersection(Set<T> a, Set<T> b) {
        return a.stream().filter(b::contains).collect(Collectors.toSet());
    }

    public static <T> Set<T> union(Set<T> a, Set<T> b) {
        return Stream.concat(a.stream(), b.stream()).collect(Collectors.toSet());
    }

}
