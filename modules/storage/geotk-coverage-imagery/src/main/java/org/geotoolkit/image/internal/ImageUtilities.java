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
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.media.jai.JAI;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import com.sun.media.jai.util.ImageUtil;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.Classes;

import static java.awt.image.DataBuffer.*;


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
     * Maximum tile width or height before to consider a tile as a stripe. It tile width or height
     * are smaller or equals than this size, then the image will be re-tiled. That is done because
     * there are many formats that use stripes as an alternative to tiles, an example is TIFF. A
     * stripe can be a performance issue, users can have stripes as large as 20000 columns x 8
     * rows. If we just want to see a chunk of 512x512, this is a lot of unneeded data to load.
     */
    private static final int STRIPE_SIZE = 64;

    /**
     * List of valid names. Note: the "Optimal" type is not
     * implemented because currently not provided by JAI.
     */
    private static final String[] INTERPOLATION_NAMES = {
        "Nearest",          // JAI name
        "NearestNeighbor",  // OpenGIS name
        "Bilinear",
        "Bicubic",
        "Bicubic2"          // Not in OpenGIS specification.
    };

    /**
     * Interpolation types (provided by Java Advanced Imaging) for {@link #INTERPOLATION_NAMES}.
     */
    private static final int[] INTERPOLATION_TYPES= {
        Interpolation.INTERP_NEAREST,
        Interpolation.INTERP_NEAREST,
        Interpolation.INTERP_BILINEAR,
        Interpolation.INTERP_BICUBIC,
        Interpolation.INTERP_BICUBIC_2
    };

    /**
     * Do not allow creation of instances of this class.
     */
    private ImageUtilities() {
    }

    /**
     * Suggests an {@link ImageLayout} for the specified image. All parameters are initially set
     * equal to those of the given {@link RenderedImage}, and then the {@linkplain #toTileSize
     * tile size is updated according the image size}. This method never returns {@code null}.
     *
     * @param  image The image for which to suggest a layout.
     * @return The suggested image layout.
     */
    public static ImageLayout getImageLayout(final RenderedImage image) {
        return getImageLayout(image, true);
    }

    /**
     * Returns an {@link ImageLayout} for the specified image. If {@code initToImage} is
     * {@code true}, then all parameters are initially set equal to those of the given
     * {@link RenderedImage} and the returned layout is never {@code null} (except if
     * the image is null).
     *
     * @param  image The image for which to suggest a layout.
     * @return The suggested image layout.
     */
    private static ImageLayout getImageLayout(final RenderedImage image, final boolean initToImage) {
        if (image == null) {
            return null;
        }
        ImageLayout layout = initToImage ? new ImageLayout(image) : null;
        if ((image.getNumXTiles() == 1 || image.getTileWidth () <= STRIPE_SIZE) &&
            (image.getNumYTiles() == 1 || image.getTileHeight() <= STRIPE_SIZE))
        {
            // If the image was already tiled, reuse the same tile size.
            // Otherwise, compute default tile size.  If a default tile
            // size can't be computed, it will be left unset.
            if (layout != null) {
                layout = layout.unsetTileLayout();
            }
            final Dimension tileSize = org.apache.sis.internal.coverage.j2d.ImageLayout.DEFAULT.suggestTileSize(image);
            int s;
            if ((s=tileSize.width) != image.getTileWidth()) {
                if (layout == null) {
                    layout = new ImageLayout();
                }
                layout = layout.setTileWidth(s);
                layout.setTileGridXOffset(image.getMinX());
            }
            if ((s=tileSize.height) != image.getTileHeight()) {
                if (layout == null) {
                    layout = new ImageLayout();
                }
                layout = layout.setTileHeight(s);
                layout.setTileGridYOffset(image.getMinY());
            }
        }
        return layout;
    }

    /**
     * Suggests a set of {@link RenderingHints} for the specified image.
     * The rendering hints may include the following parameters:
     * <p>
     * <ul>
     *   <li>{@link JAI#KEY_IMAGE_LAYOUT} with a proposed tile size.</li>
     * </ul>
     *
     * This method may returns {@code null} if no rendering hints is proposed.
     *
     * @param  image The image for which to suggest rendering hints.
     * @return The suggested rendering hints for the given image.
     */
    public static RenderingHints getRenderingHints(final RenderedImage image) {
        final ImageLayout layout = getImageLayout(image, false);
        return (layout != null) ? new RenderingHints(JAI.KEY_IMAGE_LAYOUT, layout) : null;
    }

    /**
     * Returns {@code true} if the given image is tiled.
     *
     * @param  image The image to test.
     * @return {@code true} if the given image is tiled.
     *
     * @since 3.20
     */
    public static boolean isTiled(final RenderedImage image) {
        return (image.getTileWidth()  < image.getWidth()) ||
               (image.getTileHeight() < image.getHeight());
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
                final Dimension tileSize = org.apache.sis.internal.coverage.j2d.ImageLayout.DEFAULT.suggestTileSize(source);
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
     * Casts the specified object to an {@link Interpolation object}.
     *
     * @param  type The interpolation type as an {@link Interpolation} or a {@link CharSequence} object.
     * @return The interpolation object for the specified type.
     * @throws IllegalArgumentException if the specified interpolation type is not a know one.
     */
    public static Interpolation toInterpolation(final Object type) throws IllegalArgumentException {
        if (type instanceof Interpolation) {
            return (Interpolation) type;
        } else if (type instanceof CharSequence) {
            final String name = type.toString();
            final int length = INTERPOLATION_NAMES.length;
            for (int i=0; i<length; i++) {
                if (INTERPOLATION_NAMES[i].equalsIgnoreCase(name)) {
                    return Interpolation.getInstance(INTERPOLATION_TYPES[i]);
                }
            }
        }
        throw new IllegalArgumentException(Errors.format(Errors.Keys.UnknownInterpolation_1, type));
    }

    /**
     * Returns the interpolation name for the specified interpolation object.
     * This method tries to infer the name from the object's class name.
     *
     * @param  interp The interpolation object, or {@code null} for "nearest"
     *         (which is an other way to say "no interpolation").
     * @return The interpolation name.
     */
    public static String getInterpolationName(Interpolation interp) {
        if (interp == null) {
            interp = Interpolation.getInstance(Interpolation.INTERP_NEAREST);
        }
        final String prefix = "Interpolation";
        for (Class<?> classe = interp.getClass(); classe!=null; classe=classe.getSuperclass()) {
            String name = Classes.getShortName(classe);
            int index = name.lastIndexOf(prefix);
            if (index >= 0) {
                return name.substring(index + prefix.length());
            }
        }
        return Classes.getShortClassName(interp);
    }

    /**
     * Returns {@code true} if the given type is {@link DataBuffer#TYPE_FLOAT TYPE_FLOAT}
     * or {@link DataBuffer#TYPE_DOUBLE TYPE_DOUBLE}.
     *
     * @param  type The type to test.
     * @return {@code true} if the given type is one of floating point types.
     */
    public static boolean isFloatType(final int type) {
        return (type == TYPE_FLOAT) || (type == TYPE_DOUBLE);
    }

    /**
     * Sets every samples in the given image to the given value. This method is typically used
     * for clearing an image content.
     *
     * @param image The image to fill.
     * @param value The value to be given to every samples.
     */
    public static void fill(final WritableRenderedImage image, final Number value) {
        int y = image.getMinTileY();
        for (int ny = image.getNumYTiles(); --ny >= 0; y++) {
            int x = image.getMinTileX();
            for (int nx = image.getNumXTiles(); --nx >= 0; x++) {
                final WritableRaster raster = image.getWritableTile(x, y);
                try {
                    fill(raster.getDataBuffer(), value);
                } finally {
                    image.releaseWritableTile(x, y);
                }
            }
        }
    }

    /**
     * Sets every samples in the given region of the given raster to the given value.
     *
     * @param raster The raster where to set the sample values.
     * @param region The region in the given rectangle where the values should be set.
     * @param value  The value to be given to every samples that are inside the given region.
     */
    public static void fill(final WritableRaster raster, Rectangle region, final Number value) {
        final Rectangle bounds = raster.getBounds();
        if (region.contains(bounds)) {
            fill(raster.getDataBuffer(), value);
        } else {
            region = region.intersection(bounds);
            final double[] background = new double[raster.getNumBands()];
            Arrays.fill(background, value.doubleValue());
            ImageUtil.fillBackground(raster, region, background);
        }
    }

    /**
     * Sets the content of all banks in the given data buffer to the specified value. We do not
     * allow setting of different value for individual bank because the data buffer "banks" do
     * not necessarily match the image "bands".
     * <p>
     * We do not provide version for setting only a portion of the data buffer in order to
     * avoid the complexity of considering the number of bits per pixel, the pixel stride
     * and the line stride - the risk of bug would be too high, we are better to stick to
     * the Java API for that.
     *
     * @param buffer The data buffer to fill.
     * @param value  The values to be given to every elements in the data buffer.
     */
    private static void fill(final DataBuffer buffer, final Number value) {
        final int[] offsets = buffer.getOffsets();
        final int size = buffer.getSize();
        if (buffer instanceof DataBufferByte) {
            final DataBufferByte data = (DataBufferByte) buffer;
            final byte n = value.byteValue();
            for (int i=0; i<offsets.length; i++) {
                final int offset = offsets[i];
                Arrays.fill(data.getData(i), offset, offset + size, n);
            }
        } else if (buffer instanceof DataBufferShort) {
            final DataBufferShort data = (DataBufferShort) buffer;
            final short n = value.shortValue();
            for (int i=0; i<offsets.length; i++) {
                final int offset = offsets[i];
                Arrays.fill(data.getData(i), offset, offset + size, n);
            }
        } else if (buffer instanceof DataBufferUShort) {
            final DataBufferUShort data = (DataBufferUShort) buffer;
            final short n = value.shortValue();
            for (int i=0; i<offsets.length; i++) {
                final int offset = offsets[i];
                Arrays.fill(data.getData(i), offset, offset + size, n);
            }
        } else if (buffer instanceof DataBufferInt) {
            final DataBufferInt data = (DataBufferInt) buffer;
            final int n = value.intValue();
            for (int i=0; i<offsets.length; i++) {
                final int offset = offsets[i];
                Arrays.fill(data.getData(i), offset, offset + size, n);
            }
        } else if (buffer instanceof DataBufferFloat) {
            final DataBufferFloat data = (DataBufferFloat) buffer;
            final float n = value.floatValue();
            for (int i=0; i<offsets.length; i++) {
                final int offset = offsets[i];
                Arrays.fill(data.getData(i), offset, offset + size, n);
            }
        } else if (buffer instanceof DataBufferDouble) {
            final DataBufferDouble data = (DataBufferDouble) buffer;
            final double n = value.doubleValue();
            for (int i=0; i<offsets.length; i++) {
                final int offset = offsets[i];
                Arrays.fill(data.getData(i), offset, offset + size, n);
            }
        } else {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.UnsupportedDataType));
        }
    }
}
