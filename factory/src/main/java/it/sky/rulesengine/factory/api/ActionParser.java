package it.sky.rulesengine.factory.api;

import java.util.function.Function;

/**
 * Creates an action from a given representation.
 *
 * <p>The representation could be a JEXL expression, a function etc.

 * @param <A> the type of the facts
 * @param <B> the type of the result
 * @param <D> the type of the representation
 */
@FunctionalInterface
public interface ActionParser<A, B, D> {

    /**
     * Creates an action from the given representation.
     *
     * @param representation the representation
     * @return the action
     */
    Function<A, B> parseAction(D representation);

}
