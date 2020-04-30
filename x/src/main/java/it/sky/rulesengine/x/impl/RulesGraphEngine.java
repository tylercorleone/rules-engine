package it.sky.rulesengine.x.impl;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.impl.StreamingRulesEngine;
import it.sky.rulesengine.core.impl.exception.RuleEvaluationException;
import it.sky.rulesengine.x.api.EvaluationContext;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class RulesGraphEngine extends StreamingRulesEngine {

    public static final RulesGraphEngine INSTANCE = new RulesGraphEngine(s -> s, Function.identity());

    protected RulesGraphEngine(UnaryOperator<Stream<?>> streamMapper,
                               Function<? super RuleEvaluationException, ? extends RuntimeException> exceptionMapper) {
        super(streamMapper, exceptionMapper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <A, B> Stream<Rule<A, B>> stream(Collection<Rule<A, B>> rules, A facts) {
        if (!(facts instanceof EvaluationContextImpl)) {
            throw new IllegalStateException(String.format("%s can be used only with %s facts", RulesGraphEngine.class,
                    EvaluationContextImpl.class));
        }
        return super.stream(rules, facts)
                .map(r -> (Rule<A, B>) ((EvaluationContextImpl<?, B>) facts).getRule(r.getId()).getContextMaintainer());
    }

    @Override
    protected RulesGraphEngine create(UnaryOperator<Stream<?>> streamMapper,
                                      Function<? super RuleEvaluationException, ? extends RuntimeException> exceptionMapper) {
        return new RulesGraphEngine(streamMapper, exceptionMapper);
    }

}
