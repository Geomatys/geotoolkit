/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.collection;

import java.lang.ref.Reference;
import org.geotoolkit.internal.ReferenceQueueConsumer;


/**
 * A thread invoking {@link Disposeable#dispose} on each enqueded {@linkplain Reference reference}.
 * This thread is used by {@link WeakHashSet} and {@link WeakValueHashMap}, which remove their
 * entry from the collection when the entry is garbage-collected.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.0
 *
 * @since 2.0
 * @module
 */
final class WeakCollectionCleaner extends ReferenceQueueConsumer<Object> {
    /**
     * Interface to be implemented by references that can be disposed. Every references to
     * be enqueued in {@link WeakCollectionCleaner#queue} <strong>must</strong> implement
     * this interface.
     */
    static interface Disposeable {
        /**
         * Invoked from the {@link WeakCollectionCleaner} thread after the reference has
         * garbage collected.
         */
        void dispose();
    }

    /**
     * The default thread.
     */
    static final WeakCollectionCleaner DEFAULT = new WeakCollectionCleaner();

    /**
     * Constructs and starts a new thread as a daemon. This thread will be sleeping
     * most of the time.  It will run only some few nanoseconds each time a new
     * {@link Reference} is enqueded.
     */
    private WeakCollectionCleaner() {
        super("WeakCollectionCleaner");
        start();
    }

    /**
     * Invoked when a reference has been cleared.
     */
    @Override
    protected void process(final Reference<?> reference) {
        /*
         * If the reference does not implement the Disposeable interface, we want
         * the ClassCastException to be logged in the "catch" block of the super
         * class since it would be a programming error that we want to know about.
         */
        ((Disposeable) reference).dispose();
    }
}
