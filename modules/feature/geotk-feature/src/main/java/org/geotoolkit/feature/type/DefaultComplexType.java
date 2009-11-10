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
package org.geotoolkit.feature.type;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.util.converter.Classes;

import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.util.InternationalString;


/**
 * Base class for complex types.
 *
 * @author gabriel
 * @author Ben Caradoc-Davies, CSIRO Exploration and Mining
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultComplexType extends DefaultAttributeType<AttributeType> implements ComplexType {

    /**
     * Immutable copy of the properties list with which we were constructed.
     */
    protected final PropertyDescriptor[] descriptors;
    protected final List<PropertyDescriptor> descriptorsList;

    /**
     * Map to locate properties by name.
     */
    private final Map<Name, PropertyDescriptor> propertyMap;

    public DefaultComplexType(final Name name, final Collection<PropertyDescriptor> properties,
            final boolean identified, final boolean isAbstract, final List<Filter> restrictions,
            final AttributeType superType, final InternationalString description){
        super(name, Collection.class, identified, isAbstract, restrictions, superType, description);

        final Map<Name, PropertyDescriptor> localPropertyMap;

        if (properties == null) {
            this.descriptors = new PropertyDescriptor[0];
            localPropertyMap = Collections.emptyMap();
        } else {
            this.descriptors = properties.toArray(new PropertyDescriptor[properties.size()]);
            
            localPropertyMap = new HashMap<Name, PropertyDescriptor>();
            for (PropertyDescriptor pd : properties) {
                if (pd == null) {
                    // descriptor entry may be null if a request was made for a property that does not exist
                    throw new NullPointerException("PropertyDescriptor is null - did you request a property that does not exist?");
                }
                localPropertyMap.put(pd.getName(), pd);
            }

        }
        this.descriptorsList = UnmodifiableArrayList.wrap(this.descriptors);
        this.propertyMap = Collections.unmodifiableMap(localPropertyMap);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Class<Collection<Property>> getBinding() {
        return (Class<Collection<Property>>) super.getBinding();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<PropertyDescriptor> getDescriptors() {
        return descriptorsList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyDescriptor getDescriptor(final Name name) {
        return propertyMap.get(name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PropertyDescriptor getDescriptor(final String name) {
        return getDescriptor(new DefaultName(name));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isInline() {
        //JD: at this point "inlining" is unused... we might want to kill it
        // from the interface
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof DefaultComplexType && super.equals(o)) {
            DefaultComplexType that = (DefaultComplexType) o;
            return Utilities.equals(this.descriptorsList, that.descriptorsList);
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return 59 * super.hashCode() + descriptors.hashCode();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append(" ");
        sb.append(getName());
        if (isAbstract()) {
            sb.append(" abstract");
        }
        if (isIdentified()) {
            sb.append(" identified");
        }
        if (superType != null) {
            sb.append(" extends ");
            sb.append(superType.getName().getLocalPart());
        }
        if (List.class.isAssignableFrom(binding)) {
            sb.append("[");
        } else {
            sb.append("(");
        }
        boolean first = true;
        for (PropertyDescriptor property : getDescriptors()) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(property.getName().getLocalPart());
            sb.append(":");
            sb.append(property.getType().getName().getLocalPart());
        }
        if (List.class.isAssignableFrom(binding)) {
            sb.append("]");
        } else {
            sb.append(")");
        }
        if (description != null) {
            sb.append("\n\tdescription=");
            sb.append(description);
        }
        if (restrictions != null && !restrictions.isEmpty()) {
            sb.append("\nrestrictions=");
            first = true;
            for (Filter filter : restrictions) {
                if (first) {
                    first = false;
                } else {
                    sb.append(" AND ");
                }
                sb.append(filter);
            }
        }
        return sb.toString();
    }
}
