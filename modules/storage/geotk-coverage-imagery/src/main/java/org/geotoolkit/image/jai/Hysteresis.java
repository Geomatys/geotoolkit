/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.image.jai;

import java.util.Map;
import java.util.Vector;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import javax.media.jai.ImageLayout;
import javax.media.jai.UntiledOpImage;
import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;


/**
 * Applies a hysteresis threshold on an image. This operation is defined by an upper threshold,
 * <var>high</var>, and a lower threshold, <var>low</var>. If a pixel value is equals or higher
 * than <var>high</var>, it is keep unchanged. If a pixel value is lower than <var>low</var>,
 * it is replaced by the pad value. If a pixel value is between <var>low</var> and <var>high</var>,
 * then this pixel is called "indeterminate". Its value is keep unchanged only if this pixel
 * is either a neighbor of a pixel having a value equals or higher than <var>high</var>, or a
 * neighbor of an other indeterminate pixel which has been determined close to a pixel having
 * a value equals or higher than <var>high</var> in a previous iteration. This search is performed
 * in an iterative manner until there is no more indeterminate pixels having satisfying neighbor.
 *
 * @author Lionel Flahaut (IRD)
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public class Hysteresis extends UntiledOpImage {
    /**
     * The name of this operation in the JAI registry.
     * This is {@value}.
     */
    public static final String OPERATION_NAME = "org.geotoolkit.Hysteresis";

    /**
     * The lower threshold value, inclusive. Pixels having a value
     * lower than this value will be set to the {@link #padValue}.
     */
    protected final double low;

    /**
     * The upper threshold value, inclusive. Pixels having a value
     * equals or higher than this value will be keep unchanged.
     */
    protected final double high;

    /**
     * The value to give to filtered pixel.
     */
    protected final double padValue;

    /**
     * Constructs a new Hysterisis filter for the given image. While this constructor is public,
     * it should usually not be invoked directly. You should use {@linkplain javax.media.jai.JAI}
     * factory methods instead.
     *
     * @param source        The source image.
     * @param layout        The image layout.
     * @param configuration The image properties and rendering hints.
     * @param low           The lower threshold value, inclusive.
     * @param high          The upper threshold value, inclusive.
     * @param padValue      The value to give to filtered pixel.
     */
    public Hysteresis(final RenderedImage source, final ImageLayout layout,
            final Map<?,?> configuration, final double low, final double high, final double padValue)
    {
        super(source, configuration, layout);
        this.low      = low;
        this.high     = high;
        this.padValue = padValue;
    }

    /**
     * Returns the source images.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Vector<RenderedImage> getSources() {
        return super.getSources();
    }

    /**
     * Computes a rectangle of outputs.
     *
     * @param sources  The source images. Should be an array of length 1.
     * @param dest     The raster to be filled in.
     * @param destRect The region within the raster to be filled.
     */
    @Override
    protected void computeImage(final Raster[]    sources,
                                final WritableRaster dest,
                                final Rectangle  destRect)
    {
        assert sources.length == 1;
        final Raster source = sources[0];
        Rectangle sourceRect = mapDestRect(destRect, 0);
        sourceRect = sourceRect.intersection(source.getBounds());
        final RandomIter iter = RandomIterFactory.create(source, sourceRect);
        final int minX = destRect.x;              // Minimum inclusive
        final int minY = destRect.y;              // Minimum inclusive
        final int maxX = destRect.width  + minX;  // Maximum exclusive
        final int maxY = destRect.height + minY;  // Maximum exclusive
        final int w    = width -1;
        final int h    = height-1;
        final boolean[] accepted = new boolean[destRect.width * destRect.height];
        final boolean[] rejected = new boolean[destRect.width * destRect.height];
        for (int band=source.getNumBands(); --band>=0;) {
            /*
             * Find immediately all accepted values (above the high threshold) and rejected values
             * (below the low threshold).    NaN values are both accepted and rejected ("accepted"
             * since they are going to be copied in the destination image, and "rejected" since
             * they do not cause the acceptation of neighbor values).
             */
            int index = 0;
            for (int y=minY; y<maxY; y++) {
                for (int x=minX; x<maxX; x++) {
                    final double current = iter.getSampleDouble(x, y, band);
                    accepted[index] = !(current < high); // Accept NaN values
                    rejected[index] = !(current >= low); // Accept NaN values
                    index++;
                }
            }
            assert index == accepted.length;
            /*
             * Complete the mask of "accepted" values. Unknow values (those which are neither
             * accepted or rejected) are tested for their proximity with an accepted value.
             * This loop will be run until there is no change.
             */
            int sign = +1;
            boolean changed;
            do {
                changed = false;
                final int stop;
                if (sign >= 0) {
                    index = 0;
                    stop  = accepted.length;
                } else {
                    index = accepted.length-1;
                    stop  = -1;
                }
                while (index != stop) {
                    if (!accepted[index] && !rejected[index]) {
                        int check;
                        final int y = index / width;
                        final int x = index % width;
                        if ((x!=0 && ((accepted[check=index-1      ] && !rejected[check])   ||
                            (y!=0 &&   accepted[check=index-1-width] && !rejected[check])   ||
                            (y!=h &&   accepted[check=index-1+width] && !rejected[check]))) ||
                            (x!=w && ((accepted[check=index+1      ] && !rejected[check])   ||
                            (y!=0 &&   accepted[check=index+1-width] && !rejected[check])   ||
                            (y!=h &&   accepted[check=index+1+width] && !rejected[check]))) ||
                            (y!=0 && ((accepted[check=index  -width] && !rejected[check]))) ||
                            (y!=w && ((accepted[check=index  +width] && !rejected[check]))))
                        {
                            accepted[index] = true;
                            changed = true;
                        }
                    }
                    index += sign;
                }
                sign = -sign;
            } while (changed);
            /*
             * Copy all accepted values in the destination raster.
             * Other values are replaced by NaN.
             */
            index = 0;
            for (int y=minY; y<maxY; y++) {
                for (int x=minX; x<maxX; x++) {
                    dest.setSample(x, y, band,
                                   accepted[index++] ? iter.getSampleDouble(x, y, band) : padValue);
                }
            }
            assert index == accepted.length;
        }
        iter.done();
    }
}
