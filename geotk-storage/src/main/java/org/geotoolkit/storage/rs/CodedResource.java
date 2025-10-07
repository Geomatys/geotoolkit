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
package org.geotoolkit.storage.rs;

import java.util.List;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.coverage.BandedCoverageResource;
import org.opengis.feature.FeatureType;

/**
 * A Resource which offer acces to a coverage structured in located cells.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CodedResource extends BandedCoverageResource {

    /**
     * Returns the description of the samples stored.
     *
     * @return Feature type
     */
    FeatureType getSampleType() throws DataStoreException;

    /**
     * Get default resource geometry.
     * @return resource geometry
     */
    CodedGeometry getGridGeometry();

    /**
     * List alternative geometry available.
     * First entry is the default ReferencedGridGeometry from getGridGeometry().
     *
     * @return list of alternative geometry available.
     */
    default List<CodedGeometry> getAlternateGridGeometry() {
        return List.of(getGridGeometry());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public default CodedCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        throw new UnsupportedOperationException("todo");
//        final Quantity<?> coverageResolution;
//        try {
//            coverageResolution = GridAsDiscreteGlobalGridResource.computeAverageResolution(domain);
//        } catch (TransformException ex) {
//            throw new DataStoreException(ex);
//        }
//
//        //extract zones in the wanted area
//        final DiscreteGlobalGridReferenceSystem dggrs = getReferenceSystem();
//        final DiscreteGlobalGridReferenceSystem.Coder coder = dggrs.createCoder();
//        final Stream<Zone> zones;
//        try {
//            coder.setPrecision(coverageResolution, null);
//            zones = coder.intersect(domain.getEnvelope(dggrs.getGridSystem().getCrs()));
//        } catch (IncommensurableException | TransformException ex) {
//            throw new DataStoreException(ex);
//        }
//
//        //todo check intersection with additional dimensions
//
//        final List<ZonalIdentifier> zoneIds = zones.map(Zone::getIdentifier).toList();
//        final DiscreteGlobalGridGeometry geometry = new DiscreteGlobalGridGeometry(dggrs, zoneIds);
//        return read(geometry, range);
    }

    /**
     * Retrieve a set of DGGRS zone data.
     *
     * @param geometry to read
     * @param range bands to select
     * @return DiscreteGlobalGridCoverage, never null
     * @throws DataStoreException
     */
    public CodedCoverage read(CodedGeometry geometry, int ... range) throws DataStoreException;
}
