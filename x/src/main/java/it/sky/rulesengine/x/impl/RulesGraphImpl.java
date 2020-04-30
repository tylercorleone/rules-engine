package it.sky.rulesengine.x.impl;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.impl.CompositeRule;
import it.sky.rulesengine.core.impl.StreamingRulesEngine;
import it.sky.rulesengine.x.api.EvaluationContext;
import it.sky.rulesengine.x.api.RulesGraph;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@RequiredArgsConstructor
public class RulesGraphImpl<A, B> implements RulesGraph<A, B> {

    @NonNull
    protected StreamingRulesEngine rulesEngine;
    @Delegate
    @NonNull
    protected final Collection<Rule<EvaluationContext<A, B>, B>> rules;

    @Override
    public RulesGraphImpl<A, B> setRulesEngine(UnaryOperator<StreamingRulesEngine> mapper) {
        rulesEngine = mapper.apply(rulesEngine);
        return this;
    }

    @Override
    public RulesGraphImpl<A, B> add(
            @NonNull Function<CompositeRule<EvaluationContext<A, B>, B>, Rule<EvaluationContext<A, B>, B>> mapper) {
        CompositeRule<EvaluationContext<A, B>, B> rule = CompositeRule.create(String.valueOf(rules.size()));
        rules.add(mapper.apply(rule.when(f -> true)));
        return this;
    }

    @Override
    public Optional<B> applyFirst(A facts) {
        return rulesEngine.applyFirst(rules, new EvaluationContextImpl<>(rules, facts));
    }

    @Override
    public Map<String, B> applyAll(A facts) {
        return rulesEngine.applyAll(rules, new EvaluationContextImpl<>(rules, facts));
    }

}
