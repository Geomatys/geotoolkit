/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
 * Utilities methods for threads.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.03
 * @module
 */
@Static
public final class Threads implements ThreadFactory {
    /**
     * Do not allow instantiation of this class.
     */
    private Threads() {
    }

    /**
     * The parent of all thread groups defined in this class.
     * This is the root of our tree of thread groups.
     */
    static final ThreadGroup PARENT = new ThreadGroup("Geotoolkit.org");

    /**
     * The group of shutdown hooks.
     */
    public static final ThreadGroup SHUTDOWN_HOOKS = new ThreadGroup(PARENT, "ShutdownHooks");

    /*
     * Other ThreadGroups are defined in:
     *
     *   - SwingUtilities
     *   - FactoryUtilities
     *   - ReferenceQueueConsumer
     *
     * They are left in their respective class in order to instantiate the group only on
     * class initialization. The shutdown group is defined here because needed soon anyway.
     */

    /**
     * The group of threads pooled by {@link #EXECUTOR}.
     */
    private static final ThreadGroup POOL = new ThreadGroup(PARENT, "ThreadPool");

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
     */
    public static final Executor EXECUTOR = Executors.newCachedThreadPool(new Threads());

    /**
     * For internal usage by {@link #EXECUTOR} only.
     *
     * @param  task The task to execute.
     * @return A new thread running the given task.
     */
    @Override
    public Thread newThread(final Runnable task) {
        final Thread thread = new Thread(POOL, task);
        thread.setPriority(Thread.NORM_PRIORITY + 1);
        thread.setDaemon(true);
        return thread;
    }
}
