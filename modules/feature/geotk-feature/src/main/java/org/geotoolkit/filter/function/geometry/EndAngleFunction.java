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

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import org.geotoolkit.filter.function.AbstractFunction;
import org.opengis.filter.expression.Expression;

/**
 * Extract last geometry segment angle in radians.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class EndAngleFunction extends AbstractFunction {
    
    public EndAngleFunction(final Expression expr1) {
        super(GeometryFunctionFactory.ENDANGLE, new Expression[] {expr1}, null);
    }

    @Override
    public Object evaluate(final Object feature) {
        final Geometry geom;

        try {
            geom = parameters.get(0).evaluate(feature,Geometry.class);
        } catch (Exception e){
            throw new IllegalArgumentException("Invalid function parameter."+parameters.get(0));
        }
        
        if(geom==null) return 0.0;
        final LineString line = getLine(geom);
        if(line==null) return 0.0;
        final int nb = line.getNumPoints();
        if(nb<2) return 0.0;
        final CoordinateSequence cs = line.getCoordinateSequence();
        return Math.atan2(cs.getY(nb-2)-cs.getY(nb-1),cs.getX(nb-2)-cs.getX(nb-1));
    }
    
    private static LineString getLine(Geometry geom){
        if(geom instanceof LineString){
            return (LineString) geom;
        }else if(geom instanceof GeometryCollection){
            final int nb = ((GeometryCollection)geom).getNumGeometries();
            if(nb!=0){
                return getLine(((GeometryCollection)geom).getGeometryN(nb-1));
            }
        }
        return null;
    }
    
}
