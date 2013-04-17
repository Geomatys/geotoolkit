/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.referencing.operation;

import org.geotoolkit.math.Statistics;
import org.geotoolkit.referencing.GeodeticCalculator;
import static java.lang.StrictMath.*;


/**
 * A few coordinate points, together with the expected transformation result, for testing purpose.
 * Two different results is provided: one which is expected when no EPSG database were used, and a
 * more accurate one expected when an EPSG database were used for the calculation.
 * <p>
 * The target values were computed with Geotk (not an external library). However some of them
 * have been tested with both Molodensky and Geocentric transformations.
 * <p>
 * The coordinates are declared in
 * (<var>latitude</var>, <var>longitude</var>, <var>height</var>) order.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.16
 */
final strictfp class SamplePoints {
    /**
     * Tolerance factor. If must be small enough to detect if the height has
     * been used, except when the source coordinates is known to have no height.
     * <p>
     * Do not relax the Molodensky tolerance: we have selected a value fine enough to allow
     * the tests to distinguish between a "Molodensky" and "Abridged Molodensky" operations.
     */
    static final double TOLERANCE = 1E-12, NOHEIGHT_TOLERANCE = 1E-8, MOLODENSKY_TOLERANCE = 1E-6;

    /**
     * Sample points for transformations from
     * {@link CoordinateOperationFactoryTest#NAD27_Z} to
     * {@link CoordinateOperationFactoryTest#WGS84_Z}.
     *
     * @todo The coordinate values in the EPSG case (the last row in each constructor call) is for
     *       Coordinate operation "NAD27 to WGS 84 (88)" for Cuba. May not be the most appropriate
     *       operation, but this is the one selected by the current ordering criterion in the SQL
     *       statements (GEOTK-80).
     */
    static final SamplePoints[] NAD27_TO_WGS84 = {
        new SamplePoints( 0.0,                   0.0,                   0.0,
                          0.001654978796746043,  0.0012755944235822696, 66.4042236590758,    66.4042236590758,
                          0.0016488865058817196, 0.001206062410034649,  76.25110799539834,   76.25110799539834),
        new SamplePoints( 5.0,                   8.0,                   20.0,
                          5.0012629560319874,    8.001271729856333,    120.27929787151515,  100.27929787896574,
                          5.001273036851652,     8.001180660967439,    132.40271407272667,  112.40270038507879),
        new SamplePoints( 5.0,                   8.0,                  -20.0,
                          5.001262964005206,     8.001271737831601,     80.2792978901416,   100.27929787896574,
                          5.001273045624943,     8.001180669334277,     92.40268669463694,  112.40270038507879),
        new SamplePoints(-5.0,                  -8.0,                  -20.0,
                         -4.99799698932651,     -7.998735783965731,      9.007854541763663,  29.00245368219864,
                         -4.998019250063004,    -7.998787956860261,     16.432708218693733,  36.432721899822354)
    };

    /**
     * The source (<var>latitude</var>, <var>longitude</var>, <var>height</var>) coordinates.
     */
    public static final strictfp class Source {
        public final double φ, λ, h;
        Source(final double φ, final double λ, final double h) {
            this.φ  = φ;
            this.λ  = λ;
            this.h  = h;
        }
    }

    /**
     * The expected (<var>latitude</var>, <var>longitude</var>, <var>height</var>) coordinates.
     * The {@link #h0} field is the height that we get if the source height is replaced by zero.
     */
    public static final strictfp class Target {
        public final double φ, λ, h, h0;
        Target(final double φ, final double λ, final double h, final double h0) {
            this.φ  = φ;
            this.λ  = λ;
            this.h  = h;
            this.h0 = h0;
        }
    }

    /**
     * The source coordinates.
     */
    public final Source src;

    /**
     * The expected transformation result, without EPSG database.
     */
    public final Target tgt;

    /**
     * The expected transformation result, when using the EPSG database.
     */
    public final Target epsg;

    /**
     * Creates an sample points for the given source and target ordinate values.
     */
    private SamplePoints(final double sφ, final double sλ, final double sh,
                         final double tφ, final double tλ, final double th, final double th0,
                         final double eφ, final double eλ, final double eh, final double eh0)
    {
        src  = new Source(sφ, sλ, sh);
        tgt  = new Target(tφ, tλ, th, th0);
        epsg = new Target(eφ, eλ, eh, eh0);
    }

    /**
     * Lists the distance in metres between the expected points without EPSG,
     * and the expected points computed using the EPSG database.
     *
     * @param args Ignored.
     */
    public static void main(final String[] args) {
        final SamplePoints[][] samplesList = new SamplePoints[][] {
            NAD27_TO_WGS84,
            // More may be added in the future.
        };
        final Statistics stats = new Statistics();
        final GeodeticCalculator c = new GeodeticCalculator();
        for (final SamplePoints[] samples : samplesList) {
            for (final SamplePoints sample : samples) {
                c.setStartingGeographicPoint   (sample.tgt .λ, sample.tgt .φ);
                c.setDestinationGeographicPoint(sample.epsg.λ, sample.epsg.φ);
                double distance = c.getOrthodromicDistance();
                distance = hypot(distance, sample.tgt.h - sample.epsg.h);
                stats.accept(distance);
            }
            System.out.println(stats);
            stats.reset();
        }
    }
}
