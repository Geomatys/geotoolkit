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
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.simple.DefaultSimpleFeature;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;

/**
 * DBF DataStore, holds a single feature type.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DbaseFileDataStore extends AbstractDataStore{

    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();
    private final ReadWriteLock TempLock = new ReentrantReadWriteLock();

    private final File file;
    private String name;

    private SimpleFeatureType featureType;

    public DbaseFileDataStore(final File f, final String namespace, final String name){
        super(namespace);
        this.file = f;
        this.name = name;
    }

    private synchronized void checkExist() throws DataStoreException{
        if(featureType != null) return;

        try{
            RWLock.readLock().lock();
            if(file.exists()){
                featureType = readType();
            }
        }finally{
            RWLock.readLock().unlock();
        }
    }

    private File createWriteFile() throws MalformedURLException{
        return (File) IOUtilities.changeExtension(file, "wdbf");
    }

    private SimpleFeatureType readType() throws DataStoreException{
        RandomAccessFile raf = null;
        try{
            raf = new RandomAccessFile(file, "r");
            final DbaseFileReader reader = new DbaseFileReader(raf.getChannel(), true, null);
            final DbaseFileHeader header = reader.getHeader();
            final int nbFields = header.getNumFields();

            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            final String defaultNs = getDefaultNamespace();
            ftb.setName(defaultNs, name);
            for(int i=0; i<nbFields; i++){
                final String name = header.getFieldName(i);
                final Class type = header.getFieldClass(i);
                ftb.add(new DefaultName(defaultNs, name), type);
            }
            return ftb.buildSimpleFeatureType();
        }catch(IOException ex){
            throw new DataStoreException(ex);
        }finally{
            if(raf != null){
                try {
                    raf.close();
                } catch (IOException ex) {
                    //we tryed
                    getLogger().log(Level.WARNING, "Could not close file read stream",ex);
                }
            }
        }
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        checkExist();
        if(featureType != null){
            return Collections.singleton(featureType.getName());
        }else{
            return Collections.emptySet();
        }
    }

    @Override
    public FeatureType getFeatureType(final Name typeName) throws DataStoreException {
        typeCheck(typeName); //raise error is type doesnt exist
        return featureType;
    }

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

    @Override
    public FeatureWriter getFeatureWriter(final Name typeName, final Filter filter) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    @Override
    public void createSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Schema creation not supported");
    }

    @Override
    public void deleteSchema(final Name typeName) throws DataStoreException {
        throw new DataStoreException("Schema deletion not supported");
    }

    @Override
    public void updateSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Schema update not supported");
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<FeatureId> addFeatures(final Name groupName, final Collection<? extends Feature> newFeatures) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures);
    }

    @Override
    public void updateFeatures(final Name groupName, final Filter filter, final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    @Override
    public void removeFeatures(final Name groupName, final Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    private class DBFFeatureReader implements FeatureReader<FeatureType, Feature>{

        private final RandomAccessFile raf;
        protected final DbaseFileReader reader;
        protected final SimpleFeatureBuilder sfb;
        protected final DefaultSimpleFeature reuse;
        protected SimpleFeature current = null;
        protected int inc = 0;

        private DBFFeatureReader(final boolean reuseFeature) throws DataStoreException{
            RWLock.readLock().lock();
            sfb = new SimpleFeatureBuilder(featureType);
            if(reuseFeature){
                reuse = new DefaultSimpleFeature(featureType, null, new Object[featureType.getAttributeCount()], false);
            }else{
                reuse = null;
            }

            try {
                raf = new RandomAccessFile(file, "r");
                reader = new DbaseFileReader(raf.getChannel(), true, null);
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            }
        }

        @Override
        public FeatureType getFeatureType() {
            return featureType;
        }

        @Override
        public SimpleFeature next() throws DataStoreRuntimeException {
            read();
            final SimpleFeature ob = current;
            current = null;
            if(ob == null){
                throw new DataStoreRuntimeException("No more records.");
            }
            return ob;
        }

        @Override
        public boolean hasNext() throws DataStoreRuntimeException {
            read();
            return current != null;
        }

        private void read() throws DataStoreRuntimeException{
            if(current != null) return;
            if(!reader.hasNext()) return;

            try{
                if(reuse != null){
                    reuse.setAttributes(reader.readEntry());
                    reuse.setId(String.valueOf(inc++));
                    current = reuse;
                }else{
                    sfb.reset();
                    sfb.addAll(reader.readEntry());
                    current = sfb.buildFeature(Integer.toString(inc++));
                }
            }catch(IOException ex){
                throw new DataStoreRuntimeException(ex);
            }

        }

        @Override
        public void close() {
            RWLock.readLock().unlock();
            try {
                reader.close();
                raf.close();
            } catch (IOException ex) {
                getLogger().log(Level.WARNING, ex.getLocalizedMessage(), ex);
            }
        }

        @Override
        public void remove() {
            throw new DataStoreRuntimeException("Not supported on reader.");
        }

    }

}
