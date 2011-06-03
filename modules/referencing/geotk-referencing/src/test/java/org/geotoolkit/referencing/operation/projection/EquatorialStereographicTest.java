/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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

import java.awt.geom.Point2D;
import org.opengis.referencing.operation.TransformException;

import org.junit.*;
import static org.geotoolkit.referencing.operation.provider.Stereographic.*;


/**
 * Tests the {@link EquatorialStereographic} class.
 *
 * @author Rémi Maréchal (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 */
public final class EquatorialStereographicTest extends ProjectionTestBase {
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
        tolerance = 1E-1;
        final double delta = Math.toRadians((1.0 / 60) / 1852); // Approximatively one metre.
        final Point2D.Double point = new Point2D.Double(Math.toRadians(-4), Math.toRadians( 3));

        // Test spherical formulas.
        transform = create(false);
        validate();
        checkDerivative2D(point, delta);

        // Test ellipsoidal formulas.
        transform = create(true);
        validate();
        checkDerivative2D(point, delta);

        // Test ellipsoidal formulas.
        point.x = 20;
        point.y = 15;
        checkDerivative2D(point, delta);
    }
}
