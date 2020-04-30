package it.sky.rulesengine.x.api;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.impl.CompositeRule;
import it.sky.rulesengine.core.impl.StreamingRulesEngine;
import it.sky.rulesengine.x.impl.DefaultRules;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * @param <A> the facts type
 * @param <B> the result type
 */
public interface Rules<A, B> extends Collection<Rule<A, B>> {

    /**
     * Adds a new rule to this collection.
     * The rule is obtained applying the given mapper to a {@link CompositeRule}
     * having the current collection's size as id and an always applying condition.
     *
     * <p>Example:
     * <pre>
     *      rules.add(r -> r.thenRun(() -> System.out.println("Hello world!")))
     *      is the same as
     *      rules.add(CompositeRule.create(rules.size() + "").thenRun(() -> System.out.println("Hello world!")));
     * </pre>
     *
     * @param ruleMapper the rule mapper
     * @return <code>this</code>
     */
    Rules<A, B> add(Function<CompositeRule<A, B>, Rule<A, B>> ruleMapper);

    /**
     * Applies the given mapping function to the current engine.
     *
     * @param mapper the rules engine mapper
     * @return <code>this</code>
     */
    Rules<A, B> setRulesEngine(UnaryOperator<StreamingRulesEngine> mapper);

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
     * Creates a collection of rules that reflects the given collection.
     *
     * @param rules the rules
     */
    static <A, B> Rules<A, B> of(Collection<Rule<A, B>> rules) {
        return of(StreamingRulesEngine.create(), rules);
    }

    /**
     * Creates a modifiable list with the given rules.
     *
     * @param rules the rules
     */
    @SafeVarargs
    static <A, B> Rules<A, B> of(Rule<A, B>... rules) {
        List<Rule<A, B>> rulesList = Stream.of(rules).collect(ArrayList::new, Collection::add, Collection::addAll);
        return of(rulesList);
    }

    /**
     * Creates a modifiable empty list of rules with the given facts and result type.
     *
     * @param factsType  the facts type
     * @param resultType the result type
     */
    @SuppressWarnings("unused")
    static <A, B> Rules<A, B> of(Class<A> factsType, Class<B> resultType) {
        return of();
    }

    /**
     * Creates a rules collection with the given rules and engine.
     *
     * @param rules       the rules
     * @param rulesEngine the engine
     */
    static <A, B> Rules<A, B> of(StreamingRulesEngine rulesEngine, Collection<Rule<A, B>> rules) {
        return new DefaultRules<>(rulesEngine, rules);
    }

}
