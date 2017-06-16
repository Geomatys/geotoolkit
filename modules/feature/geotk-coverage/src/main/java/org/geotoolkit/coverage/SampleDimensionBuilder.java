/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
import static java.awt.image.DataBuffer.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.measure.Unit;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.measure.Units;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.util.InternationalString;

/**
 * GridSampleDimension builder class.
 *
 * <p>
 * Sample dimensions with multiple categories may become difficult to create.
 * This class starts by collecting all categories then sort them and fill or
 * expend categories to avoid uncategorized ranges.
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 */
public class SampleDimensionBuilder {

    /**
     * Sort categories by sample range minimum.
     */
    private static final Comparator<Category> SORTER = new Comparator<Category>() {
        @Override
        public int compare(Category o1, Category o2) {
            return Double.compare(o1.minimum,o2.minimum);
        }
    };
    private static final Map<Class,Integer> CLASS_TO_TYPE = new HashMap<>();
    static {
        CLASS_TO_TYPE.put(byte.class, TYPE_BYTE);
        CLASS_TO_TYPE.put(Byte.class, TYPE_BYTE);
        CLASS_TO_TYPE.put(short.class, TYPE_SHORT);
        CLASS_TO_TYPE.put(Short.class, TYPE_SHORT);
        CLASS_TO_TYPE.put(int.class, TYPE_INT);
        CLASS_TO_TYPE.put(Integer.class, TYPE_INT);
        CLASS_TO_TYPE.put(float.class, TYPE_FLOAT);
        CLASS_TO_TYPE.put(Float.class, TYPE_FLOAT);
        CLASS_TO_TYPE.put(double.class, TYPE_DOUBLE);
        CLASS_TO_TYPE.put(Double.class, TYPE_DOUBLE);
    }

    private static final Color TRS = new Color(0f, 0f, 0f, 0f);

    private final List<Category> categories = new ArrayList<>();
    private CharSequence description;
    private Unit unit = Units.UNITY;
    private double min,max;

    //default main category, calculated at the end.
    private CharSequence mainCategoryName;
    private Color[] mainColors;
    private MathTransform1D mainSampleToGeophysic;

    /**
     * Note : byte type is considered as unsigned byte with range [0-255]
     *
     * @param dataType DataBuffer.TYPE_X
     */
    public SampleDimensionBuilder(Class dataType) {
        this(CLASS_TO_TYPE.get(dataType));
    }

    /**
     * Note : byte type is considered as unsigned byte with range [0-255]
     *
     * @param dataType DataBuffer.TYPE_X, this is the type as stored in the coverage before sample to geophysic transformation.
     */
    public SampleDimensionBuilder(int dataType) {

        switch(dataType){
            case TYPE_BYTE   : min = 0; max = 255; break;
            case TYPE_SHORT  : min = Short.MIN_VALUE; max = Short.MAX_VALUE; break;
            case TYPE_USHORT : min = 0; max = 65535; break;
            case TYPE_INT    : min = Integer.MIN_VALUE; max = Integer.MAX_VALUE; break;
            case TYPE_FLOAT  : min = Float.MIN_VALUE; max = Float.MAX_VALUE; break;
            case TYPE_DOUBLE : min = Double.MIN_VALUE; max = Double.MAX_VALUE; break;
            default: throw new IllegalArgumentException("Unknown data type "+dataType);
        }

    }

    /**
     * Set sample dimension description.
     *
     * @param description, should not be null.
     */
    public void setDescription(CharSequence description) {
        this.description = description;
    }

    /**
     * Set sample dimension unit.
     * It it recommended to fill this information if it is available.
     *
     * @param unit , can be null
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * Configure main category informations.
     *
     * @param name category name
     * @param colors category colors, can be null
     * @param sampleToGeophysic sample to geophysics transform
     */
    public void setMainCategory(CharSequence name, Color[] colors, MathTransform1D sampleToGeophysic){
        this.mainCategoryName = name;
        this.mainColors = colors;
        this.mainSampleToGeophysic = sampleToGeophysic;
    }

    /**
     * Add a punctual value to interpret as NoData.
     *
     * @param value NoData value
     */
    public void addNoData(double value){
        addNoData(NumberRange.create(value, true, value, true));
    }

    /**
     * Add a value range to interpret as NoData.
     *
     * @param range NoData value range.
     */
    public void addNoData(NumberRange range){
        addCategory(new Category("No data", TRS, range));
    }

    /**
     * Add a punctual value to interpret as a new category..
     *
     * @param name category name.
     * @param color category color.
     * @param value category value.
     */
    public void add(CharSequence name, Color color, double value){
        addCategory(new Category(name, color, value));
    }

    /**
     * Add a value range to interpret as NoData.
     *
     * @param name category name.
     * @param colors category colors.
     * @param sampleRange category value range.
     * @param scale sample to geophysics linear transform scale
     * @param offset sample to geophysics linear transform offset
     */
    public void add(String name, Color[] colors, NumberRange sampleRange, double scale, double offset){
        addCategory(new Category(name, colors, sampleRange, scale, offset));
    }

    /**
     * Add category in sample dimension.
     *
     * @param category must not overlaps with any previous category
     */
    public void addCategory(Category category){
        //ensure categories do not overlap
        final NumberRange range = category.getRange();
        for(Category cat : categories){
            if(cat.getRange().intersects(range)){
                throw new IllegalArgumentException("Category overlaps with "+cat.getName()+" category.");
            }
        }
        categories.add(category);
    }

    /**
     * Build sample dimension.
     * <br>
     * If category overlaps and are compatible then categories will be merged.
     *
     * @return {@link GridSampleDimension}
     */
    public GridSampleDimension build(){
        if(description==null){
            throw new IllegalArgumentException("Sample dimension description is undefined.");
        }

        //try to merge consecutive compatible categories
        Collections.sort(categories, SORTER);
        Category c1,c2;
        for(int i=categories.size()-1;i>0;i--){
            c1 = categories.get(i-1);
            c2 = categories.get(i);
            if(compatible(c1, c2) && c1.maximum == c2.minimum){
                categories.set(i-1, merge(c1, c2));
                categories.remove(i);
            }
        }

        //create the main category with largest span available
        if(mainCategoryName!=null){
            NumberRange range;
            if(categories.isEmpty()){
                //use the complete data type range
                range = NumberRange.create(min, true, max, true);
            }else{
                //pick the largest span above or under
                final Category catStart = categories.get(0);
                final Category catEnd = categories.get(categories.size()-1);
                if((catStart.minimum-min) > (max-catEnd.maximum)){
                    range = NumberRange.create(min, true, catStart.minimum, !catStart.getRange().isMinIncluded());
                }else{
                    range = NumberRange.create(catEnd.maximum, !catEnd.getRange().isMaxIncluded(), max, true);
                }

                //search if there is a wider span between categories
                for(int i=0,n=categories.size()-1;i<n;i++){
                    c1 = categories.get(i);
                    c2 = categories.get(i+1);
                    if(c1.maximum==c2.minimum){
                        //no gap between categories, skip it
                        continue;
                    }
                    if(((c2.minimum-c1.maximum) > (range.getMaxDouble()-range.getMinDouble())) ){
                        range = NumberRange.create(c1.maximum, !c1.getRange().isMaxIncluded(), c2.minimum, !c2.getRange().isMinIncluded());
                    }
                }
            }

            categories.add(new Category(mainCategoryName, mainColors, range, mainSampleToGeophysic));
            Collections.sort(categories,SORTER);
        }

        return new GridSampleDimension(description, categories.toArray(new Category[0]), unit).geophysics(false);
    }

    /**
     * Test if two categories can be merged.
     */
    private static boolean compatible(Category cat1, Category cat2){
        return Objects.equals(cat1.getName(),cat2.getName())
            && Objects.equals(cat1.getSampleToGeophysics(),cat2.getSampleToGeophysics());
    }

    /**
     * Merge two categories.
     */
    private static Category merge(Category cat1, Category cat2){
        final InternationalString name = cat1.getName();
        final MathTransform1D sampleToGeophysics = cat1.getSampleToGeophysics();
        final NumberRange range = (NumberRange) ((NumberRange)cat1.getRange()).union(cat2.getRange());
        return new Category(name, cat1.getColors(), range, sampleToGeophysics);
    }

}
