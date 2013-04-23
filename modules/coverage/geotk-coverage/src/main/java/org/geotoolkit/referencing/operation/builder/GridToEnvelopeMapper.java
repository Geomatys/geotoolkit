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
package org.geotoolkit.referencing.operation.builder;

import java.util.Arrays;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.MathTransforms;
import org.geotoolkit.referencing.operation.transform.LinearTransform;
import org.geotoolkit.internal.referencing.AxisDirections;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * A helper class for building <var>n</var>-dimensional {@linkplain AffineTransform affine transform}
 * mapping {@linkplain GridEnvelope grid envelopes} to georeferenced {@linkplain Envelope envelopes}.
 * The affine transform will be computed automatically from the information specified by the
 * {@link #setGridExtent(GridEnvelope)} and {@link #setEnvelope(Envelope)} methods, which are
 * mandatory. All other setter methods are optional hints about the affine transform to be created.
 * <p>
 * This builder is convenient when the following conditions are meet:
 *
 * <ul>
 *   <li><p>Pixels coordinates (usually (<var>x</var>,<var>y</var>) integer values inside
 *       the rectangle specified by the grid extent) are expressed in some
 *       {@linkplain CoordinateReferenceSystem coordinate reference system} known at compile
 *       time. This is often the case. For example the CRS attached to {@link BufferedImage}
 *       has always ({@linkplain AxisDirection#COLUMN_POSITIVE column},
 *       {@linkplain AxisDirection#ROW_POSITIVE row}) axis, with the origin (0,0) in the upper
 *       left corner, and row values increasing down.</p></li>
 *
 *   <li><p>"Real world" coordinates (inside the envelope) are expressed in arbitrary
 *       <em>horizontal</em> coordinate reference system. Axis directions may be
 *       ({@linkplain AxisDirection#NORTH North}, {@linkplain AxisDirection#WEST West}),
 *       or ({@linkplain AxisDirection#EAST East}, {@linkplain AxisDirection#NORTH North}),
 *       <i>etc</i>.</p></li>
 * </ul>
 *
 * In such case (and assuming that the image CRS has the same characteristics than the
 * {@link BufferedImage} CRS described above):
 *
 * <ul>
 *   <li><p>{@link #setSwapXY swapXY} shall be set to {@code true} if the "real world" axis
 *       order is ({@linkplain AxisDirection#NORTH North}, {@linkplain AxisDirection#EAST East})
 *       instead of ({@linkplain AxisDirection#EAST East}, {@linkplain AxisDirection#NORTH North}).
 *       This axis swapping is necessary for mapping the ({@linkplain AxisDirection#COLUMN_POSITIVE
 *       column}, {@linkplain AxisDirection#ROW_POSITIVE row}) axis order associated to the
 *       image CRS.</p></li>
 *
 *   <li><p>In addition, the "real world" axis directions shall be reversed (by invoking
 *       <code>{@linkplain #reverseAxis reverseAxis}(dimension)</code>) if their direction is
 *       {@link AxisDirection#WEST WEST} (<var>x</var> axis) or {@link AxisDirection#NORTH NORTH}
 *       (<var>y</var> axis), in order to get them oriented toward the {@link AxisDirection#EAST
 *       EAST} or {@link AxisDirection#SOUTH SOUTH} direction respectively. The later may seems
 *       unnatural, but it reflects the fact that row values are increasing down in an
 *       {@link BufferedImage} CRS.</p></li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.3
 * @module
 */
public class GridToEnvelopeMapper {
    /**
     * A bit mask for the {@link #setSwapXY swapXY} property.
     *
     * @see #isAutomatic
     * @see #setAutomatic
     */
    public static final int SWAP_XY = 1;

    /**
     * A bit mask for the {@link #setReverseAxis reverseAxis} property.
     *
     * @see #isAutomatic
     * @see #setAutomatic
     */
    public static final int REVERSE_AXIS = 2;

    /**
     * A combination of bit masks telling which property were user-defined.
     *
     * @see #isAutomatic
     * @see #setAutomatic
     */
    private int defined;

    /**
     * The grid envelope, or {@code null} if not yet specified.
     */
    private GridEnvelope gridExtent;

    /**
     * The geodetic envelope, or {@code null} if not yet specified.
     */
    private Envelope envelope;

    /**
     * Whatever the {@code gridToCRS} transform will maps pixel center or corner.
     * The default value is {@link PixelInCell#CELL_CENTER}.
     */
    private PixelInCell anchor = PixelInCell.CELL_CENTER;

    /**
     * {@code true} if we should swap the two first axis, {@code false} if we should
     * not swap and {@code null} if this state is not yet determined.
     */
    private Boolean swapXY;

    /**
     * The axis to reverse, or {@code null} if none or not yet determined.
     */
    private boolean[] reverseAxis;

    /**
     * The math transform, or {@code null} if not yet computed.
     */
    private MathTransform transform;

    /**
     * Creates a new instance of {@code GridToEnvelopeMapper}.
     */
    public GridToEnvelopeMapper() {
    }

    /**
     * Creates a new instance for the specified grid envelope and georeferenced envelope.
     *
     * @param  gridExtent The extent of grid coordinates in a grid coverage.
     * @param  envelope The corresponding domain in user coordinate. This envelope must
     *         contains entirely all pixels, i.e. the envelope upper left corner must coincide
     *         with the upper left corner of the first pixel and the envelope lower right corner
     *         must coincide with the lower right corner of the last pixel.
     * @throws MismatchedDimensionException if the two envelopes don't have consistent dimensions.
     */
    public GridToEnvelopeMapper(final GridEnvelope gridExtent, final Envelope envelope)
            throws MismatchedDimensionException
    {
        ensureNonNull("gridExtent", gridExtent);
        ensureNonNull("envelope",   envelope);
        final int gridDim = gridExtent.getDimension();
        final int userDim = envelope.getDimension();
        if (userDim != gridDim) {
            throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_2, gridDim, userDim));
        }
        this.gridExtent = gridExtent;
        this.envelope   = envelope;
    }

    /**
     * Makes sure that the specified objects have the same dimension.
     */
    private static void ensureDimensionMatch(final GridEnvelope gridExtent,
            final Envelope envelope, final boolean checkingRange)
    {
        if (gridExtent != null && envelope != null) {
            final String label;
            final int dim1, dim2;
            if (checkingRange) {
                label = "gridExtent";
                dim1  = gridExtent.getDimension();
                dim2  = envelope  .getDimension();
            } else {
                label = "envelope";
                dim1  = envelope .getDimension();
                dim2  = gridExtent.getDimension();
            }
            if (dim1 != dim2) {
                throw new MismatchedDimensionException(Errors.format(
                        Errors.Keys.MISMATCHED_DIMENSION_3, label, dim1, dim2));
            }
        }
    }

    /**
     * Flushes any information cached in this object.
     */
    private void reset() {
        transform = null;
        if (isAutomatic(REVERSE_AXIS)) {
            reverseAxis = null;
        }
        if (isAutomatic(SWAP_XY)) {
            swapXY = null;
        }
    }

    /**
     * Returns whatever the grid coordinates map {@linkplain PixelInCell#CELL_CENTER pixel center}
     * or {@linkplain PixelInCell#CELL_CORNER pixel corner}. The former is OGC convention while
     * the later is Java2D/JAI convention. The default is cell center (OGC convention).
     *
     * @return Whatever the grid coordinates map pixel center or corner.
     *
     * @since 2.5
     */
    public PixelInCell getPixelAnchor() {
        return anchor;
    }

    /**
     * Sets whatever the grid coordinates map {@linkplain PixelInCell#CELL_CENTER pixel center}
     * or {@linkplain PixelInCell#CELL_CORNER pixel corner}. The former is OGC convention
     * while the later is Java2D/JAI convention. The default is cell center (OGC convention).
     *
     * @param anchor Whatever the grid coordinates map pixel center or corner.
     *
     * @since 2.5
     */
    public void setPixelAnchor(final PixelInCell anchor) {
        ensureNonNull("anchor", anchor);
        if (!Utilities.equals(this.anchor, anchor)) {
            this.anchor = anchor;
            reset();
        }
    }

    /**
     * Returns the The extent of grid coordinates in a grid coverage.
     *
     * @return The The extent of grid coordinates in a grid coverage.
     * @throws IllegalStateException if the grid envelope has not yet been defined.
     */
    public GridEnvelope getGridExtent() throws IllegalStateException {
        if (gridExtent == null) {
            throw new IllegalStateException(Errors.format(
                    Errors.Keys.NO_PARAMETER_VALUE_1, "gridEnvelope"));
        }
        return gridExtent;
    }

    /**
     * Sets the The extent of grid coordinates in a grid coverage.
     *
     * @param extent The new grid envelope.
     *
     * @since 3.20 (derived from 2.3)
     */
    public void setGridExtent(final GridEnvelope extent) {
        ensureNonNull("extent", extent);
        ensureDimensionMatch(extent, envelope, true);
        if (!Utilities.equals(gridExtent, extent)) {
            gridExtent = extent;
            reset();
        }
    }

    /**
     * Sets the grid envelope as a two-dimensional rectangle. This convenience method
     * creates a {@link GridEnvelope2D} from the given rectangle and delegates to the
     * {@link #setGridExtent(GridEnvelope)} method.
     *
     * @param extent The new grid envelope.
     *
     * @since 3.20 (derived from 3.15)
     */
    public void setGridExtent(final Rectangle extent) {
        final GridEnvelope ge;
        if (extent instanceof GridEnvelope) {
            ge = (GridEnvelope) extent;
        } else {
            ensureNonNull("gridEnvelope", extent);
            ge = new GridEnvelope2D(extent);
        }
        setGridExtent(ge);
    }

    /**
     * Sets the grid envelope as a two-dimensional rectangle. This convenience method
     * creates a {@link GridEnvelope2D} from the given rectangle and delegates to the
     * {@link #setGridExtent(GridEnvelope)} method.
     *
     * @param x The minimal <var>x</var> ordinate.
     * @param y The minimal <var>y</var> ordinate.
     * @param width  The number of valid ordinates along the <var>x</var> axis.
     * @param height The number of valid ordinates along the <var>y</var> axis.
     *
     * @since 3.20 (derived from 3.15)
     */
    public void setGridExtent(final int x, final int y, final int width, final int height) {
        setGridExtent((GridEnvelope) new GridEnvelope2D(x, y, width, height));
    }

    /**
     * Returns the georeferenced envelope. For performance reason, this method does not
     * clone the envelope. So the returned object should not be modified.
     *
     * @return The envelope.
     * @throws IllegalStateException if the envelope has not yet been defined.
     */
    public Envelope getEnvelope() throws IllegalStateException {
        if (envelope == null) {
            throw new IllegalStateException(Errors.format(
                    Errors.Keys.NO_PARAMETER_VALUE_1, "envelope"));
        }
        return envelope;
    }

    /**
     * Sets the georeferenced envelope. This method do not clone the specified envelope,
     * so it should not be modified after this method has been invoked.
     *
     * @param envelope The new envelope.
     */
    public void setEnvelope(final Envelope envelope) {
        ensureNonNull("envelope", envelope);
        ensureDimensionMatch(gridExtent, envelope, false);
        if (!Utilities.equals(this.envelope, envelope)) {
            this.envelope = envelope;
            reset();
        }
    }

    /**
     * Sets the envelope as a two-dimensional rectangle. This convenience method creates an
     * {@link Envelope2D} from the given rectangle and delegates to the
     * {@link #setEnvelope(Envelope)} method.
     *
     * @param envelope The new envelope.
     *
     * @since 3.15
     */
    public void setEnvelope(final Rectangle2D envelope) {
        final Envelope env;
        if (envelope instanceof Envelope) {
            env = (Envelope) envelope;
        } else {
            ensureNonNull("envelope", envelope);
            env = new Envelope2D(null, envelope);
        }
        setEnvelope(env);
    }

    /**
     * Sets the envelope as a two-dimensional rectangle. This convenience method creates an
     * {@link Envelope2D} from the given rectangle and delegates to the
     * {@link #setEnvelope(Envelope)} method.
     *
     * @param x The <var>x</var> minimal value.
     * @param y The <var>y</var> minimal value.
     * @param width The envelope width.
     * @param height The envelope height.
     *
     * @since 3.15
     */
    public void setEnvelope(final double x, final double y, final double width, final double height) {
        setEnvelope((Envelope) new Envelope2D(null, x, y, width, height));
    }

    /**
     * Applies heuristic rules in order to determine if the two first axis should be interchanged.
     */
    private static boolean swapXY(final CoordinateSystem cs) {
        if (cs != null && cs.getDimension() >= 2) {
            return AxisDirection.NORTH.equals(AxisDirections.absolute(cs.getAxis(0).getDirection())) &&
                   AxisDirection.EAST .equals(AxisDirections.absolute(cs.getAxis(1).getDirection()));
        }
        return false;
    }

    /**
     * Returns {@code true} if the two first axis should be interchanged. If
     * <code>{@linkplain #isAutomatic isAutomatic}({@linkplain #SWAP_XY})</code>
     * returns {@code true} (which is the default), then this method make the
     * following assumptions:
     *
     * <ul>
     *   <li><p>Axis order in the grid envelope matches exactly axis order in the genreferenced envelope,
     *       except for the special case described in the next point. In other words, if axis order in
     *       the underlying image is (<var>column</var>, <var>row</var>) (which is the case for
     *       a majority of images), then the envelope should probably have a (<var>longitude</var>,
     *       <var>latitude</var>) or (<var>easting</var>, <var>northing</var>) axis order.</p></li>
     *
     *   <li><p>An exception to the above rule applies for CRS using exactly the following axis
     *       order: ({@link AxisDirection#NORTH NORTH}|{@link AxisDirection#SOUTH SOUTH},
     *       {@link AxisDirection#EAST EAST}|{@link AxisDirection#WEST WEST}). An example
     *       of such CRS is {@code EPSG:4326}. In this particular case, this method will
     *       returns {@code true}, thus suggesting to interchange the
     *       (<var>y</var>,<var>x</var>) axis for such CRS.</p></li>
     * </ul>
     *
     * @return {@code true} if the two first axis should be interchanged.
     */
    public boolean getSwapXY() {
        if (swapXY == null) {
            boolean value = false;
            if (isAutomatic(SWAP_XY)) {
                value = swapXY(getCoordinateSystem());
            }
            swapXY = Boolean.valueOf(value);
        }
        return swapXY.booleanValue();
    }

    /**
     * Tells if the two first axis should be interchanged. Invoking this method force
     * <code>{@linkplain #isAutomatic isAutomatic}({@linkplain #SWAP_XY})</code> to {@code false}.
     *
     * @param swapXY {@code true} if the two first axis should be interchanged.
     */
    public void setSwapXY(final boolean swapXY) {
        final Boolean newValue = Boolean.valueOf(swapXY);
        if (!newValue.equals(this.swapXY)) {
            reset();
        }
        this.swapXY = newValue;
        defined |= SWAP_XY;
    }

    /**
     * Returns which (if any) axis in <cite>user</cite> space (not grid space)
     * should have their direction reversed. If <code>{@linkplain #isAutomatic
     * isAutomatic}({@linkplain #REVERSE_AXIS})</code> returns {@code true}
     * (which is the default), then this method makes the following assumptions:
     * <p>
     * <ul>
     *   <li>Axis should be reverted if needed in order to have the most commonly used
     *       direction for increasing positive values (North, East, Up, Future).</li>
     *   <li>An exception to the above rule is the second axis in grid space,
     *       which is assumed to be the <var>y</var> axis on output device (usually
     *       the screen). This axis is reversed again in order to match the bottom
     *       direction often used with such devices.</li>
     * </ul>
     *
     * @return The reversal state of each axis, or {@code null} if unspecified.
     */
    public boolean[] getReverseAxis() {
        if (reverseAxis == null) {
            final CoordinateSystem cs = getCoordinateSystem();
            if (cs != null) {
                final int dimension = cs.getDimension();
                reverseAxis = new boolean[dimension];
                if (isAutomatic(REVERSE_AXIS)) {
                    for (int i=0; i<dimension; i++) {
                        reverseAxis[i] = AxisDirections.isOpposite(cs.getAxis(i).getDirection());
                    }
                    if (dimension >= 2) {
                        final int i = getSwapXY() ? 0 : 1;
                        reverseAxis[i] = !reverseAxis[i];
                    }
                }
            } else {
                // No coordinate system. Reverse the second axis unconditionally
                // (except if there is not enough dimensions).
                int length = 0;
                if (gridExtent != null) {
                    length = gridExtent.getDimension();
                } else if (envelope != null) {
                    length = envelope.getDimension();
                }
                if (length >= 2) {
                    reverseAxis = new boolean[length];
                    reverseAxis[1] = true;
                }
            }
        }
        return (reverseAxis != null) ? reverseAxis.clone() : null;
    }

    /**
     * Sets which (if any) axis in <cite>user</cite> space (not grid space) should have
     * their direction reversed. Invoking this method force <code>{@linkplain #isAutomatic
     * isAutomatic}({@linkplain #REVERSE_AXIS})</code> to {@code false}.
     *
     * @param reverse The reversal state of each axis. A {@code null} value means to reverse no axis.
     */
    public void setReverseAxis(boolean[] reverse) {
        if (reverse != null) {
            reverse = reverse.clone();
        }
        if (!Arrays.equals(reverseAxis, reverse)) {
            reset();
        }
        reverseAxis = reverse;
        defined |= REVERSE_AXIS;
    }

    /**
     * Reverses a single axis in user space. Invoking this methods <var>n</var> time
     * is equivalent to creating a boolean {@code reverse} array of the appropriate length,
     * setting {@code reverse[dimension] = true} for the <var>n</var> axis to be reversed,
     * and invoke <code>{@linkplain #setReverseAxis setReverseAxis}(reverse)</code>.
     *
     * @param dimension The index of the axis to reverse.
     */
    public void reverseAxis(final int dimension) {
        if (reverseAxis == null) {
            final int length;
            if (gridExtent != null) {
                length = gridExtent.getDimension();
            } else {
                ensureNonNull("envelope", envelope);
                length = envelope.getDimension();
            }
            reverseAxis = new boolean[length];
        }
        if (!reverseAxis[dimension]) {
            reset();
        }
        reverseAxis[dimension] = true;
        defined |= REVERSE_AXIS;
    }

    /**
     * Returns {@code true} if all properties designed by the specified bit mask
     * will be computed automatically.
     *
     * @param mask Any combination of {@link #REVERSE_AXIS} or {@link #SWAP_XY}.
     * @return {@code true} if all properties given by the mask will be computed automatically.
     */
    public boolean isAutomatic(final int mask) {
        return (defined & mask) == 0;
    }

    /**
     * Sets all properties designed by the specified bit mask as automatic. Their
     * value will be computed automatically by the corresponding methods (e.g.
     * {@link #getReverseAxis}, {@link #getSwapXY}). By default, all properties
     * are automatic.
     *
     * @param mask Any combination of {@link #REVERSE_AXIS} or {@link #SWAP_XY}.
     */
    public void setAutomatic(final int mask) {
        defined &= ~mask;
    }

    /**
     * Returns the coordinate system in use with the envelope.
     */
    private CoordinateSystem getCoordinateSystem() {
        if (envelope != null) {
            final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
            if (crs != null) {
                return crs.getCoordinateSystem();
            }
        }
        return null;
    }

    /**
     * Creates a <cite>Grid to Envelope</cite> (or <cite>grid to CRS</cite>) transform using
     * the information provided by setter methods. The default implementation returns an instance
     * of {@link LinearTransform}, but subclasses could create more complex transforms.
     *
     * @return The <cite>grid to CRS</cite> transform.
     * @throws IllegalStateException if the grid envelope or the georeferenced envelope were not set.
     */
    public MathTransform createTransform() throws IllegalStateException {
        if (transform == null) {
            final GridEnvelope gridEnvelope = getGridExtent();
            final Envelope     userEnvelope = getEnvelope();
            final boolean      swapXY       = getSwapXY();
            final boolean[]    reverse      = getReverseAxis();
            final PixelInCell  gridType     = getPixelAnchor();
            final int          dimension    = gridEnvelope.getDimension();
            /*
             * Setup the multi-dimensional affine transform for use with OpenGIS.
             * According OpenGIS specification, transforms must map pixel center.
             * This is done by adding 0.5 to grid coordinates.
             */
            final double translate;
            if (PixelInCell.CELL_CENTER.equals(gridType)) {
                translate = 0.5;
            } else if (PixelInCell.CELL_CORNER.equals(gridType)) {
                translate = 0.0;
            } else {
                throw new IllegalStateException(Errors.format(
                        Errors.Keys.ILLEGAL_ARGUMENT_2, "gridType", gridType));
            }
            final Matrix matrix = Matrices.create(dimension + 1);
            for (int i=0; i<dimension; i++) {
                // NOTE: i is a dimension in the 'gridEnvelope' space (source coordinates).
                //       j is a dimension in the 'userEnvelope' space (target coordinates).
                int j = i;
                if (swapXY && j<=1) {
                    j = 1-j;
                }
                double scale = userEnvelope.getSpan(j) / gridEnvelope.getSpan(i);
                double offset;
                if (reverse == null || j >= reverse.length || !reverse[j]) {
                    offset = userEnvelope.getMinimum(j);
                } else {
                    scale  = -scale;
                    offset = userEnvelope.getMaximum(j);
                }
                offset -= scale * (gridEnvelope.getLow(i) - translate);
                matrix.setElement(j, j,         0.0   );
                matrix.setElement(j, i,         scale );
                matrix.setElement(j, dimension, offset);
            }
            transform = MathTransforms.linear(matrix);
        }
        return transform;
    }

    /**
     * Returns the <cite>Grid to Envelope</cite> (or <cite>grid to CRS</cite>)
     * transform as a two-dimensional affine transform.
     *
     * @return The <cite>grid to CRS</cite> transform as a two-dimensional affine transform.
     * @throws IllegalStateException if the math transform is not of the appropriate type.
     */
    public AffineTransform createAffineTransform() throws IllegalStateException {
        final MathTransform transform = createTransform();
        if (transform instanceof AffineTransform) {
            return (AffineTransform) transform;
        }
        throw new IllegalStateException(Errors.format(Errors.Keys.NOT_AN_AFFINE_TRANSFORM));
    }
}
