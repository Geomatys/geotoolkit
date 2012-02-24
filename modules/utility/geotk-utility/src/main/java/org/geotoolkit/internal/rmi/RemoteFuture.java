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

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import net.jcip.annotations.ThreadSafe;


/**
 * The pending result of a task submitted to a {@link RemoteExecutor}.
 *
 * @param <Output> The return value of the task.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@ThreadSafe
final class RemoteFuture<Output> extends UnicastRemoteObject implements TaskFuture<Output> {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 7868765562360450526L;

    /**
     * The task for which results are being computed.
     */
    private final ShareableTask<?,Output> task;

    /**
     * The futures to be aggregated and returned by this wrapper.
     */
    private final List<TaskFuture<Output>> futures;

    /**
     * The result, computed when first required.
     */
    private transient Output output;

    /**
     * Creates a new remote future.
     *
     * @param  task The task for which results are being computed.
     * @param  future The future to be returned by this wrapper.
     * @throws RemoteException If an error occurred while exporting this future.
     */
    RemoteFuture(final ShareableTask<?,Output> task, final List<TaskFuture<Output>> futures) throws RemoteException {
        this.task = task;
        this.futures = futures;
    }

    /**
     * Returns the task result.
     *
     * @throws RemoteException If a RMI error occurred.
     * @throws ExecutionException If an error occurred during the execution of the task.
     * @throws InterruptedException If the worker thread was interrupted while waiting.
     */
    @Override
    public synchronized Output get() throws RemoteException, ExecutionException, InterruptedException {
        if (output == null) {
            final List<Output> results = new ArrayList<>(futures.size());
            for (final TaskFuture<Output> future : futures) {
                results.add(future.get());
            }
            output = task.aggregate(results);
        }
        return output;
    }

    /**
     * Invoked in case of failures for deleting the resources that the task may have created.
     *
     * @throws RemoteException If a RMI error occurred.
     */
    @Override
    public synchronized void rollback() throws RemoteException {
        RemoteException failure = null;
        for (final ListIterator<TaskFuture<Output>> it=futures.listIterator(futures.size()); it.hasPrevious();) {
            final TaskFuture<Output> future = it.previous();
            it.remove(); // Remove as we rollback.
            try {
                future.rollback();
            } catch (RemoteException e) {
                // Remember the first failure, and continue the rollbacks
                // before to report that failure.
                if (failure == null) {
                    failure = e;
                } else {
                    failure.addSuppressed(e);
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }
}
