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

import org.geotoolkit.data.gpx.xml.GPXWriter100;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.xml.stream.XMLStreamException;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.gpx.model.MetaData;
import org.geotoolkit.data.gpx.xml.GPXConstants;
import org.geotoolkit.data.gpx.xml.GPXReader;
import org.geotoolkit.data.gpx.xml.GPXWriter110;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;

import static org.geotoolkit.data.gpx.model.GPXModelConstants.*;

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
public class GPXDataStore extends AbstractDataStore{

    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();
    private final ReadWriteLock TempLock = new ReentrantReadWriteLock();

    private final File file;

    public GPXDataStore(final File f){
        super(null);
        this.file = f;
    }

    public MetaData getGPXMetaData() throws DataStoreException{
        if(file.exists()){            
            try {
                RWLock.readLock().lock();
                final GPXReader reader = new GPXReader();
                final MetaData data = reader.getMetadata();
                reader.dispose();
                return data;
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            } catch (XMLStreamException ex) {
                throw new DataStoreException(ex);
            }finally{
                RWLock.readLock().unlock();
            }
        }else{
            return null;
        }
    }

    private File createWriteFile() throws MalformedURLException{
        return (File) IOUtilities.changeExtension(file, "wgpx");
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        final Set<Name> names = new HashSet<Name>();
        names.add(TYPE_GPX_ENTITY.getName());
        names.add(TYPE_WAYPOINT.getName());
        names.add(TYPE_ROUTE.getName());
        names.add(TYPE_TRACK.getName());
        return names;
    }

    @Override
    public FeatureType getFeatureType(final Name typeName) throws DataStoreException {
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
    public boolean isWritable(Name typeName) throws DataStoreException {
        typeCheck(typeName);
        return file.canWrite() && getFeatureType(typeName) != TYPE_GPX_ENTITY;
    }


    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        final FeatureType ft = getFeatureType(query.getTypeName());
        final FeatureReader fr = new GPXFeatureReader(ft);
        return handleRemaining(fr, query);
    }

    @Override
    public FeatureWriter getFeatureWriter(final Name typeName, final Filter filter) throws DataStoreException {
        final FeatureType ft = getFeatureType(typeName);
        final FeatureWriter fw = new GPXFeatureWriter(ft);
        return handleRemaining(fw, filter);
    }

    ////////////////////////////////////////////////////////////////////////////
    // FALLTHROUGHT OR NOT IMPLEMENTED /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public QueryCapabilities getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("New schema creation not allowed on GPX files.");
    }

    @Override
    public void deleteSchema(final Name typeName) throws DataStoreException {
        throw new DataStoreException("Delete schema not allowed on GPX files.");
    }

    @Override
    public void updateSchema(final Name typeName, final FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Update schema not allowed on GPX files.");
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


    private class GPXFeatureReader implements FeatureReader<FeatureType, Feature>{

        protected final FeatureType restriction;
        protected final GPXReader reader;
        protected Feature current = null;

        private GPXFeatureReader(final FeatureType restriction) throws DataStoreException{
            RWLock.readLock().lock();
            this.restriction = restriction;

            if(file.exists()){
                reader = new GPXReader();
                try {
                    reader.setInput(file);
                } catch (IOException ex) {
                    throw new DataStoreException(ex);
                } catch (XMLStreamException ex) {
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
        public Feature next() throws DataStoreRuntimeException {
            read();
            final Feature ob = current;
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
                throw new DataStoreRuntimeException(ex);
            }
            current = null;
        }

        @Override
        public void close() {
            RWLock.readLock().unlock();
            if(reader != null){
                try {
                    reader.dispose();
                } catch (IOException ex) {
                    throw new DataStoreRuntimeException(ex);
                } catch (XMLStreamException ex) {
                    throw new DataStoreRuntimeException(ex);
                }
            }
        }

        @Override
        public void remove() {
            throw new DataStoreRuntimeException("Not supported on reader.");
        }

    }

    private class GPXFeatureWriter extends GPXFeatureReader implements FeatureWriter<FeatureType, Feature>{

        private final FeatureType writeRestriction;
        private final GPXWriter100 writer;
        private final File writeFile;
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
                if (!writeFile.exists()) {
                    writeFile.createNewFile();
                }

                switch(reader.getVersion()){
                    case v1_0_0: writer = new GPXWriter100("Geotoolkit.org");break;
                    default: writer = new GPXWriter110("Geotoolkit.org");break;
                }

                writer.setOutput(writeFile);
                writer.writeStartDocument();
                writer.writeGPXTag();
                writer.write(reader.getMetadata());
            }catch(IOException ex){
                throw new DataStoreException(ex);
            }catch(XMLStreamException ex){
                throw new DataStoreException(ex);
            }
        }

        @Override
        public boolean hasNext() throws DataStoreRuntimeException {
            findNext();
            return next != null;
        }

        @Override
        public Feature next() throws DataStoreRuntimeException {
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
                    throw new DataStoreRuntimeException("Writer append not allowed "
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
        public void write() throws DataStoreRuntimeException {
            if(edited == null || lastWritten == edited) return;
            lastWritten = edited;
            write(edited);
        }

        private void write(final Feature feature) throws DataStoreRuntimeException {
            final FeatureType ft = feature.getType();

            try{
                if(ft == TYPE_WAYPOINT){
                    writer.writeWayPoint(feature, GPXConstants.TAG_WPT);
                }else if(ft == TYPE_ROUTE){
                    writer.writeRoute(feature);
                }else if(ft == TYPE_TRACK){
                    writer.writeTrack(feature);
                }else{
                    throw new DataStoreRuntimeException("Writer not allowed on GPX "
                            + "entity writer, choose a defined type." + ft.getName());
                }
            }catch(XMLStreamException ex){
                throw new DataStoreRuntimeException(ex);
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
            } catch (IOException ex) {
                throw new DataStoreRuntimeException(ex);
            } catch (XMLStreamException ex) {
                throw new DataStoreRuntimeException(ex);
            }

            //close read iterator
            super.close();

            //flip files
            RWLock.writeLock().lock();
            try{
                file.delete();
                writeFile.renameTo(file);
            }finally{
                RWLock.writeLock().unlock();
            }

            TempLock.writeLock().unlock();
        }

    }

}
