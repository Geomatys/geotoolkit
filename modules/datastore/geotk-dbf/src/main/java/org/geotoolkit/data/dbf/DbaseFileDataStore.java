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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.dbf.DbaseFileReader.Row;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.simple.DefaultSimpleFeature;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;

/**
 * DBF DataStore, holds a single feature type.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DbaseFileDataStore extends AbstractFeatureStore{

    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();
    private final ReadWriteLock TempLock = new ReentrantReadWriteLock();

    private final File file;
    private String name;

    private SimpleFeatureType featureType;

    public DbaseFileDataStore(final File f, final String namespace) throws MalformedURLException, DataStoreException{
        this(toParameters(f, namespace));
    }

    public DbaseFileDataStore(final ParameterValueGroup params) throws DataStoreException{
        super(params);

        final URL url = (URL) params.parameter(DbaseDataStoreFactory.URLP.getName().toString()).getValue();
        try {
            this.file = new File(url.toURI());
        } catch (URISyntaxException ex) {
            throw new DataStoreException(ex);
        }

        final String path = url.toString();
        final int slash = Math.max(0, path.lastIndexOf('/') + 1);
        int dot = path.indexOf('.', slash);
        if (dot < 0) {
            dot = path.length();
        }
        this.name = path.substring(slash, dot);
    }

    private static ParameterValueGroup toParameters(final File f,
            final String namespace) throws MalformedURLException{
        final ParameterValueGroup params = DbaseDataStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(DbaseDataStoreFactory.URLP, params).setValue(f.toURI().toURL());
        Parameters.getOrCreate(DbaseDataStoreFactory.NAMESPACE, params).setValue(namespace);
        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(DbaseDataStoreFactory.NAME);
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
            final String defaultNs = getDefaultNamespace();
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName(defaultNs, name);
            ftb.addAll(header.createDescriptors(defaultNs));
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Name> getNames() throws DataStoreException {
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
    public FeatureType getFeatureType(final Name typeName) throws DataStoreException {
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
    public FeatureWriter getFeatureWriter(final Name typeName,
    final Filter filter, final Hints hints) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public void createSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Schema creation not supported");
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public void deleteSchema(final Name typeName) throws DataStoreException {
        throw new DataStoreException("Schema deletion not supported");
    }

    /**
     * Unsupported, throws a {@link DataStoreException}.
     */
    @Override
    public void updateSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
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
    public List<FeatureId> addFeatures(final Name groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures,hints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateFeatures(final Name groupName, final Filter filter, final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    /**
     * {@inheritDoc}
     */
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
        public SimpleFeature next() throws FeatureStoreRuntimeException {
            read();
            final SimpleFeature ob = current;
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
                if(reuse != null){
                    reuse.setAttributes(row.readAll(null));
                    reuse.setId(String.valueOf(inc++));
                    current = reuse;
                }else{
                    sfb.reset();
                    sfb.addAll(row.readAll(null));
                    current = sfb.buildFeature(Integer.toString(inc++));
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
                raf.close();
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
		featureType=null;
		
	}

}
