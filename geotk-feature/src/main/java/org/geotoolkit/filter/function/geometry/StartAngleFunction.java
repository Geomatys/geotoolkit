/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.filter.function.geometry;

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.Expression;

/**
 * Extract first geometry segment angle in radians.
 *
 * @author Johann Sorel (Geomatys)
 */
public class StartAngleFunction extends AbstractFunction {

    public StartAngleFunction(final Expression expr1) {
        super(GeometryFunctionFactory.STARTANGLE, expr1);
    }

    @Override
    public Object apply(final Object feature) {
        final Geometry geom = geometryValue(feature);
        if(geom==null) return 0.0;
        final LineString line = getLine(geom);
        if(line==null) return 0.0;
        final int nb = line.getNumPoints();
        if(nb<2) return 0.0;
        final CoordinateSequence cs = line.getCoordinateSequence();
        return Math.atan2(cs.getY(0)-cs.getY(1),cs.getX(0)-cs.getX(1));
    }

    private static LineString getLine(Geometry geom){
        if(geom instanceof LineString){
            return (LineString) geom;
        }else if(geom instanceof GeometryCollection){
            final int nb = ((GeometryCollection)geom).getNumGeometries();
            if(nb!=0){
                return getLine(((GeometryCollection)geom).getGeometryN(0));
            }
        }
        return null;
    }
}
