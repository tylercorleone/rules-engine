package it.sky.rulesengine.rbac;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.annotations.FromListOf;
import com.flextrade.jfixture.annotations.Range;
import it.sky.rulesengine.core.impl.exception.RulesEngineException;
import it.sky.rulesengine.factory.api.RulesFactory;
import it.sky.rulesengine.factory.impl.yaml.YamlRulesFactory;
import it.sky.rulesengine.rbac.api.Role;
import it.sky.rulesengine.rbac.impl.BasicRole;
import it.sky.rulesengine.rbac.impl.DefaultAccessManager;
import it.sky.rulesengine.rbac.impl.DryCapabilitiesDecorator;
import it.sky.rulesengine.rbac.impl.exception.ResourceNotFoundException;
import it.sky.rulesengine.rbac.impl.util.CrudPermission;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.MapContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultAccessManagerTest {

    private static final String OPERATION_A = "operation_a";
    private static final String OPERATION_B = "operation_b";
    private static final String OPERATION_C = "operation_c";

    @Fixture
    @FromListOf(numbers = {2, 4, 6, 8})
    private Integer evenPositiveNumber;

    @Fixture
    @FromListOf(numbers = {1, 3, 5, 7})
    private Integer oddPositiveNumber;

    @Fixture
    @Range(min = Integer.MIN_VALUE, max = -1)
    private Integer negativeNumber;

    @Fixture
    @FromListOf(strings = {OPERATION_A, OPERATION_B, OPERATION_C})
    private String operationName;

    private DefaultAccessManager<JexlContext, Role<CrudPermission>, CrudPermission> instance;

    @BeforeEach
    void beforeEach() {
        FixtureAnnotations.initFixtures(this);

        RulesFactory<JexlContext, Role<CrudPermission>> yamlRulesFactory = YamlRulesFactory.create(
                DefaultAccessManagerTest::inputSupplier, BasicRole.class);
        DryCapabilitiesDecorator<JexlContext, Role<CrudPermission>, CrudPermission> rulesFactory =
                new DryCapabilitiesDecorator<>(yamlRulesFactory, CrudPermission.ALL);

        instance = new DefaultAccessManager<>(yamlRulesFactory, new DefaultRulesEngine(), CrudPermission.ALL);
    }

    @Test
    void shouldThrowOnNoMatchingRule() {
        assertThrows(RulesEngineException.class, () -> instance.permissions(operationName, numberIs(negativeNumber)));
    }

    @Test
    void shouldThrowOnEmptyRoles() {
        assertThrows(RulesEngineException.class, () -> instance.permissions(operationName, Collections.emptySet()));
    }

    @Test
    void shouldMatchOperationPermission() {
        Collection<CrudPermission> permissions = instance.permissions(OPERATION_A, numberIs(evenPositiveNumber));
        assertEquals(1, permissions.size());
        assertTrue(permissions.contains(CrudPermission.READ));
    }

    @Test
    void shouldThrowOnOperationNotFound() {
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> instance.permissions("fake_operation", numberIs(evenPositiveNumber)));
        assertTrue(e.getMessage().contains("fake_operation"));
    }

    @Test
    void policyShouldCheckParameters() {
        assertThrows(NullPointerException.class, () -> instance.capabilities((Set<Role<CrudPermission>>) null));
        assertThrows(NullPointerException.class, () -> instance.capabilities((JexlContext) null));
    }

    @Test
    void rolesOfZero() {
        Map<String, Role<CrudPermission>> roles = instance.roles(numberIs(0));
        assertEquals(3, roles.size());
    }

    @Test
    void defaultPermissionsTest() {
        Set<Role<CrudPermission>> roles = instance.roles(numberIs(0));
        Capabilities<CrudPermission> numberZeroCapabilities = roles.stream()
                .filter(e -> e.getName().equals("numberZero"))
                .map(Role::getCapabilities)
                .findAny()
                .get();
        assertTrue(numberZeroCapabilities.containsKey(OPERATION_A));
    }

    @Test
    void policyWithZero() {
        Capabilities<CrudPermission> capabilities = instance.capabilities(numberIs(0));
        assertEquals(4, capabilities.get(OPERATION_A).size());
        assertTrue(capabilities.get(OPERATION_A).containsAll(CrudPermission.ALL));
        assertEquals(1, capabilities.get(OPERATION_B).size());
        assertTrue(capabilities.get(OPERATION_B).contains(CrudPermission.READ));
        assertEquals(3, capabilities.get(OPERATION_C).size());
        assertTrue(capabilities.get(OPERATION_C).containsAll(Arrays.asList(CrudPermission.READ, CrudPermission.CREATE, CrudPermission.UPDATE)));
    }

    @Test
    void rolesOfOddPositiveNumbers() {
        Set<Role<CrudPermission>> roles = instance.roles(numberIs(oddPositiveNumber));
        assertEquals(2, roles.size());
    }

    @Test
    void policyWithOddPositiveNumbers() {
        Capabilities<CrudPermission> capabilities = instance.capabilities(numberIs(oddPositiveNumber));
        assertEquals(4, capabilities.get(OPERATION_A).size());
        assertTrue(capabilities.get(OPERATION_A).containsAll(CrudPermission.ALL));
        assertEquals(1, capabilities.get(OPERATION_B).size());
        assertTrue(capabilities.get(OPERATION_B).contains(CrudPermission.READ));
        assertEquals(4, capabilities.get(OPERATION_C).size());
        assertTrue(capabilities.get(OPERATION_C).containsAll(CrudPermission.ALL));
    }

    @Test
    void rolesOfEvenPositiveNumbers() {
        Set<Role<CrudPermission>> roles = instance.roles(numberIs(evenPositiveNumber));
        assertEquals(3, roles.size());
    }

    @Test
    void policyWithEvenPositiveNumbers() {
        Capabilities<CrudPermission> capabilities = instance.capabilities(numberIs(evenPositiveNumber));
        assertEquals(1, capabilities.get(OPERATION_A).size());
        assertTrue(capabilities.get(OPERATION_A).contains(CrudPermission.READ));
        assertEquals(1, capabilities.get(OPERATION_B).size());
        assertTrue(capabilities.get(OPERATION_B).contains(CrudPermission.READ));
        assertEquals(1, capabilities.get(OPERATION_C).size());
        assertTrue(capabilities.get(OPERATION_C).contains(CrudPermission.READ));
    }

    private static JexlContext numberIs(int number) {
        MapContext facts = new MapContext();
        facts.set("number", number);
        return facts;
    }

    private static InputStream inputSupplier() {
        return DefaultAccessManagerTest.class.getClassLoader().getResourceAsStream("authorization-manager-test.yaml");
    }
}
