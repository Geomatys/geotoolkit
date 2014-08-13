/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011 Geomatys
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

import java.io.Serializable;
import java.util.Objects;

import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.feature.type.PropertyType;

/**
 * Abstract implementation of Property.
 * Only implements fallback methods, do not store any values.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public abstract class AbstractProperty implements Property,Serializable {


    protected AbstractProperty() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Name getName() {
        final PropertyDescriptor descriptor = getDescriptor();
        if(descriptor != null){
            return descriptor.getName();
        } else {
            final PropertyType type = getType();
            if (type != null) {
                return type.getName();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isNillable() {
        final PropertyDescriptor descriptor = getDescriptor();
        if(descriptor != null){
            return descriptor.isNillable();
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Property)) {
            return false;
        }

        Property other = (Property) obj;

        final PropertyDescriptor descriptor = getDescriptor();
        if (!Objects.equals(descriptor, other.getDescriptor())) {
            return false;
        }
        final PropertyType type = getType();
        if (!Objects.equals(type, other.getType())) {
            return false;
        }
        final Object value = getValue();
        if (!Objects.deepEquals(value, other.getValue())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        final PropertyDescriptor descriptor = getDescriptor();
        final PropertyType type = getType();
        final Object value = getValue();
        hash = 73 * hash + (descriptor != null ? descriptor.hashCode() : 0);
        hash = 73 * hash + (type != null ? type.hashCode() : 0);
        hash = 73 * hash + (value != null ? value.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final Object value = getValue();
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append(":");
        sb.append(getName());
        sb.append("<");
        sb.append(getType().getName().getLocalPart());
        sb.append(">=");
        sb.append(value);
        return sb.toString();
    }
}
