/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.atomic.AtomicInteger;

import org.geotoolkit.lang.Debug;
import org.apache.sis.util.logging.Logging;


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
public final class Threads extends AtomicInteger implements ThreadFactory, RejectedExecutionHandler {
    /**
     * The parent of every threads declared in this class. This parent will be declared as close
     * as possible to the root of all thread groups (i.e. not as an application thread subgroup).
     */
    public static final ThreadGroup GEOTOOLKIT;
    static {
        /*
         * Tries to put the ThreadGroup at the root, if we are allowed to do so. The intend is to
         * separate the Geotk thread groups from the user application thread groups. Without this
         * code, the Geotk thread group would appear as an user application sub-group.
         */
        ThreadGroup parent = Thread.currentThread().getThreadGroup();
        try {
            ThreadGroup candidate;
            while ((candidate = parent.getParent()) != null) {
                parent = candidate;
            }
        } catch (SecurityException e) {
            // If we are not allowed to get the parent, stop there.
            // We went up in the tree as much as we were allowed to.
        }
        GEOTOOLKIT = new ThreadGroup(parent, "Geotoolkit.org");
    }

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

    /**
     * The executor for non-disposal works. This executor is suitable for small tasks
     * that complete relatively rapidly.  We don't need too many threads, because the
     * amount of tasks to submit is not that high.
     * <p>
     * Current implementation uses 2 threads and enqueue any additional tasks arriving
     * faster than what the 2 threads can process. If 1000 tasks have been enqueued,
     * then we will start creating more threads. If the maximal number of threads have
     * been reached, then any additional tasks will be blocked until a slot is made
     * available.
     * <p>
     * The optimal number of threads for CPU-bounds tasks is typically <va>n</var>+1
     * where <va>n</var> is the number of processors. Here, we limit to <va>n</var>
     * threads since the caller thread is the "+1".
     */
    private static final ExecutorService WORK_EXECUTOR;
    static {
        final Threads handlers = new Threads(false, true, "Pooled thread #");
        final int n = Math.max(2, Runtime.getRuntime().availableProcessors());
        final ThreadPoolExecutor ex = new ThreadPoolExecutor(2, n, 5L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(1000), handlers, handlers);
        ex.allowCoreThreadTimeOut(true);
        WORK_EXECUTOR = ex;
    }

    /**
     * The executor for disposal tasks. The tasks submitted to this executor should be only
     * house-keeping works. The threads in this executor have a priority slightly higher than
     * the normal priority. Their execution should complete quickly.
     * <p>
     * A single thread is sufficient since there is not so many tasks to submit, and they
     * are expected to complete quickly. Because this executor acts as a fixed-size pool,
     * we don't want to have too many threads spending 99% of their time idle.
     */
    private static final ScheduledExecutorService DISPOSAL_EXECUTOR =
            Executors.newScheduledThreadPool(1, new Threads(true, true, "Disposer thread #"));

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
     * Executes the given task in a worker thread.
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
     * Invoked when a new thread needs to be created. This method is public as an
     * implementation side-effect and should not be invoked directly.
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
     * Invoked when a task can not be accepted because the queue is full and the maximal number
     * of threads have been reached. This method blocks until a slot is made available.
     *
     * @param task The task to execute.
     * @param executor The executor that invoked this method.
     */
    @Override
    public void rejectedExecution(final Runnable task, final ThreadPoolExecutor executor) {
        try {
            executor.getQueue().put(task);
        } catch (InterruptedException e) {
            throw new RejectedExecutionException(e);
        }
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
         * executed immediately. However we can't wait for delayed tasks, because
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
