package it.sky.rulesengine.factory.impl;

import it.sky.rulesengine.factory.impl.jexl.JexlScriptParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleParsers {

    public static <A extends JexlContext, B> GenericRuleParser<A, B, String, String> jexlRuleParser(Class<B> resultType) {
        return jexlRuleParser(resultType, new JexlBuilder().create());
    }

    public static <A extends JexlContext, B> GenericRuleParser<A, B, String, String> jexlRuleParser(Class<B> resultType,
                                                                                                    JexlEngine jexlEngine) {
        JexlScriptParser<A, B> parser = JexlScriptParser.create(resultType).withEngine(jexlEngine);
        return GenericRuleParser.create(parser, parser);
    }

}
