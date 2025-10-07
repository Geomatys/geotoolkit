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

import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.storage.dggs.internal.shared.GridAsDiscreteGlobalGridResource;
import java.util.List;
import java.util.stream.Stream;
import javax.measure.IncommensurableException;
import javax.measure.Quantity;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGrid;
import org.geotoolkit.storage.rs.CodedGeometry;
import org.geotoolkit.storage.rs.CodedResource;
import org.opengis.referencing.operation.TransformException;

/**
 * A Resource which offer acces to a coverage structured in DGGS cells.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface DiscreteGlobalGridResource extends CodedResource {

    /**
     * @return DiscreteGlobalGridGeometry, never null
     */
    DiscreteGlobalGridGeometry getGridGeometry();

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

        final Quantity<?> coverageResolution;
        try {
            coverageResolution = GridAsDiscreteGlobalGridResource.computeAverageResolution(domain);
        } catch (TransformException ex) {
            throw new DataStoreException(ex);
        }

        //extract zones in the wanted area
        final DiscreteGlobalGridReferenceSystem dggrs = getGridGeometry().getReferenceSystem();
        final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
        final Stream<Zone> zones;
        try {
            coder.setPrecision(coverageResolution, null);
            final DiscreteGlobalGrid grid = dggrs.getGridSystem().getHierarchy().getGrids().get(coder.getPrecisionLevel());
            zones = grid.getZones(domain.getEnvelope(dggrs.getGridSystem().getCrs()));
        } catch (IncommensurableException | TransformException ex) {
            throw new DataStoreException(ex);
        }

        //todo check intersection with additional dimensions

        final List<Object> zoneIds = zones.map(Zone::getIdentifier).toList();
        final DiscreteGlobalGridGeometry geometry = new DiscreteGlobalGridGeometry(dggrs, zoneIds, null);
        return read(geometry, range);
    }

    /**
     * Retrieve a set of DGGRS zone data.
     *
     * @param geometry zones to read
     * @param range bands to select
     * @return DiscreteGlobalGridCoverage, never null
     * @throws DataStoreException
     */
    @Override
    public DiscreteGlobalGridCoverage read(CodedGeometry geometry, int ... range) throws DataStoreException;

    public static DiscreteGlobalGridGeometry toDiscreteGlobalGridGeometry(CodedGeometry geom) {
        if (geom instanceof DiscreteGlobalGridGeometry dgg) return dgg;
        else throw new UnsupportedOperationException("Not available yet");
    }

}
