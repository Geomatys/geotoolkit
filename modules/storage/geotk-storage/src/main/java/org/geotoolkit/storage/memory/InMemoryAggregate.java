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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.sis.internal.storage.AbstractResource;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.WritableAggregate;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.storage.event.StoreListeners;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.event.AggregationEvent;
import org.geotoolkit.storage.coverage.DefiningCoverageResource;
import org.geotoolkit.storage.coverage.DefiningPyramidResource;
import org.opengis.feature.Feature;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class InMemoryAggregate extends AbstractResource implements WritableAggregate {

    private final StoreListeners listeners = new StoreListeners(null, this);
    private final List<Resource> resources = new ArrayList<>();
    private GenericName identifier;

    public InMemoryAggregate() {
        super(null);
    }

    public InMemoryAggregate(GenericName identifier) {
        super(null);
        this.identifier = identifier;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.ofNullable(identifier);
    }

    @Override
    public Collection<? extends Resource> components() throws DataStoreException {
        return Collections.unmodifiableList(resources);
    }

    @Override
    public synchronized Resource add(Resource resource) throws DataStoreException {

        Resource newr;
        if (resource instanceof FeatureSet) {
            final FeatureSet fs = (FeatureSet) resource;
            final InMemoryFeatureSet newres = new InMemoryFeatureSet(fs.getType());
            try (Stream<Feature> stream = fs.features(false)) {
                newres.add(stream.iterator());
            }
            newr = newres;

        } else if (resource instanceof DefiningPyramidResource) {
            final DefiningPyramidResource cr = (DefiningPyramidResource) resource;
            final GenericName name = cr.getIdentifier().orElse(null);
            newr = new InMemoryPyramidResource(name);

        } else if (resource instanceof GridCoverageResource && resource instanceof MultiResolutionResource) {
            final GridCoverageResource cr = (GridCoverageResource) resource;
            final GenericName name = cr.getIdentifier().orElse(null);
            newr = new InMemoryPyramidResource(name);

        } else if (resource instanceof GridCoverageResource) {
            final GridCoverageResource cr = (GridCoverageResource) resource;
            final GenericName name = cr.getIdentifier().orElse(null);
            final InMemoryGridCoverageResource newres = new InMemoryGridCoverageResource(name);
            newres.write(cr.read(null));
            newr = newres;

        } else if (resource instanceof DefiningCoverageResource) {
            final DefiningCoverageResource cr = (DefiningCoverageResource) resource;
            final GenericName name = cr.getIdentifier().orElse(null);
            newr = new InMemoryGridCoverageResource(name);

        } else if (resource instanceof Aggregate) {
            final Aggregate agg = (Aggregate) resource;
            final InMemoryAggregate newres = new InMemoryAggregate(agg.getIdentifier().orElse(null));
            for (Resource r : agg.components()) {
                newres.add(r);
            }
            newr = newres;

        } else {
            throw new DataStoreException("Unsupported resource type "+ resource);
        }

        resources.add(newr);
        listeners.fire(new AggregationEvent(this, AggregationEvent.TYPE_ADD, newr), AggregationEvent.class);
        return newr;
    }

    @Override
    public void remove(Resource resource) throws DataStoreException {
        if (!resources.remove(resource)) {
            throw new DataStoreException("Resource not found");
        }
        listeners.fire(new AggregationEvent(this, AggregationEvent.TYPE_REMOVE, resource), AggregationEvent.class);
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
        listeners.addListener(eventType, listener);
    }

    @Override
    public synchronized <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
        listeners.removeListener(eventType, listener);
    }

}
