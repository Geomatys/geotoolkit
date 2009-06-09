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
 * An elevation model holds a map (may not necessarly be a grid coverage) of elevation values.
 * It is possible to ask for a grid coverage or a single elevation.
 *
 * @author Johann Sorel (Geomatys)
 */
public interface ElevationModel {

    Expression getBaseOffset();
    
    Expression getBaseScale();

    double getModelHeight(DirectPosition position, Unit<Length> lenght);

    CoverageReader getCoverageReader();
    
}
