package it.sky.rulesengine.factory.examples;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.factory.impl.RuleParsers;
import it.sky.rulesengine.factory.impl.yaml.YamlRulesFactory;
import lombok.SneakyThrows;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.MapContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MathFunctionsTestCases {

    @ParameterizedTest
    @MethodSource("mathFunctionsTestCases")
    void shouldLoadRulesFromYaml(Number x, Number expectedInverse, Number expectedSquare) {
        List<Rule<JexlContext, Number>> rules = rulesWithResultType(Number.class);

        Map<String, Number> results = DefaultRulesEngine.create().applyAll(rules, valueIs(x));

        assertEquals(x, results.get("identity"));
        assertEquals(expectedInverse, results.get("inverse"));
        assertEquals(expectedSquare, results.get("square"));
        assertEquals(0, results.get("timesZero"));
    }

    @Test
    void shouldRespectTypes() {
        List<Rule<JexlContext, String>> rules = rulesWithResultType(String.class);

        Rule<JexlContext, String> timesZero = rules.stream()
                .filter(r -> r.getId().equals("timesZero"))
                .findAny()
                .orElseThrow(AssertionError::new);

        assertEquals("0", timesZero.apply(valueIs(42)));
    }

    static Stream<Arguments> mathFunctionsTestCases() {
        return Stream.of( // Number x, Number inverse, Number square
                Arguments.of(-1.0, -1.0, 1.0),
                Arguments.of(0.0, null, 0.0),
                Arguments.of(1.0, 1.0, 1.0),
                Arguments.of(2.0, 0.5, 4.0)
        );
    }

    static MapContext valueIs(Object value) {
        MapContext facts = new MapContext();
        facts.set("x", value);
        return facts;
    }

    @SneakyThrows
    private static <B> List<Rule<JexlContext, B>> rulesWithResultType(Class<B> resultType) {
        Map<String, Object> namespaces = new HashMap<>();
        namespaces.put(null, System.out);
        JexlEngine engine = new JexlBuilder().namespaces(namespaces).create();
        try (InputStream is = NextDestinationRules.class.getClassLoader().getResourceAsStream("examples/math-functions.yaml")) {
            return YamlRulesFactory.create(() -> is, RuleParsers.jexlRuleParser(resultType, engine), resultType).get();
        }
    }

}
