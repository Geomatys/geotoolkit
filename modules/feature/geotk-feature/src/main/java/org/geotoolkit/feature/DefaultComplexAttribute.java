/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

    public static DefaultComplexAttribute<Identifier> create(
            Collection<? extends Property> properties, ComplexType type, Identifier id) {
        return new DefaultComplexAttribute(
                properties,
                new DefaultAttributeDescriptor( type, type.getName(), 1, 1, true, null),
                id);
    }

    public DefaultComplexAttribute(Collection<? extends Property> properties, AttributeDescriptor descriptor, I id) {
        super(descriptor, id );
        value = new ArrayList<Property>();
        value.addAll(properties);
    }

}
