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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Spliterator;
import java.util.Spliterators;
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
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
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
public class GMLFeatureStore extends DataStore implements FeatureSet, ResourceOnFileSystem {

    private final Parameters parameters;
    private final Path file;
    private FeatureType featureType;
    private Boolean longitudeFirst;

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

    public GMLFeatureStore(final URI uri) throws MalformedURLException, DataStoreException{
        this(toParameters(uri));
    }

    public GMLFeatureStore(final ParameterValueGroup params) throws DataStoreException {
        parameters = Parameters.unmodifiable(params);

        final URI uri = parameters.getMandatoryValue(GMLProvider.PATH);
        this.file = Paths.get(uri);
        this.longitudeFirst = parameters.getValue(GMLProvider.LONGITUDE_FIRST);
    }

    private static ParameterValueGroup toParameters(final URI uri) throws MalformedURLException{
        final Parameters params = Parameters.castOrWrap(GMLProvider.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(GMLProvider.PATH).setValue(uri);
        return params;
    }

    @Override
    public GenericName getIdentifier() throws DataStoreException {
        return getType().getName();
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
            if (xsd != null) {
                //read types from XSD file
                final JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
                try {
                    for (FeatureType ft : reader.read(new URL(xsd))) {
                        if (ft.getName().tip().toString().equalsIgnoreCase(xsdTypeName)) {
                            featureType = ft;
                        }
                    }
                    if (featureType == null) {
                        throw new DataStoreException("Type for name " + xsdTypeName + " not found in xsd.");
                    }

                    // schemaLocations.put(reader.getTargetNamespace(),xsd); needed?
                } catch (MalformedURLException | JAXBException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            } else {
                final JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader();
                reader.getProperties().put(JAXPStreamFeatureReader.LONGITUDE_FIRST, longitudeFirst);
                reader.setReadEmbeddedFeatureType(true);
                try {
                    FeatureReader ite = reader.readAsStream(file);
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
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
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
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        final DefaultMetadata metadata = new DefaultMetadata();
        final DefaultDataIdentification idf = new DefaultDataIdentification();
        final DefaultCitation citation = new DefaultCitation();
        citation.getIdentifiers().add(NamedIdentifier.castOrCopy(getIdentifier()));
        idf.setCitation(citation);
        metadata.setIdentificationInfo(Arrays.asList(idf));
        return metadata;
    }

    @Override
    public Envelope getEnvelope() throws DataStoreException {
        return null;
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

}
