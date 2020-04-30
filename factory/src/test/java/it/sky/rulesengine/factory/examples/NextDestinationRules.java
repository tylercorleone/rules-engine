package it.sky.rulesengine.factory.examples;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.annotations.Fixture;
import com.flextrade.jfixture.annotations.Range;
import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.api.RulesEngine;
import it.sky.rulesengine.core.impl.StreamingRulesEngine;
import it.sky.rulesengine.factory.impl.yaml.YamlRulesFactory;
import lombok.SneakyThrows;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.MapContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NextDestinationRules {

    static final boolean RAIN = true;
    static final boolean NO_RAIN = false;
    static final Destination NO_PARTY = null;
    static final Destination BEACH_PARTY = Destination.BEACH;

    @Fixture
    @Range(min = 20, max = 50)
    Integer hotTemperature;
    @Fixture
    @Range(min = -10, max = 10)
    Integer coldTemperature;

    @BeforeEach
    void beforeEach() {
        FixtureAnnotations.initFixtures(this);
    }

    @Test
    void shouldLoadFixedResultRulesFromYaml() {
        List<Rule<JexlContext, Destination>> rules = buildRules();

        RulesEngine rulesEngine = StreamingRulesEngine.create();

        assertEquals(Destination.BEACH, rulesEngine.applyFirst(rules, given(hotTemperature, NO_RAIN, NO_PARTY)).get());
        assertEquals(Destination.HOME, rulesEngine.applyFirst(rules, given(hotTemperature, RAIN, NO_PARTY)).get());
        assertEquals(Destination.MOUNTAIN, rulesEngine.applyFirst(rules, given(coldTemperature, NO_RAIN, NO_PARTY)).get());
        assertEquals(Destination.HOME, rulesEngine.applyFirst(rules, given(coldTemperature, RAIN, NO_PARTY)).get());
        assertEquals(Destination.BEACH, rulesEngine.applyFirst(rules, given(hotTemperature, RAIN, BEACH_PARTY)).get());
    }

    enum Destination {
        BEACH, MOUNTAIN, HOME
    }

    private static MapContext given(int temperature, boolean isRaining, Destination partyLocation) {
        MapContext facts = new MapContext();
        facts.set("temperature", temperature);
        facts.set("isRaining", isRaining);
        facts.set("partyLocation", partyLocation);
        return facts;
    }

    @SneakyThrows
    private static List<Rule<JexlContext, Destination>> buildRules() {
        try (InputStream is = NextDestinationRules.class.getClassLoader().getResourceAsStream("examples/weather-rules.yaml")) {
            return YamlRulesFactory.create(() -> is, Destination.class).get();
        }
    }

}
