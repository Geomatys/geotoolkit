/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import org.geotoolkit.test.Depend;

import static org.junit.Assert.*;
import static org.geotoolkit.referencing.operation.provider.CassiniSoldner.*;


/**
 * Tests the {@link Cassini-Soldner} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@Depend(MercatorTest.class)
public class CassiniSoldnerTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public CassiniSoldnerTest() {
        super(CassiniSoldner.class, null);
    }

    /**
     * Returns a new instance of {@link CassiniSoldner}.
     *
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @return Newly created projection.
     */
    private static CassiniSoldner create(final boolean ellipse) {
        final UnitaryProjection.Parameters parameters = parameters(PARAMETERS, ellipse);
        if (ellipse) {
            return new CassiniSoldner(parameters);
        } else {
            return new CassiniSoldner.Spherical(parameters);
        }
    }

    /**
     * Tests the estimation of error.
     *
     * @throws ProjectionException Should never happen.
     */
    @Test
    public void testErrorFitting() throws ProjectionException {
        final double TOLERANCE = 0.0006;
        boolean ellipse = true;
        do {
            final ErrorFitting error = new ErrorFitting(create(ellipse));
            transform = error.projection();
            assertEquals(!ellipse, isSpherical());
            validate();
            error.fit(6, 90);
            assertEquals(2353, error.delta.count());
            assertEquals(0.0,   error.delta.minimum(), TOLERANCE);
            assertEquals(0.0,   error.delta.maximum(), TOLERANCE);
            assertEquals(0.0,   error.delta.mean(),    TOLERANCE);
            assertEquals(0.0,   error.delta.rms(),     TOLERANCE);
        } while ((ellipse = !ellipse) == false);
    }

    /**
     * Creates a projection using the provider and projects the
     * point given in the "example" section of EPSG documentation.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testKnownPoint() throws FactoryException, TransformException {
        final double feets = 0.3048;        // Conversion from feets to metres.
        final double links = 0.66 * feets;  // Conversion from links to metres
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Cassini-Soldner");
        parameters.parameter("semi-major axis").setValue(20926348 * feets);
        parameters.parameter("semi-minor axis").setValue(20855233 * feets);
        parameters.parameter("Latitude of natural origin").setValue(10 + (26 + 30.0/60)/60);
        parameters.parameter("Longitude of natural origin").setValue(-(61 + 20.0/60));
        parameters.parameter("False easting") .setValue(430000.00 * links);
        parameters.parameter("False northing").setValue(325000.00 * links);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());

        final double[] point    = new double[] {-62, 10};
        final double[] expected = new double[] {66644.94 * links, 82536.22 * links};
        tolerance = 0.01 / links;
        verifyTransform(point, expected);
    }
}
