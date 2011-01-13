/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.internal.jaxb;

import org.geotoolkit.xml.ObjectConverters;


/**
 * Thread-local status of a marshalling or unmarshalling process.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 * @module
 */
public final class MarshalContext {
    /**
     * The thread-local context.
     */
    private static final ThreadLocal<MarshalContext> CURRENT = new ThreadLocal<MarshalContext>();

    /**
     * The object converters currently in use.
     */
    private ObjectConverters converters;

    /**
     * {@code true} if a marshalling process is under progress.
     * The value is unchanged for unmarshalling processes.
     */
    private boolean isMarshalling;

    /**
     * Do not allow instantiation outside this class.
     */
    private MarshalContext() {
    }

    /**
     * Returns the object converters in use for the current marshalling or unmarshalling process.
     *
     * @return The current object converters.
     */
    public static ObjectConverters converters() {
        final MarshalContext current = CURRENT.get();
        return (current != null) ? current.converters : ObjectConverters.DEFAULT;
    }

    /**
     * Returns whatever a marshalling process is under progress.
     *
     * @return {@code true} if a marshalling process is in progress.
     */
    public static boolean isMarshalling() {
        final MarshalContext current = CURRENT.get();
        return (current != null) ? current.isMarshalling : false;
    }

    /**
     * Invoked when a marshalling or unmarshalling process is about to begin.
     * Must be followed by a call to {@link #finish()} in a {@code finally} block.
     *
     * {@preformat java
     *     MarshalContext ctx = begin(converters);
     *     try {
     *         ...
     *     } finally {
     *         ctx.finish();
     *     }
     * }
     *
     * @param  converters    The converters in use.
     * @return The context on which to invoke {@link #finish()} when the (un)marshalling is finished.
     */
    public static MarshalContext begin(final ObjectConverters converters) {
        MarshalContext current = CURRENT.get();
        if (current == null) {
            current = new MarshalContext();
            CURRENT.set(current);
        }
        current.converters = converters;
        return current;
    }

    /**
     * Declares that the process to begin is a marshalling, and returns
     * the previous value of the {@link #isMarshalling()} flag.
     *
     * @return The old value.
     */
    public boolean setMarshalling() {
        final boolean old = isMarshalling;
        isMarshalling = true;
        return old;
    }

    /**
     * Invoked in a {@code finally} block when a marshalling process is finished.
     *
     * @param marshalling The value to restore for the {@link #isMarshalling()} flag.
     */
    public void finish(final boolean marshalling) {
        converters = null;
        isMarshalling = marshalling;
    }

    /**
     * Invoked in a {@code finally} block when a unmarshalling process is finished.
     */
    public void finish() {
        converters = null;
        // Intentionally leave isMarshalling unmodified.
    }
}
