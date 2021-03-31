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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.Expression;

/**
 * Extract first geometry point.
 *
 * @author Johann Sorel (Geomatys)
 */
public class StartPointFunction extends AbstractFunction {

    public StartPointFunction(final Expression expr1) {
        super(GeometryFunctionFactory.STARTPOINT, expr1);
    }

    @Override
    public Object apply(final Object feature) {
        final Geometry geom = geometryValue(feature);
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
