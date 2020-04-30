package it.sky.rulesengine.x.api;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.impl.CompositeRule;
import it.sky.rulesengine.core.impl.StreamingRulesEngine;
import it.sky.rulesengine.x.impl.RulesGraphEngine;
import it.sky.rulesengine.x.impl.RulesGraphImpl;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public interface RulesGraph<A, B> extends Collection<Rule<EvaluationContext<A, B>, B>> {

    /**
     * Adds the rule obtained applying the given mapper to a {@link CompositeRule}
     * having the current collection's size as id and an always applying condition.
     *
     * <p>Example:
     * <pre>
     *      rules.add(r -> r.thenRun(() -> System.out.println("Hello world!")))
     *      it's the shortcut of
     *      rules.add(CompositeRule.create(rules.size() + "").thenRun(() -> System.out.println("Hello world!")));
     * </pre>
     *
     * @param mapper the rule mapper
     * @return <code>this</code>
     */
    RulesGraph<A, B> add(Function<CompositeRule<EvaluationContext<A, B>, B>, Rule<EvaluationContext<A, B>, B>> mapper);

    /**
     * Applies the given mapping function to the current engine.
     *
     * @param mapper the rules engine mapper
     * @return <code>this</code>
     */
    RulesGraph<A, B> setRulesEngine(UnaryOperator<StreamingRulesEngine> mapper);

    /**
     * Applies the first applicable rule on the given facts.
     *
     * @param facts the facts to test
     * @return the result of the applied rule
     */
    Optional<B> applyFirst(A facts);

    /**
     * Applies all the applicable rule on the given facts.
     *
     * @param facts the facts to test
     * @return the results of the applied rules. The key is the rule's id
     */
    Map<String, B> applyAll(A facts);

    /**
     * Creates an rules graph with the given rules.
     *
     * @param rules the rules
     */
    static <A, B> RulesGraph<A, B> of(Collection<Rule<EvaluationContext<A, B>, B>> rules) {
        return of(RulesGraphEngine.INSTANCE, rules);
    }

    /**
     * Creates an rules graph with the given rules.
     *
     * @param rules the rules
     */
    @SafeVarargs
    static <A, B> RulesGraph<A, B> of(Rule<EvaluationContext<A, B>, B>... rules) {
        List<Rule<EvaluationContext<A, B>, B>> rulesList = Stream.of(rules).collect(ArrayList::new, Collection::add, Collection::addAll);
        return of(rulesList);
    }

    /**
     * Creates an rules graph with the given facts and result types.
     *
     * @param factsType  the facts type
     * @param resultType the result type
     */
    @SuppressWarnings("unused")
    static <A, B> RulesGraph<A, B> of(Class<A> factsType, Class<B> resultType) {
        return of();
    }

    /**
     * Creates an rules graph with the given rules and engine.
     *
     * @param rules       the rules
     * @param rulesEngine the engine
     */
    static <A, B> RulesGraph<A, B> of(StreamingRulesEngine rulesEngine, Collection<Rule<EvaluationContext<A, B>, B>> rules) {
        return new RulesGraphImpl<>(rulesEngine, rules);
    }

}
