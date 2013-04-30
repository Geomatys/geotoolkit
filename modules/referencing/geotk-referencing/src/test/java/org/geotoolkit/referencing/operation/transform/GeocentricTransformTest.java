/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Random;
import javax.measure.unit.SI;
import javax.vecmath.Point3d;

import org.opengis.util.FactoryException;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.referencing.crs.DefaultGeocentricCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.datum.BursaWolfParameters;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;

import org.apache.sis.test.DependsOn;
import org.junit.*;
import org.opengis.referencing.datum.Ellipsoid;

import static org.junit.Assert.*;
import static java.lang.StrictMath.*;


/**
 * Tests the following transformation classes with the geocentric transform:
 * <p>
 * <ul>
 *   <li>{@link CoordinateOperation}</li>
 *   <li>{@link GeocentricTransform}</li>
 *   <li>{@link DefaultEllipsoid}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 2.1
 */
@DependsOn(AbstractMathTransformTest.class)
public final strictfp class GeocentricTransformTest extends TransformTestBase {
    /**
     * Creates the test suite.
     */
    public GeocentricTransformTest() {
        super(GeocentricTransform.class, null);
    }

    /**
     * Tests the variants for different number of dimensions.
     *
     * @throws NoninvertibleTransformException Should never happen.
     *
     * @since 3.16
     */
    @Test
    public void testForDimensions() throws NoninvertibleTransformException {
        final double a = 6378137.0;
        final double b = 6356752.0;
        final GeocentricTransform tr2D, tr3D;

        tr2D = new GeocentricTransform(a, b, SI.METRE, false);
        assertEquals(2, tr2D.getSourceDimensions());
        assertEquals(3, tr2D.getTargetDimensions());
        assertSame(tr2D, tr2D.forDimensions(false, true));

        tr3D = tr2D.forDimensions(true, true);
        assertNotSame(tr2D, tr3D);
        assertEquals(3, tr3D.getSourceDimensions());
        assertEquals(3, tr3D.getTargetDimensions());

        assertSame("Expected cached transform.", tr2D, tr3D.forDimensions(false, true));
        assertSame("Expected cached transform.", tr2D, tr3D.inverse().forDimensions(true, false).inverse());
    }

    /**
     * Tests using the coordinate given in EPSG example.
     *
     * Source point in WGS84: 53°48'33.820"N, 02°07'46.380"E, 73.00 metres.
     * Target point in ED50:  53°48'36.565"N, 02'07"51.477"E, 28.02 metres.
     * Datum shift: dX = +84.87m, dY = +96.49m, dZ = +116.95m.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testEpsgExample() throws TransformException {
        double[] source = new double[] {
             2 + ( 7 + 46.38/60)/60,  // Longitude
            53 + (48 + 33.82/60)/60,  // Latitude
            73.0                      // Height
        };
        double[] target = new double[] {
            3771793.97,
             140253.34,
            5124304.35
        };
        tolerance = 1E-2;
        transform = GeocentricTransform.create(DefaultEllipsoid.WGS84, true);
        validate();
        verifyTransform(source, target);
        stress(CoordinateDomain.GEOGRAPHIC, 306954540);
        /*
         * Applies the datum shift.
         */
        final BursaWolfParameters parameters = new BursaWolfParameters(null);
        parameters.dx =  84.87;
        parameters.dy =  96.49;
        parameters.dz = 116.95;
        source = target;
        target = new double[] {
            3771878.84,
             140349.83,
            5124421.30
        };
        tolerance = 1E-2;
        transform = new GeocentricAffineTransform(parameters);
        validate();
        verifyTransform(source, target);
        stress(CoordinateDomain.GEOCENTRIC, 288326602);
        /*
         * Back to geographic coordinates, now in ED50 datum.
         */
        source = target;
        target = new double[] {
             2 + ( 7 + 51.477/60)/60,  // Longitude
            53 + (48 + 36.565/60)/60,  // Latitude
            28.02                      // Height
        };
        tolerance = 1.5E-2;
        transform = GeocentricTransform.create(DefaultEllipsoid.INTERNATIONAL_1924, true).inverse();
        validate();
        verifyTransform(source, target);
        stress(CoordinateDomain.GEOCENTRIC, 831342815);
    }

    /**
     * Tests the {@link GeocentricTransform} class created by {@link #opFactory}.
     *
     * @throws FactoryException Should never occur.
     * @throws TransformException Should never occur.
     */
    @Test
    public void testFromCoordinateOperation() throws FactoryException, TransformException {
        final Random random = new Random(661597560);
        /*
         * Gets the math transform from WGS84 to a geocentric transform.
         */
        final DefaultEllipsoid          ellipsoid = DefaultEllipsoid.WGS84;
        final CoordinateReferenceSystem sourceCRS = DefaultGeographicCRS.WGS84_3D;
        final CoordinateReferenceSystem targetCRS = DefaultGeocentricCRS.CARTESIAN;
        final CoordinateOperation       operation = opFactory.createOperation(sourceCRS, targetCRS);
        transform = operation.getMathTransform();
        final int dimension = transform.getSourceDimensions();
        assertEquals("Source dimension", 3, dimension);
        assertEquals("Target dimension", 3, transform.getTargetDimensions());
        assertSame("Inverse transform", transform, transform.inverse().inverse());
        validate();
        /*
         * Constructs an array of random points. The first 8 points
         * are initialized to know values. Other points are left random.
         */
        final double   cartesianDistance[] = new double[4];
        final double orthodromicDistance[] = new double[4];
        final double[] array0 = new double[900]; // Must be divisible by 3.
        for (int i=0; i<array0.length; i++) {
            final int range;
            switch (i % 3) {
                case 0:  range =   360; break; // Longitude
                case 1:  range =   180; break; // Latitidue
                case 2:  range = 10000; break; // Altitude
                default: range =     0; break; // Should not happen
            }
            array0[i] = random.nextDouble() * range - (range/2);
        }
        array0[0]=35.0; array0[1]=24.0; array0[2]=8000; // 24°N 35°E 8km
        array0[3]=34.8; array0[4]=24.7; array0[5]=5000; // ... about 80 km away
        cartesianDistance  [0] = 80284.00;
        orthodromicDistance[0] = 80302.99; // Not really exact.

        array0[6]=  0; array0[ 7]=0.0; array0[ 8]=0;
        array0[9]=180; array0[10]=0.0; array0[11]=0; // Antipodes; distance should be 2*6378.137 km
        cartesianDistance  [1] = ellipsoid.getSemiMajorAxis() * 2;
        orthodromicDistance[1] = ellipsoid.getSemiMajorAxis() * PI;

        array0[12]=  0; array0[13]=-90; array0[14]=0;
        array0[15]=180; array0[16]=+90; array0[17]=0; // Antipodes; distance should be 2*6356.752 km
        cartesianDistance  [2] = ellipsoid.getSemiMinorAxis() * 2;
        orthodromicDistance[2] = 20003931.46;

        array0[18]= 95; array0[19]=-38; array0[20]=0;
        array0[21]=-85; array0[22]=+38; array0[23]=0; // Antipodes
        cartesianDistance  [3] = 12740147.19;
        orthodromicDistance[3] = 20003867.86;
        /*
         * Transforms all points, and then inverse transform them. The resulting
         * array2 should be equal to array0 except for rounding errors. We tolerate
         * maximal error of 0.1 second in longitude or latitude and 1 cm in height.
         */
        final double[] array1 = new double[array0.length];
        final double[] array2 = new double[array0.length];
        transform          .transform(array0, 0, array1, 0, array0.length / dimension);
        transform.inverse().transform(array1, 0, array2, 0, array1.length / dimension);
        for (int i=0; i<array0.length;) {
            assertEquals("Longitude", array2[i], array0[i], 0.1/3600); i++;
            assertEquals("Latitude",  array2[i], array0[i], 0.1/3600); i++;
            assertEquals("Height",    array2[i], array0[i], 0.01); i++;
        }
        /*
         * Compares the distances between "special" points with expected distances.
         * This tests the ellipsoid orthodromic distance computation as well.
         * We require a precision of 10 centimetres.
         */
        for (int i=0; i<array0.length/6; i++) {
            final int base = i*6;
            final Point3d  pt1 = new Point3d(array1[base+0], array1[base+1], array1[base+2]);
            final Point3d  pt2 = new Point3d(array1[base+3], array1[base+4], array1[base+5]);
            final double cartesian = pt1.distance(pt2);
            if (i < cartesianDistance.length) {
                assertEquals("Cartesian distance", cartesianDistance[i], cartesian, 0.1);
            }
            /*
             * Compares with orthodromic distance. Distance is computed using an ellipsoid
             * at the maximal altitude (i.e. the length of semi-major axis is increased to
             * fit the maximal altitude).
             */
            try {
                final double altitude = max(array0[base+2], array0[base+5]);
                final DefaultEllipsoid ellip = DefaultEllipsoid.createFlattenedSphere("Temporary",
                                               ellipsoid.getSemiMajorAxis()+altitude,
                                               ellipsoid.getInverseFlattening(),
                                               ellipsoid.getAxisUnit());
                double orthodromic = ellip.orthodromicDistance(array0[base+0], array0[base+1],
                                                               array0[base+3], array0[base+4]);
                orthodromic = hypot(orthodromic, array0[base+2] - array0[base+5]);
                if (i < orthodromicDistance.length) {
                    assertEquals("Orthodromic distance", orthodromicDistance[i], orthodromic, 0.1);
                }
                assertTrue("Distance consistency", cartesian <= orthodromic);
            } catch (ArithmeticException exception) {
                // Orthodromic distance computation didn't converge. Ignore...
            }
        }
    }

    /**
     * Executes the derivative test using the given ellipsoid.
     *
     * @param  ellipsoid The ellipsoid to use for the test.
     * @param  hasHeight {@code true} if geographic coordinates include an ellipsoidal
     *         height (i.e. are 3-D), or {@code false} if they are only 2-D.
     * @throws TransformException Should never happen.
     *
     * @since 3.16
     */
    private void testDerivative(final Ellipsoid ellipsoid, final boolean hasHeight) throws TransformException {
        transform = GeocentricTransform.create(ellipsoid, hasHeight);
        DirectPosition point = hasHeight ? new GeneralDirectPosition(-10, 40, 200) : new DirectPosition2D(-10, 40);
        /*
         * Derivative of the direct transform.
         */
        tolerance = 1E-2;
        derivativeDeltas = new double[] {toRadians(1.0 / 60) / 1852}; // Approximatively one metre.
        verifyDerivative(point.getCoordinate());
        /*
         * Derivative of the inverse transform.
         */
        point = transform.transform(point, null);
        transform = transform.inverse();
        tolerance = 1E-8;
        derivativeDeltas = new double[] {1}; // Approximatively one metre.
        verifyDerivative(point.getCoordinate());
    }

    /**
     * Tests the {@link GeocentricTransform#derivative} method on a sphere.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.16
     */
    @Test
    public void testDerivativeSphere() throws TransformException {
        testDerivative(DefaultEllipsoid.SPHERE, true);
        testDerivative(DefaultEllipsoid.SPHERE, false);
    }

    /**
     * Tests the {@link GeocentricTransform#derivative} method on an ellipsoid.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.16
     */
    @Test
    public void testDerivative() throws TransformException {
        testDerivative(DefaultEllipsoid.WGS84, true);
        testDerivative(DefaultEllipsoid.WGS84, false);
    }
}
