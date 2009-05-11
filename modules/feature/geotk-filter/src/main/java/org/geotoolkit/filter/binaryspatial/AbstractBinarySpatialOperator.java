/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.geometry.Envelope;

/**
 * Immutable abstract binary spatial operator.
 *
 * @author Johann Sorel (Geomatys)
 * @param <E> Expression or subclass
 * @param <F> Expression or subclass
 */
public abstract class AbstractBinarySpatialOperator<E extends Expression,F extends Expression> implements BinarySpatialOperator {

    private static final LinearRing[] EMPTY_RINGS = new LinearRing[0];
    protected static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    protected final E left;
    protected final F right;

    protected AbstractBinarySpatialOperator(E left, F right){
        if(left == null || right == null){
            throw new NullPointerException("Left and right expressions can not be null");
        }
        this.left = left;
        this.right = right;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public E getExpression1() {
        return left;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F getExpression2() {
        return right;
    }

    /**
     * Utility method to transform an envelope in geometry.
     * @param env
     * @return Geometry
     */
    protected static Geometry toGeometry(Envelope env){
        final Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(env.getMinimum(0), env.getMinimum(1));
        coords[1] = new Coordinate(env.getMinimum(0), env.getMaximum(1));
        coords[2] = new Coordinate(env.getMaximum(0), env.getMaximum(1));
        coords[3] = new Coordinate(env.getMaximum(0), env.getMinimum(1));
        coords[4] = new Coordinate(env.getMinimum(0), env.getMinimum(1));
        final LinearRing ring = GEOMETRY_FACTORY.createLinearRing(coords);
        return GEOMETRY_FACTORY.createPolygon(ring, EMPTY_RINGS);
    }

}
