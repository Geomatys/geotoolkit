/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.binaryspatial;

import com.vividsolutions.jts.geom.Geometry;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Beyond;


public class DefaultBeyond extends AbstractBinarySpatialOperator<Expression,Expression> implements Beyond {

    private final double distance;
    private final String unit;

    public DefaultBeyond(Expression left, Expression right, double distance, String unit) {
        super(left,right);
        this.distance = distance;
        this.unit = unit;
    }

    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public String getDistanceUnits() {
        return unit.toString();
    }

    @Override
    public boolean evaluate(Object object) {
        final Geometry leftGeom = left.evaluate(object, Geometry.class);
        final Geometry rightGeom = right.evaluate(object, Geometry.class);

        if(leftGeom == null || rightGeom == null){
            return false;
        }

        // TODO we can not handle units with JTS geometries
        // we need a way to obtain both geometry CRS to be able to make a correct
        // unit usage

        return !leftGeom.isWithinDistance(rightGeom, distance);
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

}
