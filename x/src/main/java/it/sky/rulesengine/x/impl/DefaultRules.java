package it.sky.rulesengine.x.impl;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.api.RulesEngine;
import it.sky.rulesengine.core.impl.CompositeRule;
import it.sky.rulesengine.core.impl.StreamingRulesEngine;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@RequiredArgsConstructor
public class DefaultRules<A, B> implements it.sky.rulesengine.x.api.Rules<A, B> {

    @NonNull
    protected StreamingRulesEngine rulesEngine;
    @Delegate
    @NonNull
    protected final Collection<Rule<A, B>> rules;

    @Override
    public DefaultRules<A, B> setRulesEngine(UnaryOperator<StreamingRulesEngine> mapper) {
        rulesEngine = mapper.apply(rulesEngine);
        return this;
    }

    @Override
    public DefaultRules<A, B> add(@NonNull Function<CompositeRule<A, B>, Rule<A, B>> ruleMapper) {
        CompositeRule<A, B> rule = CompositeRule.create(String.valueOf(rules.size()));
        rules.add(ruleMapper.apply(rule.when(f -> true)));
        return this;
    }

    @Override
    public Optional<B> applyFirst(A facts) {
        return rulesEngine.applyFirst(rules, facts);
    }

    @Override
    public Map<String, B> applyAll(A facts) {
        return rulesEngine.applyAll(rules, facts);
    }

}
