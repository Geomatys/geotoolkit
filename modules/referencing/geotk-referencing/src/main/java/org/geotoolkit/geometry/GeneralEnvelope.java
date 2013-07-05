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

import java.util.Arrays;
import java.io.Serializable;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;

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
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.spatial.PixelOrientation;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.Cloneable;
import org.geotoolkit.internal.InternalUtilities;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;

import static org.apache.sis.util.ArgumentChecks.*;
import static org.apache.sis.math.MathFunctions.isNegative;
import static org.apache.sis.math.MathFunctions.isSameSign;


/**
 * A minimum bounding box or rectangle. Regardless of dimension, an {@code Envelope} can
 * be represented without ambiguity as two {@linkplain DirectPosition direct positions}
 * (coordinate points). To encode an {@code Envelope}, it is sufficient to encode these
 * two points.
 *
 * {@note <code>Envelope</code> uses an arbitrary <cite>Coordinate Reference System</cite>, which
 * doesn't need to be geographic. This is different than the <code>GeographicBoundingBox</code>
 * class provided in the metadata package, which can be used as a kind of envelope restricted to
 * a Geographic CRS having Greenwich prime meridian.}
 *
 * This particular implementation of {@code Envelope} is said "General" because it
 * uses coordinates of an arbitrary dimension. This is in contrast with {@link Envelope2D},
 * which can use only two-dimensional coordinates.
 * <p>
 * A {@code GeneralEnvelope} can be created in various ways:
 * <p>
 * <ul>
 *   <li>{@linkplain #GeneralEnvelope(int) From a given number of dimension}, with all ordinates initialized to 0.</li>
 *   <li>{@linkplain #GeneralEnvelope(GeneralDirectPosition, GeneralDirectPosition) From two coordinate points}.</li>
 *   <li>{@linkplain #GeneralEnvelope(Envelope) From a an other envelope} (copy constructor).</li>
 *   <li>{@linkplain #GeneralEnvelope(GeographicBoundingBox) From a geographic bounding box}
 *       or a {@linkplain #GeneralEnvelope(Rectangle2D) Java2D rectangle}.</li>
 *   <li>{@linkplain #GeneralEnvelope(GridEnvelope, PixelInCell, MathTransform, CoordinateReferenceSystem)
 *       From a grid envelope} together with a <cite>Grid to CRS</cite> transform.</li>
 *   <li>{@linkplain #GeneralEnvelope(String) From a string} representing a {@code BBOX} in
 *       <cite>Well Known Text</cite> format.</li>
 * </ul>
 *
 * {@section Spanning the anti-meridian of a Geographic CRS}
 * The <cite>Web Coverage Service</cite> (WCS) specification authorizes (with special treatment)
 * cases where <var>upper</var> &lt; <var>lower</var> at least in the longitude case. They are
 * envelopes crossing the anti-meridian, like the red box below (the green box is the usual case).
 * The default implementation of methods listed in the right column can handle such cases.
 *
 * <center><table><tr><td style="white-space:nowrap">
 *   <img src="doc-files/AntiMeridian.png">
 * </td><td style="white-space:nowrap">
 * Supported methods:
 * <ul>
 *   <li>{@link #getMinimum(int)}</li>
 *   <li>{@link #getMaximum(int)}</li>
 *   <li>{@link #getMedian(int)}</li>
 *   <li>{@link #getSpan(int)}</li>
 *   <li>{@link #isEmpty()}</li>
 *   <li>{@link #contains(DirectPosition)}</li>
 *   <li>{@link #contains(Envelope, boolean)}</li>
 *   <li>{@link #intersects(Envelope, boolean)}</li>
 *   <li>{@link #intersect(Envelope)}</li>
 *   <li>{@link #add(Envelope)}</li>
 *   <li>{@link #add(DirectPosition)}</li>
 * </ul>
 * </td></tr></table></center>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Simone Giannecchini (Geosolutions)
 * @author Johann Sorel (Geomatys)
 * @version 3.21
 *
 * @see Envelope2D
 * @see org.geotoolkit.geometry.jts.ReferencedEnvelope
 * @see org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox
 *
 * @since 1.2
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.geometry.GeneralEnvelope}.
 */
@Deprecated
public class GeneralEnvelope extends ArrayEnvelope implements Cloneable, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 1752330560227688940L;

    /**
     * Used for setting the {@link #ordinates} field during a {@link #clone()} operation only.
     * Will be fetch when first needed.
     */
    private static volatile Field ordinatesField;

    /**
     * Constructs an empty envelope of the specified dimension. All ordinates
     * are initialized to 0 and the coordinate reference system is undefined.
     *
     * @param dimension The envelope dimension.
     */
    public GeneralEnvelope(final int dimension) {
        super(dimension);
    }

    /**
     * Constructs one-dimensional envelope defined by a range of values.
     *
     * @param min The lower value.
     * @param max The upper value.
     */
    public GeneralEnvelope(final double min, final double max) {
        super(min, max);
    }

    /**
     * Constructs a envelope defined by two positions.
     *
     * @param  minDP Lower ordinate values.
     * @param  maxDP Upper ordinate values.
     * @throws MismatchedDimensionException if the two positions don't have the same dimension.
     */
    public GeneralEnvelope(final double[] minDP, final double[] maxDP) {
        super(minDP, maxDP);
    }

    /**
     * Constructs a envelope defined by two positions. The coordinate
     * reference system is inferred from the supplied direct position.
     *
     * @param  lower Point containing the starting ordinate values.
     * @param  upper Point containing the ending ordinate values.
     * @throws MismatchedDimensionException if the two positions don't have the same dimension.
     * @throws MismatchedReferenceSystemException if the two positions don't use the same CRS.
     *
     * @see Envelope2D#Envelope2D(DirectPosition, DirectPosition)
     */
    public GeneralEnvelope(final GeneralDirectPosition lower, final GeneralDirectPosition upper)
            throws MismatchedReferenceSystemException
    {
//  Uncomment next lines if Sun fixes RFE #4093999
//      ensureNonNull("lower", lower);
//      ensureNonNull("upper", upper);
        super(lower.ordinates, upper.ordinates);
        crs = getCoordinateReferenceSystem(lower, upper);
        checkCoordinateReferenceSystemDimension(crs, ordinates.length >>> 1);
    }

    /**
     * Constructs an empty envelope with the specified coordinate reference system.
     * All ordinates are initialized to 0.
     *
     * @param crs The coordinate reference system.
     *
     * @since 2.2
     */
    public GeneralEnvelope(final CoordinateReferenceSystem crs) {
//  Uncomment next line if Sun fixes RFE #4093999
//      ensureNonNull("crs", crs);
        super(crs.getCoordinateSystem().getDimension());
        this.crs = crs;
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
        super(box);
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
        super(rect);
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
        final ArrayEnvelope transformed;
        try {
            transformed = Envelopes.transform(gridToCRS, this);
        } catch (TransformException exception) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_TRANSFORM_FOR_TYPE_1,
                    gridToCRS.getClass()), exception);
        }
        assert transformed.ordinates.length == this.ordinates.length;
        System.arraycopy(transformed.ordinates, 0, this.ordinates, 0, ordinates.length);
        setCoordinateReferenceSystem(crs);
    }

    /**
     * Constructs a new envelope initialized to the values parsed from the given string in
     * <cite>Well Known Text</cite> (WKT) format. The given string is typically a {@code BOX}
     * element like below:
     *
     * {@preformat wkt
     *     BOX(-180 -90, 180 90)
     * }
     *
     * However this constructor is lenient to other geometry types like {@code POLYGON}.
     * Actually this constructor ignores the geometry type and just applies the following
     * simple rules:
     * <p>
     * <ul>
     *   <li>Character sequences complying to the rules of Java identifiers are skipped.</li>
     *   <li>Coordinates are separated by a coma ({@code ,}) character.</li>
     *   <li>The ordinates in a coordinate are separated by a space.</li>
     *   <li>Ordinate numbers are assumed formatted in US locale.</li>
     *   <li>The coordinate having the highest dimension determines the dimension of this envelope.</li>
     * </ul>
     * <p>
     * This constructor does not check the consistency of the provided WKT. For example it doesn't
     * check that every points in a {@code LINESTRING} have the same dimension. However this
     * constructor ensures that the parenthesis are balanced, in order to catch some malformed WKT.
     * <p>
     * The following examples can be parsed by this constructor in addition of the standard
     * {@code BOX} element. This constructor creates the bounding box of those geometries:
     * <p>
     * <ul>
     *   <li>{@code POINT(6 10)}</li>
     *   <li>{@code MULTIPOLYGON(((1 1, 5 1, 1 5, 1 1),(2 2, 3 2, 3 3, 2 2)))}</li>
     *   <li>{@code GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(3 8,7 10))}</li>
     * </ul>
     *
     * @param  wkt The {@code BOX}, {@code POLYGON} or other kind of element to parse.
     * @throws NumberFormatException If a number can not be parsed.
     * @throws IllegalArgumentException If the parenthesis are not balanced.
     *
     * @see Envelopes#parseWKT(String)
     * @see Envelopes#toWKT(Envelope)
     *
     * @since 3.09
     */
    public GeneralEnvelope(final String wkt) throws NumberFormatException, IllegalArgumentException {
        super(wkt);
    }

    /**
     * Returns the given envelope as a {@code GeneralEnvelope} instance. If the given envelope
     * is already an instance of {@code GeneralEnvelope}, then it is returned unchanged.
     * Otherwise the coordinate values and the CRS of the given envelope are
     * {@linkplain #GeneralEnvelope(Envelope) copied} in a new {@code GeneralEnvelope}.
     *
     * @param  envelope The envelope to cast, or {@code null}.
     * @return The values of the given envelope as a {@code GeneralEnvelope} instance.
     *
     * @see AbstractEnvelope#castOrCopy(Envelope)
     *
     * @since 3.19
     */
    public static GeneralEnvelope castOrCopy(final Envelope envelope) {
        if (envelope == null || envelope instanceof GeneralEnvelope) {
            return (GeneralEnvelope) envelope;
        }
        return new GeneralEnvelope(envelope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getDimension() {
        return super.getDimension();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return super.getCoordinateReferenceSystem();
    }

    /**
     * Sets the coordinate reference system in which the coordinate are given.
     * This method <strong>does not</strong> reproject the envelope, and do not
     * check if the envelope is contained in the new domain of validity. The
     * later can be enforced by a call to {@link #reduceToDomain(boolean)}.
     * <p>
     * If the envelope coordinates need to be transformed to the new CRS, consider
     * using {@link Envelopes#transform(Envelope, CoordinateReferenceSystem)} instead.
     *
     * @param  crs The new coordinate reference system, or {@code null}.
     * @throws MismatchedDimensionException if the specified CRS doesn't have the expected
     *         number of dimensions.
     */
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs)
            throws MismatchedDimensionException
    {
        checkCoordinateReferenceSystemDimension(crs, getDimension());
        this.crs = crs;
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
        boolean changed = false;
        if (crs != null) {
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
                    final GeneralEnvelope original = new GeneralEnvelope(this);
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
        }
        return changed;
    }

    // Note: As of JDK 1.6.0_31, using {@linkplain #getLower(int)} in the first line crash the
    // Javadoc tools, maybe because getLower/getUpper are defined in a non-public parent class.
    /**
     * Ensures that <var>lower</var> &lt;= <var>upper</var> for every dimensions.
     * If a {@linkplain #getUpper(int) upper ordinate value} is less than a
     * {@linkplain #getLower(int) lower ordinate value}, then there is a choice:
     * <p>
     * <ul>
     *   <li>If the axis has {@link RangeMeaning#WRAPAROUND}, then the lower ordinate value is
     *       set to the {@linkplain CoordinateSystemAxis#getMinimumValue() axis minimum value}
     *       and the upper ordinate value is set to the
     *       {@linkplain CoordinateSystemAxis#getMaximumValue() axis maimum value}.</li>
     *   <li>Otherwise an {@link IllegalStateException} is thrown.</li>
     * </ul>
     * <p>
     * This method is useful when the envelope needs to be used with library that doesn't support
     * envelopes spanning the anti-meridian.
     *
     * @return {@code true} if this envelope has been modified as a result of this method call,
     *         or {@code false} if no change was done.
     * @throws IllegalStateException If a upper ordinate value is less than a lower ordinate
     *         value on an axis which doesn't have the {@code WRAPAROUND} range meaning.
     *
     * @since 3.20
     */
    public boolean reorderCorners() throws IllegalStateException {
        boolean changed = false;
        final int dimension = ordinates.length >>> 1;
        for (int i=0; i<dimension; i++) {
            final int j = i+dimension;
            if (isNegative(ordinates[j] - ordinates[i])) {
                final CoordinateSystemAxis axis = getAxis(crs, i);
                if (axis != null && RangeMeaning.WRAPAROUND.equals(axis.getRangeMeaning())) {
                    ordinates[i] = axis.getMinimumValue();
                    ordinates[j] = axis.getMaximumValue();
                    changed = true;
                } else {
                    throw new IllegalStateException(Errors.format(Errors.Keys.MALFORMED_ENVELOPE));
                }
            }
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
        for (int i=0; i<ordinates.length; i++) {
            ordinates[i] = InternalUtilities.adjustForRoundingError(ordinates[i], factor, maxULP);
        }
    }

    /**
     * Sets the envelope range along the specified dimension.
     *
     * @param  dimension The dimension to set.
     * @param  minimum   The minimum value along the specified dimension.
     * @param  maximum   The maximum value along the specified dimension.
     * @throws IndexOutOfBoundsException If the given index is out of bounds.
     */
    public void setRange(final int dimension, final double minimum, final double maximum)
            throws IndexOutOfBoundsException
    {
        final int d = ordinates.length >>> 1;
        ensureValidIndex(d, dimension);
        ordinates[dimension + d] = maximum;
        ordinates[dimension]     = minimum;
    }

    /**
     * Sets the envelope to the specified values, which must be the lower corner coordinates
     * followed by upper corner coordinates. The number of arguments provided shall be twice
     * this {@linkplain #getDimension envelope dimension}, and minimum shall not be greater
     * than maximum.
     * <p>
     * <b>Example:</b>
     * (<var>x</var><sub>min</sub>, <var>y</var><sub>min</sub>, <var>z</var><sub>min</sub>,
     *  <var>x</var><sub>max</sub>, <var>y</var><sub>max</sub>, <var>z</var><sub>max</sub>)
     *
     * @param ordinates The new ordinate values.
     *
     * @since 2.5
     */
    public void setEnvelope(final double... ordinates) {
        if ((ordinates.length & 1) != 0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ODD_ARRAY_LENGTH_1, ordinates.length));
        }
        final int dimension  = ordinates.length >>> 1;
        final int check = this.ordinates.length >>> 1;
        if (dimension != check) {
            throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_3, "ordinates", dimension, check));
        }
        System.arraycopy(ordinates, 0, this.ordinates, 0, ordinates.length);
    }

    /**
     * Sets this envelope to the same coordinate values than the specified envelope.
     * If the given envelope has a non-null Coordinate Reference System (CRS), then
     * the CRS of this envelope will be set to the CRS of the given envelope.
     *
     * @param  envelope The envelope to copy coordinates from.
     * @throws MismatchedDimensionException if the specified envelope doesn't have the expected
     *         number of dimensions.
     *
     * @since 2.2
     */
    public void setEnvelope(final Envelope envelope) throws MismatchedDimensionException {
        ensureNonNull("envelope", envelope);
        final int dimension = ordinates.length >>> 1;
        ensureDimensionMatch("envelope", envelope.getDimension(), dimension);
        if (envelope instanceof ArrayEnvelope) {
            System.arraycopy(((ArrayEnvelope) envelope).ordinates, 0, ordinates, 0, ordinates.length);
        } else {
            final DirectPosition lower = envelope.getLowerCorner();
            final DirectPosition upper = envelope.getUpperCorner();
            for (int i=0; i<dimension; i++) {
                ordinates[i]           = lower.getOrdinate(i);
                ordinates[i+dimension] = upper.getOrdinate(i);
            }
        }
        final CoordinateReferenceSystem envelopeCRS = envelope.getCoordinateReferenceSystem();
        if (envelopeCRS != null) {
            crs = envelopeCRS;
            assert crs.getCoordinateSystem().getDimension() == getDimension() : crs;
            assert envelope.getClass() != getClass() || equals(envelope) : envelope;
        }
    }

    /**
     * Sets a sub-domain of this envelope to the same coordinate values than the specified envelope.
     * This method copies the ordinate values of all dimensions from the given envelope to some
     * dimensions of this envelope. The target dimensions in this envelope range from {@code offset}
     * inclusive to <code>lower + {@linkplain Envelope#getDimension()}</code> exclusive.
     * <p>
     * This method ignores the Coordinate Reference System of {@code this} and the given envelope.
     *
     * @param  envelope The envelope to copy coordinates from.
     * @param  offset Index of the first dimension to write in this envelope.
     * @throws IndexOutOfBoundsException If the given offset is negative, or is greater than
     *         <code>getDimension() - envelope.getDimension()</code>.
     *
     * @since 3.16
     */
    public void setSubEnvelope(final Envelope envelope, int offset) throws IndexOutOfBoundsException {
        ensureNonNull("envelope", envelope);
        final int subDim = envelope.getDimension();
        final int dimension = ordinates.length >>> 1;
        if (offset < 0 || offset + subDim > dimension) {
            throw new IndexOutOfBoundsException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_2, "lower", offset));
        }
        final DirectPosition lower = envelope.getLowerCorner();
        final DirectPosition upper = envelope.getUpperCorner();
        for (int i=0; i<subDim; i++) {
            ordinates[offset]           = lower.getOrdinate(i);
            ordinates[offset+dimension] = upper.getOrdinate(i);
            offset++;
        }
    }

    /**
     * Returns an envelope that encompass only some dimensions of this envelope.
     * This method performs the following choice:
     *
     * <ul>
     *   <li><p>If the given {@code lower} and {@code upper} arguments are equal to 0 and this
     *     {@linkplain #getDimension() envelope dimension} respectively, then this method returns
     *     {@code this}. Note that in such case, the {@linkplain #getCoordinateReferenceSystem() CRS}
     *     (if non-null) is still valid.</p></li>
     *   <li><p>Otherwise, this method copies the ordinate values from this envelope into a new
     *     envelope, beginning at dimension {@code lower} and extending to dimension {@code upper-1}.
     *     The {@linkplain #getCoordinateReferenceSystem() CRS} of the new envelope is initialized
     *     to {@code null}. This method does not compute a new CRS because it may not be needed,
     *     or the new CRS may be already known by the caller.</p></li>
     * </ul>
     *
     * @param  lower The first dimension to copy, inclusive.
     * @param  upper The last  dimension to copy, exclusive.
     * @return The sub-envelope of dimension {@code upper-lower}, which may be {@code this}.
     * @throws IndexOutOfBoundsException if an index is out of bounds.
     */
    public GeneralEnvelope getSubEnvelope(final int lower, final int upper)
            throws IndexOutOfBoundsException
    {
        final int curDim = ordinates.length >>> 1;
        final int newDim = upper - lower;
        if (lower<0 || lower>curDim) {
            throw new IndexOutOfBoundsException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_2, "lower", lower));
        }
        if (newDim<0 || upper>curDim) {
            throw new IndexOutOfBoundsException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_2, "upper", upper));
        }
        if (newDim == curDim) {
            return this;
        }
        final GeneralEnvelope envelope = new GeneralEnvelope(newDim);
        System.arraycopy(ordinates, lower,        envelope.ordinates, 0,      newDim);
        System.arraycopy(ordinates, lower+curDim, envelope.ordinates, newDim, newDim);
        return envelope;
    }

    /**
     * Sets the lower corner to {@linkplain Double#NEGATIVE_INFINITY negative infinity}
     * and the upper corner to {@linkplain Double#POSITIVE_INFINITY positive infinity}.
     * The {@linkplain #getCoordinateReferenceSystem coordinate reference system} (if any)
     * stay unchanged.
     *
     * @since 2.2
     */
    public void setToInfinite() {
        final int mid = ordinates.length >>> 1;
        Arrays.fill(ordinates, 0,   mid,              Double.NEGATIVE_INFINITY);
        Arrays.fill(ordinates, mid, ordinates.length, Double.POSITIVE_INFINITY);
        assert isInfinite() : this;
    }

    /**
     * Returns {@code true} if at least one ordinate has an
     * {@linkplain Double#isInfinite infinite} value.
     *
     * @return {@code true} if this envelope has infinite value.
     *
     * @since 2.2
     */
    public boolean isInfinite() {
        for (int i=0; i<ordinates.length; i++) {
            if (Double.isInfinite(ordinates[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets all ordinate values to {@linkplain Double#NaN NaN}. The
     * {@linkplain #getCoordinateReferenceSystem coordinate reference system} (if any) stay
     * unchanged.
     *
     * @see #isAllNaN()
     *
     * @since 2.2
     */
    public void setToNull() {
        Arrays.fill(ordinates, Double.NaN);
        assert isAllNaN() : this;
    }

    /**
     * Adds to this envelope a point of the given array.
     * This method does not check for anti-meridian spanning. It is invoked only
     * by the {@link Envelopes} transform methods, which build "normal" envelopes.
     *
     * @param  array The array which contains the ordinate values.
     * @param  offset Index of the first valid ordinate value in the given array.
     */
    final void add(final double[] array, final int offset) {
        final int dim = ordinates.length >>> 1;
        for (int i=0; i<dim; i++) {
            final double value = array[offset + i];
            if (value < ordinates[i    ]) ordinates[i    ] = value;
            if (value > ordinates[i+dim]) ordinates[i+dim] = value;
        }
    }

    /**
     * Adds a point to this envelope. The resulting envelope is the smallest envelope that
     * contains both the original envelope and the specified point.
     * <p>
     * After adding a point, a call to {@link #contains(DirectPosition) contains(DirectPosition)}
     * with the added point as an argument will return {@code true}, except if one of the point
     * ordinates was {@link Double#NaN} in which case the corresponding ordinate has been ignored.
     *
     * {@note This method assumes that the specified point uses the same CRS than this envelope.
     *        For performance raisons, it will no be verified unless Java assertions are enabled.}
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * This method supports envelopes spanning the anti-meridian. In such cases it is possible to
     * move both envelope borders in order to encompass the given point, as illustrated below (the
     * new point is represented by the {@code +} symbol):
     *
     * {@preformat text
     *    ─────┐   + ┌─────
     *    ─────┘     └─────
     * }
     *
     * The default implementation moves only the border which is closest to the given point.
     *
     * @param  position The point to add.
     * @throws MismatchedDimensionException if the specified point doesn't have
     *         the expected dimension.
     * @throws AssertionError If assertions are enabled and the envelopes have mismatched CRS.
     */
    public void add(final DirectPosition position) throws MismatchedDimensionException, AssertionError {
        ensureNonNull("position", position);
        final int dim = ordinates.length >>> 1;
        ensureDimensionMatch("position", position.getDimension(), dim);
        assert equalsIgnoreMetadata(crs, position.getCoordinateReferenceSystem(), true) : position;
        for (int i=0; i<dim; i++) {
            final double value = position.getOrdinate(i);
            final double min = ordinates[i];
            final double max = ordinates[i+dim];
            if (!isNegative(max - min)) { // Standard case, or NaN.
                if (value < min) ordinates[i    ] = value;
                if (value > max) ordinates[i+dim] = value;
            } else {
                /*
                 * Spanning the anti-meridian. The [max…min] range (not that min/max are
                 * interchanged) is actually an exclusion area. Changes only the closest
                 * side.
                 */
                addToClosest(i, value, max, min);
            }
        }
        assert contains(position) || isEmpty() || hasNaN(position) : position;
    }

    /**
     * Invoked when a point is added to a range spanning the anti-meridian.
     * In the example below, the new point is represented by the {@code +}
     * symbol. The point is added only on the closest side.
     *
     * {@preformat text
     *    ─────┐   + ┌─────
     *    ─────┘     └─────
     * }
     *
     * @param  i     The dimension of the ordinate
     * @param  value The ordinate value to add to this envelope.
     * @param  left  The border on the left side,  which is the <em>max</em> value (yes, this is confusing!)
     * @param  right The border on the right side, which is the <em>min</em> value (yes, this is confusing!)
     *
     * @since 3.20
     */
    private void addToClosest(int i, final double value, double left, double right) {
        left = value - left;
        if (left > 0) {
            right -= value;
            if (right > 0) {
                if (right > left) {
                    i += (ordinates.length >>> 1);
                }
                ordinates[i] = value;
            }
        }
    }

    /**
     * Adds an envelope object to this envelope. The resulting envelope is the union of the
     * two {@code Envelope} objects.
     *
     * {@note This method assumes that the specified envelope uses the same CRS than this envelope.
     *        For performance raisons, it will no be verified unless Java assertions are enabled.}
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * This method supports envelopes spanning the anti-meridian. If one or both envelopes span
     * the anti-meridian, then the result of the {@code add} operation may be an envelope expanding
     * to infinities. In such case, the ordinate range will be either [-&infin;&hellip;&infin;] or
     * [0&hellip;-0] depending on whatever the original range span the anti-meridian or not.
     *
     * @param  envelope the {@code Envelope} to add to this envelope.
     * @throws MismatchedDimensionException if the specified envelope doesn't
     *         have the expected dimension.
     * @throws AssertionError If assertions are enabled and the envelopes have mismatched CRS.
     */
    public void add(final Envelope envelope) throws MismatchedDimensionException, AssertionError {
        ensureNonNull("envelope", envelope);
        final int dim = ordinates.length >>> 1;
        ensureDimensionMatch("envelope", envelope.getDimension(), dim);
        assert equalsIgnoreMetadata(crs, envelope.getCoordinateReferenceSystem(), true) : envelope;
        final DirectPosition lower = envelope.getLowerCorner();
        final DirectPosition upper = envelope.getUpperCorner();
        for (int i=0; i<dim; i++) {
            final double min0 = ordinates[i];
            final double max0 = ordinates[i+dim];
            final double min1 = lower.getOrdinate(i);
            final double max1 = upper.getOrdinate(i);
            final boolean sp0 = isNegative(max0 - min0);
            final boolean sp1 = isNegative(max1 - min1);
            if (sp0 == sp1) {
                /*
                 * Standard case (for rows in the above pictures), or case where both envelopes
                 * span the anti-meridian (which is almost the same with an additional post-add
                 * check).
                 *    ┌──────────┐          ┌──────────┐
                 *    │  ┌────┐  │    or    │  ┌───────┼──┐
                 *    │  └────┘  │          │  └───────┼──┘
                 *    └──────────┘          └──────────┘
                 *
                 *    ────┐  ┌────          ────┐  ┌────
                 *    ──┐ │  │ ┌──    or    ────┼──┼─┐┌─
                 *    ──┘ │  │ └──          ────┼──┼─┘└─
                 *    ────┘  └────          ────┘  └────
                 */
                if (min1 < min0) ordinates[i    ] = min1;
                if (max1 > max0) ordinates[i+dim] = max1;
                if (!sp0 || isNegativeUnsafe(ordinates[i+dim] - ordinates[i])) {
                    continue; // We are done, go to the next dimension.
                }
                // If we were spanning the anti-meridian before the union but
                // are not anymore after the union, we actually merged to two
                // sides, so the envelope is spanning to infinities. The code
                // close to the end of this loop will set an infinite range.
            } else if (sp0) {
                /*
                 * Only this envelope spans the anti-meridian; the given envelope is normal or
                 * has NaN values.  First we need to exclude the cases were the given envelope
                 * is fully included in this envelope:
                 *   ──────────┐  ┌─────
                 *     ┌────┐  │  │
                 *     └────┘  │  │
                 *   ──────────┘  └─────
                 */
                if (max1 <= max0) continue;  // This is the case of above picture.
                if (min1 >= min0) continue;  // Like above picture, but on the right side.
                /*
                 * At this point, the given envelope partially overlaps the "exclusion area"
                 * of this envelope or has NaN values. We will move at most one edge of this
                 * envelope, in order to leave as much free space as possible.
                 *    ─────┐      ┌─────
                 *       ┌─┼────┐ │
                 *       └─┼────┘ │
                 *    ─────┘      └─────
                 */
                final double left  = min1 - max0;
                final double right = min0 - max1;
                if (left > 0 || right > 0) {
                    // The < and > checks below are not completly redundant.
                    // The difference is when a value is NaN.
                    if (left > right) ordinates[i    ] = min1;
                    if (right > left) ordinates[i+dim] = max1; // This is the case illustrated above.
                    continue; // We are done, go to the next dimension.
                }
                // If we reach this point, the given envelope fills completly the "exclusion area"
                // of this envelope. As a consequence this envelope is now spanning to infinities.
                // We will set that fact close to the end of this loop.
            } else {
                /*
                 * Opposite of above case: this envelope is "normal" or has NaN values, and the
                 * given envelope spans to infinities.
                 */
                if (max0 <= max1 || min0 >= min1) {
                    ordinates[i]     = min1;
                    ordinates[i+dim] = max1;
                    continue;
                }
                final double left  = min0 - max1;
                final double right = min1 - max0;
                if (left > 0 || right > 0) {
                    if (left > right) ordinates[i+dim] = max1;
                    if (right > left) ordinates[i    ] = min1;
                    continue;
                }
            }
            /*
             * If we reach that point, we went in one of the many cases where the envelope
             * has been expanded to infinity.  Declares an infinite range while preserving
             * the "normal" / "anti-meridian spanning" state.
             */
            if (sp0) {
                ordinates[i    ] = +0.0;
                ordinates[i+dim] = -0.0;
            } else {
                ordinates[i    ] = Double.NEGATIVE_INFINITY;
                ordinates[i+dim] = Double.POSITIVE_INFINITY;
            }
        }
        assert contains(envelope, true) || isEmpty() || hasNaN(envelope) : this;
    }

    /**
     * Sets this envelope to the intersection if this envelope with the specified one.
     *
     * {@note This method assumes that the specified envelope uses the same CRS than this envelope.
     *        For performance raisons, it will no be verified unless Java assertions are enabled.}
     *
     * {@section Spanning the anti-meridian of a Geographic CRS}
     * This method supports envelopes spanning the anti-meridian.
     *
     * @param  envelope the {@code Envelope} to intersect to this envelope.
     * @throws MismatchedDimensionException if the specified envelope doesn't
     *         have the expected dimension.
     * @throws AssertionError If assertions are enabled and the envelopes have mismatched CRS.
     */
    public void intersect(final Envelope envelope) throws MismatchedDimensionException, AssertionError {
        ensureNonNull("envelope", envelope);
        final int dim = ordinates.length >>> 1;
        ensureDimensionMatch("envelope", envelope.getDimension(), dim);
        assert equalsIgnoreMetadata(crs, envelope.getCoordinateReferenceSystem(), true) : envelope;
        final DirectPosition lower = envelope.getLowerCorner();
        final DirectPosition upper = envelope.getUpperCorner();
        for (int i=0; i<dim; i++) {
            final double min0  = ordinates[i];
            final double max0  = ordinates[i+dim];
            final double min1  = lower.getOrdinate(i);
            final double max1  = upper.getOrdinate(i);
            final double span0 = max0 - min0;
            final double span1 = max1 - min1;
            if (isSameSign(span0, span1)) { // Always 'false' if any value is NaN.
                /*
                 * First, verify that the two envelopes intersect.
                 *     ┌──────────┐             ┌─────────────┐
                 *     │  ┌───────┼──┐    or    │  ┌───────┐  │
                 *     │  └───────┼──┘          │  └───────┘  │
                 *     └──────────┘             └─────────────┘
                 */
                if ((min1 > max0 || max1 < min0) && !isNegativeUnsafe(span0)) {
                    /*
                     * The check for !isNegative(span0) is because if both envelopes span the
                     * anti-merdian, then there is always an intersection on both side no matter
                     * what envelope ordinates are because both envelopes extend toward infinities:
                     *     ────┐  ┌────            ────┐  ┌────
                     *     ──┐ │  │ ┌──     or     ────┼──┼─┐┌─
                     *     ──┘ │  │ └──            ────┼──┼─┘└─
                     *     ────┘  └────            ────┘  └────
                     * Since we excluded the above case, entering in this block means that the
                     * envelopes are "normal" and do not intersect, so we set ordinates to NaN.
                     *   ┌────┐
                     *   │    │     ┌────┐
                     *   │    │     └────┘
                     *   └────┘
                     */
                    ordinates[i] = ordinates[i+dim] = Double.NaN;
                    continue;
                }
            } else {
                int intersect = 0; // A bitmask of intersections (two bits).
                if (!Double.isNaN(span0) && !Double.isNaN(span1)) {
                    if (isNegativeUnsafe(span0)) {
                        /*
                         * The first line below checks for the case illustrated below. The second
                         * line does the same check, but with the small rectangle on the right side.
                         *    ─────┐      ┌─────              ──────────┐  ┌─────
                         *       ┌─┼────┐ │           or        ┌────┐  │  │
                         *       └─┼────┘ │                     └────┘  │  │
                         *    ─────┘      └─────              ──────────┘  └─────
                         */
                        if (min1 <= max0) {intersect  = 1; ordinates[i    ] = min1;}
                        if (max1 >= min0) {intersect |= 2; ordinates[i+dim] = max1;}
                    } else {
                        // Same than above, but with indices 0 and 1 interchanged.
                        // No need to set ordinate values since they would be the same.
                        if (min0 <= max1) {intersect  = 1;}
                        if (max0 >= min1) {intersect |= 2;}
                    }
                }
                /*
                 * Cases 0 and 3 are illustrated below. In case 1 and 2, we will set
                 * only the ordinate value which has not been set by the above code.
                 *
                 *                [intersect=0]          [intersect=3]
                 *              ─────┐     ┌─────      ─────┐     ┌─────
                 *  negative:    max0│ ┌─┐ │min0          ┌─┼─────┼─┐
                 *                   │ └─┘ │              └─┼─────┼─┘
                 *              ─────┘     └─────      ─────┘     └─────
                 *
                 *               max1  ┌─┐  min1          ┌─────────┐
                 * positive:    ─────┐ │ │ ┌─────      ───┼─┐     ┌─┼───
                 *              ─────┘ │ │ └─────      ───┼─┘     └─┼───
                 *                     └─┘                └─────────┘
                 */
                switch (intersect) {
                    default: throw new AssertionError(intersect);
                    case 1: if (max1 < max0) ordinates[i+dim] = max1; break;
                    case 2: if (min1 > min0) ordinates[i    ] = min1; break;
                    case 3: // Fall through
                    case 0: {
                        // Before to declare the intersection as invalid, verify if the envelope
                        // actually span the whole Earth. In such case, the intersection is a no-
                        // operation (or a copy operation).
                        final double min, max;
                        final double csSpan = getSpan(getAxis(crs, i));
                        if (span1 >= csSpan) {
                            min = min0;
                            max = max0;
                        } else if (span0 >= csSpan) {
                            min = min1;
                            max = max1;
                        } else {
                            min = Double.NaN;
                            max = Double.NaN;
                        }
                        ordinates[i]     = min;
                        ordinates[i+dim] = max;
                        break;
                    }
                }
                continue;
            }
            if (min1 > min0) ordinates[i    ] = min1;
            if (max1 < max0) ordinates[i+dim] = max1;
        }
        // Tests only if the interection result is non-empty.
        assert isEmpty() || AbstractEnvelope.castOrCopy(envelope).contains(this, true) : this;
    }

    /**
     * Returns a deep copy of this envelope.
     *
     * @return A clone of this envelope.
     */
    @Override
    public GeneralEnvelope clone() {
        try {
            Field field = ordinatesField;
            if (field == null) {
                field = ArrayEnvelope.class.getDeclaredField("ordinates");
                field.setAccessible(true);
                ordinatesField = field;
            }
            GeneralEnvelope e = (GeneralEnvelope) super.clone();
            field.set(e, ordinates.clone());
            return e;
        } catch (CloneNotSupportedException | ReflectiveOperationException exception) {
            // Should not happen, since we are cloneable, the
            // field is known to exist and we made it accessible.
            throw new AssertionError(exception);
        }
    }
}
