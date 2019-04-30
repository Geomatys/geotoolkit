/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.processing.coverage.coveragetofeatures;

import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.operation.TransformException;

/**
 * Create a Collection of features used by the CoverageToFeature process
 * @author Quentin Boileau
 * @module
 */
public class CoverageToFeatureCollection extends RasterFeatureCollection {

    private final FeatureType newFeatureType;
    private final GridCoverageReader reader;
    private final GridCoverage coverage;
    private final GridGeometry gridGeom;

    /**
     * CoverageToFeatureCollection constructor connect the collection to the coverage.
     */
    public CoverageToFeatureCollection(final GridCoverageReader reader, GridExtent range,
            GridCoverage coverage, GridGeometry gridGeom) throws DataStoreException {
        super(reader, range);
        this.reader = reader;
        this.coverage = coverage;
        this.gridGeom = gridGeom;

        this.newFeatureType = CoverageToFeaturesProcess.createFeatureType(coverage, reader);
    }

    /**
     * Return the new FeatureType
     * @return FeatureType
     */
    public FeatureType getFeatureType() {
        return newFeatureType;
    }

    /**
     * Return FeatureIterator that iterate in coverage
     * @return FeatureIterator
     * @throws FeatureStoreRuntimeException
     */
    @Override
    public FeatureIterator iterator() throws FeatureStoreRuntimeException {
        return new RasterFeatureIterator();
    }

    /**
     * Run the process function to create a Feature from x,y cell coordinates
     * @param x
     * @param y
     * @return the Feature
     */
    @Override
    protected Feature create(long x, long y) throws FeatureStoreRuntimeException {
        Feature feat = null;
        try {
            feat = CoverageToFeaturesProcess.convertToFeature(getFeatureType(), x, y, CoverageUtilities.toGeotk(coverage), reader, gridGeom);
        } catch (DataStoreException ex) {
           throw new FeatureStoreRuntimeException(ex);
        } catch (TransformException ex) {
           throw new FeatureStoreRuntimeException(ex);
        }
        return feat;
    }
}
