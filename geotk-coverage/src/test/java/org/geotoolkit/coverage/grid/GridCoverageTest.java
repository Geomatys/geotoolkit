/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.grid;

import org.apache.sis.coverage.grid.GridCoverage;
import org.junit.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;


/**
 * Tests the {@link GridCoverage} implementation.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 *
 * @deprecated Not used anymore in this project, to be replaced by Apache SIS test utilities.
 */
@Deprecated
public final strictfp class GridCoverageTest extends GridCoverageTestBase {
    /**
     * Creates a new test suite.
     */
    public GridCoverageTest() {
        super(GridCoverage.class);
    }

    /**
     * Tests a grid coverage filled with random values.
     */
    @Test
    public void testRandomCoverage() {
        createRandomCoverage();
        assertRasterEquals(coverage, coverage); // Actually a test of assertEqualRasters(...).
        assertFalse(coverage.getSampleDimensions().get(0).getTransferFunction().get().isIdentity());
    }
}
