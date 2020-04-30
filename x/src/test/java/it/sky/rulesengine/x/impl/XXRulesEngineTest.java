package it.sky.rulesengine.x.impl;

import it.sky.rulesengine.x.api.EvaluationContext;
import it.sky.rulesengine.x.api.RulesGraph;
import it.sky.rulesengine.x.impl.decorator.RuleLogger;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XXRulesEngineTest {

    @Test
    void test() {
        RulesGraph<Double, Double> rulesGraph = RulesGraph.of();
        rulesGraph.add(r -> new RuleLogger<>(r.withId("double").then(c -> c.getRule("identity").getResult() * 2.0)))
                .add(r -> new RuleLogger<>(r.withId("inverse").when(c -> c.getFacts() != 0).then(c -> 1.0 / c.getFacts())))
                .add(r -> new RuleLogger<>(r.withId("triple").then(c -> c.getRule("quintuple").getResult() - c.getRule("double").getResult())))
                .add(r -> new RuleLogger<>(r.withId("quintuple").then(c -> c.getRule("fourfold").getResult() + c.getFacts())))
                .add(r -> new RuleLogger<>(r.withId("identity").then(EvaluationContext::getFacts)))
//                .add(r -> new LoggedRule<>(r.withId("fourfold").then(c ->  4.0 * c.getFacts())))
                .add(r -> new RuleLogger<>(r.withId("fourfold").then(c -> c.getRule("triple").getResult() - c.getFacts())))
                .add(r -> new RuleLogger<>(r.withId("absolute").when(c -> c.getFacts() >= 0).then(EvaluationContext::getFacts).orElse(c -> -1.0 * c.getFacts())))
                .setRulesEngine(r -> r.withStreamMapping(Stream::parallel));

        Double value = -2.0;
        Map<String, Double> result = rulesGraph.applyAll(value);

        assertEquals((Double) (1.0 / value), result.get("inverse"));
        assertEquals((Double) (Math.abs(value)), result.get("absolute"));
        assertEquals(value, result.get("identity"));
        assertEquals((Double) (value * 2.0), result.get("double"));
        assertEquals((Double) (value * 3.0), result.get("triple"));
        assertEquals((Double) (value * 4.0), result.get("fourfold"));
        assertEquals((Double) (value * 5.0), result.get("quintuple"));
    }

}
