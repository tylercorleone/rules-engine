package it.sky.rulesengine.factory.impl;

import it.sky.rulesengine.factory.api.RuleParser;
import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.impl.CompositeRule;
import it.sky.rulesengine.factory.api.ActionParser;
import it.sky.rulesengine.factory.api.ConditionParser;
import it.sky.rulesengine.factory.impl.exception.ParsingException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An immutable implementation of the {@link RuleParser} interface that handles
 * {@link RuleModel} representations with a fluent API to customize the parsing strategy.
 *
 * @param <A> the facts type
 * @param <B> the result type
 * @param <C> the condition representation type
 * @param <D> the action representation type
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GenericRuleParser<A, B, C, D> implements RuleParser<A, B, RuleModel<B, C, D>> {

    @NonNull
    protected final ConditionParser<A, C> conditionParser;
    @NonNull
    protected final ActionParser<A, B, D> actionParser;

    protected final B defaultResult;

    /**
     * Creates a parser with the given condition and action parsers.
     *
     * @param conditionParser the condition parser
     * @param actionParser    the action parser
     * @param <A>             the facts type
     * @param <B>             the result type
     * @param <C>             the condition representation type
     * @param <D>             the action representation type
     * @return the parser
     */
    public static <A, B, C, D> GenericRuleParser<A, B, C, D> create(ConditionParser<A, C> conditionParser,
                                                                    ActionParser<A, B, D> actionParser) {
        return new GenericRuleParser<>(conditionParser, actionParser, null);
    }

    /**
     * Returns a copy that uses the given condition factory.
     *
     * @param conditionParser the condition factory
     * @param <X>             the condition representation type
     * @return the new parser
     */
    @SuppressWarnings("unchecked")
    public <X> GenericRuleParser<A, B, X, D> withConditionParser(ConditionParser<A, X> conditionParser) {
        return this.conditionParser == conditionParser ? (GenericRuleParser<A, B, X, D>) this
                : new GenericRuleParser<>(conditionParser, actionParser, defaultResult);
    }

    /**
     * Returns a copy that uses the given action factory.
     *
     * @param actionParser the action factory
     * @param <X>          the action representation type
     * @return the new parser
     */
    @SuppressWarnings("unchecked")
    public <X> GenericRuleParser<A, B, C, X> withActionParser(ActionParser<A, B, X> actionParser) {
        return this.actionParser == actionParser ? (GenericRuleParser<A, B, C, X>) this
                : new GenericRuleParser<>(conditionParser, actionParser, defaultResult);
    }

    /**
     * Returns a copy with the given default result.
     *
     * @param defaultResult the result
     * @param <X>           the result type
     * @return the new parser
     */
    public <X extends B> GenericRuleParser<A, B, C, D> withDefaultResult(X defaultResult) {
        return this.defaultResult == defaultResult ? this
                : new GenericRuleParser<>(conditionParser, actionParser, defaultResult);
    }

    /**
     * Parses the given {@link RuleModel} into a rule.
     *
     * <p>The rule's id and condition must be present.
     *
     * @param ruleModel the rule model
     * @throws ParsingException if an error occurs
     */
    @Override
    public Rule<A, B> parseRule(RuleModel<B, C, D> ruleModel) {
        try {
            Predicate<A> condition = conditionParser.parseCondition(ruleModel.getCondition());
            B result = Optional.ofNullable(ruleModel.getResult()).orElse(defaultResult);

            CompositeRule<A, B> rule = CompositeRule.create(ruleModel.getId(), condition, result)
                    .withPriority(ruleModel.getPriority());

            if (ruleModel.getAction() != null) {
                Function<A, B> function = actionParser.parseAction(ruleModel.getAction());
                rule = result != null ? rule.thenConsume(function::apply) : rule.then(function);
            }
            return rule;
        } catch (Exception e) {
            throw new ParsingException("couldn't parse " + ruleModel, e);
        }
    }

}
