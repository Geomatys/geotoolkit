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

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.test.DependsOn;
import org.junit.*;

import static java.lang.StrictMath.*;
import static org.geotoolkit.referencing.operation.provider.Krovak.PARAMETERS;


/**
 * Tests the {@link Krovak} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 *
 * @since 3.00
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class KrovakTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public KrovakTest() {
        super(Krovak.class, null);
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

        tolerance = 1E-3;
        transform = Krovak.create(wrap(PARAMETERS), param);
        validate();

        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};

        verifyDerivative(toRadians( 0), toRadians( 0));
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
        new GeoapiTest(mtFactory).testKrovak();
    }
}
