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
import java.awt.color.ColorSpace;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.NullOpImage;
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
 * @version 3.20
 *
 * @since 1.2
 * @module
 */
public final class ImageUtilities extends Static {
    /**
     * The default tile size. This default tile size can be
     * overridden with a call to {@link JAI#setDefaultTileSize}.
     */
    private static final Dimension DEFAULT_TILE_SIZE = new Dimension(512, 512);

    /**
     * The minimum tile size.
     */
    public static final int MIN_TILE_SIZE = 256;

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
     * Returns the bounds of the given image as a new rectangle. Note that if the image is actually
     * and instance of {@link PlanarImage} and the caller will not modify the rectangle values,
     * then {@link PlanarImage#getBounds()} can be used instead.
     *
     * @param  image The image for which to get the bounds.
     * @return The bounds of the given image.
     *
     * @see Raster#getBounds()
     * @see PlanarImage#getBounds()
     */
    public static Rectangle getBounds(final RenderedImage image) {
        return new Rectangle(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight());
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
            Dimension defaultSize = JAI.getDefaultTileSize();
            if (defaultSize == null) {
                defaultSize = DEFAULT_TILE_SIZE;
            }
            int s;
            if ((s=toTileSize(image.getWidth(), defaultSize.width)) != 0) {
                if (layout == null) {
                    layout = new ImageLayout();
                }
                layout = layout.setTileWidth(s);
                layout.setTileGridXOffset(image.getMinX());
            }
            if ((s=toTileSize(image.getHeight(), defaultSize.height)) != 0) {
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
     * Suggests a tile size for the specified image size. On input, {@code size} is the image
     * size. On output, it is the tile size. This method write the result directly in the supplied
     * object and returns {@code size} for convenience.
     * <p>
     * This method it aimed to computing a tile size such that the tile grid would have overlapped
     * the image bound in order to avoid having tiles crossing the image bounds and being therefore
     * partially empty. This method will never returns a tile size smaller than
     * {@value #MIN_TILE_SIZE}. If this method can't suggest a size, then it left the
     * corresponding {@code size} field ({@link Dimension#width width} or
     * {@link Dimension#height height}) unchanged.
     * <p>
     * The {@link Dimension#width width} and {@link Dimension#height height} fields are processed
     * independently in the same way. The following discussion use the {@code width} field as an
     * example.
     * <p>
     * This method inspects different tile sizes close to the {@linkplain JAI#getDefaultTileSize()
     * default tile size}. Let {@code width} be the default tile width. Values are tried in the
     * following order: {@code width}, {@code width+1}, {@code width-1}, {@code width+2},
     * {@code width-2}, {@code width+3}, {@code width-3}, <i>etc.</i> until one of the
     * following happen:
     * <p>
     * <ul>
     *   <li>A suitable tile size is found. More specifically, a size is found which is a divisor
     *       of the specified image size, and is the closest one of the default tile size. The
     *       {@link Dimension} field ({@code width} or {@code height}) is set to this value.</li>
     *
     *   <li>An arbitrary limit (both a minimum and a maximum tile size) is reached. In this case,
     *       this method <strong>may</strong> set the {@link Dimension} field to a value that
     *       maximize the remainder of <var>image size</var> / <var>tile size</var> (in other
     *       words, the size that left as few empty pixels as possible).</li>
     * </ul>
     *
     * @param size The image size.
     * @return Suggested tile size for the given image size.
     */
    public static Dimension toTileSize(final Dimension size) {
        Dimension defaultSize = JAI.getDefaultTileSize();
        if (defaultSize == null) {
            defaultSize = DEFAULT_TILE_SIZE;
        }
        int s;
        if ((s=toTileSize(size.width,  defaultSize.width )) != 0) size.width  = s;
        if ((s=toTileSize(size.height, defaultSize.height)) != 0) size.height = s;
        return size;
    }

    /**
     * Suggests a tile size close to {@code tileSize} for the specified {@code imageSize}.
     * This method it aimed to computing a tile size such that the tile grid would have
     * overlapped the image bound in order to avoid having tiles crossing the image bounds
     * and being therefore partially empty. This method will never returns a tile size smaller
     * than {@value #MIN_TILE_SIZE}. If this method can't suggest a size, then it returns 0.
     *
     * @param imageSize The image size.
     * @param tileSize  The preferred tile size, which is often {@value #DEFAULT_TILE_SIZE}.
     */
    private static int toTileSize(final int imageSize, final int tileSize) {
        final int MAX_TILE_SIZE = Math.min(tileSize*2, imageSize);
        final int stop = Math.max(tileSize-MIN_TILE_SIZE, MAX_TILE_SIZE-tileSize);
        int sopt = 0;  // An "optimal" tile size, to be used if no exact dividor is found.
        int rmax = 0;  // The remainder of 'imageSize / sopt'. We will try to maximize this value.
        /*
         * Inspects all tile sizes in the range [GEOTOOLKIT_MIN_TILE_SIZE .. MAX_TIME_SIZE]. We will begin
         * with a tile size equal to the specified 'tileSize'. Next we will try tile sizes of
         * 'tileSize+1', 'tileSize-1', 'tileSize+2', 'tileSize-2', 'tileSize+3', 'tileSize-3',
         * etc. until a tile size if found suitable.
         *
         * More generally, the loop below tests the 'tileSize+i' and 'tileSize-i' values. The
         * 'stop' constant was computed assuming that MIN_TIME_SIZE < tileSize < MAX_TILE_SIZE.
         * If a tile size is found which is a dividor of the image size, than that tile size (the
         * closest one to 'tileSize') is returned. Otherwise, the loop continue until all values
         * in the range [GEOTOOLKIT_MIN_TILE_SIZE .. MAX_TIME_SIZE] were tested. In this process, we
         * remind the tile size that gave the greatest reminder (rmax). In other words, this is the
         * tile size with the smallest amount of empty pixels.
         */
        for (int i=0; i<=stop; i++) {
            int s;
            if ((s = tileSize+i) <= MAX_TILE_SIZE) {
                final int r = imageSize % s;
                if (r == 0) {
                    // Found a size >= to 'tileSize' which is a dividor of image size.
                    return s;
                }
                if (r > rmax) {
                    rmax = r;
                    sopt = s;
                }
            }
            if ((s = tileSize-i) >= MIN_TILE_SIZE) {
                final int r = imageSize % s;
                if (r == 0) {
                    // Found a size <= to 'tileSize' which is a dividor of image size.
                    return s;
                }
                if (r > rmax) {
                    rmax = r;
                    sopt = s;
                }
            }
        }
        /*
         * No dividor were found in the range [GEOTOOLKIT_MIN_TILE_SIZE .. MAX_TIME_SIZE]. At this point
         * 'sopt' is an "optimal" tile size (the one that left as few empty pixel as possible),
         * and 'rmax' is the amount of non-empty pixels using this tile size. We will use this
         * "optimal" tile size only if it fill at least 75% of the tile. Otherwise, we arbitrarily
         * consider that it doesn't worth to use a "non-standard" tile size. The purpose of this
         * arbitrary test is again to avoid too many small tiles (assuming that
         */
        return (rmax >= tileSize - tileSize/4) ? sopt : 0;
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
                if (result.isValid(ImageLayout.TILE_WIDTH_MASK)) {
                    final int oldSize = result.getTileWidth(source);
                    final int newSize = toTileSize(result.getWidth(source), oldSize);
                    if (oldSize != newSize) {
                        result.setTileWidth(newSize);
                    }
                }
                if (result.isValid(ImageLayout.TILE_HEIGHT_MASK)) {
                    final int oldSize = result.getTileHeight(source);
                    final int newSize = toTileSize(result.getHeight(source), oldSize);
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
     * Returns a data type capable to hold the values of the two given data types.
     *
     * @param  t1 The first data type.
     * @param  t2 The second data type.
     * @return A data type capable to hold the values of the two given data types.
     */
    public static int typeForBoth(final int t1, final int t2) {
        final int min = Math.min(t1, t2);
        final int max = Math.max(t1, t2);
        if (min == max) {
            return max;
        }
        if (min >= TYPE_BYTE && max <= TYPE_DOUBLE) {
            return (min == TYPE_USHORT && max == TYPE_SHORT) ? TYPE_INT : max;
        }
        return TYPE_UNDEFINED;
    }

    /**
     * Suggests the smallest type capable to hold the given range of values.
     *
     * @param  minimum The minimal value to hold, inclusive.
     * @param  maximum The maximal value to hold, <strong>inclusive</strong>.
     * @return The data type, or {@link DataBuffer#TYPE_UNDEFINED} if the given
     *         range is invalid, contains NaN or infinity values.
     */
    public static int typeForRange(final double minimum, final double maximum) {
        if (maximum >= minimum) {
            for (int type=TYPE_BYTE; type<=TYPE_DOUBLE; type++) {
                if (minimum >= minimum(type) && maximum <= maximum(type)) {
                    return type;
                }
            }
        }
        return TYPE_UNDEFINED;
    }

    /**
     * Returns the minimum allowed value for a certain data type. This method does not returns
     * negative infinity for float and double values, despite that they are valid values.
     *
     * {@section Note on floating point types}
     * If the given type is {@link DataBuffer#TYPE_FLOAT TYPE_FLOAT} or {@link DataBuffer#TYPE_DOUBLE
     * TYPE_DOUBLE} (you can use {@link #isFloatType} for checking that), then there is some chances
     * that what you really want is the minimal <cite>normalized value</cite>. In such case, you
     * should invoke {@link ColorSpace#getMinValue} instead. This doesn't apply to the alpha channel,
     * where the minimal value is always 0 (fully transparent).
     *
     * @param  dataType The data type to suggest a minimum value for.
     * @return The minimum value for the given data type.
     */
    public static double minimum(final int dataType) {
        switch (dataType) {
            case TYPE_BYTE:   // Fall through
            case TYPE_USHORT: return  0;
            case TYPE_SHORT:  return  Short  .MIN_VALUE;
            case TYPE_INT:    return  Integer.MIN_VALUE;
            case TYPE_FLOAT:  return -Float  .MAX_VALUE;
            case TYPE_DOUBLE: return -Double .MAX_VALUE;
            default: throw new IllegalArgumentException(String.valueOf(dataType));
        }
    }

    /**
     * Returns the maximum allowed value for a certain data type. This method does not returns
     * positive infinity for float and double values, despite that they are valid values.
     *
     * {@section Note on floating point types}
     * If the given type is {@link DataBuffer#TYPE_FLOAT TYPE_FLOAT} or {@link DataBuffer#TYPE_DOUBLE
     * TYPE_DOUBLE} (you can use {@link #isFloatType} for checking that), then there is some chances
     * that what you really want is the maximal <cite>normalized value</cite>. In such case, you
     * should invoke {@link ColorSpace#getMaxValue} instead. This doesn't apply to the alpha channel,
     * where the maximal value is always 1 (fully opaque).
     *
     * @param  dataType The data type to suggest a maximum value for.
     * @return The maximum value for the given data type.
     */
    public static double maximum(final int dataType) {
        switch (dataType) {
            case TYPE_BYTE:   return 0xFF;
            case TYPE_USHORT: return 0xFFFF;
            case TYPE_SHORT:  return Short  .MAX_VALUE;
            case TYPE_INT:    return Integer.MAX_VALUE;
            case TYPE_FLOAT:  return Float  .MAX_VALUE;
            case TYPE_DOUBLE: return Double .MAX_VALUE;
            default: throw new IllegalArgumentException(String.valueOf(dataType));
        }
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

    /**
     * Replaces the color model in the given image by the given one.
     * The sample values are transfered with no change. This method
     * is <strong>not</strong> suitable for anything that change the
     * pixel layout, the number of bands, etc.
     *
     * @param  image The image in which to change the color model.
     * @param  cm The new color model.
     * @return An image with the new color model.
     */
    public static RenderedImage replaceColorModel(RenderedImage image, final ColorModel cm) {
        if (image instanceof BufferedImage) {
            final BufferedImage b = (BufferedImage) image;
            image = new BufferedImage(cm, b.getRaster(), b.isAlphaPremultiplied(), null);
        } else {
            final ImageLayout layout = new ImageLayout();
            layout.setColorModel(cm);
            image = new NullOpImage(image, layout, null, NullOpImage.OP_COMPUTE_BOUND);
        }
        return image;
    }
}
