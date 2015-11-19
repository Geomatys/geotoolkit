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
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.gpx.model.MetaData;
import org.geotoolkit.data.gpx.xml.GPXConstants;
import org.geotoolkit.data.gpx.xml.GPXReader;
import org.geotoolkit.data.gpx.xml.GPXWriter100;
import org.geotoolkit.data.gpx.xml.GPXWriter110;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.type.FeatureType;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataFileStore;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.geotoolkit.data.gpx.model.GPXModelConstants.TYPE_GPX_ENTITY;
import static org.geotoolkit.data.gpx.model.GPXModelConstants.TYPE_ROUTE;
import static org.geotoolkit.data.gpx.model.GPXModelConstants.TYPE_TRACK;
import static org.geotoolkit.data.gpx.model.GPXModelConstants.TYPE_WAYPOINT;

/**
 * GPX DataStore, holds 4 feature types.
 * - One global which match the reading order in the file
 * - One WayPoint
 * - One Routes
 * - One Tracks
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class GPXFeatureStore extends AbstractFeatureStore implements DataFileStore {

    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();
    private final ReadWriteLock TempLock = new ReentrantReadWriteLock();

    private static final QueryCapabilities QUERY_CAPABILITIES = new DefaultQueryCapabilities(false);

    private final Path file;

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

    public GPXFeatureStore(final ParameterValueGroup params) throws DataStoreException{
        super(params);
        final URL url = (URL) params.parameter(GPXFeatureStoreFactory.URLP.getName().toString()).getValue();
        try {
            this.file = IOUtilities.toPath(url);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    private static ParameterValueGroup toParameter(final Path f) throws MalformedURLException{
        final ParameterValueGroup params = GPXFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(GPXFeatureStoreFactory.URLP, params).setValue(f.toUri().toURL());
        return params;
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(GPXFeatureStoreFactory.NAME);
    }

    public MetaData getGPXMetaData() throws DataStoreException{
        if(Files.exists(file)){
            try {
                RWLock.readLock().lock();
                final GPXReader reader = new GPXReader();
                final MetaData data = reader.getMetadata();
                reader.dispose();
                return data;
            } catch (IOException | XMLStreamException ex) {
                throw new DataStoreException(ex);
            } finally{
                RWLock.readLock().unlock();
            }
        }else{
            return null;
        }
    }

    private Path createWriteFile() throws MalformedURLException{
        return IOUtilities.changeExtension(file, "wgpx");
    }

    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        final Set<GenericName> names = new HashSet<>();
        names.add(TYPE_GPX_ENTITY.getName());
        names.add(TYPE_WAYPOINT.getName());
        names.add(TYPE_ROUTE.getName());
        names.add(TYPE_TRACK.getName());
        return names;
    }

    @Override
    public FeatureType getFeatureType(final GenericName typeName) throws DataStoreException {
        if(TYPE_GPX_ENTITY.getName().equals(typeName)){
            return TYPE_GPX_ENTITY;
        }else if(TYPE_WAYPOINT.getName().equals(typeName)){
            return TYPE_WAYPOINT;
        }else if(TYPE_ROUTE.getName().equals(typeName)){
            return TYPE_ROUTE;
        }else if(TYPE_TRACK.getName().equals(typeName)){
            return TYPE_TRACK;
        }else{
            throw new DataStoreException("No featureType for name : " + typeName);
        }
    }

    @Override
    public boolean isWritable(GenericName typeName) throws DataStoreException {
        typeCheck(typeName);
        return Files.isWritable(file) && getFeatureType(typeName) != TYPE_GPX_ENTITY;
    }


    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        final FeatureType ft = getFeatureType(query.getTypeName());
        final FeatureReader fr = new GPXFeatureReader(ft);
        return handleRemaining(fr, query);
    }

    @Override
    public FeatureWriter getFeatureWriter(final GenericName typeName,
            final Filter filter, final Hints hints) throws DataStoreException {
        final FeatureType ft = getFeatureType(typeName);
        final FeatureWriter fw = new GPXFeatureWriter(ft);
        return handleRemaining(fw, filter);
    }

    ////////////////////////////////////////////////////////////////////////////
    // FALLTHROUGHT OR NOT IMPLEMENTED /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return QUERY_CAPABILITIES;
    }

    @Override
    public void createFeatureType(final GenericName typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("New schema creation not allowed on GPX files.");
    }

    @Override
    public void deleteFeatureType(final GenericName typeName) throws DataStoreException {
        throw new DataStoreException("Delete schema not allowed on GPX files.");
    }

    @Override
    public void updateFeatureType(final GenericName typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Update schema not allowed on GPX files.");
    }

    @Override
    public List<FeatureId> addFeatures(final GenericName groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures, hints);
    }

    @Override
    public void updateFeatures(final GenericName groupName, final Filter filter, final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    @Override
    public void removeFeatures(final GenericName groupName, final Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    @Override
    public Path[] getDataFiles() throws DataStoreException {
        return new Path[] { this.file };
    }

    private class GPXFeatureReader implements FeatureReader{

        protected final FeatureType restriction;
        protected final GPXReader reader;
        protected Feature current = null;

        private GPXFeatureReader(final FeatureType restriction) throws DataStoreException{
            RWLock.readLock().lock();
            this.restriction = restriction;

            if(Files.exists(file)){
                reader = new GPXReader();
                try {
                    reader.setInput(file);
                } catch (IOException | XMLStreamException ex) {
                    throw new DataStoreException(ex);
                }
            }else{
                reader = null;
            }

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
            if(reader == null) return;

            try {
                while(reader.hasNext()) {
                    current = reader.next();

                    if(restriction == TYPE_GPX_ENTITY ||
                       current.getType() == restriction){
                        return; //type match
                    }
                }
            } catch (XMLStreamException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            current = null;
        }

        @Override
        public void close() {
            RWLock.readLock().unlock();
            if(reader != null){
                try {
                    reader.dispose();
                } catch (IOException | XMLStreamException ex) {
                    throw new FeatureStoreRuntimeException(ex);
                }
            }
        }

        @Override
        public void remove() {
            throw new FeatureStoreRuntimeException("Not supported on reader.");
        }

    }

    private class GPXFeatureWriter extends GPXFeatureReader implements FeatureWriter{

        private final FeatureType writeRestriction;
        private final GPXWriter100 writer;
        private final Path writeFile;
        private Feature next = null;
        private Feature edited = null;
        private Feature lastWritten = null;

        private GPXFeatureWriter(final FeatureType restriction) throws DataStoreException{
            super(TYPE_GPX_ENTITY);

            if(restriction == TYPE_GPX_ENTITY){
                super.close(); //release read lock
                throw new DataStoreException("Writer not allowed on GPX entity writer, choose a defined type.");
            }
            this.writeRestriction = restriction;

            TempLock.writeLock().lock();

            try{
                writeFile = createWriteFile();

                if (!Files.exists(writeFile)) {
                    Files.createFile(writeFile);
                }

                switch(reader.getVersion()){
                    case v1_0_0: writer = new GPXWriter100("Geotoolkit.org");break;
                    default: writer = new GPXWriter110("Geotoolkit.org");break;
                }

                writer.setOutput(writeFile);
                writer.writeStartDocument();
                writer.writeGPXTag();
                writer.write(reader.getMetadata());
            }catch(IOException | XMLStreamException ex){
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
                if(writeRestriction != TYPE_GPX_ENTITY){
                    edited = FeatureUtilities.defaultFeature(writeRestriction, "-1");
                }else{
                    throw new FeatureStoreRuntimeException("Writer append not allowed "
                            + "on GPX entity writer, choose a defined type.");
                }
            }

            return edited;
        }

        private void findNext() {
            if(next != null) return;

            while(next==null && super.hasNext()){
                final Feature candidate = super.next();

                if(candidate.getType() == writeRestriction){
                    next = candidate;
                }else{
                    //not the wished type, write it and continue
                    //since all types are store in one file
                    //we must ensure everything is copied
                    write(candidate);
                }
            }
        }

        @Override
        public void write() throws FeatureStoreRuntimeException {
            if(edited == null || lastWritten == edited) return;
            lastWritten = edited;
            write(edited);
        }

        private void write(final Feature feature) throws FeatureStoreRuntimeException {
            final FeatureType ft = feature.getType();

            try{
                if(ft == TYPE_WAYPOINT){
                    writer.writeWayPoint(feature, GPXConstants.TAG_WPT);
                }else if(ft == TYPE_ROUTE){
                    writer.writeRoute(feature);
                }else if(ft == TYPE_TRACK){
                    writer.writeTrack(feature);
                }else{
                    throw new FeatureStoreRuntimeException("Writer not allowed on GPX "
                            + "entity writer, choose a defined type." + ft.getName());
                }
            }catch(XMLStreamException ex){
                throw new FeatureStoreRuntimeException(ex);
            }

        }

        @Override
        public void close() {

            //write everything remaining if any
            while(hasNext()){
                next();
            }
            write();

            try {
                writer.writeEndDocument();
                writer.dispose();
            } catch (IOException | XMLStreamException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }

            //close read iterator
            super.close();

            //flip files
            RWLock.writeLock().lock();
            try{
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
		return;

	}

}
