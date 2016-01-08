/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.test.referencing.ParameterizedTransformTest;

import org.geotoolkit.factory.FactoryFinder;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static java.lang.StrictMath.*;
import static org.opengis.test.Assert.*;


/**
 * Runs the suite of tests provided in the GeoAPI project. The test suite is run using
 * the {@link MathTransformFactory} instance registered in {@link FactoryFinder}. This
 * class modifies the tests in order to test both the ellipsoidal and spherical formulas.
 *
 * @author Martin Desruisseaux (Geomatys)
 *
 * @see org.apache.sis.util.iso.GeoapiTest
 * @see org.geotoolkit.referencing.factory.GeoapiTest
 * @see org.geotoolkit.referencing.factory.epsg.GeoapiTest
 * @see org.geotoolkit.referencing.operation.transform.GeoapiTest
 * @see org.geotoolkit.referencing.operation.projection.GeoapiTest
 * @see org.geotoolkit.GeoapiTest
 *
 * @since 3.19
 */
@RunWith(JUnit4.class)
public final strictfp class GeoapiTest extends ParameterizedTransformTest {
    /**
     * Creates a new test suite using the singleton factory instance.
     */
    public GeoapiTest() {
        super(FactoryFinder.getMathTransformFactory(null));
    }

    /**
     * Creates a new test suite using the given factory.
     */
    GeoapiTest(final MathTransformFactory factory) {
        super(factory);
    }

    /**
     * Every map projections shall be instances of {@link MathTransform2D}.
     * Note that some tests inherited from the parent class are not about
     * map projections.
     */
    @After
    public void ensureMathTransform2D() {
        final MathTransform tr = transform;
        if (tr != null && tr.getSourceDimensions() == 2 && tr.getTargetDimensions() == 2) {
            assertInstanceOf("Unexpected implementation.", MathTransform2D.class, tr);
        }
    }

    /**
     * Modifies the parameters in order to replace the ellipsoidal implementation by the
     * spherical implementation. This method is invoked before to test again the transform.
     * It replaces the semi-axis length in the parameter values by a spherical radius.
     *
     * @param latitudeOfOrigin The latitude of origin (may be approximative), in degrees.
     * @param newTolerance The new tolerance threshold, in kilometres.
     */
    private void makeSpherical(final double latitudeOfOrigin, final double newTolerance) {
        ensureMathTransform2D();
        final double a = parameters.parameter("semi-major axis").doubleValue();
        final double b = parameters.parameter("semi-minor axis").doubleValue();
        final double r = b + (a-b)*cos(toRadians(latitudeOfOrigin));
        parameters.parameter("semi-major axis").setValue(r);
        parameters.parameter("semi-minor axis").setValue(r);
        tolerance = newTolerance * 1000;
        transform = null; // Force re-creation.
    }

    /**
     * Runs the GeoAPI tests and ensure that we used ellipsoidal formulas.
     */
    @Test
    @Override
    public void testHotineObliqueMercator() throws FactoryException, TransformException {
        super.testHotineObliqueMercator();
        // No spherical formulas for this one.
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Ignore
    @Override
    public void testCassiniSoldner() throws FactoryException, TransformException {
        super.testCassiniSoldner();
        makeSpherical(10.45, 0.1);
        super.testCassiniSoldner();
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Ignore
    @Override
    public void testLambertAzimuthalEqualArea() throws FactoryException, TransformException {
        super.testLambertAzimuthalEqualArea();
        makeSpherical(52, 2);
        super.testLambertAzimuthalEqualArea();
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testPolyconic() throws FactoryException, TransformException {
        super.testPolyconic();
        makeSpherical(0, 30);
        super.testPolyconic();
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testKrovak() throws FactoryException, TransformException {
        super.testKrovak();
        // No spherical formulas for this one.
    }
}
