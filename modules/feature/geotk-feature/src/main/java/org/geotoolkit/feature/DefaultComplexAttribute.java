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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.geotoolkit.feature.type.DefaultAttributeDescriptor;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.Identifier;

public class DefaultComplexAttribute extends DefaultAttribute implements ComplexAttribute {

    public DefaultComplexAttribute(Collection<Property> properties, AttributeDescriptor descriptor, Identifier id) {
        super(cloneProperties( properties ), descriptor, id );
    }
    
    public DefaultComplexAttribute(Collection<Property> properties, ComplexType type, Identifier id) {
        this(properties, new DefaultAttributeDescriptor( type, type.getName(), 1, 1, true, null), id);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ComplexType getType() {
        return (ComplexType) super.getType();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<? extends Property> getValue() {
       return unmodifiable((Collection) super.getValue());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Property> getProperties() {
    	return unmodifiable((Collection) super.getValue());
    }

    /**
     * Internal helper method for getting at the properties without wrapping
     * in unmodifiable collection.
     */
    protected Collection properties() {
        return (Collection) super.getValue();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Property> getProperties(Name name) {
        List<Property> matches = new ArrayList<Property>();
        for ( Iterator p = getValue().iterator(); p.hasNext(); ) {
            Property property = (Property) p.next();
            if ( property.getName().equals( name ) ) {
                matches.add( property );
            }
        }
        
        return matches;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Property> getProperties(String name) {
        List<Property> matches = new ArrayList<Property>();
        for ( Iterator p = properties().iterator(); p.hasNext(); ) {
            Property property = (Property) p.next();
            if ( property.getName().getLocalPart().equals( name ) ) {
                matches.add( property );
            }
        }
        
        return matches;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Property getProperty(Name name) {
        for ( Iterator p = properties().iterator(); p.hasNext(); ) {
            Property property = (Property) p.next();
            if ( property.getName().equals( name ) ) {
                return property;
            }
        }
        
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Property getProperty(String name) {
        for ( Iterator p = getValue().iterator(); p.hasNext(); ) {
            Property property = (Property) p.next();
            if ( property.getName().getLocalPart().equals( name ) ) {
                return property;
            }
        }
        
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setValue(Object newValue) throws IllegalArgumentException,
            IllegalStateException {
        setValue((Collection<Property>)newValue);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setValue(Collection<Property> newValue) {
        super.setValue(cloneProperties(newValue));
    }

    /**
     * helper method to clone the property collection.
     */
    private static Collection cloneProperties( Collection original ) {
        if ( original == null ) {
            return null;
        }
        
        Collection clone = null;
        try {
            clone = original.getClass().newInstance();
        }
        catch( Exception e ) {
            clone = new ArrayList();
        }
        
        clone.addAll( original );
        return clone;
    }
    
    /**
     * Wraps a collection in an umodifiable collection based on the interface
     * the collection implements.
     * <p>
     * A list will result in an umodifiable list, a set in an unmodifiable set,
     * etc..
     * </p>
     *
     */
    public static Collection unmodifiable( Collection original ) {

        if ( original instanceof Set ) {
            if ( original instanceof SortedSet ) {
                return Collections.unmodifiableSortedSet((SortedSet) original);
            }

            return Collections.unmodifiableSet((Set)original);
        }
        else if ( original instanceof List ) {
            return Collections.unmodifiableList((List)original);
        }

        return Collections.unmodifiableCollection(original);
    }

}
