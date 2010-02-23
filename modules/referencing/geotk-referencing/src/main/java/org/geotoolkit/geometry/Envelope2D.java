/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;

import org.geotoolkit.util.Cloneable;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.resources.Errors;


/**
 * A two-dimensional envelope on top of {@link Rectangle2D}. This implementation is provided for
 * interoperability between Java2D and GeoAPI.
 * <p>
 * This class inherits {@linkplain #x x} and {@linkplain #y y} fields. But despite their names,
 * they don't need to be oriented toward {@linkplain AxisDirection#EAST East} and
 * {@linkplain AxisDirection#NORTH North} respectively. The (<var>x</var>,<var>y</var>) axis can
 * have any orientation and should be understood as "ordinate 0" and "ordinate 1" values instead.
 * This is not specific to this implementation; in Java2D too, the visual axis orientation depend
 * on the {@linkplain java.awt.Graphics2D#getTransform affine transform in the graphics context}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
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
     * Serial number for interoperability with different versions.
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
     * Constructs two-dimensional envelope defined by an other {@link Envelope}.
     *
     * @param envelope The envelope to copy.
     */
    public Envelope2D(final Envelope envelope) {
        super(envelope.getMinimum(0), envelope.getMinimum(1),
              envelope.getSpan(0), envelope.getSpan(1));

        // TODO: check below should be first, if only Sun could fix RFE #4093999.
        final int dimension = envelope.getDimension();
        if (dimension != 2) {
            throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.NOT_TWO_DIMENSIONAL_$1, dimension));
        }
        setCoordinateReferenceSystem(envelope.getCoordinateReferenceSystem());
    }

    /**
     * Constructs two-dimensional envelope defined by an other {@link Rectangle2D}.
     *
     * @param crs The coordinate reference system, or {@code null}.
     * @param rect The rectangle to copy.
     */
    public Envelope2D(final CoordinateReferenceSystem crs, final Rectangle2D rect) {
        super(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        setCoordinateReferenceSystem(crs);
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
     */
    public Envelope2D(final CoordinateReferenceSystem crs,
                      final double x, final double y, final double width, final double height)
    {
        super(x, y, width, height);
        setCoordinateReferenceSystem(crs);
    }

    /**
     * Constructs two-dimensional envelope defined by the specified coordinates. Despite
     * their name, the (<var>x</var>,<var>y</var>) coordinates don't need to be oriented
     * toward ({@linkplain AxisDirection#EAST East}, {@linkplain AxisDirection#NORTH North}).
     * Those parameter names simply match the {@linkplain #x x} and {@linkplain #y y} fields.
     * The actual axis orientations are determined by the specified CRS.
     * See the <a href="#skip-navbar_top">class javadoc</a> for details.
     * <p>
     * The {@code minDP} and {@code maxDP} arguments usually contains the minimal and maximal
     * ordinate values respectively, but this is not mandatory. The ordinates will be rearanged
     * as needed.
     *
     * @param minDP The fist position.
     * @param maxDP The second position.
     * @throws MismatchedReferenceSystemException if the two positions don't use the same CRS.
     *
     * @since 2.4
     */
    public Envelope2D(final DirectPosition2D minDP, final DirectPosition2D maxDP)
            throws MismatchedReferenceSystemException
    {
//  Uncomment next lines if Sun fixes RFE #4093999
//      ensureNonNull("minDP", minDP);
//      ensureNonNull("maxDP", maxDP);
        super(Math.min(minDP.x,  maxDP.x),
              Math.min(minDP.y,  maxDP.y),
              Math.abs(maxDP.x - minDP.x),
              Math.abs(maxDP.y - minDP.y));
        setCoordinateReferenceSystem(AbstractEnvelope.getCoordinateReferenceSystem(minDP, maxDP));
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
     * Returns the median ordinate along the specified dimension. The result should be equals
     * (minus rounding error) to <code>({@linkplain #getMaximum getMaximum}(dimension) -
     * {@linkplain #getMinimum getMinimum}(dimension)) / 2</code>.
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
     * Returns the envelope span (typically width or height) along the specified dimension.
     * The result should be equals (minus rounding error) to <code>{@linkplain #getMaximum
     * getMaximum}(dimension) - {@linkplain #getMinimum getMinimum}(dimension)</code>.
     *
     * @param dimension The dimension to query.
     * @return The difference along maximal and minimal ordinates in the given dimension.
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
     * Returns a hash value for this envelope. Current implementation returns the same
     * value than {@link Rectangle2D} for consistency with its {@link Rectangle2D#equals
     * equals} method, which compare arbitrary {@code Rectangle2D} implementations.
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Compares the specified object with this envelope for equality.
     *
     * @param object The object to compare with this envelope.
     * @return {@code true} if the given object is equal to this envelope.
     */
    @Override
    public boolean equals(final Object object) {
        if (super.equals(object)) {
            final CoordinateReferenceSystem otherCRS =
                    (object instanceof Envelope2D) ? ((Envelope2D) object).crs : null;
            return Utilities.equals(crs, otherCRS);
        }
        return false;
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
     * @see AbstractEnvelope#toString(Envelope)
     *
     * @since 2.4
     */
    @Override
    public String toString() {
        return AbstractEnvelope.toString(this);
    }
}
