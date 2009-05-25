/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.test.Depend;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import static org.geotoolkit.referencing.operation.provider.ObliqueMercator.PARAMETERS;


/**
 * Tests the {@link ObliqueMercator} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@Depend(MercatorTest.class)
public class ObliqueMercatorTest extends ProjectionTestCase {
    /**
     * Creates a default test suite.
     */
    public ObliqueMercatorTest() {
        super(ObliqueMercator.class, null);
    }

    /**
     * Returns a new instance of {@link ObliqueMercator}.
     *
     * @param  cx The longitude of projection center.
     * @param  cy The latitude of projection center.
     * @param  azimuth The azimuth.
     * @return Newly created projection.
     */
    private static ObliqueMercator create(final double cx, final double cy, final double azimuth) {
        final ParameterValueGroup values = PARAMETERS.createValue();
        final DefaultEllipsoid ellipsoid = DefaultEllipsoid.WGS84;
        values.parameter("semi_major").setValue(ellipsoid.getSemiMajorAxis());
        values.parameter("semi_minor").setValue(ellipsoid.getSemiMinorAxis());
        values.parameter("azimuth").setValue(azimuth); // Given here because mandatory parameter.
        final ObliqueMercator.Parameters parameters = new ObliqueMercator.Parameters(PARAMETERS, values);
        parameters.longitudeOfCentre = cx;
        parameters.latitudeOfCentre  = cy;
        return new ObliqueMercator(parameters);
    }

    /**
     * Tests the estimation of error.
     *
     * @throws ProjectionException Should never happen.
     */
    @Test
    public void testErrorFitting() throws ProjectionException {
        for (int cx=-90; cx<=90; cx+=30) {
            for (int cy=-80; cy<=80; cy+=40) {
                for (int azimuth=-90; azimuth<=90; azimuth+=30) {
                    final ErrorFitting error = new ErrorFitting(create(cx, cy, azimuth));
                    transform = error.projection();
                    assertFalse(isSpherical());
                    validate();
                    error.fit(90, 90, 5);
                    // Nothing useful here, since I didn't found a good model.
                }
            }
        }
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
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Oblique Mercator");
        parameters.parameter("semi-major axis").setValue(6377298.556);
        parameters.parameter("semi-minor axis").setValue(6377298.556 * (1 - 1/300.8017));
        parameters.parameter("Latitude of projection centre").setValue(4.0);
        parameters.parameter("Longitude of projection centre").setValue(115.0);
        parameters.parameter("Azimuth of initial line").setValue(53 + (18 + 56.9537/60)/60);
        parameters.parameter("Angle from Rectified to Skew Grid").setValue(53 + (7 + 48.3685/60)/60);
        parameters.parameter("Scale factor on initial line").setValue(0.99984);
        parameters.parameter("Easting at projection centre").setValue(590476.87);
        parameters.parameter("Northing at projection centre").setValue(442857.65);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());
        assertWktEquals(
                "PARAM_MT[“Oblique_Mercator”,\n" +
                "  PARAMETER[“semi_major”, 6377298.556],\n" +
                "  PARAMETER[“semi_minor”, 6356097.550300896],\n" +
                "  PARAMETER[“longitude_of_center”, 109.6855202029758],\n" +
                "  PARAMETER[“latitude_of_center”, 4.0],\n" +
                "  PARAMETER[“azimuth”, 53.31582047222222],\n" +
                "  PARAMETER[“scale_factor”, 0.99984],\n" +
                "  PARAMETER[“false_easting”, 590476.87],\n" +
                "  PARAMETER[“false_northing”, 442857.65],\n" +
                "  PARAMETER[“rectified_grid_angle”, 53.13010236111111]]");

        final double[] point    = new double[] {115 + (48 + 19.8196/60)/60, 5 + (23 + 14.1129/60)/60};
        final double[] expected = new double[] {679245.73, 596562.78};
        tolerance = 0.005;
        verifyTransform(point, expected);
    }
}
