package it.sky.rulesengine.examples.advertising;

import it.sky.rulesengine.api.Rules;
import it.sky.rulesengine.factory.JexlRuleDataParser;
import it.sky.rulesengine.factory.RulesFactory;
import it.sky.rulesengine.factory.YamlRulesFactory;
import it.sky.rulesengine.util.VoidResult;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;

import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class YamlRulesTest extends AdvertisementEngineTest {

    @Override
    protected Rules<AdFacts, VoidResult> buildRules(AdsEngine adEngine, LocalDate localDate) {
        Map<String, Object> ns = new HashMap<>();
        ns.put("dayOfWeek", DayOfWeek.class);
        ns.put("adEngine", adEngine);
        ns.put("localDate", localDate);
        JexlEngine jexlEngine = new JexlBuilder().namespaces(ns).create();

        Supplier<InputStream> is = () -> AdvertisementEngineTest.class.getClassLoader()
                .getResourceAsStream("advertisement-rules.yaml");

        JexlRuleDataParser<AdFacts, VoidResult> dataParser = JexlRuleDataParser.create(AdFacts.class, VoidResult.class)
                .withEngine(jexlEngine)
                .withDefaultResult(VoidResult.INSTANCE);

        RulesFactory<AdFacts, VoidResult> rulesFactory = YamlRulesFactory.create(is, dataParser);

        return rulesFactory.buildRules();
    }

}
