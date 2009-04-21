/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2009, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.coverage.processing.GridCoverageProcessor;

import org.geotoolkit.lang.Static;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.FactoryRegistry;
import org.geotoolkit.factory.DynamicFactoryRegistry;
import org.geotoolkit.factory.FactoryRegistryException;
import org.geotoolkit.coverage.grid.GridCoverageFactory;
import org.geotoolkit.internal.LazySet;


/**
 * Defines static methods used to access the application's default
 * {@linkplain GridCoverageFactory factory} implementation.
 * <p>
 * <strong>WARNING: This class is temporary</STRONG>. The methods in this class should move
 * to {@link org.geotoolkit.factory.FactoryFinder}. We can't move them now because we have not
 * yet defined an interface abstract enough for {@code GridCoverageFactory}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.0
 *
 * @since 2.4
 * @module
 */
@Static
public final class CoverageFactoryFinder {
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
        if (hints == null) {
            hints = new Hints();
        }
        return getServiceRegistry().getServiceProvider(GridCoverageFactory.class, null, hints, null);
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
        if (hints == null) {
            hints = new Hints();
        }
        return new LazySet<GridCoverageFactory>(getServiceRegistry().getServiceProviders(
                GridCoverageFactory.class, null, hints));
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
     * @since 3.0
     */
    public static synchronized GridCoverageProcessor getCoverageProcessor(Hints hints)
            throws FactoryRegistryException
    {
        if (hints == null) {
            hints = new Hints();
        }
        return getServiceRegistry().getServiceProvider(GridCoverageProcessor.class,
                null, hints, Hints.GRID_COVERAGE_PROCESSOR);
    }

    /**
     * Returns a set of all available implementations for the {@code CoverageProcessor} interface.
     *
     * @param  hints An optional map of hints, or {@code null} if none.
     * @return Set of available coverage processor implementations.
     *
     * @since 3.0
     */
    public static synchronized Set<GridCoverageProcessor> getCoverageProcessors(Hints hints) {
        if (hints == null) {
            hints = new Hints();
        }
        return new LazySet<GridCoverageProcessor>(getServiceRegistry().getServiceProviders(
                GridCoverageProcessor.class, null, hints));
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
