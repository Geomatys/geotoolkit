/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.data.gml;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.IllegalNameException;
import org.apache.sis.storage.WritableFeatureSet;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
import org.geotoolkit.internal.data.ArrayFeatureSet;
import org.geotoolkit.internal.data.FeatureCatalogue;
import org.geotoolkit.internal.data.GenericNameIndex;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * GML feature store.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GMLFeatureStore extends DataStore implements WritableFeatureSet, ResourceOnFileSystem, FeatureCatalogue {

    private final Parameters parameters;
    private final Path file;
    private FeatureType featureType;
    private Boolean longitudeFirst;
    private GenericNameIndex<FeatureType> catalog;

    /**
     * @deprecated use {@link #GMLFeatureStore(Path)} or {@link #GMLFeatureStore(ParameterValueGroup)} instead
     */
    @Deprecated
    public GMLFeatureStore(final File f) throws MalformedURLException, DataStoreException{
        this(f.toURI());
    }

    public GMLFeatureStore(final Path f) throws MalformedURLException, DataStoreException{
        this(f.toUri());
    }

    public GMLFeatureStore(final Path f, String xsd, String typeName, Boolean longitudeFirst) throws MalformedURLException, DataStoreException{
        this(toParameters(f.toUri(), xsd, typeName, longitudeFirst));
    }

    public GMLFeatureStore(final URI uri) throws MalformedURLException, DataStoreException{
        this(toParameters(uri, null, null, null));
    }

    public GMLFeatureStore(final ParameterValueGroup params) throws DataStoreException {
        parameters = Parameters.unmodifiable(params);

        final URI uri = parameters.getMandatoryValue(GMLProvider.PATH);
        this.file = Paths.get(uri);
        this.longitudeFirst = parameters.getValue(GMLProvider.LONGITUDE_FIRST);
    }

    private static ParameterValueGroup toParameters(final URI uri, String xsd, String typeName, Boolean longitudeFirst) throws MalformedURLException{
        final Parameters params = Parameters.castOrWrap(GMLProvider.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(GMLProvider.PATH).setValue(uri);
        if (xsd != null) params.getOrCreate(GMLProvider.XSD).setValue(xsd);
        if (typeName != null) params.getOrCreate(GMLProvider.XSD_TYPE_NAME).setValue(typeName);
        if (longitudeFirst != null) params.getOrCreate(GMLProvider.LONGITUDE_FIRST).setValue(longitudeFirst);
        return params;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(getType().getName());
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(GMLProvider.NAME);
    }

    @Override
    public ParameterValueGroup getOpenParameters() {
        return parameters;
    }

    @Override
    public synchronized FeatureType getType() throws DataStoreException {
        if (featureType == null) {
            final String xsd = parameters.getValue(GMLProvider.XSD);
            final String xsdTypeName = parameters.getValue(GMLProvider.XSD_TYPE_NAME);
            catalog = new GenericNameIndex();

            if (xsd != null) {
                //read types from XSD file
                final JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
                try {
                    catalog = reader.read(new URL(xsd));
                    featureType = catalog.get(xsdTypeName);

                    // schemaLocations.put(reader.getTargetNamespace(),xsd); needed?
                } catch (MalformedURLException | JAXBException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            } else {
                final JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader();
                reader.getProperties().put(JAXPStreamFeatureReader.LONGITUDE_FIRST, longitudeFirst);
                reader.getProperties().put(JAXPStreamFeatureReader.READ_EMBEDDED_FEATURE_TYPE, true);
                try {
                    FeatureReader ite = reader.readAsStream(file);
                    catalog = reader.getFeatureTypes();
                    featureType = ite.getFeatureType();
                } catch (IOException | XMLStreamException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                } finally {
                    reader.dispose();
                }
            }
        }
        return featureType;
    }

    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        return new Path[]{file};
    }

    @Override
    public Set<GenericName> getTypeNames() throws DataStoreException {
        getType(); //force loading catalogue
        return catalog.getNames();
    }

    @Override
    public FeatureType getFeatureType(String name) throws DataStoreException, IllegalNameException {
        getType(); //force loading catalogue
        return catalog.get(null, name);
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        if (Files.exists(file)) {
            final JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader(getType());
            reader.getProperties().put(JAXPStreamFeatureReader.LONGITUDE_FIRST, longitudeFirst);
            final CloseableIterator ite;
            try {
                ite = reader.readAsStream(file);
            } catch (IOException | XMLStreamException ex) {
                reader.dispose();
                throw new DataStoreException(ex.getMessage(),ex);
            } finally{
                //do not dispose, the iterator is closeable and will close the reader
                //reader.dispose();
            }

            final Spliterator<Feature> spliterator = Spliterators.spliteratorUnknownSize(ite, Spliterator.ORDERED);
            final Stream<Feature> stream = StreamSupport.stream(spliterator, false);
            return stream.onClose(ite::close);
        } else {
            //file may not exist yet if it's a new file.
            final Spliterator<Feature> empty = Spliterators.emptySpliterator();
            return StreamSupport.stream(empty, false);
        }
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        final DefaultMetadata metadata = new DefaultMetadata();
        getIdentifier().ifPresent((id) -> {
            final DefaultDataIdentification idf = new DefaultDataIdentification();
            final DefaultCitation citation = new DefaultCitation();
            citation.getIdentifiers().add(NamedIdentifier.castOrCopy(id));
            idf.setCitation(citation);
            metadata.setIdentificationInfo(Arrays.asList(idf));
        });
        return metadata;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.empty();
    }

    @Override
    public void close() throws DataStoreException {
    }

    @Override
    public <T extends ChangeEvent> void addListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

    @Override
    public <T extends ChangeEvent> void removeListener(ChangeListener<? super T> listener, Class<T> eventType) {
    }

    @Override
    public void updateType(FeatureType newType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void add(Iterator<? extends Feature> features) throws DataStoreException {

        final Path tempFile = file.resolveSibling(file.getFileName().toString()+".update");

        final FeatureType type = getType();

        final JAXPStreamFeatureWriter writer = new JAXPStreamFeatureWriter("3.2.1", "1.0.0", null);
        writer.getProperties().put(JAXPStreamFeatureReader.LONGITUDE_FIRST, longitudeFirst);
        try {
            //concatenate existing and new feature sets
            final List<Feature> lst = new ArrayList<>();
            while (features.hasNext()) lst.add(features.next());
            final FeatureSet newfs = new ArrayFeatureSet(type, lst, null);
            //TODO concatenate previous features

            writer.write(newfs, tempFile);

        } catch (IOException | XMLStreamException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        } finally {
            try {
                writer.dispose();
            } catch (IOException | XMLStreamException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public boolean removeIf(Predicate<? super Feature> filter) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public void replaceIf(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

}
