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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.internal.storage.query.SimpleQuery;
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
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.storage.WritableFeatureSet;
import org.geotoolkit.storage.feature.FeatureReader;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
import org.geotoolkit.storage.FeatureCatalogue;
import org.geotoolkit.storage.feature.GenericNameIndex;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.nio.PosixDirectoryFilter;
import org.geotoolkit.storage.DataStores;
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
public class GMLSparseStore extends DataStore implements WritableFeatureSet, ResourceOnFileSystem, FeatureCatalogue {

    private final Parameters parameters;
    private final Path file;
    private GenericNameIndex<FeatureType> catalog;

    private FeatureType featureType;
    private final Map<String,String> schemaLocations = new HashMap<>();
    private String gmlVersion = "3.2.1";

    private Boolean longitudeFirst;

    public GMLSparseStore(final File f) throws MalformedURLException, DataStoreException{
        this(f.toPath(),null,null);
    }

    @Deprecated
    public GMLSparseStore(final File f,String xsd, String typeName) throws MalformedURLException, DataStoreException{
        this(toParameters(f.toPath(),xsd,typeName));
    }

    public GMLSparseStore(final Path f,String xsd, String typeName) throws MalformedURLException, DataStoreException{
        this(toParameters(f,xsd,typeName));
    }

    public GMLSparseStore(final ParameterValueGroup params) throws DataStoreException {
        parameters = Parameters.unmodifiable(params);

        final URI uri = (URI) params.parameter(GMLProvider.PATH.getName().toString()).getValue();
        this.file = Paths.get(uri);
        this.longitudeFirst = (Boolean) params.parameter(GMLProvider.LONGITUDE_FIRST.getName().toString()).getValue();
    }

    private static ParameterValueGroup toParameters(final Path f,String xsd, String typeName) throws MalformedURLException{
        final Parameters params = Parameters.castOrWrap(GMLProvider.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(GMLProvider.PATH).setValue(f.toUri());
        params.getOrCreate(GMLProvider.SPARSE).setValue(true);
        if (xsd != null) params.getOrCreate(GMLProvider.XSD).setValue(xsd);
        if (typeName != null) params.getOrCreate(GMLProvider.XSD_TYPE_NAME).setValue(typeName);
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
    public Metadata getMetadata() throws DataStoreException {
        final DefaultMetadata metadata = new DefaultMetadata();
        final DefaultDataIdentification idf = new DefaultDataIdentification();
        final DefaultCitation citation = new DefaultCitation();
        getIdentifier()
                .map(NamedIdentifier::castOrCopy)
                .ifPresent(citation.getIdentifiers()::add);
        idf.setCitation(citation);
        metadata.setIdentificationInfo(Arrays.asList(idf));
        return metadata;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.empty();
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
                } catch (MalformedURLException | JAXBException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            } else {
                final JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader();
                reader.getProperties().put(JAXPStreamFeatureReader.LONGITUDE_FIRST, longitudeFirst);
                reader.getProperties().put(JAXPStreamFeatureReader.READ_EMBEDDED_FEATURE_TYPE, true);
                try {
                    if (Files.isDirectory(file)) {
                        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(file, new PosixDirectoryFilter("*.gml", true))) {
                            final Iterator<Path> gmlPaths = directoryStream.iterator();
                            // get first gml file only
                            if (gmlPaths.hasNext()) {
                                final Path gmlPath = gmlPaths.next();
                                try (FeatureReader ite = reader.readAsStream(gmlPath)) {
                                    catalog = reader.getFeatureTypes();
                                    featureType = ite.getFeatureType();
                                }
                            }
                        }
                    } else {
                        try (FeatureReader ite = reader.readAsStream(file)) {
                            catalog = reader.getFeatureTypes();
                            featureType = ite.getFeatureType();
                        }
                    }

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
    public FeatureSet subset(Query query) throws UnsupportedQueryException, DataStoreException {
        if (query instanceof SimpleQuery) {
            return ((SimpleQuery) query).execute(this);
        }
        return WritableFeatureSet.super.subset(query);
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final ReadIterator ite = new ReadIterator(featureType, file);
        final Spliterator<Feature> spliterator = Spliterators.spliteratorUnknownSize(ite, Spliterator.ORDERED);
        final Stream<Feature> stream = StreamSupport.stream(spliterator, false);
        return stream.onClose(ite::close);
    }

    @Override
    public void add(Iterator<? extends Feature> features) throws DataStoreException {

        while (features.hasNext()) {
            final Feature feature = features.next();
            final Path currentFile = file.resolve(FeatureExt.getId(feature).getIdentifier()+".gml");

            //write feature
            final JAXPStreamFeatureWriter writer = new JAXPStreamFeatureWriter(gmlVersion,"2.0.0",schemaLocations);
            try {
                writer.write(feature, currentFile);
            } catch (IOException | XMLStreamException | DataStoreException ex) {
                throw new FeatureStoreRuntimeException(ex.getMessage(),ex);
            } finally {
                try {
                    writer.dispose();
                } catch (IOException | XMLStreamException ex) {
                    throw new FeatureStoreRuntimeException(ex.getMessage(),ex);
                }
            }
        }
    }

    @Override
    public boolean removeIf(Predicate<? super Feature> filter) throws DataStoreException {
        boolean changed = false;
        try (WriterIterator writer = new WriterIterator(featureType, file)) {
            while (writer.hasNext()) {
                final Feature f = writer.next();
                if (filter.test(f)) {
                    changed = true;
                    writer.remove();
                }
            }
        } catch (FeatureStoreRuntimeException ex) {
            throw new DataStoreException(ex);
        }
        return changed;
    }

    @Override
    public void replaceIf(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) throws DataStoreException {

        try (WriterIterator writer = new WriterIterator(featureType, file)) {
            while (writer.hasNext()) {
                final Feature f = writer.next();
                if (filter.test(f)) {
                    Feature nw = updater.apply(f);
                    FeatureExt.copy(nw, f, false);
                    writer.write();
                }
            }
        } catch (FeatureStoreRuntimeException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public void updateType(FeatureType newType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void close() throws DataStoreException {
    }

    private class ReadIterator implements Iterator<Feature>, AutoCloseable {

        protected final FeatureType type;
        protected final JAXPStreamFeatureReader xmlReader;
        protected FeatureReader featureReader;
        protected Feature currentFeature = null;
        protected Path currentFile = null;
        protected Feature nextFeature = null;
        protected final List<Path> files = new LinkedList<>();
        protected int index=-1;
        /**
         * @deprecated
         */
        @Deprecated
        public ReadIterator(FeatureType type, File folder) throws DataStoreException {
            this(type, folder.toPath());
        }

        public ReadIterator(FeatureType type, Path folder) throws DataStoreException {
            this.type = type;
            this.xmlReader = new JAXPStreamFeatureReader(type);
            this.xmlReader.getProperties().put(JAXPStreamFeatureReader.LONGITUDE_FIRST, longitudeFirst);
            try {
                if (Files.isDirectory(file)) {
                    this.files.addAll(IOUtilities.listChildren(folder, "*.gml"));
                } else {
                    this.files.add(folder);
                }
            } catch (IOException e) {
                throw new DataStoreException(e.getLocalizedMessage(), e);
            }
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            try {
                findNext();
            } catch (IOException | XMLStreamException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            currentFeature = nextFeature;
            if(currentFeature==null){
                currentFile = null;
                throw new NoSuchElementException("No more features");
            }
            currentFile = files.get(index);
            nextFeature = null;
            return currentFeature;
        }

        @Override
        public boolean hasNext() throws FeatureStoreRuntimeException {
            try {
                findNext();
            } catch (IOException | XMLStreamException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            return nextFeature != null;
        }

        private void findNext() throws IOException, XMLStreamException{
            if(nextFeature!=null) return;

            while(nextFeature==null){
                if(featureReader==null){
                    //get the next file

                    index++;
                    if(index >= files.size()){
                        return;
                    }
                    xmlReader.reset();
                    featureReader = xmlReader.readAsStream(files.get(index));
                }

                if(featureReader.hasNext()){
                    nextFeature = featureReader.next();
                }else{
                    featureReader = null;
                }
            }
        }

        @Override
        public void close() {
            xmlReader.dispose();
        }

    }

    private class WriterIterator extends ReadIterator {

        public WriterIterator(FeatureType type, File folder) throws DataStoreException {
            super(type, folder.toPath());
        }

        public WriterIterator(FeatureType type, Path folder) throws DataStoreException {
            super(type, folder);
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            if(hasNext()){
                return super.next();
            }else{
                //append mode
                currentFeature = type.newInstance();
                currentFeature.setPropertyValue(AttributeConvention.IDENTIFIER, FeatureExt.createDefaultFeatureId());
                currentFile = null;
                return currentFeature;
            }
        }

        @Override
        public void remove() throws FeatureStoreRuntimeException {
            if(currentFile==null){
                throw new IllegalStateException("No current feature to remove.");
            }
            try {
                Files.delete(currentFile);
            } catch (IOException e) {
                throw new FeatureStoreRuntimeException("Unable to delete GML file "+currentFile.toAbsolutePath().toString(), e);
            }
        }

        public void write() throws FeatureStoreRuntimeException {
            if(currentFeature==null){
                throw new IllegalStateException("No current feature to write.");
            }

            if(currentFile==null){
                //append mode
                currentFile = file.resolve(FeatureExt.getId(currentFeature).getIdentifier()+".gml");
            }

            //write feature
            final JAXPStreamFeatureWriter writer = new JAXPStreamFeatureWriter(gmlVersion,"2.0.0",schemaLocations);
            try{
                writer.write(currentFeature, currentFile);
            }catch(IOException | XMLStreamException | DataStoreException ex){
                throw new FeatureStoreRuntimeException(ex.getMessage(),ex);
            } finally{
                try {
                    writer.dispose();
                } catch (IOException | XMLStreamException ex) {
                    throw new FeatureStoreRuntimeException(ex.getMessage(),ex);
                }
            }
        }
    }

}
