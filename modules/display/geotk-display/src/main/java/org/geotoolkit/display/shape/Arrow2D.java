/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1998-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.NoSuchElementException;
import static java.lang.Double.doubleToLongBits;


/**
 * Arrow oriented toward positives <var>x</var> values (0° arithmetic). This shape doesn't
 * have direct support for rotation. To rotate the arrow toward an other direction, use
 * {@link AffineTransform}. See also the example documented in the {@link TransformedShape}
 * class.
 * <p>
 * The following picture shows the default {@code Arrow2D} appearance. The relative size
 * of the tail can be modified by {@link #setTailProportion}.
 *
 * <center><img src="doc-files/Arrow2D.png"></center>
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 1.0
 * @module
 */
public class Arrow2D extends RectangularShape implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 5093131349056679731L;

    /**
     * Minimal <var>x</var> et <var>y</var> coordinate values.
     */
    private double minX, minY;

    /**
     * Longueur de la flèche. Cette longueur est mesurée horizontalement (selon
     * l'axe des <var>x</var>) de la queue jusqu'à la pointe de la flèche.
     */
    private double length;

    /**
     * Largeur de la flèche. Cette largeur est mesurée verticalement (selon l'axe
     * des <var>y</var>) le long de la partie la plus large de cette flèche.
     */
    private double thickness;

    /**
     * The arrow's thickness at the tail ({@code x == minX}), as a proportion of the
     * {@linkplain #thickness maximal thickness}. Should be a factor between 0 and 1.
     */
    private double sy0 = 0;

    /**
     * The arrow's thickness at the base ({@code x == minX+sx*length}), as a proportion of
     * the {@linkplain #thickness maximal thickness}. Should be a factor between 0 and 1.
     */
    private double sy1 = 1.0 / 3;

    /**
     * The base position, as a factor of the total length. Should be a factor between 0 and 1.
     */
    private double sx = 2.0 / 3;

    /**
     * Creates a new arrow with a null surface.
     */
    public Arrow2D() {
    }

    /**
     * Creates new arrow in the specified {@linkplain #setFrame(double,double,double,double) frame}.
     *
     * @param x      Minimal <var>x</var> value.
     * @param y      Minimal <var>y</var> value.
     * @param width  The length in <var>x</var> direction.
     * @param height The length in <var>y</var> direction.
     */
    public Arrow2D(final double x, final double y, final double width, final double height) {
        this.minX      = x;
        this.minY      = y;
        this.length    = width;
        this.thickness = height;
    }

    /**
     * Sets the tail width and height, relative to the arrow width and height.
     * All given number must be in the [0 &hellip; 1] range.
     *
     * @param sx  The position where the arrow's head starts, relative to the total
     *            arrow's {@linkplain #getWidth width}.
     * @param sy1 The height of the arrow's tail at the position where the head start
     *            (<var>sx</var>), relative to the arrow's {@linkplain #getHeight height}.
     * @param sy0 The height of the arrow's tail at the leftmore position, relative to the
     *            arrow's {@linkplain #getHeight height}.
     */
    public void setTailProportion(double sx, double sy1, double sy0) {
        this.sy1 = Math.max(0, Math.min(1, sy1));
        this.sy0 = Math.max(0, Math.min(1, sy0));
        this.sx  = Math.max(0, Math.min(1, sx ));
    }

    /**
     * Returns the length of the arrow's tail. This length is measured along the <var>x</var>
     * axis.
     *
     * @return The length of the arrow's tail.
     */
    public double getTailLength() {
        return sx * length;
    }

    /**
     * Returns the minimal <var>x</var> coordinate of the smallest
     * {@linkplain #getBounds2D bounding box} that contains fully this arrow.
     */
    @Override
    public double getX() {
        return minX;
    }

    /**
     * Returns the minimal <var>y</var> coordinate of the smallest
     * {@linkplain #getBounds2D bounding box} that contains fully this arrow.
     */
    @Override
    public double getY() {
        return minY;
    }

    /**
     * Returns the width of the smallest {@linkplain #getBounds2D bounding box} that contains
     * fully this arrow.
     */
    @Override
    public double getWidth() {
        return length;
    }

    /**
     * Returns the height of the smallest {@linkplain #getBounds2D bounding box} that contains
     * fully this arrow.
     */
    @Override
    public double getHeight() {
        return thickness;
    }

    /**
     * Returns the arrow height at the given <var>x</var> ordinate. If the given position
     * is not between {@code getMinX()} and {@code getMaxX()}, then this method returns 0.
     *
     * @param  x Ordinate <var>x</var> where to get the arrow height.
     * @return The arrow height at the given <var>x</var> ordinate, as a number
     *         between 0 and {@code getHeight()}.
     */
    public double getHeight(double x) {
        x = (x-minX) / (sx*length);
        if (x<0 || x>1) {
            return 0;
        } else if (x <= 1) {
            return (sy0 + (sy1 - sy0)*x)*thickness;
        } else {
            return (x-1) * sx / (1-sx) * thickness;
        }
    }

    /**
     * Determines whether the arrow is empty.
     */
    @Override
    public boolean isEmpty() {
        return !(length>0 && thickness>0);  // Uses '!' in order to catch NaN.
    }

    /**
     * Sets the location and size of the framing rectangle of this arrow to the specified
     * rectangular values.
     *
     * @param x      The minimal <var>x</var> value.
     * @param y      The minimal <var>y</var> value.
     * @param width  The length in <var>x</var> direction.
     * @param height The length in <var>y</var> direction.
     */
    @Override
    public void setFrame(final double x, final double y, final double width, final double height) {
        this.minX      = x;
        this.minY      = y;
        this.length    = width;
        this.thickness = height;
    }

    /**
     * Returns the bounding box for this arrow. This is the smallest rectangle that
     * contains fully this arrow.
     */
    @Override
    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Double(minX, minY, length, thickness);
    }

    /**
     * Tests if the specified point is inside this shape.
     *
     * @param x The <var>x</var> coordinate to test.
     * @param y The <var>y</var> coordinate to test.
     */
    @Override
    public boolean contains(final double x, double y) {
        if (x < minX) {
            return false;
        }
        final double base = minX + sx*length;
        if (x <= base) {
            /*
             * Point dans la queue. Vérifie s'il se trouve dans le triangle...
             */
            double yMaxAtX = 0.5*thickness;
            y -= (minY + yMaxAtX);
            yMaxAtX *= sy0 + (sy1-sy0) * ((x-minX) / (base-minX));
            return (Math.abs(y) <= yMaxAtX);
        } else {
            /*
             * Point dans la pointe. Vérifie s'il se trouve dans le triangle.
             */
            final double maxX = minX + length;
            if (x > maxX) {
                return false;
            }
            double yMaxAtX = 0.5*thickness;
            y -= (minY + yMaxAtX);
            yMaxAtX *= (maxX-x) / (maxX-base);
            return Math.abs(y) <= yMaxAtX;
        }
    }

    /**
     * Tests if the interior of this arrow entirely contains the specified rectangle.
     *
     * @param x The minimal <var>x</var> value.
     * @param y The minimal <var>y</var> value.
     * @param width  The rectangle width.
     * @param height The rectangle height.
     * @return {@code true} if the interior of this arrow contains the rectangle.
     */
    @Override
    public boolean contains(final double x, final double y, final double width, final double height) {
        return contains(x      , y       ) &&
               contains(x+width, y       ) &&
               contains(x+width, y+height) &&
               contains(x      , y+height);
    }

    /**
     * Tests if the interior of this arrow intersects the interior of the specified rectangle.
     *
     * @param x The minimal <var>x</var> value.
     * @param y The minimal <var>y</var> value.
     * @param width  The rectangle width.
     * @param height The rectangle height.
     * @return {@code true} if the interior of this arrow intersects the interior of the rectangle.
     */
    @Override
    public boolean intersects(final double x, final double y, final double width, final double height) {
        final double right = x + width;
        final double maxX  = minX + length;
        if (x <= maxX  &&  right >= minX) {
            final double top = y + height;
            final double maxY = minY + thickness;
            if (y <= maxY  &&  top >= minY) {
                /*
                 * The rectangle intersects this arrow's bounding box. Now, check if a
                 * rectangle corner is outside the arrow (while in the bounding box).
                 * If such a case is found, returns false.
                 */
                final double base = minX + length*sx;
                if (x > base) {
                    double yMaxAtX = 0.5*thickness;
                    final double centerY = minY + yMaxAtX;
                    if (y >= centerY) {
                        yMaxAtX *= (maxX-x)/(maxX-base);
                        if (!(y-centerY <= yMaxAtX)) {
                            return false;
                        }
                    } else if (top <= centerY) {
                        yMaxAtX *= (maxX-x) / (maxX-base);
                        if (!(centerY-top <= yMaxAtX)) {
                            return false;
                        }
                    }
                } else if (right < base) {
                    double yMaxAtX = 0.5*thickness;
                    final double centerY = minY + yMaxAtX;
                    if (y >= centerY) {
                        yMaxAtX *= sy0 + (sy1-sy0)*((x-minX) / (base-minX));
                        if (!(y-centerY <= yMaxAtX)) {
                            return false;
                        }
                    } else if (top <= centerY) {
                        yMaxAtX *= sy0 + (sy1-sy0)*((x-minX) / (base-minX));
                        if (!(centerY-top <= yMaxAtX)) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an iterator for this arrow. Because this shape is made only of straight
     * segments, this method ignores the {@code flatness} argument and delegates to
     * <code>{@linkplain #getPathIterator(AffineTransform) getPathIterator}(at)</code>
     *
     * @param at An optional affine transform to apply, or {@code null} if none.
     */
    @Override
    public PathIterator getPathIterator(final AffineTransform at, final double flatness) {
        return getPathIterator(at);
    }

    /**
     * Returns an iterator for this arrow.
     *
     * @param at An optional affine transform to apply, or {@code null} if none.
     */
    @Override
    public PathIterator getPathIterator(final AffineTransform at) {
        return new Iterator(at);
    }

    /**
     * The iterator for the enclosing arrow shape.
     */
    private class Iterator implements PathIterator {
        /**
         * An optional affine transform to apply, or {@code null} if none.
         */
        private final AffineTransform at;

        /**
         * Frequently used constants computed once for ever at construction time.
         */
        private final double halfBottom0, halfBottom1, center, halfTop1, halfTop0, base;

        /**
         * Indicates which arrow edge will be the next one to be returned.
         */
        private int code;

        /**
         * Creates an iterator for the enclosing arrow shape.
         *
         * @param at An optional affine transform to apply, or {@code null} if none.
         */
        Iterator(final AffineTransform at) {
            this.at = at;
            final double halfheight = 0.5*thickness;
            halfBottom0 = minY + halfheight * (1-sy0);
            halfBottom1 = minY + halfheight * (1-sy1);
            center      = minY + halfheight;
            halfTop1    = minY + halfheight * (1+sy1);
            halfTop0    = minY + halfheight * (1+sy0);
            base        = minX + sx*length;
        }

        /**
         * Returns the winding rule, which is always {@link #WIND_EVEN_ODD}.
         */
        @Override
        public int getWindingRule() {
            return WIND_EVEN_ODD;
        }

        /**
         * Move to the next segment.
         */
        @Override
        public void next() {
            code++;
        }

        /**
         * Returns the coordinates for the current segment.
         */
        @Override
        public int currentSegment(final float[] coords) {
            switch (code) {
                case 0: coords[0]=(float) minX;          coords[1]=(float) halfBottom0;      break;
                case 1: coords[0]=(float) base;          coords[1]=(float) halfBottom1;      break;
                case 2: coords[0]=(float) base;          coords[1]=(float) minY;             break;
                case 3: coords[0]=(float) (minX+length); coords[1]=(float) center;           break;
                case 4: coords[0]=(float) base;          coords[1]=(float) (minY+thickness); break;
                case 5: coords[0]=(float) base;          coords[1]=(float) halfTop1;         break;
                case 6: coords[0]=(float) minX;          coords[1]=(float) halfTop0;         break;
                case 7: coords[0]=(float) minX;          coords[1]=(float) halfBottom0;      break;
                case 8:  return SEG_CLOSE;
                default: throw new NoSuchElementException();
            }
            if (at!=null) {
                at.transform(coords, 0, coords, 0, 1);
            }
            return (code == 0) ? SEG_MOVETO : SEG_LINETO;
        }

        /**
         * Returns the coordinates for the current segment.
         */
        @Override
        public int currentSegment(final double[] coords)  {
            switch (code) {
                case 0: coords[0]=minX;        coords[1]=halfBottom0;    break;
                case 1: coords[0]=base;        coords[1]=halfBottom1;    break;
                case 2: coords[0]=base;        coords[1]=minY;           break;
                case 3: coords[0]=minX+length; coords[1]=center;         break;
                case 4: coords[0]=base;        coords[1]=minY+thickness; break;
                case 5: coords[0]=base;        coords[1]=halfTop1;       break;
                case 6: coords[0]=minX;        coords[1]=halfTop0;       break;
                case 7: coords[0]=minX;        coords[1]=halfBottom0;    break;
                case 8:  return SEG_CLOSE;
                default: throw new NoSuchElementException();
            }
            if (at!=null) {
                at.transform(coords, 0, coords, 0, 1);
            }
            return (code==0) ? SEG_MOVETO : SEG_LINETO;
        }

        /**
         * Returns {@code true} if there is no more point to iterate.
         */
        @Override
        public boolean isDone() {
            return code > 8;
        }
    }

    /**
     * Compares this arrow with the specified object for equality.
     *
     * @param  object The object to compare with this arrow for equality.
     * @return {@code true} if the given object is equal to this arrow.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object != null && getClass() == object.getClass()) {
            final Arrow2D cast = (Arrow2D) object;
            return doubleToLongBits(thickness) == doubleToLongBits(cast.thickness) &&
                   doubleToLongBits(length   ) == doubleToLongBits(cast.length   ) &&
                   doubleToLongBits(minX     ) == doubleToLongBits(cast.minX     ) &&
                   doubleToLongBits(minY     ) == doubleToLongBits(cast.minY     ) &&
                   doubleToLongBits(sx       ) == doubleToLongBits(cast.sx       ) &&
                   doubleToLongBits(sy0      ) == doubleToLongBits(cast.sy1      ) &&
                   doubleToLongBits(sy1      ) == doubleToLongBits(cast.sy0      );
        } else {
            return false;
        }
    }

    /**
     * Returns a hash value for this arrow.
     */
    @Override
    public int hashCode() {
        final long code = doubleToLongBits(thickness) + 31*
                         (doubleToLongBits(length   ) + 31*
                         (doubleToLongBits(minX     ) + 31*
                         (doubleToLongBits(minY     ) + 31*
                         (doubleToLongBits(sx       ) + 31*
                         (doubleToLongBits(sy0      ) + 31*
                         (doubleToLongBits(sy1)))))));
        return (int) code + (int) (code >>> 32);
    }
}
