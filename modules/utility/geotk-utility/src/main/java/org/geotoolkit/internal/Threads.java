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
package org.geotoolkit.internal;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.logging.Logging;


/**
 * Utilities methods for threads. This class declares in a single place every {@link ThreadGroup}
 * used in Geotk. Their purpose is only to put a little bit of order in debugger informations, by
 * grouping the threads created by Geotk together under the same parent tree node.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
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
     * The group of threads disposing resources, typically after a timeout.
     */
    public static final ThreadGroup RESOURCE_DISPOSERS;

    /**
     * The group of workers to run in a background thread.
     *
     * @since 3.05
     */
    public static final ThreadGroup WORKERS;

    static {
        RESOURCE_DISPOSERS = new ThreadGroup(GEOTOOLKIT, "ResourceDisposers");
        WORKERS            = new ThreadGroup(GEOTOOLKIT, "Workers");
        RESOURCE_DISPOSERS.setMaxPriority(Thread.MAX_PRIORITY  - 2);
        WORKERS           .setMaxPriority(Thread.NORM_PRIORITY - 1);
    }

    /**
     * The executors to be returned by {@link #executor(boolean)}.
     * Will be created only when first needed, and then considered as final.
     */
    private static ExecutorService[] EXECUTORS;

    /**
     * {@code true} if the threads to be created are part of the {@link #RESOURCE_DISPOSERS}
     * group, or {@code false} if they are {@link #WORKERS} threads.
     */
    private final boolean disposer;

    /**
     * {@code true} if the threads to be created should be daemon threads.
     */
    private final boolean daemon;

    /**
     * The prefix to put at the beginning of thread names.
     */
    private final String prefix;

    /**
     * For internal usage only.
     */
    private Threads(final boolean disposer, final boolean daemon, final String prefix) {
        this.disposer = disposer;
        this.daemon   = daemon;
        this.prefix   = prefix;
    }

    /**
     * Creates a factory for worker threads created by the Geotk library.
     * This factory should be used by every method that use {@link Executors}
     * directly instead than {@link #executor(boolean)}.
     *
     * @param  prefix The prefix to put in front of thread names.
     * @return The thread factory.
     *
     * @since 3.17
     */
    public static ThreadFactory createThreadFactory(final String prefix) {
        return new Threads(false, false, prefix);
    }

    /**
     * A pool of threads to be shared by different Geotk utility classes. The threads
     * created by the executor can be part of two groups:
     *
     * <ul>
     *   <li><p><b>Disposer:</b> The tasks submitted to this executor should be only house-keeping
     *   works. The threads in this executor have a priority slightly higher than the normal priority.
     *   This is on the assumption that the tasks will spend most of their time waiting for some
     *   condition, and complete quickly when the condition become true.</p></li>
     *
     *   <li><p><b>Normal:</b> Any background task which is not about disposing resources.</p></li>
     * </ul>
     *
     * The executor threads will be daemon. However the submitted tasks should still be fully
     * completed because the shutdown hook will wait for tasks to complete, up to some timeout
     * delay.
     * <p>
     * Callers should not keep a reference to the returned executor for a long time.
     * It is preferable to use it as soon as possible and discard.
     *
     * @param  disposer {@code true} if the executor is for resources disposal tasks.
     * @return The executor.
     */
    public static synchronized Executor executor(final boolean disposer) {
        final int index = (disposer) ? 1 : 0;
        ExecutorService[] executors = EXECUTORS;
        if (executors == null) {
            ensureShutdownHookRegistered();
            EXECUTORS = executors = new ExecutorService[2];
        }
        ExecutorService executor = executors[index];
        if (executor == null) {
            /*
             * In the disposer case, the tasks should be very short-lived. So limit the number
             * of threads in order to avoid the creation of many threads at startup time which
             * become idle very soon after their creation.
             */
            final int maximumPoolSize = disposer ? 4 : 100;
            final ThreadFactory factory = new Threads(disposer, true, "Pooled daemon #");
            executors[index] = executor = new ThreadPoolExecutor(0, maximumPoolSize, 60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(), factory);
        }
        return executor;
    }

    /**
     * For internal usage by {@link #executor} only.
     *
     * @param  task The task to execute.
     * @return A new thread running the given task.
     */
    @Override
    public Thread newThread(final Runnable task) {
        final String name = prefix + incrementAndGet();
        final Thread thread = new Thread(disposer ? RESOURCE_DISPOSERS : WORKERS, task, name);
        thread.setPriority(Thread.NORM_PRIORITY + 1); // WORKERS group will lower this value.
        thread.setDaemon(daemon);
        return thread;
    }

    /**
     * Ensures that the shutdown hook is registered. The shutdown process is implemented in the
     * {@link org.geotoolkit.factory.ShutdownHook#run()} method. This method should be invoked
     * once by classes that require the service provided in the shutdown hook.
     *
     * @since 3.16
     */
    public static void ensureShutdownHookRegistered() {
        try {
            Class.forName("org.geotoolkit.factory.ShutdownHook", true, Threads.class.getClassLoader());
        } catch (Exception e) {
            Logging.unexpectedException(Threads.class, "ensureShutdownHookRegistered", e);
        }
    }

    /**
     * Shutdowns the executors and wait for the non-daemon threads to complete.
     * This method should be invoked only when we think that no more tasks are
     * going to be submitted to the executor (it is actually hard to ensure that).
     *
     * @see org.geotoolkit.factory.ShutdownHook#run()
     *
     * @since 3.06
     */
    public static synchronized void shutdown() {
        final ExecutorService[] executors = EXECUTORS;
        if (executors != null) {
            for (final ExecutorService executor : executors) {
                if (executor != null) {
                    executor.shutdown();
                }
            }
            try {
                for (final ExecutorService executor : executors) {
                    if (executor != null && !executor.awaitTermination(8, TimeUnit.SECONDS)) {
                        // We can't use java.util.logging at this point since we are shutting down.
                        System.err.println("NOTE: Some background threads didn't completed.");
                        return;
                    }
                }
            } catch (InterruptedException e) {
                // Too late for logging since we are in process of shuting down.
                System.err.println(e);
            }
        }
    }
}
