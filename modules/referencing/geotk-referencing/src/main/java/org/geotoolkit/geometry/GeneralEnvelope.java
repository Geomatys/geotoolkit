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
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.RangeMeaning;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.spatial.PixelOrientation;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.Utilities;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.internal.InternalUtilities;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.display.shape.XRectangle2D;

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
     * Constructs a new envelope with the same data than the specified
     * geographic bounding box. The coordinate reference system is set
     * to {@linkplain DefaultGeographicCRS#WGS84 WGS84}.
     *
     * @param box The bounding box to copy.
     *
     * @see Envelope2D#Envelope2D(GeographicBoundingBox)
     *
     * @since 2.4
     */
    public GeneralEnvelope(final GeographicBoundingBox box) {
        super(DefaultGeographicCRS.WGS84);
        super.setRange(0, box.getWestBoundLongitude(), box.getEastBoundLongitude());
        super.setRange(1, box.getSouthBoundLatitude(), box.getNorthBoundLatitude());
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

    /**
     * Returns {@code true} if at least one of the specified CRS is null, or both CRS are equals.
     * This special processing for {@code null} values is different from the usual contract of an
     * {@code equals} method, but allow to handle the case where the CRS is unknown.
     * <p>
     * Note that in debug mode (to be used in assertions only), the comparison are actually a bit
     * more relax than just "ignoring metadata", since some rounding errors are tolerated.
     */
    static boolean equalsIgnoreMetadata(final CoordinateReferenceSystem crs1,
            final CoordinateReferenceSystem crs2, final boolean debug)
    {
        return (crs1 == null) || (crs2 == null) || Utilities.deepEquals(crs1, crs2,
                debug ? ComparisonMode.DEBUG : ComparisonMode.IGNORE_METADATA);
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
     * Restricts this envelope to the CS or CRS
     * {@linkplain CoordinateReferenceSystem#getDomainOfValidity() domain of validity}.
     * This method performs two steps:
     *
     * <ol>
     *   <li><p>First, ensure that the envelope is contained in the {@linkplain CoordinateSystem
     *   coordinate system} domain. If some ordinates are out of range, then there is a choice
     *   depending on the {@linkplain CoordinateSystemAxis#getRangeMeaning() range meaning}:</p>
     *   <ul>
     *     <li><p>If {@link RangeMeaning#EXACT} (typically <em>latitudes</em> ordinates), then values
     *       greater than the {@linkplain CoordinateSystemAxis#getMaximumValue() maximum value} are
     *       replaced by the maximum, and values smaller than the
     *       {@linkplain CoordinateSystemAxis#getMinimumValue() minimum value} are replaced by the minimum.</p></li>
     *
     *     <li><p>If {@link RangeMeaning#WRAPAROUND} (typically <em>longitudes</em> ordinates),
     *       then a multiple of the range (e.g. 360° for longitudes) is added or subtracted.
     *       Example:
     *       <ul>
     *         <li>the [190 … 200]° longitude range is converted to [-170 … -160]°,</li>
     *         <li>the [170 … 200]° longitude range is converted to [+170 … -160]°.</li>
     *       </ul>
     *       See <cite>Spanning the anti-meridian of a Geographic CRS</cite> in the
     *       class javadoc for more information about the meaning of such range.</p></li>
     *   </ul></li>
     *   <li><p>If {@code crsDomain} is {@code true}, then the envelope from the previous step
     *   is intersected with the CRS {@linkplain CoordinateReferenceSystem#getDomainOfValidity()
     *   domain of validity}, if any.</p></li>
     * </ol>
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * Note that if the envelope is spanning the anti-meridian, then some {@linkplain #getLower(int)
     * lower} ordinate values may become greater than the {@linkplain #getUpper(int) upper} ordinate
     * values even if it was not the case before this method call. If this is not acceptable, consider
     * invoking {@link #reorderCorners()} after this method call.
     *
     * {@section Choosing the range of longitude values}
     * Geographic CRS typically have longitude values in the [-180 … +180]° range,
     * but the [0 … 360]° range is also occasionally used. Callers need to ensure
     * that this envelope CRS is associated to axes having the desired
     * {@linkplain CoordinateSystemAxis#getMinimumValue() minimum} and
     * {@linkplain CoordinateSystemAxis#getMaximumValue() maximum value}.
     * The {@link org.geotoolkit.referencing.cs.AxisRangeType} enumeration can be used
     * for shifting a geographic CRS to the desired range.
     *
     * {@section Usage}
     * This method is sometime useful before to compute the {@linkplain #add(Envelope) union}
     * or {@linkplain #intersect(Envelope) intersection} of envelopes, in order to ensure that
     * both envelopes are defined in the same domain. This method may also be invoked before
     * to project an envelope, since some projections produce {@link Double#NaN} numbers when
     * given an ordinate value out of bounds.
     *
     * @param  useDomainOfCRS {@code true} if the envelope should be restricted to
     *         the CRS <cite>domain of validity</cite> in addition to the CS domain.
     * @return {@code true} if this envelope has been modified as a result of this method call,
     *         or {@code false} if no change was done.
     *
     * @see CoordinateReferenceSystem#getDomainOfValidity()
     * @see org.geotoolkit.referencing.cs.AxisRangeType
     *
     * @since 3.11 (derived from 2.5)
     */
    public boolean reduceToDomain(final boolean useDomainOfCRS) {
        final CoordinateReferenceSystem crs = super.getCoordinateReferenceSystem();
        boolean changed = false;
        if (crs != null) {
            final double[] ordinates = ordinates();
            final int dimension = ordinates.length >>> 1;
            final CoordinateSystem cs = crs.getCoordinateSystem();
            for (int i=0; i<dimension; i++) {
                final int j = i + dimension;
                final CoordinateSystemAxis axis = cs.getAxis(i);
                final double  minimum = axis.getMinimumValue();
                final double  maximum = axis.getMaximumValue();
                final RangeMeaning rm = axis.getRangeMeaning();
                if (RangeMeaning.EXACT.equals(rm)) {
                    if (ordinates[i] < minimum) {ordinates[i] = minimum; changed = true;}
                    if (ordinates[j] > maximum) {ordinates[j] = maximum; changed = true;}
                } else if (RangeMeaning.WRAPAROUND.equals(rm)) {
                    final double csSpan = maximum - minimum;
                    if (csSpan > 0 && csSpan < Double.POSITIVE_INFINITY) {
                        double o1 = ordinates[i];
                        double o2 = ordinates[j];
                        if (Math.abs(o2-o1) >= csSpan) {
                            /*
                             * If the range exceed the CS span, then we have to replace it by the
                             * full span, otherwise the range computed by the "else" block is too
                             * small. The full range will typically be [-180 … 180]°.  However we
                             * make a special case if the two bounds are multiple of the CS span,
                             * typically [0 … 360]°. In this case the [0 … -0]° range matches the
                             * original values and is understood by GeneralEnvelope as a range
                             * spanning all the world.
                             */
                            if (o1 != minimum || o2 != maximum) {
                                if ((o1 % csSpan) == 0 && (o2 % csSpan) == 0) {
                                    ordinates[i] = +0.0;
                                    ordinates[j] = -0.0;
                                } else {
                                    ordinates[i] = minimum;
                                    ordinates[j] = maximum;
                                }
                                changed = true;
                            }
                        } else {
                            o1 = Math.floor((o1 - minimum) / csSpan) * csSpan;
                            o2 = Math.floor((o2 - minimum) / csSpan) * csSpan;
                            if (o1 != 0) {ordinates[i] -= o1; changed = true;}
                            if (o2 != 0) {ordinates[j] -= o2; changed = true;}
                        }
                    }
                }
            }
            if (useDomainOfCRS) {
                Envelope domain = Envelopes.getDomainOfValidity(crs);
                if (domain != null) {
                    final org.apache.sis.geometry.GeneralEnvelope original = new org.apache.sis.geometry.GeneralEnvelope(this);
                    final CoordinateReferenceSystem domainCRS = domain.getCoordinateReferenceSystem();
                    if (!equalsIgnoreMetadata(crs, domainCRS, false)) {
                        /*
                         * The domain may have fewer dimensions than this envelope (typically only
                         * the ones relative to horizontal dimensions).  We can rely on directions
                         * for matching axis since CRS.getEnvelope(crs) should have transformed the
                         * domain to this envelope CRS.
                         */
                        final CoordinateSystem domainCS = domainCRS.getCoordinateSystem();
                        final int domainDimension = domainCS.getDimension();
                        for (int i=0; i<domainDimension; i++) {
                            final AxisDirection direction = domainCS.getAxis(i).getDirection();
                            for (int j=0; j<dimension; j++) {
                                if (direction.equals(cs.getAxis(j).getDirection())) {
                                    ordinates[j]           = domain.getMinimum(i);
                                    ordinates[j+dimension] = domain.getMaximum(i);
                                }
                            }
                        }
                        domain = original;
                    }
                    intersect(domain);
                    if (!changed) {
                        changed = !equals(original, 0, false);
                    }
                }
            }
            ordinates(ordinates);
        }
        return changed;
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
