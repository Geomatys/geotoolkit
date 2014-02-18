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

import java.util.concurrent.Future;
import java.util.concurrent.CancellationException;
import org.geotoolkit.coverage.io.CoverageStoreException;


/**
 * The result of a {@linkplain CoverageDatabase Coverage Database} query executed in a background
 * thread. This interface extends the standard {@link Future} interface with convenience methods.
 *
 * @param <V> Type of object returned by {@link #get()} or {@link #result()}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
public interface FutureQuery<V> extends Future<V> {
    /**
     * Convenience method which block until the result is available,
     * or throw the appropriate exception otherwise.
     *
     * @return The result of the given task.
     * @throws DatabaseVetoException If a {@linkplain CoverageDatabaseListener listener}
     *         vetoed against the operation.
     * @throws CoverageStoreException If an error occurred while executing the task.
     * @throws CancellationException if the computation was canceled.
     */
    V result() throws DatabaseVetoException, CoverageStoreException, CancellationException;

    /**
     * Invokes the given task upon completion of this {@link FutureQuery}. If this
     * {@code FutureQuery} is already {@linkplain #isDone() completed}, then the given
     * task is executed immediately in the current thread. Otherwise this method returns
     * immediately, and the given task will be executed in this {@code FutureQuery} thread
     * after the query has completed either.
     * <p>
     * The given task is executed exactly once, either on success or failure.
     *
     * @param task The task to execute after completion of this {@code FutureQuery}.
     */
    void invokeAfterCompletion(Runnable task);
}
