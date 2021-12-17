/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 20019, Geomatys
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
package org.geotoolkit.coverage.grid;

import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.opengis.geometry.Envelope;

/**
 * Subclass of GridGeometry with an estimated data resolution.
 *
 * @author Johann Sorel (Geomatys)
 */
public class EstimatedGridGeometry extends GridGeometry {

    private final double[] resolution;

    public EstimatedGridGeometry(Envelope envelope, double[] resolution) {
        super(null, envelope, GridOrientation.HOMOTHETY);
        this.resolution = resolution;
    }

    @Override
    public double[] getResolution(boolean allowEstimates) {
        if (allowEstimates) {
            return resolution.clone();
        }
        return super.getResolution(allowEstimates);
    }

}
