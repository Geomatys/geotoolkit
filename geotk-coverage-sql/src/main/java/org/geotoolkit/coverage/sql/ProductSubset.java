/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2018, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.List;
import java.util.Optional;
import org.opengis.util.GenericName;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.referencing.privy.ExtentSelector;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.metadata.iso.extent.DefaultExtent;


final class ProductSubset extends AbstractGridCoverageResource {
    /**
     * The product for which this object is a subset.
     */
    private final ProductEntry product;

    /**
     * Area of data requested by user.
     */
    private final Envelope areaOfInterest;

    /**
     * Desired resolution in units of AOI, or {@code null} for no sub-sampling.
     */
    private final double[] resolution;

    /**
     * List of raster files intersection the {@link #areaOfInterest}.
     */
    private final List<GridCoverageEntry> entries;

    /**
     * An element from {@link #entries} list which seems the best match for user request.
     */
    private final GridCoverageEntry representative;

    /**
     * Creates a new subset for the given product.
     */
    ProductSubset(final ProductEntry product, final Envelope areaOfInterest, final double[] resolution,
                  final List<GridCoverageEntry> entries) throws DataStoreException, TransformException
    {
        super(null, false);
        this.product        = product;
        this.areaOfInterest = areaOfInterest;
        this.resolution     = resolution;
        this.entries        = entries;
        final DefaultExtent extent = new DefaultExtent();
        extent.addElements(areaOfInterest);
        final ExtentSelector<GridCoverageEntry> selector = new ExtentSelector<>(extent);
        selector.setTimeGranularity(product.temporalResolution);
        /*
         * TODO: need a better criteria for deciding when to seet `alternateOrdering` to true.
         * It could be  (requested time range) > (non-overlapping time range between images).
         * We want `false` when the requested time range is small (sometime 1 ms is used).
         */
        selector.alternateOrdering = true;
        entries.forEach(entry -> entry.submitTo(selector));
        GridCoverageEntry result = selector.best();
        if (result == null) {
            /*
             * Try again without Area Of Interest (AOI) constraint.
             * We use this fallback when no entry intersect the AOI.
             */
            extent.getGeographicElements().clear();
            selector.setExtentOfInterest(extent, null, null);
            entries.forEach(entry -> entry.submitTo(selector));
            result = selector.best();
            if (result == null) {
                result = entries.get(entries.size() / 2);
            }
        }
        representative = result;
    }

    @Override
    public Optional<GenericName> getIdentifier() {
        return Optional.of(product.createIdentifier("subset"));      // TODO: need a unique name.
    }

    /**
     * @todo need to build a grid geometry representative of all entries.
     */
    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return representative.getGridGeometry();
    }

    /**
     * @todo need to build sample dimensions representative of all entries.
     */
    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return representative.getSampleDimensions();
    }

    /**
     * @todo we should search here which entry is the best fit for the request.
     */
    @Override
    public GridCoverage read(final GridGeometry targetGeometry, final int... bands) throws DataStoreException {
        try {
            return representative.coverage(targetGeometry, bands);
        } catch (DataStoreException e) {
            throw e;
        } catch (Exception e) {
            throw new DataStoreException(e);
        }
    }
}
