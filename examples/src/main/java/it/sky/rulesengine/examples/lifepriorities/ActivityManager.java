package it.sky.rulesengine.examples.lifepriorities;

import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.core.api.RulesEngine;
import it.sky.rulesengine.factory.impl.yaml.YamlRulesFactory;
import lombok.SneakyThrows;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.MapContext;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

class ActivityManager {

    private static final RulesEngine rulesEngine = DefaultRulesEngine.create();

    private final List<Rule<JexlContext, Activity>> rules;

    @SneakyThrows
    ActivityManager() {
        try (InputStream is = ActivityManager.class.getClassLoader().getResourceAsStream("life-priorities.yaml")) {
            rules = YamlRulesFactory.create(() -> is, Activity.class).get();
        }
    }

    Optional<Activity> evaluate(MapContext facts) {
        return rulesEngine.applyFirst(rules, facts);
    }

}
