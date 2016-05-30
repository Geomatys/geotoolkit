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
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.apache.sis.util.Numbers;
import org.geotoolkit.resources.Vocabulary;
import org.opengis.coverage.SampleDimension;
import org.opengis.util.InternationalString;

/**
 * Utilities class for {@link SampleDimension}, {@link Category}  working.
 *
 * @author Remi Marechal (Geomatys).
 * @since 4.0
 */
public final strictfp class SampleDimensionUtils {
    
    /**
     * Used nodata {@link Category} name.
     * @see Category#getName() 
     */
    public static InternationalString NODATA_CATEGORY_NAME = Vocabulary.formatInternational(Vocabulary.Keys.Nodata);
    
    /**
     * Create a {@link Category} adapted for No data value.<br>
     * Moreover : <br>
     * - the default associate color for this category is ARGB : (0, 0, 0, 0)<br>
     * - interval borders of single nodata value are <strong>Inclusive</strong><br>
     * - the default associate Category name will be {@link #NODATA_CATEGORY_NAME}.
     * 
     * @param dataType sample type of data (double, byte, short ...)
     * @param noDataValue no data value.
     * @return no data {@link Category}.
     */
    public static Category buildSingleNoDataCategory(final Class dataType, final double noDataValue) {
        ArgumentChecks.ensureNonNull("dataType", dataType);
        return new Category(NODATA_CATEGORY_NAME, new Color(0,0,0,0), 
                                       getTypedRangeNumber(dataType, noDataValue, true, noDataValue, true));
    }
    
    /**
     * Create a {@link Category} adapted for No data values.<br>
     * Moreover : <br>
     * - the default associate color for this category is ARGB : (0, 0, 0, 0)
     * - the default associate Category name will be {@link #NODATA_CATEGORY_NAME}.
     * 
     * @param dataType sample type of data (double, byte, short ...)
     * @param minNoDataValue minimum interval category value.
     * @param isMinInclusive {@code true} for minimum interval inclusive value, else {@code false} for exclusive.
     * @param maxNodataValue maximum interval category value.
     * @param IsMaxInclusive {@code true} for maximum interval inclusive value, else {@code false} for exclusive.
     * @return expected noData category.
     */
    public static Category buildNoDataCategory(final Class dataType, 
                                               final double minNoDataValue, final boolean isMinInclusive,
                                               final double maxNodataValue, final boolean IsMaxInclusive) {
        ArgumentChecks.ensureNonNull("dataType", dataType);
        return new Category(NODATA_CATEGORY_NAME, new Color(0, 0, 0, 0), 
                                       getTypedRangeNumber(dataType, minNoDataValue, isMinInclusive, maxNodataValue, IsMaxInclusive));
    }
    
    /**
     * Create a {@link Category} adapted for any values.
     * 
     * @param name category name.
     * @param dataType sample type of data (double, byte, short ...)
     * @param colors The category color, or {@code null} for a default color.
     * @param minCategoryValue minimum interval category value.
     * @param isMinInclude {@code true} for minimum interval inclusive value, else {@code false} for exclusive.
     * @param maxCategoryValue maximum interval category value.
     * @param isMaxInclude {@code true} for maximum interval inclusive value, else {@code false} for exclusive.
     * @param scale scale value for internaly sample to geophysic mathematic function.
     * @param offset value for internaly sample to geophysic mathematic function.
     * @return expected {@link Category} adapted for any values.
     * @throws NullArgumentException if name or dataType is {@code null}.
     * @throws NullArgumentException if name {@link String} is empty. 
     */
    public static Category buildCategory(final String name, final Class dataType, final Color[] colors,
                                         final double minCategoryValue, final boolean isMinInclude,
                                         final double maxCategoryValue, final boolean isMaxInclude,
                                         final double scale, final double offset) {
        ArgumentChecks.ensureNonNull("name", name);
        ArgumentChecks.ensureNonEmpty("name", name);
        ArgumentChecks.ensureNonNull("dataType", dataType);
        return new Category(name, colors, 
                        getTypedRangeNumber(dataType, 
                                            minCategoryValue, isMinInclude, 
                                            maxCategoryValue, isMaxInclude), scale, offset);
    }
        
    /**
     * Returns an appropriate {@link NumberRange} from given parameters.
     * 
     * @param <T> type of internal data.
     * @param type type of internal data.
     * @param min minimum range value.
     * @param isMinIncluded {@code true} if minimum value is considered as include into range interval else false (exclusive).
     * @param max maximum range value.
     * @param isMaxIncluded {@code true} if maximum value is considered as include into range interval else false (exclusive).
     * @return appropriate range value casted in expected type.
     */
    private static <T extends Number & Comparable<T>> NumberRange<T> getTypedRangeNumber(final Class<T> type,
            final double min, final boolean isMinIncluded,
            final double max, final boolean isMaxIncluded)
    {
        return new NumberRange(type, Numbers.cast(min, type), isMinIncluded,
                                     Numbers.cast(max, type), isMaxIncluded);
    }
}
