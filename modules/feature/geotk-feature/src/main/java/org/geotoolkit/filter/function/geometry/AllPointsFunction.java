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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.expression.Expression;

/**
 * Extract all geometry points.
 *
 * @author Johann Sorel (Geomatys)
 */
public class AllPointsFunction extends AbstractFunction {

    public AllPointsFunction(final Expression expr1) {
        super(GeometryFunctionFactory.ALLPOINTS, new Expression[] {expr1}, null);
    }

    @Override
    public Object evaluate(final Object feature) {
        final Geometry geom;

        try {
            geom = parameters.get(0).evaluate(feature,Geometry.class);
        } catch (Exception e){
            throw new IllegalArgumentException("Invalid function parameter."+parameters.get(0));
        }

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
