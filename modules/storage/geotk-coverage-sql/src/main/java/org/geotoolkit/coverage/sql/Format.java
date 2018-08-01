/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
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
import java.util.Arrays;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.image.io.metadata.SampleDomain;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.measure.MeasurementRange;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.Category;

import static org.geotoolkit.internal.InternalUtilities.adjustForRoundingError;


/**
 * Information about a raster format.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class Format {
    /**
     * The raster format name as declared in the database.
     */
    final String rasterFormat;

    /**
     * The sample dimensions for coverages encoded with this format, or {@code null} if undefined.
     * If non-null, then the list is guaranteed to be non-empty and the list size is equals to the
     * expected number of bands.
     *
     * <p>Empty lists are not allowed because our Raster I/O framework interprets that as "no bands",
     * as opposed to "unknown bands" (which is what we mean in the particular case of our database
     * schema).</p>
     *
     * <p>Each {@code SampleDimension} specifies how to convert pixel values to geophysics values.
     * For example coverages read from PNG files will typically store their data as integer values
     * (non-geophysics), while coverages read from ASCII files will often store their pixel values
     * as real numbers (geophysics values).</p>
     *
     * @see GridSampleDimension#geophysics(boolean)
     */
    final List<GridSampleDimension> sampleDimensions;

    /**
     * The range of valid sample values and the fill values for each sample dimension.
     */
    private final List<SampleDomain> sampleDomains;

    /**
     * The name of the color palette, or {@code null} if unspecified.
     */
    private final String paletteName;

    /**
     * Reference to an entry in the {@code metadata.Format} table, or {@code null}.
     */
    private final String metadata;

    /**
     * Creates a new entry for this format.
     *
     * @param driver       the format name (i.e. the plugin to use).
     * @param paletteName  the name of the color palette, or {@code null} if unspecified.
     * @param bands        sample dimensions for coverages encoded with this format, or {@code null}.
     *                     The bands given to this constructor shall <strong>not</strong> be geophysics.
     * @param metadata     reference to an entry in the {@code metadata.Format} table, or {@code null}.
     */
    Format(final String driver, final String paletteName, final GridSampleDimension[] bands, final String metadata) {
        rasterFormat = driver.trim();
        if (bands != null) {
            final SampleDomain[] domains = new SampleDomain[bands.length];
            for (int i=0; i<bands.length; i++) {
                final GridSampleDimension band = bands[i];
                domains[i] = new Domain(band);
                bands  [i] = band;
            }
            sampleDimensions = UnmodifiableArrayList.wrap(bands);
            sampleDomains    = UnmodifiableArrayList.wrap(domains);
        } else {
            sampleDimensions = null;
            sampleDomains    = null;
        }
        this.paletteName = paletteName;
        this.metadata    = metadata;
    }

    /**
     * Returns the ranges of valid sample values for each band in this format.
     * The range are always expressed in <cite>geophysics</cite> values.
     */
    final MeasurementRange<Double>[] getSampleValueRanges() {
        final List<GridSampleDimension> bands = sampleDimensions;
        if (bands == null) {
            return null;
        }
        @SuppressWarnings({"unchecked","rawtypes"})         // Generic array creation.
        final MeasurementRange<Double>[] ranges = new MeasurementRange[bands.size()];
        for (int i=0; i<ranges.length; i++) {
            final GridSampleDimension band = bands.get(i).geophysics(true);
            /*
             * The call 'roundIfAlmostInteger' is a work-around for rounding error. We perform the
             * workaround here instead than at GridSampleDimension construction time because the
             * minimal and maximal values are the result of a computation, not a stored value.
             */
            ranges[i] = MeasurementRange.create(
                    adjustForRoundingError(band.getMinimumValue()), true,
                    adjustForRoundingError(band.getMaximumValue()), true,
                    band.getUnits());
        }
        return ranges;
    }




    /**
     * Default implementation of {@link SampleDomain} created from a {@link GridSampleDimension}.
     *
     * @author Martin Desruisseaux (Geomatys)
     */
    private static final class Domain implements SampleDomain {
        /**
         * The range of valid sample values (excluding fill values), or {@code null}.
         */
        private final NumberRange<?> range;

        /**
         * The fill values.
         */
        private final double[] fillValues;

        /**
         * Creates a new {@code Domain} from the given {@code GridSampleDimension}.
         *
         * @param dimension  the {@code GridSampleDimension} from which to extract the information.
         */
        Domain(GridSampleDimension dimension) {
            dimension  = dimension.geophysics(false);
            fillValues = dimension.getNoDataValues();
            /*
             * Computes the range ourself instead than relying on GridSampleDimension.getRange()
             * becauce we want to exclude the qualitative categories (i.e. the fill values).
             */
            NumberRange<?> range = null;
            final List<Category> categories = dimension.getCategories();
            if (categories != null) {
                for (final Category category : categories) {
                    if (category.isQuantitative()) {
                        final NumberRange<?> extent = category.getRange();
                        if (!Double.isNaN(extent.getMinDouble()) && !Double.isNaN(extent.getMaxDouble())) {
                            if (range != null) {
                                range = range.unionAny(extent);
                            } else {
                                range = extent;
                            }
                        }
                    }
                }
            }
            this.range = range;
            assert (range == null) || dimension.getRange().containsAny(range);
        }

        /**
         * Returns the range of valid sample values, excluding fill values.
         */
        @Override
        public NumberRange<?> getValidSampleValues() {
            return range;
        }

        /**
         * Returns the fill values.
         */
        @Override
        public double[] getFillSampleValues() {
            return fillValues.clone();
        }

        /**
         * Returns a string representation for debugging purpose.
         */
        @Override
        public String toString() {
            return "SampleDomain[" + range + ", fillValues=" + Arrays.toString(fillValues) + ']';
        }
    }
}
