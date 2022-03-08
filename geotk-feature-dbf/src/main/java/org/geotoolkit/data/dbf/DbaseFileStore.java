/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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

package org.geotoolkit.data.dbf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
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
import org.geotoolkit.data.dbf.DbaseFileReader.Row;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.feature.FeatureStoreRuntimeException;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * DBF DataStore, holds a single feature type.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DbaseFileStore extends DataStore implements FeatureSet, ResourceOnFileSystem {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.data.dbf");
    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();

    private final Parameters parameters;
    private final Path file;
    private String name;

    private FeatureType featureType;

    public DbaseFileStore(final Path f) throws MalformedURLException, DataStoreException{
        this(toParameters(f));
    }

    public DbaseFileStore(final ParameterValueGroup params) throws DataStoreException{
        this.parameters = Parameters.castOrWrap(params);

        final URI uri = this.parameters.getMandatoryValue(DbaseFileProvider.PATH);
        this.file = Paths.get(uri);

        final String path = uri.toString();
        final int slash = Math.max(0, path.lastIndexOf('/') + 1);
        int dot = path.indexOf('.', slash);
        if (dot < 0) {
            dot = path.length();
        }
        this.name = path.substring(slash, dot);
    }

    private static ParameterValueGroup toParameters(final Path f) throws MalformedURLException{
        final Parameters params = Parameters.castOrWrap(DbaseFileProvider.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(DbaseFileProvider.PATH).setValue(f.toUri());
        return params;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(getType().getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(DbaseFileProvider.NAME);
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
        citation.getIdentifiers().add(NamedIdentifier.castOrCopy(getIdentifier().get()));
        idf.setCitation(citation);
        metadata.setIdentificationInfo(Arrays.asList(idf));
        return metadata;
    }

    @Override
    public void close() throws DataStoreException {
        //do nothing
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        checkExist();
        return featureType;
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final DBFFeatureReader reader = new DBFFeatureReader();
        final Stream<Feature> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(reader, Spliterator.ORDERED), false);
        return stream.onClose(reader::close);
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.empty();
    }

    private synchronized void checkExist() throws DataStoreException{
        if (featureType != null) return;

        try {
            RWLock.readLock().lock();
            if (Files.exists(file)) {
                featureType = readType();
            }
        } finally {
            RWLock.readLock().unlock();
        }
    }

    private FeatureType readType() throws DataStoreException{
        try (SeekableByteChannel sbc = Files.newByteChannel(file, StandardOpenOption.READ)) {
            final DbaseFileReader reader = new DbaseFileReader(sbc, true, null);
            final DbaseFileHeader header = reader.getHeader();
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(name);

            ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY).setMinimumOccurs(1).setMaximumOccurs(1);
            final List<AttributeType> fields = header.createDescriptors();
            for (AttributeType at : fields) {
                ftb.addAttribute(at);
            }
            return ftb.build();
        }catch(IOException ex){
            throw new DataStoreException(ex);
        }
    }

    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        return new Path[] { this.file };
    }

    private class DBFFeatureReader implements Iterator<Feature>, AutoCloseable {

        protected final DbaseFileReader reader;
        protected final String[] attNames;
        protected Feature current = null;
        protected int inc = 0;

        private DBFFeatureReader() throws DataStoreException{
            RWLock.readLock().lock();

            try (SeekableByteChannel sbc = Files.newByteChannel(file, StandardOpenOption.READ)){
                reader = new DbaseFileReader(sbc, true, null);
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            }

            final Collection<? extends PropertyType> descs = featureType.getProperties(true);
            attNames = new String[descs.size()-1];
            int i=0;
            for(PropertyType pd : descs){
                //skip identifier property
                if(i>0)attNames[i-1] = pd.getName().toString();
                i++;
            }
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            read();
            final Feature ob = current;
            current = null;
            if(ob == null){
                throw new FeatureStoreRuntimeException("No more records.");
            }
            return ob;
        }

        @Override
        public boolean hasNext() throws FeatureStoreRuntimeException {
            read();
            return current != null;
        }

        private void read() throws FeatureStoreRuntimeException{
            if(current != null) return;
            if(!reader.hasNext()) return;

            try{
                final Row row = reader.next();
                final Object[] array = row.readAll(null);

                current = featureType.newInstance();
                current.setPropertyValue(AttributeConvention.IDENTIFIER, ""+inc++);

                for(int i=0;i<array.length;i++){
                    current.setPropertyValue(attNames[i], array[i]);
                }

            }catch(IOException ex){
                throw new FeatureStoreRuntimeException(ex);
            }
        }

        @Override
        public void close() {
            RWLock.readLock().unlock();
            try {
                reader.close();
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
        }

    }

}
