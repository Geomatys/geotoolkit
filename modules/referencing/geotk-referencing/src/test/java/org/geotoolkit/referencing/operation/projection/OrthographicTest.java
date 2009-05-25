/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.test.Depend;
import org.geotoolkit.referencing.operation.transform.CoordinateDomain;
import static org.geotoolkit.referencing.operation.provider.Orthographic.PARAMETERS;


/**
 * Tests the {@link Orthographic} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@Depend(MercatorTest.class)
public class OrthographicTest extends ProjectionTestCase {
    /**
     * Tolerance level for comparing floating point numbers.
     */
    private static final double TOLERANCE = 1E-11;

    /**
     * Creates a default test suite.
     */
    public OrthographicTest() {
        super(Orthographic.class, null);
    }

    /**
     * Returns a new instance of {@link Orthographic}.
     *
     * @param  cx Longitude of projection centre.
     * @param  cY Latitude of projection centre.
     * @return Newly created projection.
     */
    private static Orthographic create(final double cx, final double cy) {
        final UnitaryProjection.Parameters parameters = parameters(PARAMETERS, false);
        parameters.centralMeridian  = cx;
        parameters.latitudeOfOrigin = cy;
        return new Orthographic(parameters);
    }

    /**
     * Tests the unitary equatorial projection on a sphere.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testEquatorial() throws TransformException {
        transform = create(0, 0);
        tolerance = TOLERANCE;
        validate();
        assertTrue(isSpherical());
        stress(CoordinateDomain.GEOGRAPHIC_RADIANS_HALF, 209067359);
    }

    /**
     * Tests the unitary polar projection on a sphere.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testPolar() throws TransformException {
        boolean south = false;
        do {
            transform = create(0, south ? -90 : 90);
            tolerance = TOLERANCE;
            validate();
            assertTrue(isSpherical());
            /*
             * Note: we would expect CoordinateDomain.GEOGRAPHIC_RADIANS_SOUTH in the South case,
             * but then the latitudes are multiplied by -1 by the normalize affine transform. The
             * result is equivalent to using positive latitudes in the first place.
             */
            stress(CoordinateDomain.GEOGRAPHIC_RADIANS_NORTH, 753524735);
        } while ((south = !south) == true);
    }

    /**
     * Tests the estimation of error.
     *
     * @throws ProjectionException Should never happen.
     */
    @Test
    public void testErrorFitting() throws ProjectionException {
        final ErrorFitting error = new ErrorFitting(create(0, 0));
        transform = error.projection();
        validate();
        error.fit(90, 90);
        // Northing usefull here; finding a model would require more work.
    }
}
