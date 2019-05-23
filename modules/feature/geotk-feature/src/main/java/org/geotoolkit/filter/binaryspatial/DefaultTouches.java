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
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.util.StringUtilities;
import org.locationtech.jts.geom.Geometry;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.Touches;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Immutable "touches" filter.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultTouches extends AbstractBinarySpatialOperator<Expression,Expression> implements Touches {

    public DefaultTouches(final Expression left, final Expression right) {
        super(left,right);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(final Object object) {
        Geometry leftGeom = toGeometry(object, left);
        Geometry rightGeom = toGeometry(object, right);

        if(leftGeom == null || rightGeom == null){
            return false;
        }

        final Geometry[] values;
        try {
            values = toSameCRS(leftGeom, rightGeom);
        } catch (FactoryException | TransformException ex) {
            Logging.getLogger("org.geotoolkit.filter.binaryspatial").log(Level.WARNING, null, ex);
            return false;
        }
        leftGeom = values[0];
        rightGeom = values[1];

        return leftGeom.touches(rightGeom);
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
        final StringBuilder sb = new StringBuilder("Touches");
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
        int hash = 24;
        hash = 71 * hash + this.left.hashCode();
        hash = 71 * hash + this.right.hashCode();
        return hash;
    }

}
