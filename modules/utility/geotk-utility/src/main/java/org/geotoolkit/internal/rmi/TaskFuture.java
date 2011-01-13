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
package org.geotoolkit.internal.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;


/**
 * The pending result of a {@linkplain ShareableTask task} submited to
 * an {@linkplain TaskExecutor executor}. This is similar in spirit to
 * {@link java.util.concurrent.Future}, but the work can be performed
 * on a remote machine.
 *
 * @param <Output> The return value of the task.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @see java.util.concurrent.Future
 *
 * @since 3.00
 * @module
 */
public interface TaskFuture<Output> extends Remote {
    /**
     * Waits for the task completion and returns its result.
     *
     * @return The task result.
     * @throws RemoteException If a RMI error occurred. This is usually not an issue with the
     *         task itself, but more likely a network or RMI configuration problem.
     * @throws ExecutionException If an error occurred during the execution of the task.
     * @throws InterruptedException If the worker thread was interrupted while waiting.
     */
    Output get() throws RemoteException, ExecutionException, InterruptedException;

    /**
     * Invoked in case of failures for deleting resources created by this task.
     * The resources may be for example temporary files.
     *
     * @throws RemoteException If a RMI error occurred.
     */
    void rollback() throws RemoteException;
}
