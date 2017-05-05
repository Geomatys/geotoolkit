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

import java.util.Locale;
import org.apache.sis.measure.NumberRange;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.coverage.Category;

/**
 * Equivalent class of {@link Category} use during Yaml binding.
 *
 * @author Remi Marechal (Geomatys).
 * @since 4.0
 */
public class YamlCategory {

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
        final NumberRange numRange = category.getRange();

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
    }

    //-------------- GETTER --------

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
     * Returns minimum sample value from internaly stored {@link Category} samples.
     *
     * @return minimum sample value
     * @see #minSampleValue
     */
    public Double getMinSampleValue() {
        return minSampleValue;
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
     * Returns maximum sample value from internaly stored {@link Category} samples.
     *
     * @return maximum sample value
     * @see #maxSampleValue
     */
    public Double getMaxSampleValue() {
        return maxSampleValue;
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


    //------------- SETTER-----

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
     * Set minimum sample value from internaly stored samples.
     *
     * @param minSampleValue
     * @see #minSampleValue
     */
    public void setMinSampleValue(final Double minSampleValue) {
        this.minSampleValue = minSampleValue;
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
     * Set singleton {@link Category} border.
     *
     * @param value inclusive min and max category border value.
     */
    public void setValue(final Double value) {
        ArgumentChecks.ensureNonNull("value", value);
        this.value = value;
    }


}
