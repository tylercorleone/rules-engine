package it.sky.rulesengine.rbac.impl.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An enumeration of the read and write permissions.
 */
public enum ReadWritePermission {

    READ, WRITE;

    /**
     * The read and write permissions.
     */
    public static final Set<ReadWritePermission> ALL = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(READ, WRITE)));

    /**
     * No read/write permission.
     */
    public static final Set<ReadWritePermission> NONE = Collections.emptySet();

}
