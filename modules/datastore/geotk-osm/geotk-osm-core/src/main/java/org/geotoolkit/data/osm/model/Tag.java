/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.osm.model;

import org.geotoolkit.feature.AbstractComplexAttribute;
import org.geotoolkit.feature.DefaultProperty;
import org.opengis.feature.Property;
import org.opengis.filter.identity.Identifier;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Tag extends AbstractComplexAttribute<Identifier>{

    private final String k;
    private final String v;

    public Tag(String key, String value) {
        super(OSMModelConstants.DESC_TAG,new SimpleId(key));
        this.k = key;
        this.v = value;
    }

    public String getK() {
        return k;
    }

    public String getV() {
        return v;
    }

    // feature/attribut model --------------------------------------------------

    @Override
    protected Property[] getPropertiesInternal() {
        final Property[] props = new Property[2];
        props[0] = new DefaultProperty(k, getType().getDescriptor("k"));
        props[1] = new DefaultProperty(v, getType().getDescriptor("v"));
        return props;
    }

}
