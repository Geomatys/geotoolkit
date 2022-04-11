/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.filter.coverage;

import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreReferencingException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.RasterLoadingStrategy;
import org.apache.sis.storage.event.StoreListeners;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;


/**
 * The result of applying a filter on a grid coverage.
 * This implementation assumes that {@link CompiledFilter} modifies
 * pixel values but not the grid geometry or the sample dimensions.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
final class FilteredCoverageResource extends AbstractGridCoverageResource {
    /**
     * The original resource. All operations are delegated on it.
     */
    private final GridCoverageResource source;

    /**
     * The processor to use for applying mask operations.
     */
    private final GridCoverageProcessor processor;

    /**
     * The filter to apply on grid coverages, or {@code null} if none.
     */
    private final CompiledFilter filter;

    /**
     * Creates a new resource which will delegate all operations to the given resource,
     * then apply the specified filter operation.
     */
    FilteredCoverageResource(final GridCoverageResource source,
            final GridCoverageProcessor processor, final CompiledFilter filter)
    {
        super(source instanceof StoreListeners ? (StoreListeners) source : null);
        this.source    = source;
        this.processor = processor;
        this.filter    = filter;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return source.getEnvelope();
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return source.getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return source.getSampleDimensions();
    }

    @Override
    public RasterLoadingStrategy getLoadingStrategy() throws DataStoreException {
        return source.getLoadingStrategy();
    }

    @Override
    public boolean setLoadingStrategy(final RasterLoadingStrategy strategy) throws DataStoreException {
        return source.setLoadingStrategy(strategy);
    }

    @Override
    public GridCoverage read(final GridGeometry domain, final int... range) throws DataStoreException {
        try {
            return filter.execute(processor, source.read(domain, range));
        } catch (TransformException e) {
            throw new DataStoreReferencingException(e);
        }
    }
}
