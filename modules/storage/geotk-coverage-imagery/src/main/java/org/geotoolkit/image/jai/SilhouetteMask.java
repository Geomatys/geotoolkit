/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.util.Set;
import java.util.Vector;
import java.util.Arrays;
import java.util.HashSet;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import javax.media.jai.ImageLayout;
import javax.media.jai.UntiledOpImage;

import org.geotoolkit.image.color.ColorUtilities;
import org.geotoolkit.image.internal.ImageUtilities;

import static javax.media.jai.ImageLayout.COLOR_MODEL_MASK;
import static javax.media.jai.ImageLayout.SAMPLE_MODEL_MASK;


/**
 * Creates the silhouette of an image that isolates its content from its background.
 * The background color is assumed to be uniform, with a know value which must be
 * specified as a parameter to this operation.
 * <p>
 * This operation can be used in order to build a mask for an image that has been rotated,
 * where pixels in the middle of the image should not have their value masked even if they
 * happen to have the same color than the background color.
 * <p>
 * Current implementation is rather simple and works for images in which the content
 * is inside a rotated rectangle. More complex outline are not guaranteed to work.
 * Example:
 * <p>
 * <table align="center" cellpadding="15" border="1">
 * <tr><th>Input</th><th>output</th></tr><tr>
 * <td><img src="doc-files/sample-rgb.png" border="1"></td>
 * <td><img src="doc-files/silhouette.png" border="1"></td>
 * </tr></table>
 *
 * {@section Current algorithm}
 * This operator starts from a corner of the source image. If the pixel value in that corner is
 * equals to the specified background color, then the corresponding sample value in the target
 * image is set to -1 (for integer data type, this is equivalent to setting all bits to 1).
 * Otherwise the target sample value is set to 0. If and only if the pixel in the corner has
 * been set to -1, then its neighbors pixels are examined iteratively in the same way. This
 * operation is repeated for the 4 image corners.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.00
 * @module
 */
public class SilhouetteMask extends UntiledOpImage {
    /**
     * The name of this operation in the JAI registry.
     * This is {@value}.
     */
    public static final String OPERATION_NAME = "org.geotoolkit.SilhouetteMask";

    /**
     * The background values in the source image. The array length must matches
     * the number of bands, otherwise this operation can not work.
     */
    private final double[][] background;

    /**
     * Constructs a new silhouette mask for the given image. While this constructor is public,
     * it should usually not be invoked directly. You should use {@linkplain javax.media.jai.JAI}
     * factory methods instead.
     *
     * @param source        The source image.
     * @param layout        The image layout.
     * @param configuration The image properties and rendering hints.
     * @param background    The background values in the source image.
     */
    public SilhouetteMask(final RenderedImage source, final ImageLayout layout,
            final Map<?,?> configuration, double[]... background)
    {
        super(source, configuration, layout(source, layout));
        final int numBands = source.getSampleModel().getNumBands();
        this.background = background = background.clone();
        for (int i=0; i<background.length; i++) {
            background[i] = Arrays.copyOf(background[i], numBands);
        }
    }

    /**
     * If the user didn't specified explicitly a sample or a color model, creates default ones.
     * This method is actually a workaround for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     *
     * @param  layout The user-supplied layout.
     * @return A layout with at least a color model.
     */
    private static ImageLayout layout(final RenderedImage source, ImageLayout layout) {
        if (layout == null) {
            layout = new ImageLayout();
        } else if ((layout.getValidMask() & (SAMPLE_MODEL_MASK | COLOR_MODEL_MASK)) == 0) {
            layout = (ImageLayout) layout.clone();
        } else {
            return layout;
        }
        final ColorModel cm = ColorUtilities.BINARY_COLOR_MODEL;
        return layout.setColorModel(cm).setSampleModel(cm.createCompatibleSampleModel(
                layout.getWidth(source), layout.getHeight(source)));
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
        if (destRect.isEmpty()) return;
        final Raster source = sources[0];
        if (false) {
            /*
             * According javax.media.jai.TileFactory javadoc (see method createTile), new tiles
             * should be initialized to 0 even if they were recycled from an older image. So we
             * should not need to invoke this fill method.
             */
            ImageUtilities.fill(dest, destRect, 0);
        }
        final int xmin = destRect.x;
        final int ymin = destRect.y;
        final int xmax = destRect.width  + xmin;
        final int ymax = destRect.height + ymin;
        final int[] ones = new int[dest.getNumBands()];
        Arrays.fill(ones, -1);
        final int transferType = source.getTransferType();
        final Set<SampleValues> background = new HashSet<>();
        for (final double[] samples : this.background) {
            background.add(SampleValues.getInstance(transferType, samples));
        }
        final SampleValues buffer = SampleValues.getInstance(transferType, source.getSampleModel().getNumBands());
        assert source.getBounds().contains(destRect) : destRect;
        /*
         * For each background value found in the source image, sets the destination pixel to -1.
         * The same algorithm is repeated for the 4 corners, using only different iteration direction.
         */
        for (int corner=0; corner<4; corner++) {
            int x = source.getMinX();
            int y = source.getMinY();
            int w = source.getWidth();
            int h = source.getHeight();
            int dx = 1, dy = 1;
            if ((corner & 1) != 0) {x += w - 1; dx = -1;}
            if ((corner & 2) != 0) {y += h - 1; dy = -1;}
            final int x0 = x;
            /*
             * Next line will be scanned only if the first pixel of the previous line had
             * the background value. Next pixel on a row will be scanned only as long as
             * background values are found.
             */
            while (--h >= 0 && background.contains(buffer.getPixel(source, x, y))) {
                if (y >= ymin && y < ymax) {
                    w = destRect.width;
                    do if (x >= xmin && x < xmax) {
                        dest.setPixel(x, y, ones);
                        if (--w == 0) break;
                    } while (background.contains(buffer.getPixel(source, x += dx, y)));
                }
                y += dy;
                x = x0;
            }
        }
    }
}
