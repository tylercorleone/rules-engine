package it.sky.rulesengine.core.impl.exception;

/**
 * A Rules Engine runtime exception.
 */
public class RulesEngineException extends RuntimeException {

    public RulesEngineException(final String message) {
        super(message);
    }

    public RulesEngineException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
