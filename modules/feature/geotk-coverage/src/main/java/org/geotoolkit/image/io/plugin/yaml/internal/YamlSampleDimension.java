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
import java.util.ArrayList;
import java.util.List;
import javax.measure.Unit;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.measure.Units;

/**
 * Equivalent class of {@link SampleDimension} use during Yaml binding.
 *
 * @author Remi Marechal (Geomatys).
 * @since 4.0
 */
@JsonInclude(Include.NON_NULL)
public final class YamlSampleDimension {

    /**
     * Description or "name" of this current {@link YamlSampleDimension}.
     *
     * @see SampleDimension#getDescription()
     */
    private String description;

    /**
     * Sample dimension unit.
     */
    private String unit;

    /**
     * Internal {@link YamlCategory} which compose this {@link SampleDimension}.
     *
     * @see SampleDimension#getCategories()
     */
    private List<YamlCategory> categories;

    /**
     * Constructor only use dring Yaml binding.
     */
    public YamlSampleDimension() {
    }

    /**
     * Build a {@link YamlSampleDimension} from geotk {@link SampleDimension}.
     *
     * @param sampleDimension {@link SampleDimension} which will be serialized into Yaml format.
     */
    public YamlSampleDimension(final SampleDimension sampleDimension) {
        description = sampleDimension.getName().toString();
        sampleDimension.getUnits().ifPresent((Unit<?> t) -> {
            unit = t.getSymbol();
        });

        categories  = new ArrayList<YamlCategory>();
        for (final Category cat : sampleDimension.getCategories()) {
            categories.add(new YamlCategory(cat));
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
     * Set description of this current {@link YamlSampleDimension}.
     *
     * @param description
     * @see #description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
     * Set internal {@link YamlCategory} which compose this {@link YamlSampleDimension}.
     *
     * @param categories
     * @see #categories
     */
    public void setCategories(final List<YamlCategory> categories) {
        this.categories = categories;
    }

    public SampleDimension toSampleDimension(Class dataType) {
        Unit unit = Units.UNITY;
        if (this.unit != null && !this.unit.isEmpty()) {
            unit = Units.valueOf(this.unit);
        }

        final SampleDimension.Builder builder = new SampleDimension.Builder();
        builder.setName(description);
        for (YamlCategory cat : categories) {
            final Category c = cat.toCategory(dataType);
            if (c.isQuantitative()) {
                builder.addQuantitative(c.getName(), c.getSampleRange(), c.getTransferFunction().orElse(null), unit);
            } else {
                builder.addQualitative(c.getName(), c.getSampleRange());
            }
        }

        return builder.build();
    }
}
