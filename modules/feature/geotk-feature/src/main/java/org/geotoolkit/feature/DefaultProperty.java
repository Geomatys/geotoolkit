/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009 Geomatys
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
 * @author Johann Sorel (Geomatys)
 * @author Justin Deoliveira, The Open Planning Project
 */
public class DefaultProperty<V extends Object, D extends PropertyDescriptor> implements Property {

    /**
     * descriptor of the property
     */
    protected final D descriptor;

    /**
     * user data
     */
    protected final Map<Object, Object> userData = new HashMap<Object, Object>();

    /**
     * content of the property
     */
    protected V value;


    protected DefaultProperty(final D descriptor) {
        this(null,descriptor);
    }

    protected DefaultProperty(final V value, final D descriptor) {
        this.value = value;
        this.descriptor = descriptor;

        if (descriptor == null) {
            throw new NullPointerException("descriptor");
        }
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public V getValue() {
        return value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setValue(final Object value) {
        this.value = (V)value;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public D getDescriptor() {
        return descriptor;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Name getName() {
        return descriptor.getName();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyType getType() {
        return descriptor.getType();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isNillable() {
        return descriptor.isNillable();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<Object, Object> getUserData() {
        return userData;
    }

    /**
     * {@inheritDoc }
     */
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

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return 37 * descriptor.hashCode() + (37 * (value == null ? 0 : value.hashCode()));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append(":");
        sb.append(descriptor.getName().getLocalPart());
        sb.append("<");
        sb.append(descriptor.getType().getName().getLocalPart());
        sb.append(">=");
        sb.append(value);

        return sb.toString();
    }
}
