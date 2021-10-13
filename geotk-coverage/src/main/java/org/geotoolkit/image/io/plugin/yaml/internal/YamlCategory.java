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
package org.geotoolkit.image.io.plugin.yaml.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Locale;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.referencing.operation.transform.TransferFunction;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.opengis.referencing.operation.MathTransform1D;

/**
 * Equivalent class of {@link Category} use during Yaml binding.
 *
 * @author Remi Marechal (Geomatys).
 * @since 4.0
 */
@JsonInclude(Include.NON_NULL)
public final class YamlCategory {

    /**
     * Name of the current {@link Category}.
     *
     * @see Category#name
     */
    private String name;

    /**
     * Minimum sample value.
     *
     * @see Category#minimum
     * @see #isMinInclusive
     */
    private Double minSampleValue;

    /**
     * Define if {@link #minSampleValue} is inclusive or not.<br>
     * <strong>true for inclusive false for exclusive</strong>
     */
    private Boolean isMinInclusive;

    /**
     * Maximum sample value.
     *
     * @see Category#maximum
     * @see #isMaxInclusive
     */
    private Double maxSampleValue;

    /**
     * Define if {@link #maxSampleValue} is inclusive or not.<br>
     * <strong>true for inclusive false for exclusive</strong>
     */
    private Boolean isMaxInclusive;

    /**
     * Only use if {@link #isMaxInclusive} == {@link #isMinInclusive} == true and {@link #minSampleValue} == {@link #maxSampleValue}.
     * @see #YamlCategory(org.geotoolkit.coverage.Category)
     */
    private Double value;

    /**
     * Scale value use to build internaly {@link MathTransform1D} sample to geophysic.
     *
     * @see Category#Category(java.lang.CharSequence, java.awt.Color[], int, int, double, double)
     * @see Category#createLinearTransform(double, double)
     */
    private Double scale;

    /**
     * Offset value use to build internaly {@link MathTransform1D} sample to geophysic.
     *
     * @see Category#Category(java.lang.CharSequence, java.awt.Color[], int, int, double, double)
     * @see Category#createLinearTransform(double, double)
     */
    private Double offset;

    /**
     * Constructor only use during Yaml binding.
     */
    public YamlCategory() {
    }

    /**
     * Build a {@link YamlCategory} from geotk {@link Category}.
     *
     * @param category {@link Category} which will be serialized into Yaml format.
     */
    public YamlCategory(final Category category) {
        ArgumentChecks.ensureNonNull("geotk category", category);
        name = category.getName().toString(Locale.ENGLISH);
        final NumberRange numRange = category.getSampleRange();

        final double tempminSampleValue = numRange.getMinDouble();
        final boolean tempisMinInclusive = numRange.isMinIncluded();

        final double tempmaxSampleValue = numRange.getMaxDouble();
        final boolean tempisMaxInclusive = numRange.isMaxIncluded();

        //-- in case where min = max and two inclusives borders
        if (tempmaxSampleValue == tempminSampleValue
         && tempisMinInclusive == tempisMaxInclusive
                && tempisMinInclusive == true) {
            value = tempminSampleValue;
            minSampleValue = maxSampleValue = null;
            isMaxInclusive = isMinInclusive = null;

        } else {
            minSampleValue = tempminSampleValue;
            isMinInclusive = tempisMinInclusive;
            maxSampleValue = tempmaxSampleValue;
            isMaxInclusive = tempisMaxInclusive;
        }

        final MathTransform1D mtSToGeo = category.getTransferFunction().orElse(null);
        //-- peut etre mettre un log si la function de transformation est null .
        if (mtSToGeo != null) {
            final TransferFunction tf = new TransferFunction();
            tf.setTransform(mtSToGeo);

            scale  = tf.getScale();
            offset = tf.getOffset();
        }
    }

    /**
     * Returns the name of this {@link YamlCategory}.
     *
     * @return name
     * @see #name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name of this category.
     *
     * @param name
     * @see #name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns minimum sample value from internaly stored {@link Category} samples.
     *
     * @return minimum sample value
     * @see #minSampleValue
     */
    public Double getMinSampleValue() {
        return minSampleValue;
    }

    /**
     * Set minimum sample value from internaly stored samples.
     *
     * @param minSampleValue
     * @see #minSampleValue
     */
    public void setMinSampleValue(final Double minSampleValue) {
        this.minSampleValue = minSampleValue;
    }

    /**
     * Return {@code true} if {@link #minSampleValue} is <strong>Inclusive</strong>,
     * else {@code false} for <strong>Exclusive</strong>.
     *
     * @return {@code true} for inclusive minimum intervale value, else {@code false}.
     */
    public Boolean getIsMinInclusive() {
        return isMinInclusive;
    }

    /**
     * Set {@code true} to define {@link #minSampleValue} as inclusive, else {@code false}.
     *
     * @param isMinInclusive
     * @see #minSampleValue
     */
    public void setIsMinInclusive(final Boolean isMinInclusive) {
        this.isMinInclusive = isMinInclusive;
    }

    /**
     * Returns maximum sample value from internaly stored {@link Category} samples.
     *
     * @return maximum sample value
     * @see #maxSampleValue
     */
    public Double getMaxSampleValue() {
        return maxSampleValue;
    }

    /**
     * Set maximum sample value from internaly stored samples.
     *
     * @param maxSampleValue
     * @see #maxSampleValue
     */
    public void setMaxSampleValue(final Double maxSampleValue) {
        this.maxSampleValue = maxSampleValue;
    }

    /**
     * Set {@code true} to define {@link #maxSampleValue} as inclusive, else {@code false}.
     *
     * @param isMaxInclusive
     * @see #maxSampleValue
     */
    public void setIsMaxInclusive(final Boolean isMaxInclusive) {
        this.isMaxInclusive = isMaxInclusive;
    }

    /**
     * Return {@code true} if {@link #maxSampleValue} is <strong>Inclusive</strong>,
     * else {@code false} for <strong>Exclusive</strong>.
     *
     * @return {@code true} for inclusive maximum interval value, else {@code false}.
     */
    public Boolean getIsMaxInclusive() {
        return isMaxInclusive;
    }

    /**
     * Returns singleton sample value if min and max category border are equals else return {@code null}.
     *
     * @return singleton sample value.
     * @see #value
     * @see #YamlCategory(org.geotoolkit.coverage.Category)
     */
    public Double getValue() {
        return value;
    }

    /**
     * Set singleton {@link Category} border.
     *
     * @param value inclusive min and max category border value.
     */
    public void setValue(final Double value) {
        ArgumentChecks.ensureNonNull("value", value);
        this.value = value;
    }

    /**
     * Returns needed scale value to build sample to geophysic mathematic functions.
     *
     * @return scale
     * @see #scale
     */
    public Double getScale() {
        return scale;
    }

    /**
     * Set needed offset value to build sample to geophysic mathematic functions.
     *
     * @param scaleZ
     * @see #offset
     */
    public void setScale(Double scaleZ) {
        this.scale = scaleZ;
    }

    /**
     * Returns needed offset value to build sample to geophysic mathematic functions.
     *
     * @return offset
     * @see #offset
     */
    public Double getOffset() {
        return offset;
    }

    /**
     * Set needed scale value to build sample to geophysic mathematic functions.
     *
     * @param offsetZ
     * @see #scale
     */
    public void setOffset(Double offsetZ) {
        this.offset = offsetZ;
    }

    Category toCategory(Class dataType) {

        final SampleDimension.Builder builder = new SampleDimension.Builder();

        MathTransform1D trs = null;
        if (scale != null) {
            trs = (MathTransform1D) MathTransforms.linear(scale, offset);
        }

        final double  minSampleValue, maxSampleValue;
        final boolean isMinInclusive, isMaxInclusive;
        if (value != null) {
            minSampleValue = maxSampleValue = value;
            isMinInclusive = isMaxInclusive = true;
        } else {
            minSampleValue = this.minSampleValue;
            isMinInclusive = this.isMinInclusive;
            maxSampleValue = this.maxSampleValue;
            isMaxInclusive = this.isMaxInclusive;
        }

        if (name.equalsIgnoreCase(SampleDimensionUtils.NODATA_CATEGORY_NAME.toString(Locale.ENGLISH))) {
            builder.addQualitative(null,
                    new NumberRange(dataType, minSampleValue, isMinInclusive, maxSampleValue, isMaxInclusive));
        } else if (Double.isNaN(minSampleValue) && Double.isNaN(maxSampleValue)) {
            builder.setBackground(name, minSampleValue);
        } else if (value != null) {
            builder.addQualitative(name, value);
        } else {
            final NumberRange range = new NumberRange(dataType,
                    minSampleValue, isMinInclusive,
                    maxSampleValue, isMaxInclusive);
            builder.addQuantitative(name, range, trs, null);
        }

        return builder.categories().get(0);
    }
}
