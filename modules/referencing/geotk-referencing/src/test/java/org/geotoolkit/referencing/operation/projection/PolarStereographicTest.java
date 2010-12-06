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


/**
 * Tests the {@link PolarStereographic} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@Depend(MercatorTest.class)
public class PolarStereographicTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public PolarStereographicTest() {
        super(PolarStereographic.class, null);
    }

    /**
     * Creates a projection using the provider and projects the
     * point given in the "example" section of EPSG documentation.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testVariantA() throws FactoryException, TransformException {
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Polar Stereographic (variant A)");
        parameters.parameter("semi-major axis").setValue(6378137.0);
        parameters.parameter("semi-minor axis").setValue(6378137.0 * (1 - 1/298.2572236));
        parameters.parameter("Latitude of natural origin").setValue(90);
        parameters.parameter("Longitude of natural origin").setValue(0);
        parameters.parameter("Scale factor at natural origin").setValue(0.994);
        parameters.parameter("False easting").setValue(2000000.00);
        parameters.parameter("False northing").setValue(2000000.00);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());
        final double[] point    = new double[] {44, 73};
        final double[] expected = new double[] {3320416.75, 632668.43};
        tolerance = 0.005;
        verifyTransform(point, expected);
    }

    /**
     * Creates a projection using the provider and projects the
     * point given in the "example" section of EPSG documentation.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testVariantB() throws FactoryException, TransformException {
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Polar Stereographic (variant B)");
        parameters.parameter("semi-major axis").setValue(6378137.0);
        parameters.parameter("semi-minor axis").setValue(6378137.0 * (1 - 1/298.2572236));
        parameters.parameter("Latitude of standard parallel").setValue(-71);
        parameters.parameter("Longitude of origin").setValue(70);
        parameters.parameter("False easting").setValue(6000000.00);
        parameters.parameter("False northing").setValue(6000000.00);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());
        final double[] point    = new double[] {120, -75};
        final double[] expected = new double[] {7255380.79, 7053389.56};
        tolerance = 0.005;
        verifyTransform(point, expected);
    }
}
