/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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

import org.junit.*;
import org.geotoolkit.test.Depend;

import static org.junit.Assert.*;
import static org.geotoolkit.referencing.operation.provider.Polyconic.*;


/**
 * Tests the {@link Polyconic} class.
 *
 * @author Simon Reynard (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 */
@Depend({ProjectionParametersTest.class, UnitaryProjectionTest.class})
public final class PolyconicTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public PolyconicTest() {
        super(Polyconic.class, null);
    }

    /**
     * Returns a new instance of {@link Polyconic}.
     *
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @return Newly created projection.
     */
    private static Polyconic create(final boolean ellipse, final double latitudeOfOrigin) {
        final UnitaryProjection.Parameters parameters = parameters(PARAMETERS, ellipse);
        parameters.latitudeOfOrigin = latitudeOfOrigin;
        if (ellipse) {
            return new Polyconic(parameters);
        } else {
            return new Polyconic.Spherical(parameters);
        }
    }

    /**
     * Creates a projection using the {@link Polyconic} provider and
     * projects some points given in <A HREF="http://pubs.er.usgs.gov/usgspubs/pp/pp1395">
     * Table 19, p 132 of "Map Projections, a working manual" by John P.Snyder</A>.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testPolyconic() throws FactoryException, TransformException {
        tolerance = 0.5;
        testPolyconic(false);
    }

    /**
     * Creates a projection using the {@link Polyconic} provider and
     * projects some points given in <A HREF="http://pubs.er.usgs.gov/usgspubs/pp/pp1395">
     * Table 19, p 132 of "Map Projections, a working manual" by John P.Snyder</A>.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testPolyconicSphere() throws FactoryException, TransformException {
        tolerance = 20000; // TODO: revisit why the tolerance is so high.
        testPolyconic(true);
    }

    /**
     * Creates a projection using the {@link Polyconic} provider and
     * projects some points given in <A HREF="http://pubs.er.usgs.gov/usgspubs/pp/pp1395">
     * Table 19, p 132 of "Map Projections, a working manual" by John P.Snyder</A>.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    private void testPolyconic(final boolean sphere) throws FactoryException, TransformException {
        // The geographic coordinates to project.
        final double[] points = {
            0, 50,
            1, 49,
            2, 48,
            3, 47,
            0, 30,
            1, 29,
            2, 28,
            3, 27};

        // The expected projected coordinates.
        final double[] expected = {
                 0, 5540628,
             73172, 5429890,
            149239, 5320144,
            228119, 5211397,
                 0, 3319933,
             97440, 3209506,
            196719, 3099882,
            297742, 2991002};
        /*
         * Testing transform on ellipsoid case (Clarke 1866 ellipsoid)
         */
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Polyconic");
        parameters.parameter("semi-major axis").setValue(6378206.400);
        parameters.parameter("semi-minor axis").setValue(6378206.400 * (1 - 1/294.97870));
        parameters.parameter("Latitude of natural origin").setValue(0);
        parameters.parameter("Longitude of natural origin").setValue(0);
        parameters.parameter("False easting").setValue(0);
        parameters.parameter("False northing").setValue(0);
        transform = mtFactory.createParameterizedTransform(parameters);
        if (sphere) {
            spherical(parameters, 0);
        } else {
            assertEquals(sphere, isSpherical());
        }
        transform = mtFactory.createParameterizedTransform(parameters);
        verifyTransform(points, expected);
        /*
         * Testing inverse transform with same points.
         */
        final double[] buffer = new double[expected.length];
        transform = concatenated(create(false, 0));
        transform.inverse().transform(expected, 0, buffer, 0, expected.length/2);
        for (int i=0; i<buffer.length; i+=2) {
            assertEquals(points[i  ], buffer[i  ], tolerance);
            assertEquals(points[i+1], buffer[i+1], tolerance);
        }
    }
}
