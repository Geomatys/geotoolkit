/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.dggs;

import org.geotoolkit.storage.dggs.privy.GridAsDiscreteGlobalGridResource;
import java.util.List;
import java.util.stream.Stream;
import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.BandedCoverageResource;
import org.opengis.referencing.operation.TransformException;

/**
 * A Resource which offer acces to a coverage structured in DGGS cells.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface DiscreteGlobalGridResource extends BandedCoverageResource {

    /**
     * @return DiscreteGlobalGridReferenceSystem, never null
     */
    DiscreteGlobalGridReferenceSystem getGridReferenceSystem();

    /**
     * @return available dggrs refinement levels available in the resource
     */
    NumberRange<Integer> getAvailableDepths();

    /**
     * @return default depth of the data
     */
    int getDefaultDepth();

    /**
     * @todo : should not be here, on a query ?
     * @return maximum sub zone relative depth possible to query.
     */
    int getMaxRelativeDepth();

    /**
     * {@inheritDoc }
     */
    @Override
    public default DiscreteGlobalGridCoverage read(GridGeometry domain, int... range) throws DataStoreException {

        final Quantity<?> coverageResolution = GridAsDiscreteGlobalGridResource.computeAverageResolution(domain);

        //extract zones in the wanted area
        final DiscreteGlobalGridReferenceSystem dggrs = getGridReferenceSystem();
        final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
        final Stream<Zone> zones;
        try {
            coder.setPrecision(coverageResolution, null);
            zones = coder.intersect(domain.getEnvelope(dggrs.getGridSystem().getCrs()));
        } catch (IncommensurableException | TransformException ex) {
            throw new DataStoreException(ex);
        }

        return read(zones.map(Zone::getIdentifier).toList(), range);
    }

    /**
     * Retrieve a set of DGGRS zone data.
     *
     * @param zones zones to read
     * @param range bands to select
     * @return DiscreteGlobalGridCoverage, never null
     * @throws DataStoreException
     */
    public DiscreteGlobalGridCoverage read(List<ZonalIdentifier> zones, int ... range) throws DataStoreException;
}
