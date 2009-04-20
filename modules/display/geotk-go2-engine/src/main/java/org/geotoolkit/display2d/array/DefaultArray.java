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

import org.geotoolkit.display2d.geom.CompressionLevel;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.resources.Errors;


/**
 * Default implementation of {@link PointArray} wrapping an array of (<var>x</var>,<var>y</var>)
 * coordinates as a flat <code>float[]</code> array. The default implementation is immutable and
 * doesn't use any compression technic. However, subclasses may be mutable (i.e. support the
 * {@link #insertAt insertAt(...)} method) or compress data.
 *
 * @source $URL: http://svn.geotools.org/branches/legacy/migrate/src/org/geotools/renderer/array/DefaultArray.java $
 * @version $Id: DefaultArray.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 *
 * @see #getInstance
 */
public class DefaultArray extends PointArray implements RandomAccess {
    /**
     * Serial version for compatibility with previous version.
     */
    private static final long serialVersionUID = 3160219929318094867L;

    /**
     * The array of (<var>x</var>,<var>y</var>) coordinates.
     */
    protected float[] array;

    /**
     * Wrap the given (<var>x</var>,<var>y</var>) array. The constructor stores a direct
     * reference to <code>array</code> (i.e. the array is not copied). Do not modify the
     * data after construction if this <code>DefaultArray</code> should be immutable.
     *
     * @param  array The array of (<var>x</var>,<var>y</var>) coordinates.
     * @throws IllegalArgumentException if the array's length is not even.
     */
    public DefaultArray(final float[] array) throws IllegalArgumentException {
        this.array = array;
        if ((array.length & 1) != 0) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ODD_ARRAY_LENGTH_$1,
                                               new Integer(array.length)));
        }
    }

    /**
     * Returns a <code>PointArray</code> object wrapping the given (<var>x</var>,<var>y</var>)
     * array between the specified bounds. If the array doesn't contains any data (i.e. if
     * <code>lower==upper</code>), then this method returns <code>null</code>.
     *
     * @param  array The array of (<var>x</var>,<var>y</var>) coordinates.
     * @param  lower Index of the first <var>x</var> ordinate in <code>array</code>.
     * @param  upper Index after the last <var>y</var> oordinate in <code>array</code>.
     *         The difference <code>upper-lower</code> must be even.
     * @param  copy <code>true</code> if this method should copy the array (in order to
     *         protect the <code>PointArray</code> from changes), or <code>false</code>
     *         for a direct reference without copying. In the later case, the caller is
     *         responsable to ensure that the array will not be modified externally.
     * @return The <code>PointArray</code> object wrapping the given <code>array</code>.
     */
    public static PointArray getInstance(final float[] array, final int lower, final int upper,
                                         final boolean copy)
    {
        checkRange(array, lower, upper);
        if (upper == lower) {
            return null;
        }
        if (copy) {
            final float[] newArray = new float[upper-lower];
            System.arraycopy(array, lower, newArray, 0, newArray.length);
            return new DefaultArray(newArray);
        } else if (lower==0 && upper==array.length) {
            return new DefaultArray(array);
        } else {
            return new SubArray(array, lower, upper);
        }
    }

    /**
     * Returns the index of the first valid ordinate (inclusive).
     */
    @Override
    protected int lower() {
        return 0;
    }

    /**
     * Returns the index after the last valid ordinate.
     */
    @Override
    protected int upper() {
        return array.length;
    }

    /**
     * Returns the number of points in this array.
     */
    @Override
    public final int count() {
        return (upper()-lower())/2;
    }

    /**
     * Returns an estimation of memory usage in bytes. This method count 8 bytes for each
     * (x,y) points plus 4 bytes for the internal fields (the {@link #array} reference).
     */
    @Override
    public long getMemoryUsage() {
        return count()*8 + 4;
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
        final int lower=lower();
        assert lower <= upper();
        final float x = array[lower+0];
        final float y = array[lower+1];
        if (point != null) {
            point.setLocation(x,y);
            return point;
        } else {
            return new Point2D.Float(x,y);
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
        final int upper=upper();
        assert(upper >= lower());
        final float x = array[upper-2];
        final float y = array[upper-1];
        if (point != null) {
            point.setLocation(x,y);
            return point;
        } else {
            return new Point2D.Float(x,y);
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
        index *= 2;
        return new Point2D.Float(array[index], array[index+1]);
    }

    /**
     * Returns an iterator object that iterates along the point coordinates.
     *
     * @param  index Index of the first point to returns in the iteration.
     * @return The iterator.
     */
    @Override
    public final PointIterator iterator(final int index) {
        return new DefaultIterator(array, (2*index)+lower(), upper());
    }

    /**
     * Returns the bounding box of all <var>x</var> and <var>y</var> ordinates.
     * If this array is empty, then this method returns <code>null</code>.
     */
    @Override
    public final Rectangle2D getBounds2D() {
        float xmin = Float.POSITIVE_INFINITY;
        float xmax = Float.NEGATIVE_INFINITY;
        float ymin = Float.POSITIVE_INFINITY;
        float ymax = Float.NEGATIVE_INFINITY;
        final int upper = upper();
        for (int i=lower(); i<upper;) {
            final float x = array[i++];
            final float y = array[i++];
            if (x<xmin) xmin=x;
            if (x>xmax) xmax=x;
            if (y<ymin) ymin=y;
            if (y>ymax) ymax=y;
        }
        if (xmin<=xmax && ymin<=ymax) {
            return new Rectangle2D.Float(xmin, ymin, xmax-xmin, ymax-ymin);
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
    public PointArray subarray(int lower, int upper) {
        final int thisLower=lower();
        final int thisUpper=upper();
        lower = lower*2 + thisLower;
        upper = upper*2 + thisLower;
        if (lower            == upper              ) return null;
        if (lower==thisLower && upper==thisUpper   ) return this;
        if (lower==0         && upper==array.length) return new DefaultArray(array);
        return new SubArray(array, lower, upper);
    }

    /**
     * Ins�re les donn�es de <code>this</code> dans le tableau sp�cifi�. Cette m�thode est
     * strictement r�serv�e � l'impl�mentation de {@link #insertAt(int,PointArray,boolean)}.
     * La classe {@link DefaultArray} remplace l'impl�mentation par d�faut par une nouvelle
     * impl�mentation qui �vite de copier les donn�es avec {@link #toArray()}.
     */
    @Override
    PointArray insertTo(final PointArray dest, final int index, final boolean reverse) {
        return dest.insertAt(index, array, lower(), upper(), reverse);
    }

    /**
     * Ins�re les donn�es (<var>x</var>,<var>y</var>) du tableau <code>toMerge</code> sp�cifi�.
     * Si le drapeau <code>reverse</code> � la valeur <code>true</code>, alors les points de
     * <code>toMerge</code> seront copi�es en ordre inverse.
     *
     * @param  index Index � partir d'o� ins�rer les points dans ce tableau. Le point � cet
     *         index ainsi que tous ceux qui le suivent seront d�cal�s vers des index plus �lev�s.
     * @param  toMerge Tableau de coordonn�es (<var>x</var>,<var>y</var>) � ins�rer dans ce
     *         tableau de points. Ses valeurs seront copi�es.
     * @param  lower Index de la premi�re coordonn�e de <code>toMerge</code> � copier dans ce tableau.
     * @param  upper Index suivant celui de la derni�re coordonn�e de <code>toMerge</code> � copier.
     * @param  reverse <code>true</code> s'il faut inverser l'ordre des points de <code>toMerge</code>
     *         lors de la copie. Cette inversion ne change pas l'ordre (<var>x</var>,<var>y</var>) des
     *         coordonn�es de chaque points.
     *
     * @return <code>this</code> si l'insertion � pu �tre faite sur
     *         place, ou un autre tableau si �a n'a pas �t� possible.
     */
    @Override
    public PointArray insertAt(final int index, final float toMerge[],
                               final int lower, final int upper, final boolean reverse)
    {
        int count = upper-lower;
        if (count == 0) {
            return this;
        }
        return new DynamicArray(array, lower(), upper(), 2*(count+Math.min(count, 256)))
                        .insertAt(index, toMerge, lower, upper, reverse);
    }

    /**
     * Renverse l'ordre de tous les points compris dans ce tableau.
     *
     * @return <code>this</code> si l'inversion a pu �tre faite sur-place,
     *         ou un autre tableau si �a n'a pas �t� possible.
     */
    @Override
    public PointArray reverse() {
        return new DynamicArray(array, lower(), upper(), 16).reverse();
    }

    /**
     * Retourne un tableau immutable qui contient les m�mes donn�es que celui-ci.
     * Apr�s l'appel de cette m�thode, toute tentative de modification (avec les
     * m�thodes {@link #insertAt} ou {@link #reverse}) vont retourner un autre
     * tableau de fa�on � ne pas modifier le tableau immutable.
     */
    @Override
    public PointArray getFinal(final CompressionLevel level) {
        if (CompressionLevel.RELATIVE_AS_BYTES.equals(level) && count() >= 8) {
            return new CompressedArray(array, lower(), upper());
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
        if (resolution2 == 0) {
            final int lower    = lower();
            final int length   = upper()-lower;
            final int capacity = offset + length;
            if (copy.length < capacity) {
                dest.array = copy = XArrays.resize(copy, capacity);
            }
            System.arraycopy(array, lower, copy, offset, length);
            dest.length = capacity;
        } else {
            int src = lower();
            int dst = offset;
            final int upper = upper();
            if (src < upper) {
                if (copy.length <= dst) {
                    dest.array = copy = XArrays.resize(copy, capacity(src, dst, offset));
                }
                float lastX = copy[dst++] = array[src++];
                float lastY = copy[dst++] = array[src++];
                while (src < upper) {
                    final float  x  = array[src++];
                    final float  y  = array[src++];
                    final double dx = (double)x - (double)lastX;
                    final double dy = (double)y - (double)lastY;
                    if ((dx*dx + dy*dy) >= resolution2) {
                        if (copy.length <= dst) {
                            dest.array = copy = XArrays.resize(copy, capacity(src, dst, offset));
                        }
                        copy[dst++] = lastX = x;
                        copy[dst++] = lastY = y;
                    }
                }
            }
            dest.length = dst;
        }
        assert dest.length <= dest.array.length;
    }
}
