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

import org.opengis.referencing.operation.TransformException;

import org.apache.sis.test.DependsOn;
import org.junit.*;
import static java.lang.StrictMath.*;
import static org.geotoolkit.referencing.operation.provider.Stereographic.PARAMETERS;


/**
 * Tests the {@link EquatorialStereographic} class.
 *
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @since 3.18
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class EquatorialStereographicTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public EquatorialStereographicTest() {
        super(EquatorialStereographic.class, null);
    }

    /**
     * Returns a new instance of {@link EquatorialStereographic}.
     *
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @return Newly created projection.
     */
    private static EquatorialStereographic create(final boolean ellipse) {
        final UnitaryProjection.Parameters parameters = parameters(PARAMETERS, ellipse);
        if (ellipse) {
            return new EquatorialStereographic(parameters);
        } else {
            return new EquatorialStereographic.Spherical(parameters);
        }
    }

    /**
     * Creates a projection and tests the derivatives at a few points.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testDerivative() throws TransformException {
        tolerance = 1E-9;
        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};

        // Test spherical formulas.
        transform = create(false);
        validate();
        verifyDerivative(toRadians(-4), toRadians(3));

        // Test ellipsoidal formulas.
        transform = create(true);
        validate();
        verifyDerivative(toRadians(-4), toRadians( 3));
        verifyDerivative(toRadians(20), toRadians(15));
    }
}
