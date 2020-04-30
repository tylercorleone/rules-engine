package it.sky.rulesengine.factory.api;

import it.sky.rulesengine.core.api.Rule;

/**
 * Creates a rule from a given representation.
 *
 * @param <A> the type of the facts
 * @param <B> the type of the result
 * @param <T> the type of the representation
 */
@FunctionalInterface
public interface RuleParser<A, B, T> {

    /**
     * Creates a rule from the given representation.
     *
     * @param representation the representation
     * @return the rule
     */
    Rule<A, B> parseRule(T representation);

}
