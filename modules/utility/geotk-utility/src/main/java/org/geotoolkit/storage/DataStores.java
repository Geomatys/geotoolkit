/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2018, Geomatys
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.parameter.Parameters;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
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
     * Do not allow instantiation of this class.
     */
    private DataStores() {
    }

    /**
     * List all resources in given resource.
     *
     * @param root Root resource to explore
     * @param includeRoot include the root in the stream
     * @return Collection of all resources
     * @throws DataStoreException
     */
    public static Collection<? extends Resource> flatten(Resource root, boolean includeRoot) throws DataStoreException {
        if (root instanceof Aggregate) {
            final List<Resource> list = new ArrayList<>();
            if (includeRoot) list.add(root);
            list(root,list);
            return list;
        } else if (includeRoot) {
            return Collections.singleton(root);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    private static void list(Resource resource, Collection<Resource> list) throws DataStoreException {
        if (resource instanceof Aggregate) {
            final Aggregate ds = (Aggregate) resource;
            for (Resource rs : ds.components()) {
                list.add(rs);
                list(rs,list);
            }
        }
    }

    public static ResourceType[] getResourceTypes(DataStoreProvider provider) {
        final StoreMetadataExt meta = provider.getClass().getAnnotation(StoreMetadataExt.class);
        if (meta == null) return new ResourceType[0];
        return meta.resourceTypes();
    }

    /**
     * Returns the set of all factories, optionally filtered by type.
     * This method ensures also that the iterator backing the set is properly synchronized.
     * <p>
     * Note that the iterator doesn't need to be thread-safe; this is the accesses to the
     * underlying {@linkplain #loader}, directly or indirectly through its iterator, which
     * need to be thread-safe.
     *
     * @param  <T>  The type of factories to be returned.
     * @param  type The type of factories to be returned, or {@code null} for all kind of factories.
     * @return The set of factories for the given conditions.
     */
    public static <T> Set<T> getAllFactories(final Class<T> type) {
        Stream<DataStoreProvider> stream = org.apache.sis.storage.DataStores.providers().stream();
        if (type != null) stream = stream.filter(type::isInstance);
        return (Set)stream.collect(Collectors.toSet());
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
    @Deprecated
    public static synchronized DataStoreFactory getFactoryById(final String identifier) {
        for (final DataStoreFactory factory : getAllFactories(DataStoreFactory.class)) {
            for (String name : IdentifiedObjects.getNames(factory.getOpenParameters(),null)) {
                if (name.equals(identifier)) {
                    return factory;
                }
            }
        }
        return null;
    }

    /**
     * Returns a provider having an {@linkplain DataStoreProvider#getOpenParameters() identification}
     * equals (ignoring case) to the given string. If more than one provider is found, then this
     * method selects an arbitrary one. If no factory is found, then this method returns
     * {@code null}.
     *
     * @param  identifier The identifier of the factory to find.
     * @return A factory for the given identifier, or {@code null} if none.
     */
    public static synchronized DataStoreProvider getProviderById(final String identifier) {
        for (final DataStoreProvider factory : getAllFactories(DataStoreProvider.class)) {
            for (String name : IdentifiedObjects.getNames(factory.getOpenParameters(),null)) {
                if (name.equals(identifier)) {
                    return factory;
                }
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
        return open((ParameterValueGroup)null, parameters);
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
        for (final DataStoreFactory factory : getAllFactories(DataStoreFactory.class)) {
            try {
                if ((parameters != null) ? factory.canProcess(parameters) : canProcess(factory,asMap)) {
                    return (DataStore) ((parameters != null) ? factory.open(parameters) : open(factory,asMap));
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

    public static DataStore open(DataStoreFactory factory, Map<String, ? extends Serializable> params) throws DataStoreException {
        final ParameterValueGroup prm;
        try {
            prm = Parameters.toParameter(
                    forceIdentifier(factory, params),
                    factory.getOpenParameters());
        } catch (IllegalArgumentException ex) {
            throw new DataStoreException(ex);
        }
        return open(prm);
    }

    /**
     * @param params
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     * @see DataStoreFactory#create(org.opengis.parameter.ParameterValueGroup)
     */
    public static DataStore create(DataStoreFactory factory, Map<String, ? extends Serializable> params) throws DataStoreException {
        final ParameterValueGroup prm;
        try {
            prm = Parameters.toParameter(DataStores.forceIdentifier(factory, params), factory.getOpenParameters());
        } catch(IllegalArgumentException ex) {
            throw new DataStoreException(ex);
        }
        return factory.create(prm);
    }

    /**
     * Test to see if this factory is suitable for processing the data pointed
     * to by the params map.
     *
     * <p>
     * If this data source requires a number of parameters then this method
     * should check that they are all present and that they are all valid. If
     * the data source is a file reading data source then the extensions or
     * mime types of any files specified should be checked. For example, a
     * Shapefile data source should check that the url param ends with shp,
     * such tests should be case insensitive.
     * </p>
     *
     * @param params The full set of information needed to construct a live
     *        data source.
     *
     * @return boolean true if and only if this factory can process the resource
     *         indicated by the param set and all the required params are
     *         present.
     */
    private static boolean canProcess(DataStoreFactory factory, Map<String, ? extends Serializable> params) {
        params = forceIdentifier(factory, params);

        //ensure it's the valid identifier
        final Object id = params.get(DataStoreFactory.IDENTIFIER.getName().getCode());
        try{
            final String expectedId = ((ParameterDescriptor<String>)factory.getOpenParameters()
                .descriptor(DataStoreFactory.IDENTIFIER.getName().getCode())).getDefaultValue();
            if(!expectedId.equals(id)){
                return false;
            }
        }catch(ParameterNotFoundException ex){
            //this feature store factory does not declare a identifier id
        }

        final ParameterValueGroup prm = Parameters.toParameter(params, factory.getOpenParameters());
        if (prm == null) {
            return false;
        }
        try {
            return factory.canProcess(prm);
        } catch (InvalidParameterValueException ex) {
            return false;
        }
    }

    /**
     * Set the identifier parameter in the map if not present.
     */
    static final Map<String,Serializable> forceIdentifier(DataStoreFactory factory, Map params){

        if (!params.containsKey(DataStoreFactory.IDENTIFIER.getName().getCode())) {
            //identifier is not specified, force it
            final ParameterDescriptorGroup desc = factory.getOpenParameters();
            params = new HashMap<String, Serializable>(params);
            final Object value = ((ParameterDescriptor)desc.descriptor(DataStoreFactory.IDENTIFIER.getName().getCode())).getDefaultValue();
            params.put(DataStoreFactory.IDENTIFIER.getName().getCode(), (Serializable)value);
        }
        return params;
    }

}
