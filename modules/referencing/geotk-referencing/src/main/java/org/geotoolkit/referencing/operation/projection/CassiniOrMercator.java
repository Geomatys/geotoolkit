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

import org.opengis.referencing.operation.OperationMethod;
import org.geotoolkit.resources.Errors;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.operation.matrix.MatrixSIS;
import org.apache.sis.referencing.operation.projection.ProjectionException;

import static java.lang.Math.*;


/**
 * The base class for Cassini-Solder and Transverse Mercator projections.
 *
 * @author Gerald Evenden (USGS)
 * @author André Gosselin (MPO)
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @author Rémi Maréchal (Geomatys)
 *
 * @since 3.00
 * @module
 */
abstract class CassiniOrMercator extends UnitaryProjection {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -8816056150503228733L;

    /**
     * Maximal difference (in radians) from central meridian for enabling assertions. When
     * assertions are enabled, projections using spherical formulas are followed by projections
     * using the ellipsoidal formulas, and the results are compared. If a distance greater than
     * the tolerance level is found, then an {@link AssertionError} will be thrown.
     */
    static final double ASSERTION_DOMAIN = 5 * (PI/180);

    /**
     * Constant needed for the {@link #mlfn} method.
     * Setup at construction time.
     */
    private final double en0, en1, en2, en3, en4;

    /**
     * Constants used to calculate {@link #en0}, {@link #en1},
     * {@link #en2}, {@link #en3}, {@link #en4}.
     */
    private static final double
            C00 = 1.0,
            C02 = 0.25,
            C04 = 0.046875,
            C06 = 0.01953125,
            C08 = 0.01068115234375,
            C22 = 0.75,
            C44 = 0.46875,
            C46 = 0.01302083333333333333,
            C48 = 0.00712076822916666666,
            C66 = 0.36458333333333333333,
            C68 = 0.00569661458333333333,
            C88 = 0.3076171875;

    /**
     * Constructs a new map projection from the supplied parameters.
     *
     * @param parameters The parameters of the projection to be created.
     */
    CassiniOrMercator(final OperationMethod method, final Parameters parameters) {
        super(method, parameters, null);
        final double excentricitySquared = this.excentricitySquared;
        double t;
        en0 = C00 - excentricitySquared  * (C02 + excentricitySquared  *
             (C04 + excentricitySquared  * (C06 + excentricitySquared  * C08)));
        en1 =       excentricitySquared  * (C22 - excentricitySquared  *
             (C04 + excentricitySquared  * (C06 + excentricitySquared  * C08)));
        en2 =  (t = excentricitySquared  *        excentricitySquared) *
             (C44 - excentricitySquared  * (C46 + excentricitySquared  * C48));
        en3 = (t *= excentricitySquared) * (C66 - excentricitySquared  * C68);
        en4 =  t *  excentricitySquared  *  C88;

        final double latitudeOfOrigin = toRadians(getAndStore(parameters, org.geotoolkit.referencing.operation.provider.TransverseMercator.LATITUDE_OF_ORIGIN));
        final double ml0;
        if (excentricitySquared != 0) {
            ml0 = mlfn(latitudeOfOrigin, sin(latitudeOfOrigin), cos(latitudeOfOrigin));
        } else {
            // Above equation simplifies to the latitude of origin in the spherical case.
            ml0 = latitudeOfOrigin;
        }
        /*
         * At this point, all parameters have been processed. Now process to their
         * validation and the initialization of (de)normalize affine transforms.
         *
         * Note that in the South Orientated case, the meaning of False Easting (FE) and
         * False Northing (FN) are reversed: they are effectively False Westing and False
         * Southing. This is the opposite of what we would expect from the parameter names,
         * but is documented that way in the EPSG database. In other words while the usual
         * Transverse Mercator formulas are:
         *
         *     easting   =  FE + px
         *     northing  =  FN + py
         *
         * the Transverse Mercator South Orientated Projection formulas are:
         *
         *     westing   =  (pseudo FE) - px  =  -FE - px  =  -easting
         *     southing  =  (pseudo FN) - py  =  -FN - py  =  -northing
         *
         * Where the px and py terms are the same in both cases. Because of the sign reversal
         * of FE and FN (despite their names) there is actually nothing special to do in this
         * method for the South Orientated case.
         */
        final MatrixSIS denormalize = getContextualParameters().getMatrix(false);
        denormalize.convertBefore(1, null, -ml0);
    }

    /**
     * Calculates the meridian distance. This is the distance along the central
     * meridian from the equator to {@code φ}. Accurate to &lt; 1E-5 metres
     * when used in conjuction with typical major axis values.
     * <p>
     * Special cases:
     * <ul>
     *   <li>If <var>φ</var> is 0°, then this method returns 0.</li>
     * </ul>
     *
     * @param  φ latitude to calculate meridian distance for.
     * @param  sinφ sin(φ).
     * @param  cosφ cos(φ).
     * @return Meridian distance for the given latitude.
     */
    final double mlfn(final double φ, double sinφ, double cosφ) {
        cosφ *= sinφ;
        sinφ *= sinφ;
        return en0*φ - cosφ*(en1 + sinφ*(en2 + sinφ*(en3 + sinφ*en4)));
    }

    /**
     * Gets the derivative of this {@link #mlfn(double, double, double)} method.
     *
     * @return The derivative at the specified latitude.
     */
    final double dmlfn_dφ(final double sinφ2, final double cosφ2) {
        return en0 +
               en1 * (sinφ2 -   cosφ2) + sinφ2*(
               en2 * (sinφ2 - 3*cosφ2) + sinφ2*(
               en3 * (sinφ2 - 5*cosφ2) + sinφ2*
               en4 * (    1 - 7*cosφ2)));
    }

    /**
     * Calculates the latitude ({@code φ}) from a meridian distance.
     * Determines φ to a tenth of {@value #ITERATION_TOLERANCE} radians.
     *
     * @param  delta meridian distance to calculate latitude for.
     * @return The latitude of the meridian distance.
     * @throws ProjectionException if the iteration does not converge.
     */
    final double inv_mlfn(final double delta) throws ProjectionException {
        final double k = 1/(1 - excentricitySquared);
        double φ = delta;
        int i=MAXIMUM_ITERATIONS;
        do { // rarely goes over 5 iterations
            final double s = sin(φ);
            double t = 1 - excentricitySquared * (s*s);
            t = (mlfn(φ, s, cos(φ)) - delta) * (t * sqrt(t)) * k;
            φ -= t;
            if (abs(t) < ITERATION_TOLERANCE/10) {
                return φ;
            }
        } while (--i >= 0);
        throw new ProjectionException(Errors.format(Errors.Keys.NoConvergence));
    }
}
