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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.apache.sis.util.Numbers;
import org.geotoolkit.resources.Vocabulary;
import org.opengis.coverage.SampleDimension;

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
    public static String NODATA_CATEGORY_NAME = Vocabulary.formatInternational(Vocabulary.Keys.NODATA).toString();
    
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
     * Build {@linkplain Category categories} {@link List} from sample and noData values from band. 
     * 
     * @param minSampleValue minimum sample value for current {@link SampleDimension} (band).
     * @param maxSampleValue maximum sample value for current {@link SampleDimension} (band).
     * @param typeClass data type of 
     * @param scale scale use to convert sample values into geophysic values, or {@code null} if none. 
     * @param offset offset use to convert sample values into geophysic values, or {@code null} if none.
     * @param nodataValues {@link Set} which contain all nodata, organize in ascending order, for current band.
     * @return {@link Category} list for current band.
     * @throws NullArgumentException if nodataValues {@link Set} or typeClass is {@code null}.
     */
    public static List<Category> buildCategories(final double minSampleValue, final double maxSampleValue, 
                                                  final Double scale,         final Double offset,
                                                  final Class typeClass,       final TreeSet<Double> nodataValues) {
        ArgumentChecks.ensureNonNull("noDataValues", nodataValues);
        ArgumentChecks.ensureNonNull("typeClass",    typeClass);
        if ((scale != null && offset == null)
          || scale == null && offset != null) 
            throw new IllegalArgumentException("Impossible to build conform category : "
                    + "offset and scale from sample to geophysic transformation must be "
                    + "both null or both no null : scale = "+scale+", offset = "+offset);
        final List<Category> categories = new ArrayList<>();
        if (nodataValues.isEmpty()) {
            if (scale != null) {
                assert offset != null;
                categories.add(new Category("data", null, 
                        getTypedRangeNumber(typeClass, 
                                            minSampleValue, true, 
                                            maxSampleValue, true), scale, offset));
            } else {
                //-- create a category define as View.PHOTOMETRIC
                assert offset == null;
                categories.add(new Category("data", null, 
                        getTypedRangeNumber(typeClass, 
                                            minSampleValue, true, 
                                            maxSampleValue, true)));
            }
            
            return categories;
        }
        
        double currentMinSV  = minSampleValue;
        double currentMaxSV  = maxSampleValue;
        boolean isMinInclude = true;
        boolean isMaxInclude = true;
        
        final Iterator<Double> itNoData = nodataValues.iterator();
        while (itNoData.hasNext()) {
            final double currentNoData = itNoData.next();
            categories.add(new Category(NODATA_CATEGORY_NAME, new Color(0,0,0,0), 
                                       getTypedRangeNumber(typeClass, currentNoData, true, currentNoData, true)));
            if (currentNoData == currentMinSV) {
                isMinInclude = false;
            } else if (currentNoData == currentMaxSV) {
                isMaxInclude = false;
            } else if (currentMinSV < currentNoData && currentNoData < currentMaxSV) {//-- intersection
                if (scale != null) {
                    assert offset != null;
                    categories.add(new Category("data", null, 
                        getTypedRangeNumber(typeClass, 
                                            currentMinSV, isMinInclude, 
                                            currentNoData, false), scale, offset));
                } else {
                    //-- create a category define as View.PHOTOMETRIC
                    assert offset == null;
                    categories.add(new Category("data", null, 
                        getTypedRangeNumber(typeClass, 
                                            currentMinSV, isMinInclude, 
                                            currentNoData, false)));
                }
                
                
                isMinInclude = false;
                currentMinSV = currentNoData;
            } else {
                //-- volontary do nothing with no intersection
            }
        }
        
        assert currentMaxSV == maxSampleValue : "buildCategories : last category : currentMaxSample "
                + "value should be equals to maxSampleValues. Expected : "+maxSampleValue+". Found : "+currentMaxSV;
        
        //-- add the last category
        //-- it is the last category to insert in case with intersection between sample values intervals
        //-- else if no intersection it just will be sample interval.
        if (scale != null) {
            assert offset != null;
            categories.add(new Category("data", null, 
                        getTypedRangeNumber(typeClass, 
                                            currentMinSV, isMinInclude, 
                                            currentMaxSV, isMaxInclude), scale, offset));
        } else {
            //-- create a category define as View.PHOTOMETRIC
            assert offset == null;
            categories.add(new Category("data", null, 
                        getTypedRangeNumber(typeClass, 
                                            currentMinSV, isMinInclude, 
                                            currentMaxSV, isMaxInclude)));
        }
        
        
        return categories;
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
