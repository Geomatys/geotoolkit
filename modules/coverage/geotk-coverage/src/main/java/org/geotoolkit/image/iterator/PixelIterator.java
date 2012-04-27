/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.iterator;

/**
 * Define standard iterator for image pixel.
 *
 * Iteration order is define in sub-classes implementation.
 * However iteration begging by Bands.
 *
 * Moreover comportment not specify if iterator exceed image limits.
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public abstract class PixelIterator {

    protected PixelIterator() {
    }

    /**
     * Returns true if the iteration has more pixel(in other words if {@link PixelIterator#nextSample() } is possible)
     * and move forward iterator.
     *
     * @return true if next value exist else false.
     */
    public abstract boolean next();


    /**
     * Returns next X iterator coordinate without move forward it.
     *
     * @return X iterator position.
     */
    public abstract int getX();

    /**
     * Returns next Y iterator coordinate without move forward it.
     *
     * @return Y iterator position.
     */
    public abstract int getY();

    /**
     * Returns the next integer value from iteration.
     *
     * @return the next integer value.
     */
    public abstract int getSample();

    /**
     * Returns the next float value from iteration.
     *
     * @return the next float value.
     */
    public abstract float getSampleFloat();

    /**
     * Returns the next double value from iteration.
     *
     * @return the next double value.
     */
    public abstract double getSampleDouble();

    /**
     * Initializes iterator.
     * Carry back iterator at its initial position like iterator is just build.
     */
    public abstract void rewind();

    /**
     * Write integer value at current iterator position.
     *
     * @param value : integer to write.
     */
    public abstract void setSample(final int value);

    /**
     * Write float value at current iterator position.
     *
     * @param value : float to write.
     */
    public abstract void setSampleFloat(final float value);

    /**
     * Write double value at current iterator position.
     *
     * @param value : double to write.
     */
    public abstract void setSampleDouble(final double value);
}
