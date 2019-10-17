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
package org.geotoolkit.data.memory;

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
import org.geotoolkit.data.multires.MultiResolutionResource;
import org.geotoolkit.storage.coverage.DefiningCoverageResource;
import org.geotoolkit.storage.coverage.DefiningPyramidResource;
import org.opengis.feature.Feature;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class InMemoryAggregate extends AbstractResource implements WritableAggregate {

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
        if (resource instanceof FeatureSet) {
            final FeatureSet fs = (FeatureSet) resource;
            final InMemoryFeatureSet newres = new InMemoryFeatureSet(fs.getType());
            try (Stream<Feature> stream = fs.features(false)) {
                newres.add(stream.iterator());
            }
            resources.add(newres);
            return newres;

        } else if (resource instanceof DefiningPyramidResource) {
            final DefiningPyramidResource cr = (DefiningPyramidResource) resource;
            final GenericName name = cr.getIdentifier().orElse(null);
            final InMemoryPyramidResource newres = new InMemoryPyramidResource(name);
            resources.add(newres);
            return newres;

        } else if (resource instanceof GridCoverageResource && resource instanceof MultiResolutionResource) {
            final GridCoverageResource cr = (GridCoverageResource) resource;
            final GenericName name = cr.getIdentifier().orElse(null);
            final InMemoryPyramidResource newres = new InMemoryPyramidResource(name);
            resources.add(newres);
            return newres;

        } else if (resource instanceof GridCoverageResource) {
            final GridCoverageResource cr = (GridCoverageResource) resource;
            final GenericName name = cr.getIdentifier().orElse(null);
            final InMemoryGridCoverageResource newres = new InMemoryGridCoverageResource(name);
            newres.write(cr.read(null));
            resources.add(newres);
            return newres;

        } else if (resource instanceof DefiningCoverageResource) {
            final DefiningCoverageResource cr = (DefiningCoverageResource) resource;
            final GenericName name = cr.getIdentifier().orElse(null);
            final InMemoryGridCoverageResource newres = new InMemoryGridCoverageResource(name);
            resources.add(newres);
            return newres;

        } else if (resource instanceof Aggregate) {
            final Aggregate agg = (Aggregate) resource;
            final InMemoryAggregate newres = new InMemoryAggregate(agg.getIdentifier().orElse(null));
            for (Resource r : agg.components()) {
                newres.add(r);
            }
            resources.add(newres);
            return newres;

        } else {
            throw new DataStoreException("Unsupported resource type "+ resource);
        }
    }

    @Override
    public void remove(Resource resource) throws DataStoreException {
        if (!resources.remove(resource)) {
            throw new DataStoreException("Resource not found");
        }
    }

}
