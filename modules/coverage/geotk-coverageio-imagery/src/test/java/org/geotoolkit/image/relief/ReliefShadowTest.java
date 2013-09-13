package org.geotoolkit.image.relief;


import com.sun.media.imageioimpl.plugins.clib.CLibImageReader;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.WritableRenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.media.jai.ComponentSampleModelJAI;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.image.relief.ReliefShadow;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import sun.awt.image.WritableRasterNative;
import static org.junit.Assert.assertTrue;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rmarechal
 */
public class ReliefShadowTest {

    private final BufferedImage sourceImage;
    private final PixelIterator srcIter;
//    
    private final BufferedImage mnt;
    private final PixelIterator mntIter;
    
    public ReliefShadowTest() {
        sourceImage = new BufferedImage(5, 5, BufferedImage.TYPE_BYTE_GRAY);
        srcIter = PixelIteratorFactory.createDefaultWriteableIterator(sourceImage, sourceImage);
        
        mnt = new BufferedImage(5, 5, BufferedImage.TYPE_BYTE_GRAY);
        mntIter = PixelIteratorFactory.createDefaultWriteableIterator(mnt, mnt);
//        source
        
    }
    
    @Test
    public void shadowTest() throws IOException, NoninvertibleTransformException, TransformException {
        
        final File bluePath = new File("/home/rmarechal/Documents/image/world-mnt/bluemarble.tiff");
        ImageReader blueReader = null;
        Iterator<ImageReader> blueIT = ImageIO.getImageReaders(ImageIO.createImageInputStream(bluePath));
        
        while (blueIT.hasNext()) {
            blueReader = blueIT.next();
        }
        blueReader.setInput(ImageIO.createImageInputStream(bluePath), true, false);
        
        final BufferedImage blueMarble = blueReader.read(0);
        
        final File mntPath = new File("/home/rmarechal/Documents/image/world-mnt/marblemnt.tif");
        ImageReader mntReader = null;
        Iterator<ImageReader> mntIT = ImageIO.getImageReaders(ImageIO.createImageInputStream(mntPath));
        
        while (mntIT.hasNext()) {
            mntReader = mntIT.next();
        }
        mntReader.setInput(ImageIO.createImageInputStream(mntPath), true, false);
        
        final RenderedImage mnt = mntReader.read(0);
        
//        final double m00 = mnt.getWidth() / (double)blueMarble.getWidth();
//        final double m11 = mnt.getHeight() / (double)blueMarble.getHeight();
//        WritableRenderedImage dest = new BufferedImage(blueMarble.getWidth()>>1, blueMarble.getHeight()>>1, blueMarble.getSampleModel().getDataType());
        
//        WritableRenderedImage dest = new BufferedImage(blueMarble.getColorModel(), Raster.createWritableRaster(new PixelInterleavedSampleModel(2, 2160, 1080, 1, 2160, new int[]{0}), new Point(0, 0)), true, null);
        
//        SampleModel samp = new ComponentSampleModel(blueMarble.getType(), blueMarble.getWidth()>>1, blueMarble.getHeight()>>1, blueMarble.getSampleModel().getDataType(), scanlineStride, bandOffsets)
//        new PixelInterleavedSampleModel(2, 2160, 1080, 1, 2160, new int[]{0});
        ///////// resample
//        AffineTransform2D aff2d = new AffineTransform2D(2, 0, 0, 2, 0, 0);
//        Interpolation interpol =  Interpolation.create(PixelIteratorFactory.createRowMajorIterator(blueMarble), InterpolationCase.BICUBIC, 2);
//        
//        
//        Resample resamp = new Resample(aff2d, dest, interpol, new double[]{0});
//        resamp.fillImage();
        final double scaleZ = 40000000.0 / blueMarble.getWidth();
        ReliefShadow rs = new ReliefShadow(45, 2, 0.4);
        RenderedImage dest = rs.getRelief(blueMarble, mnt, scaleZ/2);
        ImageIO.write(dest, "tiff", new File("/home/rmarechal/Documents/image/world-mnt/blueShadowMarble.tiff"));
        
    }
    
    @Ignore
    @Test
    public void oneCenterPikeTest() throws IOException {
        initTest(0,2,1,2,2,1);
        ReliefShadow rf = new ReliefShadow(90, 22.5,0);
        RenderedImage dest = rf.getRelief(sourceImage, mnt, 1);
        PixelIterator destIter = PixelIteratorFactory.createRowMajorIterator(dest);
        while (destIter.next()) {
            System.out.println("("+destIter.getX()+", "+destIter.getY()+") : "+destIter.getSample());
        }
        ImageIO.write(mnt, "tiff", new File("/home/rmarechal/Documents/image/test/mnt.tiff"));
        ImageIO.write(dest, "tiff", new File("/home/rmarechal/Documents/image/test/shadow.tiff"));
    }
    
    @Test
    public void angle0Test() {
        initTest(2,2,128);
        
        final ReliefShadow rf = new ReliefShadow(0, 45, 0);
        final RenderedImage result = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);
        
        for (int y = 0; y<5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x == 2 && y > 2) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
        
        final ReliefShadow rfInvert         = new ReliefShadow(-360, 45, 0);
        final RenderedImage resultInvert    = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }
    
    @Test
    public void angle45Test() {
        initTest(2,2,128);
        
        final ReliefShadow rf = new ReliefShadow(45, 45, 0);
        final RenderedImage result = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);
        
        for (int y = 0; y<5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x==y && x>2 && y>2) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
        
        final ReliefShadow rfInvert         = new ReliefShadow(-315, 45, 0);
        final RenderedImage resultInvert    = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }
    
    @Test
    public void angle90Test() {
        initTest(2,2,128);
        
        final ReliefShadow rf         = new ReliefShadow(90, 45, 0);
        final RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);
        
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x > 2 && y == 2) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
        
        final ReliefShadow rfInvert         = new ReliefShadow(-270, 45, 0);
        final RenderedImage resultInvert    = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }
    
    @Test
    public void angle135Test() {
        initTest(2,2,128);
        
        final ReliefShadow rf         = new ReliefShadow(135, 45, 0);
        final RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);
        
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x > 2 && y < 2 && (5-x-1) == y) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
        
        final ReliefShadow rfInvert         = new ReliefShadow(-225, 45, 0);
        final RenderedImage resultInvert    = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }
    
    @Test
    public void angle180Test() {
        initTest(2,2,128);
        
        final ReliefShadow rf         = new ReliefShadow(180, 45, 0);
        final RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);
        
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x == 2 && y < 2) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
        
        final ReliefShadow rfInvert         = new ReliefShadow(-180, 45, 0);
        final RenderedImage resultInvert    = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }
    
    @Test
    public void angle225Test() {
        initTest(2,2,128);
        
        final ReliefShadow rf         = new ReliefShadow(225, 45, 0);
        final RenderedImage result    = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);
        
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x < 2 && y < 2 && x == y) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
        
        final ReliefShadow rfInvert               = new ReliefShadow(-135, 45, 0);
        final RenderedImage resultInvert          = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }
    
    @Test
    public void angle270Test() {
        initTest(2,2,128);
        
        final ReliefShadow rf = new ReliefShadow(270, 45, 0);
        final RenderedImage result = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);
        
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x < 2 && y == 2 ) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
        
        final ReliefShadow rfInvert               = new ReliefShadow(-90, 45, 0);
        final RenderedImage resultInvert          = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }
    
    @Test
    public void angle315Test() {
        initTest(2,2,128);
        
        final ReliefShadow rf = new ReliefShadow(315, 45, 0);
        final RenderedImage result = rf.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);
        
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x < 2 && y > 2 && 5-y-1 == x) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
        
        final ReliefShadow rfInvert         = new ReliefShadow(-45, 45, 0);
        final RenderedImage resultInvert    = rfInvert.getRelief(sourceImage, mnt, 1);
        final PixelIterator pixResultinvert = PixelIteratorFactory.createRowMajorIterator(resultInvert);
        pixResult.rewind();
        while (pixResultinvert.next()) {
            pixResult.next();
            final int resultValue       = pixResult.getSample();
            final int resultInvertValue = pixResultinvert.getSample();
            final String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value : "+resultValue+" found : "+resultInvertValue;
            assertTrue(message, resultValue == resultInvertValue);
        }
    }
    
    @Test
    public void altitudeTest() {
        initTest(2, 2, 2);
        
        ReliefShadow rf = new ReliefShadow(45, 45, 0);
        RenderedImage result = rf.getRelief(sourceImage, mnt, 1);
        PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);
        
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (x == 3 && y == 3 ) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
        
        initTest(2, 2, 3);
        rf = new ReliefShadow(45, 45, 0);
        result = rf.getRelief(sourceImage, mnt, 1);
        pixResult = PixelIteratorFactory.createRowMajorIterator(result);
        
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if ((x == 3 && y == 3) || (x == 4 && y == 4)) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
        
        initTest(2, 2, 2);
        rf = new ReliefShadow(45, 22.5, 0);
        result = rf.getRelief(sourceImage, mnt, 1);
        pixResult = PixelIteratorFactory.createRowMajorIterator(result);
        
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if ((x == 3 && y == 3) || (x == 4 && y == 4)) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
    }
    
    @Test
    public void twoPikesTest() {
        initTest(0, 2, 1, 2, 2, 1);
        
        ReliefShadow rf = new ReliefShadow(90, 22.5, 0);
        RenderedImage result = rf.getRelief(sourceImage, mnt, 1);
        PixelIterator pixResult = PixelIteratorFactory.createRowMajorIterator(result);
        
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                pixResult.moveTo(x, y, 0);
                String message = "at ("+pixResult.getX()+", "+pixResult.getY()+") position, expected value ";
                final int pixValue = pixResult.getSample();
                if (y == 2 && (x == 1 || x == 3 || x == 4)) {
                    assertTrue(message+0+" found : "+pixValue, pixResult.getSample() == 0);
                } else {
                    assertTrue(message+255+" found : "+pixValue, pixResult.getSample() == 255);
                }
            }
        }
    }
    
    private void initTest(int ...coordinates) {
        // fill src images with white color
        srcIter.rewind();
        while (srcIter.next()) {
            srcIter.setSample(255);
        }
        
        // fill mnt at 0 altitude
        mntIter.rewind();
        while (mntIter.next()) {
            mntIter.setSample(0);
        }
        
        for (int p = 0; p < coordinates.length; p += 3) {
            mntIter.moveTo(coordinates[p], coordinates[p+1], 0);
            mntIter.setSample(coordinates[p+2]);
        }
    }
}
