package it.sky.rulesengine.examples.rbac;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.annotations.Fixture;
import it.sky.rulesengine.examples.rbac.authorization.AuthorizationFilter;
import it.sky.rulesengine.examples.rbac.authorization.AuthorizedProxyFactory;
import it.sky.rulesengine.examples.rbac.authorization.UserFacts;
import it.sky.rulesengine.rbac.api.AccessManager;
import it.sky.rulesengine.rbac.api.Role;
import it.sky.rulesengine.rbac.impl.DefaultAccessManager;
import it.sky.rulesengine.rbac.impl.util.CrudPermission;
import it.sky.rulesengine.factory.RulesFactory;
import it.sky.rulesengine.factory.YamlRulesFactory;
import it.sky.rulesengine.rbac.impl.DryCapabilitiesDecorator;
import lombok.Value;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RbacExampleTest {

    static AccessManager<UserFacts, CrudPermission, Role<CrudPermission>> accessManager;
    static ServiceExample delegate;

    @Fixture
    Instant anInstant;

    ServiceExample serviceExample;

    @BeforeAll
    static void beforeAll() {
        InputStream is = RbacExampleTest.class.getClassLoader().getResourceAsStream("restrictive-rbac-rules.yaml");
        Map<String, Object> namespaces = new HashMap<>();
        namespaces.put("instant", Instant.class);
        namespaces.put("chronoUnit", ChronoUnit.class);
        JexlEngine jexlEngine = new JexlBuilder().namespaces(namespaces).create();
        RulesFactory<UserFacts, Role<CrudPermission>> yamlRulesFactory =
                YamlRulesFactory.from(() -> is, new JexlConditionFactory<>(jexlEngine), Role.class, CrudPermission.class);
        RulesFactory<UserFacts, Role<CrudPermission>> dryRulesFactory =
                new DryCapabilitiesDecorator<>(yamlRulesFactory, CrudPermission.ALL);
        accessManager = new DefaultAccessManager<>(dryRulesFactory, new DefaultRulesEngine(), CrudPermission.ALL);
        delegate = new ServiceExampleImpl();
    }

    @BeforeEach
    void beforeEach() {
        FixtureAnnotations.initFixtures(this);
    }

    @Nested
    class NonAdminUsers {

        @BeforeEach
        void beforeEach() {
            UserFacts userFacts = new UserFacts(false, anInstant);
            serviceExample = AuthorizedProxyFactory.create(ServiceExample.class, delegate, () -> userFacts, accessManager);
        }

        @Test
        void shouldNotAccessRestrictedResource() {
            assertThrows(AuthorizationFilter.AuthorizationException.class, () -> serviceExample.readRestrictedResource());
        }

    }

    @Nested
    class AdminUsers {

        @BeforeEach
        void beforeEach() {
            UserFacts userFacts = new UserFacts(true, Instant.now());
            serviceExample = AuthorizedProxyFactory.create(ServiceExample.class, delegate, () -> userFacts, accessManager);
            new Testone(null);
        }

        @Test
        void shouldNotAccessRestrictedResource() {
            assertDoesNotThrow(() -> serviceExample.readRestrictedResource());
        }

    }

    @Value
    static class Testone {
        private final String value;
    }

}
