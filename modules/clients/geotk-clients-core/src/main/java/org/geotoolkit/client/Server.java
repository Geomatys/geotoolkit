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
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.storage.StorageListener;
import org.opengis.parameter.ParameterValueGroup;


/**
 * Default interface for all server-side classes.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface Server {

    /**
     * Get the parameters used to initialize this source from it's factory.
     *
     * @return source configuration parameters
     */
    ParameterValueGroup getConfiguration();

    /**
     * Get the factory which created this source.
     *
     * @return this source original factory
     */
    ServerFactory getFactory();

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
     * @param key
     * @param value
     */
    void setUserProperty(String key,Object value);

    /**
     * Get a stored value knowing the key.
     * @param key
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
    void addStorageListener(StorageListener listener);

    /**
     * Remove a storage listener
     * @param listener to remove
     */
    void removeStorageListener(StorageListener listener);
    
}
