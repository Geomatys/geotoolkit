/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import org.geotoolkit.util.logging.Logging;


/**
 * Base class for thread that will process {@link Reference} enqueued in a {@link ReferenceQueue}.
 *
 * @param <T> The type of objects being referenced.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public abstract class ReferenceQueueConsumer<T> extends Thread {
    /**
     * The group of {@code ReferenceQueueConsumer} threads running.
     */
    private static final ThreadGroup GROUP = new ThreadGroup("ReferenceQueueHandler");

    /**
     * List of references collected by the garbage collector.
     */
    public final ReferenceQueue<T> queue = new ReferenceQueue<T>();

    /**
     * Constructs a new thread as a daemon. This thread will be sleeping most of the time.
     * It will run only some few nanoseconds each time a new {@link Reference} is enqueded.
     *
     * @param name The thread name.
     */
    protected ReferenceQueueConsumer(final String name) {
        super(GROUP, name);
        setPriority(MAX_PRIORITY - 2);
        setDaemon(true);
    }

    /**
     * Invoked everytime a reference has been taken from the {@linkplain #queue}.
     *
     * @param reference The reference (never {@code null}).
     */
    protected abstract void process(Reference<? extends T> reference);

    /**
     * Loop to be run during the virtual machine lifetime.
     */
    @Override
    public final void run() {
        // The reference queue should never be null.  However some strange cases (maybe caused
        // by an anormal JVM state) have been reported on the mailing list. In such case, stop
        // the daemon instead of writting 50 Mb of log messages.
        ReferenceQueue<T> queue;
        while ((queue = this.queue) != null) {
            final Reference<? extends T> ref;
            try {
                // Block until a reference is enqueded.
                ref = queue.remove();
                if (ref == null) {
                    /*
                     * Should never happen according Sun's Javadoc ("Removes the next reference
                     * object in this queue, blocking until one becomes available."). However a
                     * null reference seems to be returned during JVM shutdown on Linux. Wait a
                     * few seconds in order to give the JVM a chance to kill this daemon thread
                     * before the logging at the sever level, and stop the loop.  We do not try
                     * to resume the loop since something is apparently going wrong and we want
                     * the user to be notified. See GEOT-1138.
                     */
                    sleep(15 * 1000L);
                    break;
                }
            } catch (InterruptedException exception) {
                // Somebody doesn't want to lets us sleep... Go back to work.
                continue;
            }
            try {
                process(ref);
            } catch (Exception exception) {
                Logging.unexpectedException(getClass(), "run", exception);
            } catch (AssertionError exception) {
                Logging.unexpectedException(getClass(), "run", exception);
                // Do not kill the thread on assertion failure, in order to
                // keep the same behaviour as if assertions were turned off.
            }
        }
        Logging.getLogger(getClass()).severe("Daemon stopped."); // Should never happen.
    }
}
