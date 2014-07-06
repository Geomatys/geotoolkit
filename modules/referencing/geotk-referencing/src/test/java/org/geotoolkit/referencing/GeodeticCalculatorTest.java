/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing;

import java.util.Collections;
import java.util.Map;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import javax.measure.unit.SI;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.DirectPosition;

import org.opengis.referencing.IdentifiedObject;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.cs.Axes;
import org.apache.sis.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.test.referencing.ReferencingTestBase;
import org.apache.sis.referencing.CommonCRS;

import org.junit.*;
import static org.junit.Assert.*;
import static java.lang.StrictMath.*;


/**
 * Tests the geodetic calculator.
 *
 * @author Daniele Franzoni
 * @author Martin Desruisseaux (Geomatys)
 * @author Katrin Lasinger
 * @version 3.16
 *
 * @since 2.1
 */
public final strictfp class GeodeticCalculatorTest extends ReferencingTestBase {
    /**
     * Small tolerance value for floating point comparisons.
     */
    private static final double EPS = 1E-6;

    private static Map<String,String> name(final String name) {
        return Collections.singletonMap(IdentifiedObject.NAME_KEY, name);
    }

    /**
     * Tests some trivial azimuth directions.
     */
    @Test
    public void testAzimuth() {
        final double EPS = 0.2; // Relax the default (static) tolerance threshold.
        final GeodeticCalculator calculator = new GeodeticCalculator();
        assertTrue(calculator.getCoordinateReferenceSystem() instanceof GeographicCRS);
        calculator.setStartingGeographicPoint(12, 20);
        calculator.setDestinationGeographicPoint(13, 20);  assertEquals("East",   90, calculator.getAzimuth(), EPS);
        calculator.setDestinationGeographicPoint(12, 21);  assertEquals("North",   0, calculator.getAzimuth(), EPS);
        calculator.setDestinationGeographicPoint(11, 20);  assertEquals("West",  -90, calculator.getAzimuth(), EPS);
        calculator.setDestinationGeographicPoint(12, 19);  assertEquals("South", 180, calculator.getAzimuth(), EPS);
    }

    /**
     * Tests azimuth at poles.
     *
     * @since 3.16
     */
    @Test
    public void testPoles() {
        final double EPS = 0.2; // Relax the default (static) tolerance threshold.
        final GeodeticCalculator calculator = new GeodeticCalculator();
        calculator.setStartingGeographicPoint   ( 30,  90);
        calculator.setDestinationGeographicPoint( 20,  20);  assertEquals(-170, calculator.getAzimuth(), EPS);
        calculator.setDestinationGeographicPoint( 40,  20);  assertEquals( 170, calculator.getAzimuth(), EPS);
        calculator.setDestinationGeographicPoint( 30,  20);  assertEquals( 180, calculator.getAzimuth(), EPS);
        calculator.setDestinationGeographicPoint( 30, -20);  assertEquals( 180, calculator.getAzimuth(), EPS);
        calculator.setDestinationGeographicPoint( 30, -90);  assertEquals( 180, calculator.getAzimuth(), EPS);

        calculator.setStartingGeographicPoint   (  0,  90);
        calculator.setDestinationGeographicPoint( 20,  20);  assertEquals( 160, calculator.getAzimuth(), EPS);
        calculator.setDestinationGeographicPoint(-20,  20);  assertEquals(-160, calculator.getAzimuth(), EPS);
        calculator.setDestinationGeographicPoint(  0,  20);  assertEquals( 180, calculator.getAzimuth(), EPS);
        calculator.setDestinationGeographicPoint(  0, -90);  assertEquals( 180, calculator.getAzimuth(), EPS);
    }

    /**
     * Test path on the 45th parallel. Data for this test come from the
     * <A HREF="http://www.univ-lemans.fr/~hainry/articles/loxonavi.html">Orthodromie et
     * loxodromie</A> page.
     */
    @Test
    @SuppressWarnings("fallthrough")
    public void testParallel45() {
        // Column 1: Longitude difference in degrees.
        // Column 2: Orthodromic distance in kilometers
        // Column 3: Loxodromic  distance in kilometers
        final double[] DATA = {
              0.00,     0,      0,
             11.25,   883,    884,
             22.50,  1762,   1768,
             33.75,  2632,   2652,
             45.00,  3489,   3536,
             56.25,  4327,   4419,
             67.50,  5140,   5303,
             78.75,  5923,   6187,
             90.00,  6667,   7071,
            101.25,  7363,   7955,
            112.50,  8002,   8839,
            123.75,  8573,   9723,
            135.00,  9064,  10607,
            146.25,  9463,  11490,
            157.50,  9758,  12374,
            168.75,  9939,  13258,
            180.00, 10000,  14142
        };
        final double R = 20000 / PI;
        final DefaultEllipsoid ellipsoid  = DefaultEllipsoid.createEllipsoid(
                Collections.singletonMap(DefaultEllipsoid.NAME_KEY, "Test"),
                R,R, SI.KILOMETRE);
        final GeodeticCalculator calculator = new GeodeticCalculator(ellipsoid);
        calculator.setStartingGeographicPoint(0, 45);
        for (int i=0; i<DATA.length; i+=3) {
            calculator.setDestinationGeographicPoint(DATA[i], 45);
            final double orthodromic = calculator.getOrthodromicDistance();
//          final double loxodromic  = calculator. getLoxodromicDistance();
            assertEquals("Orthodromic distance", DATA[i+1], orthodromic, 0.75);
//          assertEquals( "Loxodromic distance", DATA[i+2], loxodromic,  0.75);
            /*
             * Test the orthodromic path. We compare its length with the expected length.
             */
            int    count=0;
            double length=0, lastX=Double.NaN, lastY=Double.NaN;
            final Shape        path     = calculator.getGeodeticCurve(1000);
            final PathIterator iterator = path.getPathIterator(null, 0.1);
            final double[]     buffer   = new double[6];
            while (!iterator.isDone()) {
                switch (iterator.currentSegment(buffer)) {
                    case PathIterator.SEG_LINETO: {
                        count++;
                        length += ellipsoid.orthodromicDistance(lastX, lastY, buffer[0], buffer[1]);
                        // Fall through
                    }
                    case PathIterator.SEG_MOVETO: {
                        lastX = buffer[0];
                        lastY = buffer[1];
                        break;
                    }
                    default: {
                        throw new IllegalPathStateException();
                    }
                }
                iterator.next();
            }
            assertEquals("Segment count", 1000, count); // Implementation check; will no longer be
                                                        // valid when the path will contains curves.
            assertEquals("Orthodromic path length", orthodromic, length, 1E-4);
        }
    }

    /**
     * Tests geodetic calculator involving a coordinate operation.
     * Our test uses a simple geographic CRS with only the axis order interchanged.
     *
     * @throws FactoryException Shoud never happen.
     * @throws TransformException Shoud never happen.
     */
    @Test
    public void testUsingTransform() throws FactoryException, TransformException {
        final GeographicCRS crs = new DefaultGeographicCRS(name("Test"), CommonCRS.WGS84.datum(),
                new DefaultEllipsoidalCS(name("Test"), Axes.LATITUDE, Axes.LONGITUDE));
        final GeodeticCalculator calculator = new GeodeticCalculator(crs);
        assertSame(crs, calculator.getCoordinateReferenceSystem());

        final double x = 45;
        final double y = 30;
        calculator.setStartingPosition(new DirectPosition2D(x,y));
        Point2D point = calculator.getStartingGeographicPoint();
        assertEquals(y, point.getX(), EPS);
        assertEquals(x, point.getY(), EPS);

        calculator.setDirection(10, 100);
        DirectPosition position = calculator.getDestinationPosition();
        point = calculator.getDestinationGeographicPoint();
        assertEquals(point.getX(), position.getOrdinate(1), EPS);
        assertEquals(point.getY(), position.getOrdinate(0), EPS);
    }

    /**
     * Tests orthrodromic distance on the equator. The main purpose of this method is actually
     * to get Java assertions to be run, which will compare the Geodetic Calculator results with
     * the Default Ellipsoid computations.
     */
    @Test
    public void testEquator() {
        assertTrue(GeodeticCalculator.class.desiredAssertionStatus());
        final GeodeticCalculator calculator = new GeodeticCalculator();
        calculator.setStartingGeographicPoint(0, 0);
        double last = Double.NaN;
        for (double x=0; x<=180; x+=0.125) {
            calculator.setDestinationGeographicPoint(x, 0);
            final double distance = calculator.getOrthodromicDistance() / 1000; // In kilometers
            /*
             * Checks that the increment is constant. It is not for x>179 unless
             * GeodeticCalculator switch to DefaultEllipsoid algorithm, which is
             * what we want to ensure with this test.
             */
            assertFalse(abs(abs(distance - last) - 13.914935) > 2E-6);
            last = distance;
        }
    }

    /**
     * Tests the points reported in
     * <a href="http://jira.codehaus.org/browse/GEOT-1535">GEOT-1535</a>.
     */
    @Test
    public void testGEOT1535() {
        final GeodeticCalculator calculator = new GeodeticCalculator();
        final DefaultEllipsoid reference = DefaultEllipsoid.castOrCopy(CommonCRS.WGS84.ellipsoid());

        calculator.setStartingGeographicPoint(10, 40);
        calculator.setDestinationGeographicPoint(-175, -30);
        assertEquals(reference.orthodromicDistance(10, 40, -175, -30),
                     calculator.getOrthodromicDistance(), 1E-4);
        assertEquals(23.053, calculator.getAzimuth(), 1E-3);

        calculator.setStartingGeographicPoint(180, 40);
        calculator.setDestinationGeographicPoint(-5, -30);
        assertEquals(reference.orthodromicDistance(180, 40, -5, -30),
                     calculator.getOrthodromicDistance(), 1E-4);
        assertEquals(23.053, calculator.getAzimuth(), 1E-3);
    }

    /**
     * Tests case for the error reported in
     * <a href="http://jira.geotoolkit.org/browse/GEOTK-103">GEOTK-103</a>
     *
     * @author Katrin Lasinger
     * @since 3.13
     */
    @Test
    public void testGeodeticCurveOnEquator() {
        final GeodeticCalculator calculator = new GeodeticCalculator();
        calculator.setStartingGeographicPoint(20, 0);
        calculator.setDestinationGeographicPoint(12, 0);
        assertEquals(-90, calculator.getAzimuth(), EPS);
        /*
         * Ensures that the y ordinate is 0 everywhere on the path.
         */
        final Shape geodeticCurve = calculator.getGeodeticCurve();
        final PathIterator it = geodeticCurve.getPathIterator(new AffineTransform());
        final double[] coords = new double[2];
        while (!it.isDone()) {
            it.currentSegment(coords);
            assertEquals(0, coords[1], EPS);
            it.next();
        }
    }

    /**
     * Tests case for the error reported in
     * <a href="http://jira.codehaus.org/browse/GEOT-2716">GEOT-2716</a>
     *
     * @author Katrin Lasinger
     * @since 3.13
     */
    @Test
    public void testPointsOnGeodeticCurve() {
        final GeodeticCalculator calculator = new GeodeticCalculator();
        calculator.setStartingGeographicPoint(0, 0);
        calculator.setDestinationGeographicPoint(0, 10);
        final Shape geodeticCurve = calculator.getGeodeticCurve();
        final PathIterator it = geodeticCurve.getPathIterator(new AffineTransform());
        double[] coordsStart = new double[2];
        double[] coordsEnd = new double[2];
        double distance = Double.NaN;
        int numPts = 0;
        while (!it.isDone()) {
            System.arraycopy(coordsEnd, 0, coordsStart, 0, coordsStart.length);
            it.currentSegment(coordsEnd);
            if (++numPts >= 2) {
                // We can calculate the distance only after we iterated over at least 2 points.
                calculator.setStartingGeographicPoint(coordsStart[0], coordsStart[1]);
                calculator.setDestinationGeographicPoint(coordsEnd[0], coordsEnd[1]);
                final double currentDistance = calculator.getOrthodromicDistance();
                if (numPts == 2) {
                    // Use the first distance that we computed as the reference distance value.
                    // All remaining iteration of the loop will compare against this reference.
                    distance = currentDistance;
                }
                assertEquals(distance, currentDistance, distance*EPS);
            }
            it.next();
        }
    }
}
