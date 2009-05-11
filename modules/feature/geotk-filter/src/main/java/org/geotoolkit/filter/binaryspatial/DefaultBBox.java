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

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.referencing.CRS;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BBOX;
import org.opengis.geometry.Envelope;

/**
 * Immutable "BBOX" filter.
 *
 * @author Johann Sorel (Geomatys).
 */
public class DefaultBBox extends AbstractBinarySpatialOperator<PropertyName,DefaultLiteral<Envelope>> implements BBOX {

    public DefaultBBox(PropertyName property, DefaultLiteral<Envelope> bbox) {
        super(property,bbox);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getPropertyName() {
        return left.getPropertyName();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getSRS() {
        return CRS.toSRS(right.getValue().getCoordinateReferenceSystem());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getMinX() {
        return right.getValue().getMinimum(0);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getMinY() {
        return right.getValue().getMinimum(1);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getMaxX() {
        return right.getValue().getMaximum(0);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public double getMaxY() {
        return right.getValue().getMaximum(1);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(Object object) {
        final Geometry candidate = left.evaluate(object, Geometry.class);

        if(candidate == null){
            return false;
        }

        return candidate.intersects(toGeometry(right.getValue()));
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
        return new StringBuilder("BBOX{")
                .append(left).append(',')
                .append(right).append('}')
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
        final AbstractBinarySpatialOperator other = (AbstractBinarySpatialOperator) obj;
        if (this.left != other.left && !this.left.equals(other.left)) {
            return false;
        }
        if (this.right != other.right && !this.right.equals(other.right)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        int hash = 15;
        hash = 71 * hash + this.left.hashCode();
        hash = 71 * hash + this.right.hashCode();
        return hash;
    }

}
