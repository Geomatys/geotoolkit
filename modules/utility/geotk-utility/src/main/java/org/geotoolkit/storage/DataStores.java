/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2016, Geomatys
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
package org.geotoolkit.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.parameter.ParameterValueGroup;


/**
 * Creates {@link DataStore} instances from a set of parameters.
 *
 * {@section Registration}
 * {@link DataStore} factories must implement the {@link DataStoreFactory} interface and declare their
 * fully qualified class name in a {@code META-INF/services/org.geotoolkit.storage.DataStoreFactory}
 * file. See the {@link ServiceLoader} javadoc for more information.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public final class DataStores extends Static {
    /**
     * The service loader. This loader and its iterator are not synchronized;
     * when doing an iteration, the iterator must be used inside synchronized blocks.
     */
    private static final ServiceLoader<DataStoreFactory> loader = ServiceLoader.load(DataStoreFactory.class);

    /**
     * Do not allow instantiation of this class.
     */
    private DataStores() {
    }

    /**
     * Returns the set of all factories, optionally filtered by type and availability.
     * This method ensures also that the iterator backing the set is properly synchronized.
     * <p>
     * Note that the iterator doesn't need to be thread-safe; this is the accesses to the
     * underlying {@linkplain #loader}, directly or indirectly through its iterator, which
     * need to be thread-safe.
     *
     * @param  <T>  The type of factories to be returned.
     * @param  type The type of factories to be returned, or {@code null} for all kind of factories.
     * @param  all  {@code true} for all factories, or {@code false} for only available factories.
     * @return The set of factories for the given conditions.
     */
    private static synchronized <T> Set<T> getFactories(final Class<T> type, final boolean all) {
        final Set<T> results = new HashSet<>();
        final Iterator<DataStoreFactory> factories = loader.iterator();

        while (factories.hasNext()) {
            final DataStoreFactory candidate = factories.next();
            if (type == null || type.isInstance(candidate)) {
                if (all || candidate.availability().pass()) {
                    results.add((T)candidate);
                }
            }
        }
        return results;
    }

    /**
     * Returns all factories of the given type, regardless of their
     * {@linkplain DataStoreFactory#availability() availability}.
     *
     * @param  <T>  The type of the factories to fetch.
     * @param  type The type of the factories to fetch, or {@code null} for fetching all of them.
     * @return The set of all factories of the given type.
     */
    public static <T> Set<T> getAllFactories(final Class<T> type) {
        return getFactories(type, true);
    }

    /**
     * Returns factories of the given type which are
     * {@linkplain DataStoreFactory#availability() available}.
     *
     * @param  <T>  The type of the factories to fetch.
     * @param  type The type of the factories to fetch, or {@code null} for fetching very types.
     * @return The set of available factories of the given type.
     */
    public static <T> Set<T> getAvailableFactories(final Class<T> type) {
        return getFactories(type, false);
    }

    /**
     * Returns a factory having an {@linkplain DataStoreFactory#getIdentification() identification}
     * equals (ignoring case) to the given string. If more than one factory is found, then this
     * method selects an arbitrary one. If no factory is found, then this method returns
     * {@code null}.
     *
     * @param  identifier The identifier of the factory to find.
     * @return A factory for the given identifier, or {@code null} if none.
     */
    public static synchronized DataStoreFactory getFactoryById(final String identifier) {
        for (final DataStoreFactory factory : loader) {
            if (Citations.identifierMatches(factory.getIdentification().getCitation(), identifier)) {
                return factory;
            }
        }
        return null;
    }

    /**
     * Creates a {@link DataStore} instance for the given map of parameter values. This method iterates
     * over all {@linkplain #getAvailableFactories(Class) available factories} until a factory
     * claiming to {@linkplain DataStoreFactory#canProcess(Map) be able to process} the given
     * parameters is found. This factory then {@linkplain DataStoreFactory#open(Map) open}
     * the data store.
     *
     * @param  parameters The configuration of the desired data store.
     * @return A data store created from the given parameters, or {@code null} if none.
     * @throws DataStoreException If a factory is found but can't open the data store.
     */
    public static DataStore open(final Map<String, Serializable> parameters) throws DataStoreException {
        ArgumentChecks.ensureNonNull("parameters", parameters);
        return open(null, parameters);
    }

    /**
     * Creates a {@link DataStore} instance for the given parameters group. This method iterates over
     * all {@linkplain #getAvailableFactories(Class) available factories} until a factory claiming
     * to {@linkplain DataStoreFactory#canProcess(ParameterValueGroup) be able to process} the given
     * parameters is found. This factory then {@linkplain DataStoreFactory#open(ParameterValueGroup)
     * open} the data store.
     *
     * @param  parameters The configuration of the desired data store.
     * @return A data store created from the given parameters, or {@code null} if none.
     * @throws DataStoreException If a factory is found but can't open the data store.
     */
    public static DataStore open(final ParameterValueGroup parameters) throws DataStoreException {
        ArgumentChecks.ensureNonNull("parameters", parameters);
        return open(parameters, null);
    }

    /**
     * Implementation of the public {@code open} method. Exactly one of the {@code parameters}
     * and {@code asMap} arguments shall be non-null.
     */
    private static synchronized DataStore open(final ParameterValueGroup parameters,
            final Map<String, Serializable> asMap) throws DataStoreException
    {
        CharSequence unavailable = null;
        Exception error = null;
        for (final DataStoreFactory factory : loader) {
            try {
                if ((parameters != null) ? factory.canProcess(parameters) : factory.canProcess(asMap)) {
                    if (factory.availability().pass()) {
                        return (DataStore) ((parameters != null) ? factory.open(parameters) : factory.open(asMap));
                    } else if (unavailable == null) {
                        unavailable = factory.getDisplayName();
                    }
                }
            } catch (Exception e) {
                // If an error occurs with a factory, we skip it and try another factory.
                if (error != null) {
                    error.addSuppressed(e);
                } else {
                    error = e;
                }
            }
        }
        if (unavailable != null) {
            throw new DataStoreException("The " + unavailable + " data store is not available. "
                    + "Are every required JAR files accessible on the classpath?");
        } else if (error instanceof DataStoreException) {
            throw (DataStoreException) error;
        } else if (error != null) {
            throw new DataStoreException("An error occurred while searching for a datastore", error);
        }
        return null;
    }


    /**
     * Send back a list of all nodes in a tree. Nodes are ordered by depth-first
     * encounter order.
     *
     * @param root Node to start flattening from. It will be included in result.
     * @return A list of all nodes under given root.
     * @throws NullPointerException If input node is null.
     */
    public static Stream<? extends org.apache.sis.storage.Resource> flatten(final org.apache.sis.storage.Resource root) throws DataStoreException {
        final List<org.apache.sis.storage.Resource> lst = new ArrayList<>();
        flatten(root, lst);
        return lst.stream();
    }

    private static void flatten(org.apache.sis.storage.Resource root, List<org.apache.sis.storage.Resource> lst) throws DataStoreException {
        lst.add(root);
        if (root instanceof Aggregate) {
            for (org.apache.sis.storage.Resource res : ((Aggregate) root).components()) {
                flatten(res, lst);
            }
        }
    }

    /**
     * Scans for factory plug-ins on the application class path. This method is needed because the
     * application class path can theoretically change, or additional plug-ins may become available.
     * Rather than re-scanning the classpath on every invocation of the API, the class path is scanned
     * automatically only on the first invocation. Clients can call this method to prompt a re-scan.
     * Thus this method need only be invoked by sophisticated applications which dynamically make
     * new plug-ins available at runtime.
     */
    public static synchronized void scanForPlugins() {
        loader.reload();
    }

}
