/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRenderedImage;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;

import javax.imageio.IIOParam;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.sis.test.TestUtilities;

import org.geotoolkit.coverage.io.CoverageIO;
import org.geotoolkit.image.internal.ImageUtils;
import org.geotoolkit.image.internal.PhotometricInterpretation;
import org.geotoolkit.image.internal.PlanarConfiguration;
import org.geotoolkit.image.internal.SampleType;
import org.geotoolkit.image.io.UnsupportedImageFormatException;
import org.geotoolkit.image.iterator.*;
import org.geotoolkit.nio.IOUtilities;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.geotoolkit.image.io.plugin.ImageOrientation.*;
import org.junit.Ignore;

/**
 * Primary test class to test {@link TiffImageWriter} and {@link TiffImageReader}. <br/><br/>
 *
 * All tests use random image boundary and random internales datas.<br/><br/>
 *
 * Proposed tests : <br/>
 *
 * - Simply Reading / writing without any sub sampling or other, with 1 band, in all sample format (Byte, Short, Integer, Float, Double).<br/>
 * - Simply Reading / writing without any sub sampling or other, with 3, 4 bands RGB.<br/>
 * - Simply Reading / writing without any sub sampling or other, with 1 bands Color Map.<br/><br/>
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

    protected final File tempDir;
    /**
     * @param compression choosen compression to write image.
     */
    public TestTiffImageReaderWriter(final String compression) throws IOException {
        tempDir = Files.createTempDirectory("tiffTests").toFile();

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

    @After
    public void deleteTempFiles() throws IOException {
        Files.walkFileTree(tempDir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return super.visitFile(file, attrs);
            }
        });
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
        Path fileTest = Files.createTempFile(tempDir.toPath(),"default1BandTest", "tiff");

        //-- test : 1 band type : byte grayscale --//
        defaultTest("default1BandTest : 1 band type : Byte grayscale : ", fileTest,
                SampleType.BYTE, 1, PhotometricInterpretation.GRAYSCALE);

        //-- test : 1 band type : short grayscale --//
        defaultTest("default1BandTest : 1 band type : Short grayscale : ", fileTest,
                SampleType.USHORT, 1, PhotometricInterpretation.GRAYSCALE);

        //-- test : 1 band type : int grayscale --//
        defaultTest("default1BandTest : 1 band type : Integer grayscale : ", fileTest,
                SampleType.INTEGER, 1, PhotometricInterpretation.GRAYSCALE);

        //-- test : 1 band type : float grayscale --//
        defaultTest("default1BandTest : 1 band type : Float grayscale : ", fileTest,
                SampleType.FLOAT, 1, PhotometricInterpretation.GRAYSCALE);

        //-- test : 1 band type : Double grayscale --//
        defaultTest("default1BandTest : 1 band type : Double grayscale : ", fileTest,
                SampleType.DOUBLE, 1, PhotometricInterpretation.GRAYSCALE);
    }

    /**
     * Test volontary ignored because scaled color space is not supported yet.
     *
     * @throws IOException if problem during reading/writing action.
     */
    @Test
    //@Ignore
    public void default4BandTest() throws IOException {
        Path fileTest = Files.createTempFile(tempDir.toPath(),"default4BandTest", "tiff");

        //-- test : 1 band type : byte grayscale --//
        defaultTest("default1BandTest : 4 band type : Byte grayscale : ", fileTest,
                SampleType.BYTE, 4, PhotometricInterpretation.GRAYSCALE);

        //-- test : 1 band type : short grayscale --//
        defaultTest("default1BandTest : 4 band type : Short grayscale : ", fileTest,
                SampleType.USHORT, 4, PhotometricInterpretation.GRAYSCALE);

        //-- test : 1 band type : int grayscale --//
        defaultTest("default1BandTest : 4 band type : Integer grayscale : ", fileTest,
                SampleType.INTEGER, 4, PhotometricInterpretation.GRAYSCALE);

        //-- test : 1 band type : float grayscale --//
        defaultTest("default1BandTest : 4 band type : Float grayscale : ", fileTest,
                SampleType.FLOAT, 4, PhotometricInterpretation.GRAYSCALE);

        //-- test : 1 band type : Double grayscale --//
        defaultTest("default1BandTest : 10 band type : Double grayscale : ", fileTest,
                SampleType.DOUBLE, 10, PhotometricInterpretation.GRAYSCALE);
    }

    /**
     * Test writing/ reading action with an RGB image.
     *
     * @throws IOException if problem during reading/writing action.
     */
    @Test
//    @Ignore
    public void defaultRGBTest() throws IOException {
        Path fileTest = Files.createTempFile(tempDir.toPath(),"defaultRGBTest", "tiff");

        //-- test : 3 bands type : byte RGB --//
        defaultTest("defaultRGBTest : 3 bands type : Byte RGB: ", fileTest,
                SampleType.BYTE, 3, PhotometricInterpretation.RGB);

        //-- test : 4 bands type : byte RGB --//
        defaultTest("defaultRGBTest : 4 bands type : Byte RGB: ", fileTest,
                SampleType.BYTE, 4, PhotometricInterpretation.RGB);
    }

    /**
     * Test writing/ reading action with an color map image.
     *
     * @throws IOException if problem during reading/writing action.
     */
    @Test
//    @Ignore
    public void defaultColorMapTest() throws IOException {
        Path fileTest = Files.createTempFile(tempDir.toPath(),"defaultColorMapTest", "tiff");

        //-- test : 1 bands type : byte palette --//
        defaultTest("defaultColorMapTest : 1 band type : Byte Palette: ", fileTest,
                SampleType.BYTE, 1, PhotometricInterpretation.PALETTE);

        //-- uncomment this code when a solution for multi band with color palette will be approuved.

//        //-- test : 3 bands type : byte RGB --//
//        defaultTest("defaultColorMapTest : 3 bands type : Byte Palette: ", fileTest,
//                Byte.SIZE, 3, PHOTOMETRIC_PALETTE, SAMPLEFORMAT_UINT);
//
//        //-- test : 4 bands type : byte RGB --//
//        defaultTest("defaultColorMapTest : 4 bands type : Byte RGB: ", fileTest,
//                Byte.SIZE, 4, PHOTOMETRIC_PALETTE, SAMPLEFORMAT_UINT);
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
        regionTest("CenterAreaTest", IMAGE_CENTER);
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
    protected void regionTest(final String message, final ImageOrientation imageOrientation) throws IOException {
        final File fileTest = File.createTempFile(message, "tiff", tempDir);

        //-------------------- test : 1 band -----------------------------------//
        //-- type byte
        generalTest(message+" : 1 band, type : byte.", fileTest, SampleType.BYTE, 1,
                PhotometricInterpretation.GRAYSCALE, imageOrientation);
        //-- type short
        generalTest(message + " : 1 band, type : short.", fileTest, SampleType.USHORT, 1,
                PhotometricInterpretation.GRAYSCALE, imageOrientation);
        //-- type int
        generalTest(message + " : 1 band, type : int.", fileTest, SampleType.INTEGER, 1,
                PhotometricInterpretation.GRAYSCALE, imageOrientation);
        //-- type Float
        generalTest(message + " : 1 band, type : float.", fileTest, SampleType.FLOAT, 1,
                PhotometricInterpretation.GRAYSCALE, imageOrientation);
        //-- type double
        generalTest(message + " : 1 band, type : double.", fileTest, SampleType.DOUBLE, 1,
                PhotometricInterpretation.GRAYSCALE, imageOrientation);

        //-- RGB --//
        //-- type Byte RGB
        generalTest(message+" : 3 bands RGB, type : Byte.", fileTest, SampleType.BYTE, 3,
                PhotometricInterpretation.RGB, imageOrientation);
        generalTest(message + " : 4 bands RGB, type : Byte.", fileTest, SampleType.BYTE, 4,
                PhotometricInterpretation.RGB, imageOrientation);

        //-- color Map --//
        //-- type Byte
        generalTest(message + " : 1 bands ColorMap, type : Byte.", fileTest, SampleType.BYTE, 1,
                PhotometricInterpretation.PALETTE, imageOrientation);
        //-- uncomment this code when a solution for multi band with color palette will be approuved.
//        generalTest(message + " : 4 bands ColorMap, type : Byte.", fileTest, Byte.SIZE, 4,
//                PHOTOMETRIC_PALETTE, SAMPLEFORMAT_UINT, imageOrientation);
    }

    /**
     * Particularity case for bgr sample type.
     * More precisely particularity case for {@link sun.awt.image.ByteComponentRaster}.
     */
    @Test
    public void bgrRgbTest() throws IOException {

        final Path fileTest = Files.createTempFile(tempDir.toPath(),"bgrRgbTest", "tiff");

        //create the image to write
        final BufferedImage image = new BufferedImage(113, 59, BufferedImage.TYPE_3BYTE_BGR);
        final Graphics g = image.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 113, 59);
        g.dispose();

        defaultTest("bgrRgbTest", fileTest, image);
    }

    /**
     * Particularity case for ARGB image where all samples stored into one Integer sample type.
     * More precisely particularity case for {@link BufferedImage#TYPE_4BYTE_ABGR} image type.
     */
    @Test
    public void int_ARGBTest() throws IOException {

        final Path fileTest = Files.createTempFile(tempDir.toPath(),"int_ARGBTest", "tiff");

        //create the image to write
        final BufferedImage image = new BufferedImage(113, 59, BufferedImage.TYPE_INT_ARGB);
        final Graphics g = image.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 113, 59);
        g.dispose();

        writer.setOutput(fileTest); //-- to initialize writer
        writer.write(image, writerParam);
        writer.dispose();

        //close if output is a closable stream
        if (fileTest != null && !IOUtilities.canProcessAsPath(fileTest) && fileTest instanceof Closeable) {
            ((Closeable) fileTest).close();
        }

        //test image (read using Path input)
        reader.setInput(fileTest); //-- to initialize reader
        final RenderedImage tested = reader.read(0);
        reader.close();

        assertEquals("image width ", image.getWidth(), tested.getWidth(), DEFAULT_TOLERANCE);
        assertEquals("image height ", image.getHeight(), tested.getHeight(), DEFAULT_TOLERANCE);

        final SampleModel expectedSm = image.getSampleModel();
        final int expectedNumband    = expectedSm.getNumBands();

        final SampleModel testedSm   = tested .getSampleModel();

        assertEquals("numband : ", expectedNumband, testedSm.getNumBands());

        final PixelIterator sourcePix = PixelIteratorFactory.createRowMajorIterator(image);

        final PixelIterator testedPix = PixelIteratorFactory.createRowMajorIterator(tested);
        int b = 0;
        while (sourcePix.next()) {
            testedPix.next();

            assertEquals("pixel at coordinate : (x, y, b) : ("+sourcePix.getX()+", "+sourcePix.getY()+", "+(b - 1)+") : ",
            sourcePix.getSampleDouble(), testedPix.getSampleDouble(), DEFAULT_TOLERANCE);
            if (++b == expectedNumband) b = 0;
        }
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
     * To finish read image is compare to itself before writing.<br/><br/>
     *
     * Moreover, test different input / output type setted.
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
    private void defaultTest(final String message, final Path fileTest, final SampleType sampleType,
                             final int numBand, final PhotometricInterpretation photometricInterpretation)
            throws IOException {

        final int width  = random.nextInt(256) + 16;
        final int height = random.nextInt(256) + 16;
        final RenderedImage expected = createImageTest(width, height, sampleType, numBand, photometricInterpretation);

        defaultTest(message, fileTest, expected);
    }


    /**
     * Write "expected" image at "filetest" adress and read.<br/>
     * To finish read image is compare to itself before writing.<br/><br/>
     *
     * Moreover, test different input / output type setted.
     *
     * @param message in case of assertion error.
     * @param fileTest the place to be.
     * @param expected image which will be tested.
     * @see #SAMPLEFORMAT_UINT
     * @throws IOException if problem during reading/writing action.
     */
    private void defaultTest(final String message, final Path fileTest, final RenderedImage expected)
            throws IOException {

        final String messageSize = message + "\n Test image size(w/h) "+expected.getWidth()+"/"+expected.getHeight()+".";

        //test writing with different output type from Spi
        for (Class type : writer.getOriginatingProvider().getOutputTypes()) {

            Object out = null;
            try {
                if (type.equals(Path.class)) {
                    out = fileTest;
                } else if (type.equals(File.class)) {
                    out = fileTest.toFile();
                } else if (type.equals(String.class)) {
                    out = fileTest.toString();
                } else if (type.equals(OutputStream.class)) {
                    out = Files.newOutputStream(fileTest, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                } else if (type.equals(ImageOutputStream.class)) {
                    out = CoverageIO.createImageOutputStream(fileTest);
                } else if (type.equals(FileOutputStream.class)) {
                    out = new FileImageOutputStream(fileTest.toFile());
                }

                writer.setOutput(out); //-- to initialize writer
                writer.write(expected, writerParam);
                writer.dispose();

                //close if output is a closable stream
                if (out != null && !IOUtilities.canProcessAsPath(out) && out instanceof Closeable) {
                    ((Closeable) out).close();
                }

                //test image (read using Path input)
                reader.setInput(fileTest); //-- to initialize reader
                final RenderedImage tested = reader.read(0);
                reader.close();

                //test image
                checkImage(messageSize, expected, tested);
            } catch (Exception e) {
                final String messageType = messageSize + "\n Writer output : "+type.getCanonicalName()
                        + "\n Reader input : "+fileTest.getClass().getCanonicalName()+"."
                        + "\n Cause : "+e.getMessage();
                fail(messageType);
            }
        }

        //test reading with every reader spy inputTypes (except Path because already tested)
        for (Class type : reader.getOriginatingProvider().getInputTypes()) {

            Object in = null;
            try {
                if (type.equals(File.class)) {
                    in = fileTest.toFile();
                } else if (type.equals(String.class)) {
                    in = fileTest.toString();
                } else if (type.equals(InputStream.class)) {
                    in = Files.newInputStream(fileTest);
                } else if (type.equals(ImageInputStream.class)) {
                    in = CoverageIO.createImageInputStream(fileTest);
                }

                reader.setInput(fileTest); //-- to initialize reader
                final RenderedImage tested = reader.read(0);
                reader.close();

                //close if input is a closable stream
                if (in != null && !IOUtilities.canProcessAsPath(in) && in instanceof Closeable) {
                    ((Closeable) in).close();
                }

                //test image
                checkImage(messageSize, expected, tested);
            } catch (Exception e) {
                final String messageType = messageSize + "\n Reader input : "+type.getCanonicalName()+"."
                        + "\n Cause : "+e.getMessage();
                fail(messageType);
            }
        }
    }

    /**
     * Effectuate a test in function of given parameter.
     * Internaly, a source image is generate with width height region and subsampling random values.
     *
     * @param message in case of error first part of error message.
     * @param fileTest the place to be.
     * @param sampleType
     * @param numBand source image numband.
     * @param photometricInterpretation define RGB or 1 band or also color map.
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
    protected void generalTest(final String message, final File fileTest,
            final SampleType sampleType, final int numBand,
            final PhotometricInterpretation photometricInterpretation,
            final ImageOrientation imageOrientation) throws IOException {
        int width  = random.nextInt(256) + 16;
        int height = random.nextInt(256) + 16;

        final RenderedImage sourceImage = createImageTest(width, height, sampleType, numBand, photometricInterpretation);

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

        StringBuilder seedMessage = new StringBuilder(message);
        seedMessage.append("\n").append("Test image size (w/h) : ").append(width).append("/").append(height).append("\n");
        seedMessage.append("SourceRegion : ").append(sourceRegion).append("\n");
        seedMessage.append("subsampleX/subsampleY : ").append(subsampleX).append("/").append(subsampleY).append("\n");
        seedMessage.append("subsampleXOffset/subsampleYOffset : ").append(subsampleXOffset).append("/").append(subsampleYOffset).append("\n");
        seedMessage.append("destOffset : ").append(destOffset).append("\n");

        try {
            final RenderedImage testedImage = effectuateTest(fileTest, sourceImage, sourceRegion,
                    subsampleX, subsampleY, subsampleXOffset, subsampleYOffset, destOffset);

            checkImages(message, sourceImage, sourceRegion, subsampleX, subsampleXOffset, subsampleY, subsampleYOffset, destOffset, testedImage);
        } catch (Exception e) {
            fail(seedMessage.toString()+"Cause : "+e.getMessage());
        }
    }

    /**
     * Compare two {@link RenderedImage} and throw an assertion exception if comparison criterion are not respected.
     *
     * @param message     in case of error first part of error message.
     * @param sourceImage source image
     * @param testedImage image which will be compare than source.
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
//        assertEquals(message+"numDataElement : ", expectedSm.getNumDataElements(), testedSm.getNumDataElements());
//        assertEquals(message+"datatype : ", expectedSm.getDataType(), testedSm.getDataType());

        final PixelIterator sourcePix = PixelIteratorFactory.createRowMajorIterator(sourceImage, sourceRegion);

        final Rectangle testedRegion = new Rectangle(destOffsetX, destOffsetY, expectedWidth, expectedHeight);
        final PixelIterator testedPix = PixelIteratorFactory.createRowMajorIterator(testedImage, testedRegion);

        for (int y = sourceRegion.y; y < sourceRegion.y + sourceRegion.height; y += sourceYsubsampling) {
            for (int x = sourceRegion.x; x < sourceRegion.x + sourceRegion.width; x += sourceXsubsampling) {
                sourcePix.moveTo(x, y, 0);
                int b = 0;
                while (b++ < expectedNumband) {
                    testedPix.next();
                    assertEquals(message+"pixel at coordinate : (x, y, b) : ("+sourcePix.getX()+", "+sourcePix.getY()+", "+(b - 1)+") : ",
                    sourcePix.getSampleDouble(), testedPix.getSampleDouble(), DEFAULT_TOLERANCE);
                    sourcePix.next();
                }
            }
        }
    }

    /**
     * Build an appropriate {@link ImageTypeSpecifier} in function of given parameter.
     *
     * @param sampleType
     * @param numBand expected band number
     * @param photometricInterpretation
     * @return {@link ImageTypeSpecifier}.
     * @throws UnsupportedImageFormatException if photometricInterpretation or sampleFormat are not in accordance with other parameters.
     */
    protected ImageTypeSpecifier buildImageTypeSpecifier(final SampleType sampleType, final int numBand,
                                                         final PhotometricInterpretation photometricInterpretation)
            throws UnsupportedImageFormatException {
        return ImageUtils.buildImageTypeSpecifier(sampleType, numBand,
                                                    photometricInterpretation, PlanarConfiguration.INTERLEAVED,
                                                    false, false,
                                                    buildColorMapArray(sampleType));
    }

    /**
     * Create an appropriate {@link RenderedImage} adapted for test which respond from all properties parameters.<br>
     * PlanarConfiguration is define as {@link PlanarConfiguration#INTERLEAVED}.
     *
     * @param width width of created image.
     * @param height height of created image
     * @param sampleType
     * @param numBand samples number for each pixel.
     * @param photometricInterpretation
     * @return created {@link RenderedImage}.
     * @throws UnsupportedImageFormatException if sampleBitsSize has a wrong value of photometricInterpretation is not supported.
     */
    protected WritableRenderedImage createImageTest(final int width, final int height,
                                                    final SampleType sampleType,
                                                    final int numBand,
                                                    final PhotometricInterpretation photometricInterpretation)
            throws UnsupportedImageFormatException {

        final ImageTypeSpecifier imgType = buildImageTypeSpecifier(sampleType, numBand, photometricInterpretation);

        final BufferedImage buffImg = imgType.createBufferedImage(width, height);
        fillImage(buffImg, imgType.getSampleModel().getDataType());
        return buffImg;
    }

    /**
     * Create a map array which contain random {@link Integer} values
     * adapted to create an appropriate {@link IndexColorModel}.
     *
     * @param sampleType define sample type Byte, short ...
     * @return map array.
     */
    protected int[] buildColorMapArray(final SampleType sampleType) {
        final int size = (SampleType.BYTE.equals(sampleType)) ? ((1 << Byte.SIZE) - 1) : (1 << Short.SIZE) - 1;
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
