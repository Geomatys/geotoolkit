/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.coverage;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.collection.Containers;
import org.geotoolkit.internal.coverage.ColoredCategory;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.util.InternationalString;


/**
 * Utilities class for {@link SampleDimension}, {@link Category}  working.
 *
 * @author Remi Marechal (Geomatys).
 */
public final strictfp class SampleDimensionUtils {
    private SampleDimensionUtils() {
    }

    /**
     * Used nodata {@link Category} name.
     * @see Category#getName()
     */
    public static InternationalString NODATA_CATEGORY_NAME = Vocabulary.formatInternational(Vocabulary.Keys.Nodata);

    /**
     * Returns a sequence of category names for the values contained in the sample dimension.
     * This allows for names to be assigned to numerical values. The first entry in the sequence
     * relates to a cell value of zero. For example:
     *
     * {@preformat text
     *    [0] Background
     *    [1] Water
     *    [2] Forest
     *    [3] Urban
     * }
     *
     * @return The sequence of category names for the values contained in this sample dimension,
     *         or {@code null} if there is no category in this sample dimension.
     */
    public static InternationalString[] getCategoryNames(final SampleDimension band) {
        final List<Category> categories = band.getCategories();
        InternationalString[] names = null;
        for (int i=categories.size(); --i>=0;) {
            final Category category = categories.get(i);
            final NumberRange<?> range = category.getSampleRange();
            final Number minimum = range.getMinValue();
            final Number maximum = range.getMaxValue();
            final int lower = minimum.intValue();
            final int upper = maximum.intValue();
            if (lower != minimum.doubleValue() || lower < 0 ||
                upper != maximum.doubleValue() || upper < 0)
            {
                throw new IllegalStateException(Errors.format(Errors.Keys.NonIntegerCategory));
            }
            if (names == null) {
                names = new InternationalString[upper+1];
            }
            Arrays.fill(names, lower, upper+1, category.getName());
        }
        return names;
    }

    public static double[] getNoDataValues(final SampleDimension band) {
        final Set<Number> noDataValues = band.getNoDataValues();
        final double[] nf = new double[noDataValues.size()];
        int i = 0;
        for (final Number n : noDataValues) {
            nf[i++] = n.doubleValue();
        }
        return nf;
    }

    /**
     * Returns the minimum value occurring in this sample dimension (inclusive). If the
     * minimum value can't be computed, then this method returns {@link Double#NEGATIVE_INFINITY}.
     */
    public static double getMinimumValue(final SampleDimension band) {
        final List<Category> categories = band.getCategories();
        if (!Containers.isNullOrEmpty(categories)) {
            final double value = categories.get(0).getSampleRange().getMinDouble();
            if (!Double.isNaN(value)) {
                return value;
            }
        }
        return Double.NEGATIVE_INFINITY;
    }

    /**
     * Returns the maximum value occurring in this sample dimension (inclusive). If the
     * maximum value can't be computed, then this method returns {@link Double#POSITIVE_INFINITY}.
     */
    public static double getMaximumValue(final SampleDimension band) {
        final List<Category> categories = band.getCategories();
        if (categories != null) {
            for (int i=categories.size(); --i>=0;) {
                final double value = categories.get(i).getSampleRange().getMaxDouble();
                if (!Double.isNaN(value)) {
                    return value;
                }
            }
        }
        return Double.POSITIVE_INFINITY;
    }

    public static boolean isRangeSigned(final SampleDimension band) {
        return getMinimumValue(band) < 0;
    }

    @Deprecated
    public static SampleDimensionType getSampleDimensionType(final SampleDimension band) {
        if (band != null) {
            Optional<NumberRange<?>> r = band.getSampleRange();
            if (r.isPresent()) {
                return TypeMap.getSampleDimensionType(r.get());
            }
        }
        return null;
    }

    /**
     * Returns a color model for this category list. This method builds up the color model
     * from each category's colors (as returned by {@link Category#getColors}).
     *
     * @param  visibleBand The band to be made visible (usually 0). All other bands, if any
     *         will be ignored.
     * @param  numBands The number of bands for the color model (usually 1). The returned color
     *         model will renderer only the {@code visibleBand} and ignore the others, but
     *         the existence of all {@code numBands} will be at least tolerated. Supplemental
     *         bands, even invisible, are useful for processing with Java Advanced Imaging.
     * @return The requested color model, suitable for {@link java.awt.image.RenderedImage}
     *         objects with values in the <code>{@linkplain #getRange}</code> range.
     */
    public static ColorModel getColorModel(final SampleDimension band, final int visibleBand, final int numBands) {
        final List<Category> categories = band.getCategories();
        int type = DataBuffer.TYPE_FLOAT;
        if (!categories.isEmpty()) {
            final NumberRange<?> range = categories.get(0).getSampleRange();
            final Class<?> rt = range.getElementType();
            if (rt == Byte.class || rt == Short.class || rt == Integer.class) {
                final int min = range.getMinValue().intValue();
                final int max = (int) getMaximumValue(band);
                if (min >= 0) {
                    if (max < 0x100) {
                        type = DataBuffer.TYPE_BYTE;
                    } else if (max < 0x10000) {
                        type = DataBuffer.TYPE_USHORT;
                    } else {
                        type = DataBuffer.TYPE_INT;
                    }
                } else if (min >= Short.MIN_VALUE && max <= Short.MAX_VALUE) {
                    type = DataBuffer.TYPE_SHORT;
                } else {
                    type = DataBuffer.TYPE_INT;
                }
            }
        }
        return getColorModel(band, visibleBand, numBands, type);
    }

    /**
     * Returns a color model for this category list. This method builds up the color model
     * from each category's colors (as returned by {@link ColoredCategory#getColors}).
     *
     * @param  visibleBand The band to be made visible (usually 0). All other bands, if any
     *         will be ignored.
     * @param  numBands The number of bands for the color model (usually 1). The returned color
     *         model will renderer only the {@code visibleBand} and ignore the others, but
     *         the existence of all {@code numBands} will be at least tolerated. Supplemental
     *         bands, even invisible, are useful for processing with Java Advanced Imaging.
     * @param  type The transfer type used in the sample model.
     * @return The requested color model, suitable for {@link java.awt.image.RenderedImage}
     *         objects with values in the <code>{@link #getRange}</code> range.
     */
    public static ColorModel getColorModel(final SampleDimension band, final int visibleBand, final int numBands, final int type) {
        final List<Category> categories = band.forConvertedValues(false).getCategories();
        final Map<NumberRange<?>, Color[]> ranges = new LinkedHashMap<>();
        for (final Category category : categories) {
            ranges.put(category.getSampleRange(), ColoredCategory.getColors(category));
        }
        return org.apache.sis.internal.raster.ColorModelFactory.createColorModel(ranges, visibleBand, numBands, type);
    }

    public static boolean hasQualitative(final SampleDimension band) {
        for (final Category c : band.getCategories()) {
            if (!c.isQuantitative()) return true;
        }
        return false;
    }

    public static boolean isGeophysics(final SampleDimension dimension) {
        Optional<MathTransform1D> f = dimension.getTransferFunction();
        return f.isPresent() && f.get().isIdentity();
    }
}
