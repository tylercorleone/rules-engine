package it.sky.rulesengine.x.api;

/**
 * TODO
 *
 * @param <B> the result type
 */
public interface RuleEvaluationContext<B> {

    /**
     * Returns the status of the rule, that is the outcome, in the current context.
     *
     * <p>Causes the evaluation of the rule's condition and action, if not already done.
     *
     * @return the status
     */
    RuleStatus getStatus();

    /**
     * Returns true if the rule applies to the facts of the current context.
     *
     * <p>Causes the evaluation of the rule's condition, if not already done.
     *
     * @return true if the rule applies
     */
    boolean applies();

    /**
     * Returns true if the rule has been successfully applied to the facts of the current context.
     *
     * <p>Causes the evaluation of the rule's condition and action, if not already done.
     *
     * @return true if the rule has been applied
     */
    boolean applied();

    /**
     * Returns the result of the rule if can be applied.
     *
     * <p>Causes the evaluation of the rule's condition and action, if not already done.
     *
     * @return the result or null if the rule doesn't apply
     */
    B getResult();

    /**
     * Represents the status of the rule in the current context.
     */
    enum RuleStatus {
        WAITING, TESTING, TEST_TRUE, TEST_FALSE, TEST_FAILED, APPLYING, APPLIED, APPLY_FAILED
    }

}
