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
 * Define standar iterator for image pixel.
 *
 * Iteration order is define in sub-classes implementation.
 * However iteration beging by Bands.
 *
 * Moreother comportement not specify if iterator exceed image limits.
 *
 * @author Rémi Marechal       (Geomatys).
 * @author Martin Desruisseaux (Geomatys).
 */
public abstract class PixelIterator {

    protected PixelIterator() {
    }

    /**
     * Returns true if the iteration has more pixel(in other words if {@link PixelIterator#nextSample() } is possible).
     *
     * @return true if next value exist else false.
     */
    public abstract boolean hasNext();


    /**
     * Returns next X iterator coordinate without move forward it.
     *
     * @return X iterator position.
     */
    public abstract int nextX();

    /**
     * Returns next Y iterator coordinate without move forward it.
     *
     * @return Y iterator position.
     */
    public abstract int nextY();

    /**
     * Returns the next integer value from iteration and move forward it.
     *
     * @return the next integer value.
     */
    public abstract int nextSample();

    /**
     * Returns the next float value from iteration and move forward it.
     *
     * @return the next float value.
     */
    public abstract float nextSampleFloat();

    /**
     * Returns the next double value from iteration and move forward it.
     *
     * @return the next double value.
     */
    public abstract double nextSampleDouble();

    /**
     * Initialize iterator.
     * Carry back iterator at its initial position like iterator is just build.
     */
    public abstract void rewind();

}
