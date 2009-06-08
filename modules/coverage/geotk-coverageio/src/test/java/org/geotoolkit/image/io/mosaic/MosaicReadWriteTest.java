/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.mosaic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.RenderedImage;
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
import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.internal.image.io.Formats;
import org.geotoolkit.internal.rmi.RMI;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests reading and writing of a mosaic. This tests is the only one performing
 * real read/write operations. The input tiles are opaque RGB images. The output
 * should be opaque as well, except in the case of {@link #testTransparency()}
 * which should have added an alpha channel.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.01
 */
public final class MosaicReadWriteTest {
    /**
     * The width and height (in pixels) of source tiles.
     */
    private static final int S = 90;

    /**
     * Checksum of input files.
     */
    private static final long[] TILE_CHECKSUMS = {
        3489461482L,  // A1
        3282954537L,  // B1
        3175519999L,  // C1
         504243661L,  // D1
         546121330L,  // A2
        3926870361L,  // B2
         963864334L,  // C2
         919637760L   // D2
    };

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
        Registry.setDefaultCodecPreferences();
        final ImageReaderSpi spi = Formats.getReaderByFormatName("png");
        final TileManager[] managers = TileManagerFactory.DEFAULT.create(new Tile[] {
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "A1.png"), 0, new Rectangle(0*S, 0, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "B1.png"), 0, new Rectangle(1*S, 0, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "C1.png"), 0, new Rectangle(2*S, 0, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "D1.png"), 0, new Rectangle(3*S, 0, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "A2.png"), 0, new Rectangle(0*S, S, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "B2.png"), 0, new Rectangle(1*S, S, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "C2.png"), 0, new Rectangle(2*S, S, S, S)),
            new Tile(spi, TestData.url(MosaicReadWriteTest.class, "D2.png"), 0, new Rectangle(3*S, S, S, S))
        });
        assertEquals(1, managers.length);
        sourceMosaic = managers[0];
    }

    /**
     * Performs a checksum on the input tiles. This is not yet the mosaic.
     * If this test fails, then all other tests in the file are likely to
     * fail as well.
     *
     * @throws IOException If an I/O error occured.
     */
    @Test
    public void testInputTiles() throws IOException {
        int i=0;
        for (final Tile tile : sourceMosaic.getTiles()) {
            final ImageReader reader = tile.getImageReader();
            final RenderedImage image = reader.read(0);
            Tile.dispose(reader);
            final String name = tile.getInputName();
            assertEquals(name, S, image.getWidth());
            assertEquals(name, S, image.getHeight());
            assertEquals(3, image.getSampleModel().getNumBands());
            assertEquals(Transparency.OPAQUE, image.getColorModel().getTransparency());
            assertEquals(name, TILE_CHECKSUMS[i++], Commons.checksum(image));
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
     * @throws IOException If an I/O error occured.
     */
    @Test
    public void testInputMosaic() throws IOException {
        final MosaicImageReader reader = new MosaicImageReader();
        reader.setInput(sourceMosaic);
        assertEquals("Num images", 1, reader.getNumImages(false));
        assertEquals("Width",    4*S, reader.getWidth (0));
        assertEquals("Height",   2*S, reader.getHeight(0));
        assertTrue(reader.getRawImageType(0).getColorModel() instanceof ComponentColorModel);
        RenderedImage image = reader.read(0);
        assertEquals("Width",    4*S, image.getWidth ());
        assertEquals("Height",   2*S, image.getHeight());
        assertEquals("Checksum", 1800014439L, Commons.checksum(image));
        for (int i=0,y=0; y<2; y++) {
            for (int x=0; x<4; x++) {
                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceRegion(new Rectangle(x*S, y*S, S, S));
                image = reader.read(0, param);
                assertEquals(S, image.getWidth());
                assertEquals(S, image.getHeight());
                assertEquals(3, image.getSampleModel().getNumBands());
                assertEquals(Transparency.OPAQUE, image.getColorModel().getTransparency());
                assertEquals(TILE_CHECKSUMS[i++], Commons.checksum(image));
            }
        }
        /*
         * Loads the mosaic with a subsampling.
         */
        ImageReadParam param = reader.getDefaultReadParam();
        param.setSourceSubsampling(4, 2, 0, 0);
        image = reader.read(0, param);
        assertEquals("Width",  S, image.getWidth ());
        assertEquals("Height", S, image.getHeight());
        assertEquals("Checksum", 329430756L, Commons.checksum(image));
        /*
         * Loads a sub-region of the mosaic.
         */
        param = reader.getDefaultReadParam();
        param.setSourceRegion(new Rectangle(S/2, S/4, 3*S, S+S/2));
        image = reader.read(0, param);
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
        targetDirectory = new File(RMI.getSharedTemporaryDirectory(), "mosaic-test");
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
     * @throws IOException If an I/O error occured.
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
        final long[] checksums = {
            3823973597L,  1268989087L,    90746832L,  3244609990L,
            3803928042L,  1195337429L,  3165544981L,  2981893502L,
            1536584446L,  1796661935L,  2442125326L,  1672159374L
        };
        int i=0;
        final ImageReader reader = ImageIO.getImageReadersByFormatName("png").next();
        for (final String filename : files) {
            final File file = new File(targetDirectory, filename);
            assertTrue(filename, file.isFile());
            final ImageInputStream in = ImageIO.createImageInputStream(file);
            reader.setInput(in);
            final RenderedImage image = reader.read(0);
            in.close();
            assertEquals(3, image.getSampleModel().getNumBands());
            assertEquals(Transparency.OPAQUE, image.getColorModel().getTransparency());
            assertEquals(filename, checksums[i++], Commons.checksum(image));
        }
        reader.dispose();
    }

    /**
     * Tests again writing a mosaic, but this time with an operation applied.
     * This operation add an alpha channel to the tiles.
     *
     * @throws IOException If an I/O error occured.
     */
    @Test
    public void testTransparency() throws IOException {
        final TileManager targetMosaic = builder(1, 6).createTileManager();
        /*
         * The colors found in the 4 corners of every tiles. We will try to make those
         * colors transparent. This is an unrealist test since those pixels are mostly
         * ocean colors. A real work would be to replace a uniform black or white, but
         * we are only interrested in checking that the calculation is really done.
         */
        final Color[] cornerColors = {
            new Color(  0,   0,   0),
            new Color(  1,   5,  20),
            new Color(  2,   5,  20),
            new Color(  2,   5,  21),
            new Color(  2,   7,  24),
            new Color(  2,   7,  25),
            new Color(  5,  16,  42),
            new Color(  6,  17,  44),
            new Color(  6,  18,  45),
            new Color(  6,  18,  47),
            new Color(  6,  19,  47),
            new Color(  8,  22,  54),
            new Color(  9,  27,  64),
            new Color( 14,  40,  85),
            new Color( 17,  47,  95),
            new Color( 17,  47,  96),
            new Color( 19,  50, 102),
            new Color( 22,  59, 113),
            new Color(255, 255, 255),
        };
        final MosaicImageWriter writer = new MosaicImageWriter();
        final MosaicImageWriteParam param = writer.getDefaultWriteParam();
        param.setOpaqueBorderFilter(cornerColors);
        writer.setOutput(targetMosaic);
        writer.writeFromInput(sourceMosaic, param);
        writer.dispose();
        /*
         * Verifies that every tiles has an alpha channel.
         */
        for (final Tile tile : targetMosaic.getTiles()) {
            final ImageReader reader = tile.getImageReader();
            final RenderedImage image = reader.read(0);
            Tile.dispose(reader);
            assertEquals(4, image.getSampleModel().getNumBands());
            assertEquals(Transparency.TRANSLUCENT, image.getColorModel().getTransparency());
        }
        /*
         * Now read the target mosaic as one single image.
         */
        final MosaicImageReader reader = new MosaicImageReader();
        reader.setInput(targetMosaic);
        final RenderedImage image = reader.read(0);
        reader.dispose();
        assertEquals(4, image.getSampleModel().getNumBands());
        assertEquals(Transparency.TRANSLUCENT, image.getColorModel().getTransparency());
        /*
         * Do not test the checksum, since its value may vary with the ImageWorker
         * implementation. If a visual test is wanted, enable the block below.
         */
        if (false) {
            ImageIO.write(image, "png", new File("testTransparency.png"));
        }
    }
}
