/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2011, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import org.opengis.util.FactoryException;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.cs.RangeMeaning;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.CoordinateOperationFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.UnsupportedImplementationException;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;
import org.geotoolkit.resources.Errors;

import static org.geotoolkit.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.util.Strings.trimFractionalPart;


/**
 * Utility methods for envelopes. This utility class is made up of static functions working
 * with arbitrary implementations of GeoAPI interfaces.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @author Andrea Aime (TOPP)
 * @author Johann Sorel (Geomatys)
 * @version 3.19
 *
 * @see CRS
 *
 * @since 3.19 (derived from 2.4)
 * @module
 */
public final class Envelopes extends Static {
    /**
     * Enumeration of the 4 corners in an envelope, with repetition of the first point.
     * The values are (x,y) pairs with {@code false} meaning "minimal value" and {@code true}
     * meaning "maximal value". This is used by {@link #toPolygonWKT(Envelope)} only.
     */
    private static final boolean[] CORNERS = {
        false, false,
        false, true,
        true,  true,
        true,  false,
        false, false
    };

    /**
     * Do not allow instantiation of this class.
     */
    private Envelopes() {
    }

    /**
     * Returns the domain of validity for the specified coordinate reference system,
     * or {@code null} if unknown. The returned envelope is expressed in terms of the
     * specified CRS.
     * <p>
     * This method performs the work documented in the
     * {@link CRS#getEnvelope(CoordinateReferenceSystem)} method.
     * It is defined in this class for convenience.
     *
     * @param  crs The coordinate reference system, or {@code null}.
     * @return The envelope in terms of the specified CRS, or {@code null} if none.
     *
     * @see CRS#getEnvelope(CoordinateReferenceSystem)
     * @see org.geotoolkit.geometry.GeneralEnvelope#reduceToDomain(boolean)
     */
    public static Envelope getDomainOfValidity(final CoordinateReferenceSystem crs) {
        return CRS.getEnvelope(crs);
    }

    /**
     * Transforms the given envelope to the specified CRS. If the given envelope is null, or the
     * {@linkplain Envelope#getCoordinateReferenceSystem envelope CRS} is null, or the given
     * target CRS is null, or the transform {@linkplain MathTransform#isIdentity is identity},
     * then the envelope is returned unchanged. Otherwise a new transformed envelope is returned.
     *
     * {@section Performance tip}
     * If there is many envelopes to transform with the same source and target CRS, then it
     * is more efficient to get the {@linkplain CoordinateOperation coordinate operation} or
     * {@linkplain MathTransform math transform} once for ever and invoke one of the methods
     * below.
     *
     * @param  envelope The envelope to transform (may be {@code null}).
     * @param  targetCRS The target CRS (may be {@code null}).
     * @return A new transformed envelope, or directly {@code envelope}
     *         if no transformation was required.
     * @throws TransformException If a transformation was required and failed.
     */
    public static Envelope transform(Envelope envelope, final CoordinateReferenceSystem targetCRS)
            throws TransformException
    {
        if (envelope != null && targetCRS != null) {
            final CoordinateReferenceSystem sourceCRS = envelope.getCoordinateReferenceSystem();
            if (sourceCRS != null) {
                if (!CRS.equalsIgnoreMetadata(sourceCRS, targetCRS)) {
                    final CoordinateOperationFactory factory = CRS.getCoordinateOperationFactory(true);
                    final CoordinateOperation operation;
                    try {
                        operation = factory.createOperation(sourceCRS, targetCRS);
                    } catch (FactoryException exception) {
                        throw new TransformException(Errors.format(
                                Errors.Keys.CANT_TRANSFORM_ENVELOPE), exception);
                    }
                    if (!operation.getMathTransform().isIdentity()) {
                        envelope = transform(operation, envelope);
                    }
                }
                assert CRS.equalsIgnoreMetadata(envelope.getCoordinateReferenceSystem(), targetCRS);
            }
        }
        return envelope;
    }

    /**
     * Transforms an envelope using the given {@linkplain MathTransform math transform}.
     * The transformation is only approximative: the returned envelope may be bigger than
     * necessary, or smaller than required if the bounding box contains a pole.
     * <p>
     * This method can not handle the case where the envelope contains the North or South pole,
     * or when it cross the &plusmn;180&deg; longitude, because {@linkplain MathTransform math
     * transforms} does not carry sufficient informations. For a more robust envelope
     * transformation, use {@link #transform(CoordinateOperation, Envelope)} instead.
     *
     * @param  transform The transform to use.
     * @param  envelope Envelope to transform, or {@code null}. This envelope will not be modified.
     * @return The transformed envelope, or {@code null} if {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @see #transform(CoordinateOperation, Envelope)
     */
    public static GeneralEnvelope transform(final MathTransform transform, final Envelope envelope)
            throws TransformException
    {
        ensureNonNull("transform", transform);
        return transform(transform, envelope, null);
    }

    /**
     * Implementation of {@link #transform(MathTransform, Envelope)} with the opportunity to
     * save the projected center coordinate. If {@code targetPt} is non-null, then this method
     * will set it to the center of the source envelope projected to the target CRS.
     */
    private static GeneralEnvelope transform(final MathTransform   transform,
                                             final Envelope        envelope,
                                             GeneralDirectPosition targetPt)
            throws TransformException
    {
        if (envelope == null) {
            return null;
        }
        if (transform.isIdentity()) {
            /*
             * Slight optimization: Just copy the envelope. Note that we need to set the CRS
             * to null because we don't know what the target CRS was supposed to be. Even if
             * an identity transform often imply that the target CRS is the same one than the
             * source CRS, it is not always the case. The metadata may be differents, or the
             * transform may be a datum shift without Bursa-Wolf parameters, etc.
             */
            final GeneralEnvelope e = new GeneralEnvelope(envelope);
            e.setCoordinateReferenceSystem(null);
            if (targetPt != null) {
                for (int i=envelope.getDimension(); --i>=0;) {
                    targetPt.setOrdinate(i, e.getMedian(i));
                }
            }
            return e;
        }
        /*
         * Checks argument validity: envelope and math transform dimensions must be consistent.
         */
        final int sourceDim = transform.getSourceDimensions();
        if (envelope.getDimension() != sourceDim) {
            throw new MismatchedDimensionException(Errors.format(Errors.Keys.MISMATCHED_DIMENSION_$2,
                      sourceDim, envelope.getDimension()));
        }
        int coordinateNumber = 0;
        GeneralEnvelope transformed = null;
        if (targetPt == null) {
            targetPt = new GeneralDirectPosition(transform.getTargetDimensions());
        }
        /*
         * Before to run the loops, we must initialize the coordinates to the minimal values.
         * This coordinates will be updated in the 'switch' statement inside the 'while' loop.
         */
        final GeneralDirectPosition sourcePt = new GeneralDirectPosition(sourceDim);
        for (int i=sourceDim; --i>=0;) {
            sourcePt.setOrdinate(i, envelope.getMinimum(i));
        }
  loop: while (true) {
            /*
             * Transform a point and add the transformed point to the destination envelope.
             * Note that the very last point to be projected must be the envelope center.
             */
            if (targetPt != transform.transform(sourcePt, targetPt)) {
                throw new UnsupportedImplementationException(transform.getClass());
            }
            if (transformed != null) {
                transformed.add(targetPt);
            } else {
                transformed = new GeneralEnvelope(targetPt, targetPt);
            }
            /*
             * Get the next point's coordinates.  The 'coordinateNumber' variable should
             * be seen as a number in base 3 where the number of digits is equal to the
             * number of dimensions. For example, a 4-D space would have numbers ranging
             * from "0000" to "2222" (numbers in base 3). The digits are then translated
             * into minimal, central or maximal ordinates. The outer loop stops when the
             * counter roll back to "0000".  Note that 'targetPt' must keep the value of
             * the last projected point, which must be the envelope center identified by
             * "2222" in the 4-D case.
             */
            int n = ++coordinateNumber;
            for (int i=sourceDim; --i>=0;) {
                switch (n % 3) {
                    case 0:  sourcePt.setOrdinate(i, envelope.getMinimum(i)); n /= 3; break;
                    case 1:  sourcePt.setOrdinate(i, envelope.getMaximum(i)); continue loop;
                    case 2:  sourcePt.setOrdinate(i, envelope.getMedian (i)); continue loop;
                    default: throw new AssertionError(n); // Should never happen
                }
            }
            break;
        }
        return transformed;
    }

    /**
     * Transforms an envelope using the given {@linkplain CoordinateOperation coordinate operation}.
     * The transformation is only approximative: the returned envelope may be bigger than the
     * smallest possible bounding box, but should not be smaller in most cases.
     * <p>
     * This method can handle the case where the envelope contains the North or South pole,
     * or when it cross the &plusmn;180&deg; longitude.
     *
     * {@note If the envelope CRS is non-null, then the caller should ensure that the operation
     * source CRS is the same than the envelope CRS. In case of mismatch, this method transforms
     * the envelope to the operation source CRS before to apply the operation. This extra step
     * may cause a lost of accuracy. In order to prevent this method from performing such
     * pre-transformation (if not desired), callers can ensure that the envelope CRS is
     * <code>null</code> before to call this method.}
     *
     * @param  operation The operation to use.
     * @param  envelope Envelope to transform, or {@code null}. This envelope will not be modified.
     * @return The transformed envelope, or {@code null} if {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @see #transform(MathTransform, Envelope)
     */
    public static GeneralEnvelope transform(final CoordinateOperation operation, Envelope envelope)
            throws TransformException
    {
        ensureNonNull("operation", operation);
        if (envelope == null) {
            return null;
        }
        final CoordinateReferenceSystem sourceCRS = operation.getSourceCRS();
        if (sourceCRS != null) {
            final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();
            if (crs != null && !CRS.equalsIgnoreMetadata(crs, sourceCRS)) {
                /*
                 * Argument-check: the envelope CRS seems inconsistent with the given operation.
                 * However we need to push the check a little bit further, since 3D-GeographicCRS
                 * are considered not equal to CompoundCRS[2D-GeographicCRS + ellipsoidal height].
                 * Checking for identity MathTransform is a more powerfull (but more costly) check.
                 * Since we have the MathTransform, perform an opportunist envelope transform if it
                 * happen to be required.
                 */
                final MathTransform mt;
                try {
                    mt = CRS.findMathTransform(crs, sourceCRS, false);
                } catch (FactoryException e) {
                    throw new TransformException(Errors.format(Errors.Keys.CANT_TRANSFORM_ENVELOPE), e);
                }
                if (!mt.isIdentity()) {
                    envelope = transform(mt, envelope);
                }
            }
        }
        MathTransform mt = operation.getMathTransform();
        final GeneralDirectPosition centerPt = new GeneralDirectPosition(mt.getTargetDimensions());
        final GeneralEnvelope transformed = transform(mt, envelope, centerPt);
        /*
         * If the source envelope crosses the expected range of valid coordinates, also projects
         * the range bounds as a safety. Example: if the source envelope goes from 150 to 200°E,
         * some map projections will interpret 200° as if it was -160°, and consequently produce
         * an envelope which do not include the 180°W extremum. We will add those extremum points
         * explicitly as a safety. It may leads to bigger than necessary target envelope, but the
         * contract is to include at least the source envelope, not to return the smallest one.
         */
        if (sourceCRS != null) {
            final CoordinateSystem cs = sourceCRS.getCoordinateSystem();
            if (cs != null) { // Should never be null, but check as a paranoiac safety.
                DirectPosition sourcePt = null;
                DirectPosition targetPt = null;
                final int dimension = cs.getDimension();
                for (int i=0; i<dimension; i++) {
                    final CoordinateSystemAxis axis = cs.getAxis(i);
                    if (axis == null) { // Should never be null, but check as a paranoiac safety.
                        continue;
                    }
                    final double min = envelope.getMinimum(i);
                    final double max = envelope.getMaximum(i);
                    final double  v1 = axis.getMinimumValue();
                    final double  v2 = axis.getMaximumValue();
                    final boolean b1 = (v1 > min && v1 < max);
                    final boolean b2 = (v2 > min && v2 < max);
                    if (!b1 && !b2) {
                        continue;
                    }
                    if (sourcePt == null) {
                        sourcePt = new GeneralDirectPosition(dimension);
                        for (int j=0; j<dimension; j++) {
                            sourcePt.setOrdinate(j, envelope.getMedian(j));
                        }
                    }
                    if (b1) {
                        sourcePt.setOrdinate(i, v1);
                        transformed.add(targetPt = mt.transform(sourcePt, targetPt));
                    }
                    if (b2) {
                        sourcePt.setOrdinate(i, v2);
                        transformed.add(targetPt = mt.transform(sourcePt, targetPt));
                    }
                    sourcePt.setOrdinate(i, envelope.getMedian(i));
                }
            }
        }
        /*
         * Now takes the target CRS in account...
         */
        final CoordinateReferenceSystem targetCRS = operation.getTargetCRS();
        if (targetCRS == null) {
            return transformed;
        }
        transformed.setCoordinateReferenceSystem(targetCRS);
        final CoordinateSystem targetCS = targetCRS.getCoordinateSystem();
        if (targetCS == null) {
            // It should be an error, but we keep this method tolerant.
            return transformed;
        }
        /*
         * Checks for singularity points. For example the south pole is a singularity point in
         * geographic CRS because is is located at the maximal value allowed by one particular
         * axis, namely latitude. This point is not a singularity in the stereographic projection,
         * because axes extends toward infinity in all directions (mathematically) and because the
         * South pole has nothing special apart being the origin (0,0).
         *
         * Algorithm:
         *
         * 1) Inspect the target axis, looking if there is any bounds. If bounds are found, get
         *    the coordinates of singularity points and project them from target to source CRS.
         *
         *    Example: If the transformed envelope above is (80 … 85°S, 10 … 50°W), and if the
         *             latitude in the target CRS is bounded at 90°S, then project (90°S, 30°W)
         *             to the source CRS. Note that the longitude is set to the the center of
         *             the envelope longitude range (more on this below).
         *
         * 2) If the singularity point computed above is inside the source envelope, add that
         *    point to the target (transformed) envelope.
         *
         * 3) If step #2 added the point, iterate over all other axes. If an other bounded axis
         *    is found and that axis is of kind "WRAPAROUND", test for inclusion the same point
         *    than the point tested at step #1, except for the ordinate of the axis found in this
         *    step. That ordinate is set to the minimal and maximal values of that axis.
         *
         *    Example: If the above steps found that the point (90°S, 30°W) need to be included,
         *             then this step #3 will also test phe points (90°S, 180°W) and (90°S, 180°E).
         *
         * NOTE: we test (-180°, centerY), (180°, centerY), (centerX, -90°) and (centerX, 90°)
         * at step #1 before to test (-180°, -90°), (180°, -90°), (-180°, 90°) and (180°, 90°)
         * at step #3 because the later may not be supported by every projections. For example
         * if the target envelope is located between 20°N and 40°N, then a Mercator projection
         * may fail to transform the (-180°, 90°) coordinate while the (-180°, 30°) coordinate
         * is a valid point.
         */
        TransformException warning = null;
        GeneralEnvelope generalEnvelope = null;
        DirectPosition sourcePt = null;
        DirectPosition targetPt = null;
        long includedMinValue = 0; // A bitmask for each dimension.
        long includedMaxValue = 0;
        long isWrapAroundAxis = 0;
        long dimensionBitMask = 1;
        final int dimension = targetCS.getDimension();
        for (int i=0; i<dimension; i++, dimensionBitMask <<= 1) {
            final CoordinateSystemAxis axis = targetCS.getAxis(i);
            if (axis == null) { // Should never be null, but check as a paranoiac safety.
                continue;
            }
            boolean testMax = false; // Tells if we are testing the minimal or maximal value.
            do {
                final double extremum = testMax ? axis.getMaximumValue() : axis.getMinimumValue();
                if (Double.isInfinite(extremum) || Double.isNaN(extremum)) {
                    /*
                     * The axis is unbounded. It should always be the case when the target CRS is
                     * a map projection, in which case this loop will finish soon and this method
                     * will do nothing more (no object instantiated, no MathTransform inversed...)
                     */
                    continue;
                }
                if (targetPt == null) {
                    try {
                        mt = mt.inverse();
                    } catch (NoninvertibleTransformException exception) {
                        /*
                         * If the transform is non invertible, this method can't do anything. This
                         * is not a fatal error because the envelope has already be transformed by
                         * the caller. We lost the check for singularity points performed by this
                         * method, but it make no difference in the common case where the source
                         * envelope didn't contains any of those points.
                         *
                         * Note that this exception is normal if target dimension is smaller than
                         * source dimension, since the math transform can not reconstituate the
                         * lost dimensions. So we don't log any warning in this case.
                         */
                        if (dimension >= mt.getSourceDimensions()) {
                            recoverableException(exception);
                        }
                        return transformed;
                    }
                    targetPt = new GeneralDirectPosition(mt.getSourceDimensions());
                    for (int j=0; j<dimension; j++) {
                        targetPt.setOrdinate(j, centerPt.getOrdinate(j));
                    }
                    // TODO: avoid the hack below if we provide a contains(DirectPosition)
                    //       method in the GeoAPI org.opengis.geometry.Envelope interface.
                    generalEnvelope = GeneralEnvelope.castOrCopy(envelope);
                }
                targetPt.setOrdinate(i, extremum);
                try {
                    sourcePt = mt.transform(targetPt, sourcePt);
                } catch (TransformException exception) {
                    /*
                     * This exception may be normal. For example if may occur when projecting
                     * the latitude extremums with a cylindrical Mercator projection.  Do not
                     * log any message (unless logging level is fine) and try the other points.
                     */
                    if (warning == null) {
                        warning = exception;
                    } else {
                        // TODO: addSuppress with JDK7.
                    }
                    continue;
                }
                if (generalEnvelope.contains(sourcePt)) {
                    transformed.add(targetPt);
                    if (testMax) includedMaxValue |= dimensionBitMask;
                    else         includedMinValue |= dimensionBitMask;
                }
            } while ((testMax = !testMax) == true);
            /*
             * Keep trace of axes of kind WRAPAROUND, except if the two extremum values of that
             * axis have been included in the envelope  (in which case the next step after this
             * loop doesn't need to be executed for that axis).
             */
            if ((includedMinValue & includedMaxValue & dimensionBitMask) == 0 && isWrapAround(axis)) {
                isWrapAroundAxis |= dimensionBitMask;
            }
            // Restore 'targetPt' to its initial state, which is equals to 'centerPt'.
            if (targetPt != null) {
                targetPt.setOrdinate(i, centerPt.getOrdinate(i));
            }
        }
        /*
         * Step #3 described in the above "Algorithm" section: iterate over all dimensions
         * of type "WRAPAROUND" for which minimal or maximal axis values have not yet been
         * included in the envelope. The set of axes is specified by a bitmask computed in
         * the above loop.  We examine only the points that have not already been included
         * in the envelope.
         */
        final long includedBoundsValue = (includedMinValue | includedMaxValue);
        if (includedBoundsValue != 0) {
            while (isWrapAroundAxis != 0) {
                final int wrapAroundDimension = Long.numberOfTrailingZeros(isWrapAroundAxis);
                dimensionBitMask = 1 << wrapAroundDimension;
                isWrapAroundAxis &= ~dimensionBitMask; // Clear now the bit, for the next iteration.
                final CoordinateSystemAxis wrapAroundAxis = targetCS.getAxis(wrapAroundDimension);
                final double min = wrapAroundAxis.getMinimumValue();
                final double max = wrapAroundAxis.getMaximumValue();
                /*
                 * Iterate over all axes for which a singularity point has been previously found,
                 * excluding the "wrap around axis" currently under consideration.
                 */
                for (long am=(includedBoundsValue & ~dimensionBitMask), bm; am != 0; am &= ~bm) {
                    bm = Long.lowestOneBit(am);
                    final int axisIndex = Long.numberOfTrailingZeros(bm);
                    final CoordinateSystemAxis axis = targetCS.getAxis(axisIndex);
                    /*
                     * switch (c) {
                     *   case 0: targetPt = (..., singularityMin, ..., wrapAroundMin, ...)
                     *   case 1: targetPt = (..., singularityMin, ..., wrapAroundMax, ...)
                     *   case 2: targetPt = (..., singularityMax, ..., wrapAroundMin, ...)
                     *   case 3: targetPt = (..., singularityMax, ..., wrapAroundMax, ...)
                     * }
                     */
                    for (int c=0; c<4; c++) {
                        /*
                         * Set the ordinate value along the axis having the singularity point
                         * (cases c=0 and c=2). If the envelope did not included that point,
                         * then skip completly this case and the next one, i.e. skip c={0,1}
                         * or skip c={2,3}.
                         */
                        double value = max;
                        if ((c & 1) == 0) { // 'true' if we are testing "wrapAroundMin".
                            if (((c == 0 ? includedMinValue : includedMaxValue) & bm) == 0) {
                                c++; // Skip also the case for "wrapAroundMax".
                                continue;
                            }
                            targetPt.setOrdinate(axisIndex, (c == 0) ? axis.getMinimumValue() : axis.getMaximumValue());
                            value = min;
                        }
                        targetPt.setOrdinate(wrapAroundDimension, value);
                        try {
                            sourcePt = mt.transform(targetPt, sourcePt);
                        } catch (TransformException exception) {
                            if (warning == null) {
                                warning = exception;
                            } else {
                                // TODO: addSuppress with JDK7.
                            }
                            continue;
                        }
                        if (generalEnvelope.contains(sourcePt)) {
                            transformed.add(targetPt);
                        }
                    }
                    targetPt.setOrdinate(axisIndex, centerPt.getOrdinate(axisIndex));
                }
                targetPt.setOrdinate(wrapAroundDimension, centerPt.getOrdinate(wrapAroundDimension));
            }
        }
        if (warning != null) {
            recoverableException(warning);
        }
        return transformed;
    }

    /**
     * Transforms a rectangular envelope using the given {@linkplain MathTransform math transform}.
     * The transformation is only approximative: the returned envelope may be bigger than
     * necessary, or smaller than required if the bounding box contains a pole.
     * <p>
     * Invoking this method is equivalent to invoking the following:
     *
     * {@preformat java
     *   transform(transform, new GeneralEnvelope(envelope)).toRectangle2D()
     * }
     *
     * Note that this method can not handle the case where the rectangle contains the North or South
     * pole, or when it cross the &plusmn;180&deg; longitude, because {@linkplain MathTransform
     * math transforms} do not carry sufficient informations. For a more robust rectangle
     * transformation, use {@link #transform(CoordinateOperation, Rectangle2D, Rectangle2D)}
     * instead.
     *
     * @param  transform   The transform to use. Source and target dimension must be 2.
     * @param  envelope    The rectangle to transform (may be {@code null}).
     * @param  destination The destination rectangle (may be {@code envelope}).
     *         If {@code null}, a new rectangle will be created and returned.
     * @return {@code destination}, or a new rectangle if {@code destination} was non-null
     *         and {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @see #transform(CoordinateOperation, Rectangle2D, Rectangle2D)
     * @see org.geotoolkit.referencing.operation.matrix.XAffineTransform#transform(AffineTransform, Rectangle2D, Rectangle2D)
     */
    public static Rectangle2D transform(final MathTransform2D transform,
                                        final Rectangle2D     envelope,
                                              Rectangle2D     destination)
            throws TransformException
    {
        ensureNonNull("transform", transform);
        if (transform instanceof AffineTransform) {
            // Common case implemented in a more efficient way (less points to transform).
            return XAffineTransform.transform((AffineTransform) transform, envelope, destination);
        }
        return transform(transform, envelope, destination, new Point2D.Double());
    }

    /**
     * Implementation of {@link #transform(MathTransform, Rectangle2D, Rectangle2D)} with the
     * opportunity to save the projected center coordinate. This method sets {@code point} to
     * the center of the source envelope projected to the target CRS.
     */
    @SuppressWarnings("fallthrough")
    private static Rectangle2D transform(final MathTransform2D transform,
                                         final Rectangle2D     envelope,
                                               Rectangle2D     destination,
                                         final Point2D.Double  point)
            throws TransformException
    {
        if (envelope == null) {
            return null;
        }
        double xmin = Double.POSITIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        for (int i=0; i<=8; i++) {
            /*
             *   (0)────(5)────(1)
             *    |             |
             *   (4)    (8)    (7)
             *    |             |
             *   (2)────(6)────(3)
             *
             * (note: center must be last)
             */
            point.x = (i & 1) == 0 ? envelope.getMinX() : envelope.getMaxX();
            point.y = (i & 2) == 0 ? envelope.getMinY() : envelope.getMaxY();
            switch (i) {
                case 5: // fall through
                case 6: point.x = envelope.getCenterX(); break;
                case 8: point.x = envelope.getCenterX(); // fall through
                case 7: // fall through
                case 4: point.y = envelope.getCenterY(); break;
            }
            if (point != transform.transform(point, point)) {
                throw new UnsupportedImplementationException(transform.getClass());
            }
            if (point.x < xmin) xmin = point.x;
            if (point.x > xmax) xmax = point.x;
            if (point.y < ymin) ymin = point.y;
            if (point.y > ymax) ymax = point.y;
        }
        if (destination != null) {
            destination.setRect(xmin, ymin, xmax-xmin, ymax-ymin);
        } else {
            destination = XRectangle2D.createFromExtremums(xmin, ymin, xmax, ymax);
        }
        // Attempt the 'equalsEpsilon' assertion only if source and destination are not same and
        // if the target envelope is Float or Double (this assertion doesn't work with integers).
        assert (destination == envelope || !(destination instanceof Rectangle2D.Double ||
                destination instanceof Rectangle2D.Float)) || XRectangle2D.equalsEpsilon(destination,
                transform(transform, new Envelope2D(null, envelope)).toRectangle2D()) : destination;
        return destination;
    }

    /**
     * Transforms a rectangular envelope using the given {@linkplain CoordinateOperation coordinate
     * operation}. The transformation is only approximative: the returned envelope may be bigger
     * than the smallest possible bounding box, but should not be smaller in most cases.
     * <p>
     * Invoking this method is equivalent to invoking the following:
     *
     * {@preformat java
     *     transform(operation, new GeneralEnvelope(envelope)).toRectangle2D()
     * }
     *
     * This method can handle the case where the rectangle contains the North or South pole,
     * or when it cross the &plusmn;180&deg; longitude.
     *
     * @param  operation The operation to use. Source and target dimension must be 2.
     * @param  envelope The rectangle to transform (may be {@code null}).
     * @param  destination The destination rectangle (may be {@code envelope}).
     *         If {@code null}, a new rectangle will be created and returned.
     * @return {@code destination}, or a new rectangle if {@code destination} was non-null
     *         and {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @see #transform(MathTransform2D, Rectangle2D, Rectangle2D)
     * @see org.geotoolkit.referencing.operation.matrix.XAffineTransform#transform(AffineTransform, Rectangle2D, Rectangle2D)
     */
    public static Rectangle2D transform(final CoordinateOperation operation,
                                        final Rectangle2D         envelope,
                                              Rectangle2D         destination)
            throws TransformException
    {
        ensureNonNull("operation", operation);
        if (envelope == null) {
            return null;
        }
        final MathTransform transform = operation.getMathTransform();
        if (!(transform instanceof MathTransform2D)) {
            throw new MismatchedDimensionException(Errors.format(Errors.Keys.NO_TRANSFORM2D_AVAILABLE));
        }
        MathTransform2D mt = (MathTransform2D) transform;
        final Point2D.Double center = new Point2D.Double();
        destination = transform(mt, envelope, destination, center);
        /*
         * If the source envelope crosses the expected range of valid coordinates, also projects
         * the range bounds as a safety. See the comments in transform(Envelope, ...).
         */
        final CoordinateReferenceSystem sourceCRS = operation.getSourceCRS();
        if (sourceCRS != null) {
            final CoordinateSystem cs = sourceCRS.getCoordinateSystem();
            if (cs != null && cs.getDimension() == 2) { // Paranoiac check.
                CoordinateSystemAxis axis = cs.getAxis(0);
                double min = envelope.getMinX();
                double max = envelope.getMaxX();
                Point2D.Double pt = null;
                for (int i=0; i<4; i++) {
                    if (i == 2) {
                        axis = cs.getAxis(1);
                        min = envelope.getMinY();
                        max = envelope.getMaxY();
                    }
                    final double v = (i & 1) == 0 ? axis.getMinimumValue() : axis.getMaximumValue();
                    if (!(v > min && v < max)) {
                        continue;
                    }
                    if (pt == null) {
                        pt = new Point2D.Double();
                    }
                    if ((i & 2) == 0) {
                        pt.x = v;
                        pt.y = envelope.getCenterY();
                    } else {
                        pt.x = envelope.getCenterX();
                        pt.y = v;
                    }
                    destination.add(mt.transform(pt, pt));
                }
            }
        }
        /*
         * Now takes the target CRS in account...
         */
        final CoordinateReferenceSystem targetCRS = operation.getTargetCRS();
        if (targetCRS == null) {
            return destination;
        }
        final CoordinateSystem targetCS = targetCRS.getCoordinateSystem();
        if (targetCS == null || targetCS.getDimension() != 2) {
            // It should be an error, but we keep this method tolerant.
            return destination;
        }
        /*
         * Checks for singularity points. See the transform(CoordinateOperation, Envelope)
         * method for comments about the algorithm. The code below is the same algorithm
         * adapted for the 2D case and the related objects (Point2D, Rectangle2D, etc.).
         *
         * The 'border' variable in the loop below is used in order to compress 2 dimensions
         * and 2 extremums in a single loop, in this order: (xmin, xmax, ymin, ymax).
         */
        TransformException warning = null;
        Point2D sourcePt = null;
        Point2D targetPt = null;
        int includedBoundsValue = 0; // A bitmask for each (dimension, extremum) pairs.
        for (int border=0; border<4; border++) { // 2 dimensions and 2 extremums compacted in a flag.
            final int dimension = border >>> 1;  // The dimension index being examined.
            final CoordinateSystemAxis axis = targetCS.getAxis(dimension);
            if (axis == null) { // Should never be null, but check as a paranoiac safety.
                continue;
            }
            final double extremum = (border & 1) == 0 ? axis.getMinimumValue() : axis.getMaximumValue();
            if (Double.isInfinite(extremum) || Double.isNaN(extremum)) {
                continue;
            }
            if (targetPt == null) {
                try {
                    mt = mt.inverse();
                } catch (NoninvertibleTransformException exception) {
                    recoverableException(exception);
                    return destination;
                }
                targetPt = new Point2D.Double();
            }
            switch (dimension) {
                case 0: targetPt.setLocation(extremum, center.y); break;
                case 1: targetPt.setLocation(center.x, extremum); break;
                default: throw new AssertionError(border);
            }
            try {
                sourcePt = mt.transform(targetPt, sourcePt);
            } catch (TransformException exception) {
                if (warning == null) {
                    warning = exception;
                } else {
                    // TODO: addSuppress with JDK7.
                }
                continue;
            }
            if (envelope.contains(sourcePt)) {
                destination.add(targetPt);
                includedBoundsValue |= (1 << border);
            }
        }
        /*
         * Iterate over all dimensions of type "WRAPAROUND" for which minimal or maximal axis
         * values have not yet been included in the envelope. We could inline this check inside
         * the above loop, but we don't in order to have a chance to exclude the dimensions for
         * which the point have already been added.
         *
         * See transform(CoordinateOperation, Envelope) for more comments about the algorithm.
         */
        if (includedBoundsValue != 0) {
            /*
             * Bits mask transformation:
             *   1) Swaps the two dimensions               (YyXx  →  XxYy)
             *   2) Insert a space between each bits       (XxYy  →  X.x.Y.y.)
             *   3) Fill the space with duplicated values  (X.x.Y.y.  →  XXxxYYyy)
             *
             * In terms of bit positions 1,2,4,8 (not bit values), we have:
             *
             *   8421  →  22881144
             *   i.e. (ymax, ymin, xmax, xmin)  →  (xmax², ymax², xmin², ymin²)
             *
             * Now look at the last part: (xmin², ymin²). The next step is to perform a bitwise
             * AND operation in order to have only both of the following conditions:
             *
             *   Borders not yet added to the envelope: ~(ymax, ymin, xmax, xmin)
             *   Borders in which a singularity exists:  (xmin, xmin, ymin, ymin)
             *
             * The same operation is repeated on the next 4 bits for (xmax, xmax, ymax, ymax).
             */
            int toTest = ((includedBoundsValue & 1) << 3) | ((includedBoundsValue & 4) >>> 1) |
                         ((includedBoundsValue & 2) << 6) | ((includedBoundsValue & 8) << 2);
            toTest |= (toTest >>> 1); // Duplicate the bit values.
            toTest &= ~(includedBoundsValue | (includedBoundsValue << 4));
            /*
             * Forget any axes that are not of kind "WRAPAROUND". Then get the final
             * bit pattern indicating which points to test. Iterate over that bits.
             */
            if ((toTest & 0x33333333) != 0 && !isWrapAround(targetCS.getAxis(0))) toTest &= 0xCCCCCCCC;
            if ((toTest & 0xCCCCCCCC) != 0 && !isWrapAround(targetCS.getAxis(1))) toTest &= 0x33333333;
            while (toTest != 0) {
                final int border = Integer.numberOfTrailingZeros(toTest);
                final int bitMask = 1 << border;
                toTest &= ~bitMask; // Clear now the bit, for the next iteration.
                final int dimensionToAdd = (border >>> 1) & 1;
                final CoordinateSystemAxis toAdd = targetCS.getAxis(dimensionToAdd);
                final CoordinateSystemAxis added = targetCS.getAxis(dimensionToAdd ^ 1);
                double x = (border & 1) == 0 ? toAdd.getMinimumValue() : toAdd.getMaximumValue();
                double y = (border & 4) == 0 ? added.getMinimumValue() : added.getMaximumValue();
                if (dimensionToAdd != 0) {
                    final double t=x; x=y; y=t;
                }
                targetPt.setLocation(x, y);
                try {
                    sourcePt = mt.transform(targetPt, sourcePt);
                } catch (TransformException exception) {
                    if (warning == null) {
                        warning = exception;
                    } else {
                        // TODO: addSuppress with JDK7.
                    }
                    continue;
                }
                if (envelope.contains(sourcePt)) {
                    destination.add(targetPt);
                }
            }
        }
        if (warning != null) {
            recoverableException(warning);
        }
        // Attempt the 'equalsEpsilon' assertion only if source and destination are not same.
        assert (destination == envelope) || XRectangle2D.equalsEpsilon(destination,
                transform(operation, new GeneralEnvelope(envelope)).toRectangle2D()) : destination;
        return destination;
    }

    /**
     * Returns {@code true} if the given axis is of kind "Wrap Around".
     */
    private static boolean isWrapAround(final CoordinateSystemAxis axis) {
        return RangeMeaning.WRAPAROUND.equals(axis.getRangeMeaning());
    }

    /**
     * Invoked when a recoverable exception occurred. Those exceptions must be minor enough
     * that they can be silently ignored in most cases.
     */
    private static void recoverableException(final Exception exception) {
        Logging.recoverableException(Envelopes.class, "transform", exception);
    }

    /**
     * Returns an envelope from the given <cite>Well Known Text</cite> (WKT).
     * This method is quite lenient. For example all the following strings are accepted:
     * <p>
     * <ul>
     *   <li>{@code BOX(-180 -90, 180 90)}</li>
     *   <li>{@code POINT(6 10)}</li>
     *   <li>{@code MULTIPOLYGON(((1 1, 5 1, 1 5, 1 1),(2 2, 3 2, 3 3, 2 2)))}</li>
     *   <li>{@code GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(3 8,7 10))}</li>
     * </ul>
     * <p>
     * See {@link GeneralEnvelope#GeneralEnvelope(String)} for more information about the
     * parsing rules.
     *
     * @param  wkt The {@code BOX}, {@code POLYGON} or other kind of element to parse.
     * @return The envelope of the given geometry.
     * @throws FactoryException If the given WKT can not be parsed.
     *
     * @see CRS#parseWKT(String)
     * @see #toWKT(Envelope)
     * @see org.geotoolkit.measure.CoordinateFormat
     * @see org.geotoolkit.io.wkt
     */
    public static Envelope parseWKT(final String wkt) throws FactoryException {
        ensureNonNull("wkt", wkt);
        try {
            return new GeneralEnvelope(wkt);
        } catch (RuntimeException e) {
            throw new FactoryException(Errors.format(
                    Errors.Keys.CANT_CREATE_FROM_TEXT_$1, Envelope.class), e);
        }
    }

    /**
     * Formats a {@code BOX} element from an envelope. This method formats the given envelope in
     * the <cite>Well Known Text</cite> (WKT) format. The output is like below, where <var>n</var>
     * is the {@linkplain Envelope#getDimension() number of dimensions}:
     *
     * <blockquote>{@code BOX}<var>n</var>{@code D(}{@linkplain Envelope#getLowerCorner() lower
     * corner}{@code ,} {@linkplain Envelope#getUpperCorner() upper corner}{@code )}</blockquote>
     *
     * The output of this method can be {@linkplain GeneralEnvelope#GeneralEnvelope(String) parsed}
     * by the {@link GeneralEnvelope} constructor.
     *
     * @param  envelope The envelope to format.
     * @return The envelope as a {@code BOX2D} or {@code BOX3D} in WKT format.
     *
     * @see #parseWKT(String)
     * @see org.geotoolkit.measure.CoordinateFormat
     * @see org.geotoolkit.io.wkt
     */
    public static String toWKT(final Envelope envelope) {
        return AbstractEnvelope.toString(envelope);
    }

    /**
     * Formats a {@code POLYGON} element from an envelope. This method formats the given envelope
     * as a geometry in the <cite>Well Known Text</cite> (WKT) format. This is provided as an
     * alternative to the {@code BOX} element formatted by the above {@link #toWKT(Envelope)}
     * method, because the {@code BOX} element is usually not considered a geometry while
     * {@code POLYGON} is.
     * <p>
     * The output of this method can be {@linkplain GeneralEnvelope#GeneralEnvelope(String) parsed}
     * by the {@link GeneralEnvelope} constructor.
     *
     * @param  envelope The envelope to format.
     * @return The envelope as a {@code POLYGON} in WKT format.
     *
     * @see org.geotoolkit.io.wkt
     */
    public static String toPolygonWKT(final Envelope envelope) {
        /*
         * Get the dimension, ignoring the trailing ones which have infinite values.
         */
        int dimension = envelope.getDimension();
        while (dimension != 0) {
            final double length = envelope.getSpan(dimension - 1);
            if (!Double.isNaN(length) && !Double.isInfinite(length)) {
                break;
            }
            dimension--;
        }
        final StringBuilder buffer = new StringBuilder("POLYGON(");
        String separator = "(";
        for (int corner=0; corner<CORNERS.length; corner+=2) {
            for (int i=0; i<dimension; i++) {
                final double value;
                switch (i) {
                    case  0: // Fall through
                    case  1: value = CORNERS[corner+i] ? envelope.getMaximum(i) : envelope.getMinimum(i); break;
                    default: value = envelope.getMedian(i); break;
                }
                trimFractionalPart(buffer.append(separator).append(value));
                separator = " ";
            }
            separator = ", ";
        }
        if (separator == ", ") { // NOSONAR: identity comparison is okay here.
            buffer.append(')');
        }
        return buffer.append(')').toString();
    }
}
