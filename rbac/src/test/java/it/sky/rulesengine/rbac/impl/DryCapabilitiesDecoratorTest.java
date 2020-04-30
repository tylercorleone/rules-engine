package it.sky.rulesengine.rbac.impl;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.annotations.Fixture;
import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.impl.CompositeRule;
import it.sky.rulesengine.factory.api.RulesFactory;
import it.sky.rulesengine.rbac.api.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class DryCapabilitiesDecoratorTest {

    @Mock
    RulesFactory<Object, Role<Object>> delegate;
    @Mock
    Rule<Object, Role<Object>> mockRule;
    @Fixture
    Set<Object> defaultPermissions;
    @Fixture
    String id1;
    @Fixture
    String id2;
    @Fixture
    String resource1;
    @Fixture
    String resource2;

    DryCapabilitiesDecorator<Object, Object> instance;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        FixtureAnnotations.initFixtures(this);
        instance = new DryCapabilitiesDecorator<>(delegate, defaultPermissions);
    }

    @Test
    void shouldSetDefaultPermissionsOnMissingOperations() {
        Rule<Object, Role<Object>> rule1 = CompositeRule.create(id1, f -> true, roleWithDefaultPermissionsOn(resource1, resource2));
        Rule<Object, Role<Object>> rule2 = CompositeRule.create(id2, f -> true, roleWithDefaultPermissionsOn(resource1));

        when(delegate.get()).thenReturn(Arrays.asList(rule1, rule2));

        Collection<Rule<Object, Role<Object>>> decoratedRules = instance.get();

        Rule<Object, Role<Object>> secondRule = decoratedRules.stream()
                .filter(r -> r.getId().equals(id2))
                .findAny()
                .orElseThrow(AssertionError::new);

        assertEquals(defaultPermissions, secondRule.apply(null).getCapabilities().get(resource2));
    }

    @Test
    void shouldThrowOnUnsupportedRules() {
        when(delegate.get()).thenReturn(Stream.of(mockRule).collect(Collectors.toSet()));
        assertThrows(IllegalStateException.class, () -> instance.get());
    }

    Role<Object> roleWithDefaultPermissionsOn(String... operations) {
        Map<String, Set<Object>> capabilities = Arrays.stream(operations)
                .collect(Collectors.toMap(Function.identity(), key -> defaultPermissions));
        return new BasicRole<>(capabilities);
    }

}
