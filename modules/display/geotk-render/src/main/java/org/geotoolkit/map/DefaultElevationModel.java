/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.io.IOException;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotools.coverage.io.CoverageReadParam;
import org.geotools.coverage.io.CoverageReader;

import org.opengis.filter.expression.Expression;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

/**
 * Default implementation of elevation model.
 * 
 * @author Johann Sorel (Geomatys)
 */
final class DefaultElevationModel implements ElevationModel{

    private final Expression correction;

    private final CoverageReader coverage;

    DefaultElevationModel(final CoverageReader coverage, final Expression correction){
        if(coverage == null){
            throw new NullPointerException("Coverage can not be null");
        }
        this.coverage = coverage;
        this.correction = correction;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getCorrection() {
        return correction;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getHeight(DirectPosition position, Unit<Length> lenght) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */@Override
    public CoverageReader getCoverageReader() {
        return coverage;
    }

}
