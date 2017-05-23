/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.feature;

import java.util.HashMap;
import java.util.Map;
import org.apache.sis.feature.DefaultFeatureType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public abstract class DecoratedFeatureType extends DefaultFeatureType {

    protected DecoratedFeatureType(FeatureType type) {
        super(properties(type), type.isAbstract(), type.getSuperTypes().toArray(new FeatureType[0]));
    }

    static Map<String,Object> properties(IdentifiedType type) {
        final Map<String,Object> p = new HashMap<>(8);
        p.put(NAME_KEY,        type.getName());
        p.put(DEFINITION_KEY,  type.getDefinition());
        p.put(DESCRIPTION_KEY, type.getDescription());
        p.put(DESIGNATION_KEY, type.getDesignation());
        return p;
    }

    public abstract FeatureType getDecoratedType();

    public abstract Feature newInstance(Feature base) throws IllegalStateException, UnsupportedOperationException;

}
