/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry;

import java.awt.geom.Rectangle2D;

import org.opengis.geometry.Envelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import static java.lang.Double.NaN;
import static java.lang.Double.isNaN;
import static org.apache.sis.math.MathFunctions.isSameSign;
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
 * {@section Spanning the anti-meridian of a Geographic CRS}
 * The <cite>Web Coverage Service</cite> (WCS) specification authorizes (with special treatment)
 * cases where <var>upper</var> &lt; <var>lower</var> at least in the longitude case. They are
 * envelopes spanning the anti-meridian, like the red box below (the green box is the usual case).
 * For {@code Envelope2D} objects, they are rectangle with negative {@linkplain #width width} or
 * {@linkplain #height height} field values. The default implementation of methods listed in the
 * right column can handle such cases.
 *
 * <center><table><tr><td style="white-space:nowrap">
 *   <img src="doc-files/AntiMeridian.png">
 * </td><td style="white-space:nowrap">
 * Supported methods:
 * <ul>
 *   <li>{@link #getMinimum(int)}</li>
 *   <li>{@link #getMaximum(int)}</li>
 *   <li>{@link #getSpan(int)}</li>
 *   <li>{@link #getMedian(int)}</li>
 *   <li>{@link #isEmpty()}</li>
 *   <li>{@link #contains(double,double)}</li>
 *   <li>{@link #contains(Rectangle2D)} and its variant receiving {@code double} arguments</li>
 *   <li>{@link #intersects(Rectangle2D)} and its variant receiving {@code double} arguments</li>
 *   <li>{@link #createIntersection(Rectangle2D)}</li>
 *   <li>{@link #createUnion(Rectangle2D)}</li>
 *   <li>{@link #add(Rectangle2D)}</li>
 *   <li>{@link #add(double,double)}</li>
 * </ul>
 * </td></tr></table></center>
 *
 * The {@link #getMinX()}, {@link #getMinY()}, {@link #getMaxX()}, {@link #getMaxY()},
 * {@link #getCenterX()}, {@link #getCenterY()}, {@link #getWidth()} and {@link #getHeight()}
 * methods delegate to the above-cited methods.
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
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.geometry.Envelope2D}.
 */
@Deprecated
public class Envelope2D extends org.apache.sis.geometry.Envelope2D implements Cloneable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -3319231220761419351L;

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
     */
    private Envelope2D(final double xmin, final double ymin, final double xmax, final double ymax) {
        super.setRect(xmin, ymin, xmax - xmin, ymax - ymin);
    }

    /**
     * Constructs two-dimensional envelope defined by an other {@link Envelope}.
     *
     * @param envelope The envelope to copy.
     * @throws MismatchedDimensionException If the given envelope is not two-dimensional.
     *
     * @see GeneralEnvelope#GeneralEnvelope(Envelope)
     */
    public Envelope2D(final Envelope envelope) throws MismatchedDimensionException {
        super(envelope);
    }

    /**
     * Constructs a new envelope with the same data than the specified
     * geographic bounding box. The coordinate reference system is set
     * to {@linkplain DefaultGeographicCRS#WGS84 WGS84}.
     *
     * @param box The bounding box to copy.
     *
     * @see GeneralEnvelope#GeneralEnvelope(GeographicBoundingBox)
     *
     * @since 3.11
     */
    public Envelope2D(final GeographicBoundingBox box) {
        super(box);
        super.setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84);
    }

    /**
     * Constructs two-dimensional envelope defined by an other {@link Rectangle2D}.
     * If the given rectangle has negative width or height, they will be interpreted
     * as envelope spanning the anti-meridian.
     *
     * @param crs The coordinate reference system, or {@code null}.
     * @param rect The rectangle to copy.
     *
     * @see GeneralEnvelope#GeneralEnvelope(Rectangle2D)
     */
    public Envelope2D(final CoordinateReferenceSystem crs, final Rectangle2D rect) {
        super(crs, rect);
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
     * @param width The envelope width. May be negative for envelope spanning the anti-meridian.
     * @param height The envelope height. May be negative for envelope spanning the anti-meridian.
     */
    public Envelope2D(final CoordinateReferenceSystem crs,
                      final double x, final double y, final double width, final double height)
    {
        super(crs, x, y, width, height);
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
     * @param lower The fist position.
     * @param upper The second position.
     * @throws MismatchedReferenceSystemException if the two positions don't use the same CRS.
     *
     * @see GeneralEnvelope#GeneralEnvelope(GeneralDirectPosition, GeneralDirectPosition)
     *
     * @since 2.4
     */
    public Envelope2D(final DirectPosition lower, final DirectPosition upper)
            throws MismatchedReferenceSystemException
    {
        super(lower, upper);
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
            final DirectPosition lower = envelope.getLowerCorner();
            final DirectPosition upper = envelope.getUpperCorner();
            x      = lower.getOrdinate(0);
            y      = lower.getOrdinate(1);
            width  = upper.getOrdinate(0) - x;
            height = upper.getOrdinate(1) - y;
            setCoordinateReferenceSystem(envelope.getCoordinateReferenceSystem());
        }
    }

    /**
     * A coordinate position consisting of all the starting ordinates for each
     * dimension for all points within the {@code Envelope}.
     *
     * {@note The <cite>Web Coverage Service</cite> (WCS) 1.1 specification uses an extended
     * interpretation of the bounding box definition. In a WCS 1.1 data structure, the lower
     * corner defines the edges region in the directions of <em>decreasing</em> coordinate
     * values in the envelope CRS. This is usually the algebraic minimum coordinates, but not
     * always. For example, an envelope spanning the anti-meridian could have a lower corner
     * longitude greater than the upper corner longitude. Such extended interpretation applies
     * mostly to axes having <code>WRAPAROUND</code> range meaning.}
     *
     * @return The lower corner, typically (but not necessarily) containing minimal ordinate values.
     */
    @Override
    public DirectPosition2D getLowerCorner() {
        return new DirectPosition2D(super.getCoordinateReferenceSystem(), x, y);
    }

    /**
     * A coordinate position consisting of all the ending ordinates for each
     * dimension for all points within the {@code Envelope}.
     *
     * {@note The <cite>Web Coverage Service</cite> (WCS) 1.1 specification uses an extended
     * interpretation of the bounding box definition. In a WCS 1.1 data structure, the upper
     * corner defines the edges region in the directions of <em>increasing</em> coordinate
     * values in the envelope CRS. This is usually the algebraic maximum coordinates, but not
     * always. For example, an envelope spanning the anti-meridian could have an upper corner
     * longitude less than the lower corner longitude. Such extended interpretation applies
     * mostly to axes having <code>WRAPAROUND</code> range meaning.}
     *
     * @return The upper corner, typically (but not necessarily) containing maximal ordinate values.
     */
    @Override
    public DirectPosition2D getUpperCorner() {
        return new DirectPosition2D(super.getCoordinateReferenceSystem(), x+width, y+height);
    }

    /**
     * Creates an exception for an index out of bounds.
     */
    private static IndexOutOfBoundsException indexOutOfBounds(final int dimension) {
        return new IndexOutOfBoundsException(Errors.format(Errors.Keys.INDEX_OUT_OF_BOUNDS_$1, dimension));
    }

    /**
     * Returns the intersection of this envelope with the specified rectangle. If this envelope
     * or the given rectangle have at least one {@link java.lang.Double#NaN NaN} values, then this
     * method returns an {@linkplain #isEmpty() empty} envelope.
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * This method supports anti-meridian spanning in the same way than
     * {@link GeneralEnvelope#intersect(Envelope)}.
     *
     * @param rect The rectangle to be intersected with this envelope.
     * @return The intersection of the given rectangle with this envelope.
     */
    @Override
    public Envelope2D createIntersection(final Rectangle2D rect) {
        final Envelope2D env = (rect instanceof Envelope2D) ? (Envelope2D) rect : null;
        final Envelope2D inter = new Envelope2D(super.getCoordinateReferenceSystem(), NaN, NaN, NaN, NaN);
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
            } else if (isNaN(span0) || isNaN(span1)) {
                continue; // Leave ordinate values to NaN
            } else {
                int intersect = 0; // A bitmask of intersections (two bits).
                if (isNegativeUnsafe(span0)) {
                    if (min1 <= max0) {min = min1; intersect  = 1;}
                    if (max1 >= min0) {max = max1; intersect |= 2;}
                } else {
                    if (min0 <= max1) {min = min0; intersect  = 1;}
                    if (max0 >= min1) {max = max0; intersect |= 2;}
                }
                if (intersect == 0 || intersect == 3) {
                    final double csSpan = AbstractEnvelope.getSpan(getAxis(super.getCoordinateReferenceSystem(), i));
                    if (span1 >= csSpan) {
                        min = min0;
                        max = max0;
                    } else if (span0 >= csSpan) {
                        min = min1;
                        max = max1;
                    } else {
                        continue; // Leave ordinate values to NaN
                    }
                }
            }
            inter.setRange(i, min, max);
        }
        assert inter.isEmpty() || (contains(inter) && rect.contains(inter)) : inter;
        return inter;
    }

    /**
     * Returns the union of this envelope with the specified rectangle.
     * The default implementation clones this envelope, then delegates
     * to {@link #add(Rectangle2D)}.
     *
     * @param rect The rectangle to add to this envelope.
     * @return The union of the given rectangle with this envelope.
     */
    @Override
    public Envelope2D createUnion(final Rectangle2D rect) {
        final Envelope2D union = (Envelope2D) clone();
        union.add(rect);
        assert union.isEmpty() || (union.contains(this) && union.contains(rect)) : union;
        return union;
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
}
