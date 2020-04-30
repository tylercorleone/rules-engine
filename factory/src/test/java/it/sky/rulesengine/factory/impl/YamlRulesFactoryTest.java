package it.sky.rulesengine.factory.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import it.sky.rulesengine.factory.api.RuleParser;
import it.sky.rulesengine.factory.examples.NextDestinationRules;
import it.sky.rulesengine.factory.impl.yaml.YamlRulesFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;

import static org.mockito.Mockito.verify;

class YamlRulesFactoryTest {

    @Mock
    RuleParser<Void, DummyModel, RuleModel<DummyModel, DummyModel, DummyModel>> ruleParser;

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldParseYaml() {
        parseYaml();
        verify(ruleParser).parseRule(expectedModel(1));
        verify(ruleParser).parseRule(expectedModel(2));
    }

    @SneakyThrows
    private void parseYaml() {
        try (InputStream is = NextDestinationRules.class.getClassLoader().getResourceAsStream("yaml-rules-factory-test.yaml")) {
            JavaType resultReprType = TypeFactory.defaultInstance().constructType(DummyModel.class);
            JavaType conditionReprType = TypeFactory.defaultInstance().constructType(DummyModel.class);
            JavaType actionReprType = TypeFactory.defaultInstance().constructType(DummyModel.class);
            JavaType representationType = TypeFactory.defaultInstance().constructParametricType(RuleModel.class, resultReprType,
                    conditionReprType, actionReprType);
            YamlRulesFactory.create(() -> is, ruleParser, representationType).get();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class DummyModel {
        private String stringField;
        private Integer integerField;
    }

    private static RuleModel<DummyModel, DummyModel, DummyModel> expectedModel(int id) {
        DummyModel dummyModel = new DummyModel("string " + id, id);
        RuleModel<DummyModel, DummyModel, DummyModel> model = new RuleModel<>();
        model.setId("rule" + id);
        model.setPriority(id);
        model.setCondition(dummyModel);
        model.setAction(dummyModel);
        model.setResult(dummyModel);
        return model;
    }
}
