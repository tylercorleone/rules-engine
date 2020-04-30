package it.sky.rulesengine.examples.rbac.authorization;

import it.sky.rulesengine.rbac.api.AccessManager;
import it.sky.rulesengine.rbac.api.Role;
import it.sky.rulesengine.rbac.impl.util.CrudPermission;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.lang.reflect.Proxy;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizedProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T> T create(@NonNull final Class<T> theInterface,
                               @NonNull final T delegate,
                               @NonNull final Supplier<UserFacts> userFactsSupplier,
                               @NonNull final AccessManager<UserFacts, CrudPermission, Role<CrudPermission>> accessManager) {
        return (T) Proxy.newProxyInstance(theInterface.getClassLoader(), new Class<?>[]{theInterface},
                new AuthorizationFilter(accessManager, delegate, userFactsSupplier));
    }

}
