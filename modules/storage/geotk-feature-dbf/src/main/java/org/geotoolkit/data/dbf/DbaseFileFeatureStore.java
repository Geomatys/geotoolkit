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
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.dbf.DbaseFileReader.Row;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.feature.DefaultFeature;
import org.geotoolkit.storage.DataFileStore;
import org.geotoolkit.feature.Feature;
import static org.geotoolkit.feature.FeatureUtilities.defaultProperty;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.storage.DataStores;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;

/**
 * DBF DataStore, holds a single feature type.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DbaseFileFeatureStore extends AbstractFeatureStore implements DataFileStore {

    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();
    private final ReadWriteLock TempLock = new ReentrantReadWriteLock();

    private final Path file;
    private String name;

    private FeatureType featureType;

    /**
     * @deprecated use {@link #DbaseFileFeatureStore(Path, String)} instead
     */
    public DbaseFileFeatureStore(final File f, final String namespace) throws MalformedURLException, DataStoreException{
        this(f.toPath(), namespace);
    }

    public DbaseFileFeatureStore(final Path f, final String namespace) throws MalformedURLException, DataStoreException{
        this(toParameters(f, namespace));
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

    private static ParameterValueGroup toParameters(final Path f,
            final String namespace) throws MalformedURLException{
        final ParameterValueGroup params = DbaseFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(DbaseFeatureStoreFactory.PATH, params).setValue(f.toUri());
        Parameters.getOrCreate(DbaseFeatureStoreFactory.NAMESPACE, params).setValue(namespace);
        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureStoreFactory getFactory() {
        return (FeatureStoreFactory) DataStores.getFactoryById(DbaseFeatureStoreFactory.NAME);
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
            final String defaultNs = getDefaultNamespace();
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(defaultNs, name);
            ftb.addAll(header.createDescriptors(defaultNs));
            return ftb.buildFeatureType();
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
    public FeatureType getFeatureType(final GenericName typeName) throws DataStoreException {
        typeCheck(typeName); //raise error is type doesnt exist
        return featureType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        typeCheck(query.getTypeName()); //raise error is type doesnt exist

        final Hints hints = query.getHints();
        final Boolean detached = (hints == null) ? null : (Boolean) hints.get(HintsPending.FEATURE_DETACHED);

        final FeatureReader fr = new DBFFeatureReader(detached != null && !detached);
        return handleRemaining(fr, query);
    }


    ////////////////////////////////////////////////////////////////////////////
    // FALLTHROUGHT OR NOT IMPLEMENTED /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public FeatureWriter getFeatureWriter(final GenericName typeName,
    final Filter filter, final Hints hints) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public void createFeatureType(final GenericName typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Schema creation not supported");
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public void deleteFeatureType(final GenericName typeName) throws DataStoreException {
        throw new DataStoreException("Schema deletion not supported");
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public void updateFeatureType(final GenericName typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Schema update not supported");
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public QueryCapabilities getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FeatureId> addFeatures(final GenericName groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures,hints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateFeatures(final GenericName groupName, final Filter filter, final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFeatures(final GenericName groupName, final Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    @Override
    public Path[] getDataFiles() throws DataStoreException {
        return new Path[] { this.file };
    }

    private class DBFFeatureReader implements FeatureReader{

        protected final DbaseFileReader reader;
        protected final DefaultFeature reuse;
        protected final String[] attNames;
        protected Feature current = null;
        protected int inc = 0;

        private DBFFeatureReader(final boolean reuseFeature) throws DataStoreException{
            RWLock.readLock().lock();
            if(reuseFeature){
                reuse = defaultFeature(featureType, "");
            }else{
                reuse = null;
            }

            try (SeekableByteChannel sbc = Files.newByteChannel(file, StandardOpenOption.READ)){
                reader = new DbaseFileReader(sbc, true, null);
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            }
            
            final Collection<PropertyDescriptor> descs = featureType.getDescriptors();
            attNames = new String[descs.size()];
            int i=0;
            for(PropertyDescriptor pd : descs){
                attNames[i] = pd.getName().toString();
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
                
                if(reuse!=null){
                    reuse.setIdentifier(new DefaultFeatureId(String.valueOf(inc++)));
                    current = reuse;
                }else{
                    current = defaultFeature(featureType, String.valueOf(inc++));
                }
                
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

    private static DefaultFeature defaultFeature(final FeatureType type, final String id){
        final Collection<Property> props = new ArrayList<Property>();
        for(final PropertyDescriptor subDesc : type.getDescriptors()){
            for(int i=0,n=subDesc.getMinOccurs();i<n;i++){
                final Property prop = defaultProperty(subDesc);
                if(prop != null){
                    props.add(prop);
                }
            }
        }
        return new DefaultFeature(props, type, new DefaultFeatureId(id));
    }
    
}
