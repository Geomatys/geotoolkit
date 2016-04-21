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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageTypeSpecifier;
import org.geotoolkit.image.internal.PhotometricInterpretation;
import org.geotoolkit.image.internal.SampleType;

import org.geotoolkit.image.io.plugin.TiffImageWriter;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
//import org.junit.Ignore;
import org.junit.Test;

/**
 * {@link TestTiffImageWriter} implementation which write image without any compression.
 *
 * @author Remi Marechal (Geomatys).
 */
public strictfp class UncompressedTiffWriterTest extends TestTiffImageWriter {

    public UncompressedTiffWriterTest() throws IOException {
        super(null);
    }

    /**
     * Verify writing conformity when writing only one strip which is equals to image width.<br>
     * Concretely check writing conformity when strip offset and strip byte count values are contained into tag.
     *
     * @throws java.io.IOException
     * @see TiffImageWriter#writeImageByStrips(java.awt.image.RenderedImage, javax.imageio.ImageWriteParam)
     * @see TiffImageWriter#writeByteCountAndOffsets(long, short, java.lang.Object, long, short, java.lang.Object)
     */
    @Test
    public void singleStripTest() throws IOException {
        //-- 1 band byte --//
        testSingleStrip("TestWriteEmpty : 1 band Byte", SampleType.BYTE, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band short --//
        testSingleStrip("TestWriteEmpty : 1 band Short", SampleType.USHORT, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band int --//
        testSingleStrip("TestWriteEmpty : 1 band Integer", SampleType.INTEGER, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band Float --//
        testSingleStrip("TestWriteEmpty : 1 band Float", SampleType.FLOAT, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band double --//
        testSingleStrip("TestWriteEmpty : 1 Double Byte", SampleType.DOUBLE, 1, PhotometricInterpretation.GRAYSCALE);


        //-- 3 bands RGB --//
        testSingleStrip("TestWriteEmpty : 3 bands Byte", SampleType.BYTE, 3, PhotometricInterpretation.RGB);
        //-- 4 band RGB --//
        testSingleStrip("TestWriteEmpty : 4 bands Byte", SampleType.BYTE, 4, PhotometricInterpretation.RGB);

        //--Color Map --//
        //-- 1 band byte --//
        testSingleStrip("TestWriteEmpty : 1 bands Byte Color Map", SampleType.BYTE, 1, PhotometricInterpretation.PALETTE);
        //-- uncomment this code when a solution for multi band with color palette will be approuved.
//        //-- 1 band byte --//
//        testSingleStrip("TestWriteEmpty : 4 bands Byte Color Map", Byte.SIZE, 4, PHOTOMETRIC_PALETTE, SAMPLEFORMAT_UINT);
    }

    /**
     * Write and read an image without any subsampling or other, and with only one strip.
     *
     * @param message
     * @param sampleBitsSize
     * @param numBand
     * @param photometricInterpretation
     * @param sampleFormat
     * @throws IOException
     */
    private void testSingleStrip(final String message, final SampleType sampleType,
                                 final int numBand, final PhotometricInterpretation photometricInterpretation)
            throws IOException {
        final File fileTest = File.createTempFile(message, "tiff", tempDir);
        writer.setOutput(fileTest); //-- to initialize writer

        //-- only one strip
        final int width  = random.nextInt(256) + 16;
        final int height = 1;

        /*
         * Image volontary created with planarconfiguration equal 1 to don't build banded sampleModel
         * because with banded sample model one strip offset for each band and the aim of this test is
         * to have only a single strip offset tiff tag.
         * N = StripsPerImage for PlanarConfiguration equal to 1; N = SamplesPerPixel * StripsPerImage for PlanarConfiguration equal to 2
         */
        final ImageTypeSpecifier typeSpecifier = buildImageTypeSpecifier(sampleType, numBand, photometricInterpretation);

        final WritableRenderedImage sourceImage = typeSpecifier.createBufferedImage(width, height);//createImageTest(width, height, sampleBitsSize, numBand, photometricInterpretation, sampleFormat);
        writer.write(sourceImage);
        writer.dispose();

        reader.setInput(fileTest);
        final RenderedImage tested = reader.read(0);
        reader.dispose();

        checkImage(message, sourceImage, tested);
    }

    /**
     * Test method.
     *
     * @throws IOException
     */
    @Test
    public void writeEmptyTest() throws IOException {

        //-- 1 band byte --//
        TestWriteEmpty("TestWriteEmpty : 1 band Byte", SampleType.BYTE, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band short --//
        TestWriteEmpty("TestWriteEmpty : 1 band Short", SampleType.USHORT, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band int --//
        TestWriteEmpty("TestWriteEmpty : 1 band Integer", SampleType.INTEGER, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band Float --//
        TestWriteEmpty("TestWriteEmpty : 1 band Float", SampleType.FLOAT, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band double --//
        TestWriteEmpty("TestWriteEmpty : 1 Double Byte", SampleType.DOUBLE, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 3 bands RGB --//
        TestWriteEmpty("TestWriteEmpty : 3 bands Byte", SampleType.BYTE, 3, PhotometricInterpretation.RGB);

        //-- 4 band RGB --//
        TestWriteEmpty("TestWriteEmpty : 4 bands Byte", SampleType.BYTE, 4, PhotometricInterpretation.RGB);

        //--Color Map --//
        //-- 1 band byte --//
        TestWriteEmpty("TestWriteEmpty : 1 bands Byte Color Map", SampleType.BYTE, 1, PhotometricInterpretation.PALETTE);
    }

    /**
     * Effectuate test write empty in function of given parameter.
     *
     * @param message error message in case of assertion fail.
     * @param sampleBitsSize
     * @param numBand
     * @param photometricInterpretation
     * @param sampleFormat
     * @throws IOException if problem during I/O action.
     */
    private void TestWriteEmpty(final String message, final SampleType sampleType,
                                final int numBand, final PhotometricInterpretation photometricInterpretation)
            throws IOException {

        writer.reset();
        final File fileTest = File.createTempFile(message, "tiff", tempDir);
        writer.setOutput(fileTest); //-- to initialize writer

        final ImageTypeSpecifier sourceImgSpec = buildImageTypeSpecifier(sampleType, numBand, photometricInterpretation);

        final int width  = random.nextInt(256) + 16;
        final int height = random.nextInt(256) + 16;

        writer.prepareWriteEmpty(null, sourceImgSpec, width, height, null, null, writerParam);

        //-- create an empty source image to simulate write empty --//
        final WritableRenderedImage sourceImage = sourceImgSpec.createBufferedImage(width, height);

        replacePixels(sourceImage, sampleType, numBand, photometricInterpretation);

        reader.setInput(fileTest);
        final RenderedImage tested = reader.read(0);
        reader.dispose();

        checkImage(message, sourceImage, tested);
    }

    /**
     * Improve method {@link TIFFImageWriter#replacePixels(java.awt.image.RenderedImage, javax.imageio.ImageWriteParam) }.
     *
     * @throws IOException if problem during I/O action.
     */
    @Test
//    @Ignore
    public void replacePixelTest() throws IOException {
        //-- 1 band byte --//
        TestReplacePixel("replacePixel : 1 band Byte", SampleType.BYTE, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band short --//
        TestReplacePixel("replacePixel : 1 band Short", SampleType.USHORT, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band int --//
        TestReplacePixel("replacePixel : 1 band Integer", SampleType.INTEGER, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band Float --//
        TestReplacePixel("replacePixel : 1 band Float", SampleType.FLOAT, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 1 band double --//
        TestReplacePixel("replacePixel : 1 Double Byte", SampleType.DOUBLE, 1, PhotometricInterpretation.GRAYSCALE);

        //-- 3 bands RGB --//
        TestReplacePixel("replacePixel : 3 bands Byte", SampleType.BYTE, 3, PhotometricInterpretation.RGB);
        //-- 4 band RGB --//
        TestReplacePixel("replacePixel : 4 bands Byte", SampleType.BYTE, 4, PhotometricInterpretation.RGB);

        //--Color Map --//
        //-- 1 band byte --//
        TestReplacePixel("replacePixel : 1 bands Byte Color Map", SampleType.BYTE, 1, PhotometricInterpretation.PALETTE);
        //-- uncomment this code when a solution for multi band with color palette will be approuved.
//        //-- 1 band byte --//
//        TestReplacePixel("replacePixel : 4 bands Byte Color Map", Byte.SIZE, 4, PHOTOMETRIC_PALETTE, SAMPLEFORMAT_UINT);
    }

    /**
     * Replace pixel into source image.<br/>
     * Moreover 5 smaller images than source image are generate to replace source
     * image pixel at all corner and at center of the source image.
     *
     * @param sourceImage
     * @param sampleBitsSize
     * @param numBand
     * @param photometricInterpretation
     * @param sampleFormat
     * @throws IOException if problem during I/O action.
     */
    private void replacePixels(final WritableRenderedImage sourceImage, final SampleType sampleType,
                               final int numBand, final PhotometricInterpretation photometricInterpretation)
            throws IOException {

        final int width  = sourceImage.getWidth();
        final int height = sourceImage.getHeight();

        final int w_2 = width  >>> 1;
        final int h_2 = height >>> 1;
        final int w_4 = width  >>> 2;
        final int h_4 = height >>> 2;
        final int w_8 = width  >>> 3;
        final int h_8 = height >>> 3;

        //-- prepare replace pixel
        final int regionMinX = random.nextInt(w_2);
        final int regionMinY = random.nextInt(h_2);
        final Rectangle repRegion = new Rectangle(regionMinX, regionMinY, w_2 , h_2);

        writer.prepareReplacePixels(0, repRegion);

        //-- replace region lower left corner --//
        WritableRenderedImage imgLLC = createImageTest(w_4, h_4, sampleType, numBand, photometricInterpretation);

        int dstOffX = regionMinX - w_8 + random.nextInt(w_8);
        int dstOffY = regionMinY - h_8 + random.nextInt(h_8);

        Point destOffset = new Point(dstOffX, dstOffY);
        replacePixelsInResultImage(sourceImage, repRegion, imgLLC, destOffset);
        writerParam.setDestinationOffset(destOffset);
        writer.replacePixels(imgLLC, writerParam);

        //-- replace region lower right corner --//
        imgLLC = createImageTest(w_4, h_4, sampleType, numBand, photometricInterpretation);
        dstOffX = regionMinX + w_4  + random.nextInt(w_8);
        dstOffY = regionMinY - h_8 + random.nextInt(h_8);
        destOffset = new Point(dstOffX, dstOffY);
        replacePixelsInResultImage(sourceImage, repRegion, imgLLC, destOffset);
        writerParam.setDestinationOffset(destOffset);
        writer.replacePixels(imgLLC, writerParam);

        //-- replace region upper left corner --//
        imgLLC = createImageTest(w_4, h_4, sampleType, numBand, photometricInterpretation);
        dstOffX = regionMinX - w_8  + random.nextInt(w_8);
        dstOffY = regionMinY + h_4 + random.nextInt(h_8);
        destOffset = new Point(dstOffX, dstOffY);
        replacePixelsInResultImage(sourceImage, repRegion, imgLLC, destOffset);
        writerParam.setDestinationOffset(destOffset);
        writer.replacePixels(imgLLC, writerParam);

        //-- replace region upper right corner --//
        imgLLC = createImageTest(w_4, h_4, sampleType, numBand, photometricInterpretation);
        dstOffX = regionMinX + w_4  + random.nextInt(w_8);
        dstOffY = regionMinY + h_4 + random.nextInt(h_8);
        destOffset = new Point(dstOffX, dstOffY);
        replacePixelsInResultImage(sourceImage, repRegion, imgLLC, destOffset);
        writerParam.setDestinationOffset(destOffset);
        writer.replacePixels(imgLLC, writerParam);

        //-- replace region center --//
        imgLLC = createImageTest(w_4, h_4, sampleType, numBand, photometricInterpretation);
        dstOffX = regionMinX + w_8  + random.nextInt(w_8);
        dstOffY = regionMinY + h_8 + random.nextInt(h_8);
        destOffset = new Point(dstOffX, dstOffY);
        replacePixelsInResultImage(sourceImage, repRegion, imgLLC, destOffset);
        writerParam.setDestinationOffset(destOffset);
        writer.replacePixels(imgLLC, writerParam);
        writer.dispose();
    }

    /**
     * Test method {@link TiffImageWriter#replacePixels(java.awt.image.RenderedImage, javax.imageio.ImageWriteParam) }
     * in function of followed criterions.
     *
     * @param message message in case of error.
     * @param sampleType
     * @param numBand band number
     * @param photometricInterpretation
     * @throws IOException if problem during I/O action.
     */
    protected void TestReplacePixel (final String message, final SampleType sampleType,
                                     final int numBand, final PhotometricInterpretation photometricInterpretation)
            throws IOException {

        final File fileTest = File.createTempFile(message, "tiff", tempDir);

        final int width  = random.nextInt(256) + 16;
        final int height = random.nextInt(256) + 16;
        final WritableRenderedImage sourceImage = createImageTest(width, height, sampleType, numBand, photometricInterpretation);

        writer.setOutput(fileTest); //-- to initialize writer
        writerParam.setDestinationOffset(new Point());
        writer.write(sourceImage, writerParam);

        replacePixels(sourceImage, sampleType, numBand, photometricInterpretation);

        reader.setInput(fileTest);
        final RenderedImage tested = reader.read(0);
        reader.dispose();

        checkImage(message, sourceImage, tested);
    }

    /**
     * Fill source image with pixels values from replaceImage at sourceImage and replaceImage intersection.
     *
     * @param sourceImage source image which will be filled by piece of replaceImage.
     * @param sourceRegion region of source image which may be modified.
     * @param replaceImage image which will be copied (entirely or not) into sourceImage.
     * @param destOffset offset in X and Y direction into sourceImage where to copy replacePixel image.
     * @throws IOException if problem during I/O action.
     */
    private void replacePixelsInResultImage(final WritableRenderedImage sourceImage, final Rectangle sourceRegion,
            final RenderedImage replaceImage, final Point destOffset) throws IOException {
        final Rectangle rectImage = new Rectangle(sourceImage.getMinX(), sourceImage.getMinY(), sourceImage.getWidth(), sourceImage.getHeight());
        assert rectImage.intersects(sourceRegion) : "sourceRegion and source image should intersect.";
        Rectangle sourceInter = rectImage.intersection(sourceRegion);

        //-- interested sourceImage region which will be replaced
        final Rectangle rectRIP = new Rectangle(replaceImage.getWidth(), replaceImage.getHeight());
        rectRIP.translate(destOffset.x, destOffset.y);
        sourceInter = sourceInter.intersection(rectRIP);

        //-- interested replaceImage Region
        final Rectangle repRect = new Rectangle(sourceInter);
        repRect.translate(-destOffset.x, -destOffset.y);
        assert repRect.x >= 0;
        assert repRect.y >= 0;

        final PixelIterator sourcePix  = PixelIteratorFactory.createRowMajorWriteableIterator(sourceImage, sourceImage, sourceInter);
        final PixelIterator replacePix = PixelIteratorFactory.createRowMajorIterator(replaceImage, repRect);

        while (sourcePix.next()) {
            replacePix.next();
            sourcePix.setSampleDouble(replacePix.getSampleDouble());
        }
    }
}
