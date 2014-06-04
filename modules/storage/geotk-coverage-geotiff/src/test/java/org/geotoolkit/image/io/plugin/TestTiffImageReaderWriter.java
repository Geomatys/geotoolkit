/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2014, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2014, Geomatys
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
package org.geotoolkit.image.io.plugin;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.IIOParam;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import org.apache.sis.test.TestUtilities;
import org.geotoolkit.image.io.UnsupportedImageFormatException;
import org.geotoolkit.image.iterator.*;
import org.geotoolkit.internal.image.ScaledColorSpace;
import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Ignore;
import static org.geotoolkit.image.io.plugin.ImageOrientation.*;

/**
 * Primary test class to test {@link TiffImageWriter} and {@link TiffImageReader}. <br/><br/>
 * 
 * All tests use random image boundary and random internales datas.<br/><br/>
 * 
 * Proposed tests : <br/>
 * 
 * - Simply Reading / writing without any sub sampling or other, with 1 band, in all sample format (Byte, Short, Integer, Float, Double).<br/>
 * - Simply Reading / writing without any sub sampling or other, with 3, 4 bands RGB.<br/>
 * - Simply Reading / writing without any sub sampling or other, with 3, 4 bands Color Map.<br/><br/>
 * 
 * All following tests, test internaly all precedently tests with random sub sampling, destination offset, sub sample offsets.<br/>
 * - Reading / Writing with a random source region situated on image lower left corner.<br/>
 * - Reading / Writing with a random source region situated on image lower right corner.<br/>
 * - Reading / Writing with a random source region situated on image upper left corner.<br/>
 * - Reading / Writing with a random source region situated on image upper right corner.<br/>
 * - Reading / Writing with a random source region situated on image center.<br/>
 *
 * @author Remi Marechal (Geomatys).
 */
public strictfp abstract class TestTiffImageReaderWriter {
    
    protected final double DEFAULT_TOLERANCE = 1E-15;

    //---------------- sammple format -------------------//
    /**
     * Define buffer type as IEEE floating point data.
     */
    protected final static short SAMPLEFORMAT_IEEEFP = 3;
    
    /**
     * Define buffer type as integer point data.
     */
    protected final static short SAMPLEFORMAT_UINT    = 1;
    
    //------------ Photometric interpretation -----------//
    /**
     * Define image properties for bilevel and grayscale images: 0 is imaged as black.
     */
    protected final static short PHOTOMETRIC_MINISBLACK = 1;
    
    /**
     * Define image properties for RGB image.
     * RGB value of (0,0,0) represents black, and (255,255,255) represents white, assuming 8-bit components. 
     * The components are stored in the indicated order: first Red, then Green, then Blue.
     */
    protected final static short PHOTOMETRIC_RGB         = 2;
    
    /**
     * Define image with Palette color. 
     * In this model, a color is described with a single component. 
     * The value of the component is used as an index into the red, 
     * green and blue curves in the ColorMap field to retrieve an RGB triplet that defines the color. 
     * When PhotometricInterpretation = 3 is used, ColorMap must be present and SamplesPerPixel must be 1.
     */
    protected final static short PHOTOMETRIC_PALETTE     = 3;
    
    /**
     * Tested {@link ImageReader} implementation.
     */
    protected TiffImageReader reader;
    
    /**
     * Adapted {@link IIOParam} for reading operations.
     */
    protected ImageReadParam readerParam;
    
    /**
     * Tested {@link ImageWriter} implementation.
     */
    protected TiffImageWriter writer;
    
    /**
     * Adapted {@link IIOParam} for writing operations.
     */
    protected ImageWriteParam writerParam;
    
    /**
     * Random number generator used for tests.
     */
    protected final Random random;
    
    /**
     * @param compression choosen compression to write image.
     */
    public TestTiffImageReaderWriter(final String compression) {
        
        this.reader      = new TiffImageReader(null);
        this.readerParam = reader.getDefaultReadParam();
        
        this.writer      = new TiffImageWriter(null);
        this.writerParam = writer.getDefaultWriteParam();
        if (compression != null) {
            writerParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writerParam.setCompressionType(compression);
        }
        random = TestUtilities.createRandomNumberGenerator();
    }
    
    /**
     * Effectuate distinct test in function of Reader or Writer implementation 
     * and return result image from Reading writing action.
     * 
     * @param fileTest the place to be
     * @param sourceImage image which will be written
     * @param sourceRegion Reading or writing region.
     * @param sourceXSubsampling Reading or writing subsample in X direction.
     * @param sourceYsubsampling Reading or writing subsample in Y direction.
     * @param sourceXOffset Reading or writing offset in X direction.
     * @param sourceYOffset Reading or writing offset in Y direction.
     * @return Result image from reading writing action.
     * @throws IOException if problem during reading / writing action.
     */
    protected abstract RenderedImage effectuateTest(final File fileTest, final RenderedImage sourceImage, final Rectangle sourceRegion, 
            final int sourceXSubsampling, final int sourceYsubsampling, final int sourceXOffset, final int sourceYOffset, final Point destOffset) throws IOException;
    
    /**
     * Test which write and read after an image with only one band and test all sample type.
     * 
     * @throws IOException if problem during reading/writing action.
     */
    @Test
//    @Ignore
    public void default1BandTest() throws IOException {
        File fileTest = File.createTempFile("default1BandTest", "tiff");
        
        //-- test : 1 band type : byte grayscale --//
        defaultTest("default1BandTest : 1 band type : Byte grayscale : ", fileTest,
                Byte.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);
        
        //-- test : 1 band type : short grayscale --//
        defaultTest("default1BandTest : 1 band type : Short grayscale : ", fileTest, 
                Short.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);
        
        //-- test : 1 band type : int grayscale --//
        defaultTest("default1BandTest : 1 band type : Integer grayscale : ", fileTest, 
                Integer.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);
        
        //-- test : 1 band type : float grayscale --//
        defaultTest("default1BandTest : 1 band type : Float grayscale : ", fileTest, 
                Float.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP);
        
        //-- test : 1 band type : Double grayscale --//
        defaultTest("default1BandTest : 1 band type : Double grayscale : ", fileTest, 
                Double.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP);
    }
    
    /**
     * Test volontary ignored because scaled color space is not supported yet.
     * 
     * @throws IOException if problem during reading/writing action.
     */
    @Test
    @Ignore
    public void default4BandTest() throws IOException {
        File fileTest = File.createTempFile("default1BandTest", "tiff");
        
        //-- test : 1 band type : byte grayscale --//
        defaultTest("default1BandTest : 1 band type : Byte grayscale : ", fileTest, 
                Byte.SIZE, 4, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);
        
        //-- test : 1 band type : short grayscale --//
        defaultTest("default1BandTest : 1 band type : Short grayscale : ", fileTest, 
                Short.SIZE, 4, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);
        
        //-- test : 1 band type : int grayscale --//
        defaultTest("default1BandTest : 1 band type : Integer grayscale : ", fileTest, 
                Integer.SIZE, 4, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);
        
        //-- test : 1 band type : float grayscale --//
        defaultTest("default1BandTest : 1 band type : Float grayscale : ", fileTest, 
                Float.SIZE, 4, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP);
        
        //-- test : 1 band type : Double grayscale --//
        defaultTest("default1BandTest : 1 band type : Double grayscale : ", fileTest, 
                Double.SIZE, 10, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP);
    }
    
    /**
     * Test writing/ reading action with an RGB image.
     * 
     * @throws IOException if problem during reading/writing action.
     */
    @Test
//    @Ignore
    public void defaultRGBTest() throws IOException {
        File fileTest = File.createTempFile("defaultRGBTest", "tiff");
        
        //-- test : 3 bands type : byte RGB --//
        defaultTest("defaultRGBTest : 3 bands type : Byte RGB: ", fileTest, 
                Byte.SIZE, 3, PHOTOMETRIC_RGB, SAMPLEFORMAT_UINT);
        
        //-- test : 4 bands type : byte RGB --//
        defaultTest("defaultRGBTest : 4 bands type : Byte RGB: ", fileTest,
                Byte.SIZE, 4, PHOTOMETRIC_RGB, SAMPLEFORMAT_UINT);
    }
    
    /**
     * Test writing/ reading action with an color map image.
     * 
     * @throws IOException if problem during reading/writing action.
     */
    @Test
//    @Ignore
    public void defaultColorMapTest() throws IOException {
        File fileTest = File.createTempFile("defaultColorMapTest", "tiff");
        
        //-- test : 3 bands type : byte RGB --//
        defaultTest("defaultColorMapTest : 3 bands type : Byte Palette: ", fileTest,  
                Byte.SIZE, 3, PHOTOMETRIC_PALETTE, SAMPLEFORMAT_UINT);
        
        //-- test : 4 bands type : byte RGB --//
        defaultTest("defaultColorMapTest : 4 bands type : Byte RGB: ", fileTest,
                Byte.SIZE, 4, PHOTOMETRIC_PALETTE, SAMPLEFORMAT_UINT);
    }
    
    /**
     * Improve Reader / Writer with random image dimension and subsampling values.
     * Moreover source region is near source image upper left corner.
     * 
     * @see #regionTest(java.lang.String, org.geotoolkit.image.io.plugin.ImageOrientation) 
     */
    @Test
//    @Ignore
    public void upperLeftCornerTest() throws IOException {
        regionTest("UpperLeftCornerTest", IMAGE_UPPER_LEFT_CORNER);
    }
    
    /**
     * Improve Reader / Writer with random image dimension and subsampling values.
     * Moreover source region is near source image upper right corner.
     * 
     * @see #regionTest(java.lang.String, org.geotoolkit.image.io.plugin.ImageOrientation) 
     */
    @Test
//    @Ignore
    public void upperRightCornerTest() throws IOException {
        regionTest("upperRightCornerTest", IMAGE_UPPER_RIGHT_CORNER);
    }
    
    /**
     * Improve Reader / Writer with random image dimension and subsampling values.
     * Moreover source region is near source image lower left corner.
     * 
     * @see #regionTest(java.lang.String, org.geotoolkit.image.io.plugin.ImageOrientation) 
     */
    @Test
//    @Ignore
    public void lowerLeftCornerTest() throws IOException {
        regionTest("lowerLeftCornerTest", IMAGE_LOWER_LEFT_CORNER);
    }
    
    /**
     * Improve Reader / Writer with random image dimension and subsampling values.
     * Moreover source region is near source image lower right corner.
     * 
     * @see #regionTest(java.lang.String, org.geotoolkit.image.io.plugin.ImageOrientation) 
     */
    @Test
//    @Ignore
    public void lowerRightCornerTest() throws IOException {
        regionTest("lowerRightCornerTest", IMAGE_LOWER_RIGHT_CORNER);
    }
    
    /**
     * Improve Reader / Writer with random image dimension and subsampling values.
     * Moreover source region is near source image center.
     * 
     * @see #regionTest(java.lang.String, org.geotoolkit.image.io.plugin.ImageOrientation) 
     */
    @Test
//    @Ignore
    public void centerTest() throws IOException {
        regionTest("centerAreaTest", IMAGE_CENTER);
    }
    
    /**
     * Effectuate some tests on the random region near the specified imageOrientation.
     * 
     * @param message in case of error first part of error message.
     * @param imageOrientation an enum to stipulate witch source image region will be written or read.
     * @see #IMAGE_LOWER_LEFT_CORNER
     * @see #IMAGE_LOWER_RIGHT_CORNER
     * @see #IMAGE_UPPER_LEFT_CORNER
     * @see #IMAGE_UPPER_RIGHT_CORNER
     * @see #IMAGE_CENTER
     * @throws IOException if problem during reading/writing action.
     */
    private void regionTest(final String message, final ImageOrientation imageOrientation) throws IOException {
        final File fileTest = File.createTempFile(message, "tiff");
        
        //-------------------- test : 1 band -----------------------------------// 
        //-- type byte
        generalTest(message+" : 1 band, type : byte.", fileTest, Byte.SIZE, 1,
                PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT, imageOrientation);
        //-- type short
        generalTest(message+" : 1 band, type : short.", fileTest, Short.SIZE, 1,
                PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT, imageOrientation);
        //-- type int
        generalTest(message+" : 1 band, type : int.", fileTest, Integer.SIZE, 1,
                PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT, imageOrientation);
        //-- type Float
        generalTest(message+" : 1 band, type : float.", fileTest, Float.SIZE, 1,
                PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP, imageOrientation);
        //-- type double
        generalTest(message+" : 1 band, type : double.", fileTest, Double.SIZE, 1,
                PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP, imageOrientation);
        
        //-- RGB --//
        //-- type Byte RGB
        generalTest(message+" : 3 bands RGB, type : Byte.", fileTest, Byte.SIZE, 3,
                PHOTOMETRIC_RGB, SAMPLEFORMAT_UINT, imageOrientation);
        generalTest(message+" : 4 bands RGB, type : Byte.", fileTest, Byte.SIZE, 4,
                PHOTOMETRIC_RGB, SAMPLEFORMAT_UINT, imageOrientation);
        
        //-- color Map --//
        //-- type Byte RGB 
        generalTest(message+" : 3 bands ColorMap, type : Byte.", fileTest, Byte.SIZE, 3,
                PHOTOMETRIC_PALETTE, SAMPLEFORMAT_UINT, imageOrientation);
        generalTest(message+" : 4 bands ColorMap, type : Byte.", fileTest, Byte.SIZE, 4,
                PHOTOMETRIC_PALETTE, SAMPLEFORMAT_UINT, imageOrientation);
    }
    
    /**
     * Create an image with expected properties given by followed attributs : <br/>
     * - random width and height<br/>
     * - sampleBitsSize<br/>
     * - numBand<br/>
     * - photometricInterpretation<br/>
     * - sampleFormat<br/>
     * and fill it by appropriate random sample values.<br/>
     * Then image is writen at "filetest" adress and read.<br/>
     * To finish read image is compare to itself before writing.
     * 
     * @param message in case of assertion error.
     * @param fileTest the place to be.
     * @param sampleBitsSize sample bit number.
     * @param numBand band number.
     * @param photometricInterpretation define RGB or 1 band or also color map.
     * @param sampleFormat define sample format integer or floating point.
     * @see #PHOTOMETRIC_MINISBLACK
     * @see #PHOTOMETRIC_PALETTE
     * @see #PHOTOMETRIC_RGB
     * @see #SAMPLEFORMAT_IEEEFP
     * @see #SAMPLEFORMAT_UINT
     * @throws IOException if problem during reading/writing action.
     */
    private void defaultTest(final String message, final File fileTest, final int sampleBitsSize, final int numBand, 
            final short photometricInterpretation, final short sampleFormat) throws IOException {
        
        final int width  = random.nextInt(256) + 16;
        final int height = random.nextInt(256) + 16;
        final RenderedImage expected = createImageTest(width, height, sampleBitsSize, numBand, photometricInterpretation, sampleFormat);
        
        writer.setOutput(fileTest); //-- to initialize writer
        writer.write(expected, writerParam);
        writer.dispose();
        
        reader.setInput(fileTest); //-- to initialize reader
        final RenderedImage tested = reader.read(0);
        reader.close();
        
        checkImage(message, expected, tested);
    }
    
    /**
     * Effectuate a test in function of given parameter.
     * Internaly, a source image is generate with width height region and subsampling random values.
     * 
     * @param message in case of error first part of error message.
     * @param fileTest the place to be.
     * @param sampleBitsSize sample bit number.
     * @param numBand source image numband.
     * @param photometricInterpretation define RGB or 1 band or also color map.
     * @param sampleFormat define sample format integer or floating point.
     * @param imageOrientation an enum to stipulate witch source image region will be written or read.
     * @see #PHOTOMETRIC_MINISBLACK
     * @see #PHOTOMETRIC_PALETTE
     * @see #PHOTOMETRIC_RGB
     * @see #SAMPLEFORMAT_IEEEFP
     * @see #SAMPLEFORMAT_UINT
     * @see #IMAGE_LOWER_LEFT_CORNER
     * @see #IMAGE_LOWER_RIGHT_CORNER
     * @see #IMAGE_UPPER_LEFT_CORNER
     * @see #IMAGE_UPPER_RIGHT_CORNER
     * @see #IMAGE_CENTER
     * @throws IOException if problem during reading/writing action.
     */
    private void generalTest(final String message, final File fileTest, final int sampleBitsSize, final int numBand, 
            final short photometricInterpretation, final short sampleFormat, final ImageOrientation imageOrientation) throws IOException {
        int width  = random.nextInt(256) + 16;
        int height = random.nextInt(256) + 16;
        
        final RenderedImage sourceImage = createImageTest(width, height, sampleBitsSize, numBand, photometricInterpretation, sampleFormat);
        
        final int srcRegionX, srcRegionY;
        switch (imageOrientation) {
            case IMAGE_LOWER_RIGHT_CORNER : {
                srcRegionX = width >> 1;
                srcRegionY = 0;
                break;
            }
            case IMAGE_UPPER_LEFT_CORNER : {
                srcRegionX = 0;
                srcRegionY = height >> 1;
                break;
            }
            case IMAGE_UPPER_RIGHT_CORNER : {
                srcRegionX = width  >> 1;
                srcRegionY = height >> 1;
                break;
            }
            case IMAGE_CENTER : {
                srcRegionX = (width >> 2)  + random.nextInt(width  >> 2);
                srcRegionY = (height >> 2) + random.nextInt(height >> 2);
                break;
            }
            case IMAGE_LOWER_LEFT_CORNER : //-- stipulate only to better view
            default : {
                srcRegionX = srcRegionY = 0;
                break;
            }
        }
        
        final Rectangle sourceRegion = new Rectangle(srcRegionX, srcRegionY, width >> 1, height >> 1);
        
        final int subsampleX       = random.nextInt((width >> 1) - 1) + 1;
        final int subsampleY       = random.nextInt((height >> 1) - 1) + 1;
        
        final int subsampleXOffset = Math.max(0, random.nextInt(subsampleX) - 1);
        final int subsampleYOffset = Math.max(0, random.nextInt(subsampleY) - 1);
        
        final Point destOffset = new Point(random.nextInt(width), random.nextInt(height));
        
         final RenderedImage testedImage = effectuateTest(fileTest, sourceImage, sourceRegion, 
                subsampleX, subsampleY, subsampleXOffset, subsampleYOffset, destOffset);
        
        checkImages(message, sourceImage, sourceRegion, subsampleX, subsampleXOffset, subsampleY, subsampleYOffset, destOffset, testedImage);
    }
    
    /**
     * Compare two {@link RenderedImage} and throw an assertion exception if comparison criterion are not respected.
     * 
     * @param message  in case of error first part of error message.
     * @param sourceImage source image
     * @param tested   image which will be compare than source.
     */
    protected void checkImage(final String message, final RenderedImage sourceImage, final RenderedImage testedImage) {
        checkImages(message, sourceImage, null, 1, 0, 1, 0, null, testedImage);
    }
    
    /**
     * Compare two {@link RenderedImage} in function of given parameter 
     * and throw an assertion exception if comparison criterion are not respected.
     * 
     * @param message in case of error first part of error message.
     * @param sourceImage source image
     * @param sourceRegion area from source image which will be written or read.
     * @param sourceXsubsampling subsampling in X direction.
     * @param sourceXOffset offset in X direction
     * @param sourceYsubsampling subsample in Y direction
     * @param sourceYOffset offset in Y direction
     * @param testedImage I/O result which will be test.
     */
    protected void checkImages(final String message, final RenderedImage sourceImage, Rectangle sourceRegion, 
            final int sourceXsubsampling, final int sourceXOffset, final int sourceYsubsampling, final int sourceYOffset,
            final Point destOffset, final RenderedImage testedImage) {
        
        if (sourceRegion == null) sourceRegion = new Rectangle(0, 0, sourceImage.getWidth(), sourceImage.getHeight());
        int destOffsetX = 0;
        int destOffsetY = 0;
        if (destOffset != null) {
            destOffsetX = destOffset.x;
            destOffsetY = destOffset.y;
        }
        
        sourceRegion.translate(sourceXOffset, sourceYOffset);
        sourceRegion.width  -= sourceXOffset;
        sourceRegion.height -= sourceYOffset;
        
        final int srcMinX = Math.max(sourceRegion.x, sourceImage.getMinX());
        final int srcMinY = Math.max(sourceRegion.y, sourceImage.getMinY());
        final int srcMaxX  = Math.min(sourceRegion.x + sourceRegion.width, sourceImage.getMinX() + sourceImage.getWidth());
        final int srcMaxY = Math.min(sourceRegion.y + sourceRegion.height, sourceImage.getMinY() + sourceImage.getHeight());
        
        final int expectedWidth  = (srcMaxX - srcMinX + sourceXsubsampling - 1) / sourceXsubsampling;
        final int expectedHeight = (srcMaxY - srcMinY + sourceYsubsampling - 1) / sourceYsubsampling;
        
        assertEquals(message+"image width ", destOffsetX + expectedWidth, testedImage.getWidth(), DEFAULT_TOLERANCE);
        assertEquals(message+"image height ", destOffsetY + expectedHeight, testedImage.getHeight(), DEFAULT_TOLERANCE);
        
        final SampleModel expectedSm = sourceImage.getSampleModel();
        final int expectedNumband    = expectedSm.getNumBands();
        
        final SampleModel testedSm   = testedImage .getSampleModel();
        
        assertEquals(message+"numband : ", expectedNumband, testedSm.getNumBands());
        assertEquals(message+"numDataElement : ", expectedSm.getNumDataElements(), testedSm.getNumDataElements());
        assertEquals(message+"datatype : ", expectedSm.getDataType(), testedSm.getDataType());
                
        final PixelIterator sourcePix = PixelIteratorFactory.createRowMajorIterator(sourceImage, sourceRegion);
        
        final Rectangle testedRegion = new Rectangle(destOffsetX, destOffsetY, expectedWidth, expectedHeight);
        final PixelIterator testedPix = PixelIteratorFactory.createRowMajorIterator(testedImage, testedRegion);
        
        for (int y = sourceRegion.y; y < sourceRegion.y + sourceRegion.height; y += sourceYsubsampling) {
            for (int x = sourceRegion.x; x < sourceRegion.x + sourceRegion.width; x += sourceXsubsampling) {
                sourcePix.moveTo(x, y, 0);
                int b = 0;
                while (b++ < expectedNumband) {
                    testedPix.next();
                    assertEquals(message+"pixel at coordinate : (x, y, b) : ("+sourcePix.getX()+", "+sourcePix.getY()+", "+b+") : ",  
                    sourcePix.getSampleDouble(), testedPix.getSampleDouble(), DEFAULT_TOLERANCE);
                    sourcePix.next();
                }
            }
        }
    }
    
    /**
     * Build an appropriate {@link ImageTypeSpecifier} in function of given parameter.
     * 
     * @param sampleBitsSize bit size for each sample.
     * @param numBand expected band number
     * @param photometricInterpretation 
     * @param sampleFormat
     * @return {@link ImageTypeSpecifier}.
     * @throws UnsupportedImageFormatException if photometricInterpretation or sampleFormat are not in accordance with other parameters.
     */
    protected ImageTypeSpecifier buildImageTypeSpecifier(final int sampleBitsSize, final int numBand, 
            final short photometricInterpretation, final short sampleFormat) throws UnsupportedImageFormatException {
        
        final int dataBufferType;
                
        if (sampleFormat == 3) {
            /*
             * Case to defferency 32 bits Float to 32 bits Integer. 
             */
            switch (sampleBitsSize) {
                case Float.SIZE  : dataBufferType = DataBuffer.TYPE_FLOAT; break;
                case Double.SIZE : dataBufferType = DataBuffer.TYPE_DOUBLE; break;
                default : {
                    throw new UnsupportedImageFormatException( "unsupported bitsPerSample size : "+sampleBitsSize);
                }
            }
        } else {

           /*
            * We require exact value, because the reading process read all sample values
            * in one contiguous read operation.
            */
           switch (sampleBitsSize) {
               case Byte   .SIZE : dataBufferType = DataBuffer.TYPE_BYTE;   break;
               case Short  .SIZE : dataBufferType = DataBuffer.TYPE_USHORT; break;
               case Integer.SIZE : dataBufferType = DataBuffer.TYPE_INT;    break;
               case Double.SIZE  : dataBufferType = DataBuffer.TYPE_DOUBLE; break;
               default : {
                    throw new UnsupportedImageFormatException( "unsupported bitsPerSample size : "+sampleBitsSize);
               }
           }
        }

        final ColorSpace cs;
        switch (photometricInterpretation) {
            case 0 :   //--minIsWhite
            case 1 : { //-- minIsBlack
                if (numBand > 1) {
                    cs = new ScaledColorSpace(numBand, 0, Double.MIN_VALUE, Double.MAX_VALUE);
                } else {
                    cs = ColorSpace.getInstance(ColorSpace.CS_GRAY); 
                }
                break;
            }
            case 2 : { //-- RGB
                cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                break;
            }
            case 3 : {//-- palette
                final int[] indexes = buildColorMapArray(dataBufferType);
                final ColorModel cm = new IndexColorModel(sampleBitsSize, indexes.length, indexes, 0, true, -1, dataBufferType);
                /*
                 * Create a SampleModel with size of 1x1 volontary just to know image properties.
                 * Image with correctively size will be create later with getDestination() in #read(int index, param) method.
                 */
                return new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
            }
            default : {
                throw new UnsupportedImageFormatException( "photometricInterpretation : "+photometricInterpretation);
            }
        }
        final boolean hasAlpha = numBand > cs.getNumComponents();
        final int[] bits = new int[numBand];
        Arrays.fill(bits, sampleBitsSize);
        final ColorModel cm = new ComponentColorModel(cs, bits, hasAlpha, false,
                hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE, dataBufferType);
        /*
         * Create a SampleModel with size of 1x1 volontary just to know image properties.
         * Image with correctively size will be create later with getDestination() in #read(int index, param) method.
         */  
        return new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
    }
    
    /**
     * Create an appropriate {@link RenderedImage} adapted for test which respond from all properties parameters.
     * 
     * @param width width of created image.
     * @param height height of created image
     * @param sampleBitsSize size in bit of a sample.
     * @param numBand samples number for each pixel.
     * @param photometricInterpretation 
     * @param sampleFormat
     * @return created {@link RenderedImage}.
     * @throws UnsupportedImageFormatException if sampleBitsSize has a wrong value of photometricInterpretation is not supported.
     */
    protected final WritableRenderedImage createImageTest(final int width, final int height, final int sampleBitsSize, 
            final int numBand, final short photometricInterpretation, final short sampleFormat) throws UnsupportedImageFormatException {
        
        final ImageTypeSpecifier imgType = buildImageTypeSpecifier(sampleBitsSize, numBand, photometricInterpretation, sampleFormat);
        
        final BufferedImage buffImg = imgType.createBufferedImage(width, height);
        fillImage(buffImg, imgType.getSampleModel().getDataType());
        return buffImg;
    }
    
    /**
     * Create a map array which contain random {@link Integer} values 
     * adapted to create an appropriate {@link IndexColorModel}. 
     * 
     * @param dataBufferType sample type of image which will use this map array.
     * @return map array.
     */
    private int[] buildColorMapArray(final int dataBufferType) {
        final int size = (dataBufferType == DataBuffer.TYPE_BYTE) ? ((1 << Byte.SIZE) - 1) : (1 << Short.SIZE) - 1;
        final int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = random.nextInt();
        }
        return result;
    }
    
    /**
     * Fill all image samples, with random values in fonction of its {@link DataBuffer} type.
     * 
     * @param image image which will be filled.
     * @param databufferType type of internal samples data.
     */
    private void fillImage(final WritableRenderedImage image, final int databufferType) {
        if (databufferType == DataBuffer.TYPE_BYTE) {
            final int numXTile = image.getNumXTiles();
            final int numYTile = image.getNumYTiles();
            final int tXOffset = image.getTileGridXOffset();
            final int tYOffset = image.getTileGridYOffset();

            for (int ty = tYOffset; ty < tYOffset + numYTile; ty++) {
                for (int tx = tXOffset; tx < tXOffset + numXTile; tx++) {
                    final Raster raster = image.getTile(tx, ty);
                    final DataBuffer databuff = raster.getDataBuffer();
                    final int numBanks = databuff.getNumBanks();
                    for (int bank = 0; bank < numBanks; bank ++) {
                        random.nextBytes(((DataBufferByte)databuff).getData(bank));
                    }
                }
            }
            return;
        }
        
        final PixelIterator pix = PixelIteratorFactory.createDefaultWriteableIterator(image, image);
        switch (databufferType) {
            case DataBuffer.TYPE_USHORT : 
            case DataBuffer.TYPE_SHORT  : 
            case DataBuffer.TYPE_INT    : while (pix.next()) pix.setSample(random.nextInt()); break;

            case DataBuffer.TYPE_FLOAT  : while (pix.next()) pix.setSampleFloat(random.nextFloat()); break;

            case DataBuffer.TYPE_DOUBLE : while (pix.next()) pix.setSampleDouble(random.nextDouble()); break;

            default: throw new AssertionError(databufferType);
        }
    }
}
