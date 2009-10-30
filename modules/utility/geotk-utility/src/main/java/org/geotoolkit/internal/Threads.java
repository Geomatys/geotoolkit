/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.geotoolkit.lang.Static;


/**
 * Utilities methods for threads. This class declares in a single place every {@link ThreadGroup}
 * used in Geotk. Their purpose is only to put a little bit of order in debugger informations, by
 * grouping the threads created by Geotk together under the same parent tree node.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.03
 * @module
 */
@Static
public final class Threads extends ThreadGroup implements ThreadFactory {
    /**
     * The group of shutdown hooks. This group has the default priority.
     */
    public static final ThreadGroup SHUTDOWN_HOOKS;

    /**
     * The group of {@code ReferenceQueueConsumer} threads running.
     * Threads in this group have a high priority and should be completed quickly.
     */
    static final ThreadGroup REFERENCE_CLEANERS;

    /**
     * The group of low-priority dameons. Tasks in this thread are executed only
     * when the CPU have plenty of time available.
     *
     * @since 3.05
     */
    public static final ThreadGroup DAEMONS;

    static {
        final ThreadGroup parent = new ThreadGroup("Geotoolkit.org");
        SHUTDOWN_HOOKS     = new ThreadGroup(parent, "ShutdownHooks");
        REFERENCE_CLEANERS = new ThreadGroup(parent, "ReferenceQueueConsumers");
        DAEMONS            = new ThreadGroup(parent, "Daemons");
        REFERENCE_CLEANERS.setMaxPriority(Thread.MAX_PRIORITY - 2);
        DAEMONS           .setMaxPriority(Thread.MIN_PRIORITY);
    }

    /**
     * The executor to be returned by {@link #executor()}.
     * Will be created only when first needed.
     */
    private static volatile Executor executor;

    /**
     * For internal usage only.
     */
    private Threads(final String name) {
        super(DAEMONS.getParent(), name);
    }

    /**
     * A pool of threads to be shared by different Geotk utility classes. This pool is useful
     * only for thread living for a limited amount of time. If a thread is to live until the
     * JVM shutdown, don't use this executor - create the thread directly instead.
     * <p>
     * Every threads created by this executor are daemon threads. Consequenty the tasks submitted
     * to this executor should be only house-keeping work. If the tasks need to be completed before
     * JVM shutdown, then define your own executor.
     * <p>
     * The threads in this executor have a priority slightly higher than the normal priority.
     * This is on the assumption that the tasks will spend most of their time waiting for some
     * condition, and complete quickly when the condition become true.
     *
     * @return The executor.
     *
     * @todo We need to shutdown the executor and reset the field to null. When?
     *       After a timeout?
     */
    public static Executor executor() {
        Executor exec = executor;
        if (exec == null) {
            // Double-check: was a deprecated practice before Java 5, is okay
            // since Java 5 provided that the field is declared volatile.
            synchronized (Threads.class) {
                exec = executor;
                if (exec == null) {
                    executor = exec = Executors.newCachedThreadPool(new Threads("ThreadPool"));
                }
            }
        }
        return exec;
    }

    /**
     * For internal usage by {@link #executor} only.
     *
     * @param  task The task to execute.
     * @return A new thread running the given task.
     */
    @Override
    public Thread newThread(final Runnable task) {
        final Thread thread = new Thread(this, task);
        thread.setPriority(Thread.NORM_PRIORITY + 1);
        thread.setDaemon(true);
        return thread;
    }
}
