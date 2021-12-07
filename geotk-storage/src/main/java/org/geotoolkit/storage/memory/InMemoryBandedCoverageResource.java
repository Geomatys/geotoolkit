/*
 * (C) 2021, Geomatys
 */
package org.geotoolkit.storage.memory;

import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.BandedCoverage;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.storage.coverage.BandedCoverageResource;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class InMemoryBandedCoverageResource implements BandedCoverageResource {

    private final BandedCoverage coverage;

    public InMemoryBandedCoverageResource(BandedCoverage coverage) {
        this.coverage = coverage;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return coverage.getSampleDimensions();
    }

    @Override
    public BandedCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        return coverage;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.empty();
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.empty();
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return new DefaultMetadata();
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> type, StoreListener<? super T> sl) {
    }

    @Override
    public <T extends StoreEvent> void removeListener(Class<T> type, StoreListener<? super T> sl) {
    }
}
