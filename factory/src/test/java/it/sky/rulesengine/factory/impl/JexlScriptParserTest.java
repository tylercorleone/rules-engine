package it.sky.rulesengine.factory.impl;

import it.sky.rulesengine.core.impl.exception.RulesEngineException;
import it.sky.rulesengine.factory.impl.exception.ParsingException;
import it.sky.rulesengine.factory.impl.jexl.JexlScriptParser;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.MapContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class JexlScriptParserTest {

    @Mock
    JexlEngine jexlEngine;
    @Mock
    JexlContext jexlContext;

    JexlScriptParser<? super JexlContext, ?> jexlScriptParser;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        jexlScriptParser = JexlScriptParser.create(Number.class);
    }

    @Test
    void shouldCreateCondition() {
        Predicate<? super JexlContext> predicate = jexlScriptParser.parseCondition("number == 42");

        MapContext facts = new MapContext();
        facts.set("number", 0);
        assertFalse(predicate.test(facts));
        facts.set("number", 42);
        assertTrue(predicate.test(facts));
    }

    @Test
    void shouldCreateAction() {
        Function<? super JexlContext, ?> action = jexlScriptParser.parseAction("number");

        MapContext facts = new MapContext();
        facts.set("number", 42);
        assertEquals(42, action.apply(facts));
    }

    @Test
    void shouldThrowOnParsingError() {
        assertThrows(ParsingException.class, () -> jexlScriptParser.parseCondition("an illegal expression"));
        assertThrows(ParsingException.class, () -> jexlScriptParser.parseAction("an illegal expression"));
    }

    @Test
    void shouldThrowOnEvaluationError() {
        assertThrows(RulesEngineException.class, () -> jexlScriptParser.parseCondition("1/0").test(jexlContext));
        assertThrows(RulesEngineException.class, () -> jexlScriptParser.parseAction("1/0").apply(jexlContext));
    }

    @Test
    void shouldUseGivenEngine() {
        jexlScriptParser.withEngine(jexlEngine).parseCondition("true");
        verify(jexlEngine).createScript("true");
        jexlScriptParser.withEngine(jexlEngine).parseAction("true");
        verify(jexlEngine, times(2)).createScript("true");
    }

}
