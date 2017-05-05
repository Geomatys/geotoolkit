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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.geotoolkit.coverage.Category;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.SampleDimensionUtils;
import org.opengis.coverage.SampleDimension;

/**
 * Equivalent class of {@link GridSampleDimension} use during Yaml binding.
 *
 * @author Remi Marechal (Geomatys).
 * @since 4.0
 */
public class YamlSampleDimension {

    /**
     * Description or "name" of this current {@link YamlSampleDimension}.
     *
     * @see GridSampleDimension#getDescription()
     */
    private String description;

    /**
     * Internal {@link YamlCategory} which compose this {@link SampleDimension}.
     *
     * @see GridSampleDimension#getCategories()
     */
    private List<YamlCategory> categories;

    /**
     * Constructor only use dring Yaml binding.
     */
    public YamlSampleDimension() {
    }

    /**
     * Build a {@link YamlSampleDimension} from geotk {@link GridSampleDimension}.
     *
     * @param sampleDimension {@link GridSampleDimension} which will be serialized into Yaml format.
     */
    public YamlSampleDimension(final GridSampleDimension sampleDimension) {
        description = sampleDimension.getDescription().toString(Locale.ENGLISH);

        categories  = new ArrayList<YamlCategory>();
        for (final Category cat : sampleDimension.getCategories()) {
            categories.add(SampleDimensionUtils.NODATA_CATEGORY_NAME.toString(Locale.ENGLISH).equalsIgnoreCase(cat.getName().toString(Locale.ENGLISH))
                           ? new YamlCategory(cat)
                           : new YamlSampleCategory(cat));
        }
    }

    /**
     * Returns description of this current {@link YamlSampleDimension}.
     *
     * @return description
     * @see #description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Internal {@link YamlCategory} which compose this {@link YamlSampleDimension}.
     *
     * @return categories
     * @see #categories
     */
    public List<YamlCategory> getCategories() {
        return categories;
    }

    /**
     * Set description of this current {@link YamlSampleDimension}.
     *
     * @param description
     * @see #description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Set internal {@link YamlCategory} which compose this {@link YamlSampleDimension}.
     *
     * @param categories
     * @see #categories
     */
    public void setCategories(final List<YamlCategory> categories) {
        this.categories = categories;
    }
}
