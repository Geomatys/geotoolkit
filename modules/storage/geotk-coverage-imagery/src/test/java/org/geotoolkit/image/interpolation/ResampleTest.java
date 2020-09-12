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
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Test resampling class.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class ResampleTest extends org.geotoolkit.test.TestBase {
    /**
     * Transformation applicate to source or destination image to considered a pixel orientation center.
     */
    private static final MathTransform pixelInCellCenter = new AffineTransform2D(1, 0, 0, 1, 0.5, 0.5);

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
     * Effectuate Resample test which use internaly a grid.
     * This test, test bilinear resample from computed coordinate into grid and moreover image sample values.
     */
    @Test
    public void gridCoordinateTest() throws NoSuchAuthorityCodeException, FactoryException, TransformException {

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

        //-- creation du crs
        final CoordinateReferenceSystem crs = CRS.forCode("EPSG:2154");//-- world mercator 3395

        final ProjectedCRS projCRS = (ProjectedCRS) crs;

        final CoordinateReferenceSystem baseCrs = projCRS.getBaseCRS();

        final MathTransform mt = projCRS.getConversionFromBase().getMathTransform(); //-- source to dest

//        final Envelope srcEnv = new Envelope2D(baseCrs, 20, 44.5, 45, 45); //-- geographic

        final Envelope srcEnv = new Envelope2D(baseCrs,  45,-8, 5, 16); //-- geographic

        final Envelope destEnv = Envelopes.transform(mt, srcEnv);//-- dest envelop

        final AffineTransform2D srcGridToCrs = new AffineTransform2D(srcEnv.getSpan(0) / 4, 0, 0, -srcEnv.getSpan(1) / 4, srcEnv.getMinimum(0), srcEnv.getMaximum(1));

        final AffineTransform2D destGridToCrs = new AffineTransform2D(destEnv.getSpan(0) / 12, 0, 0, -destEnv.getSpan(1) / 12, destEnv.getMinimum(0), destEnv.getMaximum(1));

        final MathTransform pixSrcGridToCrs = MathTransforms.concatenate(pixelInCellCenter, srcGridToCrs);

        final MathTransform pixDestGridToProjCrs = MathTransforms.concatenate(pixelInCellCenter, destGridToCrs);

        final MathTransform theMTSrcToDest = MathTransforms.concatenate(pixSrcGridToCrs, mt, pixDestGridToProjCrs.inverse());

        /*
         * Resampling
         */
        final Resample resample = new Resample(theMTSrcToDest.inverse(), targetImage, sourceImg,
                InterpolationCase.BICUBIC, ResampleBorderComportement.EXTRAPOLATION, new double[]{0});
        resample.fillImage();
    }

    /**
     * Affect appropriate image for tests.
     *
     * @param width image width.
     * @param height image height.
     * @param dataType image data type.
     * @param value fill image with this value.
     */
    private void setTargetImage(int width, int height, int dataType, double value) {
        final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        final ColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, dataType);
        final ImageTypeSpecifier imgTypeSpec = new ImageTypeSpecifier(cm, cm.createCompatibleSampleModel(1, 1));
        targetImage = imgTypeSpec.createBufferedImage(width, height);

        final WritablePixelIterator pix = WritablePixelIterator.create(targetImage);

        while (pix.next()) {
            pix.setSample(0, value);
        }
    }
}
