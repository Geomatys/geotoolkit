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

import java.util.logging.Level;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.util.StringUtilities;
import org.locationtech.jts.geom.Geometry;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Beyond;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Immutable "beyond" filter.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultBeyond extends AbstractBinarySpatialOperator<Expression,Expression> implements Beyond {

    private final double distance;
    private final Unit unit;
    private final String strUnit;

    public DefaultBeyond(final Expression left, final Expression right, final double distance, final String unit) {
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
        return strUnit;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(final Object object) {
        final Geometry leftGeom = toGeometry(object, left);
        final Geometry rightGeom = toGeometry(object, right);

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

        } catch (FactoryException | TransformException ex) {
            Logging.getLogger("org.geotoolkit.filter.binaryspatial").log(Level.WARNING, null, ex);
            return false;
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(final FilterVisitor visitor, final Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Beyond (distance=");
        sb.append(distance).append(" ,unit=").append(strUnit).append(")");
        sb.append(StringUtilities.toStringTree(left,right));
        return sb.toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
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
