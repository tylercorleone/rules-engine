package it.sky.rulesengine.factory.impl;

import it.sky.rulesengine.core.api.Rule;
import lombok.Data;

/**
 * A Plain Old Java Object representation of a {@link Rule}.
 *
 * <p>Contains a rule's data. E.g. a JEXL expression for the
 * condition and the action, an object for the result etc.
 *
 * @param <B> the result type
 * @param <C> the condition representation type
 * @param <D> the action representation type
 */
@Data
public class RuleModel<B, C, D> {
    private String id;
    private int priority;
    private B result;
    private C condition;
    private D action;
}
