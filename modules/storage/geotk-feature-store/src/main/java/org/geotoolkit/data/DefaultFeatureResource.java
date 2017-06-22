/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.data;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.storage.AbstractResource;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DefaultFeatureResource extends AbstractResource implements FeatureResource, FeatureStoreListener {

    private final FeatureStoreListener.Weak weakListener = new StorageListener.Weak(this);
    private final FeatureStore store;
    private final Query query;

    public DefaultFeatureResource(FeatureStore store, GenericName name) throws DataStoreException {
        this(store,QueryBuilder.all(name));
    }

    public DefaultFeatureResource(FeatureStore store, Query query) throws DataStoreException {
        super(store.getFeatureType(query.getTypeName()).getName());
        this.store = store;
        this.query = query;
        weakListener.registerSource(store);
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return store.getFeatureType(query.getTypeName());
    }

    @Override
    public FeatureResource subset(Query query) throws DataStoreException {
        if (query==null) return this;
        return new DefaultFeatureResource(store, QueryUtilities.subQuery(this.query, query));
    }

    @Override
    public Stream<Feature> features() throws DataStoreException {
        final FeatureReader reader = store.getFeatureReader(query);
        final Spliterator<Feature> spliterator = Spliterators.spliteratorUnknownSize((Iterator)reader, Spliterator.ORDERED);
        final Stream<Feature> stream = StreamSupport.stream(spliterator, false);
        return stream.onClose(reader::close);
    }

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void structureChanged(FeatureStoreManagementEvent event){

        //forward events only if the collection is typed and match the type name
        if (NamesExt.match(event.getFeatureTypeName(), query.getTypeName())) {
            sendStructureEvent(event.copy(this));
        }
    }

    /**
     * Forward event to listeners by changing source.
     */
    @Override
    public void contentChanged(final FeatureStoreContentEvent event){
        //forward events only if the collection is typed and match the type name
        if (NamesExt.match(event.getFeatureTypeName(), query.getTypeName())) {
            sendContentEvent(event.copy(this));
        }
    }


}
