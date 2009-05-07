/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.filter.spatial;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.geometry.Envelope;

/**
 *
 * @author sorel
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

    @Override
    public E getExpression1() {
        return left;
    }

    @Override
    public F getExpression2() {
        return right;
    }

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
