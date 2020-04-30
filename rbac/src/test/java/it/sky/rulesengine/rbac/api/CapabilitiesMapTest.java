//package it.sky.rulesengine.rbac.api;
//
//import com.flextrade.jfixture.FixtureAnnotations;
//import com.flextrade.jfixture.annotations.Fixture;
//import it.sky.rulesengine.rbac.impl.util.ReadWritePermission;
//import it.sky.rulesengine.rbac.impl.exception.ResourceNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//class CapabilitiesMapTest {
//
//    @Fixture
//    String operation;
//
//    @Fixture
//    String anotherOperation;
//
//    @Fixture
//    Set<ReadWritePermission> permissions;
//
//    Capabilities<ReadWritePermission> capabilities;
//
//    @BeforeEach
//    void beforeEach() {
//        FixtureAnnotations.initFixtures(this);
//        capabilities = new Capabilities<>();
//    }
//
//    @Test
//    void shouldThrowOnNullArguments() {
//        assertThrows(NullPointerException.class, () -> capabilities.put(null, permissions));
//        assertThrows(NullPointerException.class, () -> capabilities.put(operation, null));
//    }
//
//    @Test
//    void testPermissionFound() {
//        capabilities.put(operation, permissions);
//        assertEquals(permissions, capabilities.get(operation));
//    }
//
//    @Test
//    void testPermissionNotFound() {
//        capabilities.put(operation, permissions);
//        assertThrows(ResourceNotFoundException.class, () -> capabilities.get(anotherOperation));
//    }
//
//}
