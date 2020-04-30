package it.sky.rulesengine.core.impl;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.annotations.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractRuleTest {

    @Fixture
    String id1;
    @Fixture
    String id2;
    @Fixture
    Integer priority;
    @Fixture
    Integer priority2;

    @BeforeEach
    void compositeRuleTestBeforeEach() {
        FixtureAnnotations.initFixtures(this);
    }

    @Test
    void compareToTest() {
        DummyRule rule1 = new DummyRule(id1, priority);
        DummyRule rule2 = new DummyRule(id2, priority - 1);
        DummyRule rule3 = new DummyRule(id1, priority2);
        assertEquals(-1, rule1.compareTo(rule2));
        assertEquals(1, rule2.compareTo(rule1));
        assertEquals(0, rule1.compareTo(rule3));
    }

    @Test
    void equalsTest() {
        assertNotEquals(new DummyRule(id1, priority), new DummyRule(id2, priority2));
        assertNotEquals(new DummyRule(id1, priority), new DummyRule(id2, priority));
        assertEquals(new DummyRule(id1, priority), new DummyRule(id1, priority2));
    }

    @Test
    void toStringTest() {
        assertEquals(id1, new DummyRule(id1, priority).toString());
    }

    static class DummyRule extends AbstractRule<Object, Object> {

        public DummyRule(String id, int priority) {
            super(id, priority);
        }

        @Override
        public boolean test(Object facts) {
            return false;
        }

        @Override
        public Object apply(Object facts) {
            return null;
        }
    }

}
