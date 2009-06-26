/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.feature.dummy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;


/**
 * A dummy implementation of {@link SimpleFeature}.
 */
public final class DummySimpleFeatureType implements SimpleFeatureType {
    private final GeometryDescriptor defaultGeometry;
	private final CoordinateReferenceSystem crs;

    private List<AttributeType> types = null;
    private final Map<String, Integer> index;
    /**
     * Immutable copy of the properties list with which we were constructed.
     */
    private final Collection<PropertyDescriptor> properties;
    /**
     * Map to locate properties by name.
     */
    private final Map<Name, PropertyDescriptor> propertyMap;

    public DummySimpleFeatureType(final List<AttributeDescriptor> schema,
            final GeometryDescriptor defaultGeometry, final CoordinateReferenceSystem crs)
    {
        this.defaultGeometry = defaultGeometry;
        this.crs = crs;
        final List<PropertyDescriptor> localProperties;
        final Map<Name, PropertyDescriptor> localPropertyMap;
        if (schema == null) {
            localProperties = Collections.emptyList();
            localPropertyMap = Collections.emptyMap();
        } else {
            localProperties = new ArrayList<PropertyDescriptor>(schema);
            localPropertyMap = new HashMap<Name, PropertyDescriptor>();
            for (PropertyDescriptor pd : schema) {
                if (pd == null) {
                    // descriptor entry may be null if a request was made for a property that does not exist
                    throw new NullPointerException("PropertyDescriptor is null - did you request a property that does not exist?");
                }
                localPropertyMap.put(pd.getName(), pd);
            }

        }
        this.properties = Collections.unmodifiableList(localProperties);
        this.propertyMap = Collections.unmodifiableMap(localPropertyMap);
        index = buildIndex(this);
    }

    @Override
    public String getTypeName() {
        return getName().getLocalPart();
    }

    @Override
    public List<AttributeDescriptor> getAttributeDescriptors() {
        return (List) getDescriptors();
    }

    @Override
    public AttributeDescriptor getDescriptor(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AttributeDescriptor getDescriptor(Name name) {
        return getDescriptor(name.getLocalPart());
    }

    @Override
    public AttributeDescriptor getDescriptor(int index) throws IndexOutOfBoundsException {
        return getAttributeDescriptors().get(index);
    }

    @Override
    public int getAttributeCount() {
        return getAttributeDescriptors().size();
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
    public AttributeType getType(String name) {
        final AttributeDescriptor attribute = (AttributeDescriptor) getDescriptor(name);
        if (attribute != null) {
            return attribute.getType();
        }
        return null;
    }

    @Override
    public AttributeType getType(Name name) {
        final AttributeDescriptor attribute = (AttributeDescriptor) getDescriptor(name);
        if (attribute != null) {
            return attribute.getType();
        }
        return null;
    }

    @Override
    public AttributeType getType(int index) throws IndexOutOfBoundsException {
        return getTypes().get(index);
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
    public boolean isIdentified() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GeometryDescriptor getGeometryDescriptor() {
        return defaultGeometry;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return  crs;
    }

    @Override
    public Class<Collection<Property>> getBinding() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<PropertyDescriptor> getDescriptors() {
        return properties;
    }

    @Override
    public boolean isInline() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AttributeType getSuper() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Name getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAbstract() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Filter> getRestrictions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public InternationalString getDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<Object, Object> getUserData() {
        throw new UnsupportedOperationException("Not supported yet.");
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
