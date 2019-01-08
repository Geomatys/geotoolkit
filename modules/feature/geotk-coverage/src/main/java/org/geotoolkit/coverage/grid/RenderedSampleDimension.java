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
package org.geotoolkit.coverage.grid;

import java.util.Arrays;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.Serializable;
import javax.measure.Unit;

import org.apache.sis.measure.NumberRange;
import org.apache.sis.coverage.SampleDimension;
import org.geotoolkit.coverage.SampleDimensionBuilder;
import org.geotoolkit.coverage.SampleDimensionUtils;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.coverage.TypeMap;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;

import org.geotoolkit.coverage.SampleDimensionType;


/**
 * Describes the band values for a grid coverage.
 *
 * @deprecated To be removed.
 */
@Deprecated
final class RenderedSampleDimension implements Serializable {
    final SampleDimension dimension;

    /**
     * Band number for this sample dimension.
     */
    private final int band;

    /**
     * The number of bands in the {@link GridCoverage} who own this sample dimension.
     */
    private final int numBands;

    /**
     * The grid value data type.
     */
    final SampleDimensionType type;

    /**
     * Constructs a sample dimension with a set of categories from an other sample dimension.
     *
     * @param band  The originating sample dimension.
     * @param model The sample model of the image to be wrapped by {@link GridCoverage}.
     * @param bandNumber The band number.
     */
    private RenderedSampleDimension(final SampleDimension band, final SampleModel model, final int bandNumber) {
        this.dimension = band;
        this.band     = bandNumber;
        this.numBands = model.getNumBands();
        this.type     = TypeMap.getSampleDimensionType(model, bandNumber);
    }

    /**
     * Creates a set of sample dimensions for the given image. The array length of both
     * arguments must matches the number of bands in the supplied {@code image}.
     *
     * @param  name   The name for data (e.g. "Elevation"), or {@code null} if none.
     * @param  image  The image for which to create a set of sample dimensions, or {@code null}.
     * @param  src    User-provided sample dimensions, or {@code null} if none.
     * @param  dst    The array where to put sample dimensions.
     * @return {@code true} if all sample dimensions are geophysics (quantitative), or
     *         {@code false} if all sample dimensions are non-geophysics (qualitative).
     * @throws IllegalArgumentException if geophysics and non-geophysics dimensions are mixed.
     */
    static boolean create(final CharSequence  name,
                          final RenderedImage image,
                          final SampleDimension[] src,
                          final RenderedSampleDimension[] dst)
    {
        final SampleModel model = image.getSampleModel();
        final int numBands = model.getNumBands();
        if (src != null && src.length != numBands) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.MismatchedNumberOfBands_3,
                    numBands, src.length, "SampleDimension"));
        }
        if (dst.length != numBands) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.MismatchedNumberOfBands_3,
                    numBands, dst.length, "SampleDimension"));
        }
        /*
         * Now, we know that the number of bands and the array length are consistent.
         * Search if there is any null SampleDimension. If any, replace the null value
         * by a default SampleDimension. In all cases, count the number of geophysics
         * and non-geophysics sample dimensions.
         */
        int countGeophysics = 0;
        int countIndexed    = 0;
        SampleDimension[] defaultSD = null;
        for (int i=0; i<numBands; i++) {
            SampleDimension sd = (src!=null) ? src[i] : null;
            if (sd == null) {
                /*
                 * If the user didn't provided explicitly a SampleDimension, create a default one.
                 * We will creates a SampleDimension for all bands in one step, even if only a few
                 * of them are required.
                 */
                if (defaultSD == null) {
                    defaultSD = new SampleDimension[numBands];
                    CharSequence[] names = null;
                    if (name != null) {
                        names = new CharSequence[numBands];
                        Arrays.fill(names, name);
                    }
                    create(names, PixelIteratorFactory.createDefaultIterator(image),
                            model, null, null, null, null, defaultSD, null);
                }
                sd = defaultSD[i];
            }
            RenderedSampleDimension rs = new RenderedSampleDimension(sd, model, i);
            dst[i] = rs;
            /*
             * We use a equality test and not == because in some cases
             * the inverse sample dimension can be the original sample dimension
             * inverse, which is a GridSample dimension and not a RenderedSampleDimension.
             */
            if (sd.forConvertedValues(true ).equals(sd)) countGeophysics++;
            if (sd.forConvertedValues(false).equals(sd)) countIndexed++;
        }
        if (countGeophysics == numBands) {
            return true;
        }
        if (countIndexed == numBands) {
            return false;
        }
        throw new IllegalArgumentException(Errors.format(Errors.Keys.MixedCategories));
    }

    /**
     * Creates a set of sample dimensions for the given rendered image.
     *
     * @param  names The name for each bands (e.g. "Elevation"), or {@code null} if none.
     * @param  image The rendered image.
     * @param  min The minimal value for each bands, or {@code null} for computing it automatically.
     * @param  max The maximal value for each bands, or {@code null} for computing it automatically.
     * @param  units The units of sample values, or {@code null} if unknown.
     * @param  colors The colors to use for values from {@code min} to {@code max} for each
     *         bands, or {@code null} for a default color palette. If non-null, each arrays
     *         {@code colors[b]} may have any length; colors will be interpolated as needed.
     * @param  hints An optional set of rendering hints, or {@code null} if none. Those hints will
     *         not affect the sample dimensions to be created. However, they may affect the sample
     *         dimensions to be returned by <code>{@link #geophysics geophysics}(false)</code>, i.e.
     *         the view to be used at rendering time. The optional hint
     *         {@link Hints#SAMPLE_DIMENSION_TYPE} specifies the {@link SampleDimensionType}
     *         to be used at rendering time, which can be one of
     *         {@link SampleDimensionType#UBYTE UBYTE} or
     *         {@link SampleDimensionType#USHORT USHORT}.
     * @return The sample dimension for the given image.
     */
    static SampleDimension[] create(final CharSequence[] names,
                                    final RenderedImage  image,
                                    final double[]       min,
                                    final double[]       max,
                                    final Unit<?>[]      units,
                                    final Color[][]      colors,
                                    final RenderingHints hints)
    {
        final SampleModel model = image.getSampleModel();
        final SampleDimension[] dst = new SampleDimension[model.getNumBands()];
        create(names, (min == null || max == null) ? PixelIteratorFactory.createDefaultIterator(image) : null,
               model, min, max, units, colors, dst, hints);
        return dst;
    }

    /**
     * Creates a set of sample dimensions for the given raster.
     *
     * @param  names The name for each bands (e.g. "Elevation"), or {@code null} if none.
     * @param  raster The raster.
     * @param  min The minimal value for each bands, or {@code null} for computing it automatically.
     * @param  max The maximal value for each bands, or {@code null} for computing it automatically.
     * @param  units The units of sample values, or {@code null} if unknown.
     * @param  colors The colors to use for values from {@code min} to {@code max} for each
     *         bands, or {@code null} for a default color palette. If non-null, each arrays
     *         {@code colors[b]} may have any length; colors will be interpolated as needed.
     * @param  hints An optional set of rendering hints, or {@code null} if none. Those hints will
     *         not affect the sample dimensions to be created. However, they may affect the sample
     *         dimensions to be returned by <code>{@link #geophysics geophysics}(false)</code>, i.e.
     *         the view to be used at rendering time. The optional hint
     *         {@link Hints#SAMPLE_DIMENSION_TYPE} specifies the {@link SampleDimensionType}
     *         to be used at rendering time, which can be one of
     *         {@link SampleDimensionType#UBYTE UBYTE} or
     *         {@link SampleDimensionType#USHORT USHORT}.
     * @return The sample dimension for the given raster.
     */
    static SampleDimension[] create(final CharSequence[] names,
                                    final Raster         raster,
                                    final double[]       min,
                                    final double[]       max,
                                    final Unit<?>[]      units,
                                    final Color[][]      colors,
                                    final RenderingHints hints)
    {
        final SampleDimension[] dst = new SampleDimension[raster.getNumBands()];
        create(names, (min == null || max == null) ? PixelIteratorFactory.createDefaultIterator(raster) : null,
               raster.getSampleModel(), min, max, units, colors, dst, hints);
        return dst;
    }

    /**
     * Creates a set of sample dimensions for the data backing the given iterator.
     *
     * @param  names The name for each band (e.g. "Elevation"), or {@code null} if none.
     * @param  iterator The iterator through the raster data, or {@code null}.
     * @param  model The image or raster sample model.
     * @param  min The minimal value, or {@code null} for computing it automatically.
     * @param  max The maximal value, or {@code null} for computing it automatically.
     * @param  units The units of sample values, or {@code null} if unknown.
     * @param  colors The colors to use for values from {@code min} to {@code max} for each bands,
     *         or {@code null} for a default color palette. If non-null, each arrays
     *         {@code colors[b]} may have any length; colors will be interpolated as needed.
     * @param  dst The array where to store sample dimensions. The array length must matches
     *         the number of bands.
     * @param  hints An optional set of rendering hints, or {@code null} if none.
     *         Those hints will not affect the sample dimensions to be created. However,
     *         they may affect the sample dimensions to be returned by
     *         <code>{@link #geophysics geophysics}(false)</code>, i.e.
     *         the view to be used at rendering time. The optional hint
     *         {@link Hints#SAMPLE_DIMENSION_TYPE} specifies the {@link SampleDimensionType}
     *         to be used at rendering time, which can be one of
     *         {@link SampleDimensionType#UBYTE UBYTE} or
     *         {@link SampleDimensionType#USHORT USHORT}.
     */
    private static void create(final CharSequence[]        names,
                               final PixelIterator         iterator,
                               final SampleModel           model,
                               double[]                    min,
                               double[]                    max,
                               final Unit<?>[]             units,
                               final Color[][]             colors,
                               final SampleDimension[] dst,
                               final RenderingHints        hints)
    {
        final int numBands = dst.length;
        if (min != null && min.length != numBands) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.MismatchedNumberOfBands_3,
                    numBands, min.length, "min[i]"));
        }
        if (max != null && max.length != numBands) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.MismatchedNumberOfBands_3,
                    numBands, max.length, "max[i]"));
        }
        if (colors != null && colors.length != numBands) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.MismatchedNumberOfBands_3,
                    numBands, colors.length, "colors[i]"));
        }
        /*
         * Arguments are known to be valid. We now need to compute two ranges:
         *
         * STEP 1: Range of target (sample) values. This is computed in the following block.
         * STEP 2: Range of source (geophysics) values. It will be computed one block later.
         *
         * The target (sample) values will typically range from 0 to 255 or 0 to 65535, but the
         * general case is handled as well. If the source (geophysics) raster uses floating point
         * numbers, then a "nodata" category may be added in order to handle NaN values. If the
         * source raster use integer numbers instead, then we will rescale samples only if they
         * would not fit in the target data type.
         */
        boolean addNoData = false;
        final SampleDimensionType sourceType = TypeMap.getSampleDimensionType(model, 0);
        final boolean          sourceIsFloat = TypeMap.isFloatingPoint(sourceType);
        SampleDimensionType targetType = null;
        if (hints != null) {
            targetType = (SampleDimensionType) hints.get(Hints.SAMPLE_DIMENSION_TYPE);
        }
        if (targetType == null) {
            // Default to TYPE_BYTE for floating point images only; otherwise keep unchanged.
            targetType = sourceIsFloat ? SampleDimensionType.UNSIGNED_8BITS : sourceType;
        }
        // Default setting: no scaling
        final boolean  targetIsFloat = TypeMap.isFloatingPoint(targetType);
        NumberRange<?> targetRange   = TypeMap.getRange(targetType);
        final boolean needScaling;
        if (targetIsFloat) {
            // Never rescale if the target is floating point numbers.
            needScaling = false;
        } else if (sourceIsFloat) {
            // Always rescale for "float to integer" conversions. In addition,
            // Use 0 value as a "no data" category for unsigned data type only.
            needScaling = true;
            if (!TypeMap.isSigned(targetType)) {
                addNoData = true;
                targetRange = TypeMap.getPositiveRange(targetType);
            }
        } else {
            // In "integer to integer" conversions, rescale only if
            // the target range is smaller than the source range.
            needScaling = !targetRange.containsAny(TypeMap.getRange(sourceType));
        }
        /*
         * Computes the minimal and maximal values, if not explicitly provided.
         * This information is required for determining the range of geophysics
         * values.
         */
        if (needScaling && (min == null || max == null)) {
            final boolean computeMin;
            final boolean computeMax;
            if (computeMin = (min == null)) {
                min = new double[numBands];
                Arrays.fill(min, Double.POSITIVE_INFINITY);
            }
            if (computeMax = (max == null)) {
                max = new double[numBands];
                Arrays.fill(max, Double.NEGATIVE_INFINITY);
            }
            int b = 0;
            while (iterator.next()) {
                final double z = iterator.getSampleDouble();
                if (computeMin && z < min[b]) min[b] = z;
                if (computeMax && z > max[b]) max[b] = z;
                if (computeMin && computeMax) {
                    if (!(min[b] < max[b])) {
                        min[b] = 0;
                        max[b] = 1;
                    }
                }
                if (++b == numBands) b = 0;
            }
        }
        /*
         * Now, constructs the sample dimensions. We will unconditionally provides a "nodata"
         * category for floating point images targeting unsigned integers, since we don't know
         * if the user plan to have NaN values. Even if the current image doesn't have NaN values,
         * it could have NaN later if the image uses a writable raster.
         */
        final SampleDimensionBuilder builder = new SampleDimensionBuilder();
        CharSequence untitled = null;
        for (int b=0; b<numBands; b++) {
            if (addNoData) {
                builder.addQualitative(null, 0);
            }
            CharSequence name = (names != null) ? names[b] : null;
            if (name == null) {
                if (untitled == null) {
                    untitled = Vocabulary.formatInternational(Vocabulary.Keys.Untitled);
                }
                name = untitled;
                if (numBands != 1) {
                    name = Vocabulary.formatInternational(Vocabulary.Keys.Hyphen_2, name, (b+1));
                }
            }
            NumberRange<?> sourceRange = TypeMap.getRange(sourceType);
            final Color[] c = (colors != null) ? colors[b] : null;
            if (needScaling) {
                final NumberRange<Double> range = NumberRange.create(min[b], true, max[b], true);
                sourceRange = range.castTo((Class) sourceRange.getElementType());   // TODO
                builder.addQuantitative(name, targetRange, sourceRange);
                builder.setLastCategoryColors(c);
            }
            builder.setName(name);
            dst[b] = builder.build().forConvertedValues(true);
            builder.clear();
        }
    }

    /**
     * Returns a color model for this sample dimension.
     */
    public ColorModel getColorModel() {
        if (SampleDimensionUtils.isGeophysics(dimension) && SampleDimensionUtils.hasQualitative(dimension)) {
            // Data likely to have NaN values, which require a floating point type.
            return SampleDimensionUtils.getColorModel(dimension, band, numBands, DataBuffer.TYPE_FLOAT);
        }
        return SampleDimensionUtils.getColorModel(dimension, band, numBands);
    }
}
