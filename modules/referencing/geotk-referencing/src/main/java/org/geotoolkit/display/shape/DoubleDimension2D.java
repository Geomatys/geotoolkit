/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.display.shape;

import java.io.Serializable;
import java.awt.geom.Dimension2D;
import static java.lang.Double.doubleToLongBits;


/**
 * Implements {@link Dimension2D} using double-precision floating point values.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 1.0
 * @module
 */
public class DoubleDimension2D extends Dimension2D implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 3603763914115376884L;

    /**
     * The width.
     */
    public double width;

    /**
     * The height.
     */
    public double height;

    /**
     * Constructs a new dimension initialized to (0,0).
     */
    public DoubleDimension2D() {
    }

    /**
     * Constructs a new dimension initialized to the given dimension.
     *
     * @param dimension The dimension to copy.
     */
    public DoubleDimension2D(final Dimension2D dimension) {
        width  = dimension.getWidth();
        height = dimension.getHeight();
    }

    /**
     * Constructs a new dimension with the specified values.
     *
     * @param w The width.
     * @param h The height.
     */
    public DoubleDimension2D(final double w, final double h) {
        width  = w;
        height = h;
    }

    /**
     * Sets width and height for this dimension.
     *
     * @param w The width.
     * @param h The height.
     */
    @Override
    public void setSize(final double w, final double h) {
        width  = w;
        height = h;
    }

    /**
     * Returns the width.
     */
    @Override
    public double getWidth() {
        return width;
    }

    /**
     * Returns the height.
     */
    @Override
    public double getHeight() {
        return height;
    }

    /**
     * Returns a hash code value for this dimension.
     */
    @Override
    public int hashCode() {
        final long code = doubleToLongBits(width) + 31*doubleToLongBits(height);
        return (int) code ^ (int) (code >>> 32) ^ (int) serialVersionUID;
    }

    /**
     * Compares this dimension with the given object for equality.
     *
     * @param object The object to compare with.
     * @return {@code true} if this dimension is equal to the given object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && object.getClass() == getClass()) {
            final DoubleDimension2D that = (DoubleDimension2D) object;
            return doubleToLongBits(this.width)  == doubleToLongBits(that.width) &&
                   doubleToLongBits(this.height) == doubleToLongBits(that.height);
        }
        return false;
    }

    /**
     * Returns a string representation of this dimension.
     */
    @Override
    public String toString() {
        return "Dimension2D[" + width + ", " + height + ']';
    }
}
