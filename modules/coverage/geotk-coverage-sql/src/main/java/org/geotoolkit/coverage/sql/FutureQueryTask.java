/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.util.NullArgumentException;
import org.apache.sis.util.ArraysExt;


/**
 * Implementation of {@link FutureQuery}.
 *
 * @param <V> Type of object returned by {@link #get()} or {@link #result()}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
final class FutureQueryTask<V> extends FutureTask<V> implements FutureQuery<V> {
    /**
     * The tasks to run after completion, or {@code null} if none.
     */
    private Runnable[] afterCompletion;

    /**
     * Creates a {@code FutureQueryTask} that will, upon running, execute the given {@code Callable}.
     *
     * @param  callable the callable task.
     * @throws NullPointerException if callable is null.
     */
    public FutureQueryTask(final Callable<V> callable) {
        super(callable);
    }

    /**
     * Convenience method which block until the result is available,
     * or throw the appropriate exception otherwise.
     */
    @Override
    public V result() throws CoverageStoreException, CancellationException {
        try {
            return get();
        } catch (InterruptedException e) {
            final CancellationException ex = new CancellationException(e.getLocalizedMessage());
            ex.initCause(e);
            throw ex;
        } catch (ExecutionException e) {
            final Throwable cause = e.getCause();
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof CoverageStoreException) {
                throw (CoverageStoreException) cause;
            }
            throw new CoverageStoreException(cause);
        }
    }

    /**
     * Invokes the given task upon completion of this {@link FutureQuery}.
     */
    @Override
    public void invokeAfterCompletion(Runnable task) {
        if (task == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, "task"));
        }
        synchronized (this) {
            Runnable[] tasks = afterCompletion;
            if (tasks == null) {
                tasks = new Runnable[] {task};
            } else {
                tasks = ArraysExt.append(tasks, task);
            }
            afterCompletion = tasks;
        }
        if (isDone()) {
            done(); // Not a problem if invoked twice.
        }
    }

    /**
     * Run all pending tasks declared to {@link #invokeAfterCompletion(Runnable)},
     * and clears the list of pending tasks. While this method is usually invoked
     * only once, it is not a problem if it is invoked more often (which may occur
     * in some race conditions).
     */
    @Override
    protected void done() {
        final Runnable[] tasks;
        synchronized (this) {
            tasks = afterCompletion;
            afterCompletion = null;
        }
        if (tasks != null) {
            for (final Runnable task : tasks) {
                task.run();
            }
        }
    }
}
