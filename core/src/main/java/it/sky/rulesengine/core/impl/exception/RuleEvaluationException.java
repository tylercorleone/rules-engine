package it.sky.rulesengine.core.impl.exception;

import it.sky.rulesengine.core.api.Rule;
import lombok.Getter;
import lombok.NonNull;

/**
 * An exception that can occur during the evaluation of a rule's
 * {@link Rule#test(Object) condition} or {@link Rule#apply(Object) action}.
 *
 * <p>The rule, the facts and the causing exception are available for further investigations.
 */
@Getter
public class RuleEvaluationException extends RulesEngineException {

    private final transient Rule<?, ?> rule;
    private final transient Object facts;

    public <A> RuleEvaluationException(@NonNull final Rule<? super A, ?> rule, final A facts,
                                       @NonNull final Throwable cause) {
        super(String.format("an error occurred during the evaluation of the rule '%s'", rule.getId()), cause);
        this.rule = rule;
        this.facts = facts;
    }

}
