 /*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.image.interpolation;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import javax.imageio.ImageTypeSpecifier;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorConform;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Test resampling class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class ResampleTest {

    /**
     * Expected result about neighbor interpolation.
     */
    private static double[] NEIGHBOR_RESULT = new double[]{0,0,0,0,0,0,0,0,0,
                                                           0,1,1,1,1,1,1,1,0,
                                                           0,1,1,1,1,1,1,1,0,
                                                           0,1,1,2,2,2,1,1,0,
                                                           0,1,1,2,2,2,1,1,0,
                                                           0,1,1,2,2,2,1,1,0,
                                                           0,1,1,1,1,1,1,1,0,
                                                           0,1,1,1,1,1,1,1,0,
                                                           0,0,0,0,0,0,0,0,0};
    
    /**
     * Expected result about bilinear interpolation.
     */
    private static double[] BILINEAR_RESULT = new double[]{0,0,           0,           0,           0,           0,           0, 0,0,
                                                           0,1,           1,           1,           1,           1,           1, 1,0,
                                                           0,1, 1.111111111, 1.222222222, 1.333333333, 1.222222222, 1.111111111, 1,0,
                                                           0,1, 1.222222222, 1.444444444, 1.666666666, 1.444444444, 1.222222222, 1,0,
                                                           0,1, 1.333333333, 1.666666666,           2, 1.666666666, 1.333333333, 1,0,
                                                           0,1, 1.222222222, 1.444444444, 1.666666666, 1.444444444, 1.222222222, 1,0,
                                                           0,1, 1.111111111, 1.222222222, 1.333333333, 1.222222222, 1.111111111, 1,0,
                                                           0,1,           1,           1,           1,           1,           1, 1,0,
                                                           0,0,           0,           0,           0,           0,           0, 0,0};
    
    /**
     * Expected result about bicubic interpolation.
     */
    private static double[] BICUBIC_Result = new double[]{0.0, 0.0,                0.0,                0.0,                0.0,                0.0,                0.0,                0.0,                0.0,                0.0, 0.0, 0.0, 
                                                          0.0, 1.0,                1.0,                1.0,                1.0,                  1,                  1,                  1,                  1,                  1,   1, 0.0, 
                                                          0.0, 1.0, 1.1975308641975309, 1.3456790123456792, 1.4444444444444444, 1.4938271604938276, 1.4938271604938276, 1.4444444444444444, 1.3456790123456788, 1.1975308641975306,   1, 0.0, 
                                                          0.0, 1.0,  1.345679012345679, 1.6049382716049385, 1.7777777777777781, 1.8641975308641983, 1.8641975308641983, 1.7777777777777781,  1.604938271604938,  1.345679012345679,   1, 0.0, 
                                                          0.0, 1.0, 1.4444444444444444,  1.777777777777778,                  2, 2.1111111111111125, 2.1111111111111125,                  2, 1.7777777777777772, 1.4444444444444446,   1, 0.0, 
                                                          0.0,   1, 1.4938271604938271, 1.8641975308641974, 2.1111111111111116, 2.2345679012345694, 2.2345679012345694, 2.1111111111111116, 1.8641975308641967, 1.4938271604938274,   1, 0.0,
                                                          0.0,   1,  1.493827160493827, 1.8641975308641971, 2.1111111111111116,   2.23456790123457,   2.23456790123457, 2.1111111111111116, 1.8641975308641963, 1.4938271604938274,   1, 0.0, 
                                                          0.0,   1,  1.444444444444444, 1.7777777777777772,                  2,  2.111111111111114,  2.111111111111114,                  2,  1.777777777777776, 1.4444444444444446,   1, 0.0,
                                                          0.0,   1, 1.3456790123456783, 1.6049382716049374, 1.7777777777777781,    1.8641975308642,    1.8641975308642, 1.7777777777777788, 1.6049382716049356,  1.345679012345679,   1, 0.0, 
                                                          0.0,   1,   1.19753086419753, 1.3456790123456774, 1.4444444444444446, 1.4938271604938305, 1.4938271604938305,  1.444444444444445, 1.3456790123456754, 1.1975308641975306,   1, 0.0, 
                                                          0.0,   1,                  1,                  1,                  1,                  1,                  1,                  1,                  1,                  1,   1, 0.0, 
                                                          0.0, 0.0,                0.0,                0.0,                0.0,                0.0,                0.0,                0.0,                0.0,                0.0, 0.0, 0.0};
    
    
    /**
     * Transformation applicate to source or destination image to considered a pixel orientation center.
     */
    private static MathTransform pixelInCellCenter = new AffineTransform2D(1, 0, 0, 1, 0.5, 0.5);
    
    /**
     * Transformation applicate on source image for resampling.
     */
    private MathTransform mathTransform;

    /**
     * Interpolation applicate during resampling.
     */
    private Interpolation interpolation;

    /**
     * Destination image.
     * Resampling result.
     */
    private WritableRenderedImage targetImage;

    /**
     * Source image.
     * Image within interpolation computing is applicate.
     */
    private WritableRenderedImage sourceImg;

    public ResampleTest() {

        final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        final ColorModel cm = new ComponentColorModel(cs, new int[]{Double.SIZE}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_DOUBLE);
        final ImageTypeSpecifier imgTypeSpec = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
        sourceImg = imgTypeSpec.createBufferedImage(3, 3);
        final WritableRaster raster = sourceImg.getWritableTile(0, 0);
        
        raster.setSample(0, 0, 0, 1);
        raster.setSample(1, 0, 0, 1);
        raster.setSample(2, 0, 0, 1);
        
        raster.setSample(0, 1, 0, 1);
        raster.setSample(1, 1, 0, 2);
        raster.setSample(2, 1, 0, 1);
        
        raster.setSample(0, 2, 0, 1);
        raster.setSample(1, 2, 0, 1);
        raster.setSample(2, 2, 0, 1);
    }

    /**
     * Test result obtained from neighBor interpolation and a resampling.
     *
     * @throws NoninvertibleTransformException
     * @throws FactoryException
     * @throws TransformException
     */
    @Test
    public void jaiNeighBorTest() throws NoninvertibleTransformException, FactoryException, TransformException {

        setTargetImage(9, 9, DataBuffer.TYPE_DOUBLE, -1000);
        setInterpolation(sourceImg, InterpolationCase.NEIGHBOR);
        setAffineMathTransform(MathTransforms.concatenate(pixelInCellCenter, new AffineTransform2D(3, 0, 0, 3, 0, 0), pixelInCellCenter.inverse()));

        /*
         * Resampling
         */
        Resample resample = new Resample(mathTransform.inverse(), targetImage, null, interpolation, new double[]{0}, ResampleBorderComportement.FILL_VALUE);
        resample.fillImage();
        Raster coverageRaster = targetImage.getTile(0, 0);
        
        java.awt.image.DataBufferDouble datadouble = (java.awt.image.DataBufferDouble) coverageRaster.getDataBuffer();
        assertArrayEquals(NEIGHBOR_RESULT, datadouble.getData(0), 1E-9);
    }

    /**
     * Test result obtained from biLinear interpolation and a resampling.
     *
     * @throws NoninvertibleTransformException
     * @throws FactoryException
     * @throws TransformException
     */
    @Test
    public void jaiBiLinearTest() throws NoninvertibleTransformException, FactoryException, TransformException {

        setTargetImage(9, 9, DataBuffer.TYPE_DOUBLE,  -1000);
        setInterpolation(sourceImg, InterpolationCase.BILINEAR);
        setAffineMathTransform(MathTransforms.concatenate(pixelInCellCenter, new AffineTransform2D(3, 0, 0, 3, 0, 0), pixelInCellCenter.inverse()));

        /*
         * Resampling
         */
        final Resample resample = new Resample(mathTransform.inverse(), targetImage, null, interpolation, new double[]{0}, ResampleBorderComportement.FILL_VALUE);
        resample.fillImage();
        final Raster coverageRaster = targetImage.getTile(0, 0);
        java.awt.image.DataBufferDouble datadouble = (java.awt.image.DataBufferDouble) coverageRaster.getDataBuffer();
        assertArrayEquals(BILINEAR_RESULT, datadouble.getData(0), 1E-9);
    }

    /**
     * Test result obtained from biCubic interpolation and a resampling.
     *
     * @throws NoninvertibleTransformException
     * @throws FactoryException
     * @throws TransformException
     */
    @Test
    public void jaiBiCubicTest() throws NoninvertibleTransformException, FactoryException, TransformException {
        final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        final ColorModel cm = new ComponentColorModel(cs, new int[]{Double.SIZE}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_DOUBLE);
        final ImageTypeSpecifier imgTypeSpec = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
        sourceImg = imgTypeSpec.createBufferedImage(4, 4);
        final WritableRaster raster = sourceImg.getWritableTile(0, 0);
        //band0
        raster.setSample(0, 0, 0, 1);
        raster.setSample(1, 0, 0, 1);
        raster.setSample(2, 0, 0, 1);
        raster.setSample(3, 0, 0, 1);
        
        raster.setSample(0, 1, 0, 1);
        raster.setSample(1, 1, 0, 2);
        raster.setSample(2, 1, 0, 2);
        raster.setSample(3, 1, 0, 1);
        
        raster.setSample(0, 2, 0, 1);
        raster.setSample(1, 2, 0, 2);
        raster.setSample(2, 2, 0, 2);
        raster.setSample(3, 2, 0, 1);
        
        raster.setSample(0, 3, 0, 1);
        raster.setSample(1, 3, 0, 1);
        raster.setSample(2, 3, 0, 1);
        raster.setSample(3, 3, 0, 1);

        setTargetImage(12, 12, DataBuffer.TYPE_DOUBLE, -1000);
        setInterpolation(sourceImg, InterpolationCase.BICUBIC);
         
        setAffineMathTransform(MathTransforms.concatenate(pixelInCellCenter, new AffineTransform2D(3, 0, 0, 3, 0, 0), pixelInCellCenter.inverse()));

        /*
         * Resampling
         */
        final Resample resample = new Resample(mathTransform.inverse(), targetImage, null, interpolation, new double[]{0}, ResampleBorderComportement.FILL_VALUE);
        resample.fillImage();
        final Raster coverageRaster = targetImage.getTile(0, 0);

        java.awt.image.DataBufferDouble datadouble = (java.awt.image.DataBufferDouble) coverageRaster.getDataBuffer();
        assertArrayEquals(BICUBIC_Result, datadouble.getData(0), 1E-9);
    }

    /**
     * Affect appropriate image for tests.
     *
     * @param width image width.
     * @param height image height.
     * @param dataType image data type.
     * @param value fill image with this value.
     */
    private void setTargetImage(int width, int height,
            int dataType, double value) {
        final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        final ColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, dataType);
        final ImageTypeSpecifier imgTypeSpec = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
        targetImage = imgTypeSpec.createBufferedImage(width, height);

        final PixelIterator pix = PixelIteratorFactory.createDefaultWriteableIterator(targetImage, targetImage);
        
        while (pix.next()) {
            pix.setSampleDouble(value);
        }
    }

    /**
     * Affect appropriate interpolation about test.
     *
     * @param sourceImage image which will be iterate for tests.
     * @param interpolCase chosen interpolator.
     */
    private void setInterpolation(WritableRenderedImage sourceImage, InterpolationCase interpolCase) {
        interpolation = Interpolation.create(new PixelIteratorConform(sourceImage), interpolCase, 0);
    }
    
    /**
     * Affect MathTransform with appropriate test values.
     * 
     * @param mt 
     */
    private void setAffineMathTransform(MathTransform mt) {
        mathTransform = mt;
    }
}
