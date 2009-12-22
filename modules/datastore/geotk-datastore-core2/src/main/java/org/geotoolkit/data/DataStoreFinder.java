/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.spi.ServiceRegistry;

import org.geotoolkit.factory.DynamicFactoryRegistry;
import org.geotoolkit.factory.FactoryRegistry;
import org.geotoolkit.internal.LazySet;

/**
 * Enable programs to find all available datastore implementations.
 *
 * <p>
 * In order to be located by this finder datasources must provide an
 * implementation of the {@link DataStoreFactory} interface.
 * </p>
 *
 * <p>
 * In addition to implementing this interface datasouces should have a services
 * file:<br/><code>META-INF/services/org.geotoolkit.data.DataStoreFactory</code>
 * </p>
 *
 * <p>
 * The file should contain a single line which gives the full name of the
 * implementing class.
 * </p>
 *
 * <p>
 * Example:<br/><code>org.geotoolkit.data.mytype.MyTypeDataStoreFactory</code>
 * </p>
 * @module pending
 */
public final class DataStoreFinder{

    /** The logger for the datastore module. */
    private static final Logger LOGGER = org.geotoolkit.util.logging.Logging.getLogger("org.geotoolkit.data");
    /**
     * The service registry for this manager. Will be initialized only when
     * first needed.
     */
    private static FactoryRegistry registry;

    private DataStoreFinder() {}

    /**
     * @see DataStoreFinder#getDataStore(java.util.Map) 
     * Get a datastore wich has a single parameter. 
     * This is a utility method that will redirect to getDataStore(java.util.Map)
     */
    public static synchronized DataStore getDataStore(
            String key, Serializable value) throws DataStoreException{
        return getDataStore(Collections.singletonMap(key, value));
    }

    /**
     * Checks each available datasource implementation in turn and returns the
     * first one which claims to support the resource identified by the params
     * object.
     *
     * @param params
     *            A Map object which contains a defenition of the resource to
     *            connect to. for file based resources the property 'url' should
     *            be set within this Map.
     *
     * @return The first datasource which claims to process the required
     *         resource, returns null if none can be found.
     *
     * @throws IOException
     *             If a suitable loader can be found, but it can not be attached
     *             to the specified resource without errors.
     */
    public static synchronized DataStore getDataStore(
            Map<String, Serializable> params) throws DataStoreException {
        final Iterator<DataStoreFactory> ps = getAvailableDataStores();


        DataStoreException canProcessButNotAvailable = null;
        while (ps.hasNext()) {
            final DataStoreFactory fac = (DataStoreFactory) ps.next();
            boolean canProcess = false;
            try {
                canProcess = fac.canProcess(params);
            } catch (Throwable t) {
                LOGGER.log(Level.WARNING, "Problem asking " + fac.getDisplayName() + " if it can process request:" + t, t);
                // Protect against DataStores that don't carefully code
                // canProcess
                continue;
            }
            if (canProcess) {
                boolean isAvailable = false;
                try {
                    isAvailable = fac.availability().pass();
                } catch (Throwable t) {
                    LOGGER.log(Level.WARNING, "Difficulity checking if " + fac.getDisplayName() + " is available:" + t, t);
                    // Protect against DataStores that don't carefully code
                    // isAvailable
                    continue;
                }
                if (isAvailable) {
                    try {
                        return fac.createDataStore(params);
                    } catch (DataStoreException couldNotConnect) {
                        canProcessButNotAvailable = couldNotConnect;
                        LOGGER.log(Level.WARNING, fac.getDisplayName() + " should be used, but could not connect", couldNotConnect);
                    }
                } else {
                    canProcessButNotAvailable = new DataStoreException(
                            fac.getDisplayName() + " should be used, but is not availble. Have you installed the required drivers or jar files?");
                    LOGGER.log(Level.WARNING, fac.getDisplayName() + " should be used, but is not availble", canProcessButNotAvailable);
                }
            }
        }
        if (canProcessButNotAvailable != null) {
            throw canProcessButNotAvailable;
        }
        return null;
    }

    /**
     * Finds all implemtaions of DataAccessFactory which have registered using
     * the services mechanism, regardless weather it has the appropriate
     * libraries on the classpath.
     *
     * @return An iterator over all discovered datastores which have registered
     *         factories
     */
    public static synchronized Iterator<DataStoreFactory> getAllDataStores() {
        return getAllDataStores(null);
    }

    /**
     * @see DataStoreFinder#getAllDataStores() 
     * Get all datastores of a given class.
     */
    public static synchronized <T extends DataStoreFactory> Iterator<T> getAllDataStores(final Class<T> type){
        final FactoryRegistry serviceRegistry = getServiceRegistry();

        ServiceRegistry.Filter filter = null;
        if(type != null){
            filter = new ServiceRegistry.Filter() {
                @Override
                public boolean filter(Object provider) {
                    return type.isInstance(provider);
                }
            };
        }

        final Iterator<DataStoreFactory> allDataAccess = serviceRegistry.getServiceProviders(DataStoreFactory.class, filter, null, null);

        return new LazySet(allDataAccess).iterator();
    }

    /**
     * Finds all implementations of DataStoreFactory which have registered using
     * the services mechanism, and that have the appropriate libraries on the
     * classpath.
     *
     * @return An iterator over all discovered datastores which have registered
     *         factories, and whose available method returns true.
     */
    public static synchronized Iterator<DataStoreFactory> getAvailableDataStores() {
        return getAvailableDataStores(null);
    }

    /**
     * @see DataStoreFinder#getAvailableDataStores()
     * Get all available datastores of a given class.
     */
    public static synchronized <T extends DataStoreFactory> Iterator<T> getAvailableDataStores(final Class<T> type) {
        final Iterator<T> allStores = getAllDataStores(type);

        final Set<T> availableStores = new HashSet<T>();

        while (allStores.hasNext()) {
            final T dsFactory = allStores.next();
            if (dsFactory.availability().pass()) {
                availableStores.add(dsFactory);
            }
        }
        
        return availableStores.iterator();
    }

    /**
     * Returns the service registry. The registry will be created the first time
     * this method is invoked.
     */
    private static FactoryRegistry getServiceRegistry() {
        assert Thread.holdsLock(DataStoreFinder.class);
        if (registry == null) {
            registry = new DynamicFactoryRegistry(DataStoreFactory.class);
        }
        return registry;
    }

    /**
     * Scans for factory plug-ins on the application class path. This method is
     * needed because the application class path can theoretically change, or
     * additional plug-ins may become available. Rather than re-scanning the
     * classpath on every invocation of the API, the class path is scanned
     * automatically only on the first invocation. Clients can call this method
     * to prompt a re-scan. Thus this method need only be invoked by
     * sophisticated applications which dynamically make new plug-ins available
     * at runtime.
     */
    public static synchronized void scanForPlugins() {
        getServiceRegistry().scanForPlugins();
    }

}
