package it.sky.rulesengine.x.impl;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.x.api.EvaluationContext;
import it.sky.rulesengine.x.api.RuleEvaluationContext;
import it.sky.rulesengine.x.impl.EvaluationContextImpl.Node;
import it.sky.rulesengine.x.impl.EvaluationContextImpl.Node.NodeType;
import it.sky.rulesengine.x.impl.decorator.RuleDecorator;
import lombok.NonNull;

public class RuleEvaluationContextImpl<A, B> implements RuleEvaluationContext<B> {

    private static final ThreadLocal<Node> CURRENT_NODE = new ThreadLocal<>();

    private final EvaluationContextImpl<A, B> evaluationContext;
    private final LazyRule lazyRule;
    private RuleStatus status = RuleStatus.WAITING;
    private RuntimeException failCause;
    private B result;

    RuleEvaluationContextImpl(@NonNull final Rule<EvaluationContext<A, B>, B> delegate,
                              @NonNull final EvaluationContextImpl<A, B> evaluationContext) {
        this.lazyRule = new LazyRule(delegate);
        this.evaluationContext = evaluationContext;
    }

    Rule<EvaluationContext<A, B>, B> getContextMaintainer() {
        return lazyRule;
    }

    @Override
    public RuleStatus getStatus() {
        if (lazyRule.test(evaluationContext)) {
            lazyRule.apply(evaluationContext);
        }
        return status;
    }

    @Override
    public boolean applies() {
        return lazyRule.test(evaluationContext);
    }

    @Override
    public boolean applied() {
        if (lazyRule.test(evaluationContext)) {
            lazyRule.apply(evaluationContext);
        }
        return status == RuleStatus.APPLIED;
    }

    @Override
    public B getResult() {
        if (lazyRule.test(evaluationContext)) {
            lazyRule.apply(evaluationContext);
        }
        return result;
    }

    private class LazyRule extends RuleDecorator<EvaluationContext<A, B>, B> {

        private final Node conditionNode = new Node(this, NodeType.CONDITION);
        private final Node actionNode = new Node(this, NodeType.ACTION);

        LazyRule(Rule<EvaluationContext<A, B>, B> delegate) {
            super(delegate);
        }

        @Override
        public boolean test(EvaluationContext<A, B> evaluationContext) {
            Node sourceNode = CURRENT_NODE.get();
            try {
                if (sourceNode != null) {
                    RuleEvaluationContextImpl.this.evaluationContext.addEdge(CURRENT_NODE.get(), conditionNode);
                }
                CURRENT_NODE.set(conditionNode);
                synchronized (conditionNode) {
                    if (status == RuleStatus.TEST_TRUE || status == RuleStatus.APPLYING
                            || status == RuleStatus.APPLIED || status == RuleStatus.APPLY_FAILED) {
                        return true;
                    } else if (status != RuleStatus.WAITING) {
                        throw failCause != null ? failCause : new IllegalStateException(String.format("unexpected status '%s'", status));
                    }
                    return super.test(evaluationContext);
                }
            } finally {
                if (sourceNode != null) {
                    CURRENT_NODE.set(sourceNode);
                } else {
                    CURRENT_NODE.remove();
                }
            }
        }

        @Override
        public B apply(EvaluationContext<A, B> evaluationContext) {
            Node sourceNode = CURRENT_NODE.get();
            try {
                if (sourceNode != null) {
                    RuleEvaluationContextImpl.this.evaluationContext.addEdge(CURRENT_NODE.get(), actionNode);
                }
                CURRENT_NODE.set(actionNode);
                synchronized (actionNode) {
                    if (status == RuleStatus.APPLIED) {
                        return result;
                    } else if (status != RuleStatus.TEST_TRUE) {
                        throw failCause != null ? failCause : new IllegalStateException(String.format("unexpected status '%s'", status));
                    }
                    return super.apply(evaluationContext);
                }
            } finally {
                if (sourceNode != null) {
                    CURRENT_NODE.set(sourceNode);
                } else {
                    CURRENT_NODE.remove();
                }
            }
        }

        @Override
        protected boolean beforeTest(EvaluationContext<A, B> evaluationContext) {
            status = RuleStatus.TESTING;
            return true;
        }

        @Override
        protected void afterTest(EvaluationContext<A, B> evaluationContext, boolean applies) {
            status = applies ? RuleStatus.TEST_TRUE : RuleStatus.TEST_FALSE;
        }

        @Override
        protected boolean onTestError(EvaluationContext<A, B> evaluationContext, RuntimeException e) {
            status = RuleStatus.TEST_FAILED;
            failCause = e;
            throw e;
        }

        @Override
        protected void beforeApply(EvaluationContext<A, B> evaluationContext) {
            status = RuleStatus.APPLYING;
        }

        @Override
        protected B afterApply(EvaluationContext<A, B> evaluationContext, B result) {
            status = RuleStatus.APPLIED;
            RuleEvaluationContextImpl.this.result = result;
            return result;
        }

        @Override
        protected B onApplyError(EvaluationContext<A, B> evaluationContext, RuntimeException e) {
            status = RuleStatus.APPLY_FAILED;
            failCause = e;
            throw e;
        }

    }

}
