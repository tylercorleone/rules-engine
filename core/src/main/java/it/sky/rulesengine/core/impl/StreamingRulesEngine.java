package it.sky.rulesengine.core.impl;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.api.RulesEngine;
import it.sky.rulesengine.core.impl.exception.RuleEvaluationException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * An immutable implementation of the {@link RulesEngine} interface
 * with a fluent API to customize the rules evaluation strategy.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class StreamingRulesEngine implements RulesEngine {

    private static final StreamingRulesEngine INSTANCE = new StreamingRulesEngine(s -> s, Function.identity());

    protected final UnaryOperator<Stream<?>> streamMapper;
    protected final Function<? super RuleEvaluationException, ? extends RuntimeException> exceptionMapper;

    /**
     * Creates an instance.
     *
     * @return the engine
     */
    public static StreamingRulesEngine create() {
        return INSTANCE;
    }

    /**
     * Returns a copy that applies the given mapper to the rules's stream.
     * <p>Example:
     * <pre>
     *     RulesEngine sortedEngine = RulesEngine.create()
     *          .withStreamMapping(Stream::sorted)
     *          .withStreamMapping(Stream::parallel);
     * </pre>
     *
     * @param mapper the mapper
     * @return the new engine
     */
    public StreamingRulesEngine withStreamMapping(@NonNull UnaryOperator<Stream<?>> mapper) {
        return create(s -> mapper.apply(streamMapper.apply(s)), exceptionMapper);
    }

    /**
     * Returns a copy that applies the given mapper to
     * {@link RuleEvaluationException} before throwing.
     *
     * @param mapper the mapper
     * @return the new engine
     */
    public StreamingRulesEngine withExceptionMapping(
            @NonNull Function<? super RuleEvaluationException, ? extends RuntimeException> mapper) {
        return this.exceptionMapper == mapper ? this : create(streamMapper, mapper);
    }

    @Override
    public <A, B> Optional<B> applyFirst(@NonNull Collection<Rule<A, B>> rules, A facts) {
        return stream(rules, facts)
                .filter(r -> test(r, facts))
                .findFirst()
                .map(r -> apply(r, facts));
    }

    @Override
    public <A, B> Map<String, B> applyAll(@NonNull Collection<Rule<A, B>> rules, A facts) {
        return stream(rules, facts)
                .filter(r -> test(r, facts))
                .collect(HashMap::new, (m, r) -> m.put(r.getId(), apply(r, facts)), Map::putAll);
    }

    @SuppressWarnings({"unchecked", "unused"})
    protected <A, B> Stream<Rule<A, B>> stream(Collection<Rule<A, B>> rules, A facts) {
        return (Stream<Rule<A, B>>) streamMapper.apply(rules.stream());
    }

    protected <A> boolean test(Rule<A, ?> rule, A facts) {
        try {
            return rule.test(facts);
        } catch (RuntimeException e) {
            throw exceptionMapper.apply(new RuleEvaluationException(rule, facts, e));
        }
    }

    protected <A, B> B apply(Rule<A, B> rule, A facts) {
        try {
            return rule.apply(facts);
        } catch (RuntimeException e) {
            throw exceptionMapper.apply(new RuleEvaluationException(rule, facts, e));
        }
    }

    /*
     * An overrideable factory method
     */
    protected StreamingRulesEngine create(UnaryOperator<Stream<?>> streamMapper,
                                          Function<? super RuleEvaluationException, ? extends RuntimeException> mapper) {
        return new StreamingRulesEngine(streamMapper, mapper);
    }

}
