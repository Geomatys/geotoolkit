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
package org.geotoolkit.coverage.io;

import java.io.File;
import java.io.IOException;
import java.awt.geom.AffineTransform;

import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridGeometry;

import org.geotoolkit.test.TestData;
import org.geotoolkit.test.image.ImageTestBase;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.image.io.mosaic.TileTest;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.io.plugin.WorldFileImageWriter;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests the {@link CoverageIO} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 */
public final class CoverageIOTest extends ImageTestBase {
    /**
     * Creates a new test suite.
     */
    public CoverageIOTest() {
        super(CoverageIO.class);
    }

    /**
     * Ensures that the World Files codecs are registered.
     */
    @BeforeClass
    public static void registerWorldFiles() {
        WorldFileImageReader.Spi.registerDefaults(null);
        WorldFileImageWriter.Spi.registerDefaults(null);
    }

    /**
     * Unregisters the World files codecs after the test suite.
     */
    @AfterClass
    public static void unregisterWorldFiles() {
        WorldFileImageWriter.Spi.unregisterDefaults(null);
        WorldFileImageReader.Spi.unregisterDefaults(null);
    }

    /**
     * Tests the {@link CoverageIO#read} and {@link CoverageIO#write} methods.
     *
     * @throws IOException If the test file can not be found.
     * @throws CoverageStoreException If an error occurred while reading of writing the file.
     */
    @Test
    public void testReadWrite() throws IOException, CoverageStoreException {
        final GridCoverage coverage = CoverageIO.read(TestData.file(TileTest.class, "A2.png"));
        final GridGeometry geom = coverage.getGridGeometry();
        final AffineTransform gridToCRS = (AffineTransform) geom.getGridToCRS();
        assertEquals(   1.0, gridToCRS.getScaleX(),     0);
        assertEquals(  -1.0, gridToCRS.getScaleY(),     0);
        assertEquals(-179.5, gridToCRS.getTranslateX(), 0);
        assertEquals(  -0.5, gridToCRS.getTranslateY(), 0);
        /*
         * Creates a temporary file for writing the image.
         */
        final File file = File.createTempFile("Geotk", ".png");
        final File wf = (File) IOUtilities.changeExtension(file, "pgw");
        try {
            CoverageIO.write(coverage, "png", file);
        } finally {
            assertTrue(file.delete());
            assertTrue(wf.delete());
        }
    }

    /**
     * Tests the {@link CoverageIO#createMosaicReader} method.
     *
     * @throws IOException If the test files can not be read.
     * @throws CoverageStoreException If an error occurred while building the mosaic.
     */
    @Test
    @Ignore
    public void testCreateMosaic() throws IOException, CoverageStoreException {
        final GridCoverageReader reader = CoverageIO.createMosaicReader(TestData.file(TileTest.class, null));
        // TODO
        reader.dispose();
    }
}
