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
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.wfs.xml.v110.FeatureTypeListType;
import org.geotoolkit.wfs.xml.v110.FeatureTypeType;
import org.geotoolkit.wfs.xml.v110.WFSCapabilitiesType;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WFSDataStore extends AbstractDataStore{

    private final WebFeatureServer server;
    private final WFSCapabilitiesType capabilities;
    private final Map<String,SimpleFeatureType> types = new HashMap<String,SimpleFeatureType>();

    public WFSDataStore(URI serverURI) throws MalformedURLException{
        //not transactional for the moment
        super(false);

        this.server = new WebFeatureServer(serverURI.toURL(), "1.1.0");
        this.capabilities = server.getCapabilities();

        final FeatureTypeListType lst = capabilities.getFeatureTypeList();
        for(final FeatureTypeType ftt : lst.getFeatureType()){
            final String typeName = ftt.getName().getLocalPart();
            types.put(typeName,requestType(typeName));
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getTypeNames() throws IOException {
        final Set<String> keys = types.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeatureType getSchema(String typeName) throws IOException {
        final SimpleFeatureType sft = types.get(typeName);

        if(sft == null){
            throw new IOException("Type name : "+ typeName +"is unknowned is this datastore");
        }

        return sft;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(String typeName) throws IOException {
        return null;
    }

    private SimpleFeatureType requestType(String typeName){
        final DescribeFeatureTypeRequest request = server.createDescribeFeatureType();
        request.setTypeName(typeName);

//        request.getURL();

        return null;
    }

    private FeatureCollection<SimpleFeatureType,SimpleFeature> requestFeature(String typeName) throws IOException {
        final SimpleFeatureType sft = types.get(typeName);

        return null;
    }

}
