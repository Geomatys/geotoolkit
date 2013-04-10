/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.grid;

import java.util.Arrays;
import java.io.Serializable;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import net.jcip.annotations.Immutable;

import org.opengis.geometry.Envelope;
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.coverage.grid.GridCoordinates;
import org.opengis.referencing.datum.PixelInCell;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.Classes;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;


/**
 * Defines a range of grid coverage coordinates.
 * <p>
 * <b>CAUTION:</b>
 * ISO 19123 defines {@linkplain #getHigh high} coordinates as <strong>inclusive</strong>.
 * We follow this specification for all getters methods, but keep in mind that this is the
 * opposite of Java2D usage where {@link Rectangle} maximal values are exclusive. When the
 * context is ambiguous, an explicit {@code isHighIncluded} argument is required.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see GridEnvelope2D
 *
 * @since 1.2
 * @module
 */
@Immutable
public class GeneralGridEnvelope implements GridEnvelope, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -1695224412095031712L;

    /**
     * The lower left corner, inclusive.
     * Will be created only when first needed.
     */
    private transient GridCoordinates low;

    /**
     * The upper right corner, <strong>inclusive</strong>.
     * Will be created only when first needed.
     */
    private transient GridCoordinates high;

    /**
     * Minimum and maximum grid ordinates. The first half contains minimum ordinates (inclusive),
     * while the last half contains maximum ordinates (<strong>exclusive</strong>). Note that the
     * later is the opposite of ISO specification. We store upper coordinates as exclusive values
     * for implementation convenience.
     */
    private final int[] index;

    /**
     * Checks if ordinate values in the minimum index are less than or
     * equal to the corresponding ordinate value in the maximum index.
     *
     * @throws IllegalArgumentException if an ordinate value in the minimum index is not
     *         less than or equal to the corresponding ordinate value in the maximum index.
     */
    private static void checkCoherence(final int[] index) throws IllegalArgumentException {
        final int dimension = index.length >>> 1;
        for (int i=0; i<dimension; i++) {
            final int lower = index[i];
            final int upper = index[dimension+i];
            if (!(lower <= upper)) {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.ILLEGAL_GRID_ENVELOPE_$3, i, lower, upper-1));
            }
        }
    }

    /**
     * Constructs an initially empty grid envelope of the specified dimension.
     * This is used by {@link #getSubGridEnvelope} before a grid envelope goes public.
     */
    private GeneralGridEnvelope(final int dimension) {
        index = new int[dimension << 1];
    }

    /**
     * Creates a new grid envelope as a copy of the given one.
     *
     * @param envelope The grid envelope to copy.
     */
    public GeneralGridEnvelope(final GridEnvelope envelope) {
        final int dimension = envelope.getDimension();
        index = new int[dimension << 1];
        for (int i=0; i<dimension; i++) {
            index[i] = envelope.getLow(i);
            index[i + dimension] = envelope.getHigh(i) + 1;
        }
        checkCoherence(index);
    }

    /**
     * Constructs a new grid envelope.
     *
     * @param low
     *          The valid minimum inclusive grid coordinate. The array contains a minimum
     *          value (inclusive) for each dimension of the grid coverage. The lowest valid
     *          grid coordinate is often zero, but this is not mandatory.
     * @param high
     *          The valid maximum grid coordinate. The array contains a maximum
     *          value for each dimension of the grid coverage.
     * @param isHighIncluded
     *          {@code true} if the {@code high} values are inclusive (as in ISO 19123
     *          specification), or {@code false} if they are exclusive (as in Java usage).
     *          This argument does not apply to low values, which are always inclusive.
     *
     * @see #getLow()
     * @see #getHigh()
     */
    public GeneralGridEnvelope(final int[] low, final int[] high, final boolean isHighIncluded) {
        if (low.length != high.length) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_$2, low.length, high.length));
        }
        index = Arrays.copyOf(low, low.length + high.length);
        System.arraycopy(high, 0, index, low.length, high.length);
        if (isHighIncluded) {
            for (int i=low.length; i<index.length; i++) {
                index[i]++;
            }
        }
        checkCoherence(index);
    }

    /**
     * Constructs a multi-dimensional grid envelope defined by a {@link Rectangle}.
     * The two first dimensions are set to the
     * [{@linkplain Rectangle#x x} &hellip; x+{@linkplain Rectangle#width width}-1] and
     * [{@linkplain Rectangle#y y} &hellip; y+{@linkplain Rectangle#height height}-1]
     * inclusive ranges respectively.
     * Extra dimensions (if any) are set to the [0&hellip;0] inclusive range.
     *
     * @param rect The grid coordinates as a rectangle.
     * @param dimension Number of dimensions for this grid envelope.
     *        Must be equals or greater than 2.
     */
    public GeneralGridEnvelope(final Rectangle rect, final int dimension) {
        this(rect.x, rect.y, rect.width, rect.height, dimension);
    }

    /**
     * Constructs multi-dimensional grid envelope defined by a {@link Raster}.
     * The two first dimensions are set to the
     * [{@linkplain Raster#getMinX x} &hellip; x+{@linkplain Raster#getWidth width}-1] and
     * [{@linkplain Raster#getMinY y} &hellip; y+{@linkplain Raster#getHeight height}-1]
     * inclusive ranges respectively.
     * Extra dimensions (if any) are set to the [0&hellip;0] inclusive range.
     *
     * @param raster The raster for which to construct a grid envelope.
     * @param dimension Number of dimensions for this grid envelope.
     *        Must be equals or greater than 2.
     */
    public GeneralGridEnvelope(final Raster raster, final int dimension) {
        this(raster.getMinX(), raster.getMinY(), raster.getWidth(), raster.getHeight(), dimension);
    }

    /**
     * Constructs multi-dimensional grid envelope defined by a {@link RenderedImage}.
     * The two first dimensions are set to the
     * [{@linkplain RenderedImage#getMinX x} &hellip; x+{@linkplain RenderedImage#getWidth width}-1] and
     * [{@linkplain RenderedImage#getMinY y} &hellip; y+{@linkplain RenderedImage#getHeight height}-1]
     * inclusive ranges respectively.
     * Extra dimensions (if any) are set to the [0&hellip;0] inclusive range.
     *
     * @param image The image for which to construct a grid envelope.
     * @param dimension Number of dimensions for this grid envelope.
     *        Must be equals or greater than 2.
     */
    public GeneralGridEnvelope(final RenderedImage image, final int dimension) {
        this(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight(), dimension);
    }

    /**
     * Constructs a multi-dimensional grid envelope. We keep this constructor private because
     * the arguments order can be confusing. Forcing usage of {@link Rectangle} in public API
     * is probably safer.
     */
    private GeneralGridEnvelope(int x, int y, int width, int height, int dimension) {
        if (dimension < 2) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "dimension", dimension));
        }
        index = new int[dimension << 1];
        index[0] = x;
        index[1] = y;
        index[dimension + 0] = x + width;  // Reminder: upper values in index[] are exclusive.
        index[dimension + 1] = y + height; // So there is no +1 offset to add here.
        Arrays.fill(index, dimension+2, index.length, 1);
        checkCoherence(index);
    }

    /**
     * Casts the specified envelope into a grid envelope. This is sometime useful after an
     * envelope has been transformed from "real world" coordinates to grid coordinates using the
     * {@linkplain org.opengis.coverage.grid.GridGeometry#getGridToCRS grid to CRS} transform.
     * The floating point values are rounded toward the nearest integers.
     *
     * {@section Note about rounding mode}
     * It would have been possible to round the {@linkplain Envelope#getMinimum minimal value}
     * toward {@linkplain Math#floor floor} and the {@linkplain Envelope#getMaximum maximal value}
     * toward {@linkplain Math#ceil ceil} in order to make sure that the grid envelope encompass
     * fully the envelope - like what Java2D does when converting {@link java.awt.geom.Rectangle2D}
     * to {@link Rectangle}). But this approach may increase by 1 or 2 units the image
     * {@linkplain RenderedImage#getWidth width} or {@linkplain RenderedImage#getHeight height}. For
     * example the range <code>[-0.25 &hellip; 99.75]</code> (which is exactly 101 units wide) would be casted
     * to <code>[-1 &hellip; 100]</code>, which is 102 units wide. This leads to unexpected results when using
     * grid envelope with image operations like "{@link javax.media.jai.operator.AffineDescriptor
     * Affine}". For avoiding such changes in size, it is necessary to use the same rounding mode
     * for both minimal and maximal values. The selected rounding mode is {@linkplain Math#round
     * nearest integer} in this implementation.
     * <p>
     * {@section Anchor}
     * According OpenGIS specification, {@linkplain org.opengis.coverage.grid.GridGeometry grid
     * geometry} maps pixel's center. But envelopes typically encompass all pixels. This means
     * that grid coordinates (0,0) has an envelope starting at (-0.5, -0.5). In order to revert
     * back such envelope to a grid envelope, it is necessary to add 0.5 to every coordinates
     * (including the maximum value since it is exclusive in a grid envelope). This offset is
     * applied only if {@code anchor} is {@link PixelInCell#CELL_CENTER}. Users who don't want
     * such offset should specify {@link PixelInCell#CELL_CORNER}.
     * <p>
     * The convention is specified as a {@link PixelInCell} code instead than the more detailed
     * {@link org.opengis.metadata.spatial.PixelOrientation} because the latter is restricted to
     * the two-dimensional case while the former can be used for any number of dimensions.
     *
     * @param envelope
     *          The envelope to use for initializing this grid envelope.
     * @param anchor
     *          Whatever envelope coordinates map to pixel center or pixel corner. Should be
     *          {@link PixelInCell#CELL_CENTER} for OGC convention (an offset of 0.5 will be
     *          added to every envelope coordinate values), or {@link PixelInCell#CELL_CORNER}
     *          for Java2D/JAI convention (no offset will be added).
     * @param isHighIncluded
     *          {@code true} if the envelope maximal values are inclusive, or {@code false} if
     *          they are exclusive. This argument does not apply to minimal values, which are
     *          always inclusive.
     * @throws IllegalArgumentException
     *          If {@code anchor} is not valid.
     *
     * @see org.geotoolkit.geometry.GeneralEnvelope#GeneralEnvelope(GridEnvelope, PixelInCell,
     *      org.opengis.referencing.operation.MathTransform,
     *      org.opengis.referencing.crs.CoordinateReferenceSystem)
     */
    public GeneralGridEnvelope(final Envelope envelope, final PixelInCell anchor,
                               final boolean isHighIncluded)
            throws IllegalArgumentException
    {
        final double offset = PixelTranslation.getPixelTranslation(anchor) + 0.5;
        final int dimension = envelope.getDimension();
        index = new int[dimension << 1];
        for (int i=0; i<dimension; i++) {
            // See "note about conversion of floating point values to integers" in the JavaDoc.
            index[i            ] = (int) Math.round(envelope.getMinimum(i) + offset);
            index[i + dimension] = (int) Math.round(envelope.getMaximum(i) + offset);
        }
        if (isHighIncluded) {
            for (int i=(index.length >>> 1); i<index.length; i++) {
                index[i]++;
            }
        }
    }

    /**
     * Returns the number of dimensions.
     */
    @Override
    public int getDimension() {
        return index.length >>> 1;
    }

    /**
     * Returns the valid minimum inclusive grid coordinates.
     * The sequence contains a minimum value for each dimension of the grid coverage.
     */
    @Override
    public GridCoordinates getLow() {
        if (low == null) {
            low = new GeneralGridCoordinates.Immutable(index, 0, index.length >>> 1);
        }
        return low;
    }

    /**
     * Returns the valid maximum <strong>inclusive</strong> grid coordinates.
     * The sequence contains a maximum value for each dimension of the grid coverage.
     */
    @Override
    public GridCoordinates getHigh() {
        if (high == null) {
            final GeneralGridCoordinates.Immutable coords;
            coords = new GeneralGridCoordinates.Immutable(index, index.length >>> 1, index.length);
            coords.decrement();
            high = coords;
        }
        return high;
    }

    /**
     * Returns the valid minimum inclusive grid coordinate along the specified dimension.
     *
     * @see #getLow()
     */
    @Override
    public int getLow(final int dimension) {
        if (dimension < (index.length >>> 1)) {
            return index[dimension];
        }
        throw new ArrayIndexOutOfBoundsException(dimension);
    }

    /**
     * Returns the valid maximum <strong>inclusive</strong>
     * grid coordinate along the specified dimension.
     *
     * @see #getHigh()
     */
    @Override
    public int getHigh(final int dimension) {
        if (dimension >= 0) {
            return index[dimension + (index.length >>> 1)] - 1;
        }
        throw new ArrayIndexOutOfBoundsException(dimension);
    }

    /**
     * Returns the number of integer grid coordinates along the specified dimension.
     * This is equal to {@code getHigh(dimension) - getLow(dimension) + 1}.
     */
    @Override
    public int getSpan(final int dimension) {
        return index[dimension + (index.length >>> 1)] - index[dimension];
    }

    /**
     * Returns a new grid envelope that encompass only some dimensions of this grid envelope.
     * This method copies this grid envelope into a new grid envelope, beginning at dimension
     * {@code lower} and extending to dimension {@code upper-1} inclusive. Thus the dimension
     * of the sub grid envelope is {@code upper - lower}.
     *
     * @param  lower The first dimension to copy, inclusive.
     * @param  upper The last  dimension to copy, exclusive.
     * @return The sub grid envelope.
     * @throws IndexOutOfBoundsException if an index is out of bounds.
     */
    public GeneralGridEnvelope getSubGridEnvelope(final int lower, final int upper)
            throws IndexOutOfBoundsException
    {
        final int curDim = index.length >>> 1;
        final int newDim = upper - lower;
        if (lower<0 || lower>curDim) {
            throw new IndexOutOfBoundsException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "lower", lower));
        }
        if (newDim<0 || upper>curDim) {
            throw new IndexOutOfBoundsException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_$2, "upper", upper));
        }
        final GeneralGridEnvelope sub = new GeneralGridEnvelope(newDim);
        System.arraycopy(index, lower,        sub.index, 0,      newDim);
        System.arraycopy(index, lower+curDim, sub.index, newDim, newDim);
        return sub;
    }

    /**
     * Returns a {@link Rectangle} with the same bounds as this {@code GeneralGridEnvelope}.
     * This is a convenience method for inter-operability with Java2D.
     *
     * @return A rectangle with the same bounds than this grid envelope.
     * @throws IllegalStateException if this grid envelope is not two-dimensional.
     */
    public Rectangle toRectangle() throws IllegalStateException {
        if (index.length == 4) {
            return new Rectangle(index[0], index[1], index[2]-index[0], index[3]-index[1]);
        } else {
            throw new IllegalStateException(Errors.format(
                    Errors.Keys.NOT_TWO_DIMENSIONAL_$1, getDimension()));
        }
    }

    /**
     * Returns a hash value for this grid envelope. This value need not remain
     * consistent between different implementations of the same class.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(index) ^ (int) serialVersionUID;
    }

    /**
     * Compares the specified object with this grid envelope for equality.
     *
     * @param object The object to compare with this grid envelope for equality.
     * @return {@code true} if the given object is equal to this grid envelope.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof GeneralGridEnvelope) {
            final GeneralGridEnvelope that = (GeneralGridEnvelope) object;
            return Arrays.equals(this.index, that.index);
        }
        return false;
    }

    /**
     * Returns a string representation of this grid envelope. The returned string is
     * implementation dependent. It is usually provided for debugging purposes.
     */
    @Override
    public String toString() {
        return toString(this);
    }

    /**
     * Returns a string representation of the specified grid envelope.
     */
    static String toString(final GridEnvelope envelope) {
        final int dimension = envelope.getDimension();
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(envelope));
        buffer.append('[');
        for (int i=0; i<dimension; i++) {
            if (i != 0) {
                buffer.append(", ");
            }
            buffer.append(envelope.getLow(i)).append('…').append(envelope.getHigh(i));
        }
        return buffer.append(']').toString();
    }
}
