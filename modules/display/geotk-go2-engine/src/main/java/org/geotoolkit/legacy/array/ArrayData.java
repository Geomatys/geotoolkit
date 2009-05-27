/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.legacy.array;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.PathIterator;

import org.geotoolkit.util.XArrays;


/**
 * An object containing an uncompressed <strong>copy</strong> of points from a {@link PointArray}.
 * This object is used as an argument for {@link PointArray#toArray(ArrayData,float)} method.
 * Copying data is required because data will typically be transformed with
 * {@link org.geotools.ct.MathTransform}.
 *
 * This class (like the whole <code>org.geotools.renderer.array</code> package)
 * is for internal use by {@link org.geotools.renderer.geom.Polyline} only.
 *
 * @source $URL: http://svn.geotools.org/branches/legacy/migrate/src/org/geotools/renderer/array/ArrayData.java $
 * @version $Id: ArrayData.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 *
 * @deprecated Replaced by {@link org.geotools.geometry.array.ArrayData}
 *             as part of the port of J2D-renderer to the new GO-1 based API. Note that it is not
 *             possible to mix J2D-renderer classes with GO-1 rendering engine. Migration from
 *             J2D-renderer to the GO-1 API will requires the replacement of all deprecated classes
 *             together. Because the new GO-1 rendering engine is a work in progress, see
 *             <A HREF="http://jira.codehaus.org/browse/GEOT-776">GEOT-776</A> in order to determine
 *             if enough functionalites have been ported for yours need.
 */
public class ArrayData {
    /**
     * The array of points as (<var>x</var>,<var>y</var>) coordinates.
     * Valides index range from 0 inclusive to {@link #length} exclusive.
     */
    protected float[] array;

    /**
     * The number of valid elements if {@link #array}.
     * The number of points is <code>length/2</code>.
     */
    protected int length;

    /**
     * Array of <code>(index, {@link PathIterator} code)</code> pairs.  Those codes are used only
     * for the map's border coordinates. For example, suppose that the path iterator code for the
     * coordinates at <code>(array[10], array[11])</code> is {@link PathIterator#SEG_QUADTO), then
     * this array will contains the following pair: <code>(10, 2)</code> (2 is the code for QUADTO).
     * All coordinates in the range <code>array[10..13]</code> will apply to this curve. Points
     * that are not refered in this array default to {@link PathIterator#SEG_LINETO). This array
     * may be <code>null</code> if there is no curve.
     */
    private int[] curves;

    /**
     * Number of valid elements in the {@link #curves} array.
     * The number of curves is <code>curves/2</code>.
     */
    private int curveLength;

    /**
     * Index of the next curve to returns.
     */
    private int nextCurve;

    /**
     * Default constructor. Subclass must initialize {@link #array}.
     */
    protected ArrayData() {
    }

    /**
     * Construct a new array with the specified capacity.
     * The number of valid elements ({@link #length}) still 0.
     */
    public ArrayData(final int capacity) {
        assert (capacity & 1)==0 : capacity;
        array = new float[capacity];
    }

    /**
     * Set the data in this array.
     *
     * @param array  The array. Its length must be even.
     * @param length The number of valid elements in <code>array</code>.
     *               This value must be even.
     * @param curves The data returned by {@link #curves}.
     */
    public final void setData(final float[] array, final int length, final int[] curves) {
        assert array==null || (array.length & 1)==0 : array.length;
        assert (length & 1)==0 : length;
        this.array  = array;
        this.length = length;
        this.curves = curves;
        this.nextCurve  = 0;
        this.curveLength = (curves!=null) ? curves.length : 0;
    }

    /**
     * Returns {@link #array}.
     */
    public final float[] array() {
        return array;
    }

    /**
     * Returns {@link #length}.
     */
    public final int length() {
        return length;
    }

    /**
     * Returns codes for the curves. This is one of the
     * arguments to give back to {@link #setData} later.
     */
    public final int[] curves() {
        if (curves != null) {
            curves = XArrays.resize(curves, curveLength);
        }
        return curves;
    }

    /**
     * Copy the points from <code>offset</code> to the specified destination path.
     * The points in <code>array[offset..length]</code> will be removed from this
     * array.
     *
     * @param offset The index of the first point to move into <code>path</code>.
     * @param path   The destination path where to store the points.
     */
    public final void extract(final int offset, final GeneralPath path) {
        assert (offset & 1)==0 : offset;
        for (int i=offset; i<length;) {
            if (i==offset) {
                path.moveTo(array[i++], array[i++]);
            } else {
                path.lineTo(array[i++], array[i++]);
            }
        }
        length = offset;
    }

    /**
     * Add the specified shape to the end of this array.
     *
     * @param shape The shape to add.
     */
    public final void append(final Shape shape) {
        final int       start = length;
        final float[]  coords = new float[6];
        final PathIterator it = shape.getPathIterator(null);
        while (!it.isDone()) {
            final int n;
            switch (it.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO: {
                    if (length != start) {
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
                    addCurve(length, PathIterator.SEG_QUADTO);
                    break;
                }
                case PathIterator.SEG_CUBICTO: {
                    n = 6;
                    addCurve(length, PathIterator.SEG_CUBICTO);
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
            if (length+n >= array.length) {
                array = XArrays.resize(array, length+n+256);
            }
            System.arraycopy(coords, 0, array, length, n);
            length += n;
            it.next();
        }
        assert (length & 1)==0 : length;
    }

    /**
     * Add a curve.
     *
     * @param index The index where to apply the curve.
     * @param type The curve type.
     */
    private final void addCurve(final int index, final int type) {
        if (curves == null) {
            curves = new int[12];
            assert curveLength == 0 : curveLength;
            assert nextCurve   == 0 : nextCurve;
        } else if (curveLength >= curves.length) {
            curves = XArrays.resize(curves, curveLength*2);
        }
        assert (curves.length & 1)==0 && (curveLength & 1)==0;
        curves[curveLength++] = index;
        curves[curveLength++] = type;
    }

    /**
     * Returns the curve type at the specified index. This method
     * must be invoked with index in increasing order only.
     */
    protected final int getCurveType(final int index) {
        if (nextCurve != curveLength) {
            assert nextCurve < curveLength : nextCurve;
            assert curves[nextCurve] >= index : index;
            assert nextCurve==0 || curves[nextCurve-2] < index : index;
            if (curves[nextCurve] == index) {
                nextCurve += 2;
                return curves[nextCurve-1];
            }
        }
        return (index==0) ? PathIterator.SEG_MOVETO : PathIterator.SEG_LINETO;
    }
}
