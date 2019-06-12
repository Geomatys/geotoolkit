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
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.feature.AbstractOperation;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.IllegalNameException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeExt;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.filter.visitor.DuplicatingFilterVisitor;
import org.geotoolkit.internal.data.GenericNameIndex;
import org.geotoolkit.ows.xml.BoundingBox;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.wfs.xml.FeatureTypeList;
import org.geotoolkit.wfs.xml.TransactionResponse;
import org.geotoolkit.wfs.xml.WFSCapabilities;
import org.geotoolkit.wfs.xml.WFSMarshallerPool;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * WFS Datastore, This implementation is read only.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WFSFeatureStore extends AbstractFeatureStore {

    private static final AtomicLong NS_INC = new AtomicLong();

    private final QueryCapabilities queryCapabilities = new DefaultQueryCapabilities(false);
    private final WebFeatureClient server;
    private final List<GenericName> typeNames = new ArrayList<>();
    private final GenericNameIndex<FeatureType> types = new GenericNameIndex<>();
    private final GenericNameIndex<Envelope> bounds = new GenericNameIndex<>();
    private final Map<String,String> prefixes = new HashMap<>();


    public WFSFeatureStore(WebFeatureClient server) throws WebFeatureException {
        super(server.getOpenParameters());

        this.server = server;
        try {
            checkTypeExist();
        } catch (IllegalNameException ex) {
            getLogger().log(Level.WARNING, ex.getMessage(), ex);
            throw new FeatureStoreRuntimeException(ex);
        }
    }

    @Override
    public GenericName getIdentifier() {
        return null;
    }

    private void checkTypeExist() throws WebFeatureException, IllegalNameException {

        final WFSCapabilities capabilities = server.getServiceCapabilities();
        final FeatureTypeList lst = capabilities.getFeatureTypeList();

        for (final org.geotoolkit.wfs.xml.FeatureType ftt : lst.getFeatureType()) {
            QName typeName = ftt.getName();
            String prefix = typeName.getPrefix();
            final String uri = typeName.getNamespaceURI();
            final String localpart = typeName.getLocalPart();
            final boolean isNamespacePresent = uri != null && !uri.isEmpty();
            if(isNamespacePresent && (prefix == null || prefix.isEmpty())) {
                prefix = "geotk" + NS_INC.incrementAndGet();
                typeName = new QName(uri, localpart, prefix);
            }

            //extract the feature type -----------------------------------------
            CoordinateReferenceSystem crs;
            FeatureType sft;
            try {
                String defaultCRS = ftt.getDefaultCRS();
                if (defaultCRS.contains("EPSG")) {
                    final int last = defaultCRS.lastIndexOf(':');
                    defaultCRS = "EPSG:"+defaultCRS.substring(last+1);
                }
                crs = CRS.forCode(defaultCRS);
                if (getLongitudeFirst()) {
                    crs = AbstractCRS.castOrCopy(crs).forConvention(AxesConvention.RIGHT_HANDED);
                }
                sft = requestType(typeName);
            } catch (IOException ex) {
                getLogger().log(Level.WARNING, null, ex);
                continue;
            } catch (FactoryException ex) {
                getLogger().log(Level.WARNING, null, ex);
                continue;
            }

            final FeatureTypeBuilder sftb = new FeatureTypeBuilder(sft);

            AttributeTypeBuilder geomDesc = null;
            for (PropertyTypeBuilder pt : sftb.properties()) {
                if (pt instanceof AttributeTypeBuilder && Geometry.class.isAssignableFrom(((AttributeTypeBuilder)pt).getValueClass())){
                    ((AttributeTypeBuilder)pt).setCRS(crs);
                    if(geomDesc==null){
                        geomDesc = (AttributeTypeBuilder) pt;
                        geomDesc.addRole(AttributeRole.DEFAULT_GEOMETRY);
                    }
                }
            }

            sft = sftb.build();
            final GenericName name = sft.getName();
            types.add(this, name, sft);
            if (isNamespacePresent) {
                prefixes.put(NamesExt.getNamespace(name), prefix);
            }
            typeNames.add(name);

            if(geomDesc != null){
                final CoordinateReferenceSystem val = geomDesc.getCRS();
                if(val == null){
                    throw new IllegalArgumentException("CRS should not be null");
                }

                //extract the bounds -----------------------------------------------
                final BoundingBox bbox = ftt.getBoundingBox().get(0);
                try {
                    final String crsVal = bbox.getCrs();
                    crs = crsVal != null ? CRS.forCode(crsVal) : CommonCRS.WGS84.normalizedGeographic();
                    final GeneralEnvelope env = new GeneralEnvelope(crs);
                    final Integer dims        = bbox.getDimensions();
                    final List<Double> upper  = bbox.getUpperCorner();
                    final List<Double> lower  = bbox.getLowerCorner();

                    //@TODO bbox should be null if there is no bbox in response.
                    if(dims == null) {continue;}
                    for(int i=0,n=dims.intValue();i<n;i++){
                        env.setRange(i, lower.get(i), upper.get(i));
                    }
                    bounds.add(this, name, env);
                } catch (FactoryException ex) {
                    getLogger().log(Level.WARNING, null, ex);
                }
            }

        }
    }

    public boolean getUsePost(){
        return parameters.getValue(WFSProvider.POST_REQUEST);
    }

    public boolean getLongitudeFirst(){
        return parameters.getValue(WFSProvider.LONGITUDE_FIRST);
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(WFSProvider.NAME);
    }

    @Override
    public boolean isWritable(final String typeName) throws DataStoreException {
        this.typeCheck(typeName);
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        return types.getNames();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getFeatureType(final String typeName) throws DataStoreException {
        final FeatureType ft = types.get(this, typeName);

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
        if (!(query instanceof org.geotoolkit.data.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        final String typeName = gquery.getTypeName();
        final FeatureType type = getFeatureType(typeName);
        if(   gquery.getCoordinateSystemReproject() == null
           && gquery.getFilter() == Filter.INCLUDE
           && gquery.getLimit() == -1
           && gquery.getOffset() == 0){
            Envelope env = bounds.get(this, type.getName().toString());
            if(env != null) {return env;}
        }

        return super.getEnvelope(gquery);
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
    public void createFeatureType(final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Schema creation not supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void updateFeatureType(final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Schema update not supported.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteFeatureType(final String typeName) throws DataStoreException {
        throw new DataStoreException("Schema deletion not supported.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // read & write ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.data.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        final String name = gquery.getTypeName();
        //will raise an error if typename in unknowned
        final FeatureType sft = getFeatureType(name);

        FeatureReader reader;
        final GenericName gName = sft.getName();
        final String namespace = NamesExt.getNamespace(gName);
        final QName q;
        if (namespace == null || namespace.isEmpty()) {
            q = new QName(gName.tip().toString());
        } else {
            q = new QName(namespace, gName.tip().toString(), prefixes.get(namespace));
        }
        try {
            reader = requestFeature(q, gquery);
        } catch (IOException|XMLStreamException ex) {
            throw new DataStoreException(ex);
        }

        return reader;
    }

    /**
     * Writer that fall back on add,remove, update methods.
     */
    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        return handleWriter(query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FeatureId> addFeatures(final String groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {

        final FeatureType featureType = getFeatureType(groupName);

        final TransactionRequest request = server.createTransaction();
        final Insert insert = server.createInsertElement();
        insert.setInputFormat("text/xml; subtype=\"gml/3.1.1\"");

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
                fireFeaturesAdded(featureType.getName(), null); // TODO list the feature added
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
    public void updateFeatures(final String groupName, final Filter filter, final Map<String, ? extends Object> values) throws DataStoreException {

        final FeatureType featureType = getFeatureType(groupName);

        final TransactionRequest request = server.createTransaction();
        final Update update = server.createUpdateElement();
        update.setInputFormat("text/xml; subtype=\"gml/3.1.1\"");

        update.setFilter(filter);
        update.setTypeName(featureType.getName());
        for(Map.Entry<String,? extends Object> entry : values.entrySet()){
            update.updates().put(featureType.getProperty(entry.getKey()), entry.getValue());
        }

        request.elements().add(update);

        try {
            final InputStream response = request.getResponseStream();
            response.close();
            fireFeaturesUpdated(featureType.getName(), null);// TODO list the feature updated
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void removeFeatures(final String groupName, final Filter filter) throws DataStoreException {

        final FeatureType featureType = getFeatureType(groupName);

        final TransactionRequest request = server.createTransaction();
        final Delete delete = server.createDeleteElement();

        delete.setTypeName(featureType.getName());
        delete.setFilter(filter);

        request.elements().add(delete);

        try {
            final InputStream response = request.getResponseStream();
            response.close();
            fireFeaturesDeleted(featureType.getName(), null);// TODO list the feature deleted
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

        } catch (JAXBException ex) {
            throw new IOException(ex);
        }

    }

    private FeatureReader requestFeature(final QName typeName, final Query query) throws XMLStreamException, DataStoreException, IOException {
        if (!(query instanceof org.geotoolkit.data.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        final GenericName name = NamesExt.create(typeName);
        FeatureType type = types.get(this, name.toString());
        // TODO : remove SIS conventions
        final GetFeatureRequest request = server.createGetFeature();
        request.setTypeName(typeName);

        /* We create a secondary query whose role is to handle mappings we won't
         * delegate to the WFS service. Examples are start offset, which cannot
         * be converted to a proper WFS parameter, and the asked reprojection (if
         * any), because it happens that WFS servers handle it badly.
         *
         */
        final QueryBuilder remainingQuery;
        if (gquery != null) {
            remainingQuery = new QueryBuilder(gquery);

            final Map<String, String> replacements = type.getProperties(true).stream()
                // operations are not data sent back by the server.
                .filter(pType -> pType instanceof AbstractOperation)
                .map(pType -> new AbstractMap.SimpleEntry<>(pType.getName(), ((AbstractOperation) pType).getDependencies()))
                // If dependency is more than one property, we cannot make a simple replacement
                .filter(entry -> entry.getValue().size() == 1)
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue().iterator().next()
                ));

            String[] propertyNames = gquery.getPropertyNames();
            if (propertyNames != null) {
                propertyNames = Stream.of(propertyNames)
                    .map(pName -> replacements.getOrDefault(pName, pName))
                    .toArray(size -> new String[size]);
            }

            type = FeatureTypeExt.createSubType(type, propertyNames);

            final Filter filter = gquery.getFilter();
            if (filter == null) {
                request.setFilter(Filter.INCLUDE);
            } else {
                final Object visited = filter.accept(new PropertyNameReplacement(replacements), null);
                if (visited instanceof Filter) {
                    request.setFilter((Filter) visited);
                } else {
                    request.setFilter(filter);
                }
            }

            // Filter is already processed, but a query builder does not support null filter.
            remainingQuery.setFilter(Filter.INCLUDE);

            final long start = gquery.getOffset();
            final long max = gquery.getLimit();
            if (start <= 0 && max != -1) {
                request.setMaxFeatures((int) max);
                // For this one, do not remove from remaining queries : If the
                // wfs service does not manage it, we will do it afterwards.
            } else if (max > 0) {
                /* If an offset is provided, we'll have to skip elements manually,
                 * so we keep start index in remaining query. All we can do here
                 * is query that we want no more than the number of features
                 * needed to do the skip, + the number of features wanted by
                */
                request.setMaxFeatures((int) (start + max));
            }

            if (propertyNames != null) {
                final GenericName[] names = type.getProperties(true).stream()
                        // operations are not data sent back by the server.
                        .filter(pType -> !(pType instanceof Operation))
                        .map(PropertyType::getName)
                        .toArray(size -> new GenericName[size]);

                request.setPropertyNames(names);
                remainingQuery.setProperties(null);
            }
        } else remainingQuery = null;

        final XmlFeatureReader reader = new JAXPStreamFeatureReader(type);
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

        FeatureReader streamReader = reader.readAsStream(stream);
        if (remainingQuery != null) {
            streamReader = FeatureStreams.subset(streamReader, remainingQuery.buildQuery());
        }

        return streamReader;
    }

    @Override
    public void refreshMetaModel() throws IllegalNameException {
        types.clear();
        prefixes.clear();
        typeNames.clear();
        bounds.clear();
        checkTypeExist();
    }

    private static class PropertyNameReplacement extends DuplicatingFilterVisitor {

        private final Map<String, String> nameReplacements;

        public PropertyNameReplacement(Map<String, String> nameReplacements) {
            this.nameReplacements = nameReplacements;
        }

        @Override
        public Object visit(PropertyName expression, Object extraData) {
            final String newName = nameReplacements.get(expression.getPropertyName());

            if (newName != null) {
                return getFactory(extraData).property(newName);
            } else {
                return super.visit(expression, extraData);
            }
        }
    }
}
