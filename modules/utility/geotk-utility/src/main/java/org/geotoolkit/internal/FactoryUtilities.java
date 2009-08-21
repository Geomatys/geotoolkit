/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.internal;

import java.util.Map;
import java.awt.RenderingHints;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.lang.Static;
import org.geotoolkit.util.Utilities;


/**
 * A set of utilities for {@link org.geotoolkit.factory.FactoryRegistry}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.00
 * @module
 */
@Static
public final class FactoryUtilities {
    /**
     * The group of threads that dispose factories. This is used only when
     * {@link Factory#dispose} is invoked in a background thread.
     */
    public static final ThreadGroup DISPOSER_THREADS = new ThreadGroup(Threads.PARENT, "FactoryDisposers");

    /**
     * The minimal delay (in miliseconds) between uncessfull calls to {@link Factory#isAvailable}.
     * If a first call failed, then all subsequent calls before the end of this delay will returns
     * {@code false} immediatly without actually attempting a connection to the database. This is
     * used in order to avoid too frequent connection attempts, assuming the reason for the initial
     * failure changed have probably not changed before this delay.
     */
    public static final int ATTEMPTS_DELAY = 200;

    /**
     * Do not allow instantiation of this class.
     */
    private FactoryUtilities() {
    }

    /**
     * Adds to the given hints only the valid entries from the given map. This method is similar
     * to {@code target.putAll(source)} except that invalid keys are filtered. Such filtering is
     * not always wanted since it may hide bugs.
     *
     * @param  source The map which contains the key to copy.
     * @param  target The hints where to copy the hints.
     * @param  filterOnlyNulls If {@code true}, only null values may be filtered.
     * @return {@code true} if at least one value changed as a result of this call.
     *
     * @since 3.02
     */
    public static boolean addValidEntries(final Map<RenderingHints.Key, Object> source,
            final RenderingHints target, final boolean filterOnlyNulls)
    {
        boolean changed = false;
        for (final Map.Entry<RenderingHints.Key, Object> entry : source.entrySet()) {
            final RenderingHints.Key key = entry.getKey();
            final Object value = entry.getValue();
            if ((filterOnlyNulls && value != null) || key.isCompatibleValue(value)) {
                final Object old = target.put(key, value);
                if (!changed && !Utilities.equals(old, value)) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    /**
     * Adds the specified hints to a {@link Factory#hints}. This method can be used as a
     * replacement for {@code hints.putAll(map)} when the map is an instance of {@link Hints}.
     *
     * @param  source The hints to add.
     * @param  target Where to add the hints.
     * @return {@code true} if at least one value changed as a result of this call.
     */
    public static boolean addImplementationHints(final RenderingHints source,
            final Map<RenderingHints.Key, Object> target)
    {
        /*
         * Do NOT change the parameter signature to Map<?,?>. We want to keep type safety.
         * Use hints.putAll(...) if you have a Map<RenderingHints.Key,?>,  or this method
         * if you have a RenderingHints map. Furthermore this method implementation needs
         * the garantee that the map do not contains null value (otherwise the 'changed'
         * computation could be inacurate) - this condition is enforced by RenderingHints
         * but not by Map.
         *
         * The implementation below strips non-RenderingHints.Key as a paranoiac check,
         * which should not be necessary since RenderingHints implementation prevented
         * that. If the parameter was changed to Map<?,?>, the stripping would be more
         * likely and could surprise the user since it is performed without warnings.
         */
        boolean changed = false;
        if (source != null) {
            for (final Map.Entry<?,?> entry : source.entrySet()) {
                final Object key = entry.getKey();
                if (key instanceof RenderingHints.Key) {
                    final Object value = entry.getValue();
                    final Object old = target.put((RenderingHints.Key) key, value);
                    if (!changed && !Utilities.equals(value, old)) {
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }
}
