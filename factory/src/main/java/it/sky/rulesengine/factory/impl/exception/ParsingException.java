package it.sky.rulesengine.factory.impl.exception;

import it.sky.rulesengine.core.impl.exception.RulesEngineException;

/**
 * A generic parsing exception.
 */
public class ParsingException extends RulesEngineException {

    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
