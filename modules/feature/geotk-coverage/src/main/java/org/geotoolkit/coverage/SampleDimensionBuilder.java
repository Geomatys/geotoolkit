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
import java.lang.reflect.Field;
import java.util.List;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.geotoolkit.internal.coverage.ColoredCategory;


/**
 * Extension to SIS sample dimension builder.
 *
 * @deprecated use {@link SampleDimension.Builder}. The only additional features provided by this builder
 *             is {@link #setColor(Color...)}, which is a removed feature from Apache SIS.
 */
@Deprecated
public class SampleDimensionBuilder extends SampleDimension.Builder {
    public SampleDimensionBuilder() {
    }

    /**
     * Sets the colors of the last category constructed.
     * Should be invoked after an {@code add} method.
     */
    public SampleDimensionBuilder setLastCategoryColors(final Color... colors) {
        if (colors != null) {
            List<Category> categories = categories();
            final int i = categories.size() - 1;
            final Category c = categories.get(i);
            Category[] array;
            try {
                Field f = SampleDimension.Builder.class.getDeclaredField("categories");
                f.setAccessible(true);
                array = (Category[]) f.get(this);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
            array[i] = new ColoredCategory(c, colors);
        }
        return this;
    }
}
