package it.sky.rulesengine.x.impl.decorator;

import it.sky.rulesengine.core.api.Rule;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

/**
 * A rule decorator enables to rule's lifecycle interception and customization.
 *
 * @param <A> the facts type
 * @param <B> the result type
 */
@RequiredArgsConstructor
public class RuleDecorator<A, B> implements Rule<A, B> {

    @NonNull
    @Delegate(types = DelegableMethods.class)
    protected final Rule<A, B> delegate;

    /**
     * This method is invoked before the actual rule's test.
     * Can be used to tell if the rule has to be tested or not.
     * The default implementation returns true.
     *
     * @param facts the facts
     * @return true if the rule has to be tested, false otherwise
     */
    @SuppressWarnings("unused")
    protected boolean beforeTest(A facts) {
        return true;
    }

    /**
     * This method performs the actual test.
     * The default implementation just tests the delegate.
     *
     * @param facts the facts
     * @return the test result
     */
    protected boolean performTest(A facts) {
        return delegate.test(facts);
    }

    /**
     * This method is invoked after the rule's test.
     * The default implementation does nothing.
     *
     * @param facts   the facts
     * @param applies is true if the rule's condition returned true
     */
    protected void afterTest(A facts, boolean applies) {
        // no-op
    }

    /**
     * This method is called in case of a condition evaluation error.
     * Can be used for logging purpose or to provide a default result
     * in case of error.
     * The default implementation throws the exception.
     *
     * @param facts the facts
     * @param e     the exception
     * @return true if the rule has to be applied
     */
    protected boolean onTestError(A facts, RuntimeException e) {
        throw e;
    }

    /**
     * This method is invoked before the actual rule's apply.
     * The default implementation does nothing.
     *
     * @param facts the facts
     */
    @SuppressWarnings("unused")
    protected void beforeApply(A facts) {
        // no-op
    }

    /**
     * This method performs the actual apply.
     * The default implementation just apply the delegate.
     *
     * @param facts the facts
     * @return the result
     */
    protected B performApply(A facts) {
        return delegate.apply(facts);
    }

    /**
     * This method is invoked after the rule's apply.
     * Can be used for logging purpose or modify the actual result.
     * The default implementation returns the result.
     *
     * @param facts  the facts
     * @param result the result
     */
    @SuppressWarnings("unused")
    protected B afterApply(A facts, B result) {
        return result;
    }

    /**
     * This method is called in case of an action evaluation error.
     * Can be used for logging purpose or to provide a default result
     * in case of error.
     * The default implementation throws the exception.
     *
     * @param facts the facts
     * @param e     the exception
     * @return the result
     */
    protected B onApplyError(A facts, RuntimeException e) {
        throw e;
    }

    @Override
    public boolean test(A facts) {
        try {
            boolean applies = beforeTest(facts) && performTest(facts);
            afterTest(facts, applies);
            return applies;
        } catch (RuntimeException e) {
            return onTestError(facts, e);
        }
    }

    @Override
    public B apply(A facts) {
        try {
            beforeApply(facts);
            B result = performApply(facts);
            return afterApply(facts, result);
        } catch (RuntimeException e) {
            return onApplyError(facts, e);
        }
    }

    /*
     * Utility interface to let Lombok generate delegating methods without
     * incurring in "clashing methods"
     */
    private interface DelegableMethods extends Comparable<Rule> {
        String getId();

        int getPriority();

        @Override
        String toString();

        @Override
        int compareTo(Rule other);

        @Override
        boolean equals(final Object other);

        @Override
        int hashCode();
    }

}
