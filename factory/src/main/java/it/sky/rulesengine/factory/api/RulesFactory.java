package it.sky.rulesengine.factory.api;

import it.sky.rulesengine.core.api.Rule;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Marker interface for a supplier of rules collection.
 *
 * @param <A> the facts type
 * @param <B> the result type
 */
@FunctionalInterface
public interface RulesFactory<A, B> extends Supplier<Collection<Rule<A, B>>> {
}
