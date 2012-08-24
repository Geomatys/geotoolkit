/*
 * Map and oceanographical data visualisation
 * Copyright (C) 2012 Geomatys
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Library General Public
 *    License as published by the Free Software Foundation; either
 *    version 2 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Library General Public License for more details (http://www.gnu.org/).
 */
package org.geotoolkit.process.coverage.kriging;

import java.util.List;
import java.util.Arrays;
import com.vividsolutions.jts.geom.Coordinate;

import static java.lang.Math.max;
import static java.lang.Math.abs;


/**
 * A polyline in process of being created by {@link IsolineCreator}.
 * Those polylines are always two dimensional.
 *
 * @version 3.20
 * @author Martin Desruisseaux
 * @module pending
 *
 * @since 3.20
 */
final class Polyline {
    /**
     * Small tolerance factor for rounding errors. This is used for comparing ordinate values
     * in image coordinate system, i.e. the values to compare will typically be in the range
     * from 0 to the image width or height in pixels.
     */
    private static final double EPS = 1E-8;

    /**
     * Constants for {@code switch} statement.
     */
    private static final int
            APPEND  = 0, APPEND_REVERSED  = 1,
            PREPEND = 2, PREPEND_REVERSED = 3;

    /**
     * The coordinate arrays.
     */
    private double[] coordinates;

    /**
     * Length of the valid part of the {@link #coordinates} array.
     * This is twice the number of points.
     */
    private int length;

    /**
     * Creates a polyline initialized to the given line segment.
     */
    Polyline(final double x0, final double y0, final double x1, final double y1) {
        coordinates = new double[] {x0, y0, x1, y1};
        length = 4;
    }

    /**
     * Compares two values for equality, with a tolerance up to the {@link #EPS} factor.
     */
    private static boolean epsilonEquals(final double x0, final double x1) {
        return abs(x0 - x1) <= EPS * max(abs(x0), abs(x1));
    }

    /**
     * Compares two coordinates for equality, with a tolerance up to the {@link #EPS} factor.
     *
     * @return {@code true} if the two coordinates are considered equals.
     */
    private static boolean epsilonEquals(final double[] c0, final int i0, final double[] c1, final int i1) {
        return epsilonEquals(c0[i0], c1[i1]) && epsilonEquals(c0[i0+1], c1[i1+1]);
    }

    /**
     * Returns {@code true} if the merging should be done on the {@code toMerge} instance instead.
     * We do that for example if the other instance has sufficient capacity while this instance does
     * not.
     */
    private boolean preferOther(final Polyline toMerge) {
        final int newLength = length + toMerge.length - 2;
        final boolean thisHasEnough  = (newLength <= coordinates.length);
        final boolean otherHasEnough = (newLength <= toMerge.coordinates.length);
        if (thisHasEnough != otherHasEnough) {
            return otherHasEnough;
        }
        return false;
    }

    /**
     * Merges the content of this polyline with the {@code toMerge} polyline if the beginning or end
     * points of a polyline is approximatively equals to the beginning or end point of the other. If
     * no beginning or end points are equal, then this method does nothing and returns {@code null}.
     *
     * @param  toMerge The polyline to insert in this polyline, if possible.
     * @return The merged polyline, {@code null} if no merge has been performed.
     */
    final Polyline merge(final Polyline toMerge) {
        final double[] src1 = this.   coordinates;
        final double[] src2 = toMerge.coordinates;
        if (epsilonEquals(src1, 0, src2, 0)) {
            if (preferOther(toMerge)) {
                toMerge.merge(this, PREPEND_REVERSED);
                return toMerge;
            }
            merge(toMerge, PREPEND_REVERSED);
            return this;
        }
        final int last2 = toMerge.length - 2;
        if (epsilonEquals(src1, 0, src2, last2)) {
            if (preferOther(toMerge)) {
                toMerge.merge(this, APPEND);
                return toMerge;
            }
            merge(toMerge, PREPEND);
            return this;
        }
        final int last1 = length - 2;
        if (epsilonEquals(src1, last1, src2, 0)) {
            if (preferOther(toMerge)) {
                toMerge.merge(this, PREPEND);
                return toMerge;
            }
            merge(toMerge, APPEND);
            return this;
        }
        if (epsilonEquals(src1, last1, src2, last2)) {
            if (preferOther(toMerge)) {
                toMerge.merge(this, APPEND_REVERSED);
                return toMerge;
            }
            merge(toMerge, APPEND_REVERSED);
            return this;
        }
        return null;
    }

    /**
     * Actually perform the merge.
     */
    private void merge(final Polyline toMerge, final int op) {
        final double[] src1 = this.   coordinates;
        final double[] src2 = toMerge.coordinates;
        final int addLength = toMerge.length - 2;
        final int newLength = addLength + length;
        switch (op) {
            case PREPEND: {
                if (newLength > src1.length) {
                    coordinates = new double[newLength * 2];
                }
                System.arraycopy(src1, 0, coordinates, addLength, length);
                System.arraycopy(src2, 0, coordinates, 0, addLength);
                break;
            }
            case PREPEND_REVERSED: {
                if (newLength > src1.length) {
                    coordinates = new double[newLength * 2];
                }
                System.arraycopy(src1, 0, coordinates, addLength, length);
                copyReversed    (src2, 2, coordinates, 0, addLength);
                break;
            }
            case APPEND: {
                if (newLength > src1.length) {
                    coordinates = Arrays.copyOf(src1, newLength * 2);
                }
                System.arraycopy(src2, 2, coordinates, length, addLength);
                break;
            }
            case APPEND_REVERSED: {
                if (newLength > src1.length) {
                    coordinates = Arrays.copyOf(src1, newLength * 2);
                }
                copyReversed(src2, 0, coordinates, length, addLength);
                break;
            }
            default: throw new AssertionError(op);
        }
        length = newLength;
    }

    /**
     * Copies the given source array into the given destination array with (x,y) coordinates in
     * reverse order.
     */
    private static void copyReversed(final double[] src, int srcOff, final double[] dst, int dstOff, int length) {
        srcOff += length;
        length >>>= 1; // From this point, 'length' is actually the number of points.
        while (--length >= 0) {
            dst[dstOff++] = src[srcOff -= 2];
            dst[dstOff++] = src[srcOff +  1];
        }
    }

    /**
     * Returns the data as an array of JTS coordinates.
     * This is a temporary method for JTS compatibility only.
     */
    final List<Coordinate> toCoordinates() {
        final Coordinate[] coord = new Coordinate[length / 2];
        int offset = 0;
        for (int i=0; i<coord.length; i++) {
            coord[i] = new Coordinate(coordinates[offset++], coordinates[offset++]);
        }
        return Arrays.asList(coord);
    }
}
