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
package org.geotoolkit.referencing.factory;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Delayed;
import java.util.concurrent.DelayQueue;
import java.util.Iterator;
import java.util.logging.Level;

import org.geotoolkit.lang.Debug;
import org.geotoolkit.math.XMath;
import org.geotoolkit.internal.Threads;
import org.geotoolkit.internal.DaemonThread;
import org.geotoolkit.util.logging.Logging;


/**
 * The thread which will dispose the backing stores in {@link ThreadedAuthorityFactory}.
 * Only one instance of this thread exists in a running JVM. This instance is shared by
 * all instances of {@code ThreadedAuthorityFactory}. Weak references to the factories
 * are pushed in a queue and taken only after some delay. When a factory reference is taken,
 * if the reference still valid then its {@link ThreadedAuthorityFactory#disposeExpired}
 * method is invoked and a new reference is pushed for a new check after some delay.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.09
 *
 * @since 3.00
 * @module
 */
final class StoreDisposer extends DaemonThread {
    /**
     * The unique instance of this disposer thread. This is a daemon
     * thread started on this class initialization and running until
     * the application terminate.
     */
    static final StoreDisposer INSTANCE = new StoreDisposer();
    static {
        // Call to Thread.start() must be outside the constructor
        // (Reference: Goetz et al.: "Java Concurrency in Practice").
        INSTANCE.start();
    }

    /**
     * The queue of {@link ThreadedAuthorityFactory} on which to invoke
     * {@link ThreadedAuthorityFactory#disposeExpired} after some delay.
     */
    private final DelayQueue<Ref> queue = new DelayQueue<Ref>();

    /**
     * Creates a new, unstarted, thread. This constructor
     * is invoked by the unique {@link #INSTANCE} only.
     */
    private StoreDisposer() {
        super(Threads.RESOURCE_DISPOSERS, "ReferencingStoreDisposer");
        setPriority(Thread.NORM_PRIORITY + 1);
    }

    /**
     * The loop to be run in the disposer thread - do <strong>not</strong> call this method
     * explicitly. This loop invokes {@link ThreadedAuthorityFactory#disposeExpired()} and
     * waits until the time to dispose the next factory come.
     */
    @Override
    public void run() {
        // The reference queue should never be null, but experience with
        // WeakCollectionCleaner suggests that we are better to be safe.
        Level level = Level.SEVERE;
        DelayQueue<Ref> queue;
        while ((queue = this.queue) != null) {
            if (isKillRequested()) {
                level = Level.INFO;
                break;
            }
            final Ref ref;
            try {
                ref = queue.take();
            } catch (InterruptedException e) {
                // Someone doesn't want to let us sleep.
                // Go wait again.
                continue;
            }
            final ThreadedAuthorityFactory factory = ref.get();
            if (factory != null) {
                final long schedule = factory.disposeExpired();
                if (schedule != Long.MIN_VALUE) {
                    queue.add(new Ref(factory, schedule));
                }
            }
        }
        Logging.getLogger(StoreDisposer.class).log(level, "Daemon stopped.");
    }

    /**
     * Schedules a factory on which to invoke {@link ThreadedAuthorityFactory#disposeExpired()}
     * at the given time. This method is invoked when {@code ThreadedAuthorityFactory} created
     * its first backing store.
     *
     * @param factory  The factory on which to invoke {@code disposeExpired()} at the scheduled time.
     * @param schedule The scheduled time in milliseconds since January first, 1970.
     */
    final void schedule(final ThreadedAuthorityFactory factory, final long schedule) {
        assert Thread.holdsLock(factory);
        queue.add(new Ref(factory, schedule));
    }

    /**
     * Return in how much time (in milliseconds) the next factory will be disposed.
     * If there is no factory waiting for being disposed, returns {@code 0}. This is
     * used for debugging purpose only.
     */
    @Debug
    final long getDelay() {
        final Delayed next = queue.peek();
        if (next == null) {
            return 0;
        }
        return next.getDelay(TimeUnit.MILLISECONDS);
    }

    /**
     * Removes the given factory from the waiting queue. This
     * method is invoked when a factory has been disposed.
     *
     * @param factory The factory to remove from the queue.
     */
    final void cancel(final ThreadedAuthorityFactory factory) {
        assert Thread.holdsLock(factory);
        for (final Iterator<Ref> it = queue.iterator(); it.hasNext();) {
            final ThreadedAuthorityFactory ref = it.next().get();
            if (ref == null || ref == factory) {
                it.remove();
            }
        }
    }

    /**
     * Returns a string representation for debugging purpose only.
     * This string representation vary with time.
     */
    @Override
    public String toString() {
        return "Next in " + getDelay() + " ms.";
    }

    /**
     * The reference to a factory on which {@link ThreadedAuthorityFactory#disposeExpired}
     * will need to be invoked for disposing expired backing stores.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    private static final class Ref extends WeakReference<ThreadedAuthorityFactory> implements Delayed {
        /**
         * The scheduled time (in milliseconds) at which to invoke
         * {@link ThreadedAuthorityFactory#disposeExpired}.
         */
        private final long schedule;

        /**
         * Creates a new reference for the given factory, to be cleaned after the given delay.
         *
         * @param factory The factory to be cleaned.
         * @param delay The scheduled time (in milliseconds) at which to invoke
         *              {@link ThreadedAuthorityFactory#disposeExpired}.
         */
        Ref(final ThreadedAuthorityFactory factory, final long schedule) {
            super(factory);
            this.schedule = schedule;
        }

        /**
         * Returns the remaining delay.
         */
        @Override
        public long getDelay(final TimeUnit unit) {
            return unit.convert(schedule - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        /**
         * Returns -1 if this object expires before the other one, +1 if it expires after,
         * or 0 if they expire in same time.
         */
        @Override
        public int compareTo(final Delayed other) {
            return XMath.sgn(schedule - ((Ref) other).schedule);
        }
    }
}
