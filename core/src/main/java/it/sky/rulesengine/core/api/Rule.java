package it.sky.rulesengine.core.api;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A rule represents the union between a condition and an action
 *
 * <p>The {@link #test(Object) condition} tells whether the rule can be applied to some facts,
 * while the {@link #apply(Object) action} applies the rule to the facts and can return a result.
 *
 * @param <A> the facts type
 * @param <B> the result type
 */
public interface Rule<A, B> extends Predicate<A>, Function<A, B>, Comparable<Rule> {

    /**
     * Returns the rule's identifier.
     *
     * @return the identifier
     */
    String getId();

    /**
     * Tests the rule on the given facts.
     *
     * <p>Tells whether the rule can be applied to the facts or not.
     *
     * @param facts the facts
     * @return true if the rule can be applied, false otherwise
     */
    @Override
    boolean test(A facts);

    /**
     * Applies the rule to the given facts.
     *
     * @param facts the facts
     * @return the result
     */
    @Override
    B apply(A facts);

    /**
     * Returns the rule's priority.
     *
     * @return the priority
     */
    default int getPriority() {
        return 0;
    }

}
