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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.internal.util.UnmodifiableArrayList;

import org.geotoolkit.feature.type.DefaultFeatureType;

import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.AttributeType;
import org.geotoolkit.feature.type.NamesExt;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;

/**
 * Implementation fo SimpleFeatureType, subtypes must be atomic and are stored
 * in a list.
 *
 * @author Justin
 * @author Ben Caradoc-Davies, CSIRO Exploration and Mining
 * @module pending
 */
public class DefaultSimpleFeatureType extends DefaultFeatureType implements SimpleFeatureType {

    protected AttributeType[] types;
    protected List<AttributeType> typesList;
    final Map<Object, Integer> index;

    public DefaultSimpleFeatureType(final GenericName name, final List<AttributeDescriptor> schema,
            final GeometryDescriptor defaultGeometry, final boolean isAbstract,
            final List<Filter> restrictions, final AttributeType superType,
            final InternationalString description){
        // Note intentional circumvention of generics type checking;
        // this is only valid if schema is not modified.
        super(name, (List) schema, defaultGeometry, isAbstract, restrictions,
                superType, description);
        index = buildIndex(this);


        //for faster access
        types = new AttributeType[descriptors.length];
        for(int i=0; i<descriptors.length;i++){
            types[i] = (AttributeType) descriptors[i].getType();
        }
        typesList = UnmodifiableArrayList.wrap(types);
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    /**
     * @see org.geotoolkit.feature.simple.SimpleFeatureType#getAttributeDescriptors()
     */
    @Override
    public final List<AttributeDescriptor> getAttributeDescriptors() {
        // TODO we should find a way to use generic and avoid casts on this method
        // and all getDescriptor methods.
        return (List) getDescriptors();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<AttributeType> getTypes() {
        return typesList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeType getType(final GenericName name) {
        final AttributeDescriptor attribute = getDescriptor(name);
        if (attribute != null) {
            return attribute.getType();
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeType getType(final String name) {
        final AttributeDescriptor attribute = getDescriptor(name);
        if (attribute != null) {
            return attribute.getType();
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeType getType(final int index) {
        return types[index];
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeDescriptor getDescriptor(final GenericName name) {
        return (AttributeDescriptor) super.getDescriptor(name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeDescriptor getDescriptor(final String name) {
        return (AttributeDescriptor) super.getDescriptor(name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeDescriptor getDescriptor(final int index) {
        return (AttributeDescriptor) descriptors[index];
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int indexOf(final GenericName name) {
        final String ns = NamesExt.getNamespace(name);
        if(ns==null || ns.isEmpty()){
            return indexOf(name.toString());
        }
        // otherwise do a full scan
        for (int i=0; i<descriptors.length; i++){
            PropertyDescriptor descriptor = descriptors[i];
            if (descriptor.getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int indexOf(final String name) {
        Integer idx = index.get(name);
        return (idx != null) ? idx : -1;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getAttributeCount() {
        return descriptors.length;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getTypeName() {
        return name.tip().toString();
    }

    /**
     * Builds the name -> position index used by simple features for fast attribute lookup
     * @param featureType
     * @return
     */
    static Map<Object, Integer> buildIndex(final SimpleFeatureType featureType) {

        // build an index of attribute name to index
        final Map<Object, Integer> index = new HashMap<Object, Integer>();

        final List<AttributeDescriptor> descs = featureType.getAttributeDescriptors();
        final int n = descs.size();
        //must iterate backward to make first attribut with same local part first
        for(int i=n-1; i>=0; i--){
            final AttributeDescriptor ad = descs.get(i);
            final GenericName name = ad.getName();
            index.put(name, i);
            index.put(NamesExt.create(name.tip().toString()), i);
            //must add possible string combinaison
            index.put(name.tip().toString(), i);
            index.put(NamesExt.toExpandedString(name), i);
            index.put(NamesExt.toExtendedForm(name), i);
        }

        final GeometryDescriptor geomDesc = featureType.getGeometryDescriptor();
        if (geomDesc != null) {
            index.put(null, index.get(geomDesc.getName()));
        }

        //ensure that the index won't be modified
        return Collections.unmodifiableMap(index);
    }
}
