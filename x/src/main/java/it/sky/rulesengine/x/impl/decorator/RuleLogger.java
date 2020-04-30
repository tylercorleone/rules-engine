package it.sky.rulesengine.x.impl.decorator;

import it.sky.rulesengine.core.api.Rule;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Intercepts and logs calls to the delegate's {@link Rule#test(Object) test} and
 * {@link Rule#apply(Object) apply}, before and after the evaluation.
 *
 * <p>Since every application may use a different error handling strategy,
 * errors are NOT logged by default. You can enable them by using the dedicated flag.
 *
 * @param <A> the facts type
 * @param <B> the result type
 */
public class RuleLogger<A, B> extends RuleDecorator<A, B> {

    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(RuleLogger.class);

    private final Logger logger;
    private final boolean logErrors;

    /**
     * Creates a new logger for the given delegate.
     *
     * @param delegate the delegate
     */
    public RuleLogger(Rule<A, B> delegate) {
        this(delegate, DEFAULT_LOGGER, false);
    }

    /**
     * Creates a new logger for the given delegate using the given logger and error logging strategy.
     *
     * @param delegate  the delegate
     * @param logger    the logger
     * @param logErrors true to log errors, false otherwise
     */
    public RuleLogger(Rule<A, B> delegate, @NonNull final Logger logger, boolean logErrors) {
        super(delegate);
        this.logger = logger;
        this.logErrors = logErrors;
    }

    @Override
    protected boolean performTest(A facts) {
        logger.debug("testing rule '{}'", getId());
        return delegate.test(facts);
    }

    @Override
    protected void afterTest(A facts, boolean applies) {
        logger.debug("rule '{}' {}applies", getId(), applies ? "" : "don't ");
    }

    @Override
    protected boolean onTestError(A facts, RuntimeException e) {
        if (logErrors) {
            logger.error("an error occurred while testing rule '{}' ", getId(), e);
        }
        throw e;
    }

    @Override
    protected B performApply(A facts) {
        logger.debug("applying rule '{}'", getId());
        return delegate.apply(facts);
    }

    @Override
    protected B afterApply(A facts, B result) {
        logger.debug("rule '{}' applied", getId());
        return result;
    }

    @Override
    protected B onApplyError(A facts, RuntimeException e) {
        if (logErrors) {
            logger.error("an error occurred while applying rule '{}' ", getId(), e);
        }
        throw e;
    }

}
