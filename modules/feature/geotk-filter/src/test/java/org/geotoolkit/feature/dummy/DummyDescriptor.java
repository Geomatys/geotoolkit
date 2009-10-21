/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.feature.dummy;

import java.util.Map;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;


/**
 * Dummy implementation of {@link AttributeDescriptor}.
 * @module pending
 */
public final class DummyDescriptor implements AttributeDescriptor {

    private final String name;
    private final Class type;

    public DummyDescriptor(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public AttributeType getType() {
        return new DummyAttributeType(DummyDescriptor.this.getName(), type);
    }

    @Override
    public String getLocalName() {
        return name;
    }

    @Override
    public Object getDefaultValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Name getName() {
        return new DummyName(name);
    }

    @Override
    public int getMinOccurs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMaxOccurs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isNillable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Object, Object> getUserData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
