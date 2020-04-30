package it.sky.rulesengine.examples.rbac.authorization;

import com.sun.xml.internal.ws.util.StringUtils;
import it.sky.rulesengine.rbac.api.AccessManager;
import it.sky.rulesengine.rbac.api.Role;
import it.sky.rulesengine.rbac.impl.util.CrudPermission;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class AuthorizationFilter implements InvocationHandler {

    private static final Pattern METHOD_NAME_PATTERN = Pattern.compile("^(create|read|update|delete)([A-Z].*)");

    @NonNull
    private final AccessManager<UserFacts, CrudPermission, Role<CrudPermission>> accessManager;
    @NonNull
    private final Object delegate;
    @NonNull
    private final Supplier<UserFacts> userFactsSupplier;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        extractRequiredPermission(method.getName())
                .ifPresent(crudPermission -> authorize(extractResourceName(method.getName()), crudPermission));

        return method.invoke(delegate, args);
    }

    private void authorize(String resourceName, CrudPermission requiredPermission) {
        Set<CrudPermission> availablePermissions = accessManager.capabilities(userFactsSupplier.get()).get(resourceName);
        if (!availablePermissions.contains(requiredPermission)) {
            throw new AuthorizationException();
        }
    }

    private static Optional<CrudPermission> extractRequiredPermission(String methodName) {
        Matcher matcher = METHOD_NAME_PATTERN.matcher(methodName);
        if (!matcher.matches()) {
            return Optional.empty();
        }
        return Optional.of(CrudPermission.valueOf(matcher.group(1).toUpperCase()));
    }

    private static String extractResourceName(String methodName) {
        Matcher matcher = METHOD_NAME_PATTERN.matcher(methodName);
        if (!matcher.matches()) {
            throw new IllegalStateException();
        }
        return StringUtils.decapitalize(matcher.group(2));
    }

    public static class AuthorizationException extends RuntimeException {
    }

}
