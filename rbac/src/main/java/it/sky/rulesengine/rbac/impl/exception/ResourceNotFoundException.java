package it.sky.rulesengine.rbac.impl.exception;

import it.sky.rulesengine.core.impl.exception.RulesEngineException;

/**
 * An exception that signals that the requested operation has not be found.
 */
public class ResourceNotFoundException extends RulesEngineException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
