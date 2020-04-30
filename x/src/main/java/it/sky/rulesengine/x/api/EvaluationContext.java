package it.sky.rulesengine.x.api;

import it.sky.rulesengine.core.api.Rule;
import lombok.NonNull;

/**
 * An evaluation context represents a specific occurrence of the
 * evaluation of collection of rules on some given facts.
 *
 * @param <A> the facts type
 * @param <B> the result type
 */
public interface EvaluationContext<A, B> {

    /**
     * Return the facts of this evaluation.
     *
     * @return the facts
     */
    A getFacts();

    /**
     * Returns the context aware rule with the given id.
     *
     * @param ruleId the rule's id
     * @return the rule
     */
    RuleEvaluationContext<B> getRule(String ruleId);

    /**
     * Returns the evaluation context of the given rule.
     *
     * @param rule the rule
     * @return the context
     */
    default RuleEvaluationContext<B> getRule(@NonNull Rule<A, B> rule) {
        return getRule(rule.getId());
    }

}
