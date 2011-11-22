/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.geometry;

import java.util.Objects;
import java.awt.geom.Rectangle2D;

import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.cs.AxisDirection;

import org.geotoolkit.util.Cloneable;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import static java.lang.Double.NaN;
import static org.geotoolkit.math.XMath.isPositive;
import static org.geotoolkit.math.XMath.isNegative;
import static org.geotoolkit.math.XMath.isSameSign;
import static org.geotoolkit.geometry.AbstractEnvelope.*;


/**
 * A two-dimensional envelope on top of {@link Rectangle2D}. This implementation is provided for
 * inter-operability between Java2D and GeoAPI.
 * <p>
 * This class inherits {@linkplain #x x} and {@linkplain #y y} fields. But despite their names,
 * they don't need to be oriented toward {@linkplain AxisDirection#EAST East} and
 * {@linkplain AxisDirection#NORTH North} respectively. The (<var>x</var>,<var>y</var>) axis can
 * have any orientation and should be understood as "ordinate 0" and "ordinate 1" values instead.
 * This is not specific to this implementation; in Java2D too, the visual axis orientation depend
 * on the {@linkplain java.awt.Graphics2D#getTransform affine transform in the graphics context}.
 *
 * {@section Crossing the anti-meridian}
 * Negative values in the {@linkplain #width width} or {@linkplain #height height} fields are
 * interpreted as an envelope crossing the anti-meridian, providing that the corresponding
 * {@linkplain CoordinateSystemAxis#getRangeMeaning() axis range meaning} is
 * {@link org.opengis.referencing.cs.RangeMeaning#WRAPAROUND WRAPAROUND}.
 * All the following methods are anti-meridian aware:
 * <p>
 * <ul>
 *   <li>{@link #getWidth()}</li>
 *   <li>{@link #getHeight()}</li>
 *   <li>{@link #getCenterX()}</li>
 *   <li>{@link #getCenterY()}</li>
 *   <li>{@link #isEmpty()}</li>
 *   <li>{@link #contains(double,double)}</li>
 *   <li>{@link #contains(Rectangle2D)} and its variant receiving {@code double} arguments</li>
 *   <li>{@link #intersects(Rectangle2D)} and its variant receiving {@code double} arguments</li>
 * </ul>
 * <p>
 * The {@link #getSpan(int)} and {@link #getMedian(int)} methods delegate to the methods listed
 * above.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see GeneralEnvelope
 * @see org.geotoolkit.geometry.jts.ReferencedEnvelope
 * @see org.opengis.metadata.extent.GeographicBoundingBox
 *
 * @since 2.1
 * @module
 */
public class Envelope2D extends Rectangle2D.Double implements Envelope, Cloneable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -3319231220761419350L;

    /**
     * The coordinate reference system, or {@code null}.
     */
    private CoordinateReferenceSystem crs;

    /**
     * Constructs an initially empty envelope with no CRS.
     *
     * @since 2.5
     */
    public Envelope2D() {
    }

    /**
     * Creates a new envelope from the given bounding box. This constructor can not be public,
     * because the {@code xmax} and {@code ymax} arguments are not the ones usually expected for
     * {@link Rectangle2D} objects (the standard arguments are {@code width} and {@code height}).
     * Making this constructor public would probably be a too high risk of confusion.
     * <p>
     * This constructor is needed because the other constructors (expecting envelopes or other
     * rectangles) can not query directly the {@link Envelope#getSpan(int)} or equivalent methods,
     * because the return value is not the one expected by this class when the envelope spans the
     * anti-meridian.
     * <p>
     * It is caller's responsibility to invoke {@link #ensureValidRanges()} after this constructor.
     * We do not check for the range in this constructor because the caller may want to set the CRS
     * first.
     */
    private Envelope2D(final double xmin, final double ymin, final double xmax, final double ymax) {
        super(xmin, ymin, xmax - xmin, ymax - ymin);
    }

    /**
     * Constructs two-dimensional envelope defined by an other {@link Envelope}.
     *
     * @param envelope The envelope to copy.
     * @throws MismatchedDimensionException If the given envelope is not two-dimensional.
     * @throws IllegalArgumentException If the given range of ordinate values is invalid.
     *
     * @see GeneralEnvelope#GeneralEnvelope(Envelope)
     */
    public Envelope2D(final Envelope envelope) throws MismatchedDimensionException {
        this(envelope.getMinimum(0), envelope.getMinimum(1),
             envelope.getMaximum(0), envelope.getMaximum(1));
        final int dimension = envelope.getDimension();
        if (dimension != 2) {
            throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.NOT_TWO_DIMENSIONAL_$1, dimension));
        }
        setCoordinateReferenceSystem(envelope.getCoordinateReferenceSystem());
        ensureValidRanges();
    }

    /**
     * Constructs a new envelope with the same data than the specified
     * geographic bounding box. The coordinate reference system is set
     * to {@linkplain DefaultGeographicCRS#WGS84 WGS84}.
     *
     * @param box The bounding box to copy.
     * @throws IllegalArgumentException If the given range of ordinate values is invalid.
     *
     * @see GeneralEnvelope#GeneralEnvelope(GeographicBoundingBox)
     *
     * @since 3.11
     */
    public Envelope2D(final GeographicBoundingBox box) {
        this(box.getWestBoundLongitude(),
             box.getSouthBoundLatitude(),
             box.getEastBoundLongitude(),
             box.getNorthBoundLatitude());
        crs = DefaultGeographicCRS.WGS84;
        ensureValidRanges();
    }

    /**
     * Constructs two-dimensional envelope defined by an other {@link Rectangle2D}.
     *
     * @param crs The coordinate reference system, or {@code null}.
     * @param rect The rectangle to copy.
     * @throws IllegalArgumentException If the given range of ordinate values is invalid.
     *
     * @see GeneralEnvelope#GeneralEnvelope(Rectangle2D)
     */
    public Envelope2D(final CoordinateReferenceSystem crs, final Rectangle2D rect) {
        this(rect.getMinX(), rect.getMinY(), rect.getMaxX(), rect.getMaxY());
        setCoordinateReferenceSystem(crs);
        ensureValidRanges();
    }

    /**
     * Constructs two-dimensional envelope defined by the specified coordinates. Despite
     * their name, the (<var>x</var>,<var>y</var>) coordinates don't need to be oriented
     * toward ({@linkplain AxisDirection#EAST East}, {@linkplain AxisDirection#NORTH North}).
     * Those parameter names simply match the {@linkplain #x x} and {@linkplain #y y} fields.
     * The actual axis orientations are determined by the specified CRS.
     * See the <a href="#skip-navbar_top">class javadoc</a> for details.
     *
     * @param crs The coordinate reference system, or {@code null}.
     * @param x The <var>x</var> minimal value.
     * @param y The <var>y</var> minimal value.
     * @param width The envelope width.
     * @param height The envelope height.
     * @throws IllegalArgumentException If the given range of ordinate values is invalid.
     */
    public Envelope2D(final CoordinateReferenceSystem crs,
                      final double x, final double y, final double width, final double height)
    {
        super(x, y, width, height); // Really 'super', not 'this'.
        setCoordinateReferenceSystem(crs);
        ensureValidRanges();
    }

    /**
     * Constructs two-dimensional envelope defined by the specified coordinates. Despite
     * their name, the (<var>x</var>,<var>y</var>) coordinates don't need to be oriented
     * toward ({@linkplain AxisDirection#EAST East}, {@linkplain AxisDirection#NORTH North}).
     * Those parameter names simply match the {@linkplain #x x} and {@linkplain #y y} fields.
     * The actual axis orientations are determined by the specified CRS.
     * See the <a href="#skip-navbar_top">class javadoc</a> for details.
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * This {@code minDP} and {@code maxDP} arguments may not be really minimal or maximal values
     * if the rectangle cross the anti-meridian. The given arguments are rather the values to be
     * returned by {@link #getLowerCorner()} and {@link #getUpperCorner()} methods, which may
     * have an extended interpretation. See the javadoc of above-cited methods for more details.
     *
     * @param minDP The fist position.
     * @param maxDP The second position.
     * @throws MismatchedReferenceSystemException if the two positions don't use the same CRS.
     *
     * @see GeneralEnvelope#GeneralEnvelope(GeneralDirectPosition, GeneralDirectPosition)
     *
     * @since 2.4
     */
    public Envelope2D(final DirectPosition2D minDP, final DirectPosition2D maxDP)
            throws MismatchedReferenceSystemException
    {
        this(minDP.x, minDP.y, maxDP.x, maxDP.y);
        setCoordinateReferenceSystem(AbstractEnvelope.getCoordinateReferenceSystem(minDP, maxDP));
        ensureValidRanges();
    }

    /**
     * Ensures that the ranges are valid.
     *
     * @throws IllegalArgumentException If a range of ordinate values is invalid.
     */
    private void ensureValidRanges() throws IllegalArgumentException {
        ensureValidRange(crs, 0, x, x+width);
        ensureValidRange(crs, 1, y, y+height);
    }

    /**
     * Sets this envelope to the same values than the given {@link Envelope}.
     *
     * @param envelope The envelope to copy.
     * @throws MismatchedDimensionException If the given envelope is not two-dimensional.
     *
     * @since 3.09
     */
    public void setEnvelope(final Envelope envelope) throws MismatchedDimensionException {
        if (envelope != this) {
            final int dimension = envelope.getDimension();
            if (dimension != 2) {
                throw new MismatchedDimensionException(Errors.format(
                        Errors.Keys.NOT_TWO_DIMENSIONAL_$1, dimension));
            }
            setCoordinateReferenceSystem(envelope.getCoordinateReferenceSystem());
            setFrame(envelope.getMinimum(0), envelope.getMinimum(1), envelope.getSpan(0), envelope.getSpan(1));
        }
    }

    /**
     * Returns the coordinate reference system in which the coordinates are given.
     *
     * @return The coordinate reference system, or {@code null}.
     */
    @Override
    public final CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }

    /**
     * Sets the coordinate reference system in which the coordinate are given.
     * This method <strong>does not</strong> reproject the envelope.
     * If the envelope coordinates need to be transformed to the new CRS, consider using
     * {@link org.geotoolkit.referencing.CRS#transform(Envelope, CoordinateReferenceSystem)}
     * instead.
     *
     * @param crs The new coordinate reference system, or {@code null}.
     */
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs) {
        AbstractDirectPosition.checkCoordinateReferenceSystemDimension(crs, getDimension());
        this.crs = crs;
    }

    /**
     * Returns the number of dimensions.
     */
    @Override
    public final int getDimension() {
        return 2;
    }

    /**
     * A coordinate position consisting of all the minimal ordinates for each
     * dimension for all points within the {@code Envelope}.
     *
     * {@note The <cite>Web Coverage Service</cite> (WCS) 1.1 specification uses an extended
     * interpretation of the bounding box definition. In a WCS 1.1 data structure, the lower
     * corner defines the edges region in the directions of <em>decreasing</em> coordinate
     * values in the envelope CRS. This is usually the algebraic minimum coordinates, but not
     * always. For example, an envelope crossing the anti-meridian could have a lower corner
     * longitude greater than the upper corner longitude. This <code>Envelope2D</code> class
     * accepts such extended interpretation for <code>WRAPAROUND</code> axes.}
     *
     * @return The lower corner.
     */
    @Override
    public DirectPosition2D getLowerCorner() {
        return new DirectPosition2D(crs, getMinX(), getMinY());
    }

    /**
     * A coordinate position consisting of all the maximal ordinates for each
     * dimension for all points within the {@code Envelope}.
     *
     * {@note The <cite>Web Coverage Service</cite> (WCS) 1.1 specification uses an extended
     * interpretation of the bounding box definition. In a WCS 1.1 data structure, the upper
     * corner defines the edges region in the directions of <em>increasing</em> coordinate
     * values in the envelope CRS. This is usually the algebraic maximum coordinates, but not
     * always. For example, an envelope crossing the anti-meridian could have an upper corner
     * longitude less than the lower corner longitude. This <code>Envelope2D</code> class
     * accepts such extended interpretation for <code>WRAPAROUND</code> axes.}
     *
     * @return The upper corner.
     */
    @Override
    public DirectPosition2D getUpperCorner() {
        return new DirectPosition2D(crs, getMaxX(), getMaxY());
    }

    /**
     * Creates an exception for an index out of bounds.
     */
    private static IndexOutOfBoundsException indexOutOfBounds(final int dimension) {
        return new IndexOutOfBoundsException(Errors.format(Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, dimension));
    }

    /**
     * Returns the minimal ordinate along the specified dimension.
     *
     * @param dimension The dimension to query.
     * @return The minimal ordinate value along the given dimension.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    @Override
    public final double getMinimum(final int dimension) throws IndexOutOfBoundsException {
        switch (dimension) {
            case 0:  return getMinX();
            case 1:  return getMinY();
            default: throw indexOutOfBounds(dimension);
        }
    }

    /**
     * Returns the maximal ordinate along the specified dimension.
     *
     * @param dimension The dimension to query.
     * @return The maximal ordinate value along the given dimension.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    @Override
    public final double getMaximum(final int dimension) throws IndexOutOfBoundsException {
        switch (dimension) {
            case 0:  return getMaxX();
            case 1:  return getMaxY();
            default: throw indexOutOfBounds(dimension);
        }
    }

    /**
     * Returns the median ordinate along the specified dimension. The default implementation
     * delegates to {@link #getCenterX()} or {@link #getCenterY()}, depending the requested
     * dimension.
     *
     * @param dimension The dimension to query.
     * @return The mid ordinate value along the given dimension.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    @Override
    public final double getMedian(final int dimension) throws IndexOutOfBoundsException {
        switch (dimension) {
            case 0:  return getCenterX();
            case 1:  return getCenterY();
            default: throw indexOutOfBounds(dimension);
        }
    }

    /**
     * Returns the envelope span along the specified dimension. The default implementation
     * delegates to {@link #getWidth()} or {@link #getHeight()}, depending the requested
     * dimension.
     *
     * @param  dimension The dimension to query.
     * @return The rectangle width or height, depending the given dimension.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
      */
    @Override
    public final double getSpan(final int dimension) throws IndexOutOfBoundsException {
        switch (dimension) {
            case 0:  return getWidth ();
            case 1:  return getHeight();
            default: throw indexOutOfBounds(dimension);
        }
    }

    /**
     * Returns the median ordinate along the <var>x</var> dimension. This method handles crossing
     * of anti-meridian as documented in the {@link AbstractEnvelope#getMedian(int)} method.
     *
     * @since 3.20
     */
    @Override
    public double getCenterX() {
        double median = x + 0.5*width;
        if (isNegative(width)) { // Special handling for -0.0
            median = fixMedian(getAxis(crs, 0), median);
        }
        return median;
    }

    /**
     * Returns the median ordinate along the <var>y</var> dimension. This method handles crossing
     * of anti-meridian as documented in the {@link AbstractEnvelope#getMedian(int)} method
     * (note that "<var>y</var>" can be longitude, as it depends on axis order).
     *
     * @since 3.20
     */
    @Override
    public double getCenterY() {
        double median = y + 0.5*height;
        if (isNegative(height)) { // Special handling for -0.0
            median = fixMedian(getAxis(crs, 1), median);
        }
        return median;
    }

    /**
     * Returns the span along the <var>x</var> dimension. This method handles crossing of
     * anti-meridian as documented in the {@link AbstractEnvelope#getSpan(int)} method.
     *
     * @since 3.20
     */
    @Override
    public double getWidth() {
        double span = width;
        if (isNegative(span)) { // Special handling for -0.0
            span = fixSpan(getAxis(crs, 0), span);
        }
        return span;
    }

    /**
     * Returns the span along the <var>y</var> dimension. This method handles crossing of
     * anti-meridian as documented in the {@link AbstractEnvelope#getSpan(int)} method
     * (note that "<var>y</var>" can be longitude, as it depends on axis order).
     *
     * @since 3.20
     */
    @Override
    public double getHeight() {
        double span = height;
        if (isNegative(span)) { // Special handling for -0.0
            span = fixSpan(getAxis(crs, 1), span);
        }
        return span;
    }

    /**
     * Returns the "maximal" ordinate value on the <var>x</var> axis.
     * This value may not be really maximal if the rectangle cross the anti-meridian.
     * This is rather the <var>x</var> ordinate value of the in the
     * {@linkplain AbstractEnvelope#getUpperCorner() upper corner as documented in the
     * <code>AbstractEnvelope</code> interface}.
     */
    @Override
    public double getMaxX() {
        return x + width;
    }

    /**
     * Returns the "maximal" ordinate value on the <var>y</var> axis.
     * This value may not be really maximal if the rectangle cross the anti-meridian.
     * This is rather the <var>y</var> ordinate value of the in the
     * {@linkplain AbstractEnvelope#getUpperCorner() upper corner as documented in the
     * <code>AbstractEnvelope</code> interface}.
     */
    @Override
    public double getMaxY() {
        return y + height;
    }

    /**
     * Determines whether the envelope is empty. A negative {@linkplain #width} or
     * (@linkplain #height} is considered as a non-empty area if the corresponding
     * axis has the {@linkplain org.opengis.referencing.cs.RangeMeaning#WRAPAROUND
     * wraparound} range meaning.
     * <p>
     * Note that if the {@linkplain #width} or {@linkplain #height} value is
     * {@link java.lang.Double#NaN NaN}, then the envelope is considered empty.
     * This is different than the default {@link Rectangle2D.Double#isEmpty()}
     * implementation, which doesn't check for {@code NaN} values.
     *
     * @since 3.20
     */
    @Override
    public boolean isEmpty() {
        return !((width  > 0 || (isNegative(width)  && isWrapAround(crs, 0)))
              && (height > 0 || (isNegative(height) && isWrapAround(crs, 1))));
    }

    /**
     * Tests if a specified coordinate is inside the boundary of this envelope. If it least one
     * of the given ordinate value is {@link java.lang.Double#NaN NaN}, then this method returns
     * {@code false}.
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * This method supports anti-meridian spanning in the same way than
     * {@link AbstractEnvelope#contains(DirectPosition)}.
     *
     * @param  px The first ordinate value of the point to text.
     * @param  py The second ordinate value of the point to text.
     * @return {@code true} if the specified coordinate is inside the boundary
     *         of this envelope; {@code false} otherwise.
     *
     * @since 3.20
     */
    @Override
    public boolean contains(final double px, final double py) {
        boolean c1 = (px >= x);
        boolean c2 = (px <= x + width);
        // See AbstractEnvelope.contains(DirectPosition) for explanation.
        if ((c1 & c2) || ((c1 | c2) && isNegative(width) && isWrapAround(crs, 0))) {
            // Same check, but for y axis.
            c1 = (py >= y);
            c2 = (py <= y + height);
            return (c1 & c2) || ((c1 | c2) && isNegative(height) && isWrapAround(crs, 1));
        }
        return false;
    }

    /**
     * Returns {@code true} if this envelope completely encloses the specified rectangle. If this
     * envelope or the given rectangle have at least one {@link java.lang.Double#NaN NaN} value,
     * then this method returns {@code false}.
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * This method supports anti-meridian spanning in the same way than
     * {@link AbstractEnvelope#contains(Envelope, boolean)}.
     *
     * @param  rect The rectangle to test for inclusion.
     * @return {@code true} if this envelope completely encloses the specified rectangle.
     *
     * @since 3.20
     */
    @Override
    public boolean contains(final Rectangle2D rect) {
        if (rect instanceof Envelope2D) {
            // Need to bypass the overriden getWidth()/getHeight().
            final Envelope2D env = (Envelope2D) rect;
            return contains(env.x, env.y, env.width, env.height);
        }
        return super.contains(rect);
    }

    /**
     * Returns {@code true} if this envelope completely encloses the specified rectangle. If this
     * envelope or the given rectangle have at least one {@link java.lang.Double#NaN NaN} value,
     * then this method returns {@code false}.
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * This method supports anti-meridian spanning in the same way than
     * {@link AbstractEnvelope#contains(Envelope, boolean)}.
     *
     * @param  rx The <var>x</var> ordinate of the lower corner of the rectangle to test for inclusion.
     * @param  ry The <var>y</var> ordinate of the lower corner of the rectangle to test for inclusion.
     * @param  rw The width of the rectangle to test for inclusion. May be negative if the rectangle spans the anti-meridian.
     * @param  rh The height of the rectangle to test for inclusion. May be negative.
     * @return {@code true} if this envelope completely encloses the specified one.
     *
     * @since 3.20
     */
    @Override
    public boolean contains(final double rx, final double ry, final double rw, final double rh) {
        for (int i=0; i!=2; i++) {
            final double min0, min1, span0, span1;
            if (i == 0) {
                min0 =  x;  span0 = width;
                min1 = rx;  span1 = rw;
            } else {
                min0 =  y;  span0 = height;
                min1 = ry;  span1 = rh;
            }
            // See AbstractEnvelope.contains(Envelope) for an
            // illustration of the algorithm applied here.
            final boolean minCondition = (min1 >= min0);
            final boolean maxCondition = (min1 + span1 <= min0 + span0);
            if (minCondition & maxCondition) {
                if (!isNegativeUnsafe(span1) || isNegativeUnsafe(span0)) {
                    continue;
                }
            } else if (minCondition != maxCondition && isNegative(span0)
                    && isPositive(span1) && isWrapAround(crs, i))
            {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * Returns {@code true} if this envelope intersects the specified envelope. If this envelope
     * or the given rectangle have at least one {@link java.lang.Double#NaN NaN} value, then this
     * method returns {@code false}.
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * This method supports anti-meridian spanning in the same way than
     * {@link AbstractEnvelope#intersects(Envelope, boolean)}.
     *
     * @param  rect The rectangle to test for intersection.
     * @return {@code true} if this envelope intersects the specified rectangle.
     *
     * @since 3.20
     */
    @Override
    public boolean intersects(final Rectangle2D rect) {
        if (rect instanceof Envelope2D) {
            // Need to bypass the overriden getWidth()/getHeight().
            final Envelope2D env = (Envelope2D) rect;
            return intersects(env.x, env.y, env.width, env.height);
        }
        return super.contains(rect);
    }

    /**
     * Returns {@code true} if this envelope intersects the specified envelope. If this envelope
     * or the given rectangle have at least one {@link java.lang.Double#NaN NaN} value, then this
     * method returns {@code false}.
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * This method supports anti-meridian spanning in the same way than
     * {@link AbstractEnvelope#intersects(Envelope, boolean)}.
     *
     * @param  rx The <var>x</var> ordinate of the lower corner of the rectangle to test for intersection.
     * @param  ry The <var>y</var> ordinate of the lower corner of the rectangle to test for intersection.
     * @param  rw The width of the rectangle to test for inclusion. May be negative if the rectangle spans the anti-meridian.
     * @param  rh The height of the rectangle to test for inclusion. May be negative.
     * @return {@code true} if this envelope intersects the specified rectangle.
     *
     * @since 3.20
     */
    @Override
    public boolean intersects(final double rx, final double ry, final double rw, final double rh) {
        for (int i=0; i!=2; i++) {
            final double min0, min1, span0, span1;
            if (i == 0) {
                min0 =  x;  span0 = width;
                min1 = rx;  span1 = rw;
            } else {
                min0 =  y;  span0 = height;
                min1 = ry;  span1 = rh;
            }
            // See AbstractEnvelope.intersects(Envelope) for an
            // illustration of the algorithm applied here.
            final boolean minCondition = (min1 <= min0 + span0);
            final boolean maxCondition = (min1 + span1 >= min0);
            if (maxCondition & minCondition) {
                continue;
            }
            final boolean sp0 = isNegative(span0);
            final boolean sp1 = isNegative(span1);
            if (sp0 | sp1) {
                if ((sp0 & sp1) | (maxCondition | minCondition)) {
                    if (isWrapAround(crs, i)) {
                        continue;
                    }
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Returns the intersection of this envelope with the specified rectangle. If this envelope
     * or the given rectangle have at least one {@link java.lang.Double#NaN NaN} values, then this
     * method returns an {@linkplain #isEmpty() empty} envelope.
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * This method supports anti-meridian spanning in the same way than
     * {@link AbstractEnvelope#intersects(Envelope, boolean)}.     *
     * @param rect The rectangle to be intersected with this envelope.
     * @return The intersection of the given rectangle with this envelope.
     */
    @Override
    public Envelope2D createIntersection(final Rectangle2D rect) {
        final Envelope2D env = (rect instanceof Envelope2D) ? (Envelope2D) rect : null;
        final Envelope2D inter = new Envelope2D(crs, NaN, NaN, NaN, NaN);
        for (int i=0; i!=2; i++) {
            final double min0, min1, span0, span1;
            if (i == 0) {
                min0  = x;
                span0 = width;
                min1  = rect.getX();
                span1 = (env != null) ? env.width : rect.getWidth();
            } else {
                min0  = y;
                span0 = height;
                min1  = rect.getY();
                span1 = (env != null) ? env.height : rect.getHeight();
            }
            final double max0 = min0 + span0;
            final double max1 = min1 + span1;
            double min = Math.max(min0, min1);
            double max = Math.min(max0, max1);
            /*
             * See GeneralEnvelope.intersect(Envelope) for an explanation of the algorithm applied
             * below.
             */
            if (isSameSign(span0, span1)) { // Always 'false' if any value is NaN.
                if ((min1 > max0 || max1 < min0) && !isNegativeUnsafe(span0)) {
                    continue; // No intersection: leave ordinate values to NaN
                }
            } else if (isWrapAround(crs, i)) {
                int intersect = 0; // A bitmask of intersections (two bits).
                if (isNegativeUnsafe(span0)) {
                    if (min1 <= max0) {min = min1; intersect  = 1;}
                    if (max1 >= min0) {max = max1; intersect |= 2;}
                } else {
                    if (min0 <= max1) {min = min0; intersect  = 1;}
                    if (max0 >= min1) {max = max0; intersect |= 2;}
                }
                if (intersect == 0 || intersect == 3) {
                    continue; // Leave ordinate values to NaN
                }
            }
            inter.setRange(i, min, max);
        }
        assert inter.isEmpty() || (contains(inter) && rect.contains(inter)) : inter;
        return inter;
    }

    /**
     * Sets the envelope range along the specified dimension.
     *
     * @param  dimension The dimension to set.
     * @param  minimum   The minimum value along the specified dimension.
     * @param  maximum   The maximum value along the specified dimension.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    private void setRange(final int dimension, final double minimum, final double maximum)
            throws IndexOutOfBoundsException
    {
        final double span = maximum - minimum;
        switch (dimension) {
            case 0: x = minimum; width  = span; break;
            case 1: y = minimum; height = span; break;
            default: throw indexOutOfBounds(dimension);
        }
    }

    /**
     * Compares the specified object with this envelope for equality. If the given object is not
     * an instance of {@code Envelope2D}, then the two objects are compared as plain rectangles,
     * i.e. the {@linkplain #getCoordinateReferenceSystem() coordinate reference system} of this
     * envelope is ignored.
     *
     * {@section Note on <code>hashCode()</code>}
     * This class does not override the {@link #hashCode()} method for consistency with the
     * {@link Rectangle2D#equals(Object)} method, which compare arbitrary {@code Rectangle2D}
     * implementations.
     *
     * @param object The object to compare with this envelope.
     * @return {@code true} if the given object is equal to this envelope.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof Envelope2D) {
            final Envelope2D other = (Envelope2D) object;
            return Utilities.equals(x,      other.x)      &&
                   Utilities.equals(y,      other.y)      &&
                   Utilities.equals(width,  other.width)  &&
                   Utilities.equals(height, other.height) &&
                   Objects  .equals(crs,    other.crs);
        } else {
            return super.equals(object);
        }
    }

    /**
     * Returns {@code true} if {@code this} envelope bounds is equal to {@code that} envelope
     * bounds in two specified dimensions. The coordinate reference system is not compared, since
     * it doesn't need to have the same number of dimensions.
     *
     * @param that The envelope to compare to.
     * @param xDim The dimension of {@code that} envelope to compare to the <var>x</var> dimension
     *             of {@code this} envelope.
     * @param yDim The dimension of {@code that} envelope to compare to the <var>y</var> dimension
     *             of {@code this} envelope.
     * @param eps  A small tolerance number for floating point number comparisons. This value will
     *             be scaled according this envelope {@linkplain #width width} and
     *             {@linkplain #height height}.
     * @return {@code true} if the envelope bounds are the same (up to the specified tolerance
     *         level) in the specified dimensions, or {@code false} otherwise.
     */
    public boolean boundsEquals(final Envelope that, final int xDim, final int yDim, double eps) {
        eps *= 0.5*(width + height);
        for (int i=0; i<4; i++) {
            final int dim2D = (i & 1);
            final int dimND = (dim2D == 0) ? xDim : yDim;
            final double value2D, valueND;
            if ((i & 2) == 0) {
                value2D = this.getMinimum(dim2D);
                valueND = that.getMinimum(dimND);
            } else {
                value2D = this.getMaximum(dim2D);
                valueND = that.getMaximum(dimND);
            }
            // Use '!' for catching NaN values.
            if (!(Math.abs(value2D - valueND) <= eps)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Formats this envelope in the <cite>Well Known Text</cite> (WKT) format.
     * The output is like below:
     *
     * <blockquote>{@code BOX2D(}{@linkplain #getLowerCorner() lower corner}{@code ,}
     * {@linkplain #getUpperCorner() upper corner}{@code )}</blockquote>
     *
     * @see Envelopes#toWKT(Envelope)
     *
     * @since 2.4
     */
    @Override
    public String toString() {
        return AbstractEnvelope.toString(this);
    }
}
