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

package org.geotoolkit.data.osm;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
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
import org.geotoolkit.data.osm.xml.OSMXMLReader;
import org.geotoolkit.data.osm.xml.OSMXMLWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.feature.DefaultFeature;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;

import static org.geotoolkit.data.osm.model.OSMModelConstants.*;

/**
 * OSM DataStore, holds 3 feature types.
 * - Node
 * - Way
 * - relation
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OSMMemoryDataStore extends AbstractDataStore{

    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();
    private final ReadWriteLock TempLock = new ReentrantReadWriteLock();

    private final File file;

    public OSMMemoryDataStore(File f){
        this.file = f;
    }

    private File createWriteFile() throws MalformedURLException{
        return (File) IOUtilities.changeExtension(file, "wgpx");
    }

    @Override
    public Set<Name> getNames() throws DataStoreException {
        final Set<Name> names = new HashSet<Name>();
        names.add(TYPE_NODE.getName());
        names.add(TYPE_WAY.getName());
        names.add(TYPE_RELATION.getName());
        return names;
    }

    @Override
    public FeatureType getFeatureType(Name typeName) throws DataStoreException {
        if(TYPE_NODE.getName().equals(typeName)){
            return TYPE_NODE;
        }else if(TYPE_WAY.getName().equals(typeName)){
            return TYPE_WAY;
        }else if(TYPE_RELATION.getName().equals(typeName)){
            return TYPE_RELATION;
        }else{
            throw new DataStoreException("No featureType for name : " + typeName);
        }
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        final FeatureType ft = getFeatureType(query.getTypeName());
        final FeatureReader fr = new OSMFeatureReader(ft);
        return handleRemaining(fr, query);
    }

    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws DataStoreException {
        final FeatureType ft = getFeatureType(typeName);
        final FeatureWriter fw = new OSMFeatureWriter(ft);
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
    public void createSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("New schema creation not allowed on GPX files.");
    }

    @Override
    public void deleteSchema(Name typeName) throws DataStoreException {
        throw new DataStoreException("Delete schema not allowed on GPX files.");
    }

    @Override
    public void updateSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Update schema not allowed on GPX files.");
    }

    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures);
    }

    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }


    private class OSMFeatureReader implements FeatureReader<FeatureType, Feature>{

        protected final FeatureType restriction;
        protected final OSMXMLReader reader;
        protected Feature current = null;

        private OSMFeatureReader(FeatureType restriction) throws DataStoreException{
            RWLock.readLock().lock();
            this.restriction = restriction;

            if(file.exists()){
                reader = new OSMXMLReader();
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
                    final Object candidate = reader.next();
                    //OSM xml reader can return different objects, not only features.
                    if(candidate instanceof Feature){
                        current = (Feature)candidate;

                        if(current.getType() == restriction){
                            return; //type match
                        }
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

    private class OSMFeatureWriter extends OSMFeatureReader implements FeatureWriter<FeatureType, Feature>{

        private final OSMXMLWriter writer;
        private final File writeFile;
        private Feature edited = null;
        private Feature lastWritten = null;

        private OSMFeatureWriter(FeatureType restriction) throws DataStoreException{
            super(restriction);

            TempLock.writeLock().lock();

            try{
                writeFile = createWriteFile();
                if (!writeFile.exists()) {
                    writeFile.createNewFile();
                }
                writer = new OSMXMLWriter();
                writer.setOutput(writeFile);
                writer.writeStartDocument();
                writer.writeOSMTag();
            }catch(IOException ex){
                throw new DataStoreException(ex);
            }catch(XMLStreamException ex){
                throw new DataStoreException(ex);
            }
        }

        @Override
        public Feature next() throws DataStoreRuntimeException {
            try{
                write();
                edited = super.next();
            }catch(DataStoreRuntimeException ex){
                //we reach append mode
                final Collection<Property> properties = new ArrayList<Property>();
                if(restriction == TYPE_NODE){
                    edited = DefaultFeature.create(properties, TYPE_NODE, new DefaultFeatureId(String.valueOf(-1)));
                }else if(restriction == TYPE_WAY){
                    edited = DefaultFeature.create(properties, TYPE_WAY, new DefaultFeatureId(String.valueOf(-1)));
                }else if(restriction == TYPE_RELATION){
                    edited = DefaultFeature.create(properties, TYPE_RELATION, new DefaultFeatureId(String.valueOf(-1)));
                }else{
                    throw new DataStoreRuntimeException("Writer append not allowed on GPX entity writer, choose a defined type.");
                }
            }
            return edited;
        }

        @Override
        public void write() throws DataStoreRuntimeException {
            throw new DataStoreRuntimeException("not supported yet.");
//            if(edited == null || lastWritten == edited) return;
//            lastWritten = edited;
//
//            try{
//                if(restriction == TYPE_NODE){
//                    writer.writeWayPoint(edited, GPXConstants.TAG_WPT);
//                }else if(restriction == TYPE_WAY){
//                    writer.writeRoute(edited);
//                }else if(restriction == TYPE_RELATION){
//                    writer.writeTrack(edited);
//                }else{
//                    throw new DataStoreRuntimeException("Writer not allowed on GPX entity writer, choose a defined type.");
//                }
//            }catch(XMLStreamException ex){
//                throw new DataStoreRuntimeException(ex);
//            }

        }

        @Override
        public void close() {

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
                TempLock.writeLock().unlock();
            }
        }

    }


}
