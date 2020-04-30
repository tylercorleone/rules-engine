package it.sky.rulesengine.core.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.function.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompositeRuleSupport {

    public static <B, A> CompositeRule<A, B> create(final String id, int priority, final Predicate<? super A> predicate,
                                                    final Function<? super A, ? extends B> thenFunction, final Consumer<? super A> thenConsumer, B thenResult,
                                                    final Function<? super A, ? extends B> elseFunction, final Consumer<? super A> elseConsumer, B elseResult) {
        return new DefaultCompositeRuleImpl<>(id, priority, predicate, thenFunction, thenConsumer, thenResult, elseFunction, elseConsumer, elseResult);
    }

    protected static class DefaultCompositeRuleImpl<A, B> extends AbstractRule<A, B> implements CompositeRule<A, B> {

        final Predicate<? super A> predicate;
        final Function<? super A, ? extends B> thenFunction;
        final Consumer<? super A> thenConsumer;
        final B thenResult;
        final Function<? super A, ? extends B> elseFunction;
        final Consumer<? super A> elseConsumer;
        final B elseResult;

        @Override
        public CompositeRule<A, B> withId(String id) {
            return this.id.equals(id) ? this
                    : create(id, priority, predicate, thenFunction, thenConsumer, thenResult, elseFunction, elseConsumer, elseResult);
        }

        @Override
        public CompositeRule<A, B> withPriority(int priority) {
            return this.priority == priority ? this
                    : create(id, priority, predicate, thenFunction, thenConsumer, thenResult, elseFunction, elseConsumer, elseResult);
        }

        @Override
        public CompositeRule<A, B> when(Predicate<? super A> predicate) {
            return this.predicate == predicate ? this
                    : create(id, priority, predicate, thenFunction, thenConsumer, thenResult, elseFunction, elseConsumer, elseResult);
        }

        @Override
        public CompositeRule<A, B> when(@NonNull final BooleanSupplier booleanSupplier) {
            return create(id, priority, f -> booleanSupplier.getAsBoolean(), thenFunction, thenConsumer, thenResult, elseFunction, elseConsumer, elseResult);
        }

        @Override
        public CompositeRule<A, B> then(@NonNull final Function<? super A, ? extends B> function) {
            return this.thenFunction == function ? this
                    : create(id, priority, predicate, function, null, null, elseFunction, elseConsumer, elseResult);
        }

        @Override
        public CompositeRule<A, B> thenConsume(@NonNull final Consumer<? super A> consumer) {
            return this.thenConsumer == consumer ? this
                    : create(id, priority, predicate, null, consumer, thenResult, elseFunction, elseConsumer, elseResult);
        }

        @Override
        public CompositeRule<A, B> thenSupply(@NonNull final Supplier<? extends B> supplier) {
            return then(f -> supplier.get());
        }

        @Override
        public CompositeRule<A, B> thenRun(@NonNull final Runnable runnable) {
            return thenConsume(f -> runnable.run());
        }

        @Override
        public CompositeRule<A, B> thenReturn(B result) {
            return this.thenResult == result ? this : create(id, priority, predicate, thenFunction, thenConsumer, result, elseFunction, elseConsumer, elseResult);
        }

        @Override
        public CompositeRule<A, B> orElse(@NonNull final Function<? super A, ? extends B> function) {
            return this.elseFunction == function ? this
                    : create(id, priority, predicate, thenFunction, thenConsumer, thenResult, function, null, null);
        }

        @Override
        public CompositeRule<A, B> orElseConsume(@NonNull final Consumer<? super A> consumer) {
            return this.elseConsumer == consumer ? this
                    : create(id, priority, predicate, thenFunction, thenConsumer, thenResult, null, consumer, elseResult);
        }

        @Override
        public CompositeRule<A, B> orElseSupply(@NonNull final Supplier<? extends B> supplier) {
            return orElse(f -> supplier.get());
        }

        @Override
        public CompositeRule<A, B> orElseRun(@NonNull final Runnable runnable) {
            return orElseConsume(f -> runnable.run());
        }

        @Override
        public CompositeRule<A, B> orElseReturn(B result) {
            return this.elseResult == result ? this : create(id, priority, predicate, thenFunction, thenConsumer, thenResult, elseFunction, elseConsumer, result);
        }

        protected DefaultCompositeRuleImpl(final String id, int priority, @NonNull final Predicate<? super A> predicate,
                                           final Function<? super A, ? extends B> thenFunction, final Consumer<? super A> thenConsumer, B thenResult,
                                           final Function<? super A, ? extends B> elseFunction, final Consumer<? super A> elseConsumer, B elseResult) {
            super(id, priority);
            if (!Boolean.logicalXor(thenFunction != null, thenConsumer != null)) throw new IllegalStateException();
            this.predicate = predicate;
            this.thenFunction = thenFunction;
            this.thenConsumer = thenConsumer;
            this.thenResult = thenResult;
            this.elseFunction = elseFunction;
            this.elseConsumer = elseConsumer;
            this.elseResult = elseResult;
        }

        @Override
        public boolean test(A facts) {
            if (elseFunction != null || elseConsumer != null || elseResult != null) {
                // if the else path is present, then this rule always applies
                return true;
            }
            return predicate.test(facts);
        }

        @Override
        public B apply(A facts) {
            boolean testResult = true;
            if (elseFunction != null || elseConsumer != null || elseResult != null) {
                // deferred test, the predicate only tells **what** to apply
                testResult = predicate.test(facts);
            }

            if (testResult) {
                if (thenFunction != null) {
                    B functionResult = thenFunction.apply(facts);
                    return thenResult != null ? thenResult : functionResult;
                } else {
                    thenConsumer.accept(facts);
                    return thenResult;
                }
            }

            // else path
            if (elseFunction != null) {
                B functionResult = elseFunction.apply(facts);
                return elseResult != null ? elseResult : functionResult;
            } else if (elseConsumer != null) {
                elseConsumer.accept(facts);
            }
            return elseResult;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            return super.equals(other);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        /*
         * An overrideable factory method
         */
        protected CompositeRule<A, B> create(String id, int priority, Predicate<? super A> predicate,
                                             Function<? super A, ? extends B> thenFunction, Consumer<? super A> thenConsumer, B thenResult,
                                             Function<? super A, ? extends B> elseFunction, Consumer<? super A> elseConsumer, B elseResult) {
            return new DefaultCompositeRuleImpl<>(id, priority, predicate, thenFunction, thenConsumer, thenResult, elseFunction, elseConsumer, elseResult);
        }

    }

}
