/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

import java.util.Arrays;

import java.awt.Dimension;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

import javax.imageio.ImageTypeSpecifier;

import org.apache.sis.internal.coverage.ColorModelFactory;
import org.geotoolkit.image.io.large.WritableLargeRenderedImage;
import org.geotoolkit.lang.Static;

import static org.geotoolkit.image.internal.PlanarConfiguration.INTERLEAVED;

/**
 * Aggregate some methods to create {@link BufferedImage} or {@link WritableLargeRenderedImage} more easily.
 *
 * @author Remi Marechal (Geomatys).
 * @see PhotometricInterpretation
 * @see PlanarConfiguration
 * @see SampleType
 * @see WritableLargeRenderedImage
 */
public class ImageUtils extends Static {

    //---------------- sammple format -------------------//
    /**
     * Define buffer type as IEEE floating point data.
     */
    private final static short SAMPLEFORMAT_IEEEFP = 3;

    /**
     * Define buffer type as signed integer point data.
     */
    private final static short SAMPLEFORMAT_INT     = 2;

    /**
     * Define buffer type as unsigned integer point data.
     */
    private final static short SAMPLEFORMAT_UINT    = 1;

    //------------ Photometric interpretation -----------//
    /**
     * Define image properties for bilevel and grayscale images: 0 is imaged as black.
     */
    private final static short PHOTOMETRIC_MINISBLACK = 1;

    /**
     * Define image properties for RGB image.
     * RGB value of (0,0,0) represents black, and (255,255,255) represents white, assuming 8-bit components.
     * The components are stored in the indicated order: first Red, then Green, then Blue.
     */
    private final static short PHOTOMETRIC_RGB         = 2;

    /**
     * Define image with Palette color.
     * In this model, a color is described with a single component.
     * The value of the component is used as an index into the red,
     * green and blue curves in the ColorMap field to retrieve an RGB triplet that defines the color.
     * When PhotometricInterpretation = 3 is used, ColorMap must be present and SamplesPerPixel must be 1.
     */
    private final static short PHOTOMETRIC_PALETTE     = 3;

    /**
     * Integer that define planar configuration as interleaved.
     * For example in a RGB image, within the same band, pixel value will be order like follow : RGBRGBRGB ...
     */
    private final static short PLANAR_INTERLEAVED      = 1;

    /**
     * Integer that define planar configuration as banded.
     * For example in a RGB image, within the first band, pixel value will be order
     * like follow : RRRRRRRR... and next band : GGGGGGG... and last : BBBBBBB.
     */
    private final static short PLANAR_BANDED           = 2;

    /**
     * Returns a {@link BufferedImage} with an internaly palette {@link ColorSpace}, created from given parameters.
     *
     * <p><strong>Moreover : <br/>
     * - the type of internal image {@link SampleModel} is interleaved.<br/>
     * - In other words the internal planar configuration is {@link PlanarConfiguration#INTERLEAVED} type.<br/>
     * - Image will NOT CONTAIN any alpha canal.
     * </strong></p>
     *
     * @param width image width.
     * @param height image height.
     * @param type type of internal data.
     * @param numband band number of internaly image rasters.
     * @param java2DColorMap array which define map when PhotometricInterpretation is type palette.
     * @return created palette {@link BufferedImage}.
     * @throws IllegalArgumentException if colorMap argument is {@code null}.
     * @see SampleType
     * @see PhotometricInterpretation#PALETTE
     */
    public static BufferedImage createPaletteImage(final int width, final int height, final SampleType type,
                                                   final int numband, final int[] java2DColorMap) {
        return createImage(width, height,
                            type, numband,
                            PhotometricInterpretation.PALETTE, PlanarConfiguration.INTERLEAVED,
                            false, false,
                            java2DColorMap);
    }

    /**
     * Returns a {@link BufferedImage} of 4 bandswith an internaly AlphaRGB {@link ColorSpace}
     *  and created from given parameters.
     *
     * <p><strong>Moreover : <br/>
     * - the type of internal image {@link SampleModel} is interleaved.<br/>
     * - In other words the internal planar configuration is {@link PlanarConfiguration#INTERLEAVED} type.<br/>
     * - Image will CONTAIN alpha canal.
     * </strong></p>
     *
     * @param width image width.
     * @param height image height.
     * @param type type of internal data.
     * @param isAlphaPremultiplied
     * @return created AlphaRGB {@link BufferedImage}.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @see SampleType
     * @see PlanarConfiguration#INTERLEAVED
     * @see PhotometricInterpretation#RGB
     */
    public static BufferedImage createARGBInterleavedImage(final int width, final int height,
                                                           final SampleType type, final boolean isAlphaPremultiplied) {
        return createImage(width, height,
                           type, 4,
                           PhotometricInterpretation.RGB, PlanarConfiguration.INTERLEAVED,
                           true, isAlphaPremultiplied, null);
    }

    /**
     * Returns a {@link BufferedImage} of 3 bands, with an internaly RGB {@link ColorSpace}
     * and created from given parameters.
     *
     * <p><strong>Moreover : <br/>
     * - the type of internal image {@link SampleModel} is interleaved.<br/>
     * - In other words the internal planar configuration is {@link PlanarConfiguration#INTERLEAVED} type.<br/>
     * - Image will NOT CONTAIN any alpha canal.
     * </strong></p>
     *
     * @param width image width.
     * @param height image height.
     * @param type type of internal data.
     * @return created RGB {@link BufferedImage}.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @see SampleType
     * @see PlanarConfiguration#INTERLEAVED
     * @see PhotometricInterpretation#RGB
     */
    public static BufferedImage createRGBInterleavedImage(final int width, final int height, final SampleType type) {
        return createImage(width, height,
                type, 3, PhotometricInterpretation.RGB, PlanarConfiguration.INTERLEAVED,
                false, false, null);
    }

    /**
     * Returns a {@link BufferedImage} of 4 bands, with an internaly AlphaRGB {@link ColorSpace}
     * and created from given parameters.
     *
     * <p><strong>Moreover : the type of internal image {@link SampleModel} is banded.<br/>
     * In other words the internal planar configuration is {@link PlanarConfiguration#BANDED} type.<br/>
     * - Image will CONTAIN alpha canal.
     * </strong></p>
     *
     * @param width image width.
     * @param height image height.
     * @param type type of internal data.
     * @param isAlphaPremultiplied
     * @return created AlphaRGB {@link BufferedImage}.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @see SampleType
     * @see PlanarConfiguration#BANDED
     * @see PhotometricInterpretation#RGB
     */
    public static BufferedImage createARGBBandedImage(final int width, final int height, final SampleType type, final boolean isAlphaPremultiplied) {
        return createImage(width, height, type, 4, PhotometricInterpretation.RGB, PlanarConfiguration.BANDED,true, isAlphaPremultiplied, null);
    }

    /**
     * Returns a {@link BufferedImage} of 3 bands with an internaly RGB {@link ColorSpace}, created from given parameters.
     *
     * <p><strong>Moreover : <br/>
     * - the type of internal image {@link SampleModel} is banded.<br/>
     * - In other words the internal planar configuration is {@link PlanarConfiguration#BANDED} type.<br/>
     * - Image will NOT CONTAIN any alpha canal.
     * </strong></p>
     *
     * @param width image width.
     * @param height image height.
     * @param type type of internal data.
     * @return created RGB {@link BufferedImage}.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @see SampleType
     * @see PlanarConfiguration#BANDED
     * @see PhotometricInterpretation#RGB
     */
    public static BufferedImage createRGBBandedImage(final int width, final int height, final SampleType type) {
        return createImage(width, height,
                            type, 3,
                            PhotometricInterpretation.RGB, PlanarConfiguration.BANDED,
                            false, false, null);
    }

    /**
     * Returns a {@link BufferedImage} with an internaly gray scaled {@link ColorSpace}, created from given parameters.
     *
     * <p><strong>Moreover : <br/>
     * - the type of internal image {@link SampleModel} is banded.<br/>
     * - In other words the internal planar configuration is {@link PlanarConfiguration#BANDED} type.<br/>
     * - Image will NOT CONTAIN any alpha canal.
     * </strong></p>
     *
     * @param width image width.
     * @param height image height.
     * @param type type of internal data.
     * @param numBand band number.
     * @return created gray scaled {@link BufferedImage}.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @see SampleType
     * @see PlanarConfiguration#banded
     * @see PhotometricInterpretation#GRAYSCALE
     */
    public static BufferedImage createGreyScaledBandedImage(final int width, final int height,
                                                            final SampleType type, final int numBand) {
        return createImage(width, height,
                            type, numBand,
                            PhotometricInterpretation.GRAYSCALE, PlanarConfiguration.BANDED,
                            false, false, null);
    }

    /**
     * Returns a {@link BufferedImage} with an internaly gray scaled {@link ColorSpace}, created from given parameters.
     *
     * <p><strong>Moreover : <br/>
     * - the type of internal image {@link SampleModel} is pixel interleaved.<br/>
     * - In other words the internal planar configuration is {@link PlanarConfiguration#INTERLEAVED} type.<br/>
     * - Image will NOT CONTAIN any alpha canal.
     * </strong></p>
     *
     * @param width image width.
     * @param height image height.
     * @param type type of internal data.
     * @param numBand band number.
     * @return created gray scaled {@link BufferedImage}.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @see SampleType
     * @see PlanarConfiguration#INTERLEAVED
     * @see PhotometricInterpretation#GRAYSCALE
     */
    public static BufferedImage createGreyScaledInterleavedImage(final int width, final int height,
                                                                 final SampleType type, final int numBand) {
        return createImage(width, height,
                            type, numBand,
                            PhotometricInterpretation.GRAYSCALE, PlanarConfiguration.INTERLEAVED,
                            false, false, null);
    }

    /**
     * Returns a {@link WritableLargeRenderedImage} with an internaly palette {@link ColorSpace}, created from given parameters.
     *
     * <p><strong>Moreover : <br/>
     * - the type of internal image {@link SampleModel} is pixel interleaved.<br/>
     * - In other words the internal planar configuration is {@link PlanarConfiguration#INTERLEAVED} type.<br/>
     * - Image will NOT CONTAIN any alpha canal.
     * </strong></p>
     *
     * @param width image width.
     * @param height image height.
     * @param type type of internal data.
     * @param numBand band number.
     * @param java2DColorMap array which define map when PhotometricInterpretation is type palette.
     * @return created palette {@link WritableLargeRenderedImage}.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @throws IllegalArgumentException if colorMap argument is {@code null}.
     * @see SampleType
     * @see WritableLargeRenderedImage
     * @see PhotometricInterpretation#PALETTE
     */
    public static WritableLargeRenderedImage createPaletteLargeImage(final int width, final int height,
                                                                     final SampleType type, final int numBand,
                                                                     final int[] java2DColorMap) {
        final ColorModel cm  = createColorModel(type, numBand, PhotometricInterpretation.PALETTE, false, false, java2DColorMap);
        final SampleModel sm = createSampleModel(INTERLEAVED, type, width, height, numBand);
        return new WritableLargeRenderedImage(width, height, cm, sm);
    }

    /**
     * Returns a {@link WritableLargeRenderedImage} with an internaly AlphaRGB {@link ColorSpace} and 4 bands, created from given parameters.
     *
     * <p><strong>Moreover : <br/>
     * - the type of internal image {@link SampleModel} is interleaved.<br/>
     * - In other words the internal planar configuration is {@link PlanarConfiguration#INTERLEAVED} type.<br/>
     * - Image will CONTAIN alpha canal.
     * </strong></p>
     *
     * @param width image width.
     * @param height image height.
     * @param type type of internal data.
     * @param isAlphaPremultiplied
     * @return created ARGB {@link WritableLargeRenderedImage}.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @throws IllegalArgumentException if colorMap argument is {@code null} when PhotometricInterpretation is type palette.
     * @see SampleType
     * @see WritableLargeRenderedImage
     * @see PhotometricInterpretation#RGB
     */
    public static WritableLargeRenderedImage createARGBLargeImage(final int width, final int height,
                                                                  final SampleType type, final boolean isAlphaPremultiplied) {
        final ColorModel cm  = createColorModel(type, 4, PhotometricInterpretation.RGB, true, isAlphaPremultiplied, null);
        final SampleModel sm = createSampleModel(INTERLEAVED, type, width, height, 4);
        return new WritableLargeRenderedImage(width, height, cm, sm);
    }

    /**
     * Returns a {@link WritableLargeRenderedImage} of 3 bands with an internaly RGB {@link ColorSpace},
     * created from given parameters.
     *
     * <p><strong>Moreover : <br/>
     * - the type of internal image {@link SampleModel} is interleaved.<br/>
     * - In other words the internal planar configuration is {@link PlanarConfiguration#INTERLEAVED} type.<br/>
     * - Image will NOT CONTAIN alpha canal.
     * </strong></p>
     *
     * @param width image width.
     * @param height image height.
     * @param type type of internal data.
     * @return created RGB {@link WritableLargeRenderedImage}.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @throws IllegalArgumentException if colorMap argument is {@code null} when PhotometricInterpretation is type palette.
     * @see SampleType
     * @see WritableLargeRenderedImage
     * @see PhotometricInterpretation#RGB
     */
    public static WritableLargeRenderedImage createRGBLargeImage(final int width, final int height, final SampleType type) {
        final ColorModel cm  = createColorModel(type, 3, PhotometricInterpretation.RGB, false, false, null);
        final SampleModel sm = createSampleModel(INTERLEAVED, type, width, height, 3);
        return new WritableLargeRenderedImage(width, height, cm, sm);
    }

    /**
     * Returns a {@link WritableLargeRenderedImage} with an internaly gray scaled {@link ColorSpace}, created from given parameters.
     *
     * <p><strong>Moreover : <br/>
     * - the type of internal image {@link SampleModel} is interleaved.<br/>
     * - In other words the internal planar configuration is {@link PlanarConfiguration#INTERLEAVED} type.<br/>
     * - Image will NOT CONTAIN alpha canal.
     * </strong></p>
     *
     * @param width image width.
     * @param height image height.
     * @param type type of internal data.
     * @param numBand band number.
     * @return created gray Scaled {@link WritableLargeRenderedImage}.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @throws IllegalArgumentException if colorMap argument is {@code null} when PhotometricInterpretation is type palette.
     * @see SampleType
     * @see WritableLargeRenderedImage
     * @see PhotometricInterpretation#GRAYSCALE
     */
    public static WritableLargeRenderedImage createGreyScaledLargeImage(final int width, final int height,
                                                                    final SampleType type, final int numBand) {
        final ColorModel cm  = createColorModel(type, numBand, PhotometricInterpretation.GRAYSCALE, false, false, null);
        final SampleModel sm = createSampleModel(INTERLEAVED, type, width, height, numBand);
        return new WritableLargeRenderedImage(width, height, cm, sm);
    }

    /**
     * Returns a {@link WritableLargeRenderedImage} created from given parameters.
     *
     * <p><strong>Moreover : the type of internal image {@link SampleModel} is pixel interleaved.<br/>
     * In other words the internal planar configuration is {@link PlanarConfiguration#INTERLEAVED} type.</strong></p>
     *
     * @param width image width.
     * @param height image height.
     * @param type type of internal data.
     * @param numBand band number.
     * @param pI define type of {@link ColorModel}, for example RGB, palette etc.
     * @param hasAlpha
     * @param isAlphaPremultiplied
     * @param java2DColorMap array which define map when PhotometricInterpretation is type palette, or should be {@code null}.
     * @return created {@link WritableLargeRenderedImage}.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @throws IllegalArgumentException if colorMap argument is {@code null} when PhotometricInterpretation is type palette.
     * @see SampleType
     * @see PhotometricInterpretation
     * @see WritableLargeRenderedImage
     */
    public static WritableLargeRenderedImage createLargeImage(final int width, final int height, final SampleType type, final int numBand,
                                                              final PhotometricInterpretation pI,
                                                             final boolean hasAlpha, final boolean isAlphaPremultiplied,
                                                             final int[] java2DColorMap) {
        final ColorModel cm = createColorModel(type, numBand, pI, hasAlpha, isAlphaPremultiplied, java2DColorMap);
        final SampleModel sm = createSampleModel(INTERLEAVED, type, width, height, numBand);
        return new WritableLargeRenderedImage(width, height, cm, sm);
    }

    /**
     * Returns a {@link WritableLargeRenderedImage} created from given parameters.
     *
     * <p><strong>Moreover : the type of internal image {@link SampleModel} is pixel interleaved.<br/>
     * In other words the internal planar configuration is {@link PlanarConfiguration#INTERLEAVED} type.</strong></p>
     *
     * @param minx minimum image coordinate in X direction.
     * @param miny minimum image coordinate in Y direction.
     * @param width image width.
     * @param height image height.
     * @param tileSize size of internal image tiles.
     * @param type type of internal data.
     * @param numBand band number.
     * @param tilegridXOffset
     * @param tilegridYOffset
     * @param pI define type of {@link ColorModel}, for example RGB, palette etc.
     * @param hasAlpha
     * @param isAlphaPremultiplied
     * @param java2DColorMap array which define map when PhotometricInterpretation is type palette, or should be {@code null}.
     * @return created {@link WritableLargeRenderedImage}.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @throws IllegalArgumentException if colorMap argument is {@code null} when PhotometricInterpretation is type palette.
     * @see SampleType
     * @see PhotometricInterpretation
     * @see WritableLargeRenderedImage
     */
    public static WritableLargeRenderedImage createLargeImage(final int minx, final int miny, final int width, final int height,
                                                              final Dimension tileSize, final SampleType type, final int numBand,
                                                              final int tilegridXOffset, final int tilegridYOffset,
                                                              final PhotometricInterpretation pI,
                                                             final boolean hasAlpha, final boolean isAlphaPremultiplied,
                                                             final int[] java2DColorMap) {
        final ColorModel cm  = createColorModel(type, numBand, pI, hasAlpha, isAlphaPremultiplied, java2DColorMap);
        final SampleModel sm = createSampleModel(INTERLEAVED, type, width, height, numBand);
        return new WritableLargeRenderedImage(minx, miny, width, height, tileSize, tilegridXOffset, tilegridYOffset, cm, sm);
    }

    /**
     * Returns a {@link BufferedImage} created from given parameters.
     *
     * @param width image width.
     * @param height image height.
     * @param type type of internal data.
     * @param numBand band number.
     * @param pI define type of {@link ColorModel}, for example RGB, palette etc.
     * @param pC define type of internal {@link SampleModel}, banded or interleaved.
     * @param hasAlpha
     * @param isAlphaPremultiplied
     * @param java2DColorMap array which define map when PhotometricInterpretation is type palette, or should be {@code null}.
     * @return created {@link BufferedImage}.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @throws IllegalArgumentException if colorMap argument is {@code null} when PhotometricInterpretation is type palette.
     * @see SampleType
     * @see PhotometricInterpretation
     * @see PlanarConfiguration
     */
    public static BufferedImage createImage(final int width, final int height, final SampleType type,
                                            final int numBand,
                                            final PhotometricInterpretation pI, final PlanarConfiguration pC,
                                            final boolean hasAlpha, final boolean isAlphaPremultiplied,
                                            final int[] java2DColorMap) {
        final ImageTypeSpecifier imgTypeSpec = buildImageTypeSpecifier(type, numBand,
                                                                        pI, pC,
                                                                        hasAlpha, isAlphaPremultiplied,
                                                                        java2DColorMap);
        return imgTypeSpec.createBufferedImage(width, height);
    }

    /**
     * Returns an appropriate {@link ImageTypeSpecifier} built from given parameters.
     *
     * @param type type of internal data.
     * @param numBand band number.
     * @param pI define type of {@link ColorModel}, for example RGB, palette etc.
     * @param pC define type of internal {@link SampleModel}, banded or interleaved.
     * @param hasAlpha
     * @param isAlphaPremultiplied
     * @param java2DColorMap array which define map when PhotometricInterpretation is type palette, or should be {@code null}.
     * @return {@link ImageTypeSpecifier} built from given parameters.
     * @throws UnsupportedOperationException if problem during internal {@link ColorModel} creation.
     * @throws IllegalArgumentException if colorMap argument is {@code null} when PhotometricInterpretation is type palette.
     * @see SampleType
     * @see PhotometricInterpretation
     * @see PlanarConfiguration
     */
    public static ImageTypeSpecifier buildImageTypeSpecifier(final SampleType type, final int numBand,
                                                             final PhotometricInterpretation pI, final PlanarConfiguration pC,
                                                             final boolean hasAlpha, final boolean isAlphaPremultiplied,
                                                             final int[] java2DColorMap) throws UnsupportedOperationException {

        final int sampleBitSize;
        final short sampleFormat;

        switch (type) {
            case BYTE : {
                sampleBitSize = java.lang.Byte.SIZE;
                sampleFormat  = SAMPLEFORMAT_UINT;
                break;
            }
            case  SHORT : {
                sampleBitSize = java.lang.Short.SIZE;
                sampleFormat  = SAMPLEFORMAT_INT;
                break;
            }
            case USHORT : {
                sampleBitSize = java.lang.Short.SIZE;
                sampleFormat  = SAMPLEFORMAT_UINT;
                break;
            }
            case INTEGER : {
                sampleBitSize = java.lang.Integer.SIZE;
                sampleFormat  = SAMPLEFORMAT_UINT;
                break;
            }
            case FLOAT  : {
                sampleBitSize = java.lang.Float.SIZE;
                sampleFormat  = SAMPLEFORMAT_IEEEFP;
                break;
            }
            case DOUBLE : {
                sampleBitSize = java.lang.Double.SIZE;
                sampleFormat  = SAMPLEFORMAT_IEEEFP;
                break;
            }
            default : throw new IllegalArgumentException("Unknow sample type.");
        }

        final short photometricInterpret;
        switch(pI) {
            case GRAYSCALE : {
                photometricInterpret = PHOTOMETRIC_MINISBLACK;
                break;
            }
            case RGB : {
                photometricInterpret = PHOTOMETRIC_RGB;
                break;
            }
            case PALETTE : {
                photometricInterpret = PHOTOMETRIC_PALETTE;
                if (java2DColorMap == null) throw new IllegalArgumentException("colorMap should not be null with palette photometric interpretation.");
                break;
            }
            default : throw new IllegalArgumentException("Unknow photometric Interpretation.");
        }

        final short planarConfig = (pC.equals(PlanarConfiguration.BANDED)) ? PLANAR_BANDED : PLANAR_INTERLEAVED;
        return buildImageTypeSpecifier(sampleBitSize, numBand,
                                        photometricInterpret, sampleFormat, planarConfig,
                                        hasAlpha, isAlphaPremultiplied,
                                        java2DColorMap);
    }

     /**
      *  Returns {@link ColorModel} create from given parameters.
      *
      * @param type type of data within {@link ColorModel}.
      * @param numBand band nmber.
      * @param pI define type of {@link ColorModel}, for example RGB, palette etc.
     * @param hasAlpha
     * @param isAlphaPremultiplied
      * @param java2DColorMap array which define map when PhotometricInterpretation is type palette, or should be {@code null}.
      * @return created {@link ColorModel}.
      * @throws UnsupportedOperationException if problem during {@link ColorModel} creation.
      * @throws IllegalArgumentException if colorMap argument is {@code null} when PhotometricInterpretation is type palette.
      * @see SampleType
      * @see PhotometricInterpretation
      */
    public static ColorModel createColorModel(final SampleType type, final int numBand,
                                              final PhotometricInterpretation pI,
                                              final boolean hasAlpha, final boolean isAlphaPremultiplied,
                                              final int[] java2DColorMap) {
         final int sampleBitSize;
        final short sampleFormat;

        switch (type) {
            case BYTE : {
                sampleBitSize = java.lang.Byte.SIZE;
                sampleFormat  = SAMPLEFORMAT_UINT;
                break;
            }
            case SHORT  : {
                sampleBitSize = java.lang.Short.SIZE;
                sampleFormat  = SAMPLEFORMAT_INT;
                break;
            }
            case USHORT : {
                sampleBitSize = java.lang.Short.SIZE;
                sampleFormat  = SAMPLEFORMAT_UINT;
                break;
            }
            case INTEGER : {
                sampleBitSize = java.lang.Integer.SIZE;
                sampleFormat  = SAMPLEFORMAT_UINT;
                break;
            }
            case FLOAT  : {
                sampleBitSize = java.lang.Float.SIZE;
                sampleFormat  = SAMPLEFORMAT_IEEEFP;
                break;
            }
            case DOUBLE : {
                sampleBitSize = java.lang.Double.SIZE;
                sampleFormat  = SAMPLEFORMAT_IEEEFP;
                break;
            }
            default : throw new IllegalArgumentException("Unknow sample type.");
        }

        final short photometricInterpret;
        switch(pI) {
            case GRAYSCALE : {
                photometricInterpret = PHOTOMETRIC_MINISBLACK;
                break;
            }
            case RGB : {
                photometricInterpret = PHOTOMETRIC_RGB;
                break;
            }
            case PALETTE : {
                photometricInterpret = PHOTOMETRIC_PALETTE;
                if (java2DColorMap == null) throw new IllegalArgumentException("colorMap should not be null with palette photometric interpretation.");
                break;
            }
            default : throw new IllegalArgumentException("Unknow photometric Interpretation.");
        }
        return createColorModel(sampleBitSize, numBand,
                                photometricInterpret, sampleFormat,
                                hasAlpha, isAlphaPremultiplied,
                                java2DColorMap);
    }

    /**
     * Define photometric interpretation tiff tag in function of {@link ColorModel} properties.
     *
     * @param cm image color model.
     * @return 1 for gray, 2 for RGB, 3 for indexed colormodel.
     */
    public static short getPhotometricInterpretation(final ColorModel cm) {
        final ColorSpace cs = cm.getColorSpace();

        //-- to do : if we need we can also define 1 for an ScaledColorSpace --//
        if (cs.equals(ColorSpace.getInstance(ColorSpace.CS_GRAY)) || cs.getClass().getSimpleName().equals("ScaledColorSpace")) {
            // return 0 or 1
            // return 0 for min is white
            // return 1 for min is black
            return PHOTOMETRIC_MINISBLACK;
        } else if (cm instanceof IndexColorModel) {
            return PHOTOMETRIC_PALETTE;
        } else if (cs.isCS_sRGB()) {
            return PHOTOMETRIC_RGB;
        } else {
            throw new IllegalStateException("unknow photometricInterpretation : unknow color model type.");
        }
    }
    /**
     * Define photometric interpretation tiff tag in function of {@link ColorModel} properties.
     *
     * @param cm image color model.
     * @return 1 for gray, 2 for RGB, 3 for indexed colormodel.
     */
    public static PhotometricInterpretation getEnumPhotometricInterpretation(final ColorModel cm) {
        return PhotometricInterpretation.valueOf(getPhotometricInterpretation(cm));
    }

    /**
     * Define appropriate tiff tag value for planar configuration from {@link SampleModel} properties.
     *
     * @param sm needed {@link SampleModel} to define planar configuration.
     * @return 1 for pixel interleaved or 2 for band interleaved.
     * @see #PLANAR_INTERLEAVED
     * @see #PLANAR_BANDED
     */
    public static short getPlanarConfiguration(final SampleModel sm) {
        final int numband  = sm.getNumBands();
        if (numband > 1 && sm instanceof ComponentSampleModel) {
            final ComponentSampleModel csm = (ComponentSampleModel) sm;
            final int[] bankIndice = csm.getBankIndices();
            if (csm.getPixelStride() != 1 || bankIndice.length == 1) return PLANAR_INTERLEAVED;
            int b = 0;
            while (++b < bankIndice.length) if (bankIndice[b] == bankIndice[b - 1]) return PLANAR_INTERLEAVED;
            return PLANAR_BANDED;
        }
        return PLANAR_INTERLEAVED;
    }

    /**
     * Define appropriate {@link PlanarConfiguration} enum from its integer planarConfiguration into tiff specification.
     *
     * @param sm needed {@link SampleModel} to define planar configuration.
     * @return {@link PlanarConfiguration#INTERLEAVED} for an interleaved sampleModel or {@link PlanarConfiguration#BANDED}.
     * @see #PLANAR_INTERLEAVED
     * @see #PLANAR_BANDED
     * @see #getPlanarConfiguration(java.awt.image.SampleModel)
     */
    public static PlanarConfiguration getEnumPlanarConfiguration(final SampleModel sm) {
        return PlanarConfiguration.valueOf(getPlanarConfiguration(sm));
    }

    /**
     * Define appropriate tiff tag value for planar configuration from {@link SampleModel} properties.
     *
     * @param sm needed {@link SampleModel} to define planar configuration.
     * @return 1 for integer format, 2 for integer signed format or 3 for floating format (IEEE).
     * @see #SAMPLEFORMAT_UINT
     * @see #SAMPLEFORMAT_INT
     * @see #SAMPLEFORMAT_IEEEFP
     */
    public static int getSampleFormat(final SampleModel sm) {

        //-- bitpersamples
        final int[] sampleSize = sm.getSampleSize();// sample size in bits
        int samplePerPixel = sampleSize.length;
        assert samplePerPixel == sm.getNumBands() : "samplePerPixel = "+samplePerPixel+". sm.getnumDataElement = "+sm.getNumBands();

        for (int i = 1; i < samplePerPixel; i++) {
            if (sampleSize[i-1] != sampleSize[i]) {
                throw new IllegalStateException("different sample size is not supported in tiff format.");
            }
        }
        assert sampleSize[0] <= 0xFFFF : "BitsPerSample exceed short max value";

        final int dataType = sm.getDataType();
        switch (dataType) {
            case DataBuffer.TYPE_FLOAT  :
            case DataBuffer.TYPE_DOUBLE : {
                return SAMPLEFORMAT_IEEEFP; //-- type floating point --//
            }
            case DataBuffer.TYPE_BYTE  :
            case DataBuffer.TYPE_SHORT :
            case DataBuffer.TYPE_INT   : {
                return SAMPLEFORMAT_INT; //-- type signed 8, 16, 32 bits Int --//
            }
            default : { return SAMPLEFORMAT_UINT;} //-- type UInt or UShort--//
        }
    }

    /**
     * Return an appropriate {@link ImageTypeSpecifier} built from given parameter.
     *
     * @param sampleBitsSize bit size for each sample.
     * @param numBand expected band number
     * @param photometricInterpretation
     * @param sampleFormat
     * @param planarConfiguration define planar configuration of asked {@link ImageTypeSpecifier}, 1 for interveaved 2 for banded sampleModel.
     * @param hasAlpha
     * @param isAlphaPremultiplied
     * @param java2DColorMap
     * @return {@link ImageTypeSpecifier}.
     */
    public static ImageTypeSpecifier buildImageTypeSpecifier(final int sampleBitsSize, final int numBand,
                                                             final short photometricInterpretation,
                                                             final short sampleFormat,
                                                             final short planarConfiguration,
                                                             final boolean hasAlpha, final boolean isAlphaPremultiplied,
                                                             final int[] java2DColorMap)
                                                             throws UnsupportedOperationException {
        final ColorModel cm = createColorModel(sampleBitsSize, numBand,
                                                photometricInterpretation, sampleFormat,
                                                hasAlpha, isAlphaPremultiplied,
                                                java2DColorMap);
        final SampleModel sm = ImageUtils.createSampleModel(PlanarConfiguration.valueOf(planarConfiguration),
                                                            SampleType.valueOf(sampleBitsSize, sampleFormat),
                                                            1, 1, numBand);
        return new ImageTypeSpecifier(cm, sm);
    }

    /**
     * Create and returns appropriate {@link SampleModel} built from given parameters.<br><br>
     *
     * Note : 2 kind of {@link SampleModel} may be returned with differents internales configurations.<br>
     * 1 - {@link PixelInterleavedSampleModel} : <br>
     * pixelstride = numband<br>
     * scanlineStride = width * pixelstride.<br><br>
     * 2 - {@link BandedSampleModel} : <br>
     * scanlineStride = width
     *
     * @param planarConfiguration define planar configuration of asked {@link SampleModel},
     * 1 for {@link PixelInterleavedSampleModel} and 2 for {@link BandedSampleModel}
     * @param sampleType Data type of internal image raster.
     * @param width image width
     * @param height image height
     * @param numBand image numband
     * @return {@link ImageTypeSpecifier}.
     * @see #PLANAR_INTERLEAVED
     * @see #PLANAR_BANDED
     */
    public static SampleModel createSampleModel(final PlanarConfiguration planarConfiguration, final SampleType sampleType,
                                                final int width, final int height, final int numBand)
            throws UnsupportedOperationException {

        final int[] bandOff = new int[numBand];
        switch (planarConfiguration) {
            case INTERLEAVED : {
                int b = -1;
                while (++b < numBand) bandOff[b] = b;
                return new PixelInterleavedSampleModel(SampleType.valueOf(sampleType), width, height, numBand, numBand * width, bandOff);
            }
            case BANDED : {
                final int[] bankIndices = new int[numBand];
                int b = -1;
                while (++b < numBand) bankIndices[b] = b;

                return new BandedSampleModel(SampleType.valueOf(sampleType), width, height, width, bankIndices, bandOff);
            }
            default : throw new IllegalArgumentException("unknow planarConfiguration type. Expected 1 for interleaved or 2 for banded. Found : "+planarConfiguration);
        }
    }

    /**
     * Create and returns an adapted {@link ColorModel} from given parameters.
     *
     * @param sampleBitsSize
     * @param numBand
     * @param photometricInterpretation
     * @param sampleFormat
     * @param hasAlpha
     * @param isAlphaPremultiplied
     * @param java2DColorMap associate color map array in case where a palette color model is define.
     * @return an adapted {@link ColorModel} from given parameters.
     * @throws IllegalArgumentException if photometric interpretation is define
     * as palette (photometricInterpretation == 3) and colorMap is {@code null}.
     */
    public static ColorModel createColorModel(final int sampleBitsSize, final int numBand,
                                              final short photometricInterpretation, final short sampleFormat,
                                              final boolean hasAlpha, final boolean isAlphaPremultiplied,
                                              final int[] java2DColorMap)
            throws UnsupportedOperationException {
        return createColorModel(sampleBitsSize, numBand,
                                photometricInterpretation, sampleFormat,
                                null, null,
                                hasAlpha, isAlphaPremultiplied,
                                java2DColorMap);
    }

    /**
     * Create and returns an adapted {@link ColorModel} from given parameters.
     *
     * @param sampleBitsSize
     * @param numBand
     * @param photometricInterpretation
     * @param sampleFormat
     * @param minSampleValue minimum raster sample value to build needed {@link ColorSpace},
     * may be {@code null}, if null the default choosen value will be {@link Double#MIN_VALUE}.
     * @param maxSampleValue maximum raster sample value to build needed {@link ColorSpace},
     * may be {@code null}, if null the default choosen value will be {@link Double#MAX_VALUE}.
     * @param hasAlpha define if the color has a alpha component.
     * @param isAlphaPreMultiplied define if the alpha has been premultiplied in the pixel values.
     * @param java2DColorMap associate color map array in case where a palette color model is define.
     * @return an adapted {@link ColorModel} from given parameters.
     * @throws IllegalArgumentException if photometric interpretation is define
     * as palette (photometricInterpretation == 3) and colorMap is {@code null}.
     * @throws IllegalArgumentException if minSampleValue or maxSampleValue is(are) equals to {@link Double#NaN}.
     * @see ScaledColorSpace
     */
    public static ColorModel createColorModel(final int sampleBitsSize, final int numBand,
                                              final short photometricInterpretation, final short sampleFormat,
                                              final Double minSampleValue, final Double maxSampleValue,
                                              final boolean hasAlpha, final boolean isAlphaPreMultiplied,
                                              /*final long[] geotiffColorMap,*/ int[] java2DColorMap)
                                              throws UnsupportedOperationException {
        if (minSampleValue != null) {
            if (Double.isNaN(minSampleValue))
                throw new IllegalArgumentException("invalid minimum raster sample value, it should not be Double.NAN");
        }

        if (maxSampleValue != null) {
            if (Double.isNaN(maxSampleValue))
                throw new IllegalArgumentException("invalid minimum raster sample value, it should not be Double.NAN");
        }

        final int dataBufferType;

        if (sampleFormat == 3) {
            /*
             * Case to defferency 32 bits Float to 32 bits Integer.
             */
            switch (sampleBitsSize) {
                case Float.SIZE  : dataBufferType = DataBuffer.TYPE_FLOAT; break;
                case Double.SIZE : dataBufferType = DataBuffer.TYPE_DOUBLE; break;
                default : {
                    throw new UnsupportedOperationException( "unsupported bitsPerSample size : "+sampleBitsSize);
                }
            }
        } else {

           /*
            * We require exact value, because the reading process read all sample values
            * in one contiguous read operation.
            */
           switch (sampleBitsSize) {
               case java.lang.Byte.SIZE  : dataBufferType = DataBuffer.TYPE_BYTE;   break;
               case java.lang.Short.SIZE : dataBufferType = (sampleFormat == SAMPLEFORMAT_INT)
                                                            ? DataBuffer.TYPE_SHORT
                                                            : DataBuffer.TYPE_USHORT; break;
               case Integer.SIZE         : dataBufferType = DataBuffer.TYPE_INT;    break;
               case Double.SIZE          : dataBufferType = DataBuffer.TYPE_DOUBLE; break;
               default : {
                    throw new UnsupportedOperationException( "unsupported bitsPerSample size : "+sampleBitsSize);
               }
           }
        }

        final ColorSpace cs;
        switch (photometricInterpretation) {
            case 0 :   //--minIsWhite
            case 1 : { //-- minIsBlack
                if (numBand > 1
                 || dataBufferType == DataBuffer.TYPE_FLOAT
                 || dataBufferType == DataBuffer.TYPE_DOUBLE) {
                    final double minCs = (minSampleValue != null) ? minSampleValue : Double.MIN_VALUE;
                    final double maxCs = (maxSampleValue != null) ? maxSampleValue : Double.MAX_VALUE;
                    cs = ColorModelFactory.createColorSpace(numBand, 0, minCs, maxCs);//-- attention au choix de la bande !!!!
                } else {
                    cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                }
                break;
            }
            case 2 : { //-- RGB
                cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                break;
            }
            case 3 : {//-- palette
                if (java2DColorMap == null)
                     throw new IllegalArgumentException("Impossible to build palette color model with null color map array.");

                final ColorModel cm = new IndexColorModel(sampleBitsSize, java2DColorMap.length, java2DColorMap, 0, true, -1, dataBufferType);
                /*
                 * Create a SampleModel with size of 1x1 volontary just to know image properties.
                 * Image with correctively size will be create later with getDestination() in #read(int index, param) method.
                 */
                return cm;
            }
            default : {
                throw new UnsupportedOperationException( "photometricInterpretation : "+photometricInterpretation);
            }
        }

        final int[] bits = new int[numBand];
        Arrays.fill(bits, sampleBitsSize);

        return new ComponentScaledColorModel(cs, bits, hasAlpha, isAlphaPreMultiplied,
                hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE, dataBufferType);
    }

    static class ComponentScaledColorModel extends ComponentColorModel {

        private final int numbands;
        private final int[] bits;

        public ComponentScaledColorModel(ColorSpace colorSpace, int[] bits, boolean hasAlpha,
                boolean isAlphaPremultiplied, int transparency, int transferType) {
            super(colorSpace, bits, hasAlpha, isAlphaPremultiplied, transparency, transferType);
            numbands = bits.length;
            this.bits = bits;
        }

        @Override
        public boolean isCompatibleSampleModel(SampleModel sm) {
            return ((sm instanceof ComponentSampleModel)
                 && (numbands == sm.getNumBands())
                 && (sm.getTransferType() == transferType));
        }

        @Override
        public SampleModel createCompatibleSampleModel(int w, int h) {
            return createSampleModel(INTERLEAVED, SampleType.valueOf(transferType), w, h, numbands);
        }

        @Override
        public boolean isCompatibleRaster(Raster raster) {
            final SampleModel sm = raster.getSampleModel();
            if (!(sm instanceof ComponentSampleModel))
                return false;
            for (int i = 0; i < bits.length; i++) {
                if (sm.getSampleSize(i) < bits[i]) return false;
            }
            return (raster.getTransferType() == transferType);
        }

        @Override
        public int getNumComponents() {
            return numbands;
        }
    }
}
