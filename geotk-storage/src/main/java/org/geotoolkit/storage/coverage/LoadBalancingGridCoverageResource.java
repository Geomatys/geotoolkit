/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.storage.coverage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.opengis.util.GenericName;

/**
 * Dispatch reading requests on multiple resources.
 * - If a free resource is available it is returned.
 * - If not the code try to get one from the producer and return it
 * - If the producer did not create a new one then a resource already in use is returned.
 * @author Johann Sorel (Geomatys)
 */
public final class LoadBalancingGridCoverageResource extends AbstractGridCoverageResource implements GridCoverageResource {

    private final Supplier<GridCoverageResource> producer;
    private final GridCoverageResource first;
    private final List<GridCoverageResource> availables = new ArrayList<>();
    private final List<GridCoverageResource> all = new ArrayList<>();


    public LoadBalancingGridCoverageResource(Supplier<GridCoverageResource> producer) {
        super(null);
        this.producer = producer;
        first = producer.get();
        availables.add(first);
        all.add(first);
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return first.getIdentifier();
    }

    /**
     * Get a resource.
     * - If a free resource is available it is returned.
     * - If not the code try to get one from the producer and return it
     * - If the producer did not create a new one then a resource already in use is returned.
     */
    private GridCoverageResource aquireGridCoverageResource() throws DataStoreException {
        synchronized (availables) {
            if (availables.isEmpty()) {
                final GridCoverageResource shard = producer.get();
                if (shard != null) {
                    all.add(shard);
                    return shard;
                }

                //no available one and can not create a new one
                //use one already in use, move it to the back of the list
                final GridCoverageResource old = all.remove(0);
                all.add(old);
                return old;
            } else {
                return availables.remove(availables.size() - 1);
            }
        }
    }

    private void releaseGridCoverageResource(GridCoverageResource gcr) {
        synchronized (availables) {
            availables.add(gcr);
        }
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return first.getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return first.getSampleDimensions();
    }

    @Override
    public GridCoverage read(GridGeometry gg, int... ints) throws DataStoreException {
        final GridCoverageResource gcr = aquireGridCoverageResource();
        try {
            return gcr.read(gg, ints);
        } finally {
            releaseGridCoverageResource(gcr);
        }
    }

}
