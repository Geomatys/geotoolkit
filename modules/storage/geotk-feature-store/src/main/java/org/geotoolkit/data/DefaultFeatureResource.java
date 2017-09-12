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
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.ReadOnlyStorageException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.storage.AbstractFeatureSet;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DefaultFeatureResource extends AbstractFeatureSet implements FeatureSet, FeatureStoreListener {

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
    public FeatureSet subset(Query query) throws DataStoreException {
        if (query==null) return this;
        return new DefaultFeatureResource(store, QueryUtilities.subQuery(this.query, query));
    }

    @Override
    public Stream<Feature> features(boolean parallal) throws DataStoreException {
        final FeatureReader reader = store.getFeatureReader(query);
        final Spliterator<Feature> spliterator = Spliterators.spliteratorUnknownSize((Iterator)reader, Spliterator.ORDERED);
        final Stream<Feature> stream = StreamSupport.stream(spliterator, false);
        return stream.onClose(reader::close);
    }

    /**
     * This method fallback on {@link FeatureStore}
     * @param features
     * @throws ReadOnlyStorageException
     * @throws DataStoreException
     */
    @Override
    public void add(Iterator<? extends Feature> features) throws ReadOnlyStorageException, DataStoreException {
        try (final FeatureWriter writer = store.getFeatureWriter(QueryBuilder.filtered(query.getTypeName(), Filter.EXCLUDE))) {
            while (features.hasNext()) {
                FeatureExt.copy(features.next(), writer.next(), true);
                writer.write();
            }
        }
    }

    @Override
    public boolean removeIf(Predicate<? super Feature> filter) throws ReadOnlyStorageException, DataStoreException {
        ArgumentChecks.ensureNonNull("predicate", filter);
        boolean removed = false;
        try (final FeatureWriter writer = store.getFeatureWriter(query)) {
            while (writer.hasNext()) {
                Feature feature = writer.next();
                if (filter.test(feature)) {
                    writer.remove();
                    removed = true;
                }
            }
        }
        return removed;
    }

    @Override
    public void replaceIf(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) throws ReadOnlyStorageException, DataStoreException {
        ArgumentChecks.ensureNonNull("predicate", filter);
        ArgumentChecks.ensureNonNull("updater", updater);
        try (final FeatureWriter writer = store.getFeatureWriter(query)) {
            while (writer.hasNext()) {
                Feature feature = writer.next();
                if (filter.test(feature)) {
                    feature = updater.apply(feature);
                    if (feature == null) {
                       writer.remove();
                    } else {
                        writer.write();
                    }
                }
            }
        }
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
