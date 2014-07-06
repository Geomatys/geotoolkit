/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.memory.GenericReprojectFeatureIterator;
import org.geotoolkit.data.memory.GenericWrapFeatureIterator;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.ows.xml.BoundingBox;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.wfs.xml.FeatureTypeList;
import org.geotoolkit.wfs.xml.TransactionResponse;
import org.geotoolkit.wfs.xml.WFSCapabilities;
import org.geotoolkit.wfs.xml.WFSMarshallerPool;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * WFS Datastore, This implementation is read only.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WFSFeatureStore extends AbstractFeatureStore{

    private static final AtomicLong NS_INC = new AtomicLong();

    private final QueryCapabilities queryCapabilities = new DefaultQueryCapabilities(false);
    private final WebFeatureClient server;
    private final List<Name> typeNames = new ArrayList<Name>();
    private final Map<Name,FeatureType> types = new HashMap<Name,FeatureType>();
    private final Map<Name,Envelope> bounds = new HashMap<Name, Envelope>();
    private final Map<String,String> prefixes = new HashMap<String, String>();


    public WFSFeatureStore(WebFeatureClient server){
        super(server.getConfiguration());

        this.server = server;
        checkTypeExist();

    }

	private void checkTypeExist() {
		final WFSCapabilities capabilities = server.getCapabilities();

        final FeatureTypeList lst = capabilities.getFeatureTypeList();
        for(final org.geotoolkit.wfs.xml.FeatureType ftt : lst.getFeatureType()){

            //extract the name -------------------------------------------------
            QName typeName = ftt.getName();
            String prefix = typeName.getPrefix();
            final String uri = typeName.getNamespaceURI();
            final String localpart = typeName.getLocalPart();
            if(prefix == null || prefix.isEmpty()){
                prefix = "geotk" + NS_INC.incrementAndGet();
            }

            Name name = new DefaultName(uri, localpart);
            typeName = new QName(uri, localpart, prefix);

            //extract the feature type -----------------------------------------
            CoordinateReferenceSystem crs;
            FeatureType sft;
            try {
                String defaultCRS = ftt.getDefaultCRS();
                if(defaultCRS.contains("EPSG")){
                    final int last = defaultCRS.lastIndexOf(':');
                    defaultCRS = "EPSG:"+defaultCRS.substring(last+1);
                }
                crs = CRS.decode(defaultCRS,getLongitudeFirst());
                sft = requestType(typeName);
            } catch (IOException ex) {
                getLogger().log(Level.WARNING, null, ex);
                continue;
            } catch (FactoryException ex) {
                getLogger().log(Level.WARNING, null, ex);
                continue;
            }

            final FeatureTypeBuilder sftb = new FeatureTypeBuilder();
            sftb.setName(sft.getName());

            for(PropertyDescriptor desc : sft.getDescriptors()){
                if(desc instanceof GeometryDescriptor){
                    final GeometryDescriptor geomDesc = (GeometryDescriptor)desc;
                    final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
                    final AttributeTypeBuilder atb = new AttributeTypeBuilder();
                    atb.copy(geomDesc.getType());
                    atb.setCRS(crs);
                    adb.copy(geomDesc);
                    adb.setType(atb.buildGeometryType());
                    sftb.add(adb.buildDescriptor());
                }else{
                    sftb.add(desc);
                }
            }

            if(sft.getGeometryDescriptor() != null){
                sftb.setDefaultGeometry(sft.getGeometryDescriptor().getLocalName());
            }
            sft = sftb.buildFeatureType();
            name = sft.getName();
            types.put(name, sft);
            prefixes.put(name.getNamespaceURI(), prefix);
            typeNames.add(name);

            final GeometryDescriptor geomDesc = sft.getGeometryDescriptor();
            if(geomDesc != null){
                final CoordinateReferenceSystem val = geomDesc.getCoordinateReferenceSystem();
                if(val == null){
                    throw new IllegalArgumentException("CRS should not be null");
                }

                //extract the bounds -----------------------------------------------
                final BoundingBox bbox = ftt.getBoundingBox().get(0);
                try {
                    final String crsVal = bbox.getCrs();
                    crs = crsVal != null ? CRS.decode(crsVal) : CommonCRS.WGS84.normalizedGeographic();
                    final GeneralEnvelope env = new GeneralEnvelope(crs);
                    final Integer dims        = bbox.getDimensions();
                    final List<Double> upper  = bbox.getUpperCorner();
                    final List<Double> lower  = bbox.getLowerCorner();

                    //@TODO bbox should be null if there is no bbox in response.
                    if(dims == null) {continue;}
                    for(int i=0,n=dims.intValue();i<n;i++){
                        env.setRange(i, lower.get(i), upper.get(i));
                    }
                    bounds.put(name, env);
                } catch (FactoryException ex) {
                    getLogger().log(Level.WARNING, null, ex);
                }
            }

        }
	}

    public boolean getUsePost(){
        return Parameters.value(WFSFeatureStoreFactory.POST_REQUEST, parameters);
    }

    public boolean getLongitudeFirst(){
        return Parameters.getOrCreate(WFSFeatureStoreFactory.LONGITUDE_FIRST, parameters).booleanValue();
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(WFSFeatureStoreFactory.NAME);
    }

    @Override
    public boolean isWritable(final Name typeName) throws DataStoreException {
        this.typeCheck(typeName);
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<Name> getNames() throws DataStoreException {
        return new HashSet<Name>(types.keySet());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType(final Name typeName) throws DataStoreException {
        final FeatureType ft = types.get(typeName);

        if(ft == null){
            throw new DataStoreException("Type : "+ typeName + " doesn't exist in this datastore.");
        }

        return ft;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Envelope getEnvelope(final Query query) throws DataStoreException {
        final Name typeName = query.getTypeName();
        typeCheck(typeName);
        if(   query.getCoordinateSystemReproject() == null
           && query.getFilter() == Filter.INCLUDE
           && (query.getMaxFeatures() == null || query.getMaxFeatures() == Integer.MAX_VALUE)
           && query.getStartIndex() == 0){
            Envelope env = bounds.get(typeName);
            if(env != null) {return env;}
        }

        return super.getEnvelope(query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public QueryCapabilities getQueryCapabilities() {
        return queryCapabilities;
    }

    ////////////////////////////////////////////////////////////////////////////
    // schema manipulation /////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public void createFeatureType(final Name typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Schema creation not supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatureType(final Name typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Schema update not supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteFeatureType(final Name typeName) throws DataStoreException {
        throw new DataStoreException("Schema deletion not supported.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // read & write ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader<FeatureType,Feature> getFeatureReader(final Query query) throws DataStoreException {
        final Name name = query.getTypeName();
        //will raise an error if typename in unknowned
        final FeatureType sft = getFeatureType(name);

        final QName q = new QName(name.getNamespaceURI(), name.getLocalPart(), prefixes.get(name.getNamespaceURI()));
        final FeatureCollection<Feature> collection;
        try {
            collection = requestFeature(q, query);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }


        FeatureReader reader;
        if(collection == null){
            reader = GenericEmptyFeatureIterator.createReader(sft);
        }else{
            reader = GenericWrapFeatureIterator.wrapToReader(collection.iterator(), sft);
        }

        //we handle reprojection ourself, too complex or never done properly for a large
        //majority of wfs server tested.
        if(query.getCoordinateSystemReproject() != null){
            try {
                reader = GenericReprojectFeatureIterator.wrap(reader, query.getCoordinateSystemReproject(), null);
            } catch (FactoryException ex) {
                getLogger().log(Level.WARNING, ex.getMessage(), ex);
            } catch (SchemaException ex) {
                getLogger().log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        return reader;
    }

    /**
     * Writer that fall back on add,remove, update methods.
     */
    @Override
    public FeatureWriter getFeatureWriter(final Name typeName, final Filter filter, final Hints hints) throws DataStoreException {
        return handleWriter(typeName, filter, hints);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(final Name groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {

        final TransactionRequest request = server.createTransaction();
        final Insert insert = server.createInsertElement();
        insert.setInputFormat("text/xml; subtype=gml/3.1.1");

        final FeatureCollection col;
        if(newFeatures instanceof FeatureCollection){
            col = (FeatureCollection) newFeatures;
        }else{
            col = FeatureStoreUtilities.collection("", null);
            col.addAll(newFeatures);
        }
        insert.setFeatures(col);

        request.elements().add(insert);


        InputStream response = null;

        try {
            response = request.getResponseStream();
            Unmarshaller unmarshal = WFSMarshallerPool.getInstance().acquireUnmarshaller();
            Object obj = unmarshal.unmarshal(response);
            WFSMarshallerPool.getInstance().recycle(unmarshal);

            if(obj instanceof JAXBElement){
                obj = ((JAXBElement)obj).getValue();
            }

            if(obj instanceof TransactionResponse){
                final TransactionResponse tr = (TransactionResponse) obj;
                fireFeaturesAdded(groupName, null); // TODO list the feature added
                return tr.getInsertedFID();
            }else{
                throw new DataStoreException("Unexpected response : "+ obj.getClass());
            }

        } catch (IOException ex) {
            throw new DataStoreException(ex);
        } catch (JAXBException ex) {
            throw new DataStoreException(ex);
        } finally {
            if(response != null){
                try {
                    response.close();
                } catch (IOException ex) {
                    getLogger().log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatures(final Name groupName, final Filter filter, final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {

        final TransactionRequest request = server.createTransaction();
        final Update update = server.createUpdateElement();
        update.setInputFormat("text/xml; subtype=gml/3.1.1");

        update.setFilter(filter);
        update.setTypeName(groupName);
        for(Map.Entry<? extends PropertyDescriptor,? extends Object> entry : values.entrySet()){
            update.updates().put(entry.getKey(), entry.getValue());
        }

        request.elements().add(update);

        try {
            final InputStream response = request.getResponseStream();
            response.close();
            fireFeaturesUpdated(groupName, null);// TODO list the feature updated
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(final Name groupName, final Filter filter) throws DataStoreException {

        final TransactionRequest request = server.createTransaction();
        final Delete delete = server.createDeleteElement();

        delete.setTypeName(groupName);
        delete.setFilter(filter);

        request.elements().add(delete);

        try {
            final InputStream response = request.getResponseStream();
            response.close();
            fireFeaturesDeleted(groupName, null);// TODO list the feature deleted
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // read & write ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    private FeatureType requestType(final QName typeName) throws IOException{
        final DescribeFeatureTypeRequest request = server.createDescribeFeatureType();
        request.setTypeNames(Collections.singletonList(typeName));

        try {
            final JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
            final InputStream stream;
            if (getUsePost()) {
                getLogger().log(Level.INFO, "[WFS Client] request type by POST.");
                stream = request.getResponseStream();
            } else {
            getLogger().log(Level.INFO, "[WFS Client] request type : {0}", request.getURL());
                stream = request.getURL().openStream();
            }
            final List<FeatureType> featureTypes = reader.read(stream);
            return featureTypes.get(0);

        } catch (MalformedURLException ex) {
            throw new IOException(ex);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        }

    }

    private FeatureCollection<Feature> requestFeature(final QName typeName, final Query query) throws IOException {
        final Name name = new DefaultName(typeName);
        FeatureType sft = types.get(name);
        sft = FeatureTypeUtilities.createSubType(sft, query.getPropertyNames());

        final GetFeatureRequest request = server.createGetFeature();
        request.setTypeName(typeName);

        if(query != null){

            final Filter filter = query.getFilter();
            if(filter == null){
                request.setFilter(Filter.INCLUDE);
            }else{
                request.setFilter(filter);
            }

            final Integer max = query.getMaxFeatures();
            if(max != null){
                request.setMaxFeatures(max);
            }

            request.setPropertyNames(query.getPropertyNames());
        }

        XmlFeatureReader reader = null;
        try {
            reader = new JAXPStreamFeatureReader(sft);
            reader.getProperties().put(JAXPStreamFeatureReader.SKIP_UNEXPECTED_PROPERTY_TAGS, true);
            final InputStream stream;
            if (getUsePost()) {
                getLogger().log(Level.INFO, "[WFS Client] request feature by POST.");
                stream = request.getResponseStream();
            } else {
                final URL url = request.getURL();
                getLogger().log(Level.INFO, "[WFS Client] request feature : {0}", url);
                stream = url.openStream();
            }
            final Object result = reader.read(stream);


            if(result instanceof Feature){
                final Feature sf = (Feature) result;
                final FeatureCollection<Feature> col = FeatureStoreUtilities.collection("id", sft);
                col.add(sf);
                return col;
            }else if(result instanceof FeatureCollection){
                final FeatureCollection<Feature> col = (FeatureCollection<Feature>) result;
                return col;
            }else{
                final FeatureCollection<Feature> col = FeatureStoreUtilities.collection("", sft);
                return col;
            }

        }catch (XMLStreamException ex) {
            throw new IOException(ex);
        }finally{
            if(reader != null){
                reader.dispose();
            }
        }

    }

	@Override
	public void refreshMetaModel() {
		types.clear();
		prefixes.clear();
		typeNames.clear();
		bounds.clear();
		checkTypeExist();

	}

}
