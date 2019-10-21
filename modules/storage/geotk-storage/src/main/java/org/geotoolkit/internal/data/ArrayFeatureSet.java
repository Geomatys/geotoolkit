/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.internal.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.storage.WritableFeatureSet;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.data.query.QueryFeatureSet;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;

/**
 * FeatureSet implementation stored in memory.
 *
 * <p>
 * Note-1 : This implementation is read-only for now but will become writable.
 * </p>
 * <p>
 * Note-2 : this class is experimental and should be moved to SIS when ready.
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 */
@Deprecated
public class ArrayFeatureSet implements WritableFeatureSet {

    private final Metadata metadata;
    private final FeatureType type;
    private final List<Feature> features;
    private GenericName id;

    public ArrayFeatureSet(String id, FeatureType type) {
        this((id != null) ? NamesExt.create(id) : null, type, new ArrayList<>(), null);
    }

    /**
     *
     * @param type stored features type.
     * @param features collection of stored features, this collection will be copied.
     * @param metadata can be null
     */
    public ArrayFeatureSet(FeatureType type, Collection<Feature> features, Metadata metadata) {
        this(null, type, new ArrayList<>(features), metadata);
    }

    /**
     *
     * @param type stored features type.
     * @param features collection of stored features, this list will not be copied.
     * @param metadata can be null
     */
    public ArrayFeatureSet(FeatureType type, List<Feature> features, Metadata metadata) {
        this.metadata = metadata;
        this.type = type;
        this.features = features;
    }

    /**
     *
     * @param id featureSet resource identifier
     * @param type stored features type.
     * @param features collection of stored features, this list will not be copied.
     * @param metadata can be null
     */
    public ArrayFeatureSet(GenericName id, FeatureType type, List<Feature> features, Metadata metadata) {
        this.id = id;
        this.metadata = metadata;
        this.type = type;
        this.features = features;
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        if (id != null) return Optional.of(id);
        return Optional.of(type.getName());
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return type;
    }

    @Override
    public Stream<Feature> features(boolean bln) throws DataStoreException {
        Stream<Feature> str = bln ? features.parallelStream() : features.stream();
        str = str.map(FeatureExt::deepCopy);
        return str;
    }

    @Override
    public FeatureSet subset(Query query) throws UnsupportedQueryException, DataStoreException {
        if (query instanceof org.geotoolkit.data.query.Query) {
            return QueryFeatureSet.apply(this, (org.geotoolkit.data.query.Query)query);
        }
        return WritableFeatureSet.super.subset(query);
    }

    /**
     * Envelope is not stored or computed.
     *
     * @return always empty
     */
    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.empty();
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return metadata;
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
    }

    @Override
    public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
    }

    @Override
    public void updateType(FeatureType newType) throws DataStoreException {
        throw new DataStoreException("Not supported.");
    }

    @Override
    public void add(Iterator<? extends Feature> features) {
        while (features.hasNext()) {
            this.features.add(features.next());
        }
    }

    @Override
    public boolean removeIf(Predicate<? super Feature> filter) {
        return features.removeIf(filter);
    }

    @Override
    public void replaceIf(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) {
        final ListIterator<Feature> iterator = features.listIterator();
        while (iterator.hasNext()) {
            Feature feature = iterator.next();
            if (filter.test(feature)) {
                Feature changed = updater.apply(feature);
                if (changed == null) {
                    iterator.remove();
                } else {
                    iterator.set(changed);
                }
            }
        }
    }

}
