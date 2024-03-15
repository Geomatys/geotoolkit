/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.feature.AbstractOperation;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.filter.privy.FunctionNames;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.IllegalNameException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.storage.WritableFeatureSet;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.feature.FeatureTypeExt;
import org.geotoolkit.feature.xml.XmlFeatureReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.filter.visitor.DuplicatingFilterVisitor;
import org.geotoolkit.ows.xml.BoundingBox;
import org.geotoolkit.storage.FeatureMapUpdate;
import org.geotoolkit.storage.feature.FeatureReader;
import org.geotoolkit.storage.feature.FeatureStreams;
import org.geotoolkit.storage.feature.GenericNameIndex;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.wfs.xml.TransactionResponse;
import org.geotoolkit.wfs.xml.WFSCapabilities;
import org.geotoolkit.wfs.xml.WFSMarshallerPool;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.ValueReference;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WFSFeatureSet implements WritableFeatureSet {

    private static final AtomicLong NS_INC = new AtomicLong();

    private final WebFeatureClient store;
    private final WFSCapabilities capabilities;
    private final org.geotoolkit.wfs.xml.FeatureType ftt;
    private final Map<String,String> prefixes = new HashMap<>();

    private FeatureType type;
    private Envelope envelope;

    public WFSFeatureSet(WebFeatureClient store, WFSCapabilities capabilities, org.geotoolkit.wfs.xml.FeatureType ftt) {
        this.store = store;
        this.capabilities = capabilities;
        this.ftt = ftt;
    }

    private void init() throws DataStoreException {

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
            if (store.getLongitudeFirst()) {
                crs = AbstractCRS.castOrCopy(crs).forConvention(AxesConvention.RIGHT_HANDED);
            }
            sft = requestType(typeName);
        } catch (IOException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        } catch (FactoryException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

        final FeatureTypeBuilder sftb = new FeatureTypeBuilder(sft);

        AttributeTypeBuilder geomDesc = null;
        for (PropertyTypeBuilder pt : sftb.properties()) {
            if (pt instanceof AttributeTypeBuilder && Geometry.class.isAssignableFrom(((AttributeTypeBuilder)pt).getValueClass())) {
                ((AttributeTypeBuilder) pt).setCRS(crs);
                if (geomDesc == null) {
                    geomDesc = (AttributeTypeBuilder) pt;
                    geomDesc.addRole(AttributeRole.DEFAULT_GEOMETRY);
                }
            }
        }

        type = sftb.build();
        final GenericName name = sft.getName();
        if (isNamespacePresent) {
            prefixes.put(NamesExt.getNamespace(name), prefix);
        }

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

                if (dims != null) {
                    for (int i = 0, n = dims; i < n; i++) {
                        env.setRange(i, lower.get(i), upper.get(i));
                    }
                    envelope = env;
                }
            } catch (FactoryException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        init();
        return type;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        init();
        return Optional.ofNullable(envelope);
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(getType().getName());
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        final DefaultMetadata metadata = new DefaultMetadata();
        final DefaultDataIdentification idf = new DefaultDataIdentification();
        final DefaultCitation citation = new DefaultCitation();
        getIdentifier().ifPresent((name) -> citation.getIdentifiers().add(NamedIdentifier.castOrCopy(name)));
        idf.setCitation(citation);
        metadata.setIdentificationInfo(Arrays.asList(idf));
        return metadata;
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final FeatureType sft = getType();

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
            reader = requestFeature(q, new org.geotoolkit.storage.feature.query.Query(sft.getName()));
        } catch (IOException | XMLStreamException ex) {
            throw new DataStoreException(ex);
        }

        final Stream<Feature> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(reader, Spliterator.ORDERED), false);
        return stream.onClose(reader::close);
    }

    @Override
    public void add(Iterator<? extends Feature> newFeatures) throws DataStoreException {

        final List<Feature> features = new ArrayList<>();
        newFeatures.forEachRemaining(features::add);

        if (features.isEmpty()) {
            //nothing to add
            return;
        }

        final TransactionRequest request = store.createTransaction();
        final Insert insert = store.createInsertElement();
        insert.setInputFormat("text/xml; subtype=\"gml/3.1.1\"");

        final FeatureType featureType = getType();
        final FeatureSet col = new InMemoryFeatureSet(NamesExt.create("id"), featureType, features, false);
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
                // TODO send event
                //tr.getInsertedFID();
            }else{
                throw new DataStoreException("Unexpected response : "+ obj.getClass());
            }

        } catch (IOException ex) {
            throw new DataStoreException(ex);
        } catch (JAXBException ex) {
            throw new DataStoreException(ex);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ex) {
                    store.getLogger().log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void removeIf(Predicate<? super Feature> filter) throws DataStoreException {
        if (!(filter instanceof Filter)) {
            throw new DataStoreException("Predicate filter must be an instance of org.opengis.filter.Filter");
        }

        final FeatureType featureType = getType();

        final TransactionRequest request = store.createTransaction();
        final Delete delete = store.createDeleteElement();

        delete.setTypeName(featureType.getName());
        delete.setFilter((Filter) filter);

        request.elements().add(delete);

        try {
            final InputStream response = request.getResponseStream();
            response.close();
            //TODO send event
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public void replaceIf(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) throws DataStoreException {
        if (!(filter instanceof Filter)) {
            throw new DataStoreException("Predicate filter must be an instance of org.opengis.filter.Filter");
        }
        if (!(updater instanceof FeatureMapUpdate)) {
            throw new DataStoreException("Predicate filter must be an instance of org.opengis.filter.Filter");
        }

        final FeatureType featureType = getType();
        final FeatureMapUpdate map = (FeatureMapUpdate) updater;

        final TransactionRequest request = store.createTransaction();
        final Update update = store.createUpdateElement();
        update.setInputFormat("text/xml; subtype=\"gml/3.1.1\"");

        update.setFilter((Filter) filter);
        update.setTypeName(featureType.getName());
        for (Map.Entry<String,? extends Object> entry : map.getValues().entrySet()) {
            update.updates().put(featureType.getProperty(entry.getKey()), entry.getValue());
        }

        request.elements().add(update);

        try {
            final InputStream response = request.getResponseStream();
            response.close();
            //TODO send event
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public void updateType(FeatureType newType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
    }

    @Override
    public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
    }

    private FeatureType requestType(final QName typeName) throws IOException{
        final DescribeFeatureTypeRequest request = store.createDescribeFeatureType();
        request.setTypeNames(Collections.singletonList(typeName));

        try {
            final JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
            final InputStream stream;
            if (store.getUsePost()) {
                store.getLogger().log(Level.INFO, "[WFS Client] request type by POST.");
                stream = request.getResponseStream();
            } else {
                store.getLogger().log(Level.INFO, "[WFS Client] request type : {0}", request.getURL());
                stream = request.getURL().openStream();
            }
            final GenericNameIndex<FeatureType> featureTypes = reader.read(stream);
            return featureTypes.get(typeName.getLocalPart());

        } catch (JAXBException ex) {
            throw new IOException(ex);
        } catch (IllegalNameException ex) {
            throw new IOException(ex);
        }
    }

    private FeatureReader requestFeature(final QName typeName, final Query query) throws XMLStreamException, DataStoreException, IOException {
        if (!(query instanceof org.geotoolkit.storage.feature.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.storage.feature.query.Query gquery = (org.geotoolkit.storage.feature.query.Query) query;
        FeatureType type = getType();
        // TODO : remove SIS conventions
        final GetFeatureRequest request = store.createGetFeature();
        request.setTypeName(typeName);

        /* We create a secondary query whose role is to handle mappings we won't
         * delegate to the WFS service. Examples are start offset, which cannot
         * be converted to a proper WFS parameter, and the asked reprojection (if
         * any), because it happens that WFS servers handle it badly.
         *
         */
        final org.geotoolkit.storage.feature.query.Query remainingQuery;
        if (gquery != null) {
            remainingQuery = new org.geotoolkit.storage.feature.query.Query();
            remainingQuery.copy(gquery);

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

            final Filter filter = gquery.getSelection();
            if (filter == null) {
                request.setFilter(Filter.include());
            } else {
                final Object visited = new PropertyNameReplacement(replacements).visit(filter);
                if (visited instanceof Filter) {
                    request.setFilter((Filter) visited);
                } else {
                    request.setFilter(filter);
                }
            }

            // Filter is already processed, but a query builder does not support null filter.
            remainingQuery.setSelection(Filter.include());

            final long start = gquery.getOffset();
            final long max = gquery.getLimit().orElse(-1);
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
        if (store.getUsePost()) {
            store.getLogger().log(Level.INFO, "[WFS Client] request feature by POST.");
            stream = request.getResponseStream();
        } else {
            final URL url = request.getURL();
            store.getLogger().log(Level.INFO, "[WFS Client] request feature : {0}", url);
            stream = url.openStream();
        }

        FeatureReader streamReader = reader.readAsStream(stream);
        if (remainingQuery != null) {
            streamReader = FeatureStreams.subset(streamReader, remainingQuery);
        }

        return streamReader;
    }

    private static class PropertyNameReplacement extends DuplicatingFilterVisitor {
        public PropertyNameReplacement(Map<String, String> nameReplacements) {
            setExpressionHandler(FunctionNames.ValueReference, (e) -> {
                final ValueReference expression = (ValueReference) e;
                final String newName = nameReplacements.get(expression.getXPath());
                if (newName != null) {
                    return ff.property(newName);
                } else {
                    return super.visit(expression);
                }
            });
        }
    }
}
