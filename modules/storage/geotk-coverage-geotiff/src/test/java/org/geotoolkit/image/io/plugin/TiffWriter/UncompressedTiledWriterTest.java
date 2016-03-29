/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2014, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2014, Geomatys
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
package org.geotoolkit.image.io.plugin.TiffWriter;

import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageWriteParam;

import org.junit.Test;

import org.geotoolkit.image.io.plugin.TiffImageWriter;

/**
 * {@link UncompressedTiffWriterTest} implementation which write image by tiles.
 *
 * @author Remi Marechal (Geomatys).
 */
public class UncompressedTiledWriterTest extends UncompressedTiffWriterTest {

    public UncompressedTiledWriterTest() throws IOException {
        super();
        writerParam.setTilingMode(ImageWriteParam.MODE_EXPLICIT);

        final int tileWidth  = (random.nextInt(7) + 1) * 16;
        final int tileHeight = (random.nextInt(7) + 1) * 16;
        writerParam.setTiling(tileWidth, tileHeight, 0, 0);
    }

    /**
     * Verify writing conformity when writing only one tile which is equals to image boundary.<br>
     * Concretely check writing conformity when tile offset and tile byte count values are contained into tag.
     *
     * @see TiffImageWriter#writeImageByTiles(java.awt.image.RenderedImage, javax.imageio.ImageWriteParam)
     * @see TiffImageWriter#writeByteCountAndOffsets(long, short, java.lang.Object, long, short, java.lang.Object)
     */
    @Test
    public void singleTileTest() throws IOException {

        //-- 1 band byte --//
        testSingleTile("singleTileTest : 1 band Byte", Byte.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);

        //-- 1 band short --//
        testSingleTile("singleTileTest : 1 band Short", Short.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);

        //-- 1 band int --//
        testSingleTile("singleTileTest : 1 band Integer", Integer.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);

        //-- 1 band Float --//
        testSingleTile("singleTileTest : 1 band Float", Float.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP);

        //-- 1 band double --//
        testSingleTile("singleTileTest : 1 Double Byte", Double.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP);


        //-- 3 bands RGB --//
        testSingleTile("singleTileTest : 3 bands Byte", Byte.SIZE, 3, PHOTOMETRIC_RGB, SAMPLEFORMAT_UINT);
        //-- 4 band RGB --//
        testSingleTile("singleTileTest : 4 bands Byte", Byte.SIZE, 4, PHOTOMETRIC_RGB, SAMPLEFORMAT_UINT);

        //--Color Map --//
        //-- 1 band byte --//
        testSingleTile("singleTileTest : 3 bands Byte Color Map", Byte.SIZE, 3, PHOTOMETRIC_PALETTE, SAMPLEFORMAT_UINT);
        //-- 1 band byte --//
        testSingleTile("singleTileTest : 4 bands Byte Color Map", Byte.SIZE, 4, PHOTOMETRIC_PALETTE, SAMPLEFORMAT_UINT);
    }


    /**
     * Write and read an image without any subsampling or other, and with only one tile.
     *
     * @param message
     * @param sampleBitsSize
     * @param numBand
     * @param photometricInterpretation
     * @param sampleFormat
     * @throws IOException
     */
    private void testSingleTile(final String message, final int sampleBitsSize, final int numBand,
            final short photometricInterpretation, final short sampleFormat) throws IOException {
        final File fileTest = File.createTempFile(message, "tiff", tempDir);
        writer.setOutput(fileTest); //-- to initialize writer

        //-- tile param mode already in explicit mode define into this class constructor.
        final int width  = writerParam.getTileWidth();
        final int height = writerParam.getTileHeight();

        /*
         * Image volontary created with planarconfiguration equal 1 to don't build banded sampleModel
         * because with banded sample model one tile offset for each band and the aim of this test is
         * to have only a single tile offset tiff tag.
         * N = TilesPerImage for PlanarConfiguration = 1; N = SamplesPerPixel * TilesPerImage for PlanarConfiguration = 2
         */
        final WritableRenderedImage sourceImage = createImageTest(width, height, sampleBitsSize, numBand, photometricInterpretation, sampleFormat);
        writer.write(sourceImage, writerParam);
        writer.dispose();

        reader.setInput(fileTest);
        final RenderedImage tested = reader.read(0);
        reader.dispose();

        checkImage(message, sourceImage, tested);
    }
}
