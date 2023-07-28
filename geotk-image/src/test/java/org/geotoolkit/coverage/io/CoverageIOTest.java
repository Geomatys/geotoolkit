/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.coverage.io;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.test.image.ImageTestBase;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.opengis.referencing.datum.PixelInCell;


/**
 * Tests the {@link CoverageIO} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final class CoverageIOTest extends ImageTestBase {
    /**
     * Creates a new test suite.
     */
    public CoverageIOTest() {
        super(CoverageIO.class);
    }

    /**
     * Tests the {@link CoverageIO#read} and {@link CoverageIO#write} methods.
     *
     * @throws IOException If the test file can not be found.
     * @throws CoverageStoreException If an error occurred while reading of writing the file.
     */
    @Test
    public void testReadWrite() throws IOException, DataStoreException {
        final GridCoverage coverage = CoverageIO.read(CoverageIOTest.class.getResource("/org/geotoolkit/image/io/mosaic/test-data/A2.png"));
        verify(coverage.getGridGeometry(), 90, 90, 0);
        /*
         * Creates a temporary file for writing the image.
         */
        final File file = File.createTempFile("Geotk", ".png");
        final File wf = (File) IOUtilities.changeExtension(file, "pgw");
        try {
            CoverageIO.write(coverage, "png-wf", file);
        } finally {
            assertTrue(file.delete());
            assertTrue(wf.delete());
        }
    }

    /**
     * Verifies the <cite>Grid to CRS</cite> transform.
     *
     * @param geom   The grid geometry to test.
     * @param width  The expected image width.
     * @param height The expected image height.
     * @param ty     The <var>y</var> value of the upper-left corner, specified because the
     *               {@link #testReadWrite()} method use a different tile than the one in
     *               upper-left corner.
     */
    private static void verify(final GridGeometry geom, final int width, final int height, final int ty) {
        final AffineTransform gridToCRS = (AffineTransform) geom.getGridToCRS(PixelInCell.CELL_CENTER);
        assertEquals("scaleX",        1.0, gridToCRS.getScaleX(),     0);
        assertEquals("scaleY",       -1.0, gridToCRS.getScaleY(),     0);
        assertEquals("translateX", -180.0, gridToCRS.getTranslateX(), 0);
        assertEquals("translateY",     ty, gridToCRS.getTranslateY(), 0);

        final GridExtent range = geom.getExtent();
        assertEquals("Width",  width,  range.getSize(0));
        assertEquals("Height", height, range.getSize(1));
    }

}
