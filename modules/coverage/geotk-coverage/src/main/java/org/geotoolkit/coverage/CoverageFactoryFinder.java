/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage;

import java.util.Set;
import java.util.Arrays;
import java.util.Iterator;

import org.opengis.coverage.processing.GridCoverageProcessor;

import org.geotoolkit.lang.Static;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.FactoryRegistry;
import org.geotoolkit.factory.DynamicFactoryRegistry;
import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.apache.sis.internal.util.LazySet;


/**
 * Defines static methods used to access the application's default
 * {@linkplain GridCoverageFactory factory} implementation.
 * <p>
 * <strong>WARNING: This class is temporary</STRONG>. The methods in this class should move
 * to {@link org.geotoolkit.factory.FactoryFinder}. We can't move them now because we have not
 * yet defined an interface abstract enough for {@code GridCoverageFactory}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.02
 *
 * @since 2.4
 * @module
 *
 * @deprecated Deprecated together with {@link GridCoverageFactory}.
 * Will wait for a GeoAPI interface before to provide a new factory finder.
 */
@Deprecated
public final class CoverageFactoryFinder extends Static {
    /**
     * The service registry for this manager.
     * Will be initialized only when first needed.
     */
    private static FactoryRegistry registry;

    /**
     * Do not allows any instantiation of this class.
     */
    private CoverageFactoryFinder() {
        // singleton
    }

    /**
     * Returns new hints that combine user supplied hints with the default hints.
     * If a hint is specified in both user and default hints, then user hints have
     * precedence.
     * <p>
     * In a previous GeoTools version, a somewhat convolved lookup was performed here.
     * Now that default hints are filled right at {@link Hints} creation time, this
     * method just needs to ensure that the given hints are not-null.
     *
     * @param  hints The user hints, or {@code null} for the default ones.
     * @return The hints to use (never {@code null}).
     */
    static Hints mergeSystemHints(Hints hints) {
        if (hints == null) {
            hints = new Hints();
        }
        return hints;
    }

    /**
     * Returns the service registry. The registry will be created the first
     * time this method is invoked.
     */
    private static FactoryRegistry getServiceRegistry() {
        assert Thread.holdsLock(CoverageFactoryFinder.class);
        if (registry == null) {
            registry = new DynamicFactoryRegistry(Arrays.asList(new Class<?>[] {
                GridCoverageFactory.class,
                GridCoverageProcessor.class
            }));
        }
        return registry;
    }

    /**
     * Returns all providers of the specified category.
     *
     * @param  category The factory category.
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @param  key The hint key to use for searching an implementation.
     * @return Set of available factory implementations.
     */
    private static <T> Set<T> getFactories(final Class<T> category, Hints hints, final Hints.ClassKey key) {
        hints = mergeSystemHints(hints);
        final Iterator<T> iterator;
        synchronized (FactoryFinder.class) {
            iterator = getServiceRegistry().getServiceProviders(category, null, hints, key);
        }
        return new LazySet<>(iterator);
    }

    /**
     * Returns a provider of the specified category.
     *
     * @param  category The factory category.
     * @param  hints An optional map of hints, or {@code null} for the default ones.
     * @param  key The hint key to use for searching an implementation.
     * @return The first factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         specified interface.
     */
    private static <T> T getFactory(final Class<T> category, Hints hints, final Hints.ClassKey key)
            throws FactoryRegistryException
    {
        hints = mergeSystemHints(hints);
        synchronized (FactoryFinder.class) {
            return getServiceRegistry().getServiceProvider(category, null, hints, key);
        }
    }

    /**
     * Returns the first implementation of {@code GridCoverageFactory} matching the specified hints.
     * If no implementation matches, a new one is created if possible or an exception is thrown
     * otherwise.
     *
     * @param  hints An optional map of hints, or {@code null} if none.
     * @return The first grid coverage factory that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@code GridCoverageFactory} interface.
     *
     * @see Hints#DEFAULT_COORDINATE_REFERENCE_SYSTEM
     * @see Hints#TILE_ENCODING
     */
    public static synchronized GridCoverageFactory getGridCoverageFactory(Hints hints)
            throws FactoryRegistryException
    {
        return getFactory(GridCoverageFactory.class, hints, null);
    }

    /**
     * Returns a set of all available implementations for the {@code GridCoverageFactory} interface.
     *
     * @param  hints An optional map of hints, or {@code null} if none.
     * @return Set of available grid coverage factory implementations.
     *
     * @since 2.4
     */
    public static synchronized Set<GridCoverageFactory> getGridCoverageFactories(Hints hints) {
        return getFactories(GridCoverageFactory.class, hints, null);
    }

    /**
     * Returns the first implementation of {@code CoverageProcessor} matching the specified hints.
     * If no implementation matches, a new one is created if possible or an exception is thrown
     * otherwise.
     *
     * @param  hints An optional map of hints, or {@code null} if none.
     * @return The first coverage processor that matches the supplied hints.
     * @throws FactoryRegistryException if no implementation was found or can be created for the
     *         {@code CoverageProcessor} interface.
     *
     * @see Hints#GRID_COVERAGE_PROCESSOR
     *
     * @since 3.00
     */
    public static synchronized GridCoverageProcessor getCoverageProcessor(Hints hints)
            throws FactoryRegistryException
    {
        return getFactory(GridCoverageProcessor.class, hints, Hints.GRID_COVERAGE_PROCESSOR);
    }

    /**
     * Returns a set of all available implementations for the {@code CoverageProcessor} interface.
     *
     * @param  hints An optional map of hints, or {@code null} if none.
     * @return Set of available coverage processor implementations.
     *
     * @since 3.00
     */
    public static synchronized Set<GridCoverageProcessor> getCoverageProcessors(Hints hints) {
        return getFactories(GridCoverageProcessor.class, hints, Hints.GRID_COVERAGE_PROCESSOR);
    }

    /**
     * Scans for factory plug-ins on the application class path. This method is
     * needed because the application class path can theoretically change, or
     * additional plug-ins may become available. Rather than re-scanning the
     * classpath on every invocation of the API, the class path is scanned
     * automatically only on the first invocation. Clients can call this
     * method to prompt a re-scan. Thus this method need only be invoked by
     * sophisticated applications which dynamically make new plug-ins
     * available at runtime.
     */
    public static synchronized void scanForPlugins() {
        if (registry != null) {
            registry.scanForPlugins();
        }
    }
}
