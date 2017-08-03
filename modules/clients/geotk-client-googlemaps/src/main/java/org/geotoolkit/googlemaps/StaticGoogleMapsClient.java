/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.googlemaps;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.sis.parameter.Parameters;

import org.geotoolkit.client.AbstractCoverageClient;
import org.geotoolkit.client.AbstractClientFactory;
import org.geotoolkit.storage.coverage.CoverageType;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.security.ClientSecurity;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.client.Client;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.DefaultDataSet;
import org.geotoolkit.storage.Resource;
import org.opengis.util.GenericName;
import org.opengis.parameter.ParameterValueGroup;
import org.geotoolkit.storage.coverage.CoverageResource;

/**
 * Client for google static maps.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class StaticGoogleMapsClient extends AbstractCoverageClient implements Client {

    public static final URL DEFAULT_GOOGLE_STATIC_MAPS;

    static {
        try {
            DEFAULT_GOOGLE_STATIC_MAPS = new URL("http://maps.google.com/maps/api/staticmap");
        } catch (MalformedURLException ex) {
            //will not happen
            throw new RuntimeException(ex.getLocalizedMessage(),ex);
        }
    }

    private final DefaultDataSet rootNode = new DefaultDataSet(NamesExt.create("root"));

    /**
     * Builds a google maps server with the default google server address.
     */
    public StaticGoogleMapsClient() throws DataStoreException {
        this(DEFAULT_GOOGLE_STATIC_MAPS,null);
    }

    /**
     * Builds a google maps server with the given server url.
     *
     * @param serverURL The server base url.
     * @param key, account key.
     */
    public StaticGoogleMapsClient(final URL serverURL, final String key) throws DataStoreException {
        this(serverURL,key,null,false);
    }

    public StaticGoogleMapsClient(final URL serverURL, final String key,
            final ClientSecurity security, boolean cacheImage) throws DataStoreException {
        this(toParameters(serverURL, key, security, cacheImage));
    }

    public StaticGoogleMapsClient(ParameterValueGroup params) throws DataStoreException {
        super(params);

        final boolean cache = getCacheImage();
        final GoogleCoverageResource ref1 = new GoogleCoverageResource(this,NamesExt.valueOf("{http://google.com}"+GetMapRequest.TYPE_HYBRID),cache);
        final GoogleCoverageResource ref2 = new GoogleCoverageResource(this,NamesExt.valueOf("{http://google.com}"+GetMapRequest.TYPE_ROADMAP),cache);
        final GoogleCoverageResource ref3 = new GoogleCoverageResource(this,NamesExt.valueOf("{http://google.com}"+GetMapRequest.TYPE_SATELLITE),cache);
        final GoogleCoverageResource ref4 = new GoogleCoverageResource(this,NamesExt.valueOf("{http://google.com}"+GetMapRequest.TYPE_TERRAIN),cache);

        rootNode.addResource(ref1);
        rootNode.addResource(ref2);
        rootNode.addResource(ref3);
        rootNode.addResource(ref4);
    }

    private static ParameterValueGroup toParameters(final URL serverURL, final String key,
            final ClientSecurity security, boolean cacheImage){
        final Parameters params = create(StaticGoogleClientFactory.PARAMETERS, serverURL, security);
        params.getOrCreate(StaticGoogleClientFactory.IMAGE_CACHE).setValue(cacheImage);
        return params;
    }


    @Override
    public StaticGoogleClientFactory getFactory() {
        return (StaticGoogleClientFactory) DataStores.getFactoryById(StaticGoogleClientFactory.NAME);
    }

    @Override
    public Resource getRootResource() throws DataStoreException {
        return rootNode;
    }

    public boolean getCacheImage(){
        return parameters.getValue(AbstractClientFactory.IMAGE_CACHE);
    }

    @Override
    public void close() {
    }

    @Override
    public CoverageResource create(GenericName name) throws DataStoreException {
        throw new DataStoreException("Can not create new coverage.");
    }

    @Override
    public void delete(GenericName name) throws DataStoreException {
        throw new DataStoreException("Can not create new coverage.");
    }

    @Override
    public CoverageType getType() {
        return CoverageType.PYRAMID;
    }

}
