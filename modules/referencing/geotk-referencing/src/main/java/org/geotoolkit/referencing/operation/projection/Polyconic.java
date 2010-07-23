/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import static java.lang.Math.*;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform2D;

import org.geotoolkit.lang.Immutable;
import org.geotoolkit.resources.Errors;


/**
 * American Polyconic Projection (EPSG codes 9818). See the
 * <A HREF="http://mathworld.wolfram.com/PolyconicProjection.html">Polyconic projection on MathWorld</A>
 * for an overview. See the following provider for a list of programmatic parameters:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.provider.Polyconic}</li>
 * </ul>
 *
 * {@section Description}
 * <ul>
 *   <li>Neither conformal nor equal-area.</li>
 *   <li>Parallels of latitude (except for Equator) are arcs of circles, but are not concentrics.</li>
 *   <li>Central Meridian and Equator are straight lines; all other meridians are complex curves.</li>
 *   <li>Scale is true along each parallel and along the central meridian, but no parallel is "standard".</li>
 *   <li>Free of distortion only along the central meridian.</li>
 * </ul>
 *
 * {@section References}
 * <ul>
 *   <li>John P. Snyder (Map Projections - A Working Manual,<br>
 *       U.S. Geological Survey Professional Paper 1395, 1987)</li>
 *   <li>"Coordinate Conversions and Transformations including Formulas",<br>
 *       EPSG Guidence Note Number 7, Version 40.</li>
 * </ul>
 *
 * @author Simon Reynard (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
@Immutable
public class Polyconic extends CassiniOrMercator {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -4178027711158788385L;

    /**
     * Creates a Polyconic projection from the given parameters. The descriptor argument is
     * usually {@link org.geotoolkit.referencing.operation.provider.Polyconic#PARAMETERS},
     * but is not restricted to. If a different descriptor is supplied, it is user's responsibility
     * to ensure that it is suitable to a Polyconic projection.
     *
     * @param  descriptor Typically one of {@code Polyconic.PARAMETERS}.
     * @param  values The parameter values of the projection to create.
     * @return The map projection.
     */
    public static MathTransform2D create(final ParameterDescriptorGroup descriptor,
                                         final ParameterValueGroup values)
    {
        final Polyconic projection;
        final Parameters parameters = new Parameters(descriptor, values);
        if (parameters.isSpherical()) {
            projection = new Spherical(parameters);
        } else {
            projection = new Polyconic(parameters);
        }
        return projection.createConcatenatedTransform();
    }

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    protected Polyconic(final Parameters parameters) {
        super(parameters);
    }

    /**
     * Transforms the specified (<var>&lambda;</var>,<var>&phi;</var>) coordinates
     * (units in radians) and stores the result in {@code dstPts} (linear distance
     * on a unit sphere).
     * <p>
     * <b>Note:</b> This method produces NaN at poles in the spherical cases
     * (may occur if assertions are enabled).
     */
    @Override
    protected void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        double x = rollLongitude(srcPts[srcOff]);
        double y = srcPts[srcOff + 1];
        final double sinphi = sin(y);
        final double cosphi = cos(y);
        final double ms = msfn(sinphi, cosphi) / sinphi;
        /*
         * If y == 0, then we have (1/0) == infinity. Then we would have below
         * y = 0 + infinity * (1 - 1)  ==  infinity * zero  ==  indetermination.
         * Actually the indetermination resolve as being just leaving y unchanged
         * (same for x).
         *
         * In Proj4 this was handled by a check for a threshold: if (abs(y) > 1E-10).
         * In Geotk, we try to avoid threshold as much as possible in order to have
         * more continuous function.
         */
        if (!Double.isInfinite(ms)) {
            y = mlfn(y, sinphi, cosphi) + ms * (1 - cos(x *= sinphi));
            x = ms * sin(x);
        }
        dstPts[dstOff  ] = x;
        dstPts[dstOff+1] = y;
    }

    /**
     * Transforms the specified (<var>x</var>,<var>y</var>) coordinates
     * and stores the result in {@code dstPts} (angles in radians).
     */
    @Override
    protected void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff)
            throws ProjectionException
    {
        double x = unrollLongitude(srcPts[srcOff]);
        double y = srcPts[srcOff + 1];

        if (abs(y) <= EPSILON) {
            y = 0;
            /*
             * The general formulas below will not work for this case because of
             * indeterminations of the kind 0*infinity.
             */
        } else {
            double dPhi;
            final double r = y*y + x*x;
            final double y2 = 2*y;
            int i = MAXIMUM_ITERATIONS;
            do {
                if (--i < 0) {
                    throw new ProjectionException(Errors.Keys.NO_CONVERGENCE);
                }
                final double cp = cos(y);
                if (abs(cp) < ITERATION_TOLERANCE) {
                    // Continuing would lead to c = infinity, and later to an
                    // indetermination (infinity / infinity).
                    break;
                }
                final double sp = sin(y);
                final double s2ph = sp * cp;
                double mlp = sqrt(1 - excentricitySquared * (sp*sp));
                final double c = sp * mlp / cp;
                final double ml = mlfn(y, sp, cp);
                final double mlb = ml*ml + r;
                mlp  = (1 - excentricitySquared) / (mlp * mlp * mlp);
                dPhi = (2*ml + c*mlb - y2*(c*ml + 1)) /
                        (excentricitySquared * s2ph * (mlb - y2*ml)/c +
                        (y2 - 2*ml) * (c*mlp - 1/s2ph) - 2*mlp);
                y += dPhi;
            } while (abs(dPhi) > ITERATION_TOLERANCE);
            final double c = sin(y);
            x = asin(x * tan(y) * sqrt(1 - excentricitySquared * (c*c))) / c;
        }
        dstPts[dstOff  ] = x;
        dstPts[dstOff+1] = y;
    }


    /**
     * Provides the transform equations for the spherical case of the Polyconic projection.
     *
     * @author Simon Reynard (Geomatys)
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.11
     *
     * @since 3.11
     * @module
     */
    @Immutable
    static final class Spherical extends Polyconic {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 8669570024272104893L;

        /**
         * The latitude of origin, in radians.
         */
        private final double phi0;

        /**
         * Constructs a new map projection from the supplied parameters.
         *
         * @param parameters The parameters of the projection to be created.
         */
        Spherical(final Parameters parameters) {
            super(parameters);
            phi0 = toRadians(parameters.latitudeOfOrigin);
        }

        /**
         * Returns {@code true} since this class uses spherical formulas.
         */
        @Override
        final boolean isSpherical() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void transform(final double[] srcPts, final int srcOff,
                                 final double[] dstPts, final int dstOff)
                throws ProjectionException
        {
            double x = rollLongitude(srcPts[srcOff]);
            double y = srcPts[srcOff + 1];
            final double E = x * sin(y);
            final double cot = 1 / tan(y);
            x = sin(E) * cot;
            y = y - phi0 + cot * (1 - cos(E));
            assert checkTransform(srcPts, srcOff, dstPts, dstOff, x, y);
            dstPts[dstOff  ] = x;
            dstPts[dstOff+1] = y;
        }

        /**
         * Computes using ellipsoidal formulas and compares with the
         * result from spherical formulas. Used in assertions only.
         */
        private boolean checkTransform(final double[] srcPts, final int srcOff,
                                       final double[] dstPts, final int dstOff,
                                       final double x, final double y)
                throws ProjectionException
        {
            super.transform(srcPts, srcOff, dstPts, dstOff);
            return Assertions.checkTransform(dstPts, dstOff, x, y);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void inverseTransform(final double[] srcPts, final int srcOff,
                                        final double[] dstPts, final int dstOff)
                throws ProjectionException
        {
            double x = unrollLongitude(srcPts[srcOff]);
            double y = srcPts[srcOff + 1];
            if (abs(y) <= EPSILON) {
                y = 0;
            } else {
                final double y1 = y;
                final double B = x*x + y*y;
                int i = MAXIMUM_ITERATIONS;
                double dphi;
                do {
                    if (--i < 0) {
                        throw new ProjectionException(Errors.Keys.NO_CONVERGENCE);
                    }
                    final double tp = tan(y);
                    dphi = (y1 * (y*tp + 1) - y - 0.5*(y*y + B) * tp) / ((y - y1) / tp - 1);
                    y -= dphi;
                } while(abs(dphi) > ITERATION_TOLERANCE);
                x = asin(x*tan(y)) / sin(y);
            }
            assert checkInverseTransform(srcPts, srcOff, dstPts, dstOff, x, y);
            dstPts[dstOff  ] = x;
            dstPts[dstOff+1] = y;
        }

        /**
         * Computes using ellipsoidal formulas and compares with the
         * result from spherical formulas. Used in assertions only.
         */
        private boolean checkInverseTransform(final double[] srcPts, final int srcOff,
                                              final double[] dstPts, final int dstOff,
                                              final double lambda, final double phi)
                throws ProjectionException
        {
            super.inverseTransform(srcPts, srcOff, dstPts, dstOff);
            return Assertions.checkInverseTransform(dstPts, dstOff, lambda, phi);
        }
    }

    /**
     * Returns an estimation of the error in linear distance on the unit ellipse.
     */
    @Override
    double getErrorEstimate(final double lambda, final double phi) {
        return 0;
    }
}
