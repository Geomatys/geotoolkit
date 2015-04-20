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
import static org.geotoolkit.referencing.operation.projection.ProjectionTestBase.isSpherical;


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
//      assertFalse(isSpherical(transform));
        final double a = parameters.parameter("semi-major axis").doubleValue();
        final double b = parameters.parameter("semi-minor axis").doubleValue();
        final double r = b + (a-b)*cos(toRadians(latitudeOfOrigin));
        parameters.parameter("semi-major axis").setValue(r);
        parameters.parameter("semi-minor axis").setValue(r);
        tolerance = newTolerance * 1000;
        transform = null; // Force re-creation.
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testMercator1SP() throws FactoryException, TransformException {
        super.testMercator1SP();
        makeSpherical(0, 3);
        super.testMercator1SP();
//      assertTrue(isSpherical(transform));
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testMercator2SP() throws FactoryException, TransformException {
        super.testMercator2SP();
        makeSpherical(42, 20);
        super.testMercator2SP();
//      assertTrue(isSpherical(transform));
    }

    /**
     * Runs the GeoAPI tests and ensure that we used spherical formulas.
     */
    @Test
    @Override
    public void testPseudoMercator() throws FactoryException, TransformException {
        super.testPseudoMercator();
//      assertTrue(isSpherical(transform));
    }

    /**
     * Runs the GeoAPI tests and ensure that we used spherical formulas.
     */
    @Test
    @Override
    public void testMiller() throws FactoryException, TransformException {
        super.testMiller();
//      assertTrue(isSpherical(transform));
    }

    /**
     * Runs the GeoAPI tests and ensure that we used ellipsoidal formulas.
     */
    @Test
    @Override
    public void testHotineObliqueMercator() throws FactoryException, TransformException {
        super.testHotineObliqueMercator();
//      assertFalse(isSpherical(transform));
        // No spherical formulas for this one.
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testTransverseMercator() throws FactoryException, TransformException {
        super.testTransverseMercator();
        makeSpherical(49, 1);
        super.testTransverseMercator();
//      assertTrue(isSpherical(transform));
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testCassiniSoldner() throws FactoryException, TransformException {
        super.testCassiniSoldner();
        makeSpherical(10.45, 0.1);
        super.testCassiniSoldner();
//      assertTrue(isSpherical(transform));
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testLambertConicConformal1SP() throws FactoryException, TransformException {
        super.testLambertConicConformal1SP();
        makeSpherical(18, 0.05);
        super.testLambertConicConformal1SP();
//      assertTrue(isSpherical(transform));
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testLambertConicConformal2SP() throws FactoryException, TransformException {
        super.testLambertConicConformal2SP();
        makeSpherical(27.8, 0.5);
        super.testLambertConicConformal2SP();
//      assertTrue(isSpherical(transform));
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testLambertConicConformalBelgium() throws FactoryException, TransformException {
        super.testLambertConicConformalBelgium();
        makeSpherical(50, 20);
        super.testLambertConicConformalBelgium();
//      assertTrue(isSpherical(transform));
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testLambertAzimuthalEqualArea() throws FactoryException, TransformException {
        super.testLambertAzimuthalEqualArea();
        makeSpherical(52, 2);
        super.testLambertAzimuthalEqualArea();
//      assertTrue(isSpherical(transform));
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testPolarStereographicA() throws FactoryException, TransformException {
        super.testPolarStereographicA();
        makeSpherical(90, 20);
        super.testPolarStereographicA();
//      assertTrue(isSpherical(transform));
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testPolarStereographicB() throws FactoryException, TransformException {
        super.testPolarStereographicB();
        makeSpherical(-71, 10);
        super.testPolarStereographicB();
//      assertTrue(isSpherical(transform));
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testObliqueStereographic() throws FactoryException, TransformException {
        super.testObliqueStereographic();
        makeSpherical(52, 1);
        super.testObliqueStereographic();
//      assertTrue(isSpherical(transform));
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
//      assertTrue(isSpherical(transform));
    }

    /**
     * Runs the GeoAPI tests, then replace the ellipse by a sphere and runs the test again.
     */
    @Test
    @Override
    public void testKrovak() throws FactoryException, TransformException {
        super.testKrovak();
//      assertFalse(isSpherical(transform));
        // No spherical formulas for this one.
    }
}
