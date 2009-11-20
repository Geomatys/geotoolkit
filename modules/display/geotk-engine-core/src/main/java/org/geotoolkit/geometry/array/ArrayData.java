/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.geometry.array;

// J2SE dependencies
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.IllegalPathStateException;

// GeotoolKit dependencies
import org.geotoolkit.util.XArrays;


/**
 * An object holding an uncompressed copy of points from a {@link PointArray2D}.
 * Because the data are copied, they can be directly transformed by a
 * {@link org.opengis.referencing.operation.MathTransform} without any side effect
 * on the original {@code PointArray2D}.
 *
 * @module pending
 * @since 2.2
 * @version $Id$
 * @author Martin Desruisseaux (IRD)
 *
 * @see PointArray2D#toFloatArray(ArrayData,float)
 */
public final class ArrayData {
    /**
     * The array of points as (<var>x</var>,<var>y</var>) coordinates. Those points may be joined
     * by straight lines or by curves, according the information provided in the {@link #curves}
     * field. Valids index range from 0 inclusive to {@link #arrayLength} exclusive.
     */
    private float[] array;

    /**
     * The number of valid elements in {@link #array}.
     * The number of points is {@code arrayLength/2}.
     */
    private int arrayLength;

    /**
     * Array of (<var>index</var>, <var>PathIterator-code</var>) pairs. Those codes are used only
     * for curved lines. For example, suppose that the path iterator code for the coordinates at
     * {@code (array[10], array[11])} is {@link PathIterator#SEG_QUADTO), then the {@code curves}
     * array should contains the following value: {@code 10} (the index of the first affected
     * coordinate). All coordinates in the range <code>array[10..13]</code> will below to this
     * curve. Points that are not refered in this array default to {@link PathIterator#SEG_LINETO).
     * This array may be {@code null} if there is no curve.
     */
    private int[] curves;

    /**
     * {@code true} if the corresponding element in {@link #curves} is of type
     * {@link PathIterator#SEG_CUBICTO) instead of {@link PathIterator#SEG_QUADTO).
     * May be {@code null} if there is no curves or if all curves are {@code SEG_QUADTO).
     */
    private boolean[] cubics;

    /**
     * Number of valid elements in the {@link #curves} array.
     */
    private int curveLength;

    /**
     * Index of the next curve to returns.
     */
    private int nextCurve;

    /**
     * Constructs a new array with the specified capacity. The number of valid elements
     * ({@link #length length}) still 0 until new data are added. Note that this data
     * structure will growth as needed, so the initial capacity doesn't need to be too large.
     *
     * @param capacity The initial capacity for this data structure, as a number of points.
     */
    public ArrayData(final int capacity) {
        array = new float[capacity << 1];
    }

    /**
     * Returns the array which contains the (<var>x</var>,<var>y</var>) coordinates.
     * This method returns directly the internal array.
     */
    final float[] getArray() {
        return array;
    }

    /**
     * Returns the number of points in this data structure.
     */
    public final int length() {
        return arrayLength >> 1;
    }

    /**
     * Copies the points starting at {@code offset} to the specified destination path.
     * The points in <code>array[offset..{@linkplain #length length}]</code> will be
     * removed from this data structure.
     *
     * @param offset The index of the first point to move into {@code path}.
     * @param path   The destination path where to store the points.
     */
    public final void extract(int offset, final GeneralPath path) {
        offset <<= 1;
        for (int i=offset; i<arrayLength;) {
            if (i == offset) {
                path.moveTo(array[i++], array[i++]);
            } else {
                path.lineTo(array[i++], array[i++]);
            }
        }
        arrayLength = offset;
    }

    /**
     * Adds the specified shape at the end of this data structure. The specified shape must
     * not contains {@link PathIterator#SEG_MOVETO SEG_MOVETO} instructions (except for the
     * very first point), since {@code ArrayData} structure holds continuous lines only.
     *
     * @param  shape The shape to add.
     * @throws IllegalPathStateException if the shape contains illegal curve types.
     */
    public final void append(final Shape shape) throws IllegalPathStateException {
        final int       start = arrayLength;
        final float[]  coords = new float[6];
        final PathIterator it = shape.getPathIterator(null);
        while (!it.isDone()) {
            final int n;
            switch (it.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO: {
                    if (arrayLength != start) {
                        throw new IllegalPathStateException();
                    }
                    // MOVETO is treated like LINETO, providing that
                    // it is the very first segment. Fall through...
                }
                case PathIterator.SEG_LINETO: {
                    n = 2;
                    break;
                }
                case PathIterator.SEG_QUADTO: {
                    n = 4;
                    addCurve(arrayLength, false);
                    break;
                }
                case PathIterator.SEG_CUBICTO: {
                    n = 6;
                    addCurve(arrayLength, true);
                    break;
                }
                case PathIterator.SEG_CLOSE: {
                    // Since the shape argument is for map border only, which is just a small part
                    // of the whole Polyline, we are not allowed to close the shape. Fall through...
                }
                default: {
                    throw new IllegalPathStateException();
                }
            }
            if (arrayLength+n >= array.length) {
                array = XArrays.resize(array, arrayLength+n+256);
            }
            System.arraycopy(coords, 0, array, arrayLength, n);
            arrayLength += n;
            it.next();
        }
        assert (arrayLength & 1)==0 : arrayLength;
    }

    /**
     * Adds a curve.
     *
     * @param index The index where to apply the curve.
     * @param type The curve type.
     */
    private final void addCurve(final int index, final boolean isCubic) {
        if (curves == null) {
            curves = new int[6];
            assert curveLength == 0 : curveLength;
            assert nextCurve   == 0 : nextCurve;
        } else if (curveLength >= curves.length) {
            curves = XArrays.resize(curves, curveLength*2);
            if (cubics != null) {
                cubics = XArrays.resize(cubics, curves.length);
            }
        }
        if (cubics != null) {
            cubics[curveLength] = isCubic;
        } else if (isCubic) {
            cubics = new boolean[curves.length];
            cubics[curveLength] = isCubic;
        }
        curves[curveLength++] = index;
    }

    /**
     * Returns the curve type at the specified index. This method
     * must be invoked with index in increasing order only.
     */
    final int getCurveType(final int index) {
        if (nextCurve != curveLength) {
            assert nextCurve < curveLength : nextCurve;
            assert curves[nextCurve] >= index : index;
            assert nextCurve==0 || curves[nextCurve-1] < index : index;
            if (curves[nextCurve] == index) {
                return cubics!=null && cubics[nextCurve++] ? PathIterator.SEG_CUBICTO
                                                           : PathIterator.SEG_QUADTO;
            }
        }
        return (index==0) ? PathIterator.SEG_MOVETO : PathIterator.SEG_LINETO;
    }
}
