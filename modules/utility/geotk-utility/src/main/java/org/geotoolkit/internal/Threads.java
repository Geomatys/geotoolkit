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

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.geotoolkit.lang.Debug;
import org.geotoolkit.util.logging.Logging;


/**
 * Utilities methods for threads. This class declares in a single place every {@link ThreadGroup}
 * used in Geotk. Their purpose is only to put a little bit of order in debugger informations, by
 * grouping the threads created by Geotk together under the same parent tree node.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.03
 * @module
 */
@SuppressWarnings("serial")
public final class Threads extends AtomicInteger implements ThreadFactory {
    /**
     * The parent of every threads declared in this class.
     */
    public static final ThreadGroup GEOTOOLKIT = new ThreadGroup("Geotoolkit.org");

    /**
     * The group of threads disposing resources, typically after a timeout.
     */
    public static final ThreadGroup RESOURCE_DISPOSERS = new ThreadGroup(GEOTOOLKIT, "ResourceDisposers");
    static {
        RESOURCE_DISPOSERS.setMaxPriority(Thread.NORM_PRIORITY + 2);
    }

    /**
     * The group of workers to run in a background thread.
     *
     * @since 3.05
     */
    public static final ThreadGroup WORKERS = new ThreadGroup(GEOTOOLKIT, "Workers");
    static {
        WORKERS.setMaxPriority(Thread.NORM_PRIORITY - 1);
    }

    /**
     * The executor for non-disposal works. This executor is suitable for small tasks
     * that complete relatively rapidly. Each tasks is executed immediately (they are
     * not enqueued).
     */
    private static final ExecutorService WORK_EXECUTOR =
            Executors.newCachedThreadPool(new Threads(false, true, "Pooled thread #"));

    /**
     * The executor for disposal tasks. The tasks submitted to this executor should be only
     * house-keeping works. The threads in this executor have a priority slightly higher than
     * the normal priority. Their execution should complete quickly.
     */
    private static final ScheduledExecutorService DISPOSAL_EXECUTOR =
            Executors.newScheduledThreadPool(1, new Threads(true, true, "Pooled thread #"));

    /**
     * {@code true} if the threads to be created are part of the {@link #RESOURCE_DISPOSERS}
     * group, or {@code false} if they are {@link #WORKERS} threads. This information is used
     * mostly for reporting in debugger - it has no functional impact (except on the threads
     * priority).
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
     * Creates a factory for worker threads created by the Geotk library. This factory should be
     * used by every method that use {@link java.util.concurrent.Executors} directly rather than
     * the {@code execute} methods provided in this class.
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
     * Executes the given task in a worker thread. The given work is executed immediately,
     * creating a new thread if needed.
     *
     * @param task The work to execute.
     *
     * @since 3.19
     */
    public static void executeWork(final Runnable task) {
        WORK_EXECUTOR.execute(task);
    }

    /**
     * Executes the given task in a disposer thread after the given delay. The task
     * is executed in a thread from the {@link #RESOURCE_DISPOSERS} group. They have
     * a higher priority than the worker group and may be available even if the worker
     * group is full.
     *
     * @param task  The task to execute.
     * @param delay The delay to wait before to execute the task, in milliseconds.
     *              May be zero for immediate execution.
     *
     * @since 3.19
     */
    public static void executeDisposal(final Runnable task, final long delay) {
        DISPOSAL_EXECUTOR.schedule(task, delay, TimeUnit.MILLISECONDS);
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
    static {
        try {
            Class.forName("org.geotoolkit.factory.ShutdownHook", true, Threads.class.getClassLoader());
        } catch (Exception e) {
            Logging.unexpectedException(Threads.class, "<init>", e);
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
        /*
         * We can wait for the work executors to terminate, because its tasks are
         * executed immediatly. However we can't wait for delayed tasks, because
         * the delay may be long. Executes those tasks now in this current thread.
         * We execute them in the same order than they would be executed if the
         * delay were honored.
         */
        WORK_EXECUTOR.shutdown();
        DISPOSAL_EXECUTOR.shutdown();
        final ThreadPoolExecutor ex = (ThreadPoolExecutor) DISPOSAL_EXECUTOR;
        for (final Runnable task : ex.getQueue()) {
            if (ex.remove(task)) try {
                task.run();
            } catch (Exception e) {
                // Too late for logging since we are in process of shuting down.
                System.err.println(e);
            }
        }
        try {
            // Wait for work completion. In theory, there is no disposal
            // completion to wait for, but we check anyway as a safety.
            if (!WORK_EXECUTOR    .awaitTermination(8, TimeUnit.SECONDS) ||
                !DISPOSAL_EXECUTOR.awaitTermination(2, TimeUnit.SECONDS))
            {
                // Check again in case one executor finished while we waited for the other.
                if (!WORK_EXECUTOR.isTerminated() || !DISPOSAL_EXECUTOR.isTerminated()) {
                    // We can't use java.util.logging at this point since we are shutting down.
                    System.err.println("NOTE: Some background threads didn't completed.");
                }
            }
        } catch (InterruptedException e) {
            // Too late for logging since we are in process of shuting down.
            System.err.println(e);
        }
    }

    /**
     * Returns every threads that are not {@linkplain Thread#isDaemon() daemon}. This method is
     * used only for debugging purpose in order to identify the threads which may be preventing
     * an application to quit.
     *
     * @return All non-daemon threads found.
     *
     * @since 3.17
     */
    @Debug
    public static Thread[] getNonDaemonThreads() {
        ThreadGroup root = Thread.currentThread().getThreadGroup();
        for (ThreadGroup parent; (parent = root.getParent()) != null;) {
            root = parent;
        }
        Thread[] threads;
        int n = root.activeCount();
        do {
            threads = new Thread[n << 1];
            n = root.enumerate(threads);
        } while (n == threads.length);
        /*
         * Filter the threads, keeping only the non-daemon ones.
         */
        int nc = 0;
        for (int i=0; i<n; i++) {
            final Thread thread = threads[i];
            if (!thread.isDaemon()) {
                threads[nc++] = thread;
            }
        }
        return Arrays.copyOf(threads, nc);
    }
}
