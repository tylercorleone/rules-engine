package it.sky.rulesengine.examples.rbac.authorization;

import lombok.NonNull;
import org.apache.commons.jexl3.MapContext;

import java.time.Instant;

public class UserFacts extends MapContext {

    public UserFacts(final boolean isAdmin, @NonNull final Instant lastLoginInstant) {
        set("isAdmin", isAdmin);
        set("lastLoginInstant", lastLoginInstant);
    }

}
