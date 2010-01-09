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
package org.geotoolkit.internal.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.geotoolkit.lang.ThreadSafe;


/**
 * Executes {@linkplain ShareableTask shareable tasks} on a local or remote machine.
 * This is similar in spirit to {@link java.util.concurrent.ExecutorService}, but the
 * work can be performed on a remote machine.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @see java.util.concurrent.ExecutorService
 *
 * @since 3.00
 * @module
 */
@ThreadSafe
public interface TaskExecutor extends Remote {
    /**
     * Returns the name of this executor. This is used mostly for logging and debugging purpose.
     * It will typically be the name of the machine hosting the executor.
     *
     * @return The name of this executor.
     * @throws RemoteException If a RMI error occured.
     */
    String name() throws RemoteException;

    /**
     * Executes the given task.
     *
     * @param  <Input>  The type of input objects.
     * @param  <Output> The type of return value.
     * @param  task The task to execute.
     * @return The pending result of the task.
     * @throws RemoteException If a RMI error occured. This is usually not an issue with the
     *         task itself, but more likely a network or RMI configuration problem.
     */
    <Input,Output> TaskFuture<Output> submit(ShareableTask<Input,Output> task) throws RemoteException;

    /**
     * Adds or removes a slave to this executor. A slave is an other executor (typically
     * from a remote machine) to which this executor can delegate the work. This method
     * is for internal use by this package and should not be invoked directly.
     *
     * @param  slave The slave to add or remove.
     * @param  available {@code true} for adding a slave, or {@code false} for removing it.
     * @throws RemoteException If a RMI error occured.
     */
    void slave(TaskExecutor slave, boolean available) throws RemoteException;

    /**
     * Shutdown the executor. This will also shutdown every slaves, if there is any.
     *
     * @throws RemoteException If a RMI error occured.
     */
    void shutdown() throws RemoteException;
}
