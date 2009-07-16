/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import static java.lang.Double.doubleToLongBits;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform;

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
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 1.2
 * @module
 */
public class MolodenskyTransform extends AbstractMathTransform implements Serializable {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = 7536566033885338422L;

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
     * {@code true} for the abridged formula, or {@code false} for the complete version.
     */
    final boolean abridged;

    /**
     * {@code true} for a 3D transformation, or
     * {@code false} for a 2D transformation.
     */
    private final boolean source3D, target3D;

    /**
     * X,Y,Z shift in meters.
     */
    final double dx, dy, dz;

    /**
     * Semi-major (<var>a</var>) semi-minor (<var>b/<var>) radius in meters.
     */
    final double a, b;

    /**
     * Difference in the semi-major ({@code da = target a - source a}) and semi-minor
     * ({@code db = target b - source b}) axes of the target and source ellipsoids.
     */
    final double da, db;

    /**
     * Difference between the flattenings ({@code df = target f - source f})
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
     * The inverse of this transform. Will be created only when first needed.
     */
    transient MolodenskyTransform inverse;

    /**
     * Constructs a Molodensky transform from the specified parameters.
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
     */
    protected MolodenskyTransform(final boolean abridged,
            final double sa, final double sb, final boolean source3D,
            final double ta, final double tb, final boolean target3D,
            final double dx, final double dy, final double  dz)
    {
        this.abridged = abridged;
        this.source3D = source3D;
        this.target3D = target3D;
        this.dx       = dx;
        this.dy       = dy;
        this.dz       = dz;
        this.a        = sa;
        this.b        = sb;

        da    =  ta - sa;
        db    =  tb - sb;
        a_b   =  sa / sb;
        b_a   =  sb / sa;
        daa   =  da * sa;
        da_a  =  da / sa;
        df    =  (ta-tb)/ta - (sa-sb)/sa;
        e2    =  1 - (sb*sb)/(sa*sa);
        adf   =  (sa*df) + (sa-sb)*da/sa;
    }

    /**
     * Creates the inverse of the given Molodenski transform.
     *
     * @param direct The transform for which to create the inverse transform.
     */
    MolodenskyTransform(final MolodenskyTransform direct) {
        abridged =  direct.abridged;
        source3D =  direct.target3D;
        target3D =  direct.source3D;
        dx       = -direct.dx;
        dy       = -direct.dy;
        dz       = -direct.dz;
        da       = -direct.da;
        db       = -direct.db;
        df       = -direct.df;
        a        =  direct.a + direct.da;
        b        =  direct.b + direct.db;
        a_b      =  a / b;
        b_a      =  b / a;
        daa      = da * a;
        da_a     = da / a;
        e2       = 1 - (b*b)/(a*a);
        adf      = (a*df) + (a-b)*da/a;
        inverse  = direct;
        direct.inverse = this;
    }

    /**
     * Constructs a transform from the specified Molodensky parameters.
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
        return abridged ? AbridgedMolodensky.PARAMETERS : Molodensky.PARAMETERS;
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
               new ParameterValue<?>[] {
                   dim,
                   new FloatParameter(Molodensky.DX,             dx),
                   new FloatParameter(Molodensky.DY,             dy),
                   new FloatParameter(Molodensky.DZ,             dz),
                   new FloatParameter(Molodensky.SRC_SEMI_MAJOR, a),
                   new FloatParameter(Molodensky.SRC_SEMI_MINOR, b),
                   new FloatParameter(Molodensky.TGT_SEMI_MAJOR, a+da),
                   new FloatParameter(Molodensky.TGT_SEMI_MINOR, b+db)
               });
    }

    /**
     * Gets the dimension of input points.
     */
    @Override
    public final int getSourceDimensions() {
        return source3D ? 3 : 2;
    }

    /**
     * Gets the dimension of output points.
     */
    @Override
    public final int getTargetDimensions() {
        return target3D ? 3 : 2;
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
         * NOTE: The somewhat complicated expression below executes 'maxError' *only* if
         * 1) assertions are enabled and 2) the conditions before 'maxError' are meet. Do
         * not factor the call to 'maxError' outside the 'assert' statement, otherwise it
         * would be executed everytime and would hurt performance for normal operations
         * (instead of slowing down during debugging only).
         */
        final float error;
        assert !(target3D && srcPts != dstPts && (error =
                maxError(null, srcPts, srcOff, null, dstPts, dstOff, 1)) > TOLERANCE) : error;
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
        assert !(target3D && srcPts != dstPts && (error =
                maxError(null, srcPts, srcOff, null, dstPts, dstOff, numPts)) > TOLERANCE) : error;
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
        assert !(target3D && srcPts != dstPts && (error =
                maxError(srcPts, null, srcOff, dstPts, null, dstOff, numPts)) > TOLERANCE) : error;
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
        assert !(target3D && (error =
                maxError(null, srcPts, srcOff, dstPts, null, dstOff, numPts)) > TOLERANCE) : error;
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
        assert !(target3D && (error =
                maxError(srcPts, null, srcOff, null, dstPts, dstOff, numPts)) > TOLERANCE) : error;
    }

    /**
     * Implementation of the transformation methods for all cases.
     */
    private void transform(float[] srcPts1, double[] srcPts2, int srcOff,
                           float[] dstPts1, double[] dstPts2, int dstOff,
                           int numPts, final boolean askStrategy)
    {
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
     * @return The maximal error in decimal degrees.
     */
    private float maxError(final float[] srcPts1, final double[] srcPts2, int srcOff,
                           final float[] dstPts1, final double[] dstPts2, int dstOff, int numPts)
    {
        float max = 0f;
        if (inverse == null) {
            inverse();
            if (inverse == null) {
                return max; // Custom user's subclass; can't do the test.
            }
        }
        final int sourceDim = getSourceDimensions();
        final float[] tmp = new float[numPts * sourceDim];
        inverse.transform(dstPts1, dstPts2, dstOff, tmp, null, 0, numPts, false);
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
        return max;
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
        return dx == 0 && dy == 0 && dz == 0 && da == 0 && db == 0 && source3D == target3D;
    }

    /**
     * Creates the inverse transform of this object.
     */
    @Override
    public MathTransform inverse() {
        if (inverse == null) {
            inverse = new MolodenskyTransform(this);
        }
        return inverse;
    }

    /**
     * Returns a hash value for this transform.
     */
    @Override
    public final int hashCode() {
        final long code = doubleToLongBits(dx) +
                      31*(doubleToLongBits(dy) +
                      31*(doubleToLongBits(dz) +
                      31*(doubleToLongBits(a)  +
                      31*(doubleToLongBits(b)  +
                      31*(doubleToLongBits(da) +
                      31*(doubleToLongBits(db)))))));
        int c = ((int) code) ^ (int) (code >>> 32);
        if (abridged) c ^= 1;
        if (source3D) c ^= 2;
        if (target3D) c ^= 4;
        return c ^ (int) serialVersionUID;
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
            return this.abridged == that.abridged &&
                   this.source3D == that.source3D &&
                   this.target3D == that.target3D &&
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
