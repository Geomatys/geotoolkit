/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.observation.feature;

import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.storage.base.StoreResource;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.observation.AbstractFilteredObservationStore;
import org.geotoolkit.observation.ObservationStore;
import org.geotoolkit.observation.model.ObservationDataset;
import org.geotoolkit.observation.query.DatasetQuery;
import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class SensorFeatureSet extends AbstractFeatureSet implements StoreResource, FeatureSet {

    private final FeatureType type;

    private final ObservationStore store;

    public SensorFeatureSet(ObservationStore originator, FeatureType type) {
        super(null, false);
        this.type = type;
        this.store = originator;
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return type;
    }

    @Override
    public DataStore getOriginator() {
        return (DataStore) store;
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final FeatureType sft = getType();
        final CloseableIterator<Feature> reader;
        if (store instanceof AbstractFilteredObservationStore) {
            reader = new SensorFeatureFilteredReader(store.getFilter(), type);
        } else {
            reader = new SensorFeatureReader(store, type);
        }
        final Spliterator<Feature> spliterator = Spliterators.spliteratorUnknownSize(reader, Spliterator.ORDERED);
        final Stream<Feature> stream = StreamSupport.stream(spliterator, false);
        return stream.onClose(reader::close);
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        ObservationDataset dataset = store.getDataset(new DatasetQuery());
        return dataset.spatialBound.getEnvelope();
    }

}
