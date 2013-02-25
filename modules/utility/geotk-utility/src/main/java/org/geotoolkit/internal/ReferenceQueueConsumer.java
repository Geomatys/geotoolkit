/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal;

import java.util.logging.Level;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

import org.apache.sis.util.Disposable;
import org.geotoolkit.util.logging.Logging;
import org.apache.sis.util.Classes;


/**
 * Base class for thread that will process {@link Reference} enqueued in a {@link ReferenceQueue}.
 * This thread invokes {@link Disposeable#dispose} on each enqueded {@linkplain Reference reference}.
 * Every {@link Reference} implementations to be enqueued in {@link ReferenceQueueConsumer#queue}
 * <strong>must</strong> implement the {@link Disposable} interface
 *
 * @param <T> The type of objects being referenced.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.13
 *
 * @since 3.00
 * @module
 */
public class ReferenceQueueConsumer<T> extends DaemonThread {
    /**
     * The default thread.
     */
    public static final ReferenceQueueConsumer<Object> DEFAULT;
    static {
        // Call to Thread.start() must be outside the constructor
        // (Reference: Goetz et al.: "Java Concurrency in Practice").
        DEFAULT = new ReferenceQueueConsumer<>("ReferenceQueueConsumer");
        DEFAULT.start();
    }

    /**
     * List of references collected by the garbage collector.
     */
    public final ReferenceQueue<T> queue = new ReferenceQueue<>();

    /**
     * Constructs a new thread as a daemon. This thread will be sleeping most of the time.
     * It will run only some few nanoseconds each time a new {@link Reference} is enqueded.
     *
     * @param name The thread name. This name appears in the debugger.
     */
    protected ReferenceQueueConsumer(final String name) {
        super(Threads.RESOURCE_DISPOSERS, name);
        setPriority(MAX_PRIORITY - 2);
    }

    /**
     * Invoked everytime a reference has been taken from the {@linkplain #queue}.
     * The default implementation invokes {@link Disposable#dispose()} method.
     * Subclasses can override this method if they want a different behavior.
     *
     * @param reference The reference (never {@code null}).
     */
    protected void process(final Reference<? extends T> reference) {
        /*
         * If the reference does not implement the Disposeable interface, we want
         * the ClassCastException to be logged in the "catch" block of the super
         * class since it would be a programming error that we want to know about.
         */
        ((Disposable) reference).dispose();
    }

    /**
     * Loop to be run during the virtual machine lifetime.
     */
    @Override
    public final void run() {
        // The reference queue should never be null.  However some strange cases (maybe caused
        // by an anormal JVM state) have been reported on the mailing list. In such case, stop
        // the daemon instead of writing 50 Mb of log messages.
        Level level = Level.SEVERE;
        ReferenceQueue<T> queue;
        while ((queue = this.queue) != null) {
            if (isKillRequested()) {
                level = Level.INFO;
                break;
            }
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
            } catch (Throwable exception) {
                Logging.unexpectedException(getClass(), "run", exception);
            }
        }
        Logging.getLogger(getClass()).log(level, "{0} daemon stopped.", Classes.getShortClassName(this));
    }
}
