/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.geotoolkit.lang.Static;


/**
 * Utilities methods for threads. This class declares in a single place every {@link ThreadGroup}
 * used in Geotk. Their purpose is only to put a little bit of order in debugger informations, by
 * grouping the threads created by Geotk together under the same parent tree node.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 3.03
 * @module
 */
@Static
@SuppressWarnings("serial")
public final class Threads extends AtomicInteger implements ThreadFactory {
    /**
     * The parent of every threads declared in this class.
     */
    public static final ThreadGroup GEOTOOLKIT = new ThreadGroup("Geotoolkit.org");

    /**
     * The group of shutdown hooks. This group has the default priority.
     */
    public static final ThreadGroup SHUTDOWN_HOOKS;

    /**
     * The group of {@code ReferenceQueueConsumer} threads running.
     * Threads in this group have a high priority and should be completed quickly.
     */
    public static final ThreadGroup REFERENCE_CLEANERS;

    /**
     * The group of threads disposing resources, typically after a timeout.
     */
    public static final ThreadGroup RESOURCE_DISPOSERS;

    /**
     * The group of low-priority dameons. Tasks in this thread are executed only
     * when the CPU have plenty of time available.
     *
     * @since 3.05
     */
    public static final ThreadGroup DAEMONS;

    static {
        final ThreadGroup parent = GEOTOOLKIT;
        SHUTDOWN_HOOKS     = new ThreadGroup(parent, "ShutdownHooks");
        REFERENCE_CLEANERS = new ThreadGroup(parent, "ReferenceQueueConsumers");
        RESOURCE_DISPOSERS = new ThreadGroup(parent, "ResourceDisposers");
        DAEMONS            = new ThreadGroup(parent, "Daemons");
        REFERENCE_CLEANERS.setMaxPriority(Thread.MAX_PRIORITY  - 2);
        RESOURCE_DISPOSERS.setMaxPriority(Thread.NORM_PRIORITY + 1);
        DAEMONS           .setMaxPriority(Thread.MIN_PRIORITY  + 1);
    }

    /**
     * The executor to be returned by {@link #executor()}.
     * Will be created only when first needed.
     */
    private static volatile ExecutorService normalExecutor, daemonExecutor;

    /**
     * {@code true} if the threads to be created should be daemon threads.
     */
    private final boolean daemon;

    /**
     * For internal usage only.
     */
    private Threads(final boolean daemon) {
        this.daemon = daemon;
    }

    /**
     * A pool of threads to be shared by different Geotk utility classes. The tasks submitted
     * to this executor should be only house-keeping works. For tasks doing "real" computation,
     * use your own executor.
     * <p>
     * The threads in this executor have a priority slightly higher than the normal priority.
     * This is on the assumption that the tasks will spend most of their time waiting for some
     * condition, and complete quickly when the condition become true.
     * <p>
     * Callers should not keep a reference to the returned executor for a long time.
     * It is preferrable to use it as soon as possible and discart.
     *
     * @param  daemon {@code true} if the threads to be created should be daemon threads.
     * @return The executor.
     */
    public static Executor executor(final boolean daemon) {
        ExecutorService exec = daemon ? daemonExecutor : normalExecutor;
        if (exec == null) {
            // Double-check: was a deprecated practice before Java 5, is okay
            // since Java 5 provided that the field is declared volatile.
            synchronized (Threads.class) {
                exec = daemon ? daemonExecutor : normalExecutor;
                if (exec == null) {
                    exec = Executors.newCachedThreadPool(new Threads(daemon));
                    if (daemon) {
                        daemonExecutor = exec;
                    } else {
                        normalExecutor = exec;
                    }
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
        final String name = (daemon ? "PooledDaemon-" : "PooledThread-") + incrementAndGet();
        final Thread thread = new Thread(RESOURCE_DISPOSERS, task, name);
        thread.setPriority(Thread.NORM_PRIORITY + 1);
        thread.setDaemon(daemon);
        return thread;
    }

    /**
     * Shutdowns the executors and wait for the non-daemon threads to complete.
     * This method should be invoked only when we thing that no more tasks are
     * going to be submitted to the executor (it is actually hard to ensure that).
     *
     * @return {@code true} if the pending tasks have been completed, or {@code false}
     *         if this method returned before every tasks were completed.
     *
     * @since 3.06
     */
    public static synchronized boolean shutdown() {
        ExecutorService exec = daemonExecutor;
        if (exec != null) {
            exec.shutdown();
        }
        exec = normalExecutor;
        if (exec != null) try {
            exec.shutdown();
            return exec.awaitTermination(4, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Too late for logging since we are in process of shuting down.
        }
        return false;
    }
}
