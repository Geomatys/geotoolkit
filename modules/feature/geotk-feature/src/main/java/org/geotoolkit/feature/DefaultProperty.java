/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011 Geomatys
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
import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.util.NullArgumentException;
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
 * @module pending
 */
public class DefaultProperty<V extends Object, D extends PropertyDescriptor> implements Property,Serializable {

    /**
     * descriptor of the property
     */
    protected final D descriptor;

    /**
     * type of the property
     */
    protected final PropertyType type;

    /**
     * user data (lazy creation)
     */
    protected Map<Object, Object> userData = null;

    /**
     * content of the property
     */
    protected V value;


    public DefaultProperty(final D descriptor) {
        this(null,descriptor);
    }

    public DefaultProperty(final PropertyType type) {
        this(null,type);
    }

    public DefaultProperty(final V value, final D descriptor) {
        this.value = value;

        if (descriptor == null) {
            throw new NullArgumentException("Descriptor can not be null");
        }

        this.descriptor = descriptor;
        this.type = descriptor.getType();
    }

    public DefaultProperty(final V value, final PropertyType type) {
        this.value = value;
        this.descriptor = null;
        this.type = type;

        if (type == null) {
            throw new NullArgumentException("PropertyType can not be null");
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
        if(descriptor != null){
            return descriptor.getName();
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyType getType() {
        return type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isNillable() {
        if(descriptor != null){
            return descriptor.isNillable();
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<Object, Object> getUserData() {
        if(userData == null){
            userData = new HashMap<Object, Object>(1);
        }
        return userData;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
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
        if (!Utilities.equals(type, other.type)) {
            return false;
        }

        if (!Utilities.deepEquals(value, other.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.descriptor != null ? this.descriptor.hashCode() : 0);
        hash = 73 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 73 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append(":");
        sb.append(getName());
        sb.append("<");
        sb.append(getType().getName().getLocalPart());
        sb.append(">=");
        sb.append(value);
        return sb.toString();
    }
}
