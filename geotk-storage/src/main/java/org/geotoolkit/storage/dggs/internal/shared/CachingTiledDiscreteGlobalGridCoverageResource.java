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
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverage;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridCoverageProcessor;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridResource;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

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
    public NumberRange<Integer> getTileAvailableDepths() {
        return caching.getTileAvailableDepths();
    }

    @Override
    public int getTileRelativeDepth() {
        return caching.getTileRelativeDepth();
    }


    @Override
    public FeatureType getSampleType() throws DataStoreException {
        return caching.getSampleType();
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
        DiscreteGlobalGridCoverage coverage = caching.getZoneTile(identifierOrZone);
        if (coverage == null) {
            if (identifierOrZone instanceof Zone z) {
                identifierOrZone = z.getIdentifier();
            }
            final DiscreteGlobalGridGeometry tileGrid = DiscreteGlobalGridGeometry.subZone(getGridGeometry().getReferenceSystem(), identifierOrZone, getTileRelativeDepth());
            coverage = source.read(tileGrid);
            if (!coverage.getGeometry().equals(tileGrid)) {
                try {
                    coverage = processor.resample(coverage, tileGrid);
                } catch (FactoryException | TransformException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            }
        }

        return coverage;
    }
}
