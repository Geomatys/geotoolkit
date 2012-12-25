/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image;

import java.util.Arrays;
import java.awt.image.*;
import java.awt.Transparency;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;

import javax.media.jai.*;
import javax.media.jai.operator.*;

import org.opengis.coverage.PaletteInterpretation;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.image.jai.Mask;
import org.geotoolkit.image.jai.SilhouetteMask;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.internal.image.ColorModels;
import org.geotoolkit.internal.image.LookupTables;
import org.geotoolkit.internal.image.ColorUtilities;
import org.geotoolkit.internal.image.ImageUtilities;

import static java.awt.color.ColorSpace.CS_GRAY;
import static java.awt.color.ColorSpace.CS_sRGB;
import static java.awt.image.DataBuffer.TYPE_BYTE;
import static org.apache.sis.util.ArgumentChecks.*;


/**
 * Helper methods for applying JAI operations on an image. The image is specified at
 * {@linkplain #ImageWorker(RenderedImage) creation time}. Successive operations can
 * be applied by invoking the methods defined in this class, and the final image can
 * be obtained by invoking {@link #getRenderedImage} at the end of the process.
 * <p>
 * This class does not really brings new functionalities, since most of its work is performed by
 * chains of JAI image operations. However it makes the job easier by performing automatically
 * some intermediate steps based on assumptions about what the common usage is. For example some
 * methods like {@link #intensity()} may convert the image to the {@linkplain ColorSpace#CS_sRGB
 * RGB color space} before to do their work. {@linkplain ColorQuantization Color Quantization}
 * uses the {@link ColorCube#BYTE_496 BYTE_496} color cube, which is specific to the RGB color
 * space. Methods dealing with transparency assume that the alpha channel, if presents, is the
 * last band.
 * <p>
 * Developers who know exactly the characteristics of their image should probably use JAI
 * operations directly. Developers writing prototypes, or developers who don't know much
 * more about their images than what this {@code ImageWorker} assumes, can use this class
 * as a convenience.
 * <p>
 * If an exception is thrown during a method invocation, then this {@code ImageWorker}
 * is left in an undetermined state and should not be used anymore.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Simone Giannecchini (Geosolutions)
 * @author Bryce Nordgren
 * @version 3.01
 *
 * @since 2.3
 * @module
 *
 * @deprecated This class is a legacy from old days and has never been seriously used in the
 *             Geotoolkit.org library. Its work is quite arbitrary, so we are probably better
 *             to let users do their work in their own way.
 */
@Deprecated
public class ImageWorker extends ImageInspector {
    /**
     * The {@linkplain ColorQuantization Color Quantization} method to be applied if a an image
     * needs to have its color model converted to an {@link IndexColorModel}. The default value
     * is {@link ColorQuantization#ERROR_DIFFUSION ERROR_DIFFUSION}.
     *
     * @see #setColorModelType
     * @see #setRenderingHint
     */
    public static final Hints.Key COLOR_QUANTIZATION = new Hints.Key(ColorQuantization.class);

    /**
     * If {@link Boolean#FALSE FALSE}, image operators are not allowed to produce tiled images.
     * The default is value {@link Boolean#TRUE TRUE}.
     *
     * @see #setRenderingHint
     */
    public static final Hints.Key TILING_ALLOWED = new Hints.Key(Boolean.class);

    /**
     * Creates a new worker for the specified image. The images to be computed (if any)
     * will save their tiles in the default {@linkplain TileCache tile cache}.
     *
     * @param image The source image.
     */
    public ImageWorker(final RenderedImage image) {
        super(image);
    }

    /**
     * Creates a new image worker initialized to the same image and hints than the given descriptor.
     */
    ImageWorker(final ImageInspector base) {
        super(base);
    }

    /**
     * If the {@linkplain #image image} was not already tiled, tiles it. Note that no tiling will
     * be done if {@link #getRenderingHints() getRenderingHints()} failed to suggest a tile size.
     *
     * see #isTiled
     */
    public void tile() {
        if (!isTiled()) {
            final RenderingHints hints = getRenderingHints();
            final ImageLayout layout = getImageLayout(hints);
            if (layout.isValid(ImageLayout.TILE_WIDTH_MASK) ||
                layout.isValid(ImageLayout.TILE_HEIGHT_MASK))
            {
                final int type = image.getSampleModel().getDataType();
                image = FormatDescriptor.create(image, type, hints);
            }
        }
    }

    /**
     * Formats the {@linkplain #image image} to the provided data type. If the image already
     * stores its sample values in a data buffer of the given type, then this method does nothing.
     * Otherwise the behavior depends on the value of the {@code rescale} argument:
     *
     * <ul>
     *   <li><p>If {@code false}, then this method casts the sample values to the given
     *          type - values are not changed except as a result of the cast.</p></li>
     *   <li><p>If {@code true}, then this method rescales the sample values in order to make
     *          them fit in the range supported by the given data type. It does so by computing
     *          the {@linkplain #getMinimums minimum} and {@linkplain #getMaximums maximum} values
     *          for each band, {@linkplain RescaleDescriptor rescale} them to the range of the
     *          given type and format the resulting image to of that type.</p></li>
     * </ul>
     *
     * This method is often used for rescaling to bytes in the range [0 &hellip; 255],
     * which can be done with {@link DataBuffer#TYPE_BYTE} as the parameter value.
     *
     * @param type The target type as one of the {@code TYPE_*} constant defined in
     *        {@link DataBuffer}.
     * @param rescale {@code true} for rescaling the sample values to the range supported by
     *        the given type, or {@code false} for just casting them.
     *
     * @see #isBytes
     * @see RescaleDescriptor
     */
    public void format(final int type, final boolean rescale) {
        final int currentType = image.getSampleModel().getDataType();
        if (type == currentType) {
            // Already using the requested range - nothing to do.
            return;
        }
        if (!rescale || ImageUtilities.isFloatType(type)) {
            /*
             * If the target data type is floating point, do not apply any rescale.
             * Invalidates the statistics only if we casted to a smallest type.
             */
            image = FormatDescriptor.create(image, type, getRenderingHints());
            if (ImageUtilities.typeForBoth(type, currentType) != currentType) {
                invalidateStatistics();
            }
        } else {
            /*
             * Otherwise, applies a rescale operation.
             */
            double minimum = ImageUtilities.minimum(type);
            double maximum = ImageUtilities.maximum(type);
            if (minimum == 0 && ImageUtilities.isFloatType(currentType)) {
                /*
                 * If converting from a floating point type to an unsigned integer type,
                 * set the minimum to 1 in order to reserve the value 0 for NaN values.
                 */
                minimum = 1;
            }
            final double[][] extrema = getExtremas();
            final int length = extrema[0].length;
            final double[] scales  = new double[length];
            final double[] offsets = new double[length];
            for (int i=0; i<length; i++) {
                final double cmin  = extrema[0][i];
                final double cmax  = extrema[1][i];
                final double scale = (maximum - minimum) / (cmax - cmin);
                scales [i] = scale;
                offsets[i] = minimum - scale * cmin;
            }
            final RenderingHints hints = getRenderingHints(type);
            image = RescaleDescriptor.create(
                    image,      // The source image.
                    scales,     // The per-band constants to multiply by.
                    offsets,    // The per-band offsets to be added.
                    hints);     // The rendering hints.
            invalidateStatistics(); // Extremas are no longer valids.
        }
        // Post conditions for this method contract.
        assert image.getSampleModel().getDataType() == type;
    }

    /**
     * Reduces the color model to {@link IndexColorModel}. If the current {@linkplain #image image}
     * already uses an {@code IndexColorModel}, then this method does nothing. Otherwise this method
     * performs an Error Diffusion or an Ordered Dither operation according the value of the
     * {@link #COLOR_QUANTIZATION} rendering hint. If this hint is not provided, then the selected
     * method is implementation-dependent and may vary in future versions.
     * <p>
     * The current implementation performs its work on the RGB color space only.
     *
     * @see #isIndexed
     * @see #COLOR_QUANTIZATION
     * @see ErrorDiffusionDescriptor
     * @see OrderedDitherDescriptor
     */
    private void forceIndexColorModel() {
        if (image.getColorModel() instanceof IndexColorModel) {
            // Already an index color model - nothing to do.
            return;
        }
        enableTileCache(false);
        setColorSpaceType(PaletteInterpretation.RGB);
        if (image.getColorModel().hasAlpha()) {
            // Discarts the alpha band, which is assumed the last one.
            retainBands(0, -2);
        }
        enableTileCache(true);
        final RenderingHints hints = getRenderingHints();
        ColorQuantization method = (ColorQuantization) hints.get(COLOR_QUANTIZATION);
        if (method == null) {
            method = ColorQuantization.ERROR_DIFFUSION; // Default value.
        }
        final ColorCube colorMap = ColorCube.BYTE_496; // Assumes RGB color space.
        switch (method) {
            case ERROR_DIFFUSION: {
                final KernelJAI ditherMask = KernelJAI.ERROR_FILTER_FLOYD_STEINBERG;
                image = ErrorDiffusionDescriptor.create(image, colorMap, ditherMask, hints);
                break;
            }
            case ORDERED_DITHER: {
                final KernelJAI[] ditherMask = KernelJAI.DITHER_MASK_443;
                image = OrderedDitherDescriptor.create(image, colorMap, ditherMask, hints);
                break;
            }
            default: {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.ILLEGAL_ARGUMENT_$2, "method", method));
            }
        }
        invalidateStatistics();

        // Post conditions for this method contract.
        assert isIndexed();
    }

    /**
     * Reduces the color model to {@link IndexColorModel} with {@linkplain Transparency#BITMASK
     * bitmask} transparency. If the current {@linkplain #image image} already uses a suitable
     * color model, then this method does nothing.
     *
     * @param transparent A pixel value to define as the transparent pixel, or -1 for the default.
     *        The default is to reuse the existing {@linkplain #getTransparentPixel() transparent
     *        pixel} if there is one, or to use the one with the smallest alpha value otherwise,
     *        or 0 if all colors are opaque.
     *
     * @see #isIndexed
     * @see #isTranslucent
     * @see #COLOR_QUANTIZATION
     */
    public void forceBitmaskIndexColorModel(int transparent) {
        final ColorModel cm = image.getColorModel();
        if (cm instanceof IndexColorModel) {
            final IndexColorModel oldCM = (IndexColorModel) cm;
            if (transparent < 0) {
                transparent = oldCM.getTransparentPixel();
            }
            if (oldCM.getTransparency() == Transparency.BITMASK) {
                if (oldCM.getTransparentPixel() == transparent) {
                    // Suitable color model. There is nothing to do.
                    return;
                }
            }
            if (transparent < 0) {
                int min = 256;
                final int mapSize = oldCM.getMapSize();
                for (int i=0; i<mapSize; i++) {
                    final int alpha = oldCM.getAlpha(i);
                    if (alpha < min) {
                        min = alpha;
                        transparent = i;
                    }
                }
            }
            /*
             * The Index Color Model needs to be replaced. Creates a lookup table mapping from the
             * old pixel values to new pixels values, with transparent colors mapped to the new
             * transparent pixel value. The lookup table uses TYPE_BYTE or TYPE_USHORT, which are
             * the two only types supported by IndexColorModel.
             */
            final int pixelSize = oldCM.getPixelSize();
            transparent &= (1 << pixelSize) - 1;
            final int mapSize = oldCM.getMapSize();
            final int newSize = Math.max(mapSize, transparent + 1);
            final boolean wide = (newSize > 256);
            final Object data = wide ? new short[mapSize] : new byte[mapSize];
            boolean changed = false;
            for (int i=0; i<mapSize; i++) {
                final int ni = (oldCM.getAlpha(i) == 0) ? transparent : i;
                if (wide) ((short[]) data)[i] = (short) ni;
                else      ((byte []) data)[i] = (byte)  ni;
                changed |= (ni != i);
            }
            /*
             * Now we need to perform the lookup transformation. First we create the new color
             * model with a bitmask transparency using the transparency index specified to this
             * method. Then we perform the lookup operation.
             */
            final int[] RGB = new int[newSize];
            oldCM.getRGBs(RGB);
            final IndexColorModel newCM = ColorModels.unique(new IndexColorModel(pixelSize, newSize,
                    RGB, 0, false, transparent, ColorUtilities.getTransferType(newSize)));
            if (!changed) {
                // Special case if the lookup don't do anything. Just replace the color model.
                image = ImageUtilities.replaceColorModel(image, newCM);
                return; // For preventing the call to invalidateStatistics().
            } else {
                final LookupTableJAI lookupTable = wide ?
                        new LookupTableJAI((short[]) data, true) :
                        new LookupTableJAI((byte []) data);
                final RenderingHints hints = getRenderingHints();
                setColorModel(hints, newCM);
                hints.put(JAI.KEY_REPLACE_INDEX_COLOR_MODEL, Boolean.FALSE);
                image = LookupDescriptor.create(image, lookupTable, hints);
            }
        } else {
            /*
             * The image is not indexed. Gets the alpha channel, assuming that it is the last
             * channel. This is always the case when using the standard ComponentColorModel.
             */
            RenderedImage alphaChannel = null;
            if (cm.hasAlpha()) {
                final ImageWorker fork = new ImageWorker(this);
                fork.enableTileCache(false);
                fork.retainBands(-1, -1);
                fork.binarize(false);
                fork.xor(new int[] {-1});
                alphaChannel = fork.image;
            }
            /*
             * Forces to index color model, loosing the alpha channel here. Note that the color
             * quantization uses a ColorCube with some offset (38 in the case of BYTE_496), which
             * means that index 0 is available for making it transparent.
             */
            enableTileCache(false);
            forceIndexColorModel();
            enableTileCache(true);
            if (transparent < 0) {
                transparent = 0;
            }
            IndexColorModel icm = (IndexColorModel) image.getColorModel();
            if (icm.getAlpha(transparent) != 0) {
                final int[] ARGB = new int[icm.getMapSize()];
                icm.getRGBs(ARGB);
                icm = new IndexColorModel(icm.getPixelSize(), ARGB.length, ARGB, 0,
                        icm.hasAlpha(), transparent, icm.getTransferType());
            }
            final RenderingHints hints = getRenderingHints();
            setColorModel(hints, icm);
            if (alphaChannel != null) {
                // Uses the alpha channel as a mask for replacing pixels by the transparent value.
                image = JAI.create(Mask.OPERATION_NAME, new ParameterBlockJAI(Mask.OPERATION_NAME)
                        .addSource(image).addSource(alphaChannel).set(new double[] {transparent}, 0), hints);
            } else {
                // Replaces only the color model.
                image = NullDescriptor.create(image, hints);
            }
        }
        invalidateStatistics();

        // Post conditions for this method contract.
        assert isIndexed();
        assert !isTranslucent();
    }

    /**
     * Reformats the {@linkplain ColorModel color model} to a {@linkplain ComponentColorModel
     * component color model} preserving transparency. This is used especially in order to go
     * from {@link PackedColorModel} to {@link ComponentColorModel}, which seems to be well
     * accepted by PNG and TIFF encoders.
     * <p>
     * <b>Tip:</b> If the source image is known to have only one tile and to use the
     * {@link IndexColorModel}, then the {@link IndexColorModel#convertToIntDiscrete}
     * method is an alternative that may be worth consideration.
     *
     * {@note This code is adapted from jai-interests mailing list archive.}
     *
     * @see #IGNORE_FULLY_TRANSPARENT_PIXELS
     * @see FormatDescriptor
     */
    @SuppressWarnings("fallthrough")
    private void forceComponentColorModel() {
        final ColorModel cm = image.getColorModel();
        if (cm instanceof ComponentColorModel) {
            // Already an component color model - nothing to do.
            return;
        }
        /*
         * The IndexColorModel case. We will expand the indexed values using a lookup table.
         * They will be expanded to gray scale if the color map contains only gray colors, or
         * to full RGB(A) otherwise.
         */
        if (cm instanceof IndexColorModel) {
            final IndexColorModel icm = (IndexColorModel) cm;
            final RenderingHints hints = getRenderingHints();
            Boolean ignoreTransparents = (Boolean) hints.get(IGNORE_FULLY_TRANSPARENT_PIXELS);
            if (ignoreTransparents == null) {
                ignoreTransparents = Boolean.TRUE;
            }
            final boolean isGrayScale = ColorUtilities.isGrayPalette(icm, ignoreTransparents);
            final boolean hasAlpha = icm.hasAlpha();
            final int numColorBands = isGrayScale ? 1 : 3;
            final byte[][] data = new byte[hasAlpha ? numColorBands + 1 : numColorBands][icm.getMapSize()];
            switch (numColorBands) {
                default: // Fallthrough in all cases.
                case 3:  icm.getBlues (data[2]);
                case 2:  icm.getGreens(data[1]);
                case 1:  icm.getReds  (data[0]);
                case 0:  break;
            }
            if (hasAlpha) {
                icm.getAlphas(data[numColorBands]);
            }
            final LookupTableJAI lut = new LookupTableJAI(data);
            setColorModel(hints, new ComponentColorModel(
                    ColorSpace.getInstance(isGrayScale ? CS_GRAY : CS_sRGB),
                    hasAlpha,
                    cm.isAlphaPremultiplied(),
                    cm.getTransparency(),
                    cm.getTransferType()));
            image = LookupDescriptor.create(image, lut, hints);
        } else {
            /*
             * For any color model other than IndexColorModel, setup an ImageLayout having
             * the new ColorModel and get the "Format" operation to apply the layout change.
             * Most of the code adapted from jai-interests is in 'getRenderingHints(int)'.
             */
            final int type = (cm instanceof DirectColorModel) ?
                    TYPE_BYTE : image.getSampleModel().getTransferType();
            final RenderingHints hints = getRenderingHints(type);
            image = FormatDescriptor.create(image, type, hints);
        }
        invalidateStatistics();

        // Post conditions for this method contract.
        assert image.getColorModel() instanceof ComponentColorModel;
    }

    /**
     * Sets the color model to the given target type. If the {@linkplain #image image} already uses
     * a color model of the given type, then this method does nothing. Otherwise the operation
     * depends on the target type, enumerated below:
     *
     * {@section Index Color Model}
     * If the target type is {@link IndexColorModel}, then this method performs a color reduction
     * using an <cite>Error Diffusion</cite> or an <cite>Ordered Dither</cite> operation depending
     * the value of the {@link #COLOR_QUANTIZATION} rendering hint. If this hint is not provided,
     * then the selected method is implementation-dependent and may vary in future versions.
     *
     * {@note The current implementation performs its work on the RGB color space only.}
     *
     * {@section Component Color Model}
     * If the target type is {@link ComponentColorModel}, then this method reformats the current
     * image preserving transparency. The result can have any number of bands from 1 to 4 inclusive,
     * depending on the color space (grayscale or RGB) and the presence of alpha channel.
     *
     * {@note This code is adapted from jai-interests mailing list archive.}
     *
     * @param type The target color model type. Currently supported types are
     *        {@link IndexColorModel} and {@link ComponentColorModel}. More
     *        may be added in the future.
     *
     * @see #isIndexed
     * @see #COLOR_QUANTIZATION
     * @see #IGNORE_FULLY_TRANSPARENT_PIXELS
     * @see ErrorDiffusionDescriptor
     * @see OrderedDitherDescriptor
     * @see FormatDescriptor
     * @see IndexColorModel#convertToIntDiscrete
     */
    public void setColorModelType(final Class<? extends ColorModel> type) {
        ensureNonNull("type", type);
        if (IndexColorModel.class.isAssignableFrom(type)) {
            forceIndexColorModel();
        } else if (ComponentColorModel.class.isAssignableFrom(type)) {
            forceComponentColorModel();
        } else {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.UNKNOWN_TYPE_$1, type));
        }
    }

    /**
     * Forces the {@linkplain #image image} color model to the given {@linkplain ColorSpace color
     * space} type. If the current color space is already of the given type, then this method does
     * nothing.
     * <p>
     * If a color space change is performed, then this operation creates an opaque {@link ColorModel}
     * because the {@code "ColorConvert"} operation treats data as having no alpha channel.
     * Consequently the alpha channel may be lost as a result of a call to this method.
     * <p>
     * Integral data are assumed to occupy the full range of the respective data type;
     * floating point data are assumed to be normalized to the range [0.0 &hellip; 1.0].
     *
     * @param type The desired Color Space type.
     *
     * @see #getColorSpaceType
     * @see ColorConvertDescriptor
     *
     * @since 3.00
     */
    public void setColorSpaceType(final PaletteInterpretation type) {
        ensureNonNull("type", type);
        if (!type.equals(getColorSpaceType())) {
            forceComponentColorModel();
            final ColorSpace cs;
            if (type.equals(PaletteInterpretation.RGB)) {
                cs = ColorSpace.getInstance(CS_sRGB);
            } else if (type.equals(PaletteInterpretation.GRAY)) {
                cs = ColorSpace.getInstance(CS_GRAY);
            } else if (type.equals(IHS)) {
                cs = IHSColorSpace.getInstance();
            } else {
                throw new UnsupportedOperationException(type.toString());
            }
            final int[] numBits = new int[cs.getNumComponents()];
            final int t = image.getSampleModel().getDataType();
            Arrays.fill(numBits, DataBuffer.getDataTypeSize(t));
            ColorModel cm = new ComponentColorModel(cs, numBits, false, false, Transparency.OPAQUE, t);
            final RenderingHints hints = getRenderingHints();
            cm = setColorModel(hints, cm);
            image = ColorConvertDescriptor.create(image, cm, hints);
            invalidateStatistics();
        }
        // Post conditions for this method contract.
        assert type.equals(getColorSpaceType());
    }

    /**
     * Creates an image which represents approximatively the intensity of {@linkplain #image image}.
     * The result is always a single-banded image. If the image uses a gray scale or an {@linkplain
     * IHSColorSpace IHS color space}, then this method just {@linkplain #retainBands retains the
     * first band} without any further processing. Otherwise, this method performs a simple
     * {@linkplain BandCombineDescriptor band combine} operation on the image in order to come up
     * with a simple estimation of the intensity of based on the average value of the color
     * components. Note that the alpha band is stripped from the image.
     *
     * {@note The result is a usually a gray scale image. Nevertheless it is not the same than
     *        invoking <code>setColorSpaceType(PaletteInterpretation.GRAY)</code> because this
     *        <code>intensity()</code> method gives equal weight to all RGB bands, while
     *        conversion to gray color space involve a more complex combination of bands.}
     *
     * @see BandCombineDescriptor
     */
    public void intensity() {
        /*
         * If the color model already uses a IHS color space or a gray scale color space,
         * keep only the intensity band. Otherwise, we need a RGB color space to be sure
         * to understand what we are doing.
         */
        final PaletteInterpretation type = getColorSpaceType();
        if (type != null && (type.equals(PaletteInterpretation.GRAY) || type.equals(IHS))) {
            retainBands(0, 0);
            invalidateStatistics();
            return;
        }
        enableTileCache(false);
        setColorSpaceType(PaletteInterpretation.RGB);
        enableTileCache(true);
        /*
         * Prepares a gray scale color model for all cases, then checks for the
         * IndexColorModel particular case. For that one, we will use a TableLookup.
         */
        final ColorModel cm = image.getColorModel();
        final RenderingHints hints = getRenderingHints();
        setColorModel(hints, new ComponentColorModel(ColorSpace.getInstance(CS_GRAY),
                false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE));
        if (cm instanceof IndexColorModel) {
            final IndexColorModel icm = (IndexColorModel) cm;
            final byte[] data = new byte[icm.getMapSize()];
            for (int i=0; i<data.length; i++) {
                final int RGB = icm.getRGB(i) & 0xFFFFFF;
                data[i] = (byte) (((RGB & 0xFF) + ((RGB >>> 8) & 0xFF) + ((RGB >>> 16) & 0xFF)) / 3);
            }
            if (!LookupTables.isIdentity(data)) {
                final LookupTableJAI lut = new LookupTableJAI(data);
                image = LookupDescriptor.create(image, lut, hints);
                invalidateStatistics();
                return;
            }
        }
        /*
         * If there is only one color band, there is nothing to do except removing the alpha band
         * if there is one. We will nevertheless replace the color model by the gray scale one so
         * the user get an image that looks like an intensity image, but the actual pixel values
         * are passed unchanged.
         */
        final int numColorBands = cm.getNumColorComponents();
        if (numColorBands == 1) {
            retainBands(0, 0); // No-op if there is no alpha band.
            if (!PaletteInterpretation.GRAY.equals(getColorSpaceType())) {
                image = NullDescriptor.create(image, hints);
                // Statistics are sill valid, since the Null
                // operation doesn't change pixel values.
            }
            return;
        }
        /*
         * We have more than one band. Note that there is no need to remove the
         * alpha band before to apply the "bandCombine" operation - it is
         * sufficient to let the coefficient for the alpha band to the 0 value.
         */
        final double[] coeff = new double[cm.getNumComponents() + 1];
        Arrays.fill(coeff, 0, numColorBands, 1.0 / numColorBands);
        image = BandCombineDescriptor.create(image, new double[][] {coeff}, hints);
        invalidateStatistics();

        // Post conditions for this method contract.
        assert getNumBands() == 1;
    }

    /**
     * Merges the bands of the given image with the bands of the current {@linkplain #image image}.
     * The new bands can be merged before or after the bands of the current image. They can also be
     * merged in the middle, but this is less efficient.
     *
     * @param toAdd Image having the bands to merge with the bands of the current image.
     * @param insertAt Where to insert the new bands: 0 for inserting the new bands before the
     *        current ones, or {@link #getNumBands() getNumBands()} for inserting the new bands
     *        after the current ones. Value -1 can be used as a shortcut for {@code getNumBands()}.
     *        Intermediate values are also allowed, but they are less common and less efficient.
     */
    public void mergeBands(RenderedImage toAdd, int insertAt) {
        ensureNonNull("toAdd", toAdd);
        final int numBands = getNumBands();
        if (insertAt < 0) insertAt += numBands + 1;
        ensureValidIndex(numBands + 1, insertAt);
        if (insertAt == 0) {
            image = BandMergeDescriptor.create(toAdd, image, getRenderingHints());
        } else if (insertAt == numBands) {
            image = BandMergeDescriptor.create(image, toAdd, getRenderingHints());
        } else {
            enableTileCache(false);
            final RenderedImage original = image;
            retainBands(0, insertAt - 1);
            mergeBands(toAdd, insertAt);
            toAdd = image;
            image = original;
            retainBands(insertAt, -1);
            enableTileCache(true);
            mergeBands(toAdd, 0);
        }
        invalidateStatistics();
    }

    /**
     * Retains the bands in the range {@code first} to {@code last} inclusive. All other bands
     * (if any) are discarded without any further processing. This method does nothing if the
     * given range include all bands.
     * <p>
     * For convenience, negative parameter values are relative to the {@linkplain #getNumBands()
     * number of bands} (i.e. the number of bands is added to negative parameter values). So -1
     * stands for the last band, -2 for the band before the last one, <i>etc.</i>
     * <p>
     * <b>Examples:</b>
     * <ul>
     *   <li>{@code retainBands( 0,  0)} retains the first band.</li>
     *   <li>{@code retainBands(-1, -1)} retains the last band.</li>
     *   <li>{@code retainBands( 1, -1)} retains all bands except the first one.</li>
     *   <li>{@code retainBands( 0, -2)} retains all bands except the last one.</li>
     * </ul>
     *
     * @param first The first band to retain, inclusive.
     * @param last  The last band to retain, <strong>inclusive</strong>.
     *
     * @see #getNumBands
     * @see BandSelectDescriptor
     */
    public void retainBands(int first, int last) {
        final int numBands = getNumBands();
        if (first < 0) first += numBands;
        if (last  < 0) last  += numBands;
        if (first < 0 || last < first || last >= numBands) {
            throw new IndexOutOfBoundsException(Errors.format(Errors.Keys.ILLEGAL_RANGE_$2, first, last));
        }
        final int count = last - first + 1;
        if (count != numBands) {
            final int[] bands = new int[count];
            for (int i=0; i<count; i++) {
                bands[i] = first++;
            }
            image = BandSelectDescriptor.create(image, bands, getRenderingHints());
        }

        // Post conditions for this method contract.
        assert getNumBands() <= numBands;
    }

    /**
     * Retains the given bands of the {@linkplain #image image}. All other bands (if any)
     * are discarded without any further processing.
     *
     * @param bands The bands to retain.
     *
     * @see #getNumBands
     * @see BandSelectDescriptor
     */
    public void retainBands(final int[] bands) {
        image = BandSelectDescriptor.create(image, bands, getRenderingHints());
    }

    /**
     * Binarizes the {@linkplain #image image}. If the image is multi-bands, then this method first
     * computes an estimation of its {@linkplain #intensity intensity}. Then, the threshold value
     * is set halfway between the {@linkplain #getMinimums minimal} and {@linkplain #getMaximums
     * maximal} values found in the image and {@link #binarize(double)} is invoked with that
     * threshold
     *
     * @param dynamic If {@code true}, the minimum and maximum values are computed dynamically
     *        from the sample values found in the image. If {@code false}, minimum and maximum
     *        values are determined only from the data type (for example they are always 0 and
     *        255 for {@link DataBuffer#TYPE_BYTE}).
     *
     * @see #isBinary
     * @see #binarize(double)
     * @see #binarize(double,double)
     * @see BinarizeDescriptor
     */
    public void binarize(final boolean dynamic) {
        if (!isBinary()) {
            if (getNumBands() != 1) {
                enableTileCache(false);
                intensity();
                enableTileCache(true);
            }
            final double minimum, maximum;
            if (dynamic) {
                final double[][] extremas = getExtremas();
                minimum = extremas[0][0];
                maximum = extremas[1][0];
            } else {
                final int type = image.getSampleModel().getDataType();
                if (ImageUtilities.isFloatType(type)) {
                    // Assuming normalized alpha values.
                    minimum = 0;
                    maximum = 1;
                } else {
                    minimum = ImageUtilities.minimum(type);
                    maximum = ImageUtilities.maximum(type);
                }
            }
            binarize((minimum + maximum) / 2);
        }
        // Post conditions for this method contract.
        assert isBinary();
    }

    /**
     * Binarizes the {@linkplain #image image}. If the image is multi-bands, then this method first
     * computes an estimation of its {@linkplain #intensity intensity}. If the image is already
     * binarized, then this method does nothing.
     *
     * @param threshold The threshold value.
     *
     * @see #isBinary
     * @see #binarize(boolean)
     * @see #binarize(double,double)
     * @see BinarizeDescriptor
     */
    public void binarize(final double threshold) {
        // Because binary image can contains only 0 or 1 values, only
        // threshold values in the range ]0..1] can be no-op.
        if (threshold <= 0 || threshold > 1 || !isBinary()) {
            if (getNumBands() != 1) {
                enableTileCache(false);
                intensity();
                enableTileCache(true);
            }
            final RenderingHints hints = getRenderingHints();
            image = BinarizeDescriptor.create(image, threshold, hints);
            invalidateStatistics();
        }
        // Post conditions for this method contract.
        assert isBinary();
    }

    /**
     * Binarizes the {@linkplain #image image} (if not already done) and replaces all 0 values by
     * {@code value0} and all 1 values by {@code value1}. If the image should be binarized using
     * a custom threshold value (instead of the automatic one), invoke {@link #binarize(double)}
     * explicitly before this method.
     *
     * @param value0 The value to be substituted to 0 in the binarized image.
     * @param value1 The value to be substituted to 1 in the binarized image.
     *
     * @see #isBinary
     * @see #binarize(double)
     */
    public void binarize(final double value0, final double value1) {
        enableTileCache(false);
        binarize(true);
        enableTileCache(true);
        final LookupTableJAI table;
        final int iv0 = (int) value0;
        final int iv1 = (int) value1;
        if (iv0 == value0 && iv1 == value1) {
            final int min = Math.min(iv0, iv1);
            final int max = Math.max(iv0, iv1);
            if (min >= 0) {
                if (max <= 0xFF) {
                    table = new LookupTableJAI(new byte[] {(byte) iv0, (byte) iv1});
                } else if (max <= 0xFFFF) {
                    table = new LookupTableJAI(new short[] {(short) iv0, (short) iv1}, true);
                } else {
                    table = new LookupTableJAI(new int[] {iv0, iv1});
                }
            } else if (min >= Short.MIN_VALUE && max <= Short.MAX_VALUE) {
                table = new LookupTableJAI(new short[] {(short) iv0, (short) iv1}, false);
            } else {
                table = new LookupTableJAI(new int[] {iv0, iv1});
            }
        } else {
            final float fv0 = (float) value0;
            final float fv1 = (float) value1;
            if (Double.doubleToRawLongBits(fv0) == Double.doubleToRawLongBits(value0) &&
                Double.doubleToRawLongBits(fv1) == Double.doubleToRawLongBits(value1))
            {
                table = new LookupTableJAI(new float[] {fv0, fv1});
            } else {
                table = new LookupTableJAI(new double[] {value0, value1});
            }
        }
        final RenderingHints hints = getRenderingHints();
        setColorModel(hints, new ComponentColorModel(ColorSpace.getInstance(CS_GRAY),
                false, false, Transparency.OPAQUE, table.getDataType()));
        image = LookupDescriptor.create(image, table, hints);
        invalidateStatistics();
    }

    /**
     * Masks the given background values, to be replaced by the given new values. This method
     * computes a {@linkplain SilhouetteMask silhouette mask} of the current {@linkplain #image
     * image} using the given background values, then applies this mask on the image using the
     * {@link #mask(RenderedImage,double[]) mask} method below.
     * <p>
     * This method is appropriate for replacing the background color surrounding a rotated
     * rectangular area, as illustrated in the {@link Mask} javadoc.
     *
     * @param background The background color, as an array of sample values, to be replaced by the
     *                   new sampel values. The length of this array should be equals to the
     *                   {@linkplain #getNumBands number of bands} of the image.
     * @param newValues  The new sample values to be given to the background pixels, or {@code null}
     *                   for assigning the transparent color to the background.
     *
     * @see SilhouetteMask
     */
    @SuppressWarnings("fallthrough")
    public void maskBackground(final double[][] background, double[] newValues) {
        ensureNonNull("background", background);
        switch (background.length) {
            case 0: return;
            case 1: if (Arrays.equals(background[0], newValues)) return;
        }
        RenderedImage mask = JAI.create(SilhouetteMask.OPERATION_NAME,
                new ParameterBlockJAI(SilhouetteMask.OPERATION_NAME)
                .addSource(image).set(background, 0), getRenderingHints());
        if (newValues == null) {
            /*
             * User wants to make the background transparent. If the image uses IndexColorModel,
             * we will try to recycle the existing transparent pixel value (if any) and set the
             * background to that value.
             */
            final ColorModel cm = image.getColorModel();
            if (cm instanceof IndexColorModel) {
                final IndexColorModel icm = (IndexColorModel) cm;
                int transparent;
                switch (icm.getTransparency()) {
                    case Transparency.OPAQUE: {
                        if (background.length != 0) {
                            final double[] samples = background[0];
                            if (samples.length != 0) {
                                transparent = (int) samples[0];
                                break;
                            }
                        }
                        // Fall through; the end effect will be to use 0.
                    }
                    default: {
                        /*
                         * Reuses the current transparent pixel, which may still -1.
                         * forceBitmaskIndexColorModel(-1) will searches for the pixel
                         * having the smallest alpha value, or use 0 if none are found.
                         */
                        transparent = icm.getTransparentPixel();
                        break;
                    }
                }
                forceBitmaskIndexColorModel(transparent);
                newValues = new double[] {getTransparentPixel()};
            } else if (!cm.hasAlpha()) {
                /*
                 * If the image uses anything else than IndexColorModel, then we need to add
                 * an alpha band if it doesn't already have one. If an alpha band is already
                 * there, then we just need to set the background pixels to zero.
                 */
                final int type = image.getSampleModel().getDataType();
                final double max = ImageUtilities.isFloatType(type) ? 1 : ImageUtilities.maximum(type);
                final ImageWorker fork = new ImageWorker(this);
                fork.setImage(mask);
                fork.enableTileCache(false);
                fork.binarize(max, 0);
                mask = fork.image;
                mergeBands(mask, -1);
                return;
            } else {
                newValues = new double[cm.getNumComponents()];
                // Left all values initialized to zero.
            }
        }
        mask(mask, newValues);
    }

    /**
     * Applies the specified mask over the current {@linkplain #image}. The mask is typically
     * {@linkplain #binarize(boolean) binarized}, but this is not mandatory. For every pixels
     * in the mask having a value different than zero, the corresponding pixel in the
     * {@linkplain #image} will be set to the specified {@code newValues}.
     *
     * @param mask      The mask to apply.
     * @param newValues The new sample values for every pixels in the {@linkplain #image image}
     *                  corresponding to a non-zero value in the mask. If this parameter is
     *                  {@code null}, then the non-zero value in the mask itself will be used.
     *                  If non-null, then the array length should be equals to the number of
     *                  bands in the destination image.
     *
     * @see Mask
     */
    public void mask(final RenderedImage mask, final double[] newValues) {
        ensureNonNull("mask", mask);
        image = JAI.create(Mask.OPERATION_NAME, new ParameterBlockJAI(Mask.OPERATION_NAME)
                .addSource(image).addSource(mask).set(newValues, 0), getRenderingHints());
        invalidateStatistics();
    }

    /**
     * Adds every pixels of the given image to the current {@linkplain #image image}.
     * See JAI {@link AddDescriptor} for details.
     *
     * @param toAdd The image to be added to the one in this worker.
     *
     * @see AddDescriptor
     */
    public void add(final RenderedImage toAdd) {
        ensureNonNull("toAdd", toAdd);
        image = AddDescriptor.create(image, toAdd, getRenderingHints());
        invalidateStatistics();
    }

    /**
     * Adds every pixels in the current {@linkplain #image image} with the given constants.
     * The length of the given array must be equals to the {@linkplain #getNumBands() number
     * of bands}. See JAI {@link AddConstDescriptor} for details.
     *
     * @param values The constants to be added.
     *
     * @see AddConstDescriptor
     */
    public void add(final double[] values) {
        ensureNonNull("values", values);
        image = AddConstDescriptor.create(image, values, getRenderingHints());
        invalidateStatistics();
    }

    /**
     * Subtracts every pixels of the given image from the current {@linkplain #image image}.
     * See JAI {@link SubtractDescriptor} for details.
     *
     * @param toSubtract The image to be subtracted from the one in this worker.
     *
     * @see SubtractDescriptor
     */
    public void subtract(final RenderedImage toSubtract) {
        ensureNonNull("toSubtract", toSubtract);
        image = SubtractDescriptor.create(image, toSubtract, getRenderingHints());
        invalidateStatistics();
    }

    /**
     * Subtracts the given constants from every pixels in the current {@linkplain #image image}.
     * The length of the given array must be equals to the {@linkplain #getNumBands() number
     * of bands}. See JAI {@link SubtractConstDescriptor} for details.
     *
     * @param values The constants to be subtracted.
     *
     * @see SubtractConstDescriptor
     */
    public void subtract(final double[] values) {
        ensureNonNull("values", values);
        image = SubtractConstDescriptor.create(image, values, getRenderingHints());
        invalidateStatistics();
    }

    /**
     * Multiplies every pixels of the given image to the current {@linkplain #image image}.
     * See JAI {@link MultiplyDescriptor} for details.
     *
     * @param toMultiply The image to be added to the one in this worker.
     *
     * @see MultiplyDescriptor
     */
    public void multiply(final RenderedImage toMultiply) {
        ensureNonNull("toMultiply", toMultiply);
        image = MultiplyDescriptor.create(image, toMultiply, getRenderingHints());
        invalidateStatistics();
    }

    /**
     * Multiplies every pixels in the current {@linkplain #image image} with the given constants.
     * The length of the given array must be equals to the {@linkplain #getNumBands() number of
     * bands}. See JAI {@link MultiplyConstDescriptor} for details.
     *
     * @param values The constants to be multiplied.
     *
     * @see MultiplyConstDescriptor
     */
    public void multiply(final double[] values) {
        ensureNonNull("values", values);
        image = MultiplyConstDescriptor.create(image, values, getRenderingHints());
        invalidateStatistics();
    }

    /**
     * Divides every pixels of the given image to the current {@linkplain #image image}.
     * See JAI {@link DivideDescriptor} for details.
     *
     * @param toDivide The image to divide the one in this worker.
     *
     * @see DivideDescriptor
     */
    public void divide(final RenderedImage toDivide) {
        ensureNonNull("toDivide", toDivide);
        image = DivideDescriptor.create(image, toDivide, getRenderingHints());
        invalidateStatistics();
    }

    /**
     * Divides every pixels in the current {@linkplain #image image} by the given constants.
     * The length of the given array must be equals to the {@linkplain #getNumBands() number
     * of bands}. See JAI {@link DivideByConstDescriptor} for details.
     *
     * @param values The divisor constants.
     *
     * @see DivideByConstDescriptor
     */
    public void divideBy(final double[] values) {
        ensureNonNull("values", values);
        image = DivideByConstDescriptor.create(image, values, getRenderingHints());
        invalidateStatistics();
    }

    /**
     * Inverts the sign of pixel values in the {@linkplain #image image}.
     *
     * @see InvertDescriptor
     */
    public void invert() {
        image = InvertDescriptor.create(image, getRenderingHints());
        invalidateStatistics();
    }

    /**
     * Performs a bit-wise logical "xor" between every pixel in the same band of the
     * {@linkplain #image image} and the constant from the corresponding array entry.
     * See JAI {@link XorConstDescriptor} for details.
     *
     * @param values The constants to be xored.
     *
     * @see XorConstDescriptor
     */
    public void xor(int[] values) {
        ensureNonNull("values", values);
        image = XorConstDescriptor.create(image, values, getRenderingHints());
        invalidateStatistics();
    }
}
