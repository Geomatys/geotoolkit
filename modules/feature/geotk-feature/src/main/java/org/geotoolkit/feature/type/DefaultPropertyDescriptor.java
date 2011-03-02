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
package org.geotoolkit.feature.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.Utilities;

import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;

import static org.geotoolkit.util.ArgumentChecks.*;

/**
 * Default implementation of a property descriptor
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultPropertyDescriptor<T extends PropertyType> implements PropertyDescriptor,Serializable {

    protected final T type;
    protected final Name name;
    protected final int minOccurs;
    protected final int maxOccurs;
    protected final boolean isNillable;
    protected final Map<Object, Object> userData = new HashMap<Object, Object>();

    public DefaultPropertyDescriptor(final T type, final Name name, final int min,
            final int max, final boolean isNillable){

        ensureNonNull("property type", type);
        ensureNonNull("name", name);

        if (max > 0 && (max < min)) {
            throw new IllegalArgumentException("max must be -1, or < min");
        }

        this.type = type;
        this.name = name;
        this.minOccurs = min;
        this.maxOccurs = max;
        this.isNillable = isNillable;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T getType() {
        return type;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Name getName() {
        return name;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getMinOccurs() {
        return minOccurs;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getMaxOccurs() {
        return maxOccurs;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isNillable() {
        return isNillable;
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
    public boolean equals(final Object obj) {
        if (!(obj instanceof DefaultPropertyDescriptor)) {
            return false;
        }

        DefaultPropertyDescriptor other = (DefaultPropertyDescriptor) obj;
        return Utilities.equals(type, other.type) &&
                Utilities.equals(name, other.name) &&
                (minOccurs == other.minOccurs) && (maxOccurs == other.maxOccurs) &&
                (isNillable == other.isNillable);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return (37 * minOccurs + 37 * maxOccurs) ^ type.hashCode() ^ name.hashCode();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append(" ");
        sb.append(getName());
        if (type != null) {
            sb.append(" <");
            sb.append(type.getName().getLocalPart());
            sb.append(":");
            sb.append(Classes.getShortName(type.getBinding()));
            sb.append(">");
        }
        if (isNillable) {
            sb.append(" nillable");
        }
        if (minOccurs == 1 && maxOccurs == 1) {
            // ignore the 1:1
        } else {
            sb.append(" ");
            sb.append(minOccurs);
            sb.append(":");
            sb.append(maxOccurs);
        }
        if (userData != null && !userData.isEmpty()) {
            sb.append("\nuserData=(");
            for (Map.Entry entry : userData.entrySet()) {
                sb.append("\n\t");
                sb.append(entry.getKey());
                sb.append(" ==> ");
                sb.append(entry.getValue());
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
