package it.sky.rulesengine.core.impl;

import it.sky.rulesengine.core.api.Rule;

import java.util.function.*;

/**
 * An immutable {@link Rule} with a fluent creational API.
 *
 * @param <A> the facts type
 * @param <B> the result type
 */
public interface CompositeRule<A, B> extends Rule<A, B> {

    /**
     * Creates a never applying rule with zero priority.
     *
     * @param id the id
     * @return the rule
     */
    static <A, B> CompositeRule<A, B> create(String id) {
        return create(id, f -> false, f -> null);
    }

    /**
     * Creates a never applying rule with zero priority.
     *
     * @param id         the id
     * @param factsType  the facts type
     * @param resultType the result type
     * @return the rule
     */
    @SuppressWarnings("unused")
    static <A, B> CompositeRule<A, B> create(String id, Class<A> factsType, Class<B> resultType) {
        return create(id);
    }

    /**
     * Creates a rule with the given predicate as condition, function as action and zero priority.
     *
     * @param id        the id
     * @param predicate the predicate
     * @param function  the function
     * @return the rule
     */
    static <A, B> CompositeRule<A, B> create(String id, Predicate<A> predicate, Function<A, B> function) {
        return CompositeRuleSupport.create(id, 0, predicate, function, null, null, null, null, null);
    }

    /**
     * Creates a rule with the given predicate as condition, result and zero priority.
     *
     * @param id        the id
     * @param predicate the predicate
     * @param result    the result
     * @return the rule
     */
    static <A, B> CompositeRule<A, B> create(String id, Predicate<A> predicate, B result) {
        return CompositeRuleSupport.create(id, 0, predicate, f -> null, null, result, null, null, null);
    }

    /**
     * Returns a copy with the given id.
     *
     * @param id the id
     * @return the new rule
     */
    CompositeRule<A, B> withId(String id);

    /**
     * Returns a copy with the given priority.
     *
     * @param priority the priority
     * @return the new rule
     */
    CompositeRule<A, B> withPriority(int priority);

    /**
     * Returns a copy with the given predicate as condition.
     *
     * @param predicate the predicate
     * @return the new rule
     */
    CompositeRule<A, B> when(Predicate<? super A> predicate);

    /**
     * Returns a copy with the given boolean supplier as condition.
     *
     * @param booleanSupplier the boolean supplier
     * @return the new rule
     */
    CompositeRule<A, B> when(BooleanSupplier booleanSupplier);

    /**
     * Returns a copy with the given function as action.
     *
     * <p>The eventual {@link #thenReturn(B)} value will be ignored.
     *
     * @param function the function
     * @return the new rule
     */
    CompositeRule<A, B> then(Function<? super A, ? extends B> function);

    /**
     * Returns a copy with the given consumer as action.
     *
     * <p>The eventual {@link #thenReturn(B)} value will be returned.
     *
     * @param consumer the consumer
     * @return the new rule
     */
    CompositeRule<A, B> thenConsume(Consumer<? super A> consumer);

    /**
     * Returns a copy with the given supplier as action.
     *
     * <p>The eventual {@link #thenReturn(B)} value will be ignored.
     *
     * @param supplier the supplier
     * @return the new rule
     */
    CompositeRule<A, B> thenSupply(Supplier<? extends B> supplier);

    /**
     * Returns a copy with the given runnable as action.
     *
     * <p>The eventual {@link #thenReturn(B)} value will be returned.
     *
     * @param runnable the runnable
     * @return the new rule
     */
    CompositeRule<A, B> thenRun(Runnable runnable);

    /**
     * Returns a copy where the given result, if not null,
     * will override the action's return value.
     *
     * @param result the result
     * @return the new rule
     */
    CompositeRule<A, B> thenReturn(B result);

    /**
     * Returns a copy with the given function as else action.
     *
     * <p>The copy will be an always applying rule, and the condition will
     * be used only to decide which between the "then or else path" apply.
     *
     * <p>The eventual {@link #orElseReturn(B)} value will be ignored.
     *
     * @param function the function
     * @return the new rule
     */
    CompositeRule<A, B> orElse(Function<? super A, ? extends B> function);

    /**
     * Returns a copy with the given consumer as else action.
     *
     * <p>The copy will be an always applying rule, and the condition will
     * be used only to decide which between the "then or else path" apply.
     *
     * <p>The eventual {@link #orElseReturn(B)} value will be returned.
     *
     * @param consumer the consumer
     * @return the new rule
     */
    CompositeRule<A, B> orElseConsume(Consumer<? super A> consumer);

    /**
     * Returns a copy with the given supplier as else action.
     *
     * <p>The copy will be an always applying rule, and the condition will
     * be used only to decide which between the "then or else path" apply.
     *
     * <p>The eventual {@link #orElseReturn(B)} value will be ignored.
     *
     * @param supplier the supplier
     * @return the new rule
     */
    CompositeRule<A, B> orElseSupply(Supplier<? extends B> supplier);

    /**
     * Returns a copy with the given runnable as else action.
     *
     * <p>The copy will be an always applying rule, and the condition will
     * be used only to decide which between the "then or else path" apply.
     *
     * <p>The eventual {@link #orElseReturn(B)} value will be returned.
     *
     * @param runnable the runnable
     * @return the new rule
     */
    CompositeRule<A, B> orElseRun(Runnable runnable);

    /**
     * Returns a copy where the given result, if not null,
     * will override the else action's return value.
     *
     * <p>The copy will be an always applying rule, and the condition will
     * be used only to decide which between the "then or else path" apply.
     *
     * @param result the result
     * @return the new rule
     */
    CompositeRule<A, B> orElseReturn(B result);

}
