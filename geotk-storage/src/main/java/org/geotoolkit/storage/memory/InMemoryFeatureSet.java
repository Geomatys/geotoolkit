/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.storage.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableFeatureSet;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 * FeatureSet implementation stored in memory.
 *
 * @author Johann Sorel (Geomatys)
 */
public class InMemoryFeatureSet extends AbstractFeatureSet implements WritableFeatureSet {

    private final FeatureType type;
    private final List<Feature> features;
    private GenericName id;

    public InMemoryFeatureSet(FeatureType type) {
        this(type.getName(), type, new ArrayList<>());
    }

    public InMemoryFeatureSet(String id, FeatureType type) {
        this((id != null) ? NamesExt.create(id) : null, type, new ArrayList<>());
    }

    /**
     *
     * @param type stored features type.
     * @param features collection of stored features, this collection will be copied.
     */
    public InMemoryFeatureSet(FeatureType type, Collection<Feature> features) {
        this(null, type, new ArrayList<>(features));
    }

    /**
     *
     * @param type stored features type.
     * @param features collection of stored features, this list will not be copied.
     */
    public InMemoryFeatureSet(FeatureType type, List<Feature> features) {
        super(null, false);
        this.type = type;
        this.features = features;
    }

    /**
     *
     * @param id featureSet resource identifier
     * @param type stored features type.
     * @param features collection of stored features, this list will not be copied.
     */
    public InMemoryFeatureSet(GenericName id, FeatureType type, List<Feature> features) {
        super(null, false);
        this.id = id;
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
