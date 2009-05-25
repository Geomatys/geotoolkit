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


/**
 * Tests the {@link ObliqueStereographic} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@Depend(MercatorTest.class)
public class ObliqueStereographicTest extends ProjectionTestCase {
    /**
     * Creates a default test suite.
     */
    public ObliqueStereographicTest() {
        super(ObliqueStereographic.class, null);
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
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Oblique Stereographic");
        parameters.parameter("semi-major axis").setValue(6377397.155);
        parameters.parameter("semi-minor axis").setValue(6377397.155 * (1 - 1/299.15281));
        parameters.parameter("Latitude of natural origin").setValue(52 + (9 + 22.178/60)/60);
        parameters.parameter("Longitude of natural origin").setValue(5 + (23 + 15.500/60)/60);
        parameters.parameter("Scale factor at natural origin").setValue(0.9999079);
        parameters.parameter("False easting").setValue(155000.00);
        parameters.parameter("False northing").setValue(463000.00);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());
        final double[] point    = new double[] {6, 53};
        final double[] expected = new double[] {196105.283, 557057.739};
        tolerance = 0.001;
        verifyTransform(point, expected);
    }
}
