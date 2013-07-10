/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.transform;

import java.util.Arrays;
import java.io.Serializable;
import java.awt.geom.Point2D;
import net.jcip.annotations.Immutable;

import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.parameter.Parameter;
import org.geotoolkit.parameter.ParameterGroup;
import org.geotoolkit.parameter.FloatParameter;
import org.geotoolkit.referencing.operation.matrix.XMatrix;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.provider.Molodensky;
import org.geotoolkit.referencing.operation.provider.AbridgedMolodensky;

import org.apache.sis.util.ArgumentChecks;
import static java.lang.Math.*;
import static org.geotoolkit.util.Utilities.hash;


/**
 * Two- or three-dimensional datum shift using the (potentially abridged) Molodensky transformation.
 * The Molodensky transformation (EPSG code 9604) and the abridged Molodensky transformation (EPSG
 * code 9605) transform two or three dimensional geographic points from one geographic coordinate
 * reference system to another (a datum shift), using three shift parameters (delta X, delta Y,
 * delta Z) and the difference between the semi-major axis and flattenings of the two ellipsoids.
 * <p>
 * This transformation is performed directly on geographic coordinates.
 * See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.Molodensky}</li>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.AbridgedMolodensky}</li>
 * </ul>
 * <p>
 *
 * <strong>References:</strong><ul>
 *   <li> Defense Mapping Agency (DMA), Datums, Ellipsoids, Grids and Grid Reference Systems,
 *        Technical Manual 8358.1.
 *        Available from <a href="http://earth-info.nga.mil/GandG/pubs.html">http://earth-info.nga.mil/GandG/pubs.html</a></li>
 *   <li> Defense Mapping Agency (DMA), The Universal Grids: Universal Transverse
 *        Mercator (UTM) and Universal Polar Stereographic (UPS), Fairfax VA, Technical Manual 8358.2.
 *        Available from <a href="http://earth-info.nga.mil/GandG/pubs.html">http://earth-info.nga.mil/GandG/pubs.html</a></li>
 *   <li> National Imagery and Mapping Agency (NIMA), Department of Defense World
 *        Geodetic System 1984, Technical Report 8350.2.
 *        Available from <a href="http://earth-info.nga.mil/GandG/pubs.html">http://earth-info.nga.mil/GandG/pubs.html</a></li>
 *   <li> "Coordinate Conversions and Transformations including Formulas",
 *        EPSG Guidance Note Number 7, Version 19.</li>
 * </ul>
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.20
 *
 * @since 1.2
 * @module
 */
@Immutable
public class MolodenskyTransform extends AbstractMathTransform implements EllipsoidalTransform, Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7206439437113286122L;

    /**
     * The value of {@code 1/sin(1")} multiplied by the conversion
     * factor from arc-seconds to radians {@code (PI/180)/(60*60)}.
     */
    private static final double ISIN = 1.0000000000039174;

    /**
     * A mask value for {@link #type}.
     * <ul>
     *   <li>If set, the target coordinates are three-dimensional.</li>
     *   <li>If unset, the target coordinates are two-dimensional.</li>
     * </ul>
     */
    private static final int TARGET_DIMENSION_MASK = 1;

    /**
     * A mask value for {@link #type}.
     * <ul>
     *   <li>If set, the source coordinates are three-dimensional.</li>
     *   <li>If unset, the source coordinates are two-dimensional.</li>
     * </ul>
     * <p>
     * This value <strong>must</strong> be equals to {@code TARGET_DIMENSION_MASK << 1}.
     * This is required by the {@link #inverse()} method.
     */
    private static final int SOURCE_DIMENSION_MASK = 2;

    /**
     * A mask value for {@link #type}. If set, then the Molodensky transform
     * is the inverse of some previously existing Molodensky transform.
     */
    private static final int INVERSE_MASK = 4;

    /**
     * A mask value for {@link #type}.
     * <ul>
     *   <li>If set, the transform uses the abridged formulas.</li>
     *   <li>If unset, the transform uses the complete formulas.</li>
     * </ul>
     */
    private static final int ABRIDGED_MASK = 8;

    /**
     * The mask of relevant bits to keep when using the {@link #type} numerical value as
     * index in the {@link #variants} array. We discard {@link #ABRIDGED_MASK} because we
     * don't provide an API for building this kind of variants from an existing transform.
     */
    private static final int VARIANT_MASK = SOURCE_DIMENSION_MASK | TARGET_DIMENSION_MASK | INVERSE_MASK;

    /**
     * Bitwise combination of the {@code *_MASK} constants. This is also
     * the index of this transform in the {@link #variants} array.
     */
    private final int type;

    /**
     * X,Y,Z shift in meters.
     */
    final double dx, dy, dz;

    /**
     * Semi-major (<var>a</var>) semi-minor (<var>b/<var>) axes length of the source ellipsoid,
     * in metres.
     */
    final double a, b;

    /**
     * Difference in the semi-major ({@code da = target a - source a}) and semi-minor
     * ({@code db = target b - source b}) axes of the target and source ellipsoids.
     */
    final double da, db;

    /**
     * Difference between the flattening ({@code df = target f - source f})
     * of the target and source ellipsoids.
     */
    private final double df;

    /**
     * Ratio of the Semi-major (<var>a</var>) semi-minor (<var>b/<var>) axis
     * values ({@code a_b = a/b} and {@code b_a = b/a}).
     */
    private final double b_a, a_b;

    /**
     * Some more constants ({@code daa = da*a} and {@code da_a = da/a}).
     */
    private final double daa, da_a;

    /**
     * The square of eccentricity of the ellipsoid: e² = (a²-b²)/a² where
     * <var>a</var> is the semi-major axis length and
     * <var>b</var> is the semi-minor axis length.
     */
    private final double e2;

    /**
     * Defined as {@code (a*df) + (f*da)}.
     */
    private final double adf;

    /**
     * The variants for different number of dimensions, and for inverse transform. Will be computed
     * by {@link #forDimensions(boolean, boolean)} and {@link #inverse()} only when first needed.
     *
     * @see #variants()
     *
     * @since 3.16
     */
    private transient MolodenskyTransform[] variants;

    /**
     * Constructs a Molodensky transform from the specified parameters.
     * This constructor is for subclasses only; client code should use
     * the {@link #create create} static method instead.
     * <p>
     * <strong>WARNING:</strong> Current implementation expects longitude and latitude ordinates
     * in decimal degrees, but it may be changed to radians in a future version. The static factory
     * method will preserve the decimal degrees contract.
     *
     * @param abridged {@code true} for the abridged formula, or {@code false} for the complete one.
     * @param sa       The source semi-major axis length in meters.
     * @param sb       The source semi-minor axis length in meters.
     * @param source3D {@code true} if the source has a height.
     * @param ta       The target semi-major axis length in meters.
     * @param tb       The target semi-minor axis length in meters.
     * @param target3D {@code true} if the target has a height.
     * @param dx       The <var>x</var> translation in meters.
     * @param dy       The <var>y</var> translation in meters.
     * @param dz       The <var>z</var> translation in meters.
     *
     * @see #create(boolean, double, double, boolean, double, double, boolean, double, double, double)
     */
    protected MolodenskyTransform(final boolean abridged,
            final double sa, final double sb, final boolean source3D,
            final double ta, final double tb, final boolean target3D,
            final double dx, final double dy, final double  dz)
    {
        int type = abridged ? ABRIDGED_MASK : 0;
        if (source3D) type |= SOURCE_DIMENSION_MASK;
        if (target3D) type |= TARGET_DIMENSION_MASK;
        this.type = type;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;

        a     =  sa;
        b     =  sb;
        da    =  ta - sa;
        db    =  tb - sb;
        a_b   =  sa / sb;
        b_a   =  sb / sa;
        daa   =  da * sa;
        da_a  =  da / sa;
        df    =  (ta-tb)/ta - (sa-sb)/sa;
        e2    =  1 - (sb*sb) / (sa*sa);
        adf   =  (sa*df) + (sa-sb)*(da/sa);
    }

    /**
     * Creates a new transform with the same ellipsoidal and Bursa-Wolf parameters than the given
     * transform. The formula (abridged or complete) and the number of dimensions can be different.
     *
     * @param original The transform to copy.
     * @param abridged {@code true} for the abridged formula, or {@code false} for the complete one.
     * @param source3D {@code true} if the source has a height.
     * @param target3D {@code true} if the target has a height.
     *
     * @since 3.16
     */
    protected MolodenskyTransform(final MolodenskyTransform original,
            final boolean abridged, final boolean source3D, final boolean target3D)
    {
        int type = original.type & INVERSE_MASK;
        if (abridged) type |= ABRIDGED_MASK;
        if (source3D) type |= SOURCE_DIMENSION_MASK;
        if (target3D) type |= TARGET_DIMENSION_MASK;
        this.type = type;
        dx   = original.dx;
        dy   = original.dy;
        dz   = original.dz;
        da   = original.da;
        db   = original.db;
        df   = original.df;
        a    = original.a;
        b    = original.b;
        a_b  = original.a_b;
        b_a  = original.b_a;
        daa  = original.daa;
        da_a = original.da_a;
        e2   = original.e2;
        adf  = original.adf;
    }

    /**
     * Creates the inverse of the given Molodensky transform. It is caller
     * responsibility to update the {@link #variants} array after construction.
     *
     * @param direct The transform for which to create the inverse transform.
     * @param type   The value to assign to {@link #type} (computed by {@link #inverse()}).
     */
    MolodenskyTransform(final MolodenskyTransform direct, final int type) {
        this.type = type;
        dx   = -direct.dx;
        dy   = -direct.dy;
        dz   = -direct.dz;
        da   = -direct.da;
        db   = -direct.db;
        df   = -direct.df;
        a    =  direct.a + direct.da;
        b    =  direct.b + direct.db;
        a_b  =  a / b;
        b_a  =  b / a;
        daa  = da * a;
        da_a = da / a;
        e2   = 1 - (b*b)/(a*a);
        adf  = (a*df) + (a-b)*(da/a);
    }

    /**
     * Constructs a transform from the specified Molodensky parameters. The returned transform
     * works on (<var>longitude</var>, <var>latitude</var>, <var>height</var>) coordinates
     * where the longitudes and latitudes are in <em>decimal degrees</em>, and the height is
     * optional (depending on the value of the {@code source3D} and {@code target3D} arguments).
     *
     * @param abridged {@code true} for the abridged formula, or {@code false} for the complete one.
     * @param a        The source semi-major axis length in meters.
     * @param b        The source semi-minor axis length in meters.
     * @param source3D {@code true} if the source has a height.
     * @param ta       The target semi-major axis length in meters.
     * @param tb       The target semi-minor axis length in meters.
     * @param target3D {@code true} if the target has a height.
     * @param dx       The <var>x</var> translation in meters.
     * @param dy       The <var>y</var> translation in meters.
     * @param dz       The <var>z</var> translation in meters.
     * @return A transform for the given parameters.
     *
     * @since 3.00
     */
    public static MathTransform create(final boolean abridged,
            final double  a, final double  b, final boolean source3D,
            final double ta, final double tb, final boolean target3D,
            final double dx, final double dy, final double  dz)
    {
        final MathTransform transform;
        if (dx == 0 && dy == 0 && dz == 0 && a == ta && b == tb) {
            // Special case for identity transform.
            if (source3D == target3D) {
                transform = ProjectiveTransform.identity(target3D ? 3 : 2);
            } else {
                final XMatrix matrix = Matrices.createDimensionFilter(3, new int[] {0,1});
                if (target3D) {
                    matrix.transpose();
                }
                transform = ProjectiveTransform.create(matrix);
            }
        } else if (!source3D && !target3D) {
            transform = new MolodenskyTransform2D(abridged, a, b, ta, tb, dx, dy, dz);
        } else {
            transform = new MolodenskyTransform(abridged, a, b, source3D, ta, tb, target3D, dx, dy, dz);
        }
        return transform;
    }

    /**
     * Returns the parameter descriptors for this math transform.
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return isAbridged() ? AbridgedMolodensky.PARAMETERS : Molodensky.PARAMETERS;
    }

    /**
     * Returns the parameters for this math transform.
     *
     * @return The parameters for this math transform.
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        final ParameterValue<Integer> dim = new Parameter<>(Molodensky.DIM);
        dim.setValue(getSourceDimensions());
        return new ParameterGroup(getParameterDescriptors(),
                   dim,
                   new FloatParameter(Molodensky.DX,             dx),
                   new FloatParameter(Molodensky.DY,             dy),
                   new FloatParameter(Molodensky.DZ,             dz),
                   new FloatParameter(Molodensky.SRC_SEMI_MAJOR, a),
                   new FloatParameter(Molodensky.SRC_SEMI_MINOR, b),
                   new FloatParameter(Molodensky.TGT_SEMI_MAJOR, a + da),
                   new FloatParameter(Molodensky.TGT_SEMI_MINOR, b + db));
    }

    /**
     * Returns the {@link #variants} array, creating it if necessary.
     */
    private synchronized MolodenskyTransform[] variants() {
        if (variants == null) {
            variants = new MolodenskyTransform[VARIANT_MASK + 1];
            variants[type & VARIANT_MASK] = this;
        }
        return variants;
    }

    /**
     * Returns a transform having the same ellipsoidal and Bursa-Wolf parameters than
     * this transform, but a different number of source or target dimensions.
     *
     * @since 3.16
     */
    @Override
    public MolodenskyTransform forDimensions(final boolean source3D, final boolean target3D) {
        final MolodenskyTransform[] variants = variants();
        final boolean abridged = isAbridged();
        int index = type & INVERSE_MASK;
        if (source3D) index |= SOURCE_DIMENSION_MASK;
        if (target3D) index |= TARGET_DIMENSION_MASK;
        MolodenskyTransform variant;
        synchronized (variants) {
            variant = variants[index];
            if (variant == null) {
                variant = (index & (SOURCE_DIMENSION_MASK | TARGET_DIMENSION_MASK)) == 0
                        ? new MolodenskyTransform2D(this, abridged)
                        : new MolodenskyTransform(this, abridged, source3D, target3D);
                variant.variants = variants;
                variants[index] = variant;
            }
        }
        return variant;
    }

    /**
     * Gets the dimension of input points.
     */
    @Override
    public final int getSourceDimensions() {
        return (type & SOURCE_DIMENSION_MASK) != 0 ? 3 : 2;
    }

    /**
     * Gets the dimension of output points.
     */
    @Override
    public final int getTargetDimensions() {
        return (type & TARGET_DIMENSION_MASK) != 0 ? 3 : 2;
    }

    /**
     * Transforms a single coordinate in a list of ordinal values, and optionally returns
     * the derivative at that location.
     *
     * @since 3.20 (derived from 3.00)
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff,
                            final boolean derivate)
    {
        Matrix derivative = null;
        if (derivate) {
            final boolean source3D = (type & SOURCE_DIMENSION_MASK) != 0;
            derivative = derivative(
                    toRadians (srcPts[srcOff  ]),     // λ: Longitude
                    toRadians (srcPts[srcOff+1]),     // φ: Latitude
                    source3D ? srcPts[srcOff+2] : 0); // h: Height above the ellipsoid (m)
        }
        if (dstPts != null) {
            transform(null, srcPts, srcOff, null, dstPts, dstOff, 1, srcPts == dstPts);
        }
        return derivative;
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    @Override
    public void transform(double[] srcPts, int srcOff,
                          double[] dstPts, int dstOff, int numPts)
    {
        transform(null, srcPts, srcOff, null, dstPts, dstOff, numPts, srcPts == dstPts);
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    @Override
    public void transform(final float[] srcPts, int srcOff,
                          final float[] dstPts, int dstOff, int numPts)
    {
        transform(srcPts, null, srcOff, dstPts, null, dstOff, numPts, srcPts == dstPts);
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    @Override
    public void transform(double[] srcPts, int srcOff,
                          float [] dstPts, int dstOff, int numPts)
    {
        transform(null, srcPts, srcOff, dstPts, null, dstOff, numPts, false);
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    @Override
    public void transform(float [] srcPts, int srcOff,
                          double[] dstPts, int dstOff, int numPts)
    {
        transform(srcPts, null, srcOff, null, dstPts, dstOff, numPts, false);
    }

    /**
     * Implementation of the transformation methods for all cases.
     *
     * Note: if we change the implementation in order to use concatenated transform as documented
     *       in the constructor javadoc, don't forget to update the "roll longitude" part of the
     *       {@link #maxError} method.
     */
    private void transform(float[] srcPts1, double[] srcPts2, int srcOff,
                           float[] dstPts1, double[] dstPts2, int dstOff,
                           int numPts, final boolean askStrategy)
    {
        final boolean abridged = (type & ABRIDGED_MASK)         != 0;
        final boolean source3D = (type & SOURCE_DIMENSION_MASK) != 0;
        final boolean target3D = (type & TARGET_DIMENSION_MASK) != 0;
        int srcDecrement = 0;
        int dstDecrement = 0;
        int offFinal     = 0;
        Object dstFinal  = null;
        if (askStrategy) {
            final int srcDim = getSourceDimensions();
            final int dstDim = getTargetDimensions();
            switch (IterationStrategy.suggest(srcOff, srcDim, dstOff, dstDim, numPts)) {
                case ASCENDING: {
                    break;
                }
                case DESCENDING: {
                    srcOff += (numPts-1) * srcDim;
                    dstOff += (numPts-1) * dstDim;
                    srcDecrement = 2 * srcDim;
                    dstDecrement = 2 * dstDim;
                    break;
                }
                default: // Below is a reasonable default for unknown cases.
                case BUFFER_SOURCE: {
                    final int upper = srcOff + numPts*srcDim;
                    if (srcPts2 != null) {
                        srcPts2 = Arrays.copyOfRange(srcPts2, srcOff, upper);
                    } else {
                        srcPts1 = Arrays.copyOfRange(srcPts1, srcOff, upper);
                    }
                    srcOff = 0;
                    break;
                }
                case BUFFER_TARGET: {
                    if (dstPts2 != null) {
                        dstFinal = dstPts2;
                        dstPts2 = new double[numPts * dstDim];
                    } else {
                        dstFinal = dstPts1;
                        dstPts1 = new float[numPts * dstDim];
                    }
                    offFinal = dstOff;
                    dstOff = 0;
                    break;
                }
            }
        }
        while (--numPts >= 0) {
            double λ,φ,h;
            if (srcPts2 != null) {
                λ =              srcPts2[srcOff++];
                φ =              srcPts2[srcOff++];
                h = (source3D) ? srcPts2[srcOff++] : 0.0;
            } else {
                λ =              srcPts1[srcOff++];
                φ =              srcPts1[srcOff++];
                h = (source3D) ? srcPts1[srcOff++] : 0.0;
            }
            λ = toRadians(λ);
            φ = toRadians(φ);
            final double sinλ  = sin(λ);
            final double cosλ  = cos(λ);
            final double sinφ  = sin(φ);
            final double cosφ  = cos(φ);
            final double sin2φ = sinφ * sinφ;
            final double csλ   = dy*cosλ - dx*sinλ;
            final double Rn    = a / sqrt(1 - e2*sin2φ);
            final double Rm    = Rn * (1 - e2) / (1 - e2*sin2φ);
            final double csφ   = dz*cosφ - sinφ*(dy*sinλ + dx*cosλ);
            if (abridged) {
                φ += ISIN * ((csφ + adf*sin(2*φ)) / Rm);
                λ += ISIN * (csλ / (Rn*cosφ));
            } else {
                φ += ISIN * ((csφ + da_a*(Rn*e2*sinφ*cosφ) + df*(Rm*(a_b) + Rn*(b_a))*sinφ*cosφ) / (Rm + h));
                λ += ISIN * (csλ / ((Rn + h)*cosφ));
            }
            // stay within latitude +-90 deg. and longitude +-180 deg.
            if (abs(φ) >= PI/2) {
                λ = 0;
                φ = copySign(90, φ);
            } else {
                λ = rollLongitude(toDegrees(λ), 180);
                φ = toDegrees(φ);
            }
            if (dstPts2 != null) {
                dstPts2[dstOff++] = λ;
                dstPts2[dstOff++] = φ;
            } else {
                dstPts1[dstOff++] = (float) λ;
                dstPts1[dstOff++] = (float) φ;
            }
            if (target3D) {
                if (abridged) {
                    h += dx*cosφ*cosλ + dy*cosφ*sinλ + dz*sinφ + adf*sin2φ - da;
                } else {
                    h += dx*cosφ*cosλ + dy*cosφ*sinλ + dz*sinφ + df*(b_a)*Rn*sin2φ - daa/Rn;
                }
                if (dstPts2 != null) {
                    dstPts2[dstOff++] = h;
                } else {
                    dstPts1[dstOff++] = (float) h;
                }
            }
            srcOff -= srcDecrement;
            dstOff -= dstDecrement;
        }
        /*
         * If the transformation result has been stored in a temporary
         * array, copies the array content to its final location now.
         */
        if (dstFinal != null) {
            final Object source;
            final int length;
            if (dstPts2 != null) {
                source = dstPts2;
                length = dstPts2.length;
            } else {
                source = dstPts1;
                length = dstPts1.length;
            }
            System.arraycopy(source, 0, dstFinal, offFinal, length);
        }
    }

    /**
     * Computes the derivative at the given position.
     */
    @Override
    public Matrix derivative(final DirectPosition point) {
        final boolean source3D = (type & SOURCE_DIMENSION_MASK) != 0;
        ArgumentChecks.ensureDimensionMatches("point", source3D ? 3 : 2, point);
        return derivative(
                toRadians (point.getOrdinate(0)),     // λ: Longitude
                toRadians (point.getOrdinate(1)),     // φ: Latitude
                source3D ? point.getOrdinate(2) : 0); // h: Height above the ellipsoid (m)
    }

    /**
     * Computes the derivative at the given position, assuming a height of zero.
     */
    @Override
    public Matrix derivative(final Point2D point) {
        return derivative(point.getX(), point.getY(), 0);
    }

    /**
     * Gets the derivative of this transform at a point.
     */
    private Matrix derivative(final double λ, final double φ, final double h) {
        final double cosλ    = cos(λ);
        final double sinλ    = sin(λ);
        final double cosφ    = cos(φ);
        final double sinφ    = sin(φ);
        final double tanφ    = sinφ / cosφ;
        final double sincosφ = sinφ * cosφ;
        final double sinφ2   = sinφ * sinφ;
        final double scλ     = dy*sinλ + dx*cosλ;
        final double csλ     = dy*cosλ - dx*sinλ;
        final double e2sinφ2 = 1 - e2*sinφ2;
        final double Rn      = a / sqrt(e2sinφ2);
        final double dRn     = e2*sincosφ / e2sinφ2;
        final double Rm      = (1 - e2) / e2sinφ2; // Multiplication by Rn omitted.
        final double dRn3Rm  = 3*Rm*dRn;

        final int srcDim = getSourceDimensions();
        final int tgtDim = getTargetDimensions();
        final XMatrix matrix = Matrices.create(tgtDim, srcDim);

        // The following are "almost" the derivatives to be returned.
        // Some final operation commons to both kind of formulas will
        // be applied in the call to Matrix3 constructor.
        final double dXdλ, dXdφ, dYdλ, dYdφ;
        if (isAbridged()) {
            final double IRnm = ISIN / (Rn*Rm);
            final double IRnφ = ISIN / (Rn*cosφ);
            dXdλ =  IRnφ *  scλ;
            dXdφ =  IRnφ *  csλ*(tanφ - dRn);
            dYdλ = -IRnm * (csλ*sinφ);
            dYdφ =  IRnm * (scλ*(sinφ*dRn3Rm - cosφ) - dz*(cosφ*dRn3Rm + sinφ) + 2*adf*(1 - sincosφ*dRn3Rm - 2*sinφ2));
            // dXdh = dYdh = 0;
            if (tgtDim == 3) {
                final double dZdλ = cosφ*csλ;
                final double dZdφ = cosφ * (dz + 2*adf*sinφ) - sinφ*scλ;
                matrix.setElement(2, 0, toRadians(dZdλ));
                matrix.setElement(2, 1, toRadians(dZdφ));
            }
        } else {
            final double h_Rn   = h + Rn;
            final double h_Rm   = h + Rm*Rn;
            final double IRnm   = ISIN / (h_Rm);
            final double IRnφ   = ISIN / (h_Rn*cosφ);
            final double dRmh   = dRn3Rm * Rn / h_Rm;
            final double sar   = Rm*a_b + b_a;
            final double e2rd   = e2 * da_a/df;
            final double df_exp = df * (e2rd + sar)*Rn;
            dXdλ =  IRnφ * scλ;
            dXdφ =  IRnφ * csλ * (tanφ - dRn*Rn/h_Rn);
            dYdλ = -IRnm * csλ *  sinφ;
            dYdφ =  IRnm * (scλ*(dRmh*sinφ - cosφ) - dz*(dRmh*cosφ + sinφ) + df_exp*(1 - 2*sinφ2) +
                            df*Rn*sincosφ*(dRn*(2*a_b + sar + e2rd) + e2rd - dRmh*sar));
            if (srcDim == 3) {
                final double dXdh = -IRnφ * (csλ) / h_Rn;
                final double dYdh =  IRnm * (scλ*sinφ - dz*cosφ - df_exp*sincosφ) / h_Rm;
                matrix.setElement(0, 2, dXdh);
                matrix.setElement(1, 2, dYdh);
            }
            if (tgtDim == 3) {
                final double dZdλ = cosφ*csλ;
                final double dZdφ = sinφ * (df*Rn*b_a*(dRn*sinφ + 2*cosφ) - scλ) + dz*cosφ + daa*dRn/Rn;
                matrix.setElement(2, 0, toRadians(dZdλ));
                matrix.setElement(2, 1, toRadians(dZdφ));
            }
        }
        matrix.setElement(0, 0, 1 - dXdλ);
        matrix.setElement(1, 1, 1 + dYdφ);
        matrix.setElement(0, 1,     dXdφ);
        matrix.setElement(1, 0,     dYdλ);
        return matrix;
    }

    /**
     * Returns {@code true} if this Molodensky transform uses abridged formulas
     * instead than the complete ones. This is the value of the {@code abridged}
     * boolean argument given to the constructor.
     *
     * @return {@code true} if this transform uses abridged formulas.
     *
     * @since 3.16
     */
    public final boolean isAbridged() {
        return (type & ABRIDGED_MASK) != 0;
    }

    /**
     * Returns {@code true} if this transform is the identity one.
     * This transform is considered identity (minus rounding errors) if:
     * <p>
     * <ul>
     *   <li>the X,Y,Z shift are zero</li>
     *   <li>the source and target axis length are the same</li>
     *   <li>the input and output dimension are the same.</li>
     * </ul>
     *
     * @since 2.5
     */
    @Override
    public boolean isIdentity() {
        return dx == 0 && dy == 0 && dz == 0 && da == 0 && db == 0 &&
                getSourceDimensions() == getTargetDimensions();
    }

    /**
     * Creates the inverse transform of this object.
     */
    @Override
    public MathTransform inverse() {
        /*
         * We need to interchange the number of source and the number of target dimensions.
         * The last bits of the 'dm' variable below will be set to "00" if the source and
         * target dimensions are the same (so no swapping is required), or "11" if they
         * differ. The last XOR compute the type of the inverse transform.
         */
        int id = type;
        id = ((id >>> 1) ^ id) & TARGET_DIMENSION_MASK;
        id |= (id << 1) | INVERSE_MASK;
        id ^= type;
        final int index = id & VARIANT_MASK;
        final MolodenskyTransform[] variants = variants();
        MolodenskyTransform inverse;
        synchronized (variants) {
            inverse = variants[index];
            if (inverse == null) {
                inverse = (index & (SOURCE_DIMENSION_MASK | TARGET_DIMENSION_MASK)) == 0 ?
                        new MolodenskyTransform2D(this, id) : new MolodenskyTransform(this, id);
                inverse.variants = variants;
                variants[index] = inverse;
            }
        }
        return inverse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(dx, hash(dy, hash(dz, hash(a, hash(b, hash(da, hash(db, type)))))));
    }

    /**
     * Compares the specified object with this math transform for equality.
     */
    @Override
    public final boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object, mode)) {
            final MolodenskyTransform that = (MolodenskyTransform) object;
            return this.type == that.type &&
                   Utilities.equals(this.dx, that.dx) &&
                   Utilities.equals(this.dy, that.dy) &&
                   Utilities.equals(this.dz, that.dz) &&
                   Utilities.equals(this.a,  that.a)  &&
                   Utilities.equals(this.b,  that.b)  &&
                   Utilities.equals(this.da, that.da) &&
                   Utilities.equals(this.db, that.db);
        }
        return false;
    }
}
