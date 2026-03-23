/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.storage.dggs.internal.shared;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverageProcessor;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridResource;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johnan Sorel (Geomatys)
 */
public class CachingTiledDiscreteGlobalGridCoverageResource extends TiledDiscreteGlobalGridCoverageResource {

    private final DiscreteGlobalGridResource source;
    private final DiscreteGlobalGridCoverageProcessor processor;
    private final WritableTiledDiscreteGlobalGridCoverageResource caching;

    public CachingTiledDiscreteGlobalGridCoverageResource(DiscreteGlobalGridResource resource, DiscreteGlobalGridCoverageProcessor processor, WritableTiledDiscreteGlobalGridCoverageResource caching) {
        this.source = resource;
        this.processor = processor;
        this.caching = caching;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return source.getIdentifier();
    }

    @Override
    protected Metadata createMetadata() throws DataStoreException {
        return source.getMetadata();
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return source.getEnvelope();
    }

    @Override
    public NumberRange<Integer> getTileAvailableDepths() {
        return caching.getTileAvailableDepths();
    }

    @Override
    public int getTileRelativeDepth() {
        return caching.getTileRelativeDepth();
    }

    @Override
    public DiscreteGlobalGridGeometry getGridGeometry() {
        return caching.getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return caching.getSampleDimensions();
    }

    @Override
    public DiscreteGlobalGridCoverage getZoneTile(Object identifierOrZone) throws DataStoreException {

        final String zid;
        if (identifierOrZone instanceof Zone z) {
            zid = z.getTextIdentifier().toString();
        } else if (!(identifierOrZone instanceof String)) {
            zid = getGridGeometry().getReferenceSystem().getGridSystem().getHierarchy().toTextIdentifier(identifierOrZone);
        } else {
            zid = (String) identifierOrZone;
        }

        DiscreteGlobalGridCoverage coverage;
        try {
            lock(zid);

            coverage = caching.getZoneTile(identifierOrZone);
            if (coverage == null) {
                final DiscreteGlobalGridGeometry tileGrid = DiscreteGlobalGridGeometry.subZone(getGridGeometry().getReferenceSystem(), identifierOrZone, getTileRelativeDepth());
                coverage = source.read(tileGrid);
                if (!coverage.getGeometry().equals(tileGrid)) {
                    try {
                        coverage = processor.resample(coverage, tileGrid);
                    } catch (FactoryException | TransformException ex) {
                        throw new DataStoreException(ex.getMessage(), ex);
                    }
                }
                //store it in the cache
                caching.setZoneTile(identifierOrZone, coverage);
            }

        } finally {
            unlock(zid);
        }

        return coverage;
    }


    private final ConcurrentHashMap<String, LockWrapper> locks = new ConcurrentHashMap<>();

    private void lock(String key) {
        LockWrapper lockWrapper = locks.compute(key, (k, v) -> v == null ? new LockWrapper() : v.addThreadInQueue());
        lockWrapper.lock.lock();
    }

    private void unlock(String key) {
        LockWrapper lockWrapper = locks.get(key);
        lockWrapper.lock.unlock();
        if (lockWrapper.removeThreadFromQueue() == 0) {
            // NB : We pass in the specific value to remove to handle the case where another thread would queue right before the removal
            locks.remove(key, lockWrapper);
        }
    }

    private static class LockWrapper {
        private final Lock lock = new ReentrantLock();
        private final AtomicInteger numberOfThreadsInQueue = new AtomicInteger(1);

        private LockWrapper addThreadInQueue() {
            numberOfThreadsInQueue.incrementAndGet();
            return this;
        }

        private int removeThreadFromQueue() {
            return numberOfThreadsInQueue.decrementAndGet();
        }

    }

}
