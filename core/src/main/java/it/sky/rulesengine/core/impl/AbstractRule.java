package it.sky.rulesengine.core.impl;

import it.sky.rulesengine.core.api.Rule;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A basic implementation of the {@link Rule} interface that
 * handles comparison, equality, hashCode and toString.
 *
 * @param <A> the facts type
 * @param <B> the result type
 */
@Getter
@RequiredArgsConstructor
public abstract class AbstractRule<A, B> implements Rule<A, B> {

    @NonNull
    protected final String id;
    protected final int priority;

    /**
     * Returns the id.
     *
     * @return the id
     */
    @Override
    public String toString() {
        return id;
    }

    /**
     * Rules are compared applying natural order on id and reverse on priorities.
     *
     * <p>Two rules with the same id compare to 0.
     *
     * @param other the other rule
     * @return 0 if the rules have the same id,
     * -1 if <code>this</code> rule has a grater priority,
     * the result of <code>id.compareTo(other.id)</code> if
     * the rules have the same priority, 1 otherwise
     */
    @Override
    public int compareTo(Rule other) {
        if (id.equals(other.getId())) {
            return 0;
        }
        if (priority == other.getPriority()) {
            return id.compareTo(other.getId());
        }
        return priority > other.getPriority() ? -1 : 1;
    }

    /**
     * Returns true if the given object is a {@link Rule} with the same id.
     *
     * @param other the other object
     * @return true if the object is equal to this rule
     * @see Rule#getId()
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Rule) {
            Rule<?, ?> otherRule = (Rule) other;
            return this == other || id.equals(otherRule.getId());
        }
        return false;
    }

    /**
     * Returns the id's hashCode.
     *
     * @return the hashCode
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
