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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.expression.Expression;

/**
 * Extract first geometry point.
 *
 * @author Johann Sorel (Geomatys)
 */
public class StartPointFunction extends AbstractFunction {

    public StartPointFunction(final Expression expr1) {
        super(GeometryFunctionFactory.STARTPOINT, new Expression[] {expr1}, null);
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
        final Point pt = getPoint(geom);
        if(pt==null) return null;
        pt.setSRID(geom.getSRID());
        pt.setUserData(geom.getUserData());
        return pt;
    }

    private static Point getPoint(Geometry geom){
        if(geom instanceof LineString){
            return ((LineString)geom).getStartPoint();
        }else if(geom instanceof Point){
            return (Point) ((Point)geom).clone();
        }else if(geom instanceof Polygon){
            return getPoint( ((Polygon)geom).getExteriorRing());
        }else if(geom instanceof GeometryCollection){
            final int nb = ((GeometryCollection)geom).getNumGeometries();
            if(nb!=0){
                return getPoint(((GeometryCollection)geom).getGeometryN(0));
            }else{
                 return null;
            }
        }else{
            return null;
        }
    }

}
