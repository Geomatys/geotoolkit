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

import org.opengis.feature.Property;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 * Implementation of Property.
 *
 * @author Johann Sorel (Geomatys)
 * @author Justin Deoliveira, The Open Planning Project
 * @module pending
 */
public class DefaultProperty<V extends Object, D extends PropertyDescriptor> extends AbstractProperty implements Property,Serializable {

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

    /**
     * This contructor is only available for complex types,
     * Complex objects are the only ones allowed to have a property type
     * whitout descriptor since they may be top level object.
     * A Descriptor is only necessary if the property is defined inside another
     * type.
     * @param type
     */
    protected DefaultProperty(final PropertyType type) {
        this(null,type);
    }

    public DefaultProperty(final V value, final D descriptor) {
        ensureNonNull("descriptor", descriptor);
        this.value = value;
        this.descriptor = descriptor;
        this.type = descriptor.getType();
    }

    public DefaultProperty(final V value, final PropertyType type) {
        ensureNonNull("type", type);
        this.value = value;
        this.descriptor = null;
        this.type = type;
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
    public PropertyType getType() {
        return type;
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

}
