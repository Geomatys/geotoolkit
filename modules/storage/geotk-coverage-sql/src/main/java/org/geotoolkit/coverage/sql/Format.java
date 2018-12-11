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
import java.util.Iterator;
import java.util.Set;
import java.nio.file.Path;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.image.io.metadata.SampleDomain;



/**
 * Information about a raster format.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class Format {
    /**
     * The data store provider to use for opening files, or {@code null} if unknown.
     */
    private final DataStoreProvider provider;

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
     */
    final List<SampleDimension> sampleDimensions;

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
    Format(String driver, final String paletteName, final SampleDimension[] bands, final String metadata) {
        driver = driver.trim();
        if (bands != null) {
            final SampleDomain[] domains = new SampleDomain[bands.length];
            for (int i=0; i<bands.length; i++) {
                final SampleDimension band = bands[i];
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
        for (DataStoreProvider provider : DataStores.providers()) {
            if (driver.equalsIgnoreCase(provider.getShortName())) {
                this.provider = provider;
                return;
            }
        }
        provider = null;
    }

    /**
     * Opens the resource at the given path.
     */
    final DataStore open(final Path path) throws DataStoreException {
        if (provider != null) {
            return provider.open(new StorageConnector(path));
        } else {
            return DataStores.open(path);
        }
    }




    /**
     * Default implementation of {@link SampleDomain} created from a {@link SampleDimension}.
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
         * Creates a new {@code Domain} from the given {@code SampleDimension}.
         *
         * @param dimension  the {@code SampleDimension} from which to extract the information.
         */
        Domain(SampleDimension dimension) {
            dimension = dimension.forConvertedValues(false);
            Set<Number> nodata = dimension.getNoDataValues();
            fillValues = new double[nodata.size()];
            int i = 0;
            for (final Iterator<Number> it=nodata.iterator(); it.hasNext();) {
                fillValues[i++] = it.next().doubleValue();
            }
            /*
             * Computes the range ourself instead than relying on SampleDimension.getRange()
             * becauce we want to exclude the qualitative categories (i.e. the fill values).
             */
            NumberRange<?> range = null;
            final List<Category> categories = dimension.getCategories();
            if (categories != null) {
                for (final Category category : categories) {
                    if (category.isQuantitative()) {
                        final NumberRange<?> extent = category.getSampleRange();
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
            assert (range == null) || dimension.getSampleRange().get().containsAny(range);
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
