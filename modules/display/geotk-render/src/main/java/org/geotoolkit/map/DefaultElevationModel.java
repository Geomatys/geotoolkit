/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.map;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.DirectPosition;

/**
 * Default implementation of elevation model.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
final class DefaultElevationModel implements ElevationModel{


    private final GridCoverageReader coverage;
    private final double scale;
    private final double azimuth;
    private final double altitude;


    DefaultElevationModel(final GridCoverageReader coverage, final double scale, final double azimuth, final double altitude){
        this.coverage = coverage;
        this.scale = scale;
        this.azimuth = scale;
        this.altitude = scale;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GridCoverageReader getCoverageReader() {
        return coverage;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getAmplitudeScale() {
        return scale;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getAzimuth() {
        return azimuth;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getAltitude() {
        return altitude;
    }
}
