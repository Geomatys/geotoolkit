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

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
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

    public GPXDataStore(File f){
        this.file = f;
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
    public FeatureType getFeatureType(Name typeName) throws DataStoreException {
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
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        throw new DataStoreException("Not yet.");
    }

    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws DataStoreException {
        throw new DataStoreException("Not yet.");
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

}
