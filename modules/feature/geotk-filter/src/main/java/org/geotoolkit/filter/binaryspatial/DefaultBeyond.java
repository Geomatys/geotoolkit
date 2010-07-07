/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.converter.UnitConverter;
import javax.measure.unit.Unit;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Beyond;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Immutable "beyond" filter.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultBeyond extends AbstractBinarySpatialOperator<Expression,Expression> implements Beyond {

    private final double distance;
    private final Unit unit;
    private final String strUnit;

    public DefaultBeyond(Expression left, Expression right, double distance, String unit) {
        super(left,right);
        this.distance = distance;
        this.strUnit = unit;
        this.unit = toUnit(unit);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getDistance() {
        return distance;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDistanceUnits() {
        return strUnit.toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(Object object) {
        final Geometry leftGeom = left.evaluate(object, Geometry.class);
        final Geometry rightGeom = right.evaluate(object, Geometry.class);

        if(leftGeom == null || rightGeom == null){
            return false;
        }

        try {
            final Object[] values = toSameCRS(leftGeom, rightGeom, unit);

            if(values[2] == null){
                //no matching crs was found, assume both have the same and valid unit
                return !leftGeom.isWithinDistance(rightGeom, distance);
            }else{
                final Geometry leftMatch = (Geometry) values[0];
                final Geometry rightMatch = (Geometry) values[1];
                final CoordinateReferenceSystem crs = (CoordinateReferenceSystem) values[2];
                final UnitConverter converter = unit.getConverterTo(crs.getCoordinateSystem().getAxis(0).getUnit());

                return !leftMatch.isWithinDistance(rightMatch, converter.convert(distance));
            }

        } catch (FactoryException ex) {
            Logger.getLogger(DefaultBeyond.class.getName()).log(Level.WARNING, null, ex);
            return false;
        } catch (TransformException ex) {
            Logger.getLogger(DefaultBeyond.class.getName()).log(Level.WARNING, null, ex);
            return false;
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return new StringBuilder("Beyond{")
                .append(left).append(',')
                .append(right).append(',')
                .append(distance).append(',')
                .append(strUnit).append('}')
                .toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DefaultBeyond other = (DefaultBeyond) obj;
        if (this.left != other.left && !this.left.equals(other.left)) {
            return false;
        }
        if (this.right != other.right && !this.right.equals(other.right)) {
            return false;
        }
        if (this.distance != other.distance) {
            return false;
        }
        if (!this.strUnit.equals(other.strUnit)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 16;
        hash = 71 * hash + this.left.hashCode();
        hash = 71 * hash + this.right.hashCode();
        hash = 71 * hash + (int)this.distance;
        hash = 71 * hash + this.strUnit.hashCode();
        return hash;
    }

}
