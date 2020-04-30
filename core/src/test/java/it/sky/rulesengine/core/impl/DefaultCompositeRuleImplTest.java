package it.sky.rulesengine.core.impl;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.JFixture;
import com.flextrade.jfixture.annotations.Fixture;
import it.sky.rulesengine.core.impl.CompositeRuleSupport.DefaultCompositeRuleImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.function.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DefaultCompositeRuleImplTest {

    @Fixture
    String id;
    @Fixture
    Integer priority;
    @Mock
    Predicate<Object> predicate;
    @Mock
    Function<Object, Object> function;
    @Mock
    Consumer<Object> consumer;
    @Mock
    Supplier<Object> supplier;
    @Mock
    Runnable runnable;
    @Fixture
    Object thenResult;
    @Fixture
    Object elseResult;
    @Fixture
    Object functionReturnValue;
    @Fixture
    Object supplierReturnValue;
    @Fixture
    Object facts;

    DefaultCompositeRuleImpl<Object, Object> instance;

    @BeforeEach
    void CompositeRuleImplTestBeforeEach() {
        FixtureAnnotations.initFixtures(this);
        MockitoAnnotations.initMocks(this);
        instance = (DefaultCompositeRuleImpl<Object, Object>) CompositeRule.create(id);
    }

    @ParameterizedTest
    @MethodSource("rules")
    void withersTest(DefaultCompositeRuleImpl<Object, Object> rule) {
        DefaultCompositeRuleImpl<Object, Object> ruleWithId = (DefaultCompositeRuleImpl<Object, Object>) rule.withId(id);
        DefaultCompositeRuleImpl<Object, Object> ruleWithPriority = (DefaultCompositeRuleImpl<Object, Object>) rule.withPriority(priority);
        DefaultCompositeRuleImpl<Object, Object> ruleWithPredicate = (DefaultCompositeRuleImpl<Object, Object>) rule.when(predicate);

        assertSame(ruleWithId, ruleWithId.withId(id));
        assertSame(id, ruleWithId.id);
        assertSame(rule.priority, ruleWithId.priority);
        assertSame(rule.predicate, ruleWithId.predicate);
        assertSame(rule.thenFunction, ruleWithId.thenFunction);
        assertSame(rule.thenConsumer, ruleWithId.thenConsumer);
        assertSame(rule.thenResult, ruleWithId.thenResult);
        assertSame(rule.elseFunction, ruleWithId.elseFunction);
        assertSame(rule.elseConsumer, ruleWithId.elseConsumer);
        assertSame(rule.elseResult, ruleWithId.elseResult);

        assertSame(ruleWithPriority, ruleWithPriority.withPriority(priority));
        assertSame(rule.id, ruleWithPriority.id);
        assertSame(priority, ruleWithPriority.priority);
        assertSame(rule.predicate, ruleWithPriority.predicate);
        assertSame(rule.thenFunction, ruleWithPriority.thenFunction);
        assertSame(rule.thenConsumer, ruleWithPriority.thenConsumer);
        assertSame(rule.thenResult, ruleWithPriority.thenResult);
        assertSame(rule.elseFunction, ruleWithPriority.elseFunction);
        assertSame(rule.elseConsumer, ruleWithPriority.elseConsumer);
        assertSame(rule.elseResult, ruleWithPriority.elseResult);

        assertSame(ruleWithPredicate, ruleWithPredicate.when(predicate));
        assertSame(rule.id, ruleWithPredicate.id);
        assertSame(rule.priority, ruleWithPredicate.priority);
        assertSame(predicate, ruleWithPredicate.predicate);
        assertSame(rule.thenFunction, ruleWithPredicate.thenFunction);
        assertSame(rule.thenConsumer, ruleWithPredicate.thenConsumer);
        assertSame(rule.thenResult, ruleWithPredicate.thenResult);
        assertSame(rule.elseFunction, ruleWithPredicate.elseFunction);
        assertSame(rule.elseConsumer, ruleWithPredicate.elseConsumer);
        assertSame(rule.elseResult, ruleWithPredicate.elseResult);
    }

    @ParameterizedTest
    @MethodSource("rules")
    void thenMethodsTest(DefaultCompositeRuleImpl<Object, Object> rule) {
        DefaultCompositeRuleImpl<Object, Object> ruleWithFunction = (DefaultCompositeRuleImpl<Object, Object>) rule.then(function);
        DefaultCompositeRuleImpl<Object, Object> ruleWithConsumer = (DefaultCompositeRuleImpl<Object, Object>) rule.thenConsume(consumer);
        DefaultCompositeRuleImpl<Object, Object> ruleWithSupplier = (DefaultCompositeRuleImpl<Object, Object>) rule.thenSupply(supplier);
        DefaultCompositeRuleImpl<Object, Object> ruleWithRunnable = (DefaultCompositeRuleImpl<Object, Object>) rule.thenRun(runnable);
        DefaultCompositeRuleImpl<Object, Object> ruleWithResult = (DefaultCompositeRuleImpl<Object, Object>) rule.thenReturn(thenResult);

        assertSame(ruleWithFunction, ruleWithFunction.then(function));
        assertSame(rule.id, ruleWithFunction.id);
        assertSame(rule.priority, ruleWithFunction.priority);
        assertSame(rule.predicate, ruleWithFunction.predicate);
        assertSame(function, ruleWithFunction.thenFunction);
        assertNull(ruleWithFunction.thenConsumer);
        assertNull(ruleWithFunction.thenResult);
        assertSame(rule.elseFunction, ruleWithFunction.elseFunction);
        assertSame(rule.elseConsumer, ruleWithFunction.elseConsumer);
        assertSame(rule.elseResult, ruleWithFunction.elseResult);

        assertSame(ruleWithConsumer, ruleWithConsumer.thenConsume(consumer));
        assertSame(rule.id, ruleWithConsumer.id);
        assertSame(rule.priority, ruleWithConsumer.priority);
        assertSame(rule.predicate, ruleWithConsumer.predicate);
        assertNull(ruleWithConsumer.thenFunction);
        assertSame(consumer, ruleWithConsumer.thenConsumer);
        assertSame(rule.thenResult, ruleWithConsumer.thenResult);
        assertSame(rule.elseFunction, ruleWithConsumer.elseFunction);
        assertSame(rule.elseConsumer, ruleWithConsumer.elseConsumer);
        assertSame(rule.elseResult, ruleWithConsumer.elseResult);

        assertSame(rule.id, ruleWithSupplier.id);
        assertSame(rule.priority, ruleWithSupplier.priority);
        assertSame(rule.predicate, ruleWithSupplier.predicate);
        assertNotNull(ruleWithSupplier.thenFunction);
        assertNull(ruleWithSupplier.thenConsumer);
        assertNull(ruleWithSupplier.thenResult);
        assertSame(rule.elseFunction, ruleWithSupplier.elseFunction);
        assertSame(rule.elseConsumer, ruleWithSupplier.elseConsumer);
        assertSame(rule.elseResult, ruleWithSupplier.elseResult);

        assertSame(rule.id, ruleWithRunnable.id);
        assertSame(rule.priority, ruleWithRunnable.priority);
        assertSame(rule.predicate, ruleWithRunnable.predicate);
        assertNull(ruleWithRunnable.thenFunction);
        assertNotNull(ruleWithRunnable.thenConsumer);
        assertSame(rule.thenResult, ruleWithRunnable.thenResult);
        assertSame(rule.elseFunction, ruleWithRunnable.elseFunction);
        assertSame(rule.elseConsumer, ruleWithRunnable.elseConsumer);
        assertSame(rule.elseResult, ruleWithRunnable.elseResult);

        assertSame(ruleWithResult, ruleWithResult.thenReturn(thenResult));
        assertSame(rule.id, ruleWithResult.id);
        assertSame(rule.priority, ruleWithResult.priority);
        assertSame(rule.predicate, ruleWithResult.predicate);
        assertSame(rule.thenFunction, ruleWithResult.thenFunction);
        assertSame(rule.thenConsumer, ruleWithResult.thenConsumer);
        assertSame(thenResult, ruleWithResult.thenResult);
        assertSame(rule.elseFunction, ruleWithResult.elseFunction);
        assertSame(rule.elseConsumer, ruleWithResult.elseConsumer);
        assertSame(rule.elseResult, ruleWithResult.elseResult);
    }

    @ParameterizedTest
    @MethodSource("rules")
    void orElseMethodsTest(DefaultCompositeRuleImpl<Object, Object> rule) {
        DefaultCompositeRuleImpl<Object, Object> ruleWithElseFunction = (DefaultCompositeRuleImpl<Object, Object>) rule.orElse(function);
        DefaultCompositeRuleImpl<Object, Object> ruleWithElseConsumer = (DefaultCompositeRuleImpl<Object, Object>) rule.orElseConsume(consumer);
        DefaultCompositeRuleImpl<Object, Object> ruleWithElseSupplier = (DefaultCompositeRuleImpl<Object, Object>) rule.orElseSupply(supplier);
        DefaultCompositeRuleImpl<Object, Object> ruleWithElseRunnable = (DefaultCompositeRuleImpl<Object, Object>) rule.orElseRun(runnable);
        DefaultCompositeRuleImpl<Object, Object> ruleWithElseResult = (DefaultCompositeRuleImpl<Object, Object>) rule.orElseReturn(elseResult);

        assertSame(ruleWithElseFunction, ruleWithElseFunction.orElse(function));
        assertSame(rule.id, ruleWithElseFunction.id);
        assertSame(rule.priority, ruleWithElseFunction.priority);
        assertSame(rule.predicate, ruleWithElseFunction.predicate);
        assertSame(rule.thenFunction, ruleWithElseFunction.thenFunction);
        assertSame(rule.thenConsumer, ruleWithElseFunction.thenConsumer);
        assertSame(rule.thenResult, ruleWithElseFunction.thenResult);
        assertSame(function, ruleWithElseFunction.elseFunction);
        assertNull(ruleWithElseFunction.elseConsumer);
        assertNull(ruleWithElseFunction.elseResult);

        assertSame(ruleWithElseConsumer, ruleWithElseConsumer.orElseConsume(consumer));
        assertSame(rule.id, ruleWithElseConsumer.id);
        assertSame(rule.priority, ruleWithElseConsumer.priority);
        assertSame(rule.predicate, ruleWithElseConsumer.predicate);
        assertSame(rule.thenFunction, ruleWithElseConsumer.thenFunction);
        assertSame(rule.thenConsumer, ruleWithElseConsumer.thenConsumer);
        assertSame(rule.thenResult, ruleWithElseConsumer.thenResult);
        assertNull(ruleWithElseConsumer.elseFunction);
        assertSame(consumer, ruleWithElseConsumer.elseConsumer);
        assertSame(rule.elseResult, ruleWithElseConsumer.elseResult);

        assertSame(rule.id, ruleWithElseSupplier.id);
        assertSame(rule.priority, ruleWithElseSupplier.priority);
        assertSame(rule.predicate, ruleWithElseSupplier.predicate);
        assertSame(rule.thenFunction, ruleWithElseSupplier.thenFunction);
        assertSame(rule.thenConsumer, ruleWithElseSupplier.thenConsumer);
        assertSame(rule.thenResult, ruleWithElseSupplier.thenResult);
        assertNotNull(ruleWithElseSupplier.elseFunction);
        assertNull(ruleWithElseSupplier.elseConsumer);
        assertNull(ruleWithElseSupplier.elseResult);

        assertSame(rule.id, ruleWithElseRunnable.id);
        assertSame(rule.priority, ruleWithElseRunnable.priority);
        assertSame(rule.predicate, ruleWithElseRunnable.predicate);
        assertSame(rule.thenFunction, ruleWithElseRunnable.thenFunction);
        assertSame(rule.thenConsumer, ruleWithElseRunnable.thenConsumer);
        assertSame(rule.thenResult, ruleWithElseRunnable.thenResult);
        assertNull(ruleWithElseRunnable.elseFunction);
        assertNotNull(ruleWithElseRunnable.elseConsumer);
        assertSame(rule.elseResult, ruleWithElseRunnable.elseResult);

        assertSame(ruleWithElseResult, ruleWithElseResult.orElseReturn(elseResult));
        assertSame(rule.id, ruleWithElseResult.id);
        assertSame(rule.priority, ruleWithElseResult.priority);
        assertSame(rule.predicate, ruleWithElseResult.predicate);
        assertSame(rule.thenFunction, ruleWithElseResult.thenFunction);
        assertSame(rule.thenConsumer, ruleWithElseResult.thenConsumer);
        assertSame(rule.thenResult, ruleWithElseResult.thenResult);
        assertSame(rule.elseFunction, ruleWithElseResult.elseFunction);
        assertSame(rule.elseConsumer, ruleWithElseResult.elseConsumer);
        assertSame(elseResult, ruleWithElseResult.elseResult);
    }

    @Test
    void methodInvocations() {
        DefaultCompositeRuleImpl<Object, Object> rule = (DefaultCompositeRuleImpl<Object, Object>) CompositeRule.create(id);
        assertFalse(rule.test(facts));
        assertNull(rule.apply(facts));

        rule.when(predicate).test(facts);
        verify(predicate).test(facts);

        rule.then(function).apply(facts);
        verify(function).apply(facts);

        rule.thenConsume(consumer).apply(facts);
        verify(consumer).accept(facts);

        rule.thenSupply(supplier).apply(facts);
        verify(supplier).get();

        rule.thenRun(runnable).apply(facts);
        verify(runnable).run();

        // orElse

        rule.when(() -> false).orElse(function).apply(facts);
        verify(function, times(2)).apply(facts);

        rule.when(() -> false).orElseConsume(consumer).apply(facts);
        verify(consumer, times(2)).accept(facts);

        rule.when(() -> false).orElseSupply(supplier).apply(facts);
        verify(supplier, times(2)).get();

        rule.when(() -> false).orElseRun(runnable).apply(facts);
        verify(runnable, times(2)).run();
    }

    @ParameterizedTest
    @MethodSource("rules")
    void resultTest(DefaultCompositeRuleImpl<Object, Object> rule) {
        when(function.apply(any())).thenReturn(functionReturnValue);
        when(supplier.get()).thenReturn(supplierReturnValue);

        assertSame(functionReturnValue, rule.when(f -> true).then(function).apply(facts));

        assertSame(rule.thenResult, rule.when(f -> true).thenConsume(consumer).apply(facts));

        assertSame(supplierReturnValue, rule.when(f -> true).thenSupply(supplier).apply(facts));

        assertSame(rule.thenResult, rule.when(f -> true).thenRun(runnable).apply(facts));

        assertSame(thenResult, rule.when(f -> true).thenReturn(thenResult).apply(facts));

        // orElse

        assertSame(functionReturnValue, rule.when(f -> false).orElse(function).apply(facts));

        assertSame(rule.elseResult, rule.when(f -> false).orElseConsume(consumer).apply(facts));

        assertSame(supplierReturnValue, rule.when(f -> false).orElseSupply(supplier).apply(facts));

        assertSame(rule.elseResult, rule.when(f -> false).orElseRun(runnable).apply(facts));

        assertSame(elseResult, rule.when(f -> false).orElseReturn(elseResult).apply(facts));
    }

    @Test
    void fieldsNullabilityCheck() {
        assertThrows(NullPointerException.class, () -> CompositeRule.create(null));
        assertThrows(NullPointerException.class, () -> CompositeRule.create(id, null, thenResult));

        assertThrows(NullPointerException.class, () -> instance.when((Predicate<Object>) null));
        assertThrows(NullPointerException.class, () -> instance.when((BooleanSupplier) null));
        assertThrows(NullPointerException.class, () -> instance.then(null));
        assertThrows(NullPointerException.class, () -> instance.thenConsume(null));
        assertThrows(NullPointerException.class, () -> instance.thenSupply(null));
        assertThrows(NullPointerException.class, () -> instance.thenRun(null));
        assertThrows(NullPointerException.class, () -> instance.then(null));
        assertThrows(NullPointerException.class, () -> instance.orElseConsume(null));
        assertThrows(NullPointerException.class, () -> instance.orElseSupply(null));
        assertThrows(NullPointerException.class, () -> instance.orElseRun(null));

        assertDoesNotThrow(() -> instance.thenReturn(null));
        assertDoesNotThrow(() -> instance.orElseReturn(null));

        assertThrows(IllegalStateException.class, () -> new DefaultCompositeRuleImpl<>(id, priority, predicate, function, consumer, thenResult, null, null, null));
        assertThrows(IllegalStateException.class, () -> new DefaultCompositeRuleImpl<>(id, priority, predicate, null, null, thenResult, null, null, null));
    }

    @SuppressWarnings("unchecked")
    static Stream<CompositeRule<Object, Object>> rules() {
        JFixture fixture = new JFixture();
        String id = fixture.create(String.class);
        Integer priority = fixture.create(Integer.class);
        Predicate<Object> predicate = Mockito.mock(Predicate.class);
        Function<Object, Object> function = Mockito.mock(Function.class);
        Consumer<Object> consumer = Mockito.mock(Consumer.class);
        Supplier<Object> supplier = Mockito.mock(Supplier.class);
        Runnable runnable = Mockito.mock(Runnable.class);
        Object result = fixture.create(Object.class);

        CompositeRule<Object, Object> rule = CompositeRule.create(id);
        return Stream.of(
                rule,
                rule.withId(""),
                rule.withPriority(priority),
                rule.when(predicate),
                rule.then(function),
                rule.thenConsume(consumer),
                rule.thenSupply(supplier),
                rule.thenRun(runnable),
                rule.thenReturn(result),
                rule.orElse(function),
                rule.orElseConsume(consumer),
                rule.orElseSupply(supplier),
                rule.orElseRun(runnable),
                rule.orElseReturn(result)
        );
    }

}
