/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.junit.*;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;


import org.apache.sis.test.DependsOn;
import static java.lang.StrictMath.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link PolarStereographic} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class PolarStereographicTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public PolarStereographicTest() {
        super(PolarStereographic.class, null);
    }

    /**
     * Creates a projection and derivates a few points. The tolerance threshold is set
     * to a relatively high value (1E-2) because the tolerance matrix calculated by
     * {@link #verifyDerivative(double[])} is high anyway.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     *
     * @since 3.19
     */
    @Test
    public void testSphericalDerivative() throws TransformException, FactoryException {
        tolerance = 1E-9;

        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Polar Stereographic (variant A)");
        parameters.parameter("semi-minor axis").setValue(6378137.0);
        parameters.parameter("semi-major axis").setValue(6378137.0);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertTrue(isSpherical());
        validate();

        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};
        verifyDerivative(toRadians(-70), toRadians(90));
        verifyDerivative(toRadians(-60), toRadians(85));
        verifyDerivative(toRadians( 20), toRadians(80));
    }

    /**
     * Creates a projection and derivates a few points. The tolerance threshold is set
     * to a relatively high value (1E-2) because the tolerance matrix calculated by
     * {@link #verifyDerivative(double[])} is high anyway.
     *
     * @throws FactoryException Should never happen.
     * @throws TransformException Should never happen.
     *
     * @since 3.19
     */
    @Test
    public void testEllipsoidalDerivative() throws TransformException, FactoryException {
        tolerance = 1E-9;

        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Polar Stereographic (variant A)");
        parameters.parameter("semi-major axis").setValue(6378137.0);
        parameters.parameter("semi-minor axis").setValue(6378137.0 * (1 - 1/298.2572236));
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());
        validate();

        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};
        verifyDerivative(toRadians(-70), toRadians(90));
        verifyDerivative(toRadians(-60), toRadians(85));
        verifyDerivative(toRadians( 20), toRadians(80));
    }

    /**
     * Runs the tests defined in the GeoAPI-conformance module.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void runGeoapiTest() throws FactoryException, TransformException {
        new GeoapiTest(mtFactory).testPolarStereographicA();
        new GeoapiTest(mtFactory).testPolarStereographicB();
    }
}
