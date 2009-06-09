/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003 - 2008, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.coverage.io.CoverageReader;

import org.opengis.filter.expression.Expression;
import org.opengis.geometry.DirectPosition;

/**
 * Default implementation of elevation model.
 * 
 * @author Johann Sorel (Geomatys)
 */
final class DefaultElevationModel implements ElevationModel{


    private final CoverageReader coverage;
    private final Expression offset;
    private final Expression scale;


    DefaultElevationModel(CoverageReader coverage, Expression offset, Expression scale){
        this.coverage = coverage;
        this.offset = offset;
        this.scale = scale;
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public double getModelHeight(DirectPosition position, Unit<Length> lenght) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CoverageReader getCoverageReader() {
        return coverage;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getBaseOffset() {
        return offset;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getBaseScale() {
        return scale;
    }

}
