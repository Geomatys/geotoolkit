/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data.wfs;

import java.io.IOException;

import java.net.URI;
import java.util.WeakHashMap;
import org.geotoolkit.data.AbstractDataStoreFactory;
import org.geotoolkit.data.DataStore;

import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WFSDataStoreFactory extends AbstractDataStoreFactory{

    /**
     * Mandatory - server uri
     */
    public static final GeneralParameterDescriptor SERVER_URI =
            new DefaultParameterDescriptor("server uri","server uri",URI.class,null,true);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new DefaultParameterDescriptorGroup("WFSParameters",
                new GeneralParameterDescriptor[]{SERVER_URI});

    private static final WeakHashMap<URI,WFSDataStore> STORES = new WeakHashMap<URI, WFSDataStore>();

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDescription() {
        return "OGC Web Feature Service datastore factory";
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ParameterDescriptorGroup getParametersDescriptor() {
        return PARAMETERS_DESCRIPTOR;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized WFSDataStore createDataStore(ParameterValueGroup params) throws IOException {
        final URI serverURI = (URI) params.parameter(SERVER_URI.getName().getCode()).getValue();

        WFSDataStore store = STORES.get(serverURI);

        if(store == null){
            store = new WFSDataStore(serverURI);
            STORES.put(serverURI, store);
        }

        return store;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public DataStore<SimpleFeatureType, SimpleFeature> createNewDataStore(ParameterValueGroup params) throws IOException {
        throw new IOException("Can not create any new WFS DataStore");
    }

}
