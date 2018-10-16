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

import java.io.File;
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
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.data.dbf.DbaseFileReader.Row;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * DBF DataStore, holds a single feature type.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DbaseFileFeatureStore extends AbstractFeatureStore implements ResourceOnFileSystem {

    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();
    private final ReadWriteLock TempLock = new ReentrantReadWriteLock();

    private final Path file;
    private String name;

    private FeatureType featureType;

    /**
     * @deprecated use {@link #DbaseFileFeatureStore(Path, String)} instead
     */
    public DbaseFileFeatureStore(final File f) throws MalformedURLException, DataStoreException{
        this(f.toPath());
    }

    public DbaseFileFeatureStore(final Path f) throws MalformedURLException, DataStoreException{
        this(toParameters(f));
    }

    public DbaseFileFeatureStore(final ParameterValueGroup params) throws DataStoreException{
        super(params);

        final URI uri = (URI) params.parameter(DbaseFeatureStoreFactory.PATH.getName().toString()).getValue();
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
        final Parameters params = Parameters.castOrWrap(DbaseFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(DbaseFeatureStoreFactory.PATH).setValue(f.toUri());
        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataStoreFactory getProvider() {
        return DataStores.getFactoryById(DbaseFeatureStoreFactory.NAME);
    }

    @Override
    public GenericName getIdentifier() {
        return null;
    }

    private synchronized void checkExist() throws DataStoreException{
        if(featureType != null) return;

        try{
            RWLock.readLock().lock();
            if(Files.exists(file)){
                featureType = readType();
            }
        }finally{
            RWLock.readLock().unlock();
        }
    }

    private Path createWriteFile() throws MalformedURLException{
        return (Path) IOUtilities.changeExtension(file, "wdbf");
    }

    private FeatureType readType() throws DataStoreException{
        try (SeekableByteChannel sbc = Files.newByteChannel(file, StandardOpenOption.READ)){
            final DbaseFileReader reader = new DbaseFileReader(sbc, true, null);
            final DbaseFileHeader header = reader.getHeader();
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(name);

            ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY).setMinimumOccurs(1).setMaximumOccurs(1);
            final List<AttributeType> fields = header.createDescriptors();
            for(AttributeType at : fields){
                ftb.addAttribute(at);
            }
            return ftb.build();
        }catch(IOException ex){
            throw new DataStoreException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        checkExist();
        if(featureType != null){
            return Collections.singleton(featureType.getName());
        }else{
            return Collections.emptySet();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureType getFeatureType(final String typeName) throws DataStoreException {
        typeCheck(typeName); //raise error is type doesnt exist
        return featureType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.data.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        typeCheck(gquery.getTypeName()); //raise error is type doesnt exist
        final FeatureReader fr = new DBFFeatureReader();
        return FeatureStreams.subset(fr, gquery);
    }


    ////////////////////////////////////////////////////////////////////////////
    // FALLTHROUGHT OR NOT IMPLEMENTED /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public QueryCapabilities getQueryCapabilities() {
        return new DefaultQueryCapabilities(false, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FeatureId> addFeatures(final String groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures,hints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateFeatures(final String groupName, final Filter filter, final Map<String, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFeatures(final String groupName, final Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        return new Path[] { this.file };
    }

    private class DBFFeatureReader implements FeatureReader{

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
        public FeatureType getFeatureType() {
            return featureType;
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
                current.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), ""+inc++);

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
                getLogger().log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
        }

        @Override
        public void remove() {
            throw new FeatureStoreRuntimeException("Not supported on reader.");
        }

    }

    @Override
    public void refreshMetaModel() {
        featureType = null;
    }

}
