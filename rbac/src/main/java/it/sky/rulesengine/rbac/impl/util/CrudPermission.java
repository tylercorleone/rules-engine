package it.sky.rulesengine.rbac.impl.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An enumeration of the well-known CRUD permissions.
 */
public enum CrudPermission {

    CREATE, READ, UPDATE, DELETE;

    /**
     * All the CRUD permissions.
     */
    public static final Set<CrudPermission> ALL = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(CREATE, READ, UPDATE, DELETE)));

    /**
     * No CRUD permission.
     */
    public static final Set<CrudPermission> NONE = Collections.emptySet();

}
