/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.internal.coverage;

import java.awt.Color;
import java.util.function.DoubleToIntFunction;
import javax.measure.Unit;
import org.apache.sis.coverage.Category;
import org.apache.sis.measure.NumberRange;
import org.opengis.referencing.operation.MathTransform1D;


/**
 * An Apache SIS category with the addition of colors.
 * Used only for transition from Geotk to Apache SIS.
 * Will be removed in a future Geotk version.
 */
public final class ColoredCategory extends Category {
    /**
     * The colors.
     */
    final Color[] colors;

    /**
     * Constructs a qualitative or quantitative category. This constructor is provided for sub-classes.
     * For other usages, {@link SampleDimension.Builder} should be used instead.
     *
     * @param  name     the category name (mandatory).
     * @param  samples  the minimum and maximum sample values (mandatory).
     * @param  toUnits  the conversion from sample values to real values,
     *                  or {@code null} for constructing a qualitative category.
     * @param  units    the units of measurement, or {@code null} if not applicable.
     *                  This is the target units after conversion by {@code toUnits}.
     * @param  toNaN    mapping from sample values to ordinal values to be supplied to {@link MathFunctions#toNanFloat(int)}.
     *                  That mapping is used only if {@code toUnits} is {@code null}. That mapping is responsible to ensure that
     *                  there is no ordinal value collision between different categories in the same {@link SampleDimension}.
     *                  The input is a real number in the {@code samples} range and the output shall be a unique value between
     *                  {@value MathFunctions#MIN_NAN_ORDINAL} and {@value MathFunctions#MAX_NAN_ORDINAL} inclusive.
     */
    public ColoredCategory(final CharSequence name, final Color[] colors, final NumberRange<?> samples, final MathTransform1D toUnits, final Unit<?> units,
             final DoubleToIntFunction toNaN)
    {
        super(name, samples, toUnits, units, toNaN);
        this.colors = colors;
    }

    /**
     * Returns the set of colors for this category. Change to the returned array will not affect this category.
     *
     * @return the colors palette for this category.
     */
    public Color[] getColors() {
        return colors.clone();
    }
}
