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

import java.io.File;
import java.io.IOException;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;

import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.coverage.grid.GridEnvelope;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.TestData;
import org.geotoolkit.test.image.ImageTestBase;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.image.io.mosaic.TileTest;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.io.plugin.WorldFileImageWriter;
import org.geotoolkit.image.io.mosaic.MosaicReadWriteTest;

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
@DependsOn(MosaicReadWriteTest.class)
public final strictfp class CoverageIOTest extends ImageTestBase {
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
    @Ignore
    public void testReadWrite() throws IOException, CoverageStoreException {
        final GridCoverage coverage = CoverageIO.read(TestData.file(TileTest.class, "A2.png"));
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
        final AffineTransform gridToCRS = (AffineTransform) geom.getGridToCRS();
        assertEquals("scaleX",        1.0, gridToCRS.getScaleX(),     0);
        assertEquals("scaleY",       -1.0, gridToCRS.getScaleY(),     0);
        assertEquals("translateX", -179.5, gridToCRS.getTranslateX(), 0);
        assertEquals("translateY", ty-0.5, gridToCRS.getTranslateY(), 0);

        final GridEnvelope range = geom.getExtent();
        assertEquals("Width",  width,  range.getSpan(0));
        assertEquals("Height", height, range.getSpan(1));
    }

    /**
     * Verifies the metadata provided by the given reader.
     */
    private void verify(final GridCoverageReader reader) throws CoverageStoreException {
        assertEquals("Number of expected images.", 1, reader.getCoverageNames().size());
        verify(reader.getGridGeometry(0), 360, 180, 90);
        image = reader.read(0, null).getRenderableImage(0, 1).createDefaultRendering();
        assertCurrentChecksumEquals("verify", MosaicReadWriteTest.IMAGE_CHECKSUMS);
    }

    /**
     * Tests the {@link CoverageIO#createMosaicReader} method without new generation of new
     * mosaic.
     *
     * @throws IOException If the test files can not be read.
     * @throws CoverageStoreException If an error occurred while building the mosaic.
     */
    @Test
    public void testCreateMosaic() throws IOException, CoverageStoreException {
        final File directory = TestData.file(TileTest.class, null);
        final GridCoverageReader reader = CoverageIO.createMosaicReader(directory, null);
        final TileManager manager = (TileManager) reader.getInput();
        assertEquals("Expected the 8 original tiles.", 8, manager.getTiles().size());
        verify(reader);
        reader.dispose();
        showCurrentImage("testCreateMosaic()");
    }

    /**
     * Tests the {@link CoverageIO#createMosaicReader} method. The test is run twice.
     * The first execution will create the new {@code ".tiles"} directory. The second
     * execution will reuse the cache without creating new images.
     *
     * @throws IOException If the test files can not be read.
     * @throws CoverageStoreException If an error occurred while building the mosaic.
     */
    @Test
    public void testWriteOrReuseMosaic() throws IOException, CoverageStoreException {
        boolean cleaned = false;
        final File directory = TestData.file(TileTest.class, null);
        final File cacheDirectory = new File(directory.getPath() + MosaicCoverageReader.CACHE_EXTENSION);
        assertFalse("The cache directory should not exist prior this test.", cacheDirectory.exists());
        try {
            for (int step=0; step<2; step++) {
                final GridCoverageReader reader = CoverageIO.writeOrReuseMosaic(directory);
                assertEquals("Cache status: ", step == 0, ((MosaicCoverageReader) reader).saved);

                final TileManager manager = (TileManager) reader.getInput();
                assertEquals("Because the sample tiles are very small, MosaicBuilder should have built" +
                             "a single tile of size 360x180 pixels.", 1, manager.getTiles().size());
                verify(reader);

                final File tile = (File) manager.getTiles().iterator().next().getInput();
                image = ImageIO.read(tile);
                assertEquals("Width",  360, image.getWidth());
                assertEquals("Height", 180, image.getHeight());
                assertCurrentChecksumEquals("testInputMosaic", MosaicReadWriteTest.IMAGE_CHECKSUMS);
                reader.dispose();
            }
        } finally {
            cleaned = TestData.deleteRecursively(cacheDirectory);
        }
        assertTrue("Can't delete the cache directory.", cleaned);
        showCurrentImage("testWriteOrReuseMosaic()");
    }
}
