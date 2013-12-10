/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.display3d.utils;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import org.geotoolkit.image.interpolation.Interpolation;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Thomas Rouby (Geomatys)
 */
public final class TextureUtils {

    private TextureUtils(){}

    /**
     * @param imgSrc
     * @param transform
     * @param sampleValue the value to set on non-existing pixel after resampling
     * @return
     */
    public static RenderedImage sampleImage(RenderedImage imgSrc, MathTransform transform, double[] sampleValue) throws TransformException {

        final ColorModel colorModel = imgSrc.getColorModel();

        final PixelIterator it = PixelIteratorFactory.createRowMajorIterator(imgSrc);
        final Interpolation interpol = Interpolation.create(it, InterpolationCase.NEIGHBOR, 2);
//        final Rectangle area = new Rectangle(0, startRow, width, Math.min(tile_size_y - startRow, threadHeight));
        final WritableRaster raster = colorModel.createCompatibleWritableRaster(imgSrc.getWidth(), imgSrc.getHeight());
        final BufferedImage destImage = new BufferedImage(colorModel, raster, false, null);

        final Resample resampler = new Resample(transform, destImage, interpol, sampleValue);
        resampler.fillImage();

        return destImage;
    }

    public static int getNearestScaleIndex(double[] scales, double scale) {
        double dist = -1;
        int nearIndex = -1;
        for (int i = 0; i < scales.length; i++) {
            double tmpDist = Math.abs(scales[i]-scale);
            if ((tmpDist <= dist && scale >= scales[i]) || dist < 0 || nearIndex < 0) {
                dist=tmpDist;
                nearIndex=i;
            }
        }

        return nearIndex;
    }
}
