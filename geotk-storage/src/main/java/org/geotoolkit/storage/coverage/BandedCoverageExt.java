/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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
package org.geotoolkit.storage.coverage;

import org.apache.sis.coverage.BandedCoverage;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.opengis.coverage.CannotEvaluateException;

/**
 * Subclass of BandedCoverage to support missing capabilities.
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class BandedCoverageExt extends BandedCoverage {

    public abstract double[] getResolution(boolean allowEstimate) throws DataStoreException;

    /**
     *
     * @param fullArea the globe area being generated, this information is provided to ensure
     *                 continuity between tiles.
     * @param tileArea the area to create
     */
    public abstract GridCoverage sample(GridGeometry fullArea, GridGeometry tileArea) throws CannotEvaluateException;

}
