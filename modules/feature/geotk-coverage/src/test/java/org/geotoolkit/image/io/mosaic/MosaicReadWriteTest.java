/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.mosaic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.ComponentColorModel;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import org.geotoolkit.test.Commons;
import org.geotoolkit.test.TestData;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.io.TemporaryFile;
import org.geotoolkit.test.image.ImageTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests reading and writing of a mosaic. This tests is the only one performing
 * real read/write operations. The input tiles are opaque RGB images. The output
 * should be opaque as well, except in the case of {@link #testTransparency()}
 * which should have added an alpha channel.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.01
 */
public final strictfp class MosaicReadWriteTest extends ImageTestBase {
    /**
     * The width and height (in pixels) of source tiles.
     */
    private static final int S = 90;

    /**
     * Checksum of input files. Many checksums may be declared for the same
     * image because the sample model changed in different Java version:
     * <p>
     * <ol>
     *   <li>Java 6 update 17 and before</li>
     *   <li>Java 6 update 18 and after</li>
     * </ol>
     */
    private static final long[][] TILE_CHECKSUMS = {
        {3489461482L, 3995241366L},  // A1
        {3282954537L, 3034917950L},  // B1
        {3175519999L, 4097683706L},  // C1
        { 504243661L, 2349894252L},  // D1
        { 546121330L, 2654069663L},  // A2
        {3926870361L, 4125865354L},  // B2
        { 963864334L, 3858155692L},  // C2
        { 919637760L, 1047037325L}   // D2
    };

    /**
     * Potential checksums of the whole image.
     */
    public static final long[] IMAGE_CHECKSUMS = {1800014439L, 2327013649L, 3333171052L};

    /**
     * The source mosaic.
     */
    private TileManager sourceMosaic;

    /**
     * The directory which will contains the target tiles.
     * If non-null, this directory will be cleared after the test.
     */
    private File targetDirectory;

    /**
     * Creates a new test suite.
     */
    public MosaicReadWriteTest() {
        super(Tile.class);
    }

    /**
     * Clears the target directory. This method does not scan in sub-directories.
     */
    @After
    public void clearTargetDirectory() {
        if (targetDirectory != null) {
            final File[] files = targetDirectory.listFiles();
            if (files != null) {
                for (final File file : files) {
                    assertTrue(file.getPath(), file.delete());
                }
                assertTrue(targetDirectory.getPath(), targetDirectory.delete());
            }
            targetDirectory = null;
        }
    }

    /**
     * Initialize {@link #sourceMosaic}.
     *
     * @throws IOException Should not occur.
     */
    @Before
    public void setupSourceMosaic() throws IOException {
        final ImageReaderSpi spi = Formats.getReaderByFormatName("png", null);
        final TileManager[] managers = TileManagerFactory.DEFAULT.create(
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "A1.png"), 0, new Rectangle(0*S, 0, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "B1.png"), 0, new Rectangle(1*S, 0, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "C1.png"), 0, new Rectangle(2*S, 0, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "D1.png"), 0, new Rectangle(3*S, 0, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "A2.png"), 0, new Rectangle(0*S, S, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "B2.png"), 0, new Rectangle(1*S, S, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "C2.png"), 0, new Rectangle(2*S, S, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "D2.png"), 0, new Rectangle(3*S, S, S, S))
        );
        assertEquals(1, managers.length);
        sourceMosaic = managers[0];
    }

    /**
     * Performs a checksum on the input tiles. This is not yet the mosaic.
     * If this test fails, then all other tests in the file are likely to
     * fail as well.
     *
     * @throws IOException If an I/O error occurred.
     */
    @Test
    public void testInputTiles() throws IOException {
        int i=0;
        for (final Tile tile : sourceMosaic.getTiles()) {
            final ImageReader reader = tile.getImageReader();
            image = reader.read(0);
            Tile.dispose(reader);
            final String name = tile.getInputName();
            assertEquals(name, S, image.getWidth());
            assertEquals(name, S, image.getHeight());
            assertEquals(3, image.getSampleModel().getNumBands());
            assertEquals(Transparency.OPAQUE, image.getColorModel().getTransparency());
            assertCurrentChecksumEquals(name, TILE_CHECKSUMS[i++]);
        }
    }

    /**
     * Creates an image reader from the input mosaic and test reading the image.
     * First we read the image as a whole. Then we read each individual tile.
     * Finally we read at a few different subsampling a region values.
     * <p>
     * Note that this test does not tests the
     * {@linkplain MosaicImageReadParam#setSubsamplingChangeAllowed(boolean) subsampling changes}.
     * This is aimed to be a relatively simple and straightforward test.
     *
     * @throws IOException If an I/O error occurred.
     */
    @Test
    public void testInputMosaic() throws IOException {
        final MosaicImageReader reader = new MosaicImageReader();
        reader.setInput(sourceMosaic);
        assertEquals("Num images", 1, reader.getNumImages(false));
        assertEquals("Width",    4*S, reader.getWidth (0));
        assertEquals("Height",   2*S, reader.getHeight(0));
        assertTrue(reader.getRawImageType(0).getColorModel() instanceof ComponentColorModel);
        image = reader.read(0);
        assertEquals("Width",    4*S, image.getWidth ());
        assertEquals("Height",   2*S, image.getHeight());
        showCurrentImage("testInputMosaic()");
        assertCurrentChecksumEquals("testInputMosaic", IMAGE_CHECKSUMS);
        for (int i=0,y=0; y<2; y++) {
            for (int x=0; x<4; x++) {
                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceRegion(new Rectangle(x*S, y*S, S, S));
                image = reader.read(0, param);
                assertEquals(S, image.getWidth());
                assertEquals(S, image.getHeight());
                assertEquals(3, image.getSampleModel().getNumBands());
                assertEquals(Transparency.OPAQUE, image.getColorModel().getTransparency());
                assertCurrentChecksumEquals("Tile("+x+','+y+')', TILE_CHECKSUMS[i++]);
            }
        }
        /*
         * Loads the mosaic with a subsampling.
         */
        ImageReadParam param = reader.getDefaultReadParam();
        param.setSourceSubsampling(4, 2, 0, 0);
        image = reader.read(0, param);
        showCurrentImage("testInputMosaic() - subsampling");
        assertEquals("Width",  S, image.getWidth ());
        assertEquals("Height", S, image.getHeight());
        assertEquals("Checksum", 329430756L, Commons.checksum(image));
        /*
         * Loads a sub-region of the mosaic.
         */
        param = reader.getDefaultReadParam();
        param.setSourceRegion(new Rectangle(S/2, S/4, 3*S, S+S/2));
        image = reader.read(0, param);
        showCurrentImage("testInputMosaic() - subregion");
        assertEquals("Width",  3*S,   image.getWidth ());
        assertEquals("Height", S+S/2, image.getHeight());
        assertEquals("Checksum", 4259662989L, Commons.checksum(image));

        reader.dispose();
    }

    /**
     * Returns the builder to use for creating the target mosaic. The subsampling levels to create
     * must be supplied in argument. Some tests do not create tiles at the finest subsampling in
     * order to reduce the amount of data to write (and thus make the test slightly faster). The
     * levels {@code 3, 6, 9} create 3 levels with tiles of 30x30 pixels, except the last level
     * which will create an image of 20 pixels height.
     */
    private MosaicBuilder builder(final int... subsamplings) throws IOException {
        targetDirectory = new File(TemporaryFile.getSharedTemporaryDirectory(), "mosaic-test");
        assertTrue(targetDirectory.mkdir());
        final MosaicBuilder builder = new MosaicBuilder();
        builder.setUntiledImageBounds(new Rectangle(4*S, 2*S));
        builder.setTileDirectory(targetDirectory);
        builder.setTileSize(new Dimension(30, 30));
        builder.setSubsamplings(subsamplings);
        builder.setTileReaderSpi("png");
        return builder;
    }

    /**
     * Tests writing the mosaic. After the test, the created tiles will be
     * read and their pixel values compared against the expected checksum.
     *
     * @throws IOException If an I/O error occurred.
     */
    @Test
    public void testWriteMosaic() throws IOException {
        final MosaicImageWriter writer = new MosaicImageWriter();
        writer.setOutput(builder(3, 6, 9).createTileManager());
        writer.writeFromInput(sourceMosaic, null);
        writer.dispose();
        /*
         * Now ensure that the files that we created are the expected ones.
         * We expect PNG format.
         */
        final String[] files = {
            "L1_A1.png",  "L1_B1.png",  "L1_C1.png",  "L1_D1.png",
            "L1_A2.png",  "L1_B2.png",  "L1_C2.png",  "L1_D2.png",
            "L2_A1.png",  "L2_B1.png",  "L3_A1.png",  "L3_B1.png"
        };
        final long[][] checksums = {
            {3823973597L, 4171709854L},
            {1268989087L, 3973907355L},
            {  90746832L, 4072319012L},
            {3244609990L,  161414722L},
            {3803928042L, 3436002999L},
            {1195337429L,    4167432L},
            {3165544981L, 3079689620L},
            {2981893502L, 2387513289L},
            {1536584446L, 3113480928L},
            {1796661935L, 1246616471L},
            {2442125326L, 1852799903L},
            {1672159374L,   89694022L}
        };
        int i=0;
        final ImageReader reader = ImageIO.getImageReadersByFormatName("png").next();
        for (final String filename : files) {
            final File file = new File(targetDirectory, filename);
            assertTrue(filename, file.isFile());
            try (ImageInputStream in = ImageIO.createImageInputStream(file)) {
                assertNotNull("File not found", in);
                reader.setInput(in);
                image = reader.read(0);
            }
            assertEquals(3, image.getSampleModel().getNumBands());
            assertEquals(Transparency.OPAQUE, image.getColorModel().getTransparency());
            assertCurrentChecksumEquals(filename, checksums[i++]);
        }
        reader.dispose();
    }

    /**
     * Tests again writing a mosaic, but this time with an operation applied.
     * This operation add an alpha channel to the tiles.
     *
     * @throws IOException If an I/O error occurred.
     */
    @Test
    @org.junit.Ignore
    public void testTransparency() throws IOException {
        final TileManager targetMosaic = builder(1, 6).createTileManager();
        /*
         * The colors to replace by transparent pixels. There is a few occurrences of this color
         * on the last row of the source image. This artifact provides a convenient opportunity
         * for testing this operation. Note only a few black strips on the last row will be made
         * transparent - some will not be changed if they do not appear on a corner of a source
         * tile.
         */
        final Color[] opaqueColors = {
            Color.BLACK,
        };
        final MosaicImageWriter writer = new MosaicImageWriter();
        final MosaicImageWriteParam param = writer.getDefaultWriteParam();
        param.setOpaqueBorderFilter(opaqueColors);
        writer.setOutput(targetMosaic);
        writer.writeFromInput(sourceMosaic, param);
        writer.dispose();
        /*
         * Verifies that every tiles has an alpha channel.
         */
        for (final Tile tile : targetMosaic.getTiles()) {
            final ImageReader reader = tile.getImageReader();
            image = reader.read(0);
            Tile.dispose(reader);
            assertEquals(4, image.getSampleModel().getNumBands());
            assertEquals(Transparency.TRANSLUCENT, image.getColorModel().getTransparency());
        }
        /*
         * Now read the target mosaic as one single image.
         */
        final MosaicImageReader reader = new MosaicImageReader();
        reader.setInput(targetMosaic);
        image = reader.read(0);
        reader.dispose();
        assertEquals(4, image.getSampleModel().getNumBands());
        assertEquals(Transparency.TRANSLUCENT, image.getColorModel().getTransparency());
        assertCurrentChecksumEquals("testTransparency", IMAGE_CHECKSUMS);
        /*
         * Visual test (if enabled).
         */
        showCurrentImage("testTransparency()");
    }
}
