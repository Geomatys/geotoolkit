/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

/**
 *
 * @author rmarechal
 */
public strictfp abstract class TiffImageReaderWriterTest {
    
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
     * compression use during current tests suite.
     */
    private final String compression;
    
    /**
     * Random number generator used for tests.
     */
    protected final Random random;
    
    
    public TiffImageReaderWriterTest(final String compression) {
        this.compression = compression;
        
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
    
    
    
    @Test
    public void default1BandTest() throws IOException {
        File fileTest = File.createTempFile("default1BandTest", "tiff");
        
        //-- test : 1 band type : byte grayscale --//
        int width  = random.nextInt(256) + 16;
        int height = random.nextInt(256) + 16;
        defaultTest("default1BandTest : 1 band type : Byte grayscale : ", fileTest, width, height, 
                Byte.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);
        
        //-- test : 1 band type : short grayscale --//
        width  = random.nextInt(256) + 16;
        height = random.nextInt(256) + 16;
        defaultTest("default1BandTest : 1 band type : Short grayscale : ", fileTest, width, height, 
                Short.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);
        
        //-- test : 1 band type : int grayscale --//
        width  = random.nextInt(256) + 16;
        height = random.nextInt(256) + 16;
        defaultTest("default1BandTest : 1 band type : Integer grayscale : ", fileTest, width, height, 
                Integer.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);
        
        //-- test : 1 band type : float grayscale --//
        width  = random.nextInt(256) + 16;
        height = random.nextInt(256) + 16;
        defaultTest("default1BandTest : 1 band type : Float grayscale : ", fileTest, width, height, 
                Float.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP);
        
        //-- test : 1 band type : Double grayscale --//
        width  = random.nextInt(256) + 16;
        height = random.nextInt(256) + 16;
        defaultTest("default1BandTest : 1 band type : Double grayscale : ", fileTest, width, height, 
                Double.SIZE, 1, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP);
    }
    
    /**
     * Test volontary ignored because scaled color space is not supported yet.
     * 
     * @throws IOException 
     */
    @Test
    @Ignore
    public void default4BandTest() throws IOException {
        File fileTest = File.createTempFile("default1BandTest", "tiff");
        
        //-- test : 1 band type : byte grayscale --//
        int width  = random.nextInt(256) + 16;
        int height = random.nextInt(256) + 16;
        defaultTest("default1BandTest : 1 band type : Byte grayscale : ", fileTest, width, height, 
                Byte.SIZE, 4, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);
        
        //-- test : 1 band type : short grayscale --//
        width  = random.nextInt(256) + 16;
        height = random.nextInt(256) + 16;
        defaultTest("default1BandTest : 1 band type : Short grayscale : ", fileTest, width, height, 
                Short.SIZE, 4, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);
        
        //-- test : 1 band type : int grayscale --//
        width  = random.nextInt(256) + 16;
        height = random.nextInt(256) + 16;
        defaultTest("default1BandTest : 1 band type : Integer grayscale : ", fileTest, width, height, 
                Integer.SIZE, 4, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_UINT);
        
        //-- test : 1 band type : float grayscale --//
        width  = random.nextInt(256) + 16;
        height = random.nextInt(256) + 16;
        defaultTest("default1BandTest : 1 band type : Float grayscale : ", fileTest, width, height, 
                Float.SIZE, 4, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP);
        
        //-- test : 1 band type : Double grayscale --//
        width  = random.nextInt(256) + 16;
        height = random.nextInt(256) + 16;
        defaultTest("default1BandTest : 1 band type : Double grayscale : ", fileTest, width, height, 
                Double.SIZE, 10, PHOTOMETRIC_MINISBLACK, SAMPLEFORMAT_IEEEFP);
    }
    
    @Test
    public void defaultRGBTest() throws IOException {
        File fileTest = File.createTempFile("defaultRGBTest", "tiff");
        
        //-- test : 3 bands type : byte RGB --//
        int width  = random.nextInt(256) + 16;
        int height = random.nextInt(256) + 16;
        defaultTest("defaultRGBTest : 3 bands type : Byte RGB: ", fileTest, width, height, 
                Byte.SIZE, 3, PHOTOMETRIC_RGB, SAMPLEFORMAT_UINT);
        
        //-- test : 4 bands type : byte RGB --//
        width  = random.nextInt(256) + 16;
        height = random.nextInt(256) + 16;
        defaultTest("defaultRGBTest : 4 bands type : Byte RGB: ", fileTest, width, height, 
                Byte.SIZE, 4, PHOTOMETRIC_RGB, SAMPLEFORMAT_UINT);
    }
    
    private void defaultTest(String message, final File fileTest, final int width, final int height, final int sampleBitsSize, 
            final int numBand, final short photometricInterpretation, final short sampleFormat) throws IOException {
        
        final RenderedImage expected = createImageTest(width, height, sampleBitsSize, numBand, photometricInterpretation, sampleFormat);
        
        writer.setOutput(fileTest); //-- to initialize writer
        writer.write(expected);
        writer.dispose();
        
        reader.setInput(fileTest); //-- to initialize reader
        final RenderedImage tested = reader.read(0);
        reader.close();
        
        compareImage(message, expected, tested);
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
    protected final RenderedImage createImageTest(final int width, final int height, final int sampleBitsSize, 
            final int numBand, final short photometricInterpretation, final short sampleFormat) throws UnsupportedImageFormatException {
        
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
                return new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1)).createBufferedImage(width, height);
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
        final BufferedImage buffImg = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1)).createBufferedImage(width, height);
        fillImage(buffImg, dataBufferType);
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
    
    /**
     * Compare two {@link RenderedImage} and throw an assertion exception if comparison criterion are not respected.
     * 
     * @param expected expected image
     * @param tested image which will be compare than expected.
     */
    private void compareImage(final String message, final RenderedImage expected, final RenderedImage tested) {
        assertEquals(message+"image width ", expected.getWidth(), tested.getWidth(), DEFAULT_TOLERANCE);
        assertEquals(message+"image height ", expected.getHeight(), tested.getHeight(), DEFAULT_TOLERANCE);
        final SampleModel expectedSm = expected.getSampleModel();
        final SampleModel testedSm   = tested.getSampleModel();
        
        assertEquals(message+"numband : ", expectedSm.getNumBands(), testedSm.getNumBands());
        assertEquals(message+"numDataElement : ", expectedSm.getNumDataElements(), testedSm.getNumDataElements());
        assertEquals(message+"datatype : ", expectedSm.getDataType(), testedSm.getDataType());
        
        final PixelIterator expectedPix = PixelIteratorFactory.createRowMajorIterator(expected);
        final PixelIterator testedPix   = PixelIteratorFactory.createRowMajorIterator(tested);
        
        while (expectedPix.next()) {
            testedPix.next();
            assertEquals(message+"pixel at coordinate : (x, y) : ("+expectedPix.getX()+", "+expectedPix.getY()+") : ", 
                    expectedPix.getSampleDouble(), testedPix.getSampleDouble(), DEFAULT_TOLERANCE);
        }
    }
}
