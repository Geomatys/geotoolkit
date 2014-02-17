/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import org.geotoolkit.feature.type.DefaultAttributeDescriptor;

import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.ComplexType;
import org.opengis.filter.identity.Identifier;

/**
 * Default implementation of a complexeAttribut.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultComplexAttribute<I extends Identifier> extends AbstractComplexAttribute<Collection<Property>,I> {

    
    public DefaultComplexAttribute(final Collection<? extends Property> properties, final AttributeDescriptor descriptor, final I id) {
        super(descriptor, id );
        value = new ArrayList<Property>();
        value.addAll(properties);
    }

    public DefaultComplexAttribute(final Collection<? extends Property> properties, final ComplexType type, final I id) {
        super(type,id);
        value = new ArrayList<Property>();
        value.addAll(properties);
    }

}
