/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.image.io.metadata.SampleDomain;


/**
 * Default implementation of {@link SampleDomain} created from a {@link GridSampleDimension}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.12
 * @module
 */
final class FormatSampleDomain implements SampleDomain {
    /**
     * The range of valid sample values (excluding fill values), or {@code null}.
     */
    private final NumberRange<?> range;

    /**
     * The fill values.
     */
    private final double[] fillValues;

    /**
     * Creates a new {@code FormatSampleDomain} from the given {@code GridSampleDimension}.
     *
     * @param dimension The {@code GridSampleDimension} from which to extract the information.
     */
    FormatSampleDomain(GridSampleDimension dimension) {
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
     * Returns the fill values. This method does not clone the returned array. We allow this
     * shortcut since {@code FormatSampleDomain} is not public and this method is used mostly
     * by Geotk implementation, which does not modify the returned array.
     */
    @Override
    public double[] getFillSampleValues() {
        return fillValues;
    }

    /**
     * Returns a string representation for debugging purpose.
     */
    @Override
    public String toString() {
        return "SampleDomain[" + range + ", fillValues=" + Arrays.toString(fillValues) + ']';
    }
}
