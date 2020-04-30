package it.sky.rulesengine.core.impl;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.annotations.Fixture;
import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.api.RulesEngine;
import it.sky.rulesengine.core.impl.exception.RuleEvaluationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StreamingRulesEngineTest {

    @Fixture
    private String id1;
    @Fixture
    private String id2;
    @Fixture
    private String id3;
    @Fixture
    private Object facts;
    @Fixture
    private Object result1;
    @Fixture
    private Object result2;
    @Fixture
    private Object result3;

    private StreamingRulesEngine instance = StreamingRulesEngine.create();

    @BeforeEach
    void beforeEachRulesEngineTest() {
        FixtureAnnotations.initFixtures(this);
    }

    @Test
    void shouldReturnEmptyWhenNoRuleMatches() {
        Collection<Rule<Object, Object>> rules = Collections.singletonList(mockRule(id1, f -> false, f -> result1));

        assertFalse(instance.applyFirst(rules, facts).isPresent());
        assertTrue(instance.applyAll(rules, facts).isEmpty());
    }

    @Test
    void shouldSupportNullResults() {
        Collection<Rule<Object, Object>> rules = Collections.singletonList(mockRule(id1, f -> true, f -> null));

        assertFalse(instance.applyFirst(rules, facts).isPresent());
        assertFalse(instance.applyAll(rules, facts).isEmpty());
    }

    @Test
    void shouldApplyStreamMapping() {
        Rule<Object, Object> rule = mockRule(id1, f -> true, f -> result2);
        Rule<Object, Object> mappedRule = mockRule(id1, f -> true, f -> result1);

        instance.withStreamMapping(s -> Stream.of(mappedRule))
                .applyFirst(Collections.singletonList(rule), facts);

        verify(rule, never()).test(facts);
        verify(rule, never()).apply(facts);
        verify(mappedRule).test(facts);
        verify(mappedRule).apply(facts);

        instance.withStreamMapping(s -> Stream.of(mappedRule))
                .applyAll(Collections.singletonList(rule), facts);

        verify(rule, never()).test(facts);
        verify(rule, never()).apply(facts);
        verify(mappedRule, times(2)).test(facts);
        verify(mappedRule, times(2)).apply(facts);
    }

    @Test
    void fireFirstShouldMatchOneRule() {
        Rule<Object, Object> notMatchingRule1 = mockRule(id1, f -> false, f -> result1);
        Rule<Object, Object> matchingRule = mockRule(id2, f -> true, f -> result2);
        Rule<Object, Object> notMatchingRule2 = mockRule(id3, f -> false, f -> result3);
        Collection<Rule<Object, Object>> rules = Arrays.asList(notMatchingRule1, matchingRule, notMatchingRule2);

        assertEquals(Optional.of(result2), instance.applyFirst(rules, facts));

        verify(notMatchingRule1).test(facts);
        verify(notMatchingRule1, never()).apply(facts);
        verify(matchingRule).test(facts);
        verify(matchingRule).apply(facts);
        verify(notMatchingRule2, never()).test(facts);
        verify(notMatchingRule2, never()).apply(facts);
    }

    @Test
    void fireAllShouldMatchMultipleRules() {
        Rule<Object, Object> matchingRule1 = mockRule(id1, f -> true, f -> result1);
        Rule<Object, Object> notMatchingRule = mockRule(id2, f -> false, f -> result2);
        Rule<Object, Object> matchingRule2 = mockRule(id3, f -> true, f -> result3);
        Collection<Rule<Object, Object>> rules = Arrays.asList(matchingRule1, notMatchingRule, matchingRule2);

        Map<String, Object> results = instance.applyAll(rules, facts);

        assertEquals(2, results.size());
        assertEquals(result1, results.get(id1));
        assertEquals(result3, results.get(id3));
        verify(matchingRule1).test(facts);
        verify(matchingRule1).apply(facts);
        verify(notMatchingRule).test(facts);
        verify(notMatchingRule, never()).apply(facts);
        verify(matchingRule2).test(facts);
        verify(matchingRule2).apply(facts);
    }

    @ParameterizedTest
    @MethodSource("brokenRules")
    void evaluationErrorsTest(Rule<Object, Object> brokenRule) {
        Collection<Rule<Object, Object>> rules = Collections.singleton(brokenRule);

        assertThrows(RuleEvaluationException.class, () -> instance.applyFirst(rules, facts));
        assertThrows(RuleEvaluationException.class, () -> instance.applyAll(rules, facts));

        RuntimeException mappedException = new RuntimeException();

        RulesEngine rulesEngine = instance.withExceptionMapping(f -> mappedException);

        Exception e1 = assertThrows(RuntimeException.class, () -> rulesEngine.applyFirst(rules, facts));
        Exception e2 = assertThrows(RuntimeException.class, () -> rulesEngine.applyAll(rules, facts));
        assertSame(e1, e2);
        assertSame(e1, mappedException);
    }

    static Stream<Rule<Object, Object>> brokenRules() {
        return Stream.of(
                mockRule("a", f -> {
                    throw new RuntimeException();
                }, f -> null),
                mockRule("b", f -> true, f -> {
                    throw new RuntimeException();
                })
        );
    }

    @SuppressWarnings("unchecked")
    static <A, B> Rule<A, B> mockRule(String id, Predicate<? super A> condition, Function<? super A, ? extends B> action) {
        Rule<A, B> rule = mock(Rule.class);
        when(rule.getId()).thenReturn(id);
        when(rule.test(any())).thenAnswer(i -> condition.test((A) i.getArguments()[0]));
        when(rule.apply(any())).thenAnswer(i -> action.apply((A) i.getArguments()[0]));
        return rule;
    }

}
