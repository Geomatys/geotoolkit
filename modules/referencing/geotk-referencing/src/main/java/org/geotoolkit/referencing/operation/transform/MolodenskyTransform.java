/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.transform;

import java.util.Arrays;
import java.io.Serializable;
import static java.lang.Math.*;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform;

import org.geotoolkit.lang.Immutable;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.parameter.Parameter;
import org.geotoolkit.parameter.ParameterGroup;
import org.geotoolkit.parameter.FloatParameter;
import org.geotoolkit.referencing.operation.matrix.XMatrix;
import org.geotoolkit.referencing.operation.provider.Molodensky;
import org.geotoolkit.referencing.operation.provider.AbridgedMolodensky;


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
 *        EPSG Guidence Note Number 7, Version 19.</li>
 * </ul>
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
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
     * The tolerance error for assertions, in decimal degrees. A value of 0.002° is more than
     * 200 metres. This is quite high, but we don't want the assertion to be too intrusive.
     */
    private static final float TOLERANCE = 0.002f;

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
     * The square of excentricity of the ellipsoid: e² = (a²-b²)/a² where
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
                transform = IdentityTransform.create(target3D ? 3 : 2);
            } else {
                final XMatrix matrix = ProjectiveTransform.createSelectMatrix(3, new int[] {0,1});
                if (target3D) {
                    matrix.transpose();
                }
                transform = ProjectiveTransform.create(matrix);
            }
        } else if (!source3D && !target3D) {
            transform = new MolodenskyTransform2D(abridged, a, b, ta, tb, dx, dy, dz);
            assert !transform.isIdentity();
        } else {
            transform = new MolodenskyTransform(abridged, a, b, source3D, ta, tb, target3D, dx, dy, dz);
            assert !transform.isIdentity();
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
        final ParameterValue<Integer> dim = new Parameter<Integer>(Molodensky.DIM);
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
     * Transforms a single coordinate point.
     */
    @Override
    protected void transform(double[] srcPts, int srcOff,
                             double[] dstPts, int dstOff)
    {
        transform(null, srcPts, srcOff, null, dstPts, dstOff, 1, srcPts == dstPts);
        /*
         * Assertions: computes the inverse transform in the 3D-case only
         *             (otherwise the transform is too approximative).
         *
         * NOTE: The expression below executes 'maxError' *only* if assertions are enabled and the
         * conditions before 'maxError' are meet. Do not factor the call to 'maxError' outside the
         * 'assert' statement, otherwise it would be executed everytime and would hurt performance
         * for normal operations (instead of slowing down during debugging only).
         */
        final float error;
        assert (srcPts == dstPts) || // Following assertion can not be performed if the arrays are the same.
               (error = maxError(null, srcPts, srcOff, null, dstPts, dstOff, 1)) <= TOLERANCE : error;
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    @Override
    public void transform(double[] srcPts, int srcOff,
                          double[] dstPts, int dstOff, int numPts)
    {
        transform(null, srcPts, srcOff, null, dstPts, dstOff, numPts, srcPts == dstPts);
        final float error;
        assert (srcPts == dstPts) || // Following assertion can not be performed if the arrays are the same.
               (error = maxError(null, srcPts, srcOff, null, dstPts, dstOff, numPts)) <= TOLERANCE : error;
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    @Override
    public void transform(final float[] srcPts, int srcOff,
                          final float[] dstPts, int dstOff, int numPts)
    {
        transform(srcPts, null, srcOff, dstPts, null, dstOff, numPts, srcPts == dstPts);
        final float error;
        assert (srcPts == dstPts) || // Following assertion can not be performed if the arrays are the same.
               (error = maxError(srcPts, null, srcOff, dstPts, null, dstOff, numPts)) <= TOLERANCE : error;
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    @Override
    public void transform(double[] srcPts, int srcOff,
                          float [] dstPts, int dstOff, int numPts)
    {
        transform(null, srcPts, srcOff, dstPts, null, dstOff, numPts, false);
        final float error;
        assert (error = maxError(null, srcPts, srcOff, dstPts, null, dstOff, numPts)) <= TOLERANCE : error;
    }

    /**
     * Transforms a list of coordinate point ordinal values.
     */
    @Override
    public void transform(float [] srcPts, int srcOff,
                          double[] dstPts, int dstOff, int numPts)
    {
        transform(srcPts, null, srcOff, null, dstPts, dstOff, numPts, false);
        final float error;
        assert (error = maxError(srcPts, null, srcOff, null, dstPts, dstOff, numPts)) <= TOLERANCE : error;
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
            double x,y,z;
            if (srcPts2 != null) {
                x =              srcPts2[srcOff++];
                y =              srcPts2[srcOff++];
                z = (source3D) ? srcPts2[srcOff++] : 0.0;
            } else {
                x =              srcPts1[srcOff++];
                y =              srcPts1[srcOff++];
                z = (source3D) ? srcPts1[srcOff++] : 0.0;
            }
            x = toRadians(x);
            y = toRadians(y);
            final double sinX = sin(x);
            final double cosX = cos(x);
            final double sinY = sin(y);
            final double cosY = cos(y);
            final double sin2Y = sinY * sinY;
            final double Rn = a / sqrt(1 - e2*sin2Y);
            final double Rm = Rn * (1 - e2) / (1 - e2*sin2Y);
            if (abridged) {
                y += ISIN * ((dz*cosY - sinY*(dy*sinX + dx*cosX) + adf*sin(2*y)) / Rm);
                x += ISIN * ((dy*cosX - dx*sinX) / (Rn*cosY));
            } else {
                y += ISIN * ((dz*cosY - sinY*(dy*sinX + dx*cosX) + da_a*(Rn*e2*sinY*cosY) +
                              df*(Rm*(a_b) + Rn*(b_a))*sinY*cosY) / (Rm + z));
                x += ISIN * ((dy*cosX - dx*sinX) / ((Rn + z)*cosY));
            }
            // stay within latitude +-90 deg. and longitude +-180 deg.
            if (abs(y) >= PI/2) {
                x = 0;
                y = copySign(90, y);
            } else {
                x = rollLongitude(toDegrees(x), 180);
                y = toDegrees(y);
            }
            if (dstPts2 != null) {
                dstPts2[dstOff++] = x;
                dstPts2[dstOff++] = y;
            } else {
                dstPts1[dstOff++] = (float) x;
                dstPts1[dstOff++] = (float) y;
            }
            if (target3D) {
                if (abridged) {
                    z += dx*cosY*cosX + dy*cosY*sinX + dz*sinY + adf*sin2Y - da;
                } else {
                    z += dx*cosY*cosX + dy*cosY*sinX + dz*sinY + df*(b_a)*Rn*sin2Y - daa/Rn;
                }
                if (dstPts2 != null) {
                    dstPts2[dstOff++] = z;
                } else {
                    dstPts1[dstOff++] = (float) z;
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
     * After a call to {@code transform}, applies the <em>inverse</em> transform on {@code dstPts}
     * and compares the result with {@code srcPts}. The maximal difference is returned. This method
     * is used for assertions only.
     *
     * @return The maximal error in decimal degrees, or 0 if it can not be computed.
     */
    private float maxError(final float[] srcPts1, final double[] srcPts2, int srcOff,
                           final float[] dstPts1, final double[] dstPts2, int dstOff, int numPts)
    {
        float max = 0f;
        if (getTargetDimensions() == 3) {
            final MathTransform inverse = inverse();
            // We will perform the test only for MolodenskyTransform and MolodenskyTransform2D.
            if (inverse.getClass().getName().startsWith(MolodenskyTransform.class.getName())) {
                final int sourceDim = getSourceDimensions();
                final float[] tmp = new float[numPts * sourceDim];
                ((MolodenskyTransform) inverse).transform(dstPts1, dstPts2, dstOff, tmp, null, 0, numPts, false);
                for (int i=0; i<tmp.length; i++,srcOff++) {
                    final float expected = (srcPts2 != null) ? (float) srcPts2[srcOff] : srcPts1[srcOff];
                    float error = abs(tmp[i] - expected);
                    switch (i % sourceDim) {
                        case 0: error -= 360 * floor(error / 360); break; // Rool Longitude
                        case 2: continue; // Ignore height because inacurate.
                    }
                    if (error > max) {
                        max = error;
                    }
                }
            }
        }
        return max;
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
     * Returns a hash value for this transform.
     */
    @Override
    public final int hashCode() {
        return Utilities.hash(dx,
               Utilities.hash(dy,
               Utilities.hash(dz,
               Utilities.hash(a,
               Utilities.hash(b,
               Utilities.hash(da,
               Utilities.hash(db, type)))))));
    }

    /**
     * Compares the specified object with this math transform for equality.
     */
    @Override
    public final boolean equals(final Object object) {
        if (object == this) {
            // Slight optimization
            return true;
        }
        if (super.equals(object)) {
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
