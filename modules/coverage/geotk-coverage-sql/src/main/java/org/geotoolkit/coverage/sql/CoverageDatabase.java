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

import java.io.IOException;
import java.util.Locale;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ArrayBlockingQueue;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import javax.sql.DataSource;
import java.sql.SQLException;
import net.jcip.annotations.ThreadSafe;

import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;

import org.geotoolkit.util.Localized;
import org.geotoolkit.util.DateRange;
import org.geotoolkit.util.MeasurementRange;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.image.io.IIOListeners;
import org.geotoolkit.internal.Threads;
import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.internal.sql.table.ConfigurationKey;
import org.geotoolkit.internal.sql.table.TablePool;
import org.geotoolkit.resources.Errors;


/**
 * A connection to a collection of coverages declared in a SQL database.
 * The connection to the database is specified by a {@link DataSource}.
 * <p>
 * Every query methods in this class are executed in a background thread. In order to get the result
 * immediately, the {@link FutureQuery#result()} convenience method can be used as in the example below:
 *
 * {@preformat java
 *     CoverageDatabase database = ...; // Specify your database here.
 *     Layer myLayer = database.getLayer("Temperature").result();
 *     // Use the layer here...
 * }
 *
 * However it is better to invoke {@code FutureQuery.result()} as late as possible,
 * in order to have more work executed concurrently.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.10
 * @module
 */
@ThreadSafe
public class CoverageDatabase implements Localized {
    /**
     * Maximal amount of concurrent threads which can be running. Note that higher values are
     * not necessarily better, since each thread will typically perform a lot of SQL or I/O
     * operations and too many concurrency in such operations may decrease performance.
     */
    private static final int MAXIMUM_THREADS = 8;

    /**
     * Maximal amount of tasks which may be pending in the queue. If a greater amount of
     * tasks is requested, the caller thread will be blocked until the number of tasks
     * go down to this amount.
     */
    private static final int MAXIMUM_TASKS = 256;

    /**
     * The default instance. Created only when first requested.
     *
     * @see #getDefaultInstance()
     */
    private static Reference<CoverageDatabase> instance;

    /**
     * A description of the parameters expected by the {@code CoverageDatabase} constructors.
     * See the {@linkplain org.geotoolkit.coverage.sql package javadoc} for an overview of
     * those parameters.
     *
     * @since 3.18
     */
    public static final ParameterDescriptorGroup PARAMETERS;
    static {
        final ConfigurationKey[] keys = ConfigurationKey.values();
        final ParameterDescriptor<?>[] param = new ParameterDescriptor<?>[keys.length];
        for (int i=0; i<keys.length; i++) {
            final ConfigurationKey key = keys[i];
            param[i] = new DefaultParameterDescriptor<String>(key.key, null, String.class, key.defaultValue, false);
        }
        PARAMETERS = new DefaultParameterDescriptorGroup("CoverageDatabase", param);
    }

    /**
     * The object which will manage the connections to the database.
     * This field shall never be {@code null}.
     */
    volatile TableFactory database;

    /**
     * The executor service to use for loading data in background. Concurrency is only one raison
     * for our usage of executor here. The other raison is to limit the amount of JDBC resources
     * to be allocated, since we use a different connection for different thread.
     */
    private final Executor executor;

    /**
     * The listener list.
     */
    private final List<CoverageDatabaseListener> listeners;

    /**
     * The listeners as an array, or {@code null} if it need to be recomputed.
     * A new array will be created every time the listener list is changed. We
     * iterate over an array instead than over the list in order to avoid to
     * hold the synchronization lock during the iteration.
     */
    private volatile CoverageDatabaseListener[] listenerArray;

    /**
     * Creates a new instance using the given properties. The properties shall contains at
     * least an entry for the {@code "URL"} key. The value of this entry shall be a JDBC URL
     * in the form of {@code "jdbc:postgresql://host/database"}.
     * <p>
     * See the {@linkplain org.geotoolkit.coverage.sql package javadoc} for the list of
     * parameters supported by this constructor.
     *
     * @param properties The configuration properties.
     *
     * @since 3.11
     */
    public CoverageDatabase(final Properties properties) {
        this(null, properties);
    }

    /**
     * Creates a new instance using the given parameters. This constructor provides the same
     * functionality than {@link #CoverageDatabase(Properties)}, but using the ISO 19111
     * parameters construct instead than the Java properties.
     * <p>
     * See the {@linkplain org.geotoolkit.coverage.sql package javadoc} for the list of
     * parameters supported by this constructor.
     *
     * @param parameters The configuration parameters.
     *
     * @since 3.18
     */
    public CoverageDatabase(final ParameterValueGroup parameters) {
        this(null, parameters);
    }

    /**
     * Creates a new instance using the given data source and configuration properties.
     * See the {@linkplain org.geotoolkit.coverage.sql package javadoc} for the list of
     * parameters supported by this constructor.
     *
     * @param datasource The data source, or {@code null} for creating it from the URL.
     * @param properties The configuration properties, or {@code null} if none.
     */
    public CoverageDatabase(final DataSource datasource, final Properties properties) {
        this(new TableFactory(datasource, properties));
    }

    /**
     * Creates a new instance using the given data source and configuration parameters.
     * See the {@linkplain org.geotoolkit.coverage.sql package javadoc} for the list of
     * parameters supported by this constructor.
     * <p>
     * This constructor provides the same functionality than
     * {@link #CoverageDatabase(DataSource, Properties)}, but using the ISO 19111
     * parameters construct instead than the Java properties.
     *
     * @param datasource The data source, or {@code null} for creating it from the URL.
     * @param parameters The configuration parameters, or {@code null} if none.
     *
     * @since 3.18
     */
    public CoverageDatabase(final DataSource datasource, final ParameterValueGroup parameters) {
        this(datasource, singleton(parameters));
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static Properties singleton(final ParameterValueGroup parameters) {
        if (parameters == null) {
            return null;
        }
        final Properties properties = new Properties();
        properties.put(ConfigurationKey.PARAMETERS, parameters);
        return properties;
    }

    /**
     * Creates a new instance using the given database.
     */
    CoverageDatabase(final TableFactory db) {
        database  = db;
        executor  = new Executor();
        listeners = new ArrayList<CoverageDatabaseListener>(4);
    }

    /**
     * Returns the default instance, or {@code null} if none. The default instance can be specified by
     * the <a href="http://www.geotoolkit.org/modules/utility/geotk-setup/index.html">geotk-setup</a>
     * module.
     *
     * @return The default instance, or {@code null} if none.
     * @throws CoverageStoreException If an error occurred while fetching the default instance.
     *
     * @since 3.11
     */
    public static synchronized CoverageDatabase getDefaultInstance() throws CoverageStoreException {
        if (instance != null) {
            final CoverageDatabase database = instance.get();
            if (database != null) {
                return database;
            }
            instance = null;
        }
        final Properties properties;
        try {
            properties = Installation.COVERAGES.getDataSource();
        } catch (IOException e) {
            throw new CoverageStoreException(e);
        }
        if (properties != null) {
            final CoverageDatabase database = new CoverageDatabase(properties) {
                @Override public void dispose() {
                    synchronized (CoverageDatabase.class) {
                        instance = null;
                    }
                    super.dispose();
                }
            };
            instance = new WeakReference<CoverageDatabase>(database);
            return database;
        }
        return null;
    }

    /**
     * Returns the CRS authority factory used by this database. This factory is typically backed
     * by the PostGIS {@code "spatial_ref_sys"} table - this is usually <strong>not</strong> the
     * standard EPSG factory used by default in the Geotk library. In particular, axis order are
     * often different.
     *
     * @return The CRS authority factory used by this database.
     * @throws FactoryException If the factory can not be created.
     *
     * @since 3.12
     */
    public CRSAuthorityFactory getCRSAuthorityFactory() throws FactoryException {
        return database.getCRSAuthorityFactory();
    }

    /**
     * Returns the Coordinate Reference System used by the database for indexing the coverages
     * envelopes. This is the "native" CRS in which this {@code CoverageDatabase} instance will
     * transform the requested envelopes before to execute the queries.
     *
     * @return The "native" coordinate reference system.
     *
     * @since 3.11
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return database.spatioTemporalCRS;
    }

    /**
     * The executor used by {@link CoverageDatabase}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.17
     *
     * @since 3.11
     * @module
     */
    private static final class Executor extends ThreadPoolExecutor {
        /**
         * Creates a new executor.
         */
        Executor() {
            super(0, MAXIMUM_THREADS, 1, TimeUnit.MINUTES,
                  new ArrayBlockingQueue<Runnable>(MAXIMUM_TASKS, true),
                  Threads.createThreadFactory("CoverageDatabase #"));
        }

        /**
         * Returns the {@link FutureQueryTask} for the given callable task.
         */
        @Override
        protected <T> RunnableFuture<T> newTaskFor(final Callable<T> task) {
            return new FutureQueryTask<T>(task);
        }

        /**
         * Executes the given task and casts the result to {@link FutureQuery},
         * which is the expected type.
         */
        @Override
        public <T> FutureQuery<T> submit(final Callable<T> task) {
            return (FutureQuery<T>) super.submit(task);
        }
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
            throw new NullArgumentException(Errors.getResources(getLocale())
                    .getString(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
    }

    /**
     * Returns the name of every layers is the database.
     *
     * @return The layer of the given name.
     */
    public FutureQuery<Set<String>> getLayers() {
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
            final Set<String> names;
            try {
                final LayerTable table = pool.acquire();
                names = table.getIdentifiers();
                pool.release(table);
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            }
            return names;
        }
    }

    /**
     * Returns the layer of the given name.
     *
     * @param  name The layer name.
     * @return The layer of the given name.
     */
    public FutureQuery<Layer> getLayer(final String name) {
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
            final LayerEntry layer;
            try {
                final LayerTable table = pool.acquire();
                layer = table.getEntry(name);
                pool.release(table);
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            }
            layer.setCoverageDatabase(CoverageDatabase.this);
            return layer;
        }
    }

    /**
     * Adds a new layer of the given name, if it does not already exist. If a layer of the
     * given name already exists, then this method does nothing and returns {@code false}.
     *
     * @param  name The name of the new layer.
     * @return {@code true} if the layer has been added, of {@code false} if a layer of
     *         the given name already exists.
     *
     * @since 3.11
     */
    public FutureQuery<Boolean> addLayer(final String name) {
        ensureNonNull("name", name);
        return executor.submit(new AddLayer(name));
    }

    /**
     * The task for {@link CoverageDatabase#addLayer(String)}. Declared as an explicit class
     * rather than an inner class in order to have more helpful stack trace in case of failure.
     */
    private final class AddLayer implements Callable<Boolean> {
        private final String name;

        /** Creates a new task. */
        AddLayer(final String name) {
            this.name = name;
        }

        /** Executes the task in a background thread. */
        @Override public Boolean call() throws CoverageStoreException {
            fireChange(true, +1, name);
            final TablePool<LayerTable> pool = database.layers;
            boolean added;
            try {
                final LayerTable table = pool.acquire();
                added = table.createIfAbsent(name);
                pool.release(table);
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            }
            fireChange(false, added ? 1 : 0, name);
            return added;
        }
    }

    /**
     * Removes the layer of the given name, if it exist. If no layer of the
     * given name exists, then this method does nothing and returns {@code false}.
     * <p>
     * <strong>This action removes all references to raster data declared in that layer</strong>,
     * unless the foreigner key constraints in the database have been changed from their default
     * values. Note that the raster files are never deleted by this method.
     *
     * @param  name The name of the layer to remove.
     * @return {@code true} if the layer has been removed, of {@code false} if no layer of
     *         the given name exists.
     *
     * @since 3.11
     */
    public FutureQuery<Boolean> removeLayer(final String name) {
        ensureNonNull("name", name);
        return executor.submit(new RemoveLayer(name));
    }

    /**
     * The task for {@link CoverageDatabase#removeLayer(String)}. Declared as an explicit class
     * rather than an inner class in order to have more helpful stack trace in case of failure.
     */
    private final class RemoveLayer implements Callable<Boolean> {
        private final String name;

        /** Creates a new task. */
        RemoveLayer(final String name) {
            this.name = name;
        }

        /** Executes the task in a background thread. */
        @Override public Boolean call() throws CoverageStoreException {
            fireChange(true, -1, name);
            final TablePool<LayerTable> pool = database.layers;
            int removed;
            try {
                final LayerTable table = pool.acquire();
                removed = table.delete(name);
                pool.release(table);
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            }
            fireChange(false, -removed, name);
            return (removed != 0);
        }
    }

    /**
     * Returns a time range encompassing all coverages in this layer.
     * This method is equivalent to the code below, except that more
     * code are executed in the background thread:
     *
     * {@preformat java
     *     return getLayer(layer).result().getTimeRange();
     * }
     *
     * @param  layer The layer for which the time range is desired.
     * @return The time range encompassing all coverages.
     *
     * @see Layer#getTimeRange()
     */
    public FutureQuery<DateRange> getTimeRange(final String layer) {
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
     *     return getLayer(layer).result().getAvailableTimes();
     * }
     *
     * @param  layer The layer for which the available times are desired.
     * @return The set of dates.
     *
     * @see Layer#getAvailableTimes()
     */
    public FutureQuery<SortedSet<Date>> getAvailableTimes(final String layer) {
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
     *     return getLayer(layer).result().getAvailableElevations();
     * }
     *
     * @param  layer The layer for which the available elevations are desired.
     * @return The set of altitudes.
     *
     * @see Layer#getAvailableElevations()
     */
    public FutureQuery<SortedSet<Number>> getAvailableElevations(final String layer) {
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
     *     return getLayer(layer).result().getSampleValueRanges();
     * }
     *
     * @param  layer The layer for which the range of measurement values is desired.
     * @return The range of valid sample values.
     *
     * @see Layer#getSampleValueRanges()
     */
    public FutureQuery<List<MeasurementRange<?>>> getSampleValueRanges(final String layer) {
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
     * Note that the returned two-dimensional slice is not guaranteed to have exactly
     * the {@linkplain CoverageQuery#getEnvelope() requested envelope}. Callers may
     * need to check the geometry of the returned envelope and perform an additional
     * resampling if needed.
     *
     * @param  layer     The layer of the coverage to query.
     * @param  envelope  The desired envelope and resolution, or {@code null} for all data.
     * @param  listeners The listeners, or {@code null} if none.
     * @return The coverage.
     *
     * @see LayerCoverageReader#readSlice(int, GridCoverageReadParam)
     *
     * @since 3.20
     */
    public FutureQuery<GridCoverage2D> readSlice(final String layer, final CoverageEnvelope envelope, final IIOListeners listeners) {
        ensureNonNull("layer", layer);
        return executor.submit(new ReadSlice(layer, envelope, listeners));
    }

    /**
     * Reads the data of a two-dimensional slice and returns them as a coverage.
     *
     * @param  request Parameters used to control the reading process.
     * @return The coverage.
     *
     * @deprecated Replaced by {@link #readSlice(String, CoverageEnvelope, IIOListeners)}
     *             because the {@link CoverageQuery} class has been deprecated.
     */
    @Deprecated
    public FutureQuery<GridCoverage2D> readSlice(final CoverageQuery request) {
        ensureNonNull("request", request);
        return readSlice(request.getLayer(), request.getEnvelope(), request.listeners);
    }

    /**
     * The task for {@link CoverageDatabase#readSlice(CoverageQuery, IIOListeners)}. Declared as an
     * explicit class rather than an inner class in order to have more helpful stack trace in case
     * of failure.
     */
    private final class ReadSlice implements Callable<GridCoverage2D> {
        private final String           layer;
        private final CoverageEnvelope envelope;
        private final IIOListeners     listeners;

        /** Creates a new task. */
        ReadSlice(final String layer, final CoverageEnvelope envelope, final IIOListeners listeners) {
            this.layer     = layer;
            this.envelope  = envelope;
            this.listeners = listeners;
        }

        /** Executes the task in a background thread. */
        @Override public GridCoverage2D call() throws CoverageStoreException {
            final TablePool<GridCoverageTable> pool = database.coverages;
            final GridCoverageReference entry;
            try {
                final GridCoverageTable table = pool.acquire();
                table.setLayer(layer);
                table.envelope.setAll(envelope);
                entry = table.getEntry();
                pool.release(table);
            } catch (SQLException e) {
                throw new CoverageStoreException(e);
            } catch (TransformException e) {
                throw new CoverageStoreException(Errors.format(
                        Errors.Keys.ILLEGAL_COORDINATE_REFERENCE_SYSTEM), e);
            }
            return entry.read(envelope, listeners);
        }
    }

    /**
     * Configures and returns a {@link GridCoverageReader} for the given layer. This provides an
     * alternative way (as compared to {@link #readSlice readSlice}) for reading two-dimensional
     * slices of coverage. This method is provided for inter-operability with libraries which want
     * to access to the data through the {@link GridCoverageReader} API only.
     *
     * @param  layer The name of the initial layer to be read by the returned reader, or {@code null}.
     * @return A grid coverage reader using the given layer as input.
     * @throws CoverageStoreException If an error occurred while querying the database.
     */
    public LayerCoverageReader createGridCoverageReader(final String layer) throws CoverageStoreException {
        final FutureQuery<Layer> future = (layer != null) ? getLayer(layer) : null;
        return new LayerCoverageReader(this, future);
    }

    /**
     * Configures and returns a {@link LayerCoverageWriter} for the given layer.
     * This method is provided for inter-operability with libraries which want
     * to add data through the {@link LayerCoverageWriter} API only.
     *
     * @param  layer The name of the initial layer to be read by the returned writer, or {@code null}.
     * @return A grid coverage writer using the given layer as input.
     * @throws CoverageStoreException If an error occurred while querying the database.
     *
     * @since 3.20
     */
    public LayerCoverageWriter createGridCoverageWriter(final String layer) throws CoverageStoreException {
        final FutureQuery<Layer> future = (layer != null) ? getLayer(layer) : null;
        return new LayerCoverageWriter(this, future);
    }

    /**
     * Adds the given object to the list of objects to notify about changes in database content.
     *
     * @param listener The new listener to add.
     *
     * @since 3.12
     */
    public void addListener(final CoverageDatabaseListener listener) {
        if (listener != null) {
            synchronized (listeners) {
                if (!listeners.contains(listener)) {
                    if (listeners.add(listener)) {
                        listenerArray = null;
                    }
                }
            }
        }
    }

    /**
     * Removes the given object from the list of objects to notify about changes in database
     * content. This method does nothing if the given object is not a member of the listener
     * list.
     *
     * @param listener The listener to remove.
     *
     * @since 3.12
     */
    public void removeListener(final CoverageDatabaseListener listener) {
        synchronized (listeners) {
            if (listeners.remove(listener)) {
                listenerArray = null;
            }
        }
    }

    /**
     * Returns all listeners to notify about changes in database constent, or an empty array
     * if none. This method returns a direct reference to the internal array; <strong>do not
     * modify</strong>.
     *
     * @since 3.12
     */
    private CoverageDatabaseListener[] getInternalListeners() {
        CoverageDatabaseListener[] array = listenerArray;
        if (array == null) {
            synchronized (listeners) {
                array = listenerArray;
                if (array == null) {
                    array = listeners.toArray(new CoverageDatabaseListener[listeners.size()]);
                    listenerArray = array;
                }
            }
        }
        return array;
    }

    /**
     * Returns all listeners to notify about changes in database constent, or an empty array
     * if none.
     *
     * @return All registered listeners, or an empty array if none.
     *
     * @since 3.12
     */
    public CoverageDatabaseListener[] getListeners() {
        return getInternalListeners().clone();
    }

    /**
     * Notifies every listener that a value is about to change, or have already changed.
     * The method to be invoked is determined from the type of the {@code value} argument,
     * which can be {@link String} (for layers) or {@link NewGridCoverageReference}.
     *
     * @param  isBefore       {@code true} if the event is invoked before the change,
     *                        or {@code false} if the event occurs after the change.
     * @param  numEntryChange Number of entries added, or a negative number if entries removed.
     * @param  value          The entry which is added or removed.
     * @throws DatabaseVetoException if {@code isBefore} is {@code true} and a listener vetoed
     *         against the change.
     */
    final void fireChange(final boolean isBefore, final int numEntryChange, final Object value)
            throws DatabaseVetoException
    {
        final CoverageDatabaseListener[] listeners = getInternalListeners();
        if (listeners.length != 0) {
            final CoverageDatabaseEvent event = new CoverageDatabaseEvent(this, isBefore, numEntryChange);
            for (final CoverageDatabaseListener listener : listeners) {
                try {
                    if (value instanceof NewGridCoverageReference) {
                        listener.coverageAdding(event, (NewGridCoverageReference) value);
                    } else {
                        listener.layerListChange(event, (String) value);
                    }
                } catch (DatabaseVetoException veto) {
                    if (isBefore) {
                        throw veto;
                    }
                    final String method;
                    if (value instanceof NewGridCoverageReference) {
                        method = "coverageAdding";
                    } else {
                        method = "layerListChange";
                    }
                    final LogRecord record = new LogRecord(Level.WARNING,
                            Errors.getResources(getLocale()).getString(Errors.Keys.VETO_TOO_LATE));
                    record.setSourceClassName(CoverageDatabaseListener.class.getName());
                    record.setSourceMethodName(method);
                    record.setThrown(veto);
                    database.getLogger().log(record);
                }
            }
        }
    }

    /**
     * Returns the locale used for formatting logging and error messages.
     *
     * @return The locale, or {@code null} for the {@linkplain Locale#getDefault() default} locale.
     */
    @Override
    public Locale getLocale() {
        return database.getLocale();
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

    /*
     * No need to override finalize(), because ThreadPoolExecutor already
     * has a finalize() method which invoke its shutdown() method.
     */
}
