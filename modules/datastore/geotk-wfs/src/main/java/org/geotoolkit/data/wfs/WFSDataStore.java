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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollectionUtilities;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.feature.xml.jaxp.JAXPEventFeatureReader;
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
            try {
                types.put(typeName, requestType(typeName));
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
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
        final SimpleFeatureType sft = types.get(typeName);
        final FeatureCollection<SimpleFeatureType,SimpleFeature> collection = requestFeature(typeName);

        return DataUtilities.wrapToReader(sft, collection.features());
    }

    private SimpleFeatureType requestType(String typeName) throws IOException{
        final DescribeFeatureTypeRequest request = server.createDescribeFeatureType();
        request.setTypeName(typeName);
        try {
            request.getURL();
        } catch (MalformedURLException ex) {
            throw new IOException(ex);
        }

        return null;
    }

    private FeatureCollection<SimpleFeatureType,SimpleFeature> requestFeature(String typeName) throws IOException {
        final SimpleFeatureType sft = types.get(typeName);

        final GetFeatureRequest request = server.createGetFeature();
        request.setTypeName(typeName);

        try {
            final XmlFeatureReader reader = new JAXPEventFeatureReader(sft);
            final URL url = request.getURL();
            System.out.println("Get feature url : " + url);
            final Object result = reader.read(url.openStream());

            if(result instanceof SimpleFeature){
                final SimpleFeature sf = (SimpleFeature) result;
                final FeatureCollection<SimpleFeatureType,SimpleFeature> col = FeatureCollectionUtilities.createCollection("id", sft);
                col.add(sf);
                return col;
            }else if(result instanceof FeatureCollection){
                return (FeatureCollection<SimpleFeatureType, SimpleFeature>) result;
            }

        } catch (JAXBException ex) {
            throw new IOException(ex);
        }

        return null;
    }

}
