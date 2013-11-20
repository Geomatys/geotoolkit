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

import org.geotoolkit.client.AbstractCoverageServer;
import org.geotoolkit.client.AbstractServerFactory;
import org.geotoolkit.client.ServerFinder;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.CoverageType;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.security.ClientSecurity;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataNode;
import org.geotoolkit.storage.DefaultDataNode;
import org.opengis.feature.type.Name;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Client for google static maps.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class StaticGoogleMapsServer extends AbstractCoverageServer implements CoverageStore{

    public static final URL DEFAULT_GOOGLE_STATIC_MAPS;

    static {
        try {
            DEFAULT_GOOGLE_STATIC_MAPS = new URL("http://maps.google.com/maps/api/staticmap");
        } catch (MalformedURLException ex) {
            //will not happen
            throw new RuntimeException(ex.getLocalizedMessage(),ex);
        }
    }

    private final DataNode rootNode = new DefaultDataNode();

    /**
     * Builds a google maps server with the default google server address.
     */
    public StaticGoogleMapsServer() throws DataStoreException {
        this(DEFAULT_GOOGLE_STATIC_MAPS,null);
    }

    /**
     * Builds a google maps server with the given server url.
     *
     * @param serverURL The server base url.
     * @param key, account key.
     */
    public StaticGoogleMapsServer(final URL serverURL, final String key) throws DataStoreException {
        this(serverURL,key,null,false);
    }

    public StaticGoogleMapsServer(final URL serverURL, final String key,
            final ClientSecurity security, boolean cacheImage) throws DataStoreException {
        this(toParameters(serverURL, key, security, cacheImage));
    }

    public StaticGoogleMapsServer(ParameterValueGroup params) throws DataStoreException {
        super(params);

        final boolean cache = getCacheImage();
        final GoogleCoverageReference ref1 = new GoogleCoverageReference(this,DefaultName.valueOf("{http://google.com}"+GetMapRequest.TYPE_HYBRID),cache);
        final GoogleCoverageReference ref2 = new GoogleCoverageReference(this,DefaultName.valueOf("{http://google.com}"+GetMapRequest.TYPE_ROADMAP),cache);
        final GoogleCoverageReference ref3 = new GoogleCoverageReference(this,DefaultName.valueOf("{http://google.com}"+GetMapRequest.TYPE_SATELLITE),cache);
        final GoogleCoverageReference ref4 = new GoogleCoverageReference(this,DefaultName.valueOf("{http://google.com}"+GetMapRequest.TYPE_TERRAIN),cache);

        rootNode.getChildren().add(ref1);
        rootNode.getChildren().add(ref2);
        rootNode.getChildren().add(ref3);
        rootNode.getChildren().add(ref4);
    }

    private static ParameterValueGroup toParameters(final URL serverURL, final String key,
            final ClientSecurity security, boolean cacheImage){
        final ParameterValueGroup params = create(StaticGoogleServerFactory.PARAMETERS, serverURL, security);
        Parameters.getOrCreate(StaticGoogleServerFactory.IMAGE_CACHE, params).setValue(cacheImage);
        return params;
    }


    @Override
    public StaticGoogleServerFactory getFactory() {
        return (StaticGoogleServerFactory) ServerFinder.getFactoryById(StaticGoogleServerFactory.NAME);
    }

    @Override
    public DataNode getRootNode() throws DataStoreException {
        return rootNode;
    }

    public boolean getCacheImage(){
        return (Boolean)Parameters.getOrCreate(AbstractServerFactory.IMAGE_CACHE, parameters).getValue();
    }

    @Override
    public void dispose() {
    }

    @Override
    public CoverageReference create(Name name) throws DataStoreException {
        throw new DataStoreException("Can not create new coverage.");
    }

    @Override
    public void delete(Name name) throws DataStoreException {
        throw new DataStoreException("Can not create new coverage.");
    }

    @Override
    public CoverageType getType() {
        return CoverageType.PYRAMID;
    }

}
