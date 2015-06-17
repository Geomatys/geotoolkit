/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.projection;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.OperationMethod;
import org.geotoolkit.math.Complex;
import org.geotoolkit.resources.Errors;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.projection.ProjectionException;

import static java.lang.Math.*;


/**
 * <cite>New Zealand Map Grid</cite> (NZMG) projection (EPSG code 9811).
 * See any of the following providers for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.NewZealandMapGrid}</li>
 * </ul>
 *
 * {@section Description}
 *
 * This is an implementation of algorithm published by
 * <a href="http://www.govt.nz/record?recordid=28">Land Information New Zealand</a>.
 * The algorithm is documented <a href="http://www.linz.govt.nz/rcs/linz/6137/">here</a>.
 *
 * {@note This class makes extensive use of <code>Complex</code> type which may be costly
 *        unless the compiler can inline on the stack. We assume that Jave 6 and above can
 *        do this optimization.}
 *
 * @author Justin Deoliveira (Refractions)
 * @author Martin Desruisseaux (IRD, Geomatys)
 *
 * @since 2.2
 * @module
 */
public class NewZealandMapGrid extends UnitaryProjection {
    /**
     * For compatibility with different versions during deserialization.
     */
    private static final long serialVersionUID = 8394817836243729133L;

    /**
     * Coefficients for forward and inverse projection.
     */
    private static final Complex[] A = {
        new Complex(  0.7557853228,  0.0         ),
        new Complex(  0.249204646,   0.003371507 ),
        new Complex( -0.001541739,   0.041058560 ),
        new Complex( -0.10162907,    0.01727609  ),
        new Complex( -0.26623489,   -0.36249218  ),
        new Complex( -0.6870983,    -1.1651967   )
    };

    /**
     * Coefficients for inverse projection.
     */
    private static final Complex[] B = {
        new Complex(  1.3231270439,   0.0         ),
        new Complex( -0.577245789,   -0.007809598 ),
        new Complex(  0.508307513,   -0.112208952 ),
        new Complex( -0.15094762,     0.18200602  ),
        new Complex(  1.01418179,     1.64497696  ),
        new Complex(  1.9660549,      2.5127645   )
    };

    /**
     * Coefficients for inverse projection.
     */
    private static final double[] TPHI = new double[] {
        1.5627014243, 0.5185406398, -0.03333098, -0.1052906, -0.0368594, 0.007317,
        0.01220, 0.00394, -0.0013
    };

    /**
     * Coefficients for forward projection.
     */
    private static final double[] TPSI = new double[] {
        0.6399175073, -0.1358797613, 0.063294409, -0.02526853, 0.0117879,
        -0.0055161, 0.0026906, -0.001333, 0.00067, -0.00034
    };

    /**
     * Creates an NZMG projection from the given parameters. The descriptor argument is usually
     * {@link org.geotoolkit.referencing.operation.provider.NewZealandMapGrid#PARAMETERS}, but is
     * not restricted to. If a different descriptor is supplied, it is user's responsibility to
     * ensure that it is suitable to a NZMG projection.
     *
     * @param  descriptor Typically {@code NewZealandMapGrid.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     *
     * @since 3.00
     */
    public static MathTransform2D create(final OperationMethod descriptor,
                                         final ParameterValueGroup values)
    {
        final Parameters parameters = Parameters.castOrWrap(values);
        final NewZealandMapGrid projection = new NewZealandMapGrid(descriptor, parameters);
        try {
            return (MathTransform2D) projection.createMapProjection(
                    org.apache.sis.internal.system.DefaultFactories.forBuildin(
                            org.opengis.referencing.operation.MathTransformFactory.class));
        } catch (org.opengis.util.FactoryException e) {
            throw new IllegalArgumentException(e); // TODO
        }
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected NewZealandMapGrid(final OperationMethod method, final Parameters parameters) {
        super(method, parameters, null);
        final MatrixSIS normalize = getContextualParameters().getMatrix(true);
        normalize.convertBefore(1, 180/PI * 3600E-5, null);
        normalize.convertBefore(1, null, -getAndStore(parameters, org.geotoolkit.referencing.operation.provider.NewZealandMapGrid.LATITUDE_OF_ORIGIN));
    }

    /**
     * Converts the specified (<var>&lambda;</var>,<var>&phi;</var>) coordinate (units in radians)
     * and stores the result in {@code dstPts} (linear distance on a unit sphere).
     *
     * @since 3.20 (derived from 3.00)
     */
    @Override
    public Matrix transform(final double[] srcPts, final int srcOff,
                            final double[] dstPts, final int dstOff,
                            final boolean derivate) throws ProjectionException
    {
        if (dstPts != null) {
            final double dphi = srcPts[srcOff + 1];
            double dphi_pow_i = dphi;
            double dpsi       = 0;
            for (int i=0; i<TPSI.length; i++) {
                dpsi += (TPSI[i] * dphi_pow_i);
                dphi_pow_i *= dphi;
            }
            // See implementation note in class javadoc.
            final Complex theta = new Complex(dpsi, srcPts[srcOff]);
            final Complex power = new Complex(theta);
            final Complex z     = new Complex();
            z.multiply(A[0], power);
            for (int i=1; i<A.length; i++) {
                power.multiply(power, theta);
                z.addMultiply(z, A[i], power);
            }
            dstPts[dstOff  ] = z.imag;
            dstPts[dstOff+1] = z.real;
        }
        if (derivate) {
            throw new ProjectionException(Errors.format(Errors.Keys.CantComputeDerivative));
        }
        return null;
    }

    /**
     * Transforms the specified (<var>x</var>,<var>y</var>) coordinates
     * and stores the result in {@code dstPts} (angles in radians).
     */
    @Override
    protected void inverseTransform(final double[] srcPts, final int srcOff,
                                    final double[] dstPts, final int dstOff)
            throws ProjectionException
    {
        // See implementation note in class javadoc.
        final Complex z = new Complex(srcPts[srcOff+1], srcPts[srcOff]);
        final Complex power = new Complex(z);
        final Complex theta = new Complex();
        theta.multiply(B[0], z);
        for (int j=1; j<B.length; j++) {
            power.multiply(power, z);
            theta.addMultiply(theta, B[j], power);
        }
        // Increasing the number of iterations through this loop decreases
        // the error in the calculation, but 3 iterations gives 10-3 accuracy.
        final Complex num   = new Complex();
        final Complex denom = new Complex();
        final Complex t     = new Complex();
        for (int j=0; j<3; j++) {
            power.power(theta, 2);
            num.addMultiply(z, A[1], power);
            for (int k=2; k<A.length; k++) {
                power.multiply(power, theta);
                t.multiply(A[k], power);
                t.multiply(t, k);
                num.add(num, t);
            }
            power.real = 1;
            power.imag = 0;
            denom.copy(A[0]);
            for (int k=1; k<A.length; k++) {
                power.multiply(power, theta);
                t.multiply(A[k], power);
                t.multiply(t, k+1);
                denom.add(denom, t);
            }
            theta.divide(num, denom);
        }
        final double dpsi = theta.real;
        double dpsi_pow_i = dpsi;
        double dphi = TPHI[0] * dpsi;
        for (int i=1; i<TPHI.length; i++) {
            dpsi_pow_i *= dpsi;
            dphi += (TPHI[i] * dpsi_pow_i);
        }
        dstPts[dstOff  ] = theta.imag;
        dstPts[dstOff+1] = dphi;
    }
}
