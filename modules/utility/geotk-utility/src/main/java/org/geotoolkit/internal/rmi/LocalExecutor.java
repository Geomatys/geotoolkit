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
package org.geotoolkit.internal.rmi;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.geotoolkit.internal.Threads;
import org.apache.sis.util.logging.Logging;


/**
 * Executes {@linkplain ShareableTask shareable tasks} on the local machine.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
final class LocalExecutor implements TaskExecutor {
    /**
     * The executor for the tasks.
     */
    private final ExecutorService executor;

    /**
     * Creates a new executor.
     *
     * @param threaded {@code true} for using an executor service, or {@code false} for running
     *        the task directly in the current thread. The later has simpler task trace.
     */
    LocalExecutor(final boolean threaded) {
        executor = threaded ? Executors.newCachedThreadPool(Threads.createThreadFactory("LocalExecutor #")) : null;
    }

    /**
     * Returns the name of the machine hosting this executor.
     */
    static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            Logging.unexpectedException(RemoteService.logger(), LocalExecutor.class, "name", e);
        } catch (SecurityException e) {
            Logging.recoverableException(RemoteService.logger(), LocalExecutor.class, "name", e);
        }
        return "localhost";
    }

    /**
     * Returns the name of the machine hosting this executor.
     */
    @Override
    public String name() {
        return hostname();
    }

    /**
     * Executes the given task.
     */
    @Override
    public <Input,Output> TaskFuture<Output> submit(final ShareableTask<Input,Output> task) {
        return new LocalFuture<>(executor != null ? executor.submit(task) : null, task);
    }

    /**
     * Always throw an exception since this executor does not accept slaves.
     */
    @Override
    public void slave(TaskExecutor slave, boolean available) {
        throw new UnsupportedOperationException();
    }

    /**
     * Shutdown the executor.
     */
    @Override
    public void shutdown() {
        executor.shutdown();
    }
}
