package it.sky.rulesengine.core.api;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * The purpose of a Rules Engine is to {@link Rule#test(Object) test} some facts
 * on a collection of rules and then {@link Rule#apply(Object) apply} the applicable ones.
 */
public interface RulesEngine {

    /**
     * Applies the first applicable rule on the given facts.
     *
     * @param <A>   the facts type
     * @param <B>   the result type
     * @param rules the rules to evaluate
     * @param facts the facts to test
     * @return the result of the rule
     */
    <A, B> Optional<B> applyFirst(Collection<Rule<A, B>> rules, A facts);

    /**
     * Applies all the applicable rules on the given facts.
     *
     * @param <A>   the facts type
     * @param <B>   the result type
     * @param rules the rules to evaluate
     * @param facts the facts to test
     * @return the results of the rules. The key is the rule's id
     */
    <A, B> Map<String, B> applyAll(Collection<Rule<A, B>> rules, A facts);

}
