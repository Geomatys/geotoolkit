/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.gpx;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.opengis.util.GenericName;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.DataFileStore;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.sis.internal.storage.gpx.Store;
import org.apache.sis.internal.storage.gpx.Metadata;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.storage.DataStoreFactory;

import org.geotoolkit.storage.DataStores;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.FeatureCatalogueDescription;
import org.opengis.metadata.content.FeatureTypeInfo;


/**
 * GPX DataStore, holds 4 feature types.
 * - One global which match the reading order in the file
 * - One WayPoint
 * - One Routes
 * - One Tracks
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GPXFeatureStore extends AbstractFeatureStore implements DataFileStore {

    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();
    private final ReadWriteLock TempLock = new ReentrantReadWriteLock();

    private static final QueryCapabilities QUERY_CAPABILITIES = new DefaultQueryCapabilities(false);

    private final Path file;

    private final Store reader;

    private final FeatureType rootType;

    /**
     * @deprecated use {@link #GPXFeatureStore(Path)} instead
     */
    @Deprecated
    public GPXFeatureStore(final File f) throws MalformedURLException, DataStoreException{
        this(f.toPath());
    }

    public GPXFeatureStore(final Path f) throws MalformedURLException, DataStoreException{
        this(toParameter(f));
    }

    public GPXFeatureStore(final ParameterValueGroup params) throws DataStoreException {
        super(params);
        final URI uri = (URI) params.parameter(GPXFeatureStoreFactory.PATH.getName().toString()).getValue();
        try {
            this.file = IOUtilities.toPath(uri);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
        reader = new Store(null, new StorageConnector(file));
        rootType = reader.getFeatureType("Route").getSuperTypes().iterator().next();
    }

    private static ParameterValueGroup toParameter(final Path f) throws MalformedURLException{
        final Parameters params = Parameters.castOrWrap(GPXFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(GPXFeatureStoreFactory.PATH).setValue(f.toUri());
        return params;
    }

    @Override
    public DataStoreFactory getProvider() {
        return DataStores.getFactoryById(GPXFeatureStoreFactory.NAME);
    }

    public Metadata getGPXMetaData() throws DataStoreException{
        return (Metadata) reader.getMetadata();
    }

    private Path createWriteFile() throws MalformedURLException{
        return IOUtilities.changeExtension(file, "wgpx");
    }

    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        final Set<GenericName> names = new LinkedHashSet<>();
        for (ContentInformation c : getGPXMetaData().getContentInfo()) {
            for (FeatureTypeInfo f : ((FeatureCatalogueDescription) c).getFeatureTypeInfo()) {
                names.add(f.getFeatureTypeName());
            }
        }
        return names;
    }

    @Override
    public FeatureType getFeatureType(final String typeName) throws DataStoreException {
        return reader.getFeatureType(typeName);
    }

    @Override
    public boolean isWritable(String typeName) throws DataStoreException {
        typeCheck(typeName);
        return Files.isWritable(file) && getFeatureType(typeName) != rootType;
    }


    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        final FeatureType ft = getFeatureType(query.getTypeName());
        final FeatureReader fr = new GPXFeatureReader(ft);
        return FeatureStreams.subset(fr, query);
    }

    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        final FeatureType ft = getFeatureType(query.getTypeName());
        final FeatureWriter fw = new GPXFeatureWriter(ft);
        return FeatureStreams.filter(fw, query.getFilter());
    }

    ////////////////////////////////////////////////////////////////////////////
    // FALLTHROUGHT OR NOT IMPLEMENTED /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return QUERY_CAPABILITIES;
    }

    @Override
    public void createFeatureType(final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("New schema creation not allowed on GPX files.");
    }

    @Override
    public void deleteFeatureType(final String typeName) throws DataStoreException {
        throw new DataStoreException("Delete schema not allowed on GPX files.");
    }

    @Override
    public void updateFeatureType(final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Update schema not allowed on GPX files.");
    }

    @Override
    public List<FeatureId> addFeatures(final String groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures, hints);
    }

    @Override
    public void updateFeatures(final String groupName, final Filter filter, final Map<String, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    @Override
    public void removeFeatures(final String groupName, final Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    @Override
    public Path[] getDataFiles() throws DataStoreException {
        return new Path[] { this.file };
    }

    private class GPXFeatureReader implements FeatureReader {

        private final FeatureType restriction;
        private final Iterator<Feature> features;
        private Feature current;

        private GPXFeatureReader(final FeatureType restriction) throws DataStoreException{
            RWLock.readLock().lock();
            this.restriction = restriction;
            features = ((FeatureSet) reader).features(false).iterator();
        }

        @Override
        public FeatureType getFeatureType() {
            return restriction;
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            read();
            final Feature ob = current;
            current = null;
            if (ob == null) {
                throw new FeatureStoreRuntimeException("No more records.");
            }
            return ob;
        }

        @Override
        public boolean hasNext() throws FeatureStoreRuntimeException {
            read();
            return current != null;
        }

        private void read() throws FeatureStoreRuntimeException {
            if (current != null) return;
            if (!features.hasNext()) return;
            try {
                while(features.hasNext()) {
                    current = features.next();
                    if (restriction == rootType || current.getType() == restriction) {
                        return; //type match
                    }
                }
            } catch (BackingStoreException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            current = null;
        }

        @Override
        public void close() {
            RWLock.readLock().unlock();
        }

        @Override
        public void remove() {
            throw new FeatureStoreRuntimeException("Not supported on reader.");
        }
    }

    private class GPXFeatureWriter extends GPXFeatureReader implements FeatureWriter {

        private final FeatureType writeRestriction;
        private final Store writer;
        private final Path writeFile;
        private Feature next = null;
        private Feature edited = null;
        private Feature lastWritten = null;
        private final List<Feature> features = new ArrayList<>();

        private GPXFeatureWriter(final FeatureType restriction) throws DataStoreException {
            super(rootType);

            if (restriction == rootType) {
                super.close(); //release read lock
                throw new DataStoreException("Writer not allowed on GPX entity writer, choose a defined type.");
            }
            this.writeRestriction = restriction;

            TempLock.writeLock().lock();

            try {
                writeFile = createWriteFile();
                if (!Files.exists(writeFile)) {
                    Files.createFile(writeFile);
                }
                writer = new Store(null, new StorageConnector(writeFile));
                writer.setVersion(reader.getVersion());
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            }
        }

        @Override
        public boolean hasNext() throws FeatureStoreRuntimeException {
            findNext();
            return next != null;
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            write();

            findNext();
            if(next != null){
                edited = next;
                next = null;
                return edited;
            }else{
                //we reach append mode
                if (writeRestriction != rootType) {
                    edited = writeRestriction.newInstance();
                }else{
                    throw new FeatureStoreRuntimeException("Writer append not allowed "
                            + "on GPX entity writer, choose a defined type.");
                }
            }

            return edited;
        }

        private void findNext() {
            if (next != null) return;

            while (next==null && super.hasNext()) {
                final Feature candidate = super.next();
                if (candidate.getType() == writeRestriction) {
                    next = candidate;
                } else {
                    //not the wished type, write it and continue
                    //since all types are store in one file
                    //we must ensure everything is copied
                    write(candidate);
                }
            }
        }

        @Override
        public void write() throws FeatureStoreRuntimeException {
            if (edited == null || lastWritten == edited) return;
            lastWritten = edited;
            write(edited);
        }

        private void write(final Feature feature) throws FeatureStoreRuntimeException {
            features.add(feature);
        }

        @Override
        public void close() {
            // write everything remaining if any
            while (hasNext()) {
                next();
            }
            write();
            try {
                writer.write(reader.getMetadata(), features.stream());
                writer.close();
            } catch (DataStoreException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            // close read iterator
            super.close();

            // flip files
            RWLock.writeLock().lock();
            try {
                Files.move(writeFile, file, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new FeatureStoreRuntimeException(ex);
            } finally{
                RWLock.writeLock().unlock();
            }
            TempLock.writeLock().unlock();
        }
    }

    @Override
    public void refreshMetaModel() {
    }
}
