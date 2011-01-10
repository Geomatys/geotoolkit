/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.legacy.array;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import javax.vecmath.MismatchedSizeException;

import org.geotoolkit.legacy.geom.CompressionLevel;
import org.geotoolkit.util.XArrays;
import org.geotoolkit.display.shape.XRectangle2D;


/**
 * An array wrapping a pair of <var>x</var> and <var>y</var> vectors. The vectors may be arrays of
 * any of Java primitive types: <code>double[]</code>, <code>float[]</code>, <code>long[]</code>,
 * <code>int[]</code>, <code>short[]</code>, <code>byte[]</code>, <code>char[]</code> (which may
 * be used as a kind of unsigned short) or <code>boolean[]</code> (0 or 1 values). The <var>x</var>
 * and <var>y</var> arrays doesn't need to be of the same type.
 *
 * Note: this implementation is not the fastest one. For maximal performance, consider using
 * {@link DefaultArray} instead.
 *
 * @version $Id: GenericArray.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 *
 * @see DefaultArray
 * @see JTSArray
 * @module pending
 */
public class GenericArray extends PointArray implements RandomAccess {
    /**
     * Serial version for compatibility with previous version.
     */
    private static final long serialVersionUID = 3451275073963894288L;

    /**
     * The <var>x</var> and <var>y</var> vectors.
     */
    private final Vector x,y;

    /**
     * Lower and upper index of valid data in <var>x</var> and <var>y</var> vectors.
     * The range goes from <code>lower</code> inclusive to <code>upper</code> exclusive.
     * <strong>Note:</strong> Methods {@link #lower()} and {@link #upper()} returns twice
     * those values, because of {@link PointArray} specification.
     */
    private final int lower, upper;

    /**
     * Construct a new array of points. The <var>x</var> and <var>y</var> arrays may be an array
     * of any of Java's primitive types, and doesn't need to be the same type. The array are stored
     * by reference (i.e. data are not copied).
     *
     * @param x <var>x</var> ordinates.
     * @param y <var>y</var> ordinates.
     * @throws ClassCastException if <var>x</var> and <var>y</var> are not arrays
     *         of a primitive type.
     * @throws MismatchedSizeException if arrays doesn't have the same length.
     */
    public GenericArray(final Object x, final Object y) throws ClassCastException,
                                                               MismatchedSizeException
    {
        this.x = Vector.wrap(x);
        this.y = Vector.wrap(y);
        this.lower = 0;
        this.upper = Array.getLength(x);
        if (upper != Array.getLength(y)) {
            throw new MismatchedSizeException();
        }
    }

    /**
     * Construct a new array of points. The <var>x</var> and <var>y</var> arrays may be an array
     * of any of Java's primitive types, and doesn't need to be the same type. The array are stored
     * by reference (i.e. data are not copied).
     *
     * @param x <var>x</var> ordinates.
     * @param y <var>y</var> ordinates.
     * @param lower Index of lower point, inclusive.
     * @param upper Index of upper point, exclusive.
     * @throws ClassCastException if <var>x</var> and <var>y</var> are not arrays
     *         of a primitive type.
     * @throws MismatchedSizeException if arrays doesn't have the same length.
     */
    public GenericArray(final Object x, final Object y,
                        final int lower, final int upper) throws ClassCastException
    {
        this.x = Vector.wrap(x);
        this.y = Vector.wrap(y);
        this.lower = lower;
        this.upper = upper;
        checkRange();
    }

    /**
     * Construct a new array clipped in the specified bounds.
     *
     * @param root  The data to use. The array will be shared.
     * @param lower The lower index, inclusive.
     * @param upper The upper index, exclusive.
     */
    private GenericArray(final GenericArray root, final int lower, final int upper) {
        this.x     = root.x;
        this.y     = root.y;
        this.lower = lower;
        this.upper = upper;
        checkRange();
    }

    /**
     * Check the [{@link #lower}..{@link #upper}] range.
     */
    private void checkRange() {
        if (lower<0) {
            throw new IllegalArgumentException(String.valueOf(lower));
        }
        if (upper<lower || upper>Math.min(x.length(), y.length())) {
            throw new IllegalArgumentException(String.valueOf(upper));
        }
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
     * Returns the number of points in this array.
     */
    @Override
    public int count() {
        return upper-lower;
    }

    /**
     * Returns an estimation of memory usage in bytes.
     */
    @Override
    public long getMemoryUsage() {
        return (long)count()*(x.sizeof() + y.sizeof())/8 + 24;
    }
    
    /**
     * Returns the first point in this array. If <code>point</code> is null, a new
     * {@link Point2D} object is allocated and then the result is stored in this object.
     *
     * @param  point The object in which to store the first point, or <code>null</code>.
     * @return <code>point</code> or a new {@link Point2D}, which contains the first point.
     */
    @Override
    public Point2D getFirstPoint(final Point2D point) {
        if (point != null) {
            point.setLocation(x.getAsDouble(lower), y.getAsDouble(lower));
            return point;
        } else {
            return getValue(0);
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
    public Point2D getLastPoint(final Point2D point) {
        final int i = upper-1;
        if (point != null) {
            point.setLocation(x.getAsDouble(i), y.getAsDouble(i));
            return point;
        } else {
            return getValue(i-lower);
        }
    }

    /**
     * Returns the point at the specified index.
     *
     * @param  i The index from 0 inclusive to {@link #count} exclusive.
     * @return The point at the given index.
     * @throws IndexOutOfBoundsException if <code>index</code> is out of bounds.
     */
    @Override
    public Point2D getValue(int i) throws IndexOutOfBoundsException {
        if (i>=0) {
            i += lower;
            if (i<upper) {
                switch (Math.max(x.type(), y.type())) {
                    case 0:  return new Point         (x.getAsInteger(i), y.getAsInteger(i));
                    case 1:  return new Point2D.Float (x.getAsFloat  (i), y.getAsFloat  (i));
                    default: return new Point2D.Double(x.getAsDouble (i), y.getAsDouble (i));
                }
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
    public PointIterator iterator(final int index) {
        return new GenericIterator(x, y, index+lower, upper);
    }

    /**
     * Returns an iterator object that iterates along the point coordinates.
     * If an optional affine transform is specified, the coordinates returned
     * in the iteration are transformed accordingly.
     *
     * @see #toShape
     * @see #getBounds2D
     */
    @Override
    final PathIterator getPathIterator(final AffineTransform at) {
        return new Iterator(at);
    }
    
    /**
     * Returns the bounding box of all <var>x</var> and <var>y</var> ordinates.
     * If this array is empty, then this method returns <code>null</code>.
     */
    @Override
    public Rectangle2D getBounds2D() {
        final XRectangle2D bounds = XRectangle2D.createFromExtremums(x.minimum, y.minimum,
                                                                     x.maximum, y.maximum);
        return bounds.isEmpty() ? null : bounds;
    }

    /**
     * Construct a new array clipped to the specified rectangle. This method will clip the
     * array only if it can be done in a cheap way. It is legal for this method to returns
     * <code>this</code> if the clip is not cheap.
     *
     * @param  clip The clip, or <code>null</code> for none.
     * @return The clipped array, or <code>null</code>.
     */
    public GenericArray clip(final Rectangle2D clip) {
        if (clip != null) {
            int lower = this.lower;
            int upper = this.upper;
            if (x.isSorted) {
                final int min = x.search(clip.getMinX());
                final int max = x.search(clip.getMaxX());
                if (min>lower) lower=min;
                if (max<upper) upper=max;
            }
            if (y.isSorted) {
                final int min = y.search(clip.getMinY());
                final int max = y.search(clip.getMaxY());
                if (min>lower) lower=min;
                if (max<upper) upper=max;
            }
            if (lower!=this.lower || upper!=this.upper) {
                return new GenericArray(this, lower, upper);
            }
        }
        return this;
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
        lower += this.lower;
        upper += this.lower;
        if (lower             == upper            ) return null;
        if (lower==this.lower && upper==this.upper) return this;
        return new GenericArray(this, lower, upper);
    }

    /**
     * Ins�re les donn�es (<var>x</var>,<var>y</var>) du tableau <code>toMerge</code> sp�cifi�.
     */
    @Override
    public PointArray insertAt(final int index, final float[] toMerge, final int lower, final int upper, final boolean reverse) {
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
    public PointArray getFinal(final CompressionLevel level) {
        if (level!=null && count()>=8 && Math.max(x.type(), y.type())>=2) {
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
    public void toArray(final ArrayData dest, final float resolution2) {
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
            float lastX, lastY;
            copy[dst++] = (lastX = x.getAsFloat(src));
            copy[dst++] = (lastY = y.getAsFloat(src));
            while (++src < upper) {
                final float sx = x.getAsFloat(src);
                final float sy = y.getAsFloat(src);
                final float dx = sx - lastX;
                final float dy = sy - lastY;
                if ((dx*dx + dy*dy) >= resolution2) {
                    if (copy.length <= dst) {
                        dest.array = copy = XArrays.resize(copy, capacity(2*src, dst, offset));
                    }
                    copy[dst++] = (lastX = sx);
                    copy[dst++] = (lastY = sy);
                }
            }
        }
        dest.length = dst;
        assert dest.length <= dest.array.length;
    }    

    /**
     * A path iterator for the data.
     *
     * @version $Id: GenericArray.java 17672 2006-01-19 00:25:55Z desruisseaux $
     * @author Martin Desruisseaux
     */
    private final class Iterator implements PathIterator {
        /** The current index in the iteration. */
        private int index = lower;

        /** The value to returns. */
        private int move = SEG_MOVETO;

        /** The affine transform. */
        private final AffineTransform at;

        /** Construct an iterator */
        public Iterator(final AffineTransform at) {
            this.at = at;
            while (index < upper) {
                if (!x.isNaN(index) && !y.isNaN(index)) {
                    break;
                }
                index++;
            }
        }
        
        /** Returns the winding rule for determining the interior of the path. */
        public int getWindingRule() {
            return WIND_EVEN_ODD;
        }
        
        /** Tests if the iteration is complete. */
        public boolean isDone() {
            return index >= upper;
        }

        /** Returns the coordinates and type of the current path segment in the iteration. */
        public int currentSegment(final double[] coords) {
            coords[0] = x.getAsDouble(index);
            coords[1] = y.getAsDouble(index);
            if (at != null) {
                at.transform(coords, 0, coords, 0, 1);
            }
            return move;
        }
        
        /** Returns the coordinates and type of the current path segment in the iteration. */
        public int currentSegment(final float[] coords) {
            coords[0] = x.getAsFloat(index);
            coords[1] = y.getAsFloat(index);
            if (at != null) {
                at.transform(coords, 0, coords, 0, 1);
            }
            return move;
        }
        
        /** Moves the iterator to the next segment of the path. */
        public void next() {
            move = SEG_LINETO;
            while (++index < upper) {
                if (!x.isNaN(index) && !y.isNaN(index)) {
                    break;
                }
                move = SEG_MOVETO;
            }
        }
    }

    /**
     * Wrap an array of <code>double</code>, <code>float</code>, <code>long</code>,
     * <code>int</code>, <code>short</code> or <code>byte</code> data.
     *
     * @version $Id: GenericArray.java 17672 2006-01-19 00:25:55Z desruisseaux $
     * @author Martin Desruisseaux
     */
    static abstract class Vector implements Serializable {
        /**
         * Wrap the specified array in a vector.
         */
        public static Vector wrap(final Object array) {
            if (array instanceof double []) return new Double   ((double []) array);
            if (array instanceof float  []) return new Float    ((float  []) array);
            if (array instanceof long   []) return new Long     ((long   []) array);
            if (array instanceof int    []) return new Integer  ((int    []) array);
            if (array instanceof short  []) return new Short    ((short  []) array);
            if (array instanceof byte   []) return new Byte     ((byte   []) array);
            if (array instanceof char   []) return new Character((char   []) array);
            if (array instanceof boolean[]) return new Boolean  ((boolean[]) array);
            throw new ClassCastException(array.getClass().getName());
        }

        /**
         * <code>true</code> if the data are sorted in increasing order
         * and do not contains any NaN value.
         */
        protected final boolean isSorted;

        /**
         * The minimum and maximum value found in the data.
         * will be set the subclass's constructors.
         */
        protected double minimum = java.lang.Double.POSITIVE_INFINITY,
                         maximum = java.lang.Double.NEGATIVE_INFINITY;

        /** Contruct a vector. */
        public Vector(final boolean isSorted) {
            this.isSorted = isSorted;
        }

        /** Returns the size of each elements, in bits. */
        public abstract int sizeof();

        /** Returns the type: 0=integer, 1=floating point (single), 2=floating point (double). */
        public int type() {return 0;}

        /** Returns the array length. */
        public abstract int length();

        /** Tests if the value at the specified index is NaN. */
        public boolean isNaN(final int index) {return false;}

        /** Returns the value at the specified index. */
        public abstract double getAsDouble(int index);

        /** Returns the value at the specified index. */
        public abstract float getAsFloat(int index);

        /** Returns the value at the specified index. */
        public abstract int getAsInteger(int index);

        /** Search a value. This method should be invoked only if {@link #isSorted} is true. */
        protected abstract int binarySearch(double value);

        /** Search a value. This method should be invoked only if {@link #isSorted} is true. */
        public final int search(final double value) {
            if (value<minimum) return 0;
            if (value>maximum) return length();
            final int index = binarySearch(value);
            return (index>=0) ? index : ~index; // Tild sign (bits inversion), not minus.
        }
    }

    /** Vector for the <code>double</code> type. */
    private static final class Double extends Vector {
        private final double[] array;
        public Double(final double[] array) {
            super(XArrays.isSorted(array, false) && !XArrays.hasNaN(array));
            this.array = array;
            for (int i=array.length; --i>=0;) {
                final double value = array[i];
                if (value<minimum) minimum=value;
                if (value>maximum) maximum=value;
            }
        }
        public int     type        ()         {return 2;}
        public int     sizeof      ()         {return 64;}
        public int     length      ()         {return array.length;}
        public double  getAsDouble (final int i)    {return        array[i];}
        public float   getAsFloat  (final int i)    {return (float)array[i];}
        public int     getAsInteger(final int i)    {return (int)  array[i];}
        public boolean isNaN       (final int i)    {return java.lang.Double.isNaN(array[i]);}
        protected int  binarySearch(final double v) {return Arrays.binarySearch(array, v);}
    }

    /** Vector for the <code>float</code> type. */
    private static final class Float extends Vector {
        private final float[] array;
        public Float(final float[] array) {
            super(XArrays.isSorted(array, false) && !XArrays.hasNaN(array));
            this.array = array;
            for (int i=array.length; --i>=0;) {
                final double value = array[i];
                if (value<minimum) minimum=value;
                if (value>maximum) maximum=value;
            }
        }
        public int     type        ()         {return 1;}
        public int     sizeof      ()         {return 32;}
        public int     length      ()         {return array.length;}
        public double  getAsDouble (final int i)    {return array[i];}
        public float   getAsFloat  (final int i)    {return array[i];}
        public int     getAsInteger(final int i)    {return (int)array[i];}
        public boolean isNaN       (final int i)    {return java.lang.Float.isNaN(array[i]);}
        protected int  binarySearch(final double v) {return Arrays.binarySearch(array, (float)v);}
    }

    /** Vector for the <code>long</code> type. */
    private static final class Long extends Vector {
        private final long[] array;
        public Long(final long[] array) {
            super(XArrays.isSorted(array, false));
            this.array = array;
            for (int i=array.length; --i>=0;) {
                final double value = array[i];
                if (value<minimum) minimum=value;
                if (value>maximum) maximum=value;
            }
        }
        public int    type        ()         {return 2;}
        public int    sizeof      ()         {return 64;}
        public int    length      ()         {return array.length;}
        public double getAsDouble (final int i)    {return array[i];}
        public float  getAsFloat  (final int i)    {return array[i];}
        public int    getAsInteger(final int i)    {return (int)array[i];}
        protected int binarySearch(final double v) {return Arrays.binarySearch(array, (long)Math.floor(v));}
    }

    /** Vector for the <code>int</code> type. */
    private static final class Integer extends Vector {
        private final int[] array;
        public Integer(final int[] array) {
            super(XArrays.isSorted(array, false));
            this.array = array;
            for (int i=array.length; --i>=0;) {
                final double value = array[i];
                if (value<minimum) minimum=value;
                if (value>maximum) maximum=value;
            }
        }
        public int    sizeof      ()         {return 32;}
        public int    length      ()         {return array.length;}
        public double getAsDouble (final int i)    {return array[i];}
        public float  getAsFloat  (final int i)    {return array[i];}
        public int    getAsInteger(final int i)    {return array[i];}
        protected int binarySearch(final double v) {return Arrays.binarySearch(array, (int)Math.floor(v));}
    }

    /** Vector for the <code>short</code> type. */
    private static final class Short extends Vector {
        private final short[] array;
        public Short(final short[] array) {
            super(XArrays.isSorted(array, false));
            this.array = array;
            for (int i=array.length; --i>=0;) {
                final double value = array[i];
                if (value<minimum) minimum=value;
                if (value>maximum) maximum=value;
            }
        }
        public int    sizeof      ()         {return 16;}
        public int    length      ()         {return array.length;}
        public double getAsDouble (final int i)    {return array[i];}
        public float  getAsFloat  (final int i)    {return array[i];}
        public int    getAsInteger(final int i)    {return array[i];}
        protected int binarySearch(final double v) {return Arrays.binarySearch(array, (short)Math.floor(v));}
    }

    /** Vector for the <code>byte</code> type. */
    private static final class Byte extends Vector {
        private final byte[] array;
        public Byte(final byte[] array) {
            super(XArrays.isSorted(array, false));
            this.array = array;
            for (int i=array.length; --i>=0;) {
                final double value = array[i];
                if (value<minimum) minimum=value;
                if (value>maximum) maximum=value;
            }
        }
        public int    sizeof      ()         {return 8;}
        public int    length      ()         {return array.length;}
        public double getAsDouble (final int i)    {return array[i];}
        public float  getAsFloat  (final int i)    {return array[i];}
        public int    getAsInteger(final int i)    {return array[i];}
        protected int binarySearch(final double v) {return Arrays.binarySearch(array, (byte)Math.floor(v));}
    }

    /** Vector for the <code>char</code> type. */
    private static final class Character extends Vector {
        private final char[] array;
        public Character(final char[] array) {
            super(XArrays.isSorted(array, false));
            this.array = array;
            for (int i=array.length; --i>=0;) {
                final double value = array[i];
                if (value<minimum) minimum=value;
                if (value>maximum) maximum=value;
            }
        }
        public int    sizeof      ()         {return 16;}
        public int    length      ()         {return array.length;}
        public double getAsDouble (final int i)    {return array[i];}
        public float  getAsFloat  (final int i)    {return array[i];}
        public int    getAsInteger(final int i)    {return array[i];}
        protected int binarySearch(final double v) {return Arrays.binarySearch(array, (char)Math.floor(v));}
    }

    /** Vector for the <code>boolean</code> type. */
    private static final class Boolean extends Vector {
        private final boolean[] array;
        public Boolean(final boolean[] array) {
            super(false);
            this.array   = array;
            this.minimum = 0;
            this.maximum = 1;
        }
        public int    sizeof      ()         {return 1;}
        public int    length      ()         {return array.length;}
        public double getAsDouble (final int i)    {return array[i] ? 0 : 1;}
        public float  getAsFloat  (final int i)    {return array[i] ? 0 : 1;}
        public int    getAsInteger(final int i)    {return array[i] ? 0 : 1;}
        protected int binarySearch(final double v) {throw new UnsupportedOperationException();}
    }
}
