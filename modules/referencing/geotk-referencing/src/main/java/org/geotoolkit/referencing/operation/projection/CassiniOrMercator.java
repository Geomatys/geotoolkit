/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2009, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.geom.AffineTransform;
import org.geotoolkit.resources.Errors;
import static java.lang.Math.*;


/**
 * The base class for Cassini-Solder and Transverse Mercator projections.
 *
 * @author Gerald Evenden (USGS)
 * @author André Gosselin (MPO)
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @author Rueben Schulz (UBC)
 * @version 3.0
 *
 * @since 3.0
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
     * @param southOrientated {@code true} if the projection is south orientated.
     */
    CassiniOrMercator(final Parameters parameters, final boolean southOrientated) {
        super(parameters);
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

        final double latitudeOfOrigin = toRadians(parameters.latitudeOfOrigin);
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
         */
        parameters.validate();
        final AffineTransform denormalize = parameters.normalize(false);
        if (southOrientated) {
            denormalize.scale(-1, -1);
        }
        denormalize.translate(0, -ml0);
        finish();
    }

    /**
     * Calculates the meridian distance. This is the distance along the central
     * meridian from the equator to {@code phi}. Accurate to &lt; 1E-5 metres
     * when used in conjuction with typical major axis values.
     *
     * @param  phi latitude to calculate meridian distance for.
     * @param  sphi sin(phi).
     * @param  cphi cos(phi).
     * @return Meridian distance for the given latitude.
     */
    final double mlfn(final double phi, double sphi, double cphi) {
        cphi *= sphi;
        sphi *= sphi;
        return en0 * phi - cphi *
              (en1 + sphi *
              (en2 + sphi *
              (en3 + sphi * en4)));
    }

    /**
     * Calculates the latitude ({@code phi}) from a meridian distance.
     * Determines phi to a tenth of {@value #ITERATION_TOLERANCE} radians.
     *
     * @param  delta meridian distance to calulate latitude for.
     * @return The latitude of the meridian distance.
     * @throws ProjectionException if the iteration does not converge.
     */
    final double inv_mlfn(final double delta) throws ProjectionException {
        final double k = 1/(1 - excentricitySquared);
        double phi = delta;
        int i=MAXIMUM_ITERATIONS;
        do { // rarely goes over 5 iterations
            final double s = sin(phi);
            double t = 1 - excentricitySquared * (s*s);
            t = (mlfn(phi, s, cos(phi)) - delta) * (t * sqrt(t)) * k;
            phi -= t;
            if (abs(t) < ITERATION_TOLERANCE/10) {
                return phi;
            }
        } while (--i >= 0);
        throw new ProjectionException(Errors.Keys.NO_CONVERGENCE);
    }
}
