/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import javax.sql.DataSource;
import java.sql.SQLException;

import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.internal.sql.table.TablePool;
import org.geotoolkit.resources.Errors;


/**
 * A connection to a collection of coverages declared in a SQL database.
 * The connection to the database is specified by a {@link DataSource}.
 * <p>
 * Every query methods in this class are executed in a background thread. In order to get the result
 * immediately, the {@link #now(Future)} convenience method can be used as in the example below:
 *
 * {@preformat java
 *     import static org.geotoolkit.coverage.sql.CoverageDatabase.now;
 *
 *     class MyClass {
 *         private CoverageDatabase database = ...; // Specify your database here.
 *
 *         void myMethod() throws CoverageStoreException {
 *             Layer myLayer = now(database.getLayer("Temperature"));
 *             // Use the layer here...
 *         }
 *     }
 * }
 *
 * However it is better to invoke {@code now(Future)} as late as possible, in order to have
 * more work executed concurrently.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
@ThreadSafe(concurrent = true)
public class CoverageDatabase {
    /**
     * Maximal amount of concurrent threads which can be running. It is better to not use
     * a too high value, since each threads will hold a connection to the database.
     */
    private static final int MAXIMUM_THREADS = 8;

    /**
     * Maximal amount of tasks which may be pending in the queue. If a greater amount of
     * tasks is requested, the caller thread will be blocked until the number of tasks
     * go down to this amount.
     */
    private static final int MAXIMUM_TASKS = 256;

    /**
     * The object which will manage the connections to the database.
     */
    volatile TableFactory database;

    /**
     * The executor service to use for loading data in background. We force the usage of this
     * executor because the current {@code org.geotoolkit.coverage.sql} uses thread-local JDBC
     * connections, and we want to constraint the creation of those JDBC resources in only a
     * limited amount of threads.
     */
    private final ExecutorService executor;

    /**
     * Creates a new instance using the given data source.
     *
     * @param datasource The data source.
     * @param properties The configuration properties, or {@code null} if none.
     */
    public CoverageDatabase(final DataSource datasource, final Properties properties) {
        this(new TableFactory(datasource, properties));
    }

    /**
     * Creates a new instance using the given database.
     */
    CoverageDatabase(final TableFactory db) {
        database = db;
        executor = new ThreadPoolExecutor(0, MAXIMUM_THREADS, 1, TimeUnit.MINUTES,
                   new ArrayBlockingQueue<Runnable>(MAXIMUM_TASKS, true));
    }

    /**
     * Ensures that the given argument is non-null.
     *
     * @param  name  The argument name.
     * @param  value The argument value.
     * @throws IllegalArgumentException if the given value is {@code null}.
     */
    private void ensureNonNull(final String name, final Object value) {
        if (value == null) {
            throw new NullArgumentException(Errors.getResources(database.getLocale())
                    .getString(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
    }

    /**
     * Convenience method which block until the result of the given task is available,
     * or throw the appropriate exception otherwise.
     *
     * @param  <T>  The result type.
     * @param  task The task for which the result is wanted now.
     * @return The result of the given task.
     * @throws CoverageStoreException If an error occured while executing the task.
     * @throws CancellationException if the computation was cancelled.
     */
    public static <T> T now(final Future<T> task) throws CoverageStoreException, CancellationException {
        try {
            return task.get();
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
     * Returns the name of every layers is the database.
     *
     * @return The layer of the given name.
     */
    public Future<Set<String>> getLayers() {
        return executor.submit(new GetLayers());
    }

    /**
     * The task for {@link CoverageDatabase#getLayers()}. Declared as an explicit class
     * rather than an inner class in order to have more helpful stack trace in case of failure.
     */
    private final class GetLayers implements Callable<Set<String>> {
        /** Creates a new task. */
        GetLayers() {
        }

        /** Executes the task in a background thread. */
        @Override public Set<String> call() throws CoverageStoreException {
            final TablePool<LayerTable> pool = database.layers;
            try {
                final LayerTable table = pool.acquire();
                final Set<String> names = table.getIdentifiers();
                pool.release(table);
                return names;
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            }
        }
    }

    /**
     * Returns the layer of the given name.
     *
     * @param  name The layer name.
     * @return The layer of the given name.
     */
    public Future<Layer> getLayer(final String name) {
        ensureNonNull("name", name);
        return executor.submit(new GetLayer(name));
    }

    /**
     * The task for {@link CoverageDatabase#getLayer(String)}. Declared as an explicit class
     * rather than an inner class in order to have more helpful stack trace in case of failure.
     */
    private final class GetLayer implements Callable<Layer> {
        private final String name;

        /** Creates a new task. */
        GetLayer(final String name) {
            this.name = name;
        }

        /** Executes the task in a background thread. */
        @Override public Layer call() throws CoverageStoreException {
            final TablePool<LayerTable> pool = database.layers;
            try {
                final LayerTable table = pool.acquire();
                final Layer layer = table.getEntry(name);
                pool.release(table);
                return layer;
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            }
        }
    }

    /**
     * Returns a time range encompassing all coverages in this layer.
     * This method is equivalent to the code below, except that more
     * code are executed in the background thread:
     *
     * {@preformat java
     *     return now(getLayer(layer)).getTimeRange();
     * }
     *
     * @param  layer The layer for which the time range is desired.
     * @return The time range encompassing all coverages.
     *
     * @see Layer#getTimeRange()
     */
    public Future<DateRange> getTimeRange(final String layer) {
        ensureNonNull("layer", layer);
        return executor.submit(new GetTimeRange(layer));
    }

    /**
     * The task for {@link CoverageDatabase#getTimeRange(String)}. Declared as an explicit class
     * rather than an inner class in order to have more helpful stack trace in case of failure.
     */
    private final class GetTimeRange implements Callable<DateRange> {
        private final String layer;

        /** Creates a new task. */
        GetTimeRange(final String layer) {
            this.layer = layer;
        }

        /** Executes the task in a background thread. */
        @Override public DateRange call() throws CoverageStoreException {
            final TablePool<LayerTable> pool = database.layers;
            final Layer entry;
            try {
                final LayerTable table = pool.acquire();
                entry = table.getEntry(layer);
                pool.release(table);
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            }
            return entry.getTimeRange();
        }
    }

    /**
     * Returns the set of dates when a coverage is available.
     * This method is equivalent to the code below, except that
     * more code are executed in the background thread:
     *
     * {@preformat java
     *     return now(getLayer(layer)).getAvailableTimes();
     * }
     *
     * @param  layer The layer for which the available times are desired.
     * @return The set of dates.
     *
     * @see Layer#getAvailableTimes()
     */
    public Future<SortedSet<Date>> getAvailableTimes(final String layer) {
        ensureNonNull("layer", layer);
        return executor.submit(new GetAvailableTimes(layer));
    }

    /**
     * The task for {@link CoverageDatabase#getAvailableTimes(String)}. Declared as an explicit class
     * rather than an inner class in order to have more helpful stack trace in case of failure.
     */
    private final class GetAvailableTimes implements Callable<SortedSet<Date>> {
        private final String layer;

        /** Creates a new task. */
        GetAvailableTimes(final String layer) {
            this.layer = layer;
        }

        /** Executes the task in a background thread. */
        @Override public SortedSet<Date> call() throws CoverageStoreException {
            final TablePool<LayerTable> pool = database.layers;
            final Layer entry;
            try {
                final LayerTable table = pool.acquire();
                entry = table.getEntry(layer);
                pool.release(table);
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            }
            return entry.getAvailableTimes();
        }
    }

    /**
     * Returns the set of altitudes where a coverage is available.
     * This method is equivalent to the code below, except that
     * more code are executed in the background thread:
     *
     * {@preformat java
     *     return now(getLayer(layer)).getAvailableElevations();
     * }
     *
     * @param  layer The layer for which the available elevations are desired.
     * @return The set of altitudes.
     *
     * @see Layer#getAvailableElevations()
     */
    public Future<SortedSet<Number>> getAvailableElevations(final String layer) {
        ensureNonNull("layer", layer);
        return executor.submit(new GetAvailableElevations(layer));
    }

    /**
     * The task for {@link CoverageDatabase#getAvailableElevations(String)}. Declared as an explicit
     * class rather than an inner class in order to have more helpful stack trace in case of failure.
     */
    private final class GetAvailableElevations implements Callable<SortedSet<Number>> {
        private final String layer;

        /** Creates a new task. */
        GetAvailableElevations(final String layer) {
            this.layer = layer;
        }

        /** Executes the task in a background thread. */
        @Override public SortedSet<Number> call() throws CoverageStoreException {
            final TablePool<LayerTable> pool = database.layers;
            final Layer entry;
            try {
                final LayerTable table = pool.acquire();
                entry = table.getEntry(layer);
                pool.release(table);
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            }
            return entry.getAvailableElevations();
        }
    }

    /**
     * Returns the ranges of valid <cite>geophysics</cite> values for each band of the given layer.
     * This method is equivalent to the code below, except that more code are executed in the
     * background thread:
     *
     * {@preformat java
     *     return now(getLayer(layer)).getSampleValueRanges();
     * }
     *
     * @param  layer The layer for which the range of measurement values is desired.
     * @return The range of valid sample values.
     *
     * @see Layer#getSampleValueRanges()
     */
    public Future<List<MeasurementRange<?>>> getSampleValueRanges(final String layer) {
        ensureNonNull("layer", layer);
        return executor.submit(new GetSampleValueRanges(layer));
    }

    /**
     * The task for {@link CoverageDatabase#getSampleValueRanges(String)}. Declared as an explicit
     * class rather than an inner class in order to have more helpful stack trace in case of failure.
     */
    private final class GetSampleValueRanges implements Callable<List<MeasurementRange<?>>> {
        private final String layer;

        /** Creates a new task. */
        GetSampleValueRanges(final String layer) {
            this.layer = layer;
        }

        /** Executes the task in a background thread. */
        @Override public List<MeasurementRange<?>> call() throws CoverageStoreException {
            final TablePool<LayerTable> pool = database.layers;
            final Layer entry;
            try {
                final LayerTable table = pool.acquire();
                entry = table.getEntry(layer);
                pool.release(table);
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            }
            return entry.getSampleValueRanges();
        }
    }

    /**
     * Reads the data of a two-dimensional slice and returns them as a coverage.
     * Note that the returned two-dimensional slice is not garanteed to have exactly
     * the {@linkplain CoverageQuery#getEnvelope() requested envelope}. Callers may
     * need to check the geometry of the returned envelope and perform an additional
     * resampling if needed.
     *
     * @param  request Parameters used to control the reading process.
     * @return The coverage.
     *
     * @see LayerCoverageReader#readSlice
     */
    public Future<GridCoverage2D> readSlice(final CoverageQuery request) {
        ensureNonNull("request", request);
        return executor.submit(new ReadSlice(request));
    }

    /**
     * The task for {@link CoverageDatabase#readSlice(CoverageQuery, IIOListeners)}. Declared as an
     * explicit class rather than an inner class in order to have more helpful stack trace in case
     * of failure.
     */
    private final class ReadSlice implements Callable<GridCoverage2D> {
        /** The query. */
        private final CoverageQuery query;

        /** Creates a new task. */
        ReadSlice(final CoverageQuery query) {
            this.query = query;
        }

        /** Executes the task in a background thread. */
        @Override public GridCoverage2D call() throws CoverageStoreException {
            final CoverageEnvelope envelope = query.getEnvelope();
            final TablePool<GridCoverageTable> pool = database.coverages;
            final GridCoverageReference entry;
            try {
                final GridCoverageTable table = pool.acquire();
                table.setLayer(query.getLayer());
                table.envelope.setAll(envelope);
                entry = table.getEntry();
                pool.release(table);
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            } catch (TransformException e) {
                throw new CoverageStoreException(Errors.format(
                        Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM), e);
            }
            return entry.read(envelope, query.listeners);
        }
    }

    /**
     * Configures and returns a {@link GridCoverageReader} for the given layer. This provides an
     * alternative way (as compared to {@link #readSlice readSlice}) for reading two-dimensional
     * slices of coverage. This method is provided for interoperability with library which want
     * to access to the data through the {@link GridCoverageReader} API only.
     * <p>
     * The default implementation is like below, omitting the check for {@code null} argument
     * value:
     *
     * {@preformat java
     *     LayerCoverageReader reader = new LayerCoverageReader(this);
     *     reader.setInput(now(getLayer(layer)));
     *     return reader;
     * }
     *
     * @param  layer The name of the initial layer to be read by the returned reader, or {@code null}.
     * @return A grid coverage reader using the given layer as input.
     * @throws CoverageStoreException If an error occured while querying the database.
     */
    public LayerCoverageReader createGridCoverageReader(final String layer) throws CoverageStoreException {
        final LayerCoverageReader reader = new LayerCoverageReader(this);
        if (layer != null) {
            reader.setInput(now(getLayer(layer)));
        }
        return reader;
    }

    /**
     * Flushes the cache. This method shall be invoked when the database content has been
     * changed by some other way than through the {@code CoverageDatabase} API.
     */
    public void flush() {
        database = new TableFactory(database);
    }

    /**
     * Disposes the resources used by this database.
     */
    public void dispose() {
        executor.shutdown();
    }
}
