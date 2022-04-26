/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.internal;

import java.util.List;
import java.util.Arrays;

import java.awt.image.*;
import java.awt.Dimension;
import javax.media.jai.ImageLayout;

import org.geotoolkit.lang.Static;
import org.apache.sis.internal.coverage.j2d.FillValues;



/**
 * A set of static methods working on images. Some of those methods are useful, but not
 * really rigorous. This is why they do not appear in any "official" package, but instead
 * in this private one.
 *
 *                      <strong>Do not rely on this API!</strong>
 *
 * It may change in incompatible way in any future version.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Simone Giannecchini (Geosolutions)
 *
 * @since 1.2
 * @module
 */
public final class ImageUtilities extends Static {
    /**
     * Do not allow creation of instances of this class.
     */
    private ImageUtilities() {
    }

    /**
     * Computes a new {@link ImageLayout} which is the intersection of the specified
     * {@code ImageLayout} and all {@code RenderedImage}s in the supplied list. If the
     * {@link ImageLayout#getMinX minX}, {@link ImageLayout#getMinY minY},
     * {@link ImageLayout#getWidth width} and {@link ImageLayout#getHeight height}
     * properties are not defined in the {@code layout}, then they will be inherited
     * from the <strong>first</strong> source for consistency with {@link javax.media.jai.OpImage}
     * constructor.
     *
     * @param  layout The original layout. This object will not be modified.
     * @param  sources The list of sources {@link RenderedImage}.
     * @return A new {@code ImageLayout}, or the original {@code layout} if no change was needed.
     */
    public static ImageLayout createIntersection(final ImageLayout layout,
            final List<? extends RenderedImage> sources)
    {
        ImageLayout result = layout;
        if (result == null) {
            result = new ImageLayout();
        }
        final int n = sources.size();
        if (n != 0) {
            // If layout is not set, OpImage uses the layout of the *first*
            // source image according OpImage constructor javadoc.
            RenderedImage source = sources.get(0);
            int minXL = result.getMinX  (source);
            int minYL = result.getMinY  (source);
            int maxXL = result.getWidth (source) + minXL;
            int maxYL = result.getHeight(source) + minYL;
            for (int i=0; i<n; i++) {
                source = sources.get(i);
                final int minX = source.getMinX  ();
                final int minY = source.getMinY  ();
                final int maxX = source.getWidth () + minX;
                final int maxY = source.getHeight() + minY;
                int mask = 0;
                if (minXL < minX) mask |= (1|4); // set minX and width
                if (minYL < minY) mask |= (2|8); // set minY and height
                if (maxXL > maxX) mask |= (4);   // Set width
                if (maxYL > maxY) mask |= (8);   // Set height
                if (mask != 0) {
                    if (layout == result) {
                        result = (ImageLayout) layout.clone();
                    }
                    if ((mask & 1) != 0) result.setMinX   (minXL=minX);
                    if ((mask & 2) != 0) result.setMinY   (minYL=minY);
                    if ((mask & 4) != 0) result.setWidth ((maxXL=maxX) - minXL);
                    if ((mask & 8) != 0) result.setHeight((maxYL=maxY) - minYL);
                }
            }
            // If the bounds changed, adjust the tile size.
            if (result != layout) {
                source = sources.get(0);
                final Dimension tileSize = org.apache.sis.internal.coverage.j2d.ImageLayout.DEFAULT.suggestTileSize(source, null, false);
                if (result.isValid(ImageLayout.TILE_WIDTH_MASK)) {
                    final int oldSize = result.getTileWidth(source);
                    final int newSize = tileSize.width;
                    if (oldSize != newSize) {
                        result.setTileWidth(newSize);
                    }
                }
                if (result.isValid(ImageLayout.TILE_HEIGHT_MASK)) {
                    final int oldSize = result.getTileHeight(source);
                    final int newSize = tileSize.height;
                    if (oldSize != newSize) {
                        result.setTileHeight(newSize);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Sets every samples in the given image to the given value. This method is typically used
     * for clearing an image content.
     *
     * @param image The image to fill (modified in-place).
     * @param value The value to be given to every samples.
     * @see FillValues used pixel filling engine.
     */
    public static void fill(final WritableRenderedImage image, final Number value) {
        final SampleModel sampleModel = image.getSampleModel();
        final int numBands = sampleModel.getNumBands();
        final Number[] fillValues = new Number[numBands];
        Arrays.fill(fillValues, value);
        final FillValues filler = new FillValues(sampleModel, fillValues, true);
        int y = image.getMinTileY();
        for (int ny = image.getNumYTiles(); --ny >= 0; y++) {
            int x = image.getMinTileX();
            for (int nx = image.getNumXTiles(); --nx >= 0; x++) {
                final WritableRaster raster = image.getWritableTile(x, y);
                try {
                    filler.fill(raster);
                } finally {
                    image.releaseWritableTile(x, y);
                }
            }
        }
    }

    /**
     * @deprecated Use Apache SIS utilities instead.
     * @see FillValues#fill(WritableRaster)
     */
    @Deprecated
    public static void fill(final WritableRaster raster, final Number value) {
        new FillValues(raster.getSampleModel(), new Number[]{ value }, true).fill(raster);
    }
}
