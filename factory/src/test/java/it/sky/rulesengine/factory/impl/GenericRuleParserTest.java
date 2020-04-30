package it.sky.rulesengine.factory.impl;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.annotations.Fixture;
import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.impl.CompositeRule;
import it.sky.rulesengine.factory.api.ActionParser;
import it.sky.rulesengine.factory.api.ConditionParser;
import it.sky.rulesengine.factory.impl.exception.ParsingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenericRuleParserTest {

    @Mock
    ConditionParser<Object, Object> conditionParser;
    @Mock
    ActionParser<Object, Object, Object> actionParser;
    @Mock
    Predicate<Object> predicate;
    @Mock
    Function<Object, Object> function;
    @Fixture
    String id;
    @Fixture
    Object result;
    @Fixture
    Object conditionRepresentation;
    @Fixture
    Object actionRepresentation;
    @Fixture
    Integer priority;
    @Fixture
    Object facts;

    GenericRuleParser<Object, Object, Object, Object> instance;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        FixtureAnnotations.initFixtures(this);
        when(conditionParser.parseCondition(conditionRepresentation)).thenReturn(predicate);
        when(actionParser.parseAction(actionRepresentation)).thenReturn(function);
        instance = GenericRuleParser.create(conditionParser, actionParser);
    }

    @Test
    void fixedResultNoActionTest() {
        Rule<Object, Object> rule = instance.parseRule(
                ruleModel(id, priority, conditionRepresentation, null, result)
        );

        mainAssertions(rule);
        assertSame(result, rule.apply(facts));
        verify(function, never()).apply(facts);
    }

    @Test
    void fixedResultAndActionTest() {
        Rule<Object, Object> rule = instance.parseRule(
                ruleModel(id, priority, conditionRepresentation, actionRepresentation, result)
        );

        mainAssertions(rule);
        assertSame(result, rule.apply(facts));
        verify(function).apply(facts);
    }

    @Test
    void onlyActionTest() {
        Rule<Object, Object> rule = instance.parseRule(
                ruleModel(id, priority, conditionRepresentation, actionRepresentation, null)
        );

        mainAssertions(rule);
        assertNull(rule.apply(facts));
        verify(function).apply(facts);
    }

    @Test
    void usesDefaultResult() {
        Rule<Object, Object> rule = instance.withDefaultResult(result)
                .parseRule(ruleModel(id, priority, conditionRepresentation, actionRepresentation, null));

        assertSame(result, rule.apply(facts));
    }

    @Test
    void shouldThrowOnNullId() {
        assertThrows(ParsingException.class, () -> instance.parseRule(
                ruleModel(null, priority, conditionRepresentation, actionRepresentation, result))
        );
    }

    @Test
    void shouldThrowOnNullCondition() {
        assertThrows(ParsingException.class, () -> instance.parseRule(
                ruleModel(id, priority, null, actionRepresentation, result))
        );
    }

    private void mainAssertions(Rule<Object, Object> rule) {
        CompositeRule<Object, Object> compositeRule = (CompositeRule<Object, Object>) rule;
        assertSame(id, compositeRule.getId());
        assertSame(priority, compositeRule.getPriority());
        rule.test(facts);
        verify(predicate).test(facts);
    }

    private static <B, C, D> RuleModel<B, C, D> ruleModel(String id, int priority, C condition, D action, B result) {
        RuleModel<B, C, D> ruleModel = new RuleModel<>();
        ruleModel.setId(id);
        ruleModel.setPriority(priority);
        ruleModel.setCondition(condition);
        ruleModel.setAction(action);
        ruleModel.setResult(result);
        return ruleModel;
    }
}
