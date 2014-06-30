/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains formulas from the PROJ package of USGS.
 *    USGS's work is fully acknowledged here. This derived work has
 *    been relicensed under LGPL with Frank Warmerdam's permission.
 */
package org.geotoolkit.referencing.operation.projection;

import java.io.Serializable;
import java.util.Objects;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.awt.geom.AffineTransform;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.util.GenericName;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Errors;
import org.apache.sis.measure.Latitude;
import org.apache.sis.measure.Longitude;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.util.Utilities;
import org.apache.sis.util.Deprecable;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.collection.WeakHashSet;
import org.geotoolkit.referencing.operation.provider.UniversalParameters;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.provider.MapProjection;
import org.geotoolkit.referencing.operation.transform.AbstractMathTransform2D;

import static java.lang.Math.*;
import static java.lang.Double.*;
import static org.apache.sis.math.MathFunctions.atanh;
import static org.apache.sis.math.MathFunctions.xorSign;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.util.Utilities.hash;
import static org.geotoolkit.internal.InternalUtilities.epsilonEqual;
import static org.geotoolkit.parameter.Parameters.getOrCreate;
import static org.geotoolkit.referencing.operation.provider.UniversalParameters.*;
import static org.geotoolkit.referencing.operation.provider.MapProjection.XY_PLANE_ROTATION;


/**
 * Base class for conversion services between ellipsoidal and cartographic projections.
 * This conversion works on a normalized spaces, where angles are express in radians and
 * computations are performed for a sphere having a semi-major axis of 1. More specifically:
 *
 * <ul>
 *   <li><p>On input, the {@link #transform(double[],int,double[],int,boolean) transform} method expects
 *   (<var>longitude</var>, <var>latitude</var>) angles in <strong>radians</strong>. Longitudes
 *   have the {@linkplain Parameters#centralMeridian central meridian} removed before the transform
 *   method is invoked. The conversion from degrees to radians and the longitude rotation are applied
 *   by the {@linkplain Parameters#normalize(boolean) normalize} affine transform.</p></li>
 *
 *   <li><p>On output, the {@link #transform(double[],int,double[],int,boolean) transform} method returns
 *   (<var>easting</var>, <var>northing</var>) values on a sphere or ellipse having a semi-major
 *   axis length of 1. The multiplication by the scale factor and the false easting/northing offsets
 *   are applied by the {@link Parameters#normalize(boolean) denormalize} affine transform.</p></li>
 * </ul>
 *
 * {@code UnitaryProjection} does not expose publicly the above cited parameters (central meridian,
 * scale factor, <i>etc.</i>) on intend, in order to make clear that those parameters are not used
 * by subclasses. This separation removes ambiguity when testing for {@linkplain #equals(Object,
 * ComparisonMode) equivalence}. The ability to recognize two {@code UnitaryProjection}s as
 * equivalent without consideration for the scale factor (among other) allow more efficient
 * concatenation in some cases (typically some combinations of inverse projection followed
 * by a direct projection).
 * <p>
 * All angles (either fields, method parameters or return values) in this class and subclasses are
 * in radians. This is the opposite of {@link Parameters} where all angles are in decimal degrees.
 *
 * {@note Serialization of this class is appropriate for short-term storage or RMI use, but may
 *        not be compatible with future versions. For long term storage, WKT (Well Know Text) or
 *        XML are more appropriate.}
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author André Gosselin (MPO)
 * @author Rueben Schulz (UBC)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.20
 *
 * @see <A HREF="http://mathworld.wolfram.com/MapProjection.html">Map projections on MathWorld</A>
 * @see <A HREF="http://atlas.gc.ca/site/english/learningresources/carto_corner/map_projections.html">Map projections on the atlas of Canada</A>
 *
 * @since 3.18
 * @module
 */
@Immutable
public abstract class UnitaryProjection extends AbstractMathTransform2D implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 1969740225939106310L;

    /**
     * Tolerance in the correctness of argument values provided to the mathematical functions
     * defined in this class.
     *
     * @since 3.18
     */
    private static final double ARGUMENT_TOLERANCE = 1E-15;

    /**
     * Maximum difference allowed when comparing longitudes or latitudes in radians.
     * A tolerance of 1E-6 is about 0.2 second of arcs, which is about 6 kilometers
     * (computed from the standard length of nautical mile).
     * <p>
     * Some formulas use this tolerance value for testing sinus or cosinus of an angle.
     * In the sinus case, this is justified because <code>sin(&theta;) ≅ &theta;</code>
     * when &theta; is small. Similar reasoning applies to cosinus with
     * <code>cos(&theta;) ≅ &theta; + &pi;/2</code> when &theta; is small.
     */
    static final double ANGLE_TOLERANCE = 1E-6;

    /**
     * Difference allowed in iterative computations. A value of 1E-10 causes the
     * {@link #cphi2} function to compute the latitude at a precision of 1E-10 radians,
     * which is slightly smaller than one millimetre.
     */
    static final double ITERATION_TOLERANCE = 1E-10;

    /**
     * Maximum number of iterations for iterative computations.
     */
    static final int MAXIMUM_ITERATIONS = 15;

    /**
     * Maximum difference allowed when comparing real numbers (other cases). The value defined
     * here is consistent with the one that was used in {@link LambertAzimuthalEqualArea} for
     * the same purpose (not to be confused with the current {@code EPSILON} constant defined
     * in the above-mentioned class, which has been renamed), and the modified value used in
     * {@link AlbersEqualArea}.
     */
    static final double EPSILON = 1E-7;

    /**
     * The pool of unitary projections created in this running JVM.
     */
    private static final WeakHashSet<UnitaryProjection> POOL =
            new WeakHashSet<>(UnitaryProjection.class);

    /**
     * The parameters used for creating this projection. They are used for formatting <cite>Well
     * Known Text</cite> (WKT) and error messages. Subclasses shall not use the values defined in
     * this object for computation purpose, except at construction time.
     */
    final Parameters parameters;

    /**
     * Ellipsoid excentricity, equal to <code>sqrt({@linkplain #excentricitySquared})</code>.
     * Value 0 means that the ellipsoid is spherical.
     */
    protected final double excentricity;

    /**
     * The square of excentricity: e² = (a²-b²)/a² where
     * <var>e</var> is the {@linkplain #excentricity excentricity},
     * <var>a</var> is the {@linkplain Parameters#semiMajor semi major} axis length and
     * <var>b</var> is the {@linkplain Parameters#semiMinor semi minor} axis length.
     */
    protected final double excentricitySquared;

    /**
     * The inverse of this map projection.
     */
    private final MathTransform2D inverse;

    /**
     * The absolute value of the minimal and maximal longitude value. This is usually either
     * infinity (no bounds check) or {@code PI}, but could also be a different value if a scale
     * has been applied on the normalize affine transform.
     */
    private double longitudeBound = POSITIVE_INFINITY;

    /**
     * The value to subtract from the longitude before to apply a forward projection. This is
     * usually equal to the central meridian in radians, except if some scale has been applied
     * on the normalize affine transform.
     */
    private double longitudeRotation = 0;

    /**
     * Constructs a new map projection from the supplied parameters. Subclass constructors
     * must invoke {@link #finish} when they have finished their work.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected UnitaryProjection(final Parameters parameters) {
        this.parameters = parameters;
        ensureNonNull("parameters", parameters);
        final double a = parameters.semiMajor;
        final double b = parameters.semiMinor;
        excentricitySquared = 1.0 - (b*b) / (a*a);
        excentricity = sqrt(excentricitySquared);
        inverse = new Inverse();
    }

    /**
     * Must be invoked by subclass constructors after they finished their work. Current
     * implementation computes the longitude bounds to be used by {@link #rollLongitude()}.
     */
    protected final void finish() {
        double rotation = 0;
        double bound = POSITIVE_INFINITY;
        final Boolean p = parameters.rollLongitude;
        if ((p == null) ? (parameters.centralMeridian != 0) : p.booleanValue()) {
            final AffineTransform normalize = parameters.normalize(true);
            if (normalize.getShearX() == 0 && normalize.getShearY() == 0) {
                rotation = -normalize.getTranslateX();
                bound = abs(normalize.getScaleX()) * 180;
                if (abs(bound - PI) <= ANGLE_TOLERANCE) {
                    bound = PI;
                }
            } else {
                /*
                 * Should not happen with most projections. If it happen anyway, just log a
                 * warning. We do not consider this limitation as a fatal error since the
                 * projection will still work fine in many cases. We set the source to the
                 * subclass constructor since this is the place where the issue originate.
                 */
                Logging.log(UnitaryProjection.class, "finish", Loggings.format(Level.WARNING,
                        Loggings.Keys.CANT_ROLL_LONGITUDE_1, getClass()));
            }
        }
        longitudeBound = bound;
        longitudeRotation = rotation;
    }

    /**
     * Creates a chain of concatenated transforms from the <cite>normalize</cite> transform,
     * this unitary projection and the <cite>denormalize</cite> transform. This method tries
     * to recycle existing instances of {@code UnitaryProjection} if possible, so subclasses
     * should be careful to implement their {@link #hashCode()} and {@link #equals} methods.
     * <p>
     * This method is not public as a safety against user-defined subclasses which may not
     * implement the above methods correctly. User-defined implementations can use the following
     * code in their {@code create} method, which does the same without the recycling of existing
     * instances:
     *
     * {@preformat java
     *     public static MyProjection create(...) {
     *         Parameters parameters = new Parameters(...);
     *         MyProjection projection = new MyProjection(parameters);
     *         return parameters.createConcatenatedTransform(projection);
     *     }
     * }
     *
     * Note that we do not cache the {@code ConcatenatedTransform} instance since it will be
     * {@code DefaultMathTransformFactory}'s job to do so. We cache only the internal parts
     * because the factory will not see them.
     *
     * @return The concatenation of (<cite>normalize</cite> &ndash; this unitary projection
     *         &ndash; <cite>denormalize</cite>) transforms.
     */
    final MathTransform2D createConcatenatedTransform() {
        return parameters.createConcatenatedTransform(POOL.unique(this));
    }

    /**
     * Convenience method for throwing an exception in case of unknown parameter.
     * This is used by subclass constructors.
     */
    static IllegalArgumentException unknownParameter(final Object parameter) {
        final String name;
        if (parameter instanceof IdentifiedObject) {
            name = ((IdentifiedObject) parameter).getName().getCode();
        } else {
            name = String.valueOf(parameter);
        }
        return new IllegalArgumentException(Errors.format(Errors.Keys.UNKNOWN_PARAMETER_1, name));
    }

    /**
     * Returns {@code true} if {@link #rollLongitude(double)} needs to be invoked.
     * This is used for optimizations only.
     */
    final boolean rollLongitude() {
        return longitudeBound != POSITIVE_INFINITY;
    }

    /**
     * Ensures that the specified longitude stay within its valid range. The longitude bounds
     * are typically, but not always, &plusmn;&pi; radians. This method returns <var>x</var>
     * unchanged if no longitude rolling should be applied.
     * <p>
     * This method should be invoked before the {@linkplain #transform(double[],int,double[],int,boolean)
     * forward transform} begin its calculation.
     *
     * @param  x The longitude to roll.
     * @return The rolled longitude.
     *
     * @see MapProjection#ROLL_LONGITUDE
     */
    protected final double rollLongitude(double x) {
        final double mx = longitudeBound;
        if (mx != POSITIVE_INFINITY) {
            x = rollLongitude(x, mx);
        }
        return x;
    }

    /**
     * Ensures that the given longitude added to the {@linkplain Parameters#centralMeridian
     * central meridian} will stay in the [-180 &hellip; 180]&deg; range. This method returns
     * <var>x</var> unchanged if no longitude rolling should be applied.
     * <p>
     * This method should be invoked after the {@linkplain #inverseTransform inverse transform}
     * finished its calculation.
     *
     * @param  x The longitude to unroll.
     * @return The unrolled longitude.
     *
     * @see #rollLongitude(double)
     */
    protected final double unrollLongitude(double x) {
        final double r = longitudeRotation;
        if (r != 0) {
            final double mx = longitudeBound;
            if (xorSign(x + r, r) > mx) {
                x -= 2*copySign(mx, r);
            }
        }
        return x;
    }

    /**
     * Converts the coordinate in {@code srcPts} at the given offset and stores the result
     * in {@code dstPts} at the given offset. In addition, opportunistically computes the
     * transform derivative if requested.
     * <p>
     * The input ordinates are (<var>&lambda;</var>,<var>&phi;</var>) (the variable names for
     * <var>longitude</var> and <var>latitude</var> respectively) angles in radians, usually
     * (but not always) in the range [-&pi;..&pi;] and [-&pi;/2..&pi;/2] respectively. However
     * values outside those ranges are accepted on the assumption that most implementations use
     * those values only in trigonometric functions like {@linkplain Math#sin sin} and
     * {@linkplain Math#cos cos}. If this assumption is not applicable to a particular subclass,
     * then it is implementor's responsibility to check the range.
     * <p>
     * Input coordinate shall have the {@linkplain Parameters#centralMeridian central meridian}
     * removed from the longitude before this method is invoked. After this method is invoked,
     * the output coordinate shall be multiplied by the global scale factor and the ({@linkplain
     * Parameters#falseEasting false easting}, {@linkplain Parameters#falseNorthing false northing})
     * offset shall be applied. This means that projections that implement this method are performed
     * on a sphere or ellipse having a semi-major axis length of 1.
     * <p>
     * In <A HREF="http://trac.osgeo.org/proj/">PROJ.4</A>, the same standardization,
     * described above, is handled by {@code pj_fwd.c}. Therefore when porting projections
     * from PROJ.4, the forward transform equations can be used directly here with minimal
     * change.
     *
     * @param srcPts The array containing the source point coordinate, as (<var>longitude</var>,
     *               <var>latitude</var>) angles in <strong>radians</strong>.
     * @param srcOff The offset of the point to be converted in the source array.
     * @param dstPts The array into which the converted point coordinate is returned (may be
     *               the same than {@code srcPts}). Ordinates will be expressed in a dimensionless
     *               unit, as a linear distance on a unit sphere or ellipse.
     * @param dstOff The offset of the location of the converted point that is
     *               stored in the destination array.
     * @param  derivate {@code true} for computing the derivative, or {@code false} if not needed.
     * @return The matrix of the projection derivative at the given source position, or {@code null}
     *         if the {@code derivate} argument is {@code false}.
     * @throws ProjectionException if the point can't be converted.
     *
     * @since 3.20 (derived from 3.00)
     */
    @Override
    public abstract Matrix transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, boolean derivate)
            throws ProjectionException;

    /**
     * Converts the coordinate in {@code srcPts} at the given offset and stores the result
     * in {@code ptDst} at the given offset. The output ordinates are (<var>longitude</var>,
     * <var>latitude</var>) angles in radians, usually in the range [-&pi;..&pi;] and
     * [-&pi;/2..&pi;/2] respectively.
     * <p>
     * Input coordinate shall have the ({@linkplain Parameters#falseEasting false easting},
     * {@linkplain Parameters#falseNorthing false northing}) removed and the result divided
     * by the global scale factor before this method is invoked. After this method is invoked,
     * the output coordinate shall have the {@linkplain Parameters#centralMeridian central meridian}
     * added to the longitude in {@code ptDst}. This means that projections that implement this
     * method are performed on a sphere or ellipse having a semi-major axis of 1.
     * <p>
     * In <A HREF="http://www.remotesensing.org/proj/">PROJ.4</A>, the same standardization,
     * described above, is handled by {@code pj_inv.c}. Therefore when porting projections
     * from PROJ.4, the inverse transform equations can be used directly here with minimal
     * change.
     *
     * @param srcPts The array containing the source point coordinate, as linear distance
     *               on a unit sphere or ellipse.
     * @param srcOff The offset of the point to be converted in the source array.
     * @param dstPts the array into which the converted point coordinate is returned (may be
     *               the same than {@code srcPts}). Ordinates will be (<var>longitude</var>,
     *               <var>latitude</var>) angles in <strong>radians</strong>.
     * @param dstOff The offset of the location of the converted point that is
     *               stored in the destination array.
     * @throws ProjectionException if the point can't be converted.
     */
    protected abstract void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException;
    /*
     * Note for subclasses: do NOT invoke Assertions.checkReciprocal in implementations of this
     * method. Doing so would introduce a never-ending loop. This assertion is already performed
     * by the Inverse inner class.
     */

    /**
     * Returns the inverse of this map projection.
     */
    @Override
    public MathTransform2D inverse() {
        return inverse;
    }

    /**
     * Inverse of a normalized map projection.
     *
     * @author Martin Desruisseaux (MPO, IRD)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    private final class Inverse extends AbstractMathTransform2D.Inverse {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -9138242780765956870L;

        /**
         * Default constructor.
         */
        public Inverse() {
            UnitaryProjection.this.super();
        }

        /**
         * Inverse transforms the specified {@code srcPts} and stores the result in {@code dstPts}.
         * If the derivative has been requested, then this method will delegate the derivative
         * calculation to the enclosing class and inverts the resulting matrix.
         *
         * @since 3.20 (derived from 3.00)
         */
        @Override
        public Matrix transform(final double[] srcPts, final int srcOff,
                                      double[] dstPts,       int dstOff,
                                final boolean derivate) throws TransformException
        {
            if (!derivate) {
                inverseTransform(srcPts, srcOff, dstPts, dstOff);
                return null;
            } else {
                if (dstPts == null) {
                    dstPts = new double[2];
                    dstOff = 0;
                }
                inverseTransform(srcPts, srcOff, dstPts, dstOff);
                return Matrices.invert(UnitaryProjection.this.transform(dstPts, dstOff, null, 0, true));
            }
        }
    }




    //////////////////////////////////////////////////////////////////////////////////////////
    ////////                                                                          ////////
    ////////                           FORMULAS FROM SNYDER                           ////////
    ////////                                                                          ////////
    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Computes function <code>f(s,c,e²) = c/sqrt(1 - s²&times;e²)</code> needed for the true scale
     * latitude (Snyder 14-15), where <var>s</var> and <var>c</var> are the sine and cosine of
     * the true scale latitude, and <var>e²</var> is the {@linkplain #excentricitySquared
     * eccentricity squared}.
     * <p>
     * Special cases:
     * <ul>
     *   <li>If φ is 0°, then this method returns 1.</li>
     *   <li>If φ is ±90°, then this method returns 0 provided that we are
     *       not in the spherical case (otherwise we get {@link Double#NaN}).</li>
     * </ul>
     *
     * @param sinφ The   sine of the φ latitude in radians.
     * @param cosφ The cosine of the φ latitude in radians.
     */
    final double msfn(final double sinφ, final double cosφ) {
        assert !(abs(sinφ*sinφ + cosφ*cosφ - 1) > ARGUMENT_TOLERANCE);
        return cosφ / sqrt(1.0 - (sinφ*sinφ) * excentricitySquared);
    }

    /**
     * Computes the derivative of this {@link #msfn(double, double)} method divided by {@code msfn}.
     * Callers must multiply the return value by {@code msfn} in order to get the actual value.
     *
     * @param  sinφ The sinus of latitude.
     * @param  cosφ The cosine of latitude.
     * @param  msfn The value of {@code msfn(sinφ, cosφ)}.
     * @return The {@code msfn} derivative at the specified latitude.
     *
     * @since 3.19
     */
    final double dmsfn_dφ(final double sinφ, final double cosφ, double msfn) {
        msfn *= excentricity;
        return (sinφ/cosφ) * (msfn - 1) * (msfn + 1);
    }

    /**
     * Computes part of function (3-1) from Snyder. This is numerically equivalent to
     * <code>{@linkplain #tsfn tsfn}(-φ, sinφ)</code>, but is defined as a separated
     * function for clarity and because the function properties are not the same.
     *
     * @param  φ    The latitude in radians.
     * @param  sinφ The sine of the φ argument. This is provided explicitly
     *              because in many cases, the caller has already computed this value.
     */
    final double ssfn(double φ, double sinφ) {
        assert !(abs(sinφ - sin(φ)) > ARGUMENT_TOLERANCE) : φ;
        sinφ *= excentricity;
        return tan(PI/4 + 0.5*φ) * pow((1-sinφ) / (1+sinφ), 0.5*excentricity);
    }

    /**
     * Computes the derivative of the {@link #ssfn(double, double)} method divided by {@code ssfn}.
     * Callers must multiply the return value by {@code ssfn} in order to get the actual value.
     *
     * @param  φ    The latitude.
     * @param  sinφ the sine of latitude.
     * @param  cosφ The cosine of latitude.
     * @return The {@code dssfn} derivative at the specified latitude.
     *
     * @since 3.18
     */
    final double dssfn_dφ(final double φ, final double sinφ, final double cosφ) {
        assert !(abs(sinφ - sin(φ)) > ARGUMENT_TOLERANCE) : φ;
        assert !(abs(cosφ - cos(φ)) > ARGUMENT_TOLERANCE) : φ;
        return (1/cosφ) - (excentricitySquared*cosφ)/(1-excentricitySquared*sinφ*sinφ);
    }

    /**
     * Computes functions (15-9) and (9-13) from Snyder. This is equivalent to
     * the negative of function (7-7) and is the converse of {@link #cphi2}.
     * <p>
     * This function has a periodicity of 2π.  The result is always a positive value when
     * φ is valid (more on it below). More specifically its behavior at some
     * particular points is:
     * <p>
     * <ul>
     *   <li>If φ is NaN or infinite, then the result is NaN.</li>
     *   <li>If φ is π/2,  then the result is close to 0.</li>
     *   <li>If φ is 0,    then the result is close to 1.</li>
     *   <li>If φ is -π/2, then the result tends toward positive infinity.
     *       The actual result is not infinity however, but some large value like 1E+10.</li>
     *   <li>If φ, after removal of any 2π periodicity, still outside the [-π/2 ... π/2]
     *       range, then the result is a negative number. If the caller is going to compute the
     *       logarithm of the returned value as in the Mercator projection, he will get NaN.</li>
     * </ul>
     *
     * {@note <code>ssfn(φ, sinφ)</code> which is part of function (3-1)
     *        from Snyder, is equivalent to <code>tsfn(-φ, sinφ)</code>.}
     *
     * @param  φ    The latitude in radians.
     * @param  sinφ The sine of the φ argument. This is provided explicitly
     *              because in many cases, the caller has already computed this value.
     *
     * @return The negative of function 7-7 from Snyder. In the case of Mercator projection,
     *         this is {@code exp(-y)} where <var>y</var> is the northing on the unit ellipse.
     */
    final double tsfn(final double φ, double sinφ) {
        assert !(abs(sinφ - sin(φ)) > ARGUMENT_TOLERANCE) : φ;
        sinφ *= excentricity;
        return tan(PI/4 - 0.5*φ) / pow((1-sinφ) / (1+sinφ), 0.5*excentricity);
    }

    /**
     * Gets the derivative of the {@link #tsfn(double, double)} method divided by {@code tsfn}.
     * Callers must multiply the return value by {@code tsfn} in order to get the actual value.
     *
     * @param  φ    The latitude.
     * @param  sinφ the sine of latitude.
     * @param  cosφ The cosine of latitude.
     * @return The {@code tsfn} derivative at the specified latitude.
     *
     * @since 3.18
     */
    final double dtsfn_dφ(final double φ, final double sinφ, final double cosφ) {
        assert !(abs(sinφ - sin(φ)) > ARGUMENT_TOLERANCE) : φ;
        assert !(abs(cosφ - cos(φ)) > ARGUMENT_TOLERANCE) : φ;
        final double t = (1 - sinφ) / cosφ;
        return (excentricitySquared*cosφ / (1 - excentricitySquared*sinφ*sinφ) - 0.5*(t + 1/t));
    }

    /**
     * Iteratively solve equation (7-9) from Snyder. This is the converse of {@link #tsfn}.
     * The input should be a positive number, otherwise the result will be either outside
     * the [-π/2 ... π/2] range, or will be NaN. Its behavior at some particular points is:
     * <p>
     * <ul>
     *   <li>If {@code ts} is zero, then the result is close to π/2.</li>
     *   <li>If {@code ts} is 1, then the result is close to zero.</li>
     *   <li>If {@code ts} is positive infinity, then the result is close to -π/2.</li>
     * </ul>
     *
     * @param  ts The value returned by {@link #tsfn}.
     * @return The latitude in radians.
     * @throws ProjectionException if the iteration does not converge.
     */
    final double cphi2(final double ts) throws ProjectionException {
        final double he = 0.5 * excentricity;
        double φ = (PI/2) - 2.0 * atan(ts);
        for (int i=0; i<MAXIMUM_ITERATIONS; i++) {
            final double con  = excentricity * sin(φ);
            final double dphi = abs(φ - (φ = PI/2 - 2.0*atan(ts * pow((1-con)/(1+con), he))));
            if (dphi <= ITERATION_TOLERANCE) {
                return φ;
            }
        }
        if (isNaN(ts)) {
            return NaN;
        }
        throw new ProjectionException(Errors.Keys.NO_CONVERGENCE);
    }

    /**
     * Calculates <var>q</var>, Snyder equation (3-12).
     * This equation has the following properties:
     * <ul>
     *   <li>Input  in the [-1 ... +1] range.</li>
     *   <li>Output in the [-2 ... +2] range.</li>
     *   <li>Output is 0 when input is 0.</li>
     *   <li>Output of the same sign than input.</li>
     *   <li>{@code qsfn(-sinφ) == -qsfn(sinφ)}.</li>
     * </ul>
     *
     * @param sinφ Sine of the latitude <var>q</var> is calculated for.
     * @return <var>q</var> from Snyder equation (3-12).
     */
    final double qsfn(final double sinφ) {
        if (excentricity < EPSILON) {
            return 2 * sinφ;
        }
        /*
         * Above check was required because the expression below would simplify to
         * sinφ - 0.5/0*log(1) where the right terms are infinity multiplied by
         * zero, thus producing NaN.
         */
        final double esinφ = excentricity * sinφ;
        return (1 - excentricitySquared) * (sinφ / (1 - esinφ*esinφ) + atanh(esinφ)/excentricity);
    }

    /**
     * Gets the derivative of the {@link #qsfn(double)} method.
     *
     * @param  sinφ The sine of latitude.
     * @param  cosφ The cosines of latitude.
     * @return The {@code qsfn} derivative at the specified latitude.
     *
     * @since 3.18
     */
    final double dqsfn_dφ(final double sinφ, final double cosφ) {
        assert !(abs(sinφ*sinφ + cosφ*cosφ - 1) > ARGUMENT_TOLERANCE);
        double esinφ2 = excentricity * sinφ;
        esinφ2 *= esinφ2;
        return (1 - excentricitySquared) * (cosφ / (1 - esinφ2)) * (1 + ((1 + esinφ2) / (1 - esinφ2)));
    }




    //////////////////////////////////////////////////////////////////////////////////////////
    ////////                                                                          ////////
    ////////                                PARAMETERS                                ////////
    ////////                                                                          ////////
    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Parameters that determine the affine transforms to be applied before and after the
     * {@link UnitaryProjection}. Most of those parameters are not used by the unitary
     * projection itself, but a few ones like the {@linkplain #standardParallels standard
     * parallels} may be on a case-by-case basis.
     * <p>
     * The lifecycle of this object is as below:
     *
     * <ol>
     *   <li><p>{@linkplain MapProjection#createMathTransform MapProjection.createMathTransform}
     *   invokes the static {@code create} method of some appropriate {@link UnitaryProjection}
     *   subclass. For example both {@link org.geotoolkit.referencing.operation.provider.Mercator1SP}
     *   and {@link org.geotoolkit.referencing.operation.provider.Mercator2SP} invokes the same
     *   {@link Mercator#create Mercator.create} method, but with different descriptor.</p></li>
     *
     *   <li><p>The static factory method creates a new instance of this {@code Parameters} class
     *   with the user-supplied {@link ParameterValueGroup}. This have the effect of decoding the
     *   parameters in the given group and store their values in the corresponding {@code Parameters}
     *   fields. The parameters are then given to the class constructor.</p></li>
     *
     *   <li><p>The constructor at step 2 is free to modify the value of any field contained in
     *   the {@code Parameters} instance it got in argument. The most typical use cases are to
     *   multiply the {@linkplain #scaleFactor scale factor} by some value inferred from the
     *   {@linkplain #standardParallels standard parallels}, and to restrict the {@linkplain
     *   #centralMeridian central meridian} to the middle of a UTM or MTM zone.</p></li>
     *
     *   <li><p>When every fields have their final values, the constructor at steps 2-3 must invoke
     *   {@link #validate()}. At this point, the scalar values in {@code Parameters} should not be
     *   modified anymore. However the constructor is free to apply additional operations on the
     *   two affine transforms ({@linkplain #normalize(boolean) normalize/denormalize}) after
     *   {@code validate()} has been invoked.</p></li>
     *
     *   <li><p>Once the execution point returned to {@link MapProjection}, the affine transforms
     *   are marked as immutable and concatenated in that order:
     *   <code>{@linkplain #normalize(boolean) normalize(true)}</code>,
     *   &lt;<var>the transform created at step 2</var>&gt;,
     *   <code>{@linkplain #normalize(boolean) normalize(false)}</code>.
     *   The {@code Parameters} instance is saved for <cite>Well Known Text</cite> formatting,
     *   but is not used in the transformation chain.</p></li>
     * </ol>
     *
     * All angles in this class (either fields, method parameters or return values) are in decimal
     * degrees. This is the opposite of {@link UnitaryProjection} where all angles are in radians.
     *
     * {@note Serialization of this class is appropriate for short-term storage or RMI use, but may
     *        not be compatible with future versions. For long term storage, WKT (Well Know Text) or
     *        XML are more appropriate.}
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    protected static class Parameters extends AbstractMathTransform2D.Parameters {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = -4952134260969915530L;

        /**
         * Namespaces of identifiers to ignore because they are ambiguous. For example the
         * Proj.4 {@code "lcc"} projection name could be both <cite>Lambert Conformal 1SP</cite>
         * or <cite>Lambert Conformal 2SP</cite>, so we can not use the Proj.4 code as a criterion.
         *
         * @since 3.20
         */
        private static final Citation[] AMBIGUOUS = new Citation[] {
            Citations.GEOTOOLKIT, Citations.PROJ4
        };

        /**
         * Length of semi-major axis, in metres. This is named <var>a</var> or <var>R</var>
         * (Radius in spherical cases) in Snyder.
         *
         * @see MapProjection#SEMI_MAJOR
         * @see UnitaryProjection#excentricity
         */
        public final double semiMajor;

        /**
         * Length of semi-minor axis, in metres. This is named <var>b</var> in Snyder.
         *
         * @see MapProjection#SEMI_MINOR
         * @see UnitaryProjection#excentricity
         */
        public final double semiMinor;

        /**
         * Whatever the projection should roll longitude. If {@code true}, then the value of
         * (<var>longitude</var> - {@linkplain #centralMeridian central meridian}) will be
         * rolled to the [-180 &hellip; 180]&deg; range before the projection is applied.
         * <p>
         * This parameter may be {@code null} if the user didn't set it explicitly.
         */
        final Boolean rollLongitude;

        /**
         * Central longitude in degrees. Default value is 0, the Greenwich meridian.
         * This is named <var>λ0</var> in Snyder.
         */
        public double centralMeridian;

        /**
         * Latitude of origin in degrees. Default value is 0, the equator.
         * This is named <var>phi0</var> in Snyder.
         */
        public double latitudeOfOrigin;

        /**
         * The standard parallels, or an empty array if there is none.
         * There is typically no more than 2 standard parallels.
         */
        public final double[] standardParallels;

        /**
         * The azimuth of the central line passing through the centre of the projection,
         * in degrees. This is 0&deg; for most projections.
         */
        public double azimuth;

        /**
         * The scale factor. Default value is 1. This is named <var>k</var> in Snyder.
         */
        public double scaleFactor;

        /**
         * False easting, in metres. Default value is 0.
         */
        public double falseEasting;

        /**
         * False northing, in metres. Default value is 0.
         */
        public double falseNorthing;

        /**
         * The ESRI-specific parameters, or {@code null} if none. Those parameters
         * are {@code "X_Scale"}, {@code "Y_Scale"} and {@code "XY_Plane_Rotation"}.
         * We stores those values as an array for saving space in the common case
         * where no value is given.
         */
        private final double[] xyScaleAndRotation;

        /**
         * Creates parameters initialized to values extracted from the given parameter group.
         * The following parameters are recognized:
         * <p>
         * <ul>
         *   <li>{@code "semi_major"}          (mandatory)</li>
         *   <li>{@code "semi_minor"}          (mandatory)</li>
         *   <li>{@code "central_meridian"}    (default to 0&deg;)</li>
         *   <li>{@code "latitude_of_origin"}  (default to 0&deg;)</li>
         *   <li>{@code "standard_parallel_1"} (default to none)</li>
         *   <li>{@code "standard_parallel_2"} (default to none)</li>
         *   <li>{@code "azimuth"}             (default to 0&deg;)</li>
         *   <li>{@code "scale_factor"}        (default to 1)</li>
         *   <li>{@code "false_easting"}       (default to 0)</li>
         *   <li>{@code "false_northing"}      (default to 0)</li>
         * </ul>
         * <p>
         * Constructors of {@link UnitaryProjection} subclasses must invoke the {@link #validate()}
         * method when all parameters (especially the {@linkplain #centralMeridian central meridian}
         * and {@linkplain #scaleFactor scale factor}) are assigned their final value.
         *
         * @param  descriptor The descriptor of parameters that are legal for the projection being
         *         constructed. In theory it should be the same than {@code values.descriptors()},
         *         but projection providers should give explicitly the expected descriptors for safety.
         * @param  values The parameter values in standard units.
         * @throws ParameterNotFoundException if a mandatory parameter is missing.
         */
        public Parameters(final ParameterDescriptorGroup descriptor,
                          final ParameterValueGroup values)
                throws ParameterNotFoundException
        {
            super(descriptor);
            ensureNonNull("values", values);
            final Collection<GeneralParameterDescriptor> expected = descriptor.descriptors();
            final double standardParallel1, standardParallel2, xScale, yScale, xyPlaneRotation;
            semiMajor         = org.geotoolkit.parameter.Parameters.doubleValue(SEMI_MAJOR, values);
            semiMinor         = org.geotoolkit.parameter.Parameters.doubleValue(SEMI_MINOR, values);
            rollLongitude     = org.geotoolkit.parameter.Parameters.value(ROLL_LONGITUDE, values);
            centralMeridian   = doubleValue(expected, CENTRAL_MERIDIAN,    values);
            latitudeOfOrigin  = doubleValue(expected, LATITUDE_OF_ORIGIN,  values);
            standardParallel1 = doubleValue(expected, STANDARD_PARALLEL_1, values);
            standardParallel2 = doubleValue(expected, STANDARD_PARALLEL_2, values);
            azimuth           = doubleValue(expected, AZIMUTH,             values);
            scaleFactor       = doubleValue(expected, SCALE_FACTOR,        values);
            falseEasting      = doubleValue(expected, FALSE_EASTING,       values);
            falseNorthing     = doubleValue(expected, FALSE_NORTHING,      values);
            xScale            = doubleValue(expected, X_SCALE,             values);
            yScale            = doubleValue(expected, Y_SCALE,             values);
            xyPlaneRotation   = doubleValue(expected, XY_PLANE_ROTATION,   values);
            switch ((isNaN(standardParallel1) ? 0 : 1) | (isNaN(standardParallel2) ? 0 : 2)) {
                case  0: standardParallels = ArraysExt.EMPTY_DOUBLE; break;
                case  1: standardParallels = new double[] {standardParallel1}; break;
                case  2: standardParallels = new double[] {standardParallel2}; break;
                case  3: standardParallels = new double[] {standardParallel1, standardParallel2}; break;
                default: throw new AssertionError();
            }
            if (xScale != 1 || yScale != 1 || xyPlaneRotation != 0) {
                xyScaleAndRotation = new double[] {xScale, yScale, xyPlaneRotation};
            } else {
                xyScaleAndRotation = null;
            }
        }

        /**
         * Returns {@code true} if at least one identifier of the {@linkplain #descriptor}
         * matches the name of the given descriptor. The {@code reference} arguments must
         * be the {@code PARAMETERS} constant of one of Geotk provider implementations.
         * This method is not public because it make assumptions about the way those
         * {@code PARAMETERS} constants are constructed.
         */
        final boolean nameMatches(final ParameterDescriptorGroup reference) {
            final ParameterDescriptorGroup descriptor = getParameterDescriptors();
            for (final GenericName name : reference.getAlias()) {
                if (name instanceof Identifier) {
                    final Identifier identifier = (Identifier) name;
                    if (!ArraysExt.containsIdentity(AMBIGUOUS, identifier.getAuthority()) &&
                            IdentifiedObjects.nameMatches(descriptor, identifier.getCode()))
                    {
                        if (identifier instanceof Deprecable && ((Deprecable) identifier).isDeprecated()) {
                            /*
                             * The name matches, but is a deprecated. This case occurs with:
                             *
                             *  - Equidistant Cylindrical (Spherical)
                             *  - Lambert Azimuthal Equal Area (Spherical)
                             *
                             * which are defined twice by EPSG with different parameter names.
                             * One of the definition is deprecated and needs to be ignored, in
                             * order to allow the referencing module to pickup the correct one.
                             */
                            continue;
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Returns {@code true} if the parameters have equal semi-major and semi-minor axis.
         *
         * @return {@code true} if the parameters have equal semi-major and semi-minor axis.
         */
        final boolean isSpherical() {
            return semiMajor == semiMinor;
        }

        /**
         * Ensures that the parameters have equal semi-major and semi-minor axis. This method
         * is invoked by constructors of classes implementing only spherical formulas.
         *
         * @throws IllegalArgumentException If the {@linkplain #semiMajor semi-major} axis length
         *         is not equal to the {@linkplain #semiMinor semi-minor} axis length.
         */
        final void ensureSpherical() throws IllegalArgumentException {
            if (!isSpherical()) {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.ELLIPTICAL_NOT_SUPPORTED));
            }
        }

        /**
         * Sets the {@linkplain #normalize(boolean) normalize/denormalize} affine transforms to
         * the values contained in this object. They are the transforms to be applied before and
         * after {@link UnitaryProjection} respectively.
         * <p>
         * The default implementation defines those affine transforms as if they were performing
         * the following steps, in that order:
         * <ul>
         * <li><p><b>Normalize:</b><ol>
         *   <li>Remove the {@linkplain #centralMeridian central meridian} value from the longitude.</li>
         *   <li>Convert the ordinates from degrees to radians.</li>
         * </ol></p></li>
         * <li><p><b>Denormalize:</b><ol>
         *   <li>Multiply the ordinates by the {@linkplain #semiMajor semi-major} axis length.</li>
         *   <li>Multiply again the ordinates by the {@linkplain #scaleFactor scale factor}.</li>
         *   <li>Add the ({@linkplain #falseEasting false easting},
         *       {@linkplain #falseNorthing false northing}) offsets.</li>
         * </ol></p></li>
         * </ul>
         * <p>
         * The normalize/denormalize affine are usually identity transforms before this method is
         * invoked. If they were not, the above operations will be concatenated to their current
         * state.
         *
         * @throws IllegalArgumentException if a field has an illegal value.
         */
        public void validate() throws IllegalArgumentException {
            ensureLongitudeInRange(CENTRAL_MERIDIAN,   centralMeridian,  true);
            ensureLatitudeInRange (LATITUDE_OF_ORIGIN, latitudeOfOrigin, true);
            final AffineTransform normalize = normalize(true);
            normalize.scale(PI/180, PI/180);
            if (centralMeridian != 0) {
                /*
                 * In theory the above test is useless because -0 == 0. However Java has a notion
                 * of negative zero, and in this case we want to avoid this negative zero because
                 * we don't want it to be appears in WKT formatting of matrix elements  (negative
                 * zero is considered different than the default value because the comparison is
                 * done bitwise).
                 */
                normalize.translate(-centralMeridian, 0);
            }
            final AffineTransform denormalize = normalize(false);
            final double globalScale = scaleFactor * semiMajor;
            denormalize.translate(falseEasting, falseNorthing);
            denormalize.scale(globalScale, globalScale);
            /*
             * If there is a rotation, apply it before the false (easting,northing) translation.
             * If we applied the rotation after the translation, the later would not be toward
             * (east,north) anymore. In addition, this is consistent with the order of operation
             * performed by ObliqueMercator, which has an ESRI "XY_Plane_Rotation" parameter
             * similar to the one involved here.
             */
            if (xyScaleAndRotation != null) {
                final double r   = xyScaleAndRotation[2];
                final double qr  = r / 90;
                final int    qri = (int) qr;
                if (qr == qri) {
                    denormalize.quadrantRotate(qri);
                } else {
                    denormalize.rotate(toRadians(r));
                }
                denormalize.scale(xyScaleAndRotation[0], xyScaleAndRotation[1]);
            }
        }

        /**
         * Returns a group of parameters initialized to the values contained in this object.
         * Changes to the returned parameters will not affect this object.
         */
        @Override
        @SuppressWarnings("fallthrough")
        public ParameterValueGroup getParameterValues() {
            final ParameterDescriptorGroup descriptor = getParameterDescriptors();
            final ParameterValueGroup values = descriptor.createValue();
            final Collection<GeneralParameterDescriptor> expected = descriptor.descriptors();
            getOrCreate(SEMI_MAJOR, values).setValue(semiMajor);
            getOrCreate(SEMI_MINOR, values).setValue(semiMinor);
            if (rollLongitude != null) {
                getOrCreate(ROLL_LONGITUDE, values).setValue(rollLongitude);
            }
            set(expected, AZIMUTH,            values, azimuth         );
            set(expected, CENTRAL_MERIDIAN,   values, centralMeridian );
            set(expected, LATITUDE_OF_ORIGIN, values, latitudeOfOrigin);
            set(expected, SCALE_FACTOR,       values, scaleFactor     );
            set(expected, FALSE_EASTING,      values, falseEasting    );
            set(expected, FALSE_NORTHING,     values, falseNorthing   );
            switch (standardParallels.length) {
                default: // Fall through in all cases
                case 2:  set(expected, STANDARD_PARALLEL_2, values, standardParallels[1]);
                case 1:  set(expected, STANDARD_PARALLEL_1, values, standardParallels[0]);
                case 0:  break;
            }
            if (xyScaleAndRotation != null) {
                set(expected, X_SCALE,           values, xyScaleAndRotation[0]);
                set(expected, Y_SCALE,           values, xyScaleAndRotation[1]);
                set(expected, XY_PLANE_ROTATION, values, xyScaleAndRotation[2]);
            }
            return values;
        }

        /**
         * Returns a hash code value for this object. This value is
         * implementation-dependent and may change in any future version.
         */
        @Override
        public int hashCode() {
            return Utilities.hash(semiMajor,
                   Utilities.hash(semiMinor,
                   Utilities.hash(centralMeridian,
                   Utilities.hash(latitudeOfOrigin,
                   Utilities.hash(scaleFactor,
                   Utilities.hash(falseEasting,
                   Utilities.hash(falseNorthing,
                   Arrays.hashCode(standardParallels))))))));
        }

        /**
         * Compares the given object with the parameters for equality.
         *
         * @param  object The object to compare with the parameters.
         * @return {@code true} if the given object is equal to this one.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (super.equals(object)) {
                final Parameters that = (Parameters) object;
                return Utilities.equals(semiMajor,          that.semiMajor)          &&
                       Utilities.equals(semiMinor,          that.semiMinor)          &&
                         Objects.equals(rollLongitude,      that.rollLongitude)      &&
                       Utilities.equals(centralMeridian,    that.centralMeridian)    &&
                       Utilities.equals(latitudeOfOrigin,   that.latitudeOfOrigin)   &&
                          Arrays.equals(standardParallels,  that.standardParallels)  &&
                       Utilities.equals(azimuth,            that.azimuth)            &&
                       Utilities.equals(scaleFactor,        that.scaleFactor)        &&
                       Utilities.equals(falseEasting,       that.falseEasting)       &&
                       Utilities.equals(falseNorthing,      that.falseNorthing)      &&
                          Arrays.equals(xyScaleAndRotation, that.xyScaleAndRotation);
            }
            return false;
        }

        /**
         * Returns the parameter value for the specified operation parameter. Values are automatically
         * converted into the standard units specified by the supplied {@code param} argument.
         *
         * @param  expected The value returned by {@code descriptor.descriptors()}.
         * @param  param The parameter to look for.
         * @param  group The parameter value group to search into.
         * @return The requested parameter value, or {@code NaN} if {@code param} is
         *         {@linkplain MathTransformProvider#createOptionalDescriptor optional}
         *         and the user didn't provided any value.
         * @throws ParameterNotFoundException if the parameter is not found.
         */
        static double doubleValue(final Collection<GeneralParameterDescriptor> expected,
                final ParameterDescriptor<Double> param, final ParameterValueGroup group)
                throws ParameterNotFoundException
        {
            if (param instanceof UniversalParameters) {
                final ParameterDescriptor<?> descriptor = ((UniversalParameters) param).find(expected);
                if (descriptor != null) {
                    final double value = org.geotoolkit.parameter.Parameters.doubleValue(descriptor, group);
                    if (!isNaN(value)) {
                        return value;
                    }
                }
            } else if (expected.contains(param)) {
                /*
                 * The line above search for exactly the given descriptor, not a descriptor having
                 * the same name. This strictness is needed in order to avoid confusing a specific
                 * descriptor like XY_PLANE_ROTATION (which is not a standard parameter) with some
                 * "real" projection parameter having the same alias, like RECTIFIED_GRID_ANGLE.
                 */
                final double value = org.geotoolkit.parameter.Parameters.doubleValue(param, group);
                if (!isNaN(value)) {
                    return value;
                }
            }
            /*
             * The constructor asked for a parameter value that do not apply to the type of the
             * projection to be created. Returns a default value common to all projection types,
             * but this value should not be used in projection computations.
             */
            final Object value = param.getDefaultValue();
            return (value instanceof Number) ? ((Number) value).doubleValue() : Double.NaN;
        }

        /**
         * Ensures that the given longitude is within allowed limits (&plusmn;180&deg;). This method
         * is used for checking the validity of projection parameters like {@link #centralMeridian}.
         *
         * @param  x Longitude to verify, in degrees.
         * @param  edge {@code true} for accepting longitudes of exactly &plusmn;180&deg;.
         * @throws IllegalArgumentException if the longitude is out of range.
         */
        static void ensureLongitudeInRange(final ParameterDescriptor<? extends Number> name,
                final double x, final boolean edge) throws IllegalArgumentException
        {
            if (edge ? (x >= Longitude.MIN_VALUE  &&  x <= Longitude.MAX_VALUE) :
                       (x >  Longitude.MIN_VALUE  &&  x <  Longitude.MAX_VALUE))
            {
                return;
            }
            final String code = name.getName().getCode();
            throw new InvalidParameterValueException(Errors.format(
                    Errors.Keys.LONGITUDE_OUT_OF_RANGE_1, new Longitude(x)), code, x);
        }

        /**
         * Ensures that the latitude is within allowed limits (&plusmn;90&deg;). This method is useful
         * for checking the validity of projection parameters like {@link #latitudeOfOrigin}.
         *
         * @param  y Latitude to check, in degrees.
         * @param  edge {@code true} to accept latitudes of exactly &plusmn;90&deg;.
         * @throws IllegalArgumentException if the latitude is out of range.
         */
        static void ensureLatitudeInRange(final ParameterDescriptor<? extends Number> name,
                final double y, final boolean edge) throws IllegalArgumentException
        {
            if (edge ? (y >= Latitude.MIN_VALUE  &&  y <= Latitude.MAX_VALUE) :
                       (y >  Latitude.MIN_VALUE  &&  y <  Latitude.MAX_VALUE))
            {
                return;
            }
            final String code = name.getName().getCode();
            throw new InvalidParameterValueException(Errors.format(
                    Errors.Keys.LATITUDE_OUT_OF_RANGE_1, new Latitude(y)), code, y);
        }

        /**
         * Ensures that the absolute value of a latitude is equal to the specified value, up to the
         * tolerance value. The expected value is usually either 0 or 90&deg; (the equator or a pole).
         *
         * @param  y Latitude to check, in degrees.
         * @param  expected The expected value, in degrees.
         * @throws IllegalArgumentException if the latitude is not the expected one.
         */
        static void ensureLatitudeEquals(final ParameterDescriptor<? extends Number> name,
                final double y, final double expected) throws IllegalArgumentException
        {
            if (!(abs(abs(y) - expected) < ANGLE_TOLERANCE * (180/PI))) {
                final String code = name.getName().getCode();
                throw new InvalidParameterValueException(Errors.format(
                        Errors.Keys.ILLEGAL_ARGUMENT_2, code, new Latitude(y)), code, y);
            }
        }

        /**
         * Sets the value in a parameter group.
         *
         * @param expected  The value returned by {@code descriptor.descriptors()}.
         * @param param     One of the {@link MapProjection} provider constants.
         * @param group     The group in which to set the value.
         * @param value     The value to set.
         */
        static void set(final Collection<GeneralParameterDescriptor> expected,
                ParameterDescriptor<?> descriptor, final ParameterValueGroup group, double value)
        {
            if (descriptor instanceof UniversalParameters) {
                descriptor = ((UniversalParameters) descriptor).find(expected);
                if (descriptor == null) {
                    return;
                }
            } else if (!expected.contains(descriptor)) {
                /*
                 * The line above search for exactly the given descriptor, not a descriptor having
                 * the same name. This strictness is needed in order to avoid confusing a specific
                 * descriptor like XY_PLANE_ROTATION (which is not a standard parameter) with some
                 * "real" projection parameter having the same alias, like RECTIFIED_GRID_ANGLE.
                 */
                return;
            }
            if (descriptor.getMinimumOccurs() == 0) {
                /*
                 * Parameter is optional. Checks if its value is equals
                 * to the default one. If it is, then we will omit it.
                 */
                final Object df = descriptor.getDefaultValue();
                if (df instanceof Number) {
                    if (Utilities.equals(((Number) df).doubleValue(), value)) {
                        return;
                    }
                }
            }
            getOrCreate(descriptor, group).setValue(value);
        }
    }




    //////////////////////////////////////////////////////////////////////////////////////////
    ////////                                                                          ////////
    ////////                         METHODS FROM SUPER-CLASS                         ////////
    ////////                                                                          ////////
    //////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The parameters used for creating this projection. They are used for formatting <cite>Well
     * Known Text</cite> (WKT) and error messages. Subclasses shall not use the values defined in
     * this object for computation purpose, except at construction time.
     *
     * @level advanced
     */
    @Override
    protected final Parameters getUnmarshalledParameters() {
        return parameters;
    }

    /**
     * Returns a copy of the parameter values for this projection.
     * The default implementation returns a group of parameters with the
     * {@linkplain MapProjection#SEMI_MAJOR semi-major} axis length set to 1 and the
     * {@linkplain MapProjection#SEMI_MINOR semi-minor} axis length set to
     * <code>sqrt(1 - {@linkplain #excentricitySquared e²})</code>, which is
     * consistent with the definition of {@link UnitaryProjection}.
     *
     * @return A copy of the parameter values for this unitary projection.
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        final ParameterDescriptorGroup descriptor = getParameterDescriptors();
        final ParameterValueGroup values = descriptor.createValue();
        getOrCreate(SEMI_MAJOR, values).setValue(1.0);
        getOrCreate(SEMI_MINOR, values).setValue(sqrt(1 - excentricitySquared));
        return values;
    }

    /**
     * Returns {@code true} if this class is a {@code Spherical} nested class. This method is not
     * public because the usage of those nested classes is specific to Geotk implementation.
     * This information is used sometime for selecting formulas, and for testing purpose.
     */
    boolean isSpherical() {
        return false;
    }

    /**
     * Computes a hash code value for this unitary projection. The default implementation
     * computes a value from the parameters given at construction time.
     */
    @Override
    protected int computeHashCode() {
        return hash(parameters, super.computeHashCode());
    }

    /**
     * Compares the given object with this transform for equivalence. The default implementation
     * checks if {@code object} is an instance of the same class than {@code this}, then compares
     * the excentricity.
     * <p>
     * If this method returns {@code true}, then for any given identical source position, the
     * two compared unitary projections shall compute the same target position. Many of the
     * {@linkplain Parameters projection parameters} used for creating the unitary projections
     * are irrelevant and don't need to be known. Those projection parameters will be compared
     * only if the comparison mode is {@link ComparisonMode#STRICT STRICT} or
     * {@link ComparisonMode#BY_CONTRACT BY_CONTRACT}.
     *
     * <blockquote><font size="-1"><b>Example:</b> a {@linkplain Mercator Mercator} projection can
     * be created in the 2SP case with a {@linkplain Parameters#standardParallels standard parallel}
     * value of 60°. The same projection can also be created in the 1SP case with a {@linkplain
     * Parameters#scaleFactor scale factor} of 0.5. Nevertheless those two unitary projections
     * applied on a sphere gives identical results. Considering them as equivalent allows the
     * referencing module to transform coordinates between those two projections more
     * efficiently.</font></blockquote>
     *
     * @param object The object to compare with this unitary projection for equivalence.
     * @param mode The strictness level of the comparison. Default to {@link ComparisonMode#STRICT STRICT}.
     * @return {@code true} if the given object is equivalent to this unitary projection.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (super.equals(object, mode)) {
            final double e1, e2;
            final UnitaryProjection that = (UnitaryProjection) object;
            if (mode.ordinal() < ComparisonMode.IGNORE_METADATA.ordinal()) {
                if (!Objects.equals(parameters, that.parameters)) {
                    return false;
                }
                e1 = this.excentricitySquared;
                e2 = that.excentricitySquared;
            } else {
                e1 = this.excentricity;
                e2 = that.excentricity;
            }
            /*
             * There is no need to compare both 'excentricity' and 'excentricitySquared' since
             * the former is computed from the later. In strict comparison mode, we are better
             * to compare the 'excentricitySquared' since it is the original value from which
             * the other value is derived. However in approximative comparison mode, we need
             * to use the 'excentricity', otherwise we would need to take the square of the
             * tolerance factor before comparing 'excentricitySquared'.
             */
            return epsilonEqual(e1, e2, mode) &&
                   epsilonEqual(longitudeRotation, that.longitudeRotation, mode) &&
                   epsilonEqual(longitudeBound,    that.longitudeBound,    mode);
        }
        return false;
    }
}
