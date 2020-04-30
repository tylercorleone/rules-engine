package it.sky.rulesengine.rbac.impl;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.impl.CompositeRule;
import it.sky.rulesengine.factory.api.RulesFactory;
import it.sky.rulesengine.rbac.api.Role;
import it.sky.rulesengine.rbac.impl.util.CrudPermission;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A decorator created to define DRY capabilities, that is,
 * if a factory would create two rules with the following roles:
 * <pre>
 *      roleA -&gt; operation1: [READ], operation2: [READ, CREATE, UPDATE]
 *      roleB -&gt; operation1: [READ]
 * </pre>
 * applying this decorator with {@link CrudPermission#ALL} to set default
 * permissions on the omitted resources we would obtain:
 * <pre>
 *      roleA -&gt; operation1: [READ], operation2: [READ, CREATE, UPDATE]
 *      roleB -&gt; operation1: [READ], operation2: [READ, CREATE, UPDATE, DELETE]
 * </pre>
 *
 * @param <A> the facts type
 * @param <P> the permissions type
 */
@RequiredArgsConstructor
public class DryCapabilitiesDecorator<A, B extends Role<P>, P> implements RulesFactory<A, B> {

    @NonNull
    private final RulesFactory<A, B> delegate;
    @NonNull
    private final Set<P> defaultPermissions;

    /**
     * {@inheritDoc}
     *
     * @throws ClassCastException if the delegate's rules are not {@link CompositeRule}s
     */
    @Override
    public Collection<Rule<A, B>> get() {
        Collection<Rule<A, B>> rules = delegate.get();
        setDefaultOnMissingOperations(getRoles(rules));
        return rules;
    }

    @SneakyThrows
    private Collection<B> getRoles(Collection<Rule<A, B>> rules) {
        Field resultField = CompositeRule.class.getDeclaredField("thenResult");
        return rules.stream()
                .map(r -> (CompositeRule<A, B>) r)
                .map(r -> getRole(resultField, r))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private B getRole(Field resultField, CompositeRule<?, B> rule) {
        return (B) resultField.get(rule);
    }

    private void setDefaultOnMissingOperations(Collection<B> roles) {
        Set<String> operations = roles.stream()
                .map(Role::getCapabilities)
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        roles.forEach(e -> setDefaultOnMissingOperations(e.getCapabilities(), operations));
    }

    private void setDefaultOnMissingOperations(Map<String, Set<P>> capabilities, Set<String> operations) {
        operations.stream()
                .filter(e -> !capabilities.containsKey(e))
                .forEach(e -> capabilities.put(e, defaultPermissions));
    }

}
