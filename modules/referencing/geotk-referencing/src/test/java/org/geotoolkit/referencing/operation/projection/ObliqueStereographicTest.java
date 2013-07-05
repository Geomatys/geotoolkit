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
 * Tests the {@link ObliqueStereographic} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class ObliqueStereographicTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public ObliqueStereographicTest() {
        super(ObliqueStereographic.class, null);
    }

    /**
     * Creates a projection and derivates a few points.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     *
     * @since 3.19
     */
    @Test
    public void testDerivative() throws FactoryException, TransformException {
        tolerance = 1E-9;
        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};

        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Oblique Stereographic");
        parameters.parameter("semi-major axis").setValue(6377397.155);
        parameters.parameter("semi-minor axis").setValue(6377397.155 * (1 - 1/299.15281));
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());
        validate();
        verifyDerivative(toRadians( 5), toRadians(  5));
        verifyDerivative(toRadians(-5), toRadians( 15));
        verifyDerivative(toRadians(20), toRadians(-30));
    }

    /**
     * Runs the test defined in the GeoAPI-conformance module.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void runGeoapiTest() throws FactoryException, TransformException {
        new GeoapiTest(mtFactory).testObliqueStereographic();
    }
}
