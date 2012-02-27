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

import java.io.IOException;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the {@link GridCoverage2D} implementation.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 */
public final strictfp class GridCoverageTest extends GridCoverageTestBase {
    /**
     * Creates a new test suite.
     */
    public GridCoverageTest() {
        super(GridCoverage2D.class);
    }

    /**
     * Tests a grid coverage filled with random values.
     */
    @Test
    public void testRandomCoverage() {
        createRandomCoverage();
        assertRasterEquals(coverage, coverage); // Actually a test of assertEqualRasters(...).
        assertSame(coverage.getRenderedImage(), coverage.getRenderableImage(0,1).createDefaultRendering());
        /*
         * Tests the creation of a "geophysics" view. This test make sure that the
         * 'geophysics' method do not creates more grid coverage than needed.
         */
        GridCoverage2D geophysics= coverage.view(ViewType.GEOPHYSICS);
        assertSame(coverage,       coverage.view(ViewType.PACKED));
        assertSame(coverage,     geophysics.view(ViewType.PACKED));
        assertSame(geophysics,   geophysics.view(ViewType.GEOPHYSICS));
        assertFalse( coverage.equals(geophysics));
        assertFalse( coverage.getSampleDimension(0).getSampleToGeophysics().isIdentity());
        assertTrue(geophysics.getSampleDimension(0).getSampleToGeophysics().isIdentity());
    }

    /**
     * Tests the serialization of a grid coverage.
     *
     * @throws IOException if an I/O operation was needed and failed.
     * @throws ClassNotFoundException Should never happen.
     */
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        loadSampleCoverage(SampleCoverage.SST);
        GridCoverage2D serial = serialize();
        assertNotSame(coverage, serial);
        assertEquals(GridCoverage2D.class, serial.getClass());
        // Compares the geophysics view for working around the
        // conversions of NaN values which may be the expected ones.
        coverage = coverage.view(ViewType.GEOPHYSICS);
        serial   = serial  .view(ViewType.GEOPHYSICS);
        assertRasterEquals(coverage, serial);
    }
}
