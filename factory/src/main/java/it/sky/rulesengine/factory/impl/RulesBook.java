package it.sky.rulesengine.factory.impl;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.api.RulesEngine;
import it.sky.rulesengine.core.impl.CompositeRule;
import it.sky.rulesengine.core.impl.StreamingRulesEngine;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.*;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RulesBook<A, B> extends AbstractCollection<Rule<A, B>> {

    @Delegate
    @NonNull
    protected final Collection<Rule<A, B>> rules;
    @NonNull
    protected final RulesEngine rulesEngine;

    /**
     * Creates an empty rules book.
     */
    public RulesBook() {
        this(new ArrayList<>());
    }

    /**
     * Creates an rules book with the given rules.
     *
     * @param rules the rules
     */
    public RulesBook(Collection<Rule<A, B>> rules) {
        this(rules, StreamingRulesEngine.create());
    }

    /**
     * Creates an rules book with the given rules.
     *
     * @param factsType  the facts type
     * @param resultType the result type
     */
    @SuppressWarnings("unused")
    public RulesBook(Class<A> factsType, Class<B> resultType) {
        this();
    }

    /**
     * Creates a copy with the given {@link RulesEngine}.
     *
     * @param rulesEngine the rules engine
     * @return the new rules book
     */
    public RulesBook<A, B> withRulesEngine(RulesEngine rulesEngine) {
        return this.rulesEngine == rulesEngine ? this : new RulesBook<>(rules, rulesEngine);
    }

    /**
     * Adds a rule created from a {@link CompositeRule}
     * with the current the rules collection's size as id.
     *
     * <p>Example:
     * <pre>
     *      this.add(r -> r.withCondition(x -> x > y)
     *          .withRunnable(() -> System.out.println("Hello world!")))
     * </pre>
     *
     * @param ruleMapper the rule mapper
     * @return <code>this</code>
     */
    public RulesBook<A, B> add(Function<CompositeRule<A, B>, Rule<A, B>> ruleMapper) {
        rules.add(ruleMapper.apply(CompositeRule.create("" + rules.size())));
        return this;
    }

    /**
     * Applies the first applicable rule on the given facts.
     *
     * @param facts the facts to test
     * @return the result of the applied rule
     */
    public Optional<B> applyFirst(A facts) {
        return rulesEngine.applyFirst(this, facts);
    }

    /**
     * Applies all the applicable rule on the given facts.
     *
     * @param facts the facts to test
     * @return the results of the applied rules. The key is the rule's id
     */
    public Map<String, B> applyAll(A facts) {
        return rulesEngine.applyAll(this, facts);
    }

}
