/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.referencing.operation.TransformException;

import org.apache.sis.test.DependsOn;
import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.operation.provider.NewZealandMapGrid.PARAMETERS;


/**
 * Tests the {@link NewZealandMapGrid} implementation.
 *
 * @author Justin Deoliveira (Refractions)
 * @version 3.00
 *
 * @since 2.2
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class NewZealandMapGridTest extends ProjectionTestBase {
    /**
     * Sets of geographic coordinates to project.
     */
    private static final double[] GEOGRAPHIC = {
        172.739194,  -34.444066,
        172.723106,  -40.512409,
        169.172062,  -46.651295
    };

    /**
     * Set of projected coordinates.
     */
    private static final double[] PROJECTED = {
        2487100.638,  6751049.719,
        2486533.395,  6077263.661,
        2216746.425,  5388508.765
    };

    /**
     * Creates a default test suite.
     */
    public NewZealandMapGridTest() {
        super(NewZealandMapGrid.class, null);
    }

    /**
     * Creates the projection.
     */
    @Before
    public void createProjection() {
        transform = NewZealandMapGrid.create(PARAMETERS, PARAMETERS.createValue());
    }

    /**
     * Computes the forward transform and compares against the expected result.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testTransform() throws TransformException {
        final double[] dst = new double[6];
        transform.transform(GEOGRAPHIC, 0, dst, 0, 3);
        for (int i=0; i<PROJECTED.length; i++) {
            assertEquals(PROJECTED[i], dst[i], 0.1);   // 10 cm precision
        }
    }

    /**
     * Computes the inverse transform and compares against the expected result.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testInverseTransform() throws TransformException {
        final double[] dst = new double[6];
        transform.inverse().transform(PROJECTED, 0, dst, 0, 3);
        for (int i=0; i<GEOGRAPHIC.length; i++) {
            assertEquals(GEOGRAPHIC[i], dst[i], 0.0001); // About 10 m precision
        }
    }

    /**
     * Tests WKT formatting.
     */
    @Test
    public void testWKT() {
        final String wkt = transform.toWKT();
        assertTrue(wkt.indexOf("central_meridian") >= 0);
    }
}
