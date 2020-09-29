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

import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import org.apache.sis.image.ImageProcessor;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Thomas Rouby (Geomatys)
 */
public final class TextureUtils {

    private TextureUtils(){}

    /**
     * @param sampleValue the value to set on non-existing pixel after resampling
     */
    public static RenderedImage sampleImage(RenderedImage imgSrc, MathTransform transform, double[] sampleValue) throws TransformException {
        final ImageProcessor processor = new ImageProcessor();
        processor.setInterpolation(org.apache.sis.image.Interpolation.NEAREST);
        final RenderedImage resampled = processor.resample(imgSrc, new Rectangle(imgSrc.getWidth(), imgSrc.getHeight()), transform);
        return resampled;
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

    public static double getNearestScale(double[] scales, double scale) {
        double dist = -1;
        int nearIndex = -1;
        for (int i = 0; i < scales.length; i++) {
            double tmpDist = Math.abs(scales[i]-scale);
            if ((tmpDist <= dist && scale >= scales[i]) || dist < 0 || nearIndex < 0) {
                dist=tmpDist;
                nearIndex=i;
            }
        }

        return scales[nearIndex];
    }
}
