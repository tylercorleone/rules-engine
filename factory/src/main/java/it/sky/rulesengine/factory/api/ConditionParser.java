package it.sky.rulesengine.factory.api;

import java.util.function.Predicate;

/**
 * Creates a condition from a given representation.
 *
 * <p>The representation could be a JEXL expression, a fact to compare to etc.
 *
 * @param <A> the type of the facts
 * @param <C> the type of the representation
 */
@FunctionalInterface
public interface ConditionParser<A, C> {

    /**
     * Creates a condition from the given representation.
     *
     * @param representation the representation
     * @return the condition
     */
    Predicate<A> parseCondition(C representation);

}
