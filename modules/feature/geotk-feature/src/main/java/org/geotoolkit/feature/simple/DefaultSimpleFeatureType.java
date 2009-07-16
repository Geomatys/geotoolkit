/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.feature.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotoolkit.feature.type.DefaultFeatureType;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.util.InternationalString;

/**
 * Implementation fo SimpleFeatureType, subtypes must be atomic and are stored
 * in a list.
 *
 * @author Justin
 * @author Ben Caradoc-Davies, CSIRO Exploration and Mining
 */
public class DefaultSimpleFeatureType extends DefaultFeatureType implements SimpleFeatureType {

    // list of types
    List<AttributeType> types = null;
    Map<String, Integer> index;

    public DefaultSimpleFeatureType(final Name name, final List<AttributeDescriptor> schema,
            final GeometryDescriptor defaultGeometry, final boolean isAbstract,
            final List<Filter> restrictions, final AttributeType superType,
            final InternationalString description){
        // Note intentional circumvention of generics type checking;
        // this is only valid if schema is not modified.
        super(name, (List) schema, defaultGeometry, isAbstract, restrictions,
                superType, description);
        index = buildIndex(this);
    }

    /**
     * @see org.opengis.feature.simple.SimpleFeatureType#getAttributeDescriptors()
     */
    @Override
    public final List<AttributeDescriptor> getAttributeDescriptors() {
        // Here we circumvent the generics type system. Because we provide the schema and know it is
        // copied into an ArrayList in ComplexTypeImpl, this must work. Ugly, but then so are simple
        // features.
        return (List) getDescriptors();
    }

    @Override
    public List<AttributeType> getTypes() {
        if (types == null) {
            synchronized (this) {
                if (types == null) {
                    types = new ArrayList<AttributeType>();
                    for (AttributeDescriptor ad : getAttributeDescriptors()) {
                        types.add(ad.getType());
                    }
                }
            }
        }
        return types;
    }

    @Override
    public AttributeType getType(final Name name) {
        final AttributeDescriptor attribute = (AttributeDescriptor) getDescriptor(name);
        if (attribute != null) {
            return attribute.getType();
        }
        return null;
    }

    @Override
    public AttributeType getType(final String name) {
        final AttributeDescriptor attribute = (AttributeDescriptor) getDescriptor(name);
        if (attribute != null) {
            return attribute.getType();
        }
        return null;
    }

    @Override
    public AttributeType getType(final int index) {
        return getTypes().get(index);
    }

    @Override
    public AttributeDescriptor getDescriptor(final Name name) {
        return (AttributeDescriptor) super.getDescriptor(name);
    }

    @Override
    public AttributeDescriptor getDescriptor(final String name) {
        return (AttributeDescriptor) super.getDescriptor(name);
    }

    @Override
    public AttributeDescriptor getDescriptor(final int index) {
        return getAttributeDescriptors().get(index);
    }

    @Override
    public int indexOf(final Name name) {
        if (name.getNamespaceURI() == null) {
            return indexOf(name.getLocalPart());
        }
        // otherwise do a full scan
        int index = 0;
        for (AttributeDescriptor descriptor : getAttributeDescriptors()) {
            if (descriptor.getName().equals(name)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    @Override
    public int indexOf(final String name) {
        Integer idx = index.get(name);
        if (idx != null) {
            return idx.intValue();
        } else {
            return -1;
        }
    }

    @Override
    public int getAttributeCount() {
        return getAttributeDescriptors().size();
    }

    @Override
    public String getTypeName() {
        return getName().getLocalPart();
    }

    /**
     * Builds the name -> position index used by simple features for fast attribute lookup
     * @param featureType
     * @return
     */
    static Map<String, Integer> buildIndex(final SimpleFeatureType featureType) {
        // build an index of attribute name to index
        final Map<String, Integer> index = new HashMap<String, Integer>();
        int i = 0;
        for (AttributeDescriptor ad : featureType.getAttributeDescriptors()) {
            index.put(ad.getLocalName(), i++);
        }
        if (featureType.getGeometryDescriptor() != null) {
            index.put(null, index.get(featureType.getGeometryDescriptor().getLocalName()));
        }
        return index;
    }
}
