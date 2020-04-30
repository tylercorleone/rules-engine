package it.sky.rulesengine.factory.impl.jexl;

import it.sky.rulesengine.core.impl.exception.RulesEngineException;
import it.sky.rulesengine.factory.api.ActionParser;
import it.sky.rulesengine.factory.api.ConditionParser;
import it.sky.rulesengine.factory.impl.exception.ParsingException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.jexl3.*;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A JEXL condition and action parser.
 *
 * @param <A> the facts type
 * @param <B> the result type
 * @see <a href="https://commons.apache.org/proper/commons-jexl">Apache JEXL syntax</a>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class JexlScriptParser<A extends JexlContext, B> implements ConditionParser<A, String>, ActionParser<A, B, String> {

    @NonNull
    protected final JexlEngine jexlEngine;
    @NonNull
    protected final Class<B> resultType;

    /**
     * Creates an instance that uses a default {@link JexlEngine}
     * to {@link JexlEngine#createExpression(String) create} scripts.
     *
     * @return the parser
     */
    public static <A extends JexlContext, B> JexlScriptParser<A, B> create(Class<B> resultType) {
        return new JexlScriptParser<>(new JexlBuilder().create(), resultType);
    }

    /**
     * Returns a copy that uses the given {@link JexlEngine}
     * to {@link JexlEngine#createExpression(String) create} scripts.
     *
     * @param jexlEngine the engine
     * @return the new parser
     */
    @SuppressWarnings("unchecked")
    public <X extends JexlContext> JexlScriptParser<X, B> withEngine(@NonNull final JexlEngine jexlEngine) {
        return this.jexlEngine == jexlEngine ? (JexlScriptParser<X, B>) this : new JexlScriptParser<>(jexlEngine, resultType);
    }

    /**
     * Creates a condition from the given JEXL script.
     *
     * @param script the script
     * @return the condition
     * @throws ParsingException if the script is not valid
     */
    @Override
    public Predicate<A> parseCondition(String script) {
        JexlScript jexlScript = createScript(script);
        return facts -> (boolean) evaluate(jexlScript, facts);
    }

    /**
     * Creates an action from the given JEXL script.
     *
     * @param script the script
     * @return the action
     * @throws ParsingException if the script is not valid
     */
    @Override
    public Function<A, B> parseAction(String script) {
        JexlScript jexlScript = createScript(script);
        return facts -> resultType.cast(evaluate(jexlScript, facts));
    }

    private JexlScript createScript(String script) {
        try {
            return jexlEngine.createScript(script);
        } catch (JexlException e) {
            throw new ParsingException(String.format("parsing '%s' at line %d, column %d",
                    script, e.getInfo().getLine(), e.getInfo().getColumn()), e);
        }
    }

    private Object evaluate(JexlScript jexlScript, A facts) {
        try {
            return jexlScript.execute(facts);
        } catch (JexlException e) {
            throw new RulesEngineException(String.format("evaluating '%s' at line %d, column %d",
                    jexlScript.getSourceText(), e.getInfo().getLine(), e.getInfo().getColumn()), e);
        }
    }

}
