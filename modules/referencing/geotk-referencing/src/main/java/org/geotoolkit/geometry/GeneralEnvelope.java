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
import org.opengis.coverage.grid.GridEnvelope;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.metadata.spatial.PixelOrientation;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.internal.InternalUtilities;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.display.shape.XRectangle2D;
import org.apache.sis.geometry.Envelopes;

import static org.apache.sis.util.ArgumentChecks.*;


/**
 * A minimum bounding box or rectangle.
 *
 * <p>This class is kept as a workaround for the "not yet working" {@code GeneralEnvelope(GeographicBoundingBox)}
 * constructor in the Apache SIS class, and for some methods not yet ported to SIS (because of uncertain value).</p>
 *
 * <p>Note that the {@code reorderCorners()} method in Geotk has been renamed {@code simplify()} in SIS,
 * and {@code reduceToDomain(false)} has been renamed as {@code normalize()}.</p>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Simone Giannecchini (Geosolutions)
 * @author Johann Sorel (Geomatys)
 * @version 3.21
 *
 * @since 1.2
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.geometry.GeneralEnvelope}.
 */
@Deprecated
public class GeneralEnvelope extends org.apache.sis.geometry.GeneralEnvelope implements Cloneable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 1752330560227688940L;

    /**
     * Constructs an empty envelope with the specified coordinate reference system.
     * All ordinates are initialized to 0.
     *
     * @param crs The coordinate reference system.
     *
     * @since 2.2
     */
    public GeneralEnvelope(final CoordinateReferenceSystem crs) {
        super(crs);
    }

    /**
     * Constructs a new envelope with the same data than the specified envelope.
     *
     * @param envelope The envelope to copy.
     *
     * @see Envelope2D#Envelope2D(Envelope)
     */
    public GeneralEnvelope(final Envelope envelope) {
        super(envelope);
    }

    /**
     * Constructs two-dimensional envelope defined by a {@link Rectangle2D}.
     * The coordinate reference system is initially undefined.
     *
     * @param rect The rectangle to copy.
     *
     * @see Envelope2D#Envelope2D(CoordinateReferenceSystem, Rectangle2D)
     */
    public GeneralEnvelope(final Rectangle2D rect) {
        super(new double[] {rect.getMinX(), rect.getMinY()},
              new double[] {rect.getMaxX(), rect.getMaxY()});
    }

    /**
     * Constructs a georeferenced envelope from a grid envelope transformed using the specified
     * math transform. The <cite>grid to CRS</cite> transform should map either the
     * {@linkplain PixelInCell#CELL_CENTER cell center} (as in OGC convention) or
     * {@linkplain PixelInCell#CELL_CORNER cell corner} (as in Java2D/JAI convention)
     * depending on the {@code anchor} value. This constructor creates an envelope
     * containing entirely all pixels on a <cite>best effort</cite> basis - usually
     * accurate for affine transforms.
     * <p>
     * <b>Note:</b> The convention is specified as a {@link PixelInCell} code instead than
     * the more detailed {@link PixelOrientation}, because the later is restricted to the
     * two-dimensional case while the former can be used for any number of dimensions.
     *
     * @param gridEnvelope The grid envelope in integer coordinates.
     * @param anchor       Whatever grid coordinates map to pixel center or pixel corner.
     * @param gridToCRS    The transform (usually affine) from grid envelope to the CRS.
     * @param crs          The CRS for the envelope to be created, or {@code null} if unknown.
     *
     * @throws MismatchedDimensionException If one of the supplied object doesn't have
     *         a dimension compatible with the other objects.
     * @throws IllegalArgumentException if an argument is illegal for some other raisons,
     *         including failure to use the provided math transform.
     *
     * @since 2.3
     *
     * @see org.geotoolkit.referencing.operation.builder.GridToEnvelopeMapper
     * @see org.geotoolkit.coverage.grid.GeneralGridEnvelope#GeneralGridEnvelope(Envelope,PixelInCell,boolean)
     */
    public GeneralEnvelope(final GridEnvelope  gridEnvelope,
                           final PixelInCell   anchor,
                           final MathTransform gridToCRS,
                           final CoordinateReferenceSystem crs)
            throws IllegalArgumentException
    {
//  Uncomment next line if Sun fixes RFE #4093999
//      ensureNonNull("gridEnvelope", gridEnvelope);
        super(gridEnvelope.getDimension());
        ensureNonNull("gridToCRS", gridToCRS);
        final int dimension = getDimension();
        ensureSameDimension(dimension, gridToCRS.getSourceDimensions());
        ensureSameDimension(dimension, gridToCRS.getTargetDimensions());
        final double offset = PixelTranslation.getPixelTranslation(anchor) + 0.5;
        for (int i=0; i<dimension; i++) {
            /*
             * According OpenGIS specification, GridGeometry maps pixel's center. We want a bounding
             * box for all pixels, not pixel's centers. Offset by 0.5 (use -0.5 for maximum too, not
             * +0.5, since maximum is exclusive).
             *
             * Note: the offset of 1 after getHigh(i) is because high values are inclusive according
             *       ISO specification, while our algorithm and Java usage expect exclusive values.
             */
            setRange(i, gridEnvelope.getLow(i) - offset, gridEnvelope.getHigh(i) - (offset - 1));
        }
        final Envelope transformed;
        try {
            transformed = Envelopes.transform(gridToCRS, this);
        } catch (TransformException exception) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_TRANSFORM_FOR_TYPE_1,
                    gridToCRS.getClass()), exception);
        }
        super.setEnvelope(transformed);
        setCoordinateReferenceSystem(crs);
    }

    /**
     * Makes sure the specified dimensions are identical.
     */
    private static void ensureSameDimension(final int dim1, final int dim2) throws MismatchedDimensionException {
        if (dim1 != dim2) {
            throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_2, dim1, dim2));
        }
    }

    private double[] ordinates() {
        final int dimension = super.getDimension();
        final double[] ordinates = new double[dimension * 2];
        for (int i=0; i<dimension; i++) {
            ordinates[i] = super.getLower(i);
            ordinates[i + dimension] = super.getUpper(i);
        }
        return ordinates;
    }

    private void ordinates(final double[] ordinates) {
        final int dimension = super.getDimension();
        for (int i=0; i<dimension; i++) {
            super.setRange(i, ordinates[i], ordinates[i + dimension]);
        }
    }

    /**
     * Fixes rounding errors up to a given tolerance level. For each value {@code ordinates[i]}
     * at dimension <var>i</var>, this method multiplies the ordinate value by the given factor,
     * then round the result only if the product is close to an integer value. The threshold is
     * defined by the {@code maxULP} argument in ULP units (<cite>Unit in the Last Place</cite>).
     * If and only if the product has been rounded, it is divided by the factor and stored in this
     * envelope in place of the original ordinate.
     * <p>
     * This method is useful after envelope calculations subject to rounding errors, like the
     * {@link #GeneralEnvelope(GridEnvelope, PixelInCell, MathTransform, CoordinateReferenceSystem)}
     * constructor.
     *
     * @param factor The factor by which to multiply ordinates before rounding
     *               and divide after rounding. A recommended value is 360.
     * @param maxULP The maximal change allowed in ULPs (Unit in the Last Place).
     *
     * @since 3.11
     */
    public void roundIfAlmostInteger(final double factor, final int maxULP) {
        ensureStrictlyPositive("factor", factor);
        final double[] ordinates = ordinates();
        for (int i=0; i<ordinates.length; i++) {
            ordinates[i] = InternalUtilities.adjustForRoundingError(ordinates[i], factor, maxULP);
        }
        ordinates(ordinates);
    }

    /**
     * Returns a {@link Rectangle2D} with the {@linkplain #getMinimum(int) minimum}
     * and {@linkplain #getMaximum(int) maximum} values of this {@code Envelope}.
     * This envelope must be two-dimensional before this method is invoked.
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * If this envelope spans the anti-meridian, then the longitude dimension will be
     * extended to full range of its coordinate system axis (typically [-180 … 180]°).
     *
     * @return This envelope as a two-dimensional rectangle.
     * @throws IllegalStateException if this envelope is not two-dimensional.
     *
     * @since 3.20 (derived from 3.00)
     */
    public Rectangle2D toRectangle2D() throws IllegalStateException {
        final int dimension = getDimension();
        if (dimension != 2) {
            throw new IllegalStateException(Errors.format(
                    Errors.Keys.NOT_TWO_DIMENSIONAL_1, dimension));
        }
        return XRectangle2D.createFromExtremums(
                getMinimum(0), getMinimum(1),
                getMaximum(0), getMaximum(1));
    }
}
