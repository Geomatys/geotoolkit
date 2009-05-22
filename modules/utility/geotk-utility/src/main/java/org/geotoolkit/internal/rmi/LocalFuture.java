/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;


/**
 * The pending result of a task submited to a {@link LocalExecutor}.
 *
 * @param <Output> The return value of the task.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
final class LocalFuture<Output> implements TaskFuture<Output> {
    /**
     * The {@code Future} given by {@link java.util.concurrent.ExecutorService}.
     */
    private final Future<Output> future;

    /**
     * The task being run.
     */
    private final ShareableTask<?,Output> task;

    /**
     * Creates a new {@code LocalFuture}.
     *
     * @param future The {@code Future} given by {@link java.util.concurrent.ExecutorService}.
     * @param task The task being run.
     */
    LocalFuture(final Future<Output> future, final ShareableTask<?,Output> task) {
        this.future = future;
        this.task = task;
    }

    /**
     * Returns {@code true} if the task is executed on a different thread.
     */
    public boolean isThreaded() {
        return future != null;
    }

    /**
     * Returns the task result.
     *
     * @throws ExecutionException If an error occured during the execution of the task.
     * @throws InterruptedException If the worker thread was interrupted while waiting.
     */
    @Override
    public Output get() throws ExecutionException, InterruptedException {
        if (future != null) {
            return future.get();
        } else try {
            return task.call();
        } catch (RuntimeException e) {
            throw e; // Let them propagates, for simplier task trace.
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    /**
     * Invoked in case of failures for deleting the resources that the task may have created.
     */
    @Override
    public void rollback() {
        task.rollback();
    }
}
