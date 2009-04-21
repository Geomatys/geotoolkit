/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
@Static
public final class FactoryUtilities {
    /**
     * If {@code true}, then {@code FactoryRegistry} must check the factory type
     * using {@link Class#equals} rather than {@link Class#isAssignableFrom}.
     * <p>
     * This is not commited API because there is probably a better way to do that, for example
     * specifying a {@link javax.imageio.spi.ServiceRegistry.Filter}. However this condition
     * is handled especially because {@code ServiceRegistry} registers at most one instance of
     * a given class for a given category, so the implementation class can be used as a key
     * during factory search.
     */
    public static final Hints.Key EXACT_CLASS = new Hints.Key(Boolean.class);

    /**
     * The group of threads that dispose factories. This is used only when
     * {@link Factory#dispose} is invoked in a background thread.
     */
    public static final ThreadGroup DISPOSAL_GROUP = new ThreadGroup("Factory disposal");

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
