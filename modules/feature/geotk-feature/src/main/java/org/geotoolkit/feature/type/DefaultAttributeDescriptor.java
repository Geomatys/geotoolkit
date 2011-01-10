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

import com.vividsolutions.jts.geom.Geometry;
import java.util.Map;

import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.util.Utilities;

import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

/**
 * Default implementation of a property descriptor
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultAttributeDescriptor<T extends AttributeType> extends DefaultPropertyDescriptor<T>
        implements AttributeDescriptor {

    protected final Object defaultValue;

    public DefaultAttributeDescriptor(final T type, final Name name, final int min,
            final int max, final boolean isNillable, final Object defaultValue){
        super(type, name, min, max, isNillable);

        if(defaultValue != null && !type.getBinding().isInstance(defaultValue)){
           throw new IllegalArgumentException("Default value doesn't match");
        }
        this.defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getLocalName() {
        return getName().getLocalPart();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^
                (defaultValue != null ? defaultValue.hashCode() : 0);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DefaultAttributeDescriptor)) {
            return false;
        }

        final DefaultAttributeDescriptor d = (DefaultAttributeDescriptor) o;

        if(!super.equals(o)){
            return false;
        }

        if(defaultValue instanceof Geometry && d.defaultValue instanceof Geometry){
            return ((Geometry)defaultValue).equalsExact((Geometry) d.defaultValue);
        }else{
            return Utilities.deepEquals(defaultValue, d.defaultValue);
        }
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
        if (defaultValue != null) {
            sb.append("\ndefault= ");
            sb.append(defaultValue);
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
