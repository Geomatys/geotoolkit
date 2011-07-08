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
package org.geotoolkit.process.coverage.coveragetofeatures;

import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureIterator;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.operation.TransformException;

/**
 * Create a Collection of features used by the CoverageToFeature process
 * @author Quentin Boileau
 * @module pending
 */
public class CoverageToFeatureCollection extends RasterFeatureCollection {

    private final FeatureType newFeatureType;
    private final GridCoverageReader reader;
    private final GridCoverage2D coverage;
    private final GeneralGridGeometry gridGeom;

    /**
     * CoverageToFeatureCollection constructor connect the collection to the coverage.
     */
    public CoverageToFeatureCollection(final GridCoverageReader reader, GridEnvelope range,
            GridCoverage2D coverage, GeneralGridGeometry gridGeom) throws CoverageStoreException {
        super(reader, range);
        this.reader = reader;
        this.coverage = coverage;
        this.gridGeom = gridGeom;

        this.newFeatureType = CoverageToFeatures.createFeatureType(coverage, reader);
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
     * @throws DataStoreRuntimeException
     */
    @Override
    public FeatureIterator<Feature> iterator() throws DataStoreRuntimeException {
        return new RasterFeatureIterator();
    }

    /**
     * Run the process function to create a Feature from x,y cell coordinates
     * @param x
     * @param y
     * @return the Feature
     */
    @Override
    protected Feature create(int x, int y) throws DataStoreRuntimeException {
        Feature feat = null;
        try {
            feat = CoverageToFeatures.convertToFeature(getFeatureType(), x, y, coverage, reader, gridGeom);
        } catch (CoverageStoreException ex) {
           throw new DataStoreRuntimeException(ex);
        } catch (TransformException ex) {
           throw new DataStoreRuntimeException(ex);
        }
        return feat;
    }
}
