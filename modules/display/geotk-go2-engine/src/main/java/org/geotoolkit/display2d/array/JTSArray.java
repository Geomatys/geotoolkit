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
package org.geotoolkit.display2d.array;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.util.XArrays;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import org.geotoolkit.display2d.geom.CompressionLevel;


/**
 * A wrapper around an array of JTS {@link Coordinate}s. As of JTS 1.3, {@link LineString} stores
 * its internal data as an array of type <code>Coordinate[]</code>.    This <code>JTSArray</code>
 * class wrap directly this {@link LineString} internal array,  in order to avoid object creation
 * and copies.
 *
 * @source $URL: http://svn.geotools.org/branches/legacy/migrate/src/org/geotools/renderer/array/JTSArray.java $
 * @version $Id: JTSArray.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 *
 * @see DefaultArray
 * @see GenericArray
 * @see DefaultArray#getInstance
 */
public final class JTSArray extends PointArray implements RandomAccess {
    /**
     * Serial version for compatibility with previous version.
     */
    private static final long serialVersionUID = 5944964058006239460L;

    /**
     * The coordinates. This is usually a reference to an internal
     * array of {@link LineString}. <strong>Do not modify</strong>.
     */
    private final Coordinate[] coords;

    /**
     * Range of valid coordinates in {@link #coords}. This range goes from <code>lower</code>
     * inclusive to <code>upper</code> exclusive. <strong>Note:</strong> Methods {@link #lower()}
     * and {@link #upper()} returns twice those values, because of {@link PointArray} specification.
     */
    private final int lower, upper;

    /**
     * Construct a new <code>JTSArray</code> for the specified coordinate points.
     *
     * @param coords The array of coordinate points. This array is not cloned.
     */
    public JTSArray(final Coordinate[] coords) {
        this.coords = coords;
        this.lower  = 0;
        this.upper  = coords.length;
    }

    /**
     * Construct a new <code>JTSArray</code> which is a sub-array of the specified coordinates.
     *
     * @param coords The array of coordinate points. This array is not cloned.
     * @param lower  The first coordinate to use (inclusive).
     * @param upper  The last  coordinate to use (exclusive).
     */
    public JTSArray(final Coordinate[] coords, final int lower, final int upper) {
        this.coords = coords;
        this.lower  = lower;
        this.upper  = upper;
    }

    /**
     * Returns 2&times;{@link #lower}.
     */
    @Override
    final int lower() {
        return lower << 1;
    }

    /**
     * Returns 2&times;{@link #upper}.
     */
    @Override
    final int upper() {
        return upper << 1;
    }

    /**
     * Returns an estimation of memory usage in bytes. This method count 32 bytes for each
     * {@link Coordinate} object plus 12 bytes for internal fields (the {@link #array}
     * reference plus {@link #lower} and {@link #upper} values).
     */
    @Override
    public final long getMemoryUsage() {
        return count()*32 + 12;
    }

    /**
     * Returns the number of points in this array.
     */
    @Override
    public final int count() {
        return upper-lower;
    }
    
    /**
     * Returns the first point in this array. If <code>point</code> is null, a new
     * {@link Point2D} object is allocated and then the result is stored in this object.
     *
     * @param  point The object in which to store the first point, or <code>null</code>.
     * @return <code>point</code> or a new {@link Point2D}, which contains the first point.
     */
    @Override
    public final Point2D getFirstPoint(final Point2D point) {
        final Coordinate coord = coords[lower];
        if (point != null) {
            point.setLocation(coord.x, coord.y);
            return point;
        } else {
            return new Point2D.Double(coord.x, coord.y);
        }
    }
    
    /**
     * Returns the last point in this array. If <code>point</code> is null, a new
     * {@link Point2D} object is allocated and then the result is stored in this object.
     *
     * @param  point The object in which to store the last point, or <code>null</code>.
     * @return <code>point</code> or a new {@link Point2D}, which contains the last point.
     */
    @Override
    public final Point2D getLastPoint(final Point2D point) {
        final Coordinate coord = coords[upper-1];
        if (point != null) {
            point.setLocation(coord.x, coord.y);
            return point;
        } else {
            return new Point2D.Double(coord.x, coord.y);
        }
    }

    /**
     * Returns the point at the specified index.
     *
     * @param  index The index from 0 inclusive to {@link #count} exclusive.
     * @return The point at the given index.
     * @throws IndexOutOfBoundsException if <code>index</code> is out of bounds.
     */
    @Override
    public Point2D getValue(int index) throws IndexOutOfBoundsException {
        if (index >= 0) {
            index += lower;
            if (index < upper) {
                final Coordinate coord = coords[index];
                return new Point2D.Double(coord.x, coord.y);
            }
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Returns an iterator object that iterates along the point coordinates.
     *
     * @param  index Index of the first point to returns in the iteration.
     * @return The iterator.
     */
    @Override
    public final PointIterator iterator(final int index) {
        return new JTSIterator(coords, index+lower, upper);
    }

    /**
     * Returns the bounding box of all <var>x</var> and <var>y</var> ordinates.
     * If this array is empty, then this method returns <code>null</code>.
     */
    @Override
    public final Rectangle2D getBounds2D() {
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        for (int i=lower; i<upper; i++) {
            final Coordinate coord = coords[i];
            final double x = coord.x;
            final double y = coord.y;
            if (x<xmin) xmin=x;
            if (x>xmax) xmax=x;
            if (y<ymin) ymin=y;
            if (y>ymax) ymax=y;
        }
        if (xmin<=xmax && ymin<=ymax) {
            return new Rectangle2D.Double(xmin, ymin, xmax-xmin, ymax-ymin);
        } else {
            return null;
        }
    }

    /**
     * Retourne un tableau enveloppant les m�mes points que le tableau courant,
     * mais des index <code>lower</code> inclusivement jusqu'� <code>upper</code>
     * exclusivement. Si le sous-tableau ne contient aucun point (c'est-�-dire si
     * <code>lower==upper</code>), alors cette m�thode retourne <code>null</code>.
     *
     * @param lower Index du premier point � prendre en compte.
     * @param upper Index suivant celui du dernier point � prendre en compte.
     */
    @Override
    public final PointArray subarray(int lower, int upper) {
        lower += this.lower;
        upper += this.lower;
        if (lower             == upper            ) return null;
        if (lower==this.lower && upper==this.upper) return this;
        return new JTSArray(coords, lower, upper);
    }

    /**
     * Ins�re les donn�es (<var>x</var>,<var>y</var>) du tableau <code>toMerge</code> sp�cifi�.
     */
    @Override
    public PointArray insertAt(int index, float[] toMerge, int lower, int upper, boolean reverse) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Renverse l'ordre de tous les points compris dans ce tableau.
     */
    @Override
    public PointArray reverse() {
        throw new UnsupportedOperationException();
    }

    /**
     * Retourne un tableau immutable qui contient les m�mes donn�es que celui-ci.
     */
    @Override
    public final PointArray getFinal(final CompressionLevel level) {
        if (level!=null && count() >= 8) {
            return new DefaultArray(toArray()).getFinal(level);
        }
        return super.getFinal(level);
    }
    
    /**
     * Append (<var>x</var>,<var>y</var>) coordinates to the specified destination array.
     * The destination array will be filled starting at index {@link ArrayData#length}.
     * If <code>resolution2</code> is greater than 0, then points that are closer than
     * <code>sqrt(resolution2)</code> from previous one will be skiped.
     *
     * @param  The destination array. The coordinates will be filled in
     *         {@link ArrayData#array}, which will be expanded if needed.
     *         After this method completed, {@link ArrayData#length} will
     *         contains the index after the <code>array</code>'s element
     *         filled with the last <var>y</var> ordinate.
     * @param  resolution2 The minimum squared distance desired between points.
     */
    @Override
    public final void toArray(final ArrayData dest, final float resolution2) {
        if (!(resolution2 >= 0)) {
            throw new IllegalArgumentException(String.valueOf(resolution2));
        }
        final int offset = dest.length;
        float[]   copy   = dest.array;
        int       src    = lower;
        int       dst    = offset;
        if (src < upper) {
            if (copy.length <= dst) {
                dest.array = copy = XArrays.resize(copy, capacity(2*src, dst, offset));
            }
            double lastX, lastY;
            Coordinate coord = coords[src];
            copy[dst++] = (float)(lastX = coord.x);
            copy[dst++] = (float)(lastY = coord.y);
            while (++src < upper) {
                coord = coords[src];
                final double dx = coord.x - lastX;
                final double dy = coord.y - lastY;
                if ((dx*dx + dy*dy) >= resolution2) {
                    if (copy.length <= dst) {
                        dest.array = copy = XArrays.resize(copy, capacity(2*src, dst, offset));
                    }
                    copy[dst++] = (float)(lastX = coord.x);
                    copy[dst++] = (float)(lastY = coord.y);
                }
            }
        }
        dest.length = dst;
        assert dest.length <= dest.array.length;
    }    
}
