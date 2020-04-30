package it.sky.rulesengine.factory.impl.yaml;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.sky.rulesengine.core.api.Rule;
import it.sky.rulesengine.factory.api.RuleParser;
import it.sky.rulesengine.factory.api.RulesFactory;
import it.sky.rulesengine.factory.impl.RuleModel;
import it.sky.rulesengine.factory.impl.RuleParsers;
import it.sky.rulesengine.factory.impl.exception.ParsingException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;
import org.apache.commons.jexl3.JexlContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Reads rules from YAML sources using a {@link RuleParser} to parse each rule representation.
 *
 * <p>An example using JEXL expressions:
 *
 * <pre>
 * # A rule with computed result
 * name: partyRule
 * priority: 0
 * condition: invitation != null
 * action: invitation.destination
 * ---
 * # A rule with fixed result
 * name: beachRule
 * priority: 1
 * condition: temperature &gt; 25 &amp;&amp; !isRaining
 * result: BEACH
 * ---
 * # A rule with fixed result and an action
 * name: mountainRule
 * priority: 2
 * condition: temperature &lt; 0 &amp;&amp; !isRaining
 * result: MOUNTAIN
 * action: home.closeTheWindow()
 * </pre>
 *
 * @param <A> the facts type
 * @param <B> the result type
 * @param <T> the rule representation type
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class YamlRulesFactory<A, B, T> implements RulesFactory<A, B> {

    @NonNull
    protected final Supplier<InputStream> inputSupplier;
    @NonNull
    protected final RuleParser<A, B, T> ruleParser;
    @NonNull
    protected final JavaType representationType;
    @Wither
    @NonNull
    protected final ObjectMapper yamlMapper;

    /**
     * Creates a new instance with the given facts and result type using
     * a {@link RuleParsers#jexlRuleParser(Class resultType)} parser.
     *
     * @param inputSupplier the input supplier
     * @param resultType    the result type
     * @param <A>           the facts type
     * @param <B>           the result type
     * @return the factory
     */
    public static <A extends JexlContext, B> YamlRulesFactory<A, B, RuleModel<B, String, String>> create(
            Supplier<InputStream> inputSupplier, Class<B> resultType) {
        JavaType representationType = TypeFactory.defaultInstance().constructParametricType(RuleModel.class, resultType,
                String.class, String.class);
        return create(inputSupplier, RuleParsers.jexlRuleParser(resultType), representationType);
    }

    /**
     * Creates a new instance using the given arguments.
     *
     * @param inputSupplier the input supplier
     * @param ruleParser    the rule parser
     * @param <A>           the facts type
     * @param <B>           the result type
     * @param <T>           the rule representation type
     * @return the factory
     * @see TypeFactory#constructParametricType(java.lang.Class, java.lang.Class[])
     */
    public static <A, B, T> YamlRulesFactory<A, B, T> create(Supplier<InputStream> inputSupplier,
                                                             RuleParser<A, B, T> ruleParser,
                                                             @NonNull Class<?> resultType,
                                                             Class<?>... resultTypeParameters) {
        JavaType resultReprType = TypeFactory.defaultInstance().constructParametricType(resultType, resultTypeParameters);
        JavaType conditionReprType = TypeFactory.defaultInstance().constructType(String.class);
        JavaType actionReprType = TypeFactory.defaultInstance().constructType(String.class);
        JavaType javaType = TypeFactory.defaultInstance().constructParametricType(RuleModel.class, resultReprType,
                conditionReprType, actionReprType);
        return create(inputSupplier, ruleParser, javaType);
    }

    /**
     * Creates a new instance using the given arguments.
     *
     * @param inputSupplier the input supplier
     * @param ruleParser    the rule parser
     * @param <A>           the facts type
     * @param <B>           the result type
     * @param <T>           the rule representation type.
     *                      E.g. <code>TypeFactory.defaultInstance().constructParametricType(RuleModel.class,
     *                      resultType, String.class, String.class)</code>. For more complex types see
     *                      {@link TypeFactory#constructParametricType(Class, JavaType...)}
     * @return the factory
     */
    public static <A, B, T> YamlRulesFactory<A, B, T> create(Supplier<InputStream> inputSupplier,
                                                             RuleParser<A, B, T> ruleParser,
                                                             JavaType representationType) {
        return new YamlRulesFactory<>(inputSupplier, ruleParser, representationType, new ObjectMapper(new YAMLFactory()));
    }

    @Override
    public List<Rule<A, B>> get() {
        try (InputStream inputStream = inputSupplier.get()) {
            return ruleModels(inputStream).map(ruleParser::parseRule).collect(Collectors.toList());
        } catch (IOException e) {
            throw new ParsingException("couldn't read from the input", e);
        }
    }

    protected Stream<T> ruleModels(@NonNull InputStream inputStream) throws IOException {
        Iterator<T> it = yamlMapper.readValues(yamlMapper.getFactory().createParser(inputStream), representationType);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED), false);
    }

}
