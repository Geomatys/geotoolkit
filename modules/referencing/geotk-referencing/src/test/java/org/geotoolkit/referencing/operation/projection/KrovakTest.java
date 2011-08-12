/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;
import org.geotoolkit.test.Depend;

import org.junit.*;
import static org.junit.Assert.*;
import static java.lang.StrictMath.*;
import static org.geotoolkit.referencing.operation.provider.Krovak.PARAMETERS;


/**
 * Tests the {@link Krovak} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 */
@Depend(MercatorTest.class)
public final class KrovakTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public KrovakTest() {
        super(Krovak.class, null);
    }

    /**
     * Creates a projection using the provider and projects the
     * point given in the "example" section of EPSG documentation.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     *
     * @deprecated This test is partially replaced by {@link org.opengis.test.referencing.MathTransformTest}.
     * The main difference is that the tolerance threshold used here is lower.
     */
    @Test
    @Deprecated
    public void testKnownPoint() throws FactoryException, TransformException {
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Krovak Oblique Conic Conformal");
        parameters.parameter("semi-major axis").setValue(6377397.155);
        parameters.parameter("semi-minor axis").setValue(6377397.155 * (1 - 1/299.15281));
        parameters.parameter("Latitude of projection centre").setValue(49.5);
        parameters.parameter("Longitude of origin").setValue(24 + 50.0/60);
        parameters.parameter("Latitude of pseudo standard parallel").setValue(78.5);
        parameters.parameter("Scale factor on pseudo standard parallel").setValue(0.99990);
        parameters.parameter("Co-latitude of cone axis").setValue(30 + (17 + 17.3031/60)/60);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());

        final double[] point    = new double[] {16 + (50 + 59.1790/60)/60, 50 + (12 + 32.4416/60)/60};
        final double[] expected = new double[] {-568990.997, -1050538.643};
        tolerance = 0.0005;
        verifyTransform(point, expected);
    }

    /**
     * Creates a projection and derivates a few points.
     *
     * @see {@link verifyDerivative} and {@link Matrix tolmat} for tolerance = 1E-2.
     * @throws TransformException Should never happen.
     *
     * @since 3.19
     */
    @Test
    public void testDerivative() throws TransformException {
        final ParameterValueGroup param = PARAMETERS.createValue();
        param.parameter("semi_major").setValue(6377397.155);
        param.parameter("semi_minor").setValue(6377397.155 * (1 - 1/299.15281));

        tolerance = 1E-2;
        transform = Krovak.create(PARAMETERS, param);
        validate();

        final double delta = toRadians((1.0 / 60) / 1852); // Approximatively one metre.
        derivativeDeltas = new double[] {delta, delta};

        verifyDerivative(toRadians( 0), toRadians( 0));
        verifyDerivative(toRadians( 5), toRadians(  5));
        verifyDerivative(toRadians(-5), toRadians( 15));
        verifyDerivative(toRadians(20), toRadians(-30));
    }
}
