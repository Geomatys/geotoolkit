/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.client;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.security.ClientSecurity;
import org.opengis.parameter.ParameterValueGroup;


/**
 * Default interface for all server-side classes.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface Client extends AutoCloseable {

    /**
     * Get the parameters used to initialize this source from it's factory.
     *
     * @return source configuration parameters
     */
    Optional<ParameterValueGroup> getOpenParameters();

    /**
     * Get the factory which created this source.
     *
     * @return this source original factory
     */
    DataStoreProvider getProvider();

    /**
     * @return the server url as an {@link URI}, or {@code null} il the uri syntax
     * is not respected.
     */
    URI getURI();

    /**
     * @return the server url as an {@link URL}.
     */
    URL getURL();

    /**
     * @return ClientSecurity used by this server. never null.
     */
    ClientSecurity getClientSecurity();

    /**
     * @return recommanded timeout value for this server.
     */
    int getTimeOutValue();

    /**
     * Store a value for this server in a hashmap using the given key.
     */
    void setUserProperty(String key,Object value);

    /**
     * Get a stored value knowing the key.
     * @return user property object , can be null
     */
    Object getUserProperty(String key);

    /**
     * @return map of all user properties.
     *          This is the live map.
     */
    Map<String,Object> getUserProperties();

    /**
     * Add a storage listener which will be notified when structure changes or
     * when data changes.
     * @param listener to add
     */
    <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener);

    /**
     * Remove a storage listener
     * @param listener to remove
     */
    <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener);

    @Override
    void close() throws DataStoreException;
}
