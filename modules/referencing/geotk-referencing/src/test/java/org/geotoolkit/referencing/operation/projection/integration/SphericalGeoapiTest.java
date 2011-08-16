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
package org.geotoolkit.referencing.operation.projection.integration;

import org.geotoolkit.util.XArrays;
import org.geotoolkit.referencing.operation.projection.ProjectionTestBase;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;


/**
 * Runs the suite of tests provided in the GeoAPI project with spherical formulas instead than
 * the ellipsoidal ones. We relax the tolerance to 20 km, because this test suite compare the
 * transform results with the values expected by ellipsoidal formulas.
 * <p>
 * Actually, the main interest of this test is to ensure that the {@code assert} statements
 * inside the projection implementations (which compare the spherical calculations with the
 * ellipsoidal calculations) did not fail.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 */
@RunWith(JUnit4.class)
public final class SphericalGeoapiTest extends GeoapiTest {
    /**
     * Projections used by the GeoAPI test suites for which no spherical formulas are available.
     */
    private static final String[] NO_SPHERICAL = {
        "CRS S-JTSK (Ferro) / Krovak",
        "Timbalai 1948 / RSO Borneo (m)"
    };

    /**
     * Creates a new test suite using the singleton factory instance.
     */
    public SphericalGeoapiTest() {
        super(new SphericalMathTransformFactory());
    }

    /**
     * Relaxes the tolerance threshold before the test is run.
     */
    @Before
    public void relaxTolerance() {
        tolerance = 20000;
    }

    /**
     * Verifies that the formulas used by the {@code testFoo()} methods (inherited from GeoAPI)
     * where the expected ones. If this {@code SphericalGeoapiTest} class, we expect spherical
     * formulas. This test is geotk-specific.
     */
    @After
    @Override
    public void verifyFormulas() {
        assertEquals(nameOfTargetCRS, !XArrays.containsIgnoreCase(NO_SPHERICAL, nameOfTargetCRS),
                ProjectionTestBase.isSpherical(transform));
    }
}
