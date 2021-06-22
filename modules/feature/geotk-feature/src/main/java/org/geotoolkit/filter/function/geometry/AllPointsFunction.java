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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.Expression;

/**
 * Extract all geometry points.
 *
 * @author Johann Sorel (Geomatys)
 */
public class AllPointsFunction extends AbstractFunction {

    public AllPointsFunction(final Expression expr1) {
        super(GeometryFunctionFactory.ALLPOINTS, expr1);
    }

    @Override
    public Object apply(final Object feature) {
        final Geometry geom = geometryValue(feature);
        if(geom==null) return null;
        final Geometry pt = getPoints(geom);
        if(pt==null) return null;
        pt.setSRID(geom.getSRID());
        pt.setUserData(geom.getUserData());
        return pt;
    }

    private static Geometry getPoints(Geometry geom){
        final Coordinate[] coordinates = geom.getCoordinates();
        if(coordinates.length==1){
            return geom.getFactory().createPoint(coordinates[0]);
        }else{
            return geom.getFactory().createMultiPoint(coordinates);
        }
    }
}
