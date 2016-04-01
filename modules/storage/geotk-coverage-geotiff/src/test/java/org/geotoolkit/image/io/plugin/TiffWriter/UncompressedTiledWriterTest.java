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

import org.geotoolkit.image.internal.PhotometricInterpretation;
import org.geotoolkit.image.internal.SampleType;

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
        testSingleTile("singleTileTest : 1 band Byte", SampleType.BYTE, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band short --//
        testSingleTile("singleTileTest : 1 band Short", SampleType.USHORT, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band int --//
        testSingleTile("singleTileTest : 1 band Integer", SampleType.INTEGER, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band Float --//
        testSingleTile("singleTileTest : 1 band Float", SampleType.FLOAT, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band double --//
        testSingleTile("singleTileTest : 1 Double Byte", SampleType.DOUBLE, 1, PhotometricInterpretation.GRAYSCALE);


        //-- 3 bands RGB --//
        testSingleTile("singleTileTest : 3 bands Byte", SampleType.BYTE, 3, PhotometricInterpretation.RGB);
        //-- 4 band RGB --//
        testSingleTile("singleTileTest : 4 bands Byte", SampleType.BYTE, 4, PhotometricInterpretation.RGB);

        //--Color Map --//
        //-- 1 band byte --//
        testSingleTile("singleTileTest : 1 bands Byte Color Map", SampleType.BYTE, 1, PhotometricInterpretation.PALETTE);
        //-- uncomment this code when a solution for multi band with color palette will be approuved.
//        //-- 1 band byte --//
//        testSingleTile("singleTileTest : 4 bands Byte Color Map", Byte.SIZE, 4, PHOTOMETRIC_PALETTE, SAMPLEFORMAT_UINT);
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
    private void testSingleTile(final String message, final SampleType sampleType,
                                final int numBand, final PhotometricInterpretation photometricInterpretation)
            throws IOException {
        final File fileTest = File.createTempFile(message, "tiff", tempDir);
        writer.setOutput(fileTest); //-- to initialize writer

        //-- tile param mode already in explicit mode define into this class constructor.
        final int width  = writerParam.getTileWidth();
        final int height = writerParam.getTileHeight();

        final WritableRenderedImage sourceImage = createImageTest(width, height, sampleType, numBand, photometricInterpretation);
        writer.write(sourceImage, writerParam);
        writer.dispose();

        reader.setInput(fileTest);
        final RenderedImage tested = reader.read(0);
        reader.dispose();

        checkImage(message, sourceImage, tested);
    }
}
