/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.List;
import java.util.stream.Collectors;
import org.opengis.util.InternationalString;
import org.opengis.referencing.operation.MathTransform1D;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.math.MathFunctions;


/**
 * Information about a {@link SampleDimension} to be inserted in the database.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
final class SampleDimensionEntry extends Entry {
    /**
     * Default range of values used if the sample dimensions does not define any transfer function.
     * This is the range of "packed values" stored as integers, together with an arbitrary transfer
     * function from integer values to real values computed by {@code defaultCategories(…)} method.
     *
     * @see #defaultCategories(List, MeasurementRange)
     */
    private static final int DEFAULT_RANGE = 10000;

    /**
     * The sample dimension to write.
     */
    final SampleDimension band;

    /**
     * {@code true} if the sample dimension is for real values, or {@code false} if for packed values.
     */
    final boolean isReal;

    /**
     * Category to write in the database. May be different than {@link SampleDimension#getCategories()}
     * if default categories are used.
     */
    List<Category> categories;

    /**
     * The value to use as background.
     */
    Number background;

    /**
     * Whether the sample dimension categories have been replaced by default categories.
     */
    private boolean approximate;

    /**
     * Prepares an entry to be inserted in the database.
     */
    private SampleDimensionEntry(final SampleDimension band) {
        this.band = band;
        isReal = isReal(band);
        final SampleDimension packed = band.forConvertedValues(false);
        categories = packed.getCategories();
        if (isReal && band == packed) {
            /*
             * If the SampleDimension describes values that are already converted and does not describe
             * any way to encode the data as integer, we have to invent or own packaging here because
             * this is the way we encode the information in the database.
             */
            band.getMeasurementRange().ifPresent(range -> {
                approximate = true;
                categories = defaultCategories(categories, range);
            });
        }
    }

    /**
     * Wraps sample dimensions as {@code SampleDimensionEntry}.
     */
    static List<SampleDimensionEntry> wrap(final List<SampleDimension> bands) {
        if (bands == null) return null;
        return bands.stream().map(SampleDimensionEntry::new).collect(Collectors.toList());
    }

    /**
     * Returns {@code true} if the following sample dimension is for real values,
     * or {@code false} if for packed values.
     */
    static boolean isReal(final SampleDimension band) {
        return band.getTransferFunction().map(MathTransform1D::isIdentity).orElse(false);
    }

    /**
     * Creates a new list of categories for real values in the given range.  The returned categories use
     * an arbitrary packing. This method is invoked when the sample dimensions to insert in the database
     * declare only real values, without transfer function.  Since our database expect integer values to
     * be converted to real value using  transfer function, we have to invent our own. We insert rounded
     * numbers because the minimum and maximum values in that situation are often determined by scanning
     * the data, in which case every raster would produce different minimum and maximum. Since we want a
     * sample dimension that fit for all data of the same kind, we expand the value range for increasing
     * the chance that the categories would be reusable for other files of the same series.
     */
    private static List<Category> defaultCategories(final List<Category> original, final MeasurementRange<?> range) {
        final SampleDimension.Builder b = new SampleDimension.Builder();
        InternationalString name = null;
        int padValue = 0;
        for (final Category c : original) {
            if (!c.getTransferFunction().isPresent()) {
                b.addQualitative(c.getName(), padValue++);
            } else if (name == null) {
                name = c.getName();
            }
        }
        if (padValue == 0) {
            b.setBackground(null, padValue++);
        }
        double min = range.getMinDouble();
        double max = range.getMaxDouble();
        /*
         * The minimum and maximum values may have been extracted from the actual data, in which case
         * they are likely to differ for each files. But we need more stable values in order to avoid
         * generating new SampleDimension entries for every files of the same product. First we round
         * the extremum numbers in such a way that they differ by exactly one decimal digit.
         */
        final int exp = (int) Math.floor(Math.log10(max - min));
        final double magnitude = MathFunctions.pow10(Math.abs(exp));
        if (exp >= 0) {
            min = Math.floor(min / magnitude) * magnitude;
            max = Math.ceil (max / magnitude) * magnitude;
        } else {
            // Keep 'magnitude' as an integer for reducing rounding errors.
            min = Math.floor(min * magnitude) / magnitude;
            max = Math.ceil (max * magnitude) / magnitude;
        }
        /*
         * Compute a scale factor for this new range and round it in such a way that its only decimal digit
         * is 1, 2 or 5.  This may shrink or expand the range by a factor between 0.5714 and 1.4286 with an
         * average factor of 0.9764 (determined empirically).  To compensate, we multiply the initial scale
         * by 1.75 since 0.5714… × 1.75 ≈ 1. The result will be a range with a span between 1 and 2.5 times
         * the span of the original range.
         */
        double delta = max - min;
        double scale = delta / (DEFAULT_RANGE / 1.75);
        final int es = (int) Math.floor(Math.log10(scale));
        final double ms = MathFunctions.pow10(es);
        final double sr = scale / ms;
        if (sr >= 3.5) {
            scale = MathFunctions.pow10(es + 1);            // Equivalent to ms * 10 but more accurate.
            if (sr < 7.5) scale /= 2;                       // Equivalent to ms *  5 but more accurate.
        } else if (sr >= 1.5) {
            scale = ms * 2;
        } else {
            scale = ms;
        }
        /*
         * Distribute the extra space between the minimum and maximum and apply the same rounding than we did before
         * (only one decimal digit different between minimum and maximum). If the range is positive, distribute evenly.
         * If the range crosses zero, distribute with a proportion p tending to result in min = -max.
         *
         *     min - delta*p = -(max + delta*(1-p))
         *     delta*p = (min + max + delta) / 2
         */
        final boolean isPositive = (min >= 0);
        delta = scale * DEFAULT_RANGE - delta;
        if (isPositive || max < 0) {
            delta /= 2;
        } else {
            delta = Math.min((min + max + delta) / 2, delta);
        }
        min -= Math.max(delta, 0);
        if (exp >= 0) {
            min = Math.rint(min / magnitude) * magnitude;
        } else {
            min = Math.rint(min * magnitude) / magnitude;
        }
        if (isPositive && min < 0) min = 0;
        /*
         * We take the minimum value as the offset, assuming that above rounding introduced enough space below the
         * minimum value for encompassing all the data. This declared range is approximative anyway.
         */
        b.addQuantitative(name, padValue, DEFAULT_RANGE + 1, scale, min, range.unit());
        return b.build().getCategories();
    }
}
