package it.sky.rulesengine.x.impl;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.impl.exception.RulesEngineException;
import it.sky.rulesengine.x.api.EvaluationContext;
import lombok.Data;
import lombok.NonNull;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EvaluationContextImpl<A, B> implements EvaluationContext<A, B> {

    private final Graph<Node, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    private final SzwarcfiterLauerSimpleCycles<Node, DefaultEdge> cycleDetector = new SzwarcfiterLauerSimpleCycles<>(graph);
    private final A facts;
    private final Map<String, RuleEvaluationContextImpl<A, B>> rules;

    EvaluationContextImpl(@NonNull final Collection<Rule<EvaluationContext<A, B>, B>> rules, A facts) {
        this.facts = facts;
        this.rules = rules.stream().collect(Collectors.toMap(Rule::getId, r -> new RuleEvaluationContextImpl<>(r, this)));
    }

    @Override
    public A getFacts() {
        return facts;
    }

    @Override
    public RuleEvaluationContextImpl<A, B> getRule(@NonNull String ruleId) {
        return Optional.ofNullable(rules.get(ruleId)).orElseThrow(
                () -> new IllegalArgumentException(String.format("rule '%s' not found", ruleId)));
    }

    void addEdge(Node from, Node to) {
        synchronized (graph) {
            graph.addVertex(from);
            graph.addVertex(to);
            graph.addEdge(from, to);
            List<List<Node>> cycles = cycleDetector.findSimpleCycles().stream()
                    .filter(cycle -> cycle.contains(from))
                    .collect(Collectors.toList());
            if (!cycles.isEmpty()) {
                throw new CyclicDependencyException(cycles);
            }
        }
    }

    @Data
    static class Node {
        private final String ruleId;
        private final NodeType type;

        protected Node(@NonNull Rule<?, ?> rule, @NonNull NodeType type) {
            this.ruleId = rule.getId();
            this.type = type;
        }

        @Override
        public String toString() {
            return String.format("%s (%s)", ruleId, type);
        }

        enum NodeType {
            CONDITION, ACTION
        }
    }

    private static class CyclicDependencyException extends RulesEngineException {

        public CyclicDependencyException(@NonNull List<List<Node>> cycles) {
            super("cyclic dependency found: " + cycles.stream()
                    .map(CyclicDependencyException::renderCycle)
                    .collect(Collectors.joining(", "))
            );
        }

        private static String renderCycle(List<Node> nodes) {
            return nodes.stream().map(Node::toString).collect(Collectors.joining(" -> "));
        }

    }

}
