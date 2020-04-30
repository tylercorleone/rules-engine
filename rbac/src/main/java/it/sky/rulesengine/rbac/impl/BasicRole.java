package it.sky.rulesengine.rbac.impl;

import it.sky.rulesengine.rbac.api.Role;
import it.sky.rulesengine.rbac.impl.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A basic Plain Old Java Object implementation of a {@link Role}.
 *
 * @param <P> the permissions type
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicRole<P> implements Role<P> {

    @NonNull
    private Map<String, Set<P>> capabilities;

//    @Override
//    public Set<P> getPermissions(String resource) {
//        return Optional.ofNullable(capabilities.get(resource)).orElseThrow(() ->
//                new ResourceNotFoundException(String.format("resource not found: '%s'", resource)));
//    }

}
