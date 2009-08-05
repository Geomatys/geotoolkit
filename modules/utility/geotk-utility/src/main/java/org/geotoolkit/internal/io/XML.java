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
package org.geotoolkit.internal.io;

import org.geotoolkit.lang.Static;


/**
 * Utilities method for XML marshalling or unmarshalling.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@Static
public final class XML {
    /**
     * Whatever the current thread is in the process of marshalling an object.
     */
    private static final ThreadLocal<Boolean> MARSHALLING = new ThreadLocal<Boolean>();

    /**
     * Do not allow instantiation of this class.
     */
    private XML() {
    }

    /**
     * Sets whatever the current thread is in the process of marshalling an object.
     * <strong>Must</strong> be used in a {@code try ... finally} block as below:
     *
     * {@preformat java
     *     XML.marshalling(true);
     *     try {
     *         ...
     *     } finally {
     *         XML.marshalling(false);
     *     }
     * }
     *
     * @param state {@code true} before marshalling, or {@code false} after marshalling.
     */
    public static void marshalling(final boolean state) {
        MARSHALLING.set(state);
    }

    /**
     * Returns the state set by the last call to {@link #marshalling(boolean)} in this thread.
     *
     * @return {@code true} if a marshalling in under progress in this thread.
     */
    public static boolean marshalling() {
        final Boolean state = MARSHALLING.get();
        return (state != null) ? state.booleanValue() : false;
    }
}
