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

import java.util.Collections;
import java.util.Random;
import javax.vecmath.Point3d;
import org.opengis.util.FactoryException;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.TransformException;
import org.geotoolkit.referencing.crs.PredefinedCRS;
import org.apache.sis.referencing.datum.DefaultEllipsoid;
import org.apache.sis.referencing.operation.transform.EllipsoidToCentricTransform;
import org.apache.sis.referencing.CommonCRS;
import org.junit.*;

import static org.junit.Assert.*;
import static java.lang.StrictMath.*;


/**
 * Tests the following transformation classes with the geocentric transform:
 * <p>
 * <ul>
 *   <li>{@link CoordinateOperation}</li>
 *   <li>{@link EllipsoidalToCartesian}</li>
 *   <li>{@link DefaultEllipsoid}</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
public final strictfp class GeocentricTransformTest extends TransformTestBase {
    /**
     * Creates the test suite.
     */
    public GeocentricTransformTest() {
        super(EllipsoidToCentricTransform.class, null);
    }

    /**
     * Tests the {@link EllipsoidalToCartesian} class created by {@link #opFactory}.
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
        final Ellipsoid                 ellipsoid = CommonCRS.WGS84.ellipsoid();
        final CoordinateReferenceSystem sourceCRS = PredefinedCRS.WGS84_3D;
        final CoordinateReferenceSystem targetCRS = PredefinedCRS.GEOCENTRIC;
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
                final DefaultEllipsoid ellip = DefaultEllipsoid.createFlattenedSphere(
                        Collections.singletonMap(Ellipsoid.NAME_KEY, "Temporary"),
                        ellipsoid.getSemiMajorAxis() + altitude,
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
}
