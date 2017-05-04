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

import org.geotoolkit.math.XMath;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.Transform;

/**
 * From the RgbToYuv420 Transform of JCodec Project
 *
 * @author Thomas Rouby (Geomatys)
 *
 */
public class TransformRGBtoYUV420 implements Transform {

    private final int upShift;
    private final int downShift;
    private final int downShiftChr;

    public TransformRGBtoYUV420(int upShift, int downShift) {
        this.upShift = upShift;
        this.downShift = downShift;

        // The +2 is to create an average of 4 pixels with chr transform
        this.downShiftChr = downShift + 2;
    }

    /**
     * Transform a Picture object with one channel RGB to a Picture with three channel YUV on format YUV420
     * Format YUV420
     *   Channel 1 is int[width*height] for Y values
     *   Channel 2 is int[width/2 * height/2] for U
     *   Channel 3 is int[width/2 * height/2] for V
     *
     *   each square of 2x2 Y correspond to a unique UV value
     *
     * This transform try to rescale src picture on dst size with linear method.
     *
     * @param src
     * @param dst must has a multiple of two for width and height. If not, last pixel forget.
     */
    public void transform(Picture src, Picture dst) {
        final int[] srcData = src.getData()[0];
        final int[][] dstData = dst.getData();

        final int srcWidth = src.getWidth();
        final int srcHeight = src.getHeight();

        final int dstWidth = dst.getWidth();
        final int dstHeight = dst.getHeight();

        for (int h=0; h<dstHeight-1; h+=2) {

            final int srcH0 = (int)(((double)h/(double)dstHeight)*srcHeight);
            final int srcH1 = (int)(((double)(h+1)/(double)dstHeight)*srcHeight);

            for (int w=0; w<dstWidth-1; w+=2) {

                final int srcW0 = (int)(((double)w/(double)dstWidth)*srcWidth);
                final int srcW1 = (int)(((double)(w+1)/(double)dstWidth)*srcWidth);

                // Compute array index for the pixels conversion

                final int chr = ind(w >> 1, h >> 1, dstWidth >> 1);

                final int ind00 = ind(srcW0, srcH0, srcWidth) * 3; final int luma00 = ind(w,   h,   dstWidth);
                final int ind01 = ind(srcW0, srcH1, srcWidth) * 3; final int luma01 = ind(w,   h+1, dstWidth);
                final int ind10 = ind(srcW1, srcH0, srcWidth) * 3; final int luma10 = ind(w+1, h,   dstWidth);
                final int ind11 = ind(srcW1, srcH1, srcWidth) * 3; final int luma11 = ind(w+1, h+1, dstWidth);

                rgb2yuv(srcData[ind00], srcData[ind00+1], srcData[ind00+2], dstData[0], luma00, dstData[1], chr, dstData[2], chr);
                dstData[0][luma00] = shiftLuma(dstData[0][luma00]);

                rgb2yuv(srcData[ind01], srcData[ind01+1], srcData[ind01+2], dstData[0], luma01, dstData[1], chr, dstData[2], chr);
                dstData[0][luma01] = shiftLuma(dstData[0][luma01]);

                rgb2yuv(srcData[ind10], srcData[ind10+1], srcData[ind10+2], dstData[0], luma10, dstData[1], chr, dstData[2], chr);
                dstData[0][luma10] = shiftLuma(dstData[0][luma10]);

                rgb2yuv(srcData[ind11], srcData[ind11+1], srcData[ind11+2], dstData[0], luma11, dstData[1], chr, dstData[2], chr);
                dstData[0][luma11] = shiftLuma(dstData[0][luma11]);

                dstData[1][chr] = shiftChroma(dstData[1][chr]);
                dstData[2][chr] = shiftChroma(dstData[2][chr]);
            }
        }
    }

    private int ind(int w, int h, int width){
        return w + h*width;
    }

    private int shiftLuma(int luma) {
        return (luma << upShift) >> downShift;
    }

    private int shiftChroma(int chr) {
        return (chr << upShift) >> downShiftChr;
    }

    public static final void rgb2yuv(int r, int g, int b, int[] Y, int offY, int[] U, int offU, int[] V, int offV) {
        int y = 66 * r + 129 * g + 25 * b;
        int u = -38 * r - 74 * g + 112 * b;
        int v = 112 * r - 94 * g - 18 * b;
        y = (y + 128) >> 8;
        u = (u + 128) >> 8;
        v = (v + 128) >> 8;

        Y[offY] = XMath.clamp(y + 16, 0, 255);
        U[offU] += XMath.clamp(u + 128, 0, 255);
        V[offV] += XMath.clamp(v + 128, 0, 255);
    }
}
