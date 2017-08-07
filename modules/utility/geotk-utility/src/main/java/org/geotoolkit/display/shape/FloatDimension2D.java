/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
import static java.lang.Float.floatToIntBits;


/**
 * Implements {@link Dimension2D} using single-precision floating point values.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public class FloatDimension2D extends Dimension2D implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 4011566975974105082L;

    /**
     * The width.
     */
    public float width;

    /**
     * The height.
     */
    public float height;

    /**
     * Constructs a new dimension initialized to (0,0).
     */
    public FloatDimension2D() {
    }

    /**
     * Constructs a new dimension initialized to the given dimension.
     *
     * @param dimension The dimension to copy.
     */
    public FloatDimension2D(final Dimension2D dimension) {
        width  = (float) dimension.getWidth();
        height = (float) dimension.getHeight();
    }

    /**
     * Constructs a new dimension with the specified values.
     *
     * @param w The width.
     * @param h The height.
     */
    public FloatDimension2D(final float w, final float h) {
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
        width  = (float) w;
        height = (float) h;
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
        return (floatToIntBits(width) + 31*floatToIntBits(height)) ^ (int) serialVersionUID;
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
            final FloatDimension2D that = (FloatDimension2D) object;
            return floatToIntBits(this.width)  == floatToIntBits(that.width) &&
                   floatToIntBits(this.height) == floatToIntBits(that.height);
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
