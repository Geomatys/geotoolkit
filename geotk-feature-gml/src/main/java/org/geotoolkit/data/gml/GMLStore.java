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
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import jakarta.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.storage.aggregate.ConcatenatedFeatureSet;
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
import org.geotoolkit.storage.feature.FeatureReader;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
import org.geotoolkit.storage.FeatureCatalogue;
import org.geotoolkit.storage.feature.GenericNameIndex;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * GML feature store.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GMLStore extends DataStore implements WritableFeatureSet, FeatureCatalogue {

    private final ReadWriteLock MAINLOCK = new ReentrantReadWriteLock();
    private final ReadWriteLock UPDATELOCK = new ReentrantReadWriteLock();

    private final Parameters parameters;
    private final Path file;
    /**
     * Root type may differ from feature type if the root type is actually
     * a feature collection (has attributes which are GML FeatureMember).
     *
     * A feature collection is a collection of feature instances.Within GML 3.2.1, the generic
     * gml:FeatureCollection element has been deprecated. A feature collection is any feature class
     * with a property element in its content model (for example member) which is derived by
     * extension from gml:AbstractFeatureMemberType.
     *
     */
    private FeatureType rootType;
    private FeatureType featureType;
    private Boolean longitudeFirst;
    private GenericNameIndex<FeatureType> catalog;

    /**
     * @deprecated use {@link #GMLFeatureStore(Path)} or {@link #GMLFeatureStore(ParameterValueGroup)} instead
     */
    @Deprecated
    public GMLStore(final File f) throws MalformedURLException, DataStoreException{
        this(f.toURI());
    }

    public GMLStore(final Path f) throws MalformedURLException, DataStoreException{
        this(f.toUri());
    }

    public GMLStore(final Path f, String xsd, String typeName, Boolean longitudeFirst) throws MalformedURLException, DataStoreException{
        this(toParameters(f.toUri(), xsd, typeName, longitudeFirst));
    }

    public GMLStore(final URI uri) throws MalformedURLException, DataStoreException{
        this(toParameters(uri, null, null, null));
    }

    public GMLStore(final ParameterValueGroup params) throws DataStoreException {
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
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.of(parameters);
    }

    @Override
    public synchronized FeatureType getType() throws DataStoreException {
        if (rootType == null) {
            final String xsd = parameters.getValue(GMLProvider.XSD);
            final String xsdTypeName = parameters.getValue(GMLProvider.XSD_TYPE_NAME);
            catalog = new GenericNameIndex();

            if (xsd != null) {
                //read types from XSD file
                final JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
                try {
                    catalog = reader.read(new URL(xsd));
                    rootType = catalog.get(xsdTypeName);

                    // schemaLocations.put(reader.getTargetNamespace(),xsd); needed?
                } catch (MalformedURLException | JAXBException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            } else {
                final JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader();
                reader.getProperties().put(JAXPStreamFeatureReader.LONGITUDE_FIRST, longitudeFirst);
                reader.getProperties().put(JAXPStreamFeatureReader.READ_EMBEDDED_FEATURE_TYPE, true);
                try (FeatureReader ite = reader.readAsStream(file)) {
                    catalog = reader.getFeatureTypes();
                    rootType = ite.getFeatureType();
                } catch (IOException | XMLStreamException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                } finally {
                    reader.dispose();
                }
            }

            featureType = getCollectionSubType(rootType);
        }
        return featureType;
    }

    @Override
    public Optional<FileSet> getFileSet() throws DataStoreException {
        return Optional.of(new FileSet(file));
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
                MAINLOCK.readLock().lock();
                ite = reader.readAsStream(file);
            } catch (IOException | XMLStreamException ex) {
                MAINLOCK.readLock().unlock();
                reader.dispose();
                throw new DataStoreException(ex.getMessage(),ex);
            } finally {
                //do not dispose, the iterator is closeable and will close the reader
                //reader.dispose();
            }

            final Spliterator<Feature> spliterator = Spliterators.spliteratorUnknownSize(ite, Spliterator.ORDERED);
            final Stream<Feature> stream = StreamSupport.stream(spliterator, false);
            return stream
                    .onClose(ite::close)
                    .onClose(MAINLOCK.readLock()::unlock);
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
            while (features.hasNext()) {
                final Feature feature = features.next();
                if (!type.isAssignableFrom(feature.getType())) {
                    throw new DataStoreException(feature.getType().getName() + " is not of type " +type.getName());
                }
                lst.add(feature);
            }
            final FeatureSet newfs = new InMemoryFeatureSet(type, lst);
            final FeatureSet all = ConcatenatedFeatureSet.create(this, newfs);

            try {
                UPDATELOCK.writeLock().lock();

                // write
                writer.write(all, tempFile);

                // replace files
                try {
                    MAINLOCK.writeLock().lock();
                    Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
                } finally {
                    MAINLOCK.writeLock().unlock();
                }
            } finally {
                UPDATELOCK.writeLock().unlock();
            }

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
    public void removeIf(Predicate<? super Feature> filter) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    @Override
    public void replaceIf(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) throws DataStoreException {
        throw new DataStoreException("Not supported yet.");
    }

    /**
     * Determinate if give type is a FeatureCollection type.
     * @return the children collection feature type or base feature type if type is not a collection
     */
    private static FeatureType getCollectionSubType(FeatureType featureType) {
        boolean isCollectionType = false;

        //check if type is a Feature Collection, for GML up to version 3.2.1
        if (isGmlCollectionType(featureType)) {
            //TODO which property is the sub feature type ?
            return featureType;
        }

        //check if an attribute is a gml feature member, from GML version 3.0
        FeatureType subType = featureType;
        if (!isCollectionType) {
            Collection<? extends PropertyType> properties = featureType.getProperties(true);
            for (PropertyType property : properties) {
                if (property instanceof FeatureAssociationRole) {
                    final FeatureAssociationRole far = (FeatureAssociationRole) property;
                    final FeatureType valueType = far.getValueType();
                    if (isGmlCollectionMemberType(valueType)) {
                        subType = valueType;
                    }
                }
            }
        }

        //not a collection type
        return subType;
    }

    private static boolean isGmlCollectionType(FeatureType type) {
        final String name = type.getName().tip().toString();
        if ("AbstractFeatureCollectionType".equals(name) || "FeatureCollection".equals(name)) {
            return true;
        }
        for (FeatureType ft : type.getSuperTypes()) {
            if (isGmlCollectionType(ft)) return true;
        }
        return false;
    }

    private static boolean isGmlCollectionMemberType(FeatureType type) {
        final String name = type.getName().tip().toString();
        if ("AbstractFeatureMemberType".equals(name)) {
            return true;
        }
        for (FeatureType ft : type.getSuperTypes()) {
            if (isGmlCollectionMemberType(ft)) return true;
        }
        return false;
    }
}
