/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.referencing;


/**
 * Semaphores that need to be shared accross different referencing packages. Each thread has
 * its own set of semaphores. The {@link #clear} method <strong>must</strong> be invoked after
 * the {@link #queryAndSet} method in a {@code try ... finally} block.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public final class Semaphores {
    /**
     * A lock for avoiding never-ending recursivity in the {@code equals}
     * method of {@link org.geotoolkit.referencing.crs.AbstractDerivedCRS} and
     * {@link org.geotoolkit.referencing.operation.AbstractCoordinateOperation}. It is set
     * to {@code true} when a comparison is in progress. This lock is necessary because
     * {@code AbstractDerivedCRS} objects contain a {@code conversionFromBase} field,
     * which contains a {@code DefaultConversion.targetCRS} field referencing back the
     * {@code AbstractDerivedCRS} object.
     */
    public static final int COMPARING = 1;

    /**
     * A flag to indicate that {@link org.geotoolkit.referencing.operation.DefaultSingleOperation}
     * is querying {@link org.geotoolkit.referencing.operation.transform.ConcatenatedTransform} in
     * the intend to format WKT (normally a {@code "PROJCS"} element).
     */
    public static final int PROJCS = 2;

    /**
     * The flags per running thread.
     */
    private static final ThreadLocal<Semaphores> FLAGS = new ThreadLocal<>();

    /**
     * The bit flags.
     */
    private int flags;

    /**
     * Do not allow instantiation of this class by anyone else.
     */
    private Semaphores() {
    }

    /**
     * Returns {@code true} if the given flag is set.
     *
     * @param  flag One of {@link #COMPARING} or {@link #PROJCS} constants.
     * @return {@code true} if the given flag is set.
     */
    public static boolean query(final int flag) {
        final Semaphores s = FLAGS.get();
        return (s != null) && (s.flags & flag) != 0;
    }

    /**
     * Sets the given flag.
     *
     * @param  flag One of {@link #COMPARING} or {@link #PROJCS} constants.
     * @return {@code true} if the given flag was already set.
     */
    public static boolean queryAndSet(final int flag) {
        Semaphores s = FLAGS.get();
        if (s == null) {
            s = new Semaphores(); // NOSONAR
            FLAGS.set(s);
        }
        final boolean isSet = ((s.flags & flag) != 0);
        s.flags |= flag;
        return isSet;
    }

    /**
     * Clears the given flag.
     *
     * @param flag One of {@link #COMPARING} or {@link #PROJCS} constants.
     */
    public static void clear(final int flag) {
        final Semaphores s = FLAGS.get();
        if (s != null) {
            s.flags &= ~flag;
        }
    }
}
