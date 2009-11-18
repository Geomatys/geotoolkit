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
import java.lang.ref.SoftReference;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureCollectionUtilities;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.collection.FeatureCollection;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.store.EmptyFeatureCollection;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPEventFeatureReader;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.wfs.xml.v110.FeatureTypeListType;
import org.geotoolkit.wfs.xml.v110.FeatureTypeType;
import org.geotoolkit.wfs.xml.v110.WFSCapabilitiesType;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WFSDataStore extends AbstractDataStore{

    private static final AtomicLong NS_INC = new AtomicLong();

    private final WebFeatureServer server;
    private final WFSCapabilitiesType capabilities;
    private final List<Name> typeNames = new ArrayList<Name>();
    private final Map<Name,SimpleFeatureType> types = new HashMap<Name,SimpleFeatureType>();
    private final Map<Name,Envelope> bounds = new HashMap<Name, Envelope>();
    private final Map<String,String> prefixes = new HashMap<String, String>();

    private Request lastRequest = null;
    private SoftReference<FeatureCollection<SimpleFeatureType,SimpleFeature>> lastCollection = null;


    public WFSDataStore(URI serverURI) throws MalformedURLException{
        //not transactional for the moment
        super(false);

        this.server = new WebFeatureServer(serverURI.toURL(), "1.1.0");
        this.capabilities = server.getCapabilities();

        final FeatureTypeListType lst = capabilities.getFeatureTypeList();
        for(final FeatureTypeType ftt : lst.getFeatureType()){

            //extract the name -------------------------------------------------
            QName typeName = ftt.getName();
            String prefix = typeName.getPrefix();
            String uri = typeName.getNamespaceURI();
            String localpart = typeName.getLocalPart();
            if(prefix == null || prefix.isEmpty()){
                prefix = "geotk" + NS_INC.incrementAndGet();
            }

            prefixes.put(uri, prefix);
            final Name name = new DefaultName(uri, localpart);
            typeName = new QName(uri, localpart, prefix);

            //extract the feature type -----------------------------------------
            CoordinateReferenceSystem crs;
            SimpleFeatureType sft;
            try {
                crs = CRS.decode(ftt.getDefaultSRS(),true);
                sft = requestType(typeName);                
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
                continue;
            } catch (FactoryException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
                continue;
            }

            final SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
            sftb.setCRS(crs);
            sftb.setName(sft.getName());
            for(AttributeDescriptor desc : sft.getAttributeDescriptors()){
                if(desc instanceof GeometryDescriptor){
                    GeometryDescriptor gd = (GeometryDescriptor) desc;
                    final AttributeTypeBuilder atb = new AttributeTypeBuilder();
                    atb.init(gd);
                    atb.setCRS(crs);
                    gd = atb.buildDescriptor(gd.getLocalName(), atb.buildGeometryType());
                    sftb.add(gd);
                }else{
                    sftb.add(desc);
                }
            }
            sftb.setDefaultGeometry(sft.getGeometryDescriptor().getLocalName());
            sft = sftb.buildFeatureType();

            CoordinateReferenceSystem val = sft.getGeometryDescriptor().getCoordinateReferenceSystem();
            if(val == null){
                throw new IllegalArgumentException("CRS should not be null");
            }

            types.put(name, sft);
            typeNames.add(name);

            //extract the bounds -----------------------------------------------
            final WGS84BoundingBoxType bbox = ftt.getWGS84BoundingBox().get(0);
            try {
                crs = CRS.decode(bbox.getCrs(),true);
                final GeneralEnvelope env = new GeneralEnvelope(crs);
                final BigInteger dims = bbox.getDimensions();
                final List<Double> upper = bbox.getUpperCorner();
                final List<Double> lower = bbox.getLowerCorner();

                for(int i=0,n=dims.intValue();i<n;i++){
                    env.setRange(i, lower.get(i), upper.get(i));
                }
                bounds.put(name, env);
            } catch (FactoryException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }

        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String[] getTypeNames() throws IOException {
        final String[] locals = new String[typeNames.size()];

        for(int i=0,n=typeNames.size(); i<n;i++){
            final Name name = typeNames.get(i);
            locals[i] = name.getLocalPart();
        }

        return locals;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Name> getNames() throws IOException {
        return Collections.unmodifiableList(typeNames);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeatureType getSchema(String typeName) throws IOException {
        for(Name n : typeNames){
            if(n.getLocalPart().equals(typeName)){
                return getSchema(n);
            }
        }

        throw new IOException("Type name : "+ typeName +"is unknowned is this datastore");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeatureType getSchema(Name name) throws IOException {
        final SimpleFeatureType sft = types.get(name);

        if(sft == null){
            throw new IOException("Type name : "+ name +"is unknowned is this datastore");
        }

        return sft;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(String typeName) throws IOException {
        return getFeatureReader(typeName, Query.ALL);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(String typeName, Query query) throws IOException {

        for(Name n : typeNames){
            if(n.getLocalPart().equals(typeName)){
                final SimpleFeatureType sft = types.get(n);
                final QName q = new QName(n.getNamespaceURI(), n.getLocalPart(), prefixes.get(n.getNamespaceURI()));
                FeatureCollection<SimpleFeatureType,SimpleFeature> collection = requestFeature(q,query);

                if(collection == null){
                    return DataUtilities.wrapToReader(sft, new EmptyFeatureCollection(sft).features());
                }else{
                    return DataUtilities.wrapToReader(sft, collection.features());
                }

            }
        }

        throw new IOException("Type name : "+ typeName +"is unknowned is this datastore");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected JTSEnvelope2D getBounds(Query query) throws IOException {
        final String typeName = query.getTypeName();

        for(Name n : typeNames){
            if(n.getLocalPart().equals(typeName)){
                return new JTSEnvelope2D(bounds.get(n));
            }
        }

        throw new IOException("Type name : "+ typeName +"is unknowned is this datastore");
    }

    private SimpleFeatureType requestType(QName typeName) throws IOException{
        final DescribeFeatureTypeRequest request = server.createDescribeFeatureType();
        request.setTypeNames(Collections.singletonList(typeName));

        try {
            final JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
            LOGGER.log(Level.INFO, "[WFS Client] request type : " + request.getURL());
            final List<FeatureType> types = reader.read(request.getURL().openStream());
            return (SimpleFeatureType) types.get(0);

        } catch (MalformedURLException ex) {
            throw new IOException(ex);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }

    }

    private FeatureCollection<SimpleFeatureType,SimpleFeature> requestFeature(QName typeName, Query query) throws IOException {
        final Name name = new DefaultName(typeName);
        final SimpleFeatureType sft = types.get(name);

        final GetFeatureRequest request = server.createGetFeature();
        request.setTypeName(typeName);

        if(query != null){

            final Filter filter = query.getFilter();
            if(filter == null){
                request.setFilter(Filter.INCLUDE);
            }else{
                request.setFilter(filter);
            }

            final int max = query.getMaxFeatures();
            if(max != Query.DEFAULT_MAX){
                request.setMaxFeatures(max);
            }

            request.setPropertyNames(query.getPropertyNames());
        }

        if(request.equals(lastRequest)){
            FeatureCollection<SimpleFeatureType,SimpleFeature> col = lastCollection.get();
            if(col != null){
                return col;
            }
        }

        try {
            final XmlFeatureReader reader = new JAXPEventFeatureReader(sft);
            final URL url = request.getURL();
            LOGGER.log(Level.INFO, "[WFS Client] request feature : " + url);
            final Object result = reader.read(url.openStream());

            lastRequest = request;

            if(result instanceof SimpleFeature){
                final SimpleFeature sf = (SimpleFeature) result;
                final FeatureCollection<SimpleFeatureType,SimpleFeature> col = FeatureCollectionUtilities.createCollection("id", sft);
                col.add(sf);

                lastCollection = new SoftReference<FeatureCollection<SimpleFeatureType, SimpleFeature>>(col);

                return col;
            }else if(result instanceof FeatureCollection){
                final FeatureCollection<SimpleFeatureType,SimpleFeature> col = (FeatureCollection<SimpleFeatureType, SimpleFeature>) result;
                lastCollection = new SoftReference<FeatureCollection<SimpleFeatureType, SimpleFeature>>(col);
                return col;
            }else{
                throw new IOException("unexpected type : " + result);
            }

        } catch (JAXBException ex) {
            throw new IOException(ex);
        }

    }

}
