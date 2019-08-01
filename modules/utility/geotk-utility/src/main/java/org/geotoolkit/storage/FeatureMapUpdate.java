/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.storage;

import java.util.Map;
import java.util.function.UnaryOperator;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeatureMapUpdate implements UnaryOperator<Feature> {

    private final Map<String, ? extends Object> values;

    public FeatureMapUpdate(Map<String, ? extends Object> values) {
        this.values = values;
    }

    public Map<String, ? extends Object> getValues() {
        return values;
    }

    @Override
    public Feature apply(Feature t) {
        for (Map.Entry<String,? extends Object> entry : values.entrySet()) {
            t.setPropertyValue(entry.getKey(), entry.getValue());
        }
        return t;
    }


}
