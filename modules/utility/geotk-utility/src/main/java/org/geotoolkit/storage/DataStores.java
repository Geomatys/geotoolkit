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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.lang.Static;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataStores.ResourceWalker.VisitOption;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;


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

    public static void walk(Resource root, ResourceWalker walker) throws DataStoreException {
        walkInternal(root, walker);
    }

    private static ResourceWalker.VisitOption walkInternal(Resource root, ResourceWalker walker) throws DataStoreException {
        final VisitOption opt = walker.visit(root);
        switch (opt) {
            case FINISH:
                return VisitOption.FINISH;
            case SKIP:
                return VisitOption.CONTINUE;
            case CONTINUE:
                if (root instanceof Aggregate) {
                    Aggregate agg = (Aggregate) root;
                    try {
                        for (Resource r : agg.components()) {
                            final VisitOption copt = walkInternal(r, walker);
                            if (copt == VisitOption.FINISH) return VisitOption.FINISH;
                        }
                    } catch (DataStoreException ex) {
                        ResourceWalker.ErrorOption erropt = walker.errorOccured(agg, ex);
                        if (null != erropt) switch (erropt) {
                            case SKIP:   return VisitOption.CONTINUE;
                            case FINISH: return VisitOption.FINISH;
                            case ERROR:  throw ex;
                            default: throw new IllegalArgumentException("Unexpected option "+ erropt);
                        }
                    }
                }
                return VisitOption.CONTINUE;
            default:
                throw new IllegalArgumentException("Unexpected option "+ opt);
        }
    }

    /**
     * List all resources in given resource.
     *
     * @param root Root resource to explore
     * @param includeRoot include the root in the stream
     * @return Collection of all resources
     */
    public static Collection<? extends Resource> flatten(Resource root, boolean includeRoot) throws DataStoreException {
        return flatten(root, includeRoot, Resource.class);
    }

    /**
     * List all resources in given resource.
     *
     * @param root Root resource to explore
     * @param includeRoot include the root in the stream
     * @param resourceClass class of searched resources
     * @return Collection of all resources
     */
    public static <T extends Resource> Collection<T> flatten(Resource root, boolean includeRoot, Class<T> resourceClass) throws DataStoreException {
        return flatten(root, includeRoot, resourceClass, false);
    }

    /**
     * List all resources in given resource.
     *
     * @param root Root resource to explore
     * @param includeRoot include the root in the stream
     * @param resourceClass class of searched resources
     * @return Collection of all resources
     */
    public static <T extends Resource> Collection<T> flatten(Resource root, boolean includeRoot, Class<T> resourceClass, boolean ignoreErrors) throws DataStoreException {
        ArgumentChecks.ensureNonNull("resourceClass", resourceClass);   // null not allowed because unsafe.
        if (root instanceof Aggregate) {
            final List<T> list = new ArrayList<>();
            if (includeRoot && resourceClass.isInstance(root)) list.add((T) root);
            list(root, list, resourceClass, ignoreErrors);
            return list;
        } else if (includeRoot && resourceClass.isInstance(root)) {
            return Collections.singleton((T) root);
        } else {
            return Collections.emptyList();
        }
    }

    private static <T extends Resource> void list(Resource resource, Collection<T> list, Class<T> resourceClass, boolean ignoreErrors) throws DataStoreException {
        if (resource instanceof Aggregate) {
            final Aggregate ds = (Aggregate) resource;
            try {
                for (Resource rs : ds.components()) {
                    if (resourceClass.isInstance(rs)) list.add((T) rs);
                    list(rs, list, resourceClass, ignoreErrors);
                }
            } catch (DataStoreException ex) {
                if (!ignoreErrors) {
                    throw ex;
                }
            }
        }
    }


    /**
     * Get a collection of all available names of a specific resource type.
     *
     * @return Set<GenericName> , never null, but can be empty.
     * @throws DataStoreException
     */
    public static final <T extends Resource> Set<GenericName> getNames(Resource root, boolean includeRoot, Class<T> resourceClass) throws DataStoreException {
        final Set<GenericName> names = new HashSet<>();
        for (T t : flatten(root, includeRoot, resourceClass)) {
            t.getIdentifier().ifPresent((name) -> names.add(name));
        }
        return names;
    }

    /**
     * Extract the {@link ResourceType} supported by the {@link DataStoreProvider}.
     *
     * @return supported resource types, never null, can be empty
     */
    public static ResourceType[] getResourceTypes(DataStoreProvider provider) {
        final StoreMetadataExt meta = provider.getClass().getAnnotation(StoreMetadataExt.class);
        if (meta == null) return new ResourceType[0];
        return meta.resourceTypes();
    }

    /**
     * Returns the set of all providers, optionally filtered by class and resource types.
     *
     * @param  <T>  The type of provider to be returned.
     * @param  clazz The type of factories to be returned, or {@code null} for all kind of factories.
     * @param types types of resources that must be supported by the provider, none for all
     * @return The set of factories for the given conditions.
     *
     * @deprecated the implementation of this method is unsafe.
     */
    @Deprecated
    public static <T> Set<T> getProviders(final Class<T> clazz, ResourceType ... types) {
        final Set<T> results = new HashSet<>();
        loop:
        for (DataStoreProvider p : org.apache.sis.storage.DataStores.providers()) {
            if (clazz != null && !clazz.isInstance(p)) continue;
            if (types != null && types.length > 0) {
                final ResourceType[] supportedTypes = getResourceTypes(p);
                for (ResourceType type : types) {
                    if (ArraysExt.contains(supportedTypes, type)) {
                        results.add((T) p);
                        continue loop;
                    }
                }
            } else {
                results.add((T) p);
            }
        }
        return results;
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
        for (final DataStoreFactory factory : getProviders(DataStoreFactory.class)) {
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
        for (final DataStoreProvider factory : org.apache.sis.storage.DataStores.providers()) {
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
    public static org.apache.sis.storage.DataStore open(final Map<String, Serializable> parameters) throws DataStoreException {
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
    public static org.apache.sis.storage.DataStore open(final ParameterValueGroup parameters) throws DataStoreException {
        ArgumentChecks.ensureNonNull("parameters", parameters);
        return open(parameters, null);
    }

    /**
     * Implementation of the public {@code open} method. Exactly one of the {@code parameters}
     * and {@code asMap} arguments shall be non-null.
     */
    private static synchronized org.apache.sis.storage.DataStore open(final ParameterValueGroup parameters,
            final Map<String, Serializable> asMap) throws DataStoreException
    {
        CharSequence unavailable = null;
        Exception error = null;
        final List<DataStoreProvider> newProviders = new ArrayList();
        for (DataStoreProvider provider : org.apache.sis.storage.DataStores.providers()) {

            if (provider instanceof DataStoreFactory) {
                final DataStoreFactory factory = (DataStoreFactory) provider;
                try {
                    if ((parameters != null) ? factory.canProcess(parameters) : canProcess(factory,asMap)) {
                        return ((parameters != null) ? factory.open(parameters) : open(factory,asMap));
                    }
                } catch (Exception e) {
                    // If an error occurs with a factory, we skip it and try another factory.
                    if (error != null) {
                        error.addSuppressed(e);
                    } else {
                        error = e;
                    }
                }
            } else {
                newProviders.add(provider);
            }
        }

        for (DataStoreProvider provider : newProviders) {
            if (parameters != null && provider.getOpenParameters().getName().equals(parameters.getDescriptor().getName())) {
                return provider.open(parameters);
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

    public static org.apache.sis.storage.DataStore open(DataStoreProvider factory, Map<String, ? extends Serializable> params) throws DataStoreException {
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
     * @see DataStoreFactory#create(org.opengis.parameter.ParameterValueGroup)
     */
    public static org.apache.sis.storage.DataStore create(DataStoreProvider factory, Map<String, ? extends Serializable> params) throws DataStoreException {
        final ParameterValueGroup prm;
        try {
            prm = Parameters.toParameter(DataStores.forceIdentifier(factory, params), factory.getOpenParameters());
        } catch(IllegalArgumentException ex) {
            throw new DataStoreException(ex);
        }
        if (factory instanceof DataStoreFactory) {
            return ((DataStoreFactory) factory).create(prm);
        } else {
            return factory.open(prm);
        }
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
            if (!expectedId.equals(id)) {
                return false;
            }
        } catch(ParameterNotFoundException ex) {
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
    static final Map<String,Serializable> forceIdentifier(DataStoreProvider factory, Map params){

        if (!params.containsKey(DataStoreFactory.IDENTIFIER.getName().getCode())) {
            //identifier is not specified, force it
            final ParameterDescriptorGroup desc = factory.getOpenParameters();
            params = new HashMap<String, Serializable>(params);
            final Object value = ((ParameterDescriptor)desc.descriptor(DataStoreFactory.IDENTIFIER.getName().getCode())).getDefaultValue();
            params.put(DataStoreFactory.IDENTIFIER.getName().getCode(), (Serializable)value);
        }
        return params;
    }

    public static interface ResourceWalker {

        public static enum VisitOption {
            CONTINUE,
            SKIP,
            FINISH;
        }

        public static enum ErrorOption {
            SKIP,
            FINISH,
            ERROR;
        }

        VisitOption visit(Resource resource);

        default ErrorOption errorOccured(Aggregate aggregate, Exception ex) {
            return ErrorOption.ERROR;
        }
    }
}
