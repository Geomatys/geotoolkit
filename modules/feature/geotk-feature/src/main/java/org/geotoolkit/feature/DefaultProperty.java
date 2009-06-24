/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.util.Utilities;
import org.opengis.feature.Property;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;


/**
 * Implementation of Property.
 *
 * @author Justin Deoliveira, The Open Planning Project
 *
 */
public class DefaultProperty implements Property {

    /**
     * content of the property
     */
    protected Object value;

    /**
     * descriptor of the property
     */
    protected PropertyDescriptor descriptor;

    /**
     * user data
     */
    protected final Map<Object, Object> userData;

    protected DefaultProperty(final Object value, final PropertyDescriptor descriptor) {
        this.value = value;
        this.descriptor = descriptor;
        userData = new HashMap<Object, Object>();

        if (descriptor == null) {
            throw new NullPointerException("descriptor");
        }
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(final Object value) {
        this.value = value;
    }

    @Override
    public PropertyDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public Name getName() {
        return getDescriptor().getName();
    }

    @Override
    public PropertyType getType() {
        return getDescriptor().getType();
    }

    @Override
    public boolean isNillable() {
        return getDescriptor().isNillable();
    }

    @Override
    public Map<Object, Object> getUserData() {
        return userData;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof DefaultProperty)) {
            return false;
        }

        DefaultProperty other = (DefaultProperty) obj;

        if (!Utilities.equals(descriptor, other.descriptor)) {
            return false;
        }

        if (!Utilities.deepEquals(value, other.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return 37 * descriptor.hashCode() + (37 * (value == null ? 0 : value.hashCode()));
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(getClass().getSimpleName()).append(":");
        sb.append(getDescriptor().getName().getLocalPart());
        sb.append("<");
        sb.append(getDescriptor().getType().getName().getLocalPart());
        sb.append(">=");
        sb.append(value);

        return sb.toString();
    }
}
