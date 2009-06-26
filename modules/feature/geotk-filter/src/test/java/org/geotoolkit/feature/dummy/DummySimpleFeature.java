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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.BoundingBox;


/**
 * A dummy implementation of {@link SimpleFeature}.
 */
public final class DummySimpleFeature implements SimpleFeature {
    private final String id;

    private final SimpleFeatureType featureType;

    /**
     * The set of user data attached to each attribute (lazily created)
     */
    private Map<Object, Object>[] attributeUserData;

    /**
     * The actual values held by this feature
     */
    private final Object[] values;

    public DummySimpleFeature(final Object[] values, final String id, final SimpleFeatureType featureType) {
        this.values = values;
        this.id = id;
        this.featureType = featureType;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public SimpleFeatureType getType() {
        return featureType;
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    @Override
    public List<Object> getAttributes() {
        return new ArrayList(Arrays.asList(values));
    }

    @Override
    public void setAttributes(List<Object> values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAttributes(Object[] values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getAttribute(String name) {
        Integer idx = featureType.indexOf(name);
        if (idx != null) {
            return getAttribute(idx);
        } else {
            return null;
        }
    }

    @Override
    public void setAttribute(String name, Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getAttribute(Name name) {
        return getAttribute(name.getLocalPart());
    }

    @Override
    public void setAttribute(Name name, Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getAttribute(int index) throws IndexOutOfBoundsException {
        return values[index];
    }

    @Override
    public void setAttribute(int index, Object value) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAttributeCount() {
        return values.length;
    }

    @Override
    public Object getDefaultGeometry() {
        final GeometryDescriptor geometryDescriptor = featureType.getGeometryDescriptor();
        final Integer defaultGeomIndex = featureType.indexOf(geometryDescriptor.getName().getLocalPart());
        return getAttribute(defaultGeomIndex.intValue());
    }

    @Override
    public void setDefaultGeometry(Object geometry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureId getIdentifier() {
        return new DefaultFeatureId(id);
    }

    @Override
    public BoundingBox getBounds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GeometryAttribute getDefaultGeometryProperty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDefaultGeometryProperty(GeometryAttribute geometryAttribute) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValue(Collection<Property> values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Collection<? extends Property> getValue() {
        return getProperties();
    }

    @Override
    public Collection<Property> getProperties(Name name) {
        return getProperties(name.getLocalPart());
    }

    @Override
    public Property getProperty(Name name) {
        return getProperty(name.getLocalPart());
    }

    @Override
    public Collection<Property> getProperties(String name) {
        final Integer idx = featureType.indexOf(name);
        if (idx != null) {
            // cast temporarily to a plain collection to avoid type problems with generics
            Collection c = Collections.singleton(new Attribute(idx));
            return c;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<Property> getProperties() {
        return new AttributeList();
    }

    @Override
    public Property getProperty(String name) {
        final Integer idx = featureType.indexOf(name);
        if (idx == null) {
            return null;
        } else {
            int index = idx.intValue();
            final AttributeDescriptor descriptor = featureType.getDescriptor(index);
            if (descriptor instanceof GeometryDescriptor) {
                return new DummyProperty(values[0]);
            } else {
                return new Attribute(index);
            }
        }
    }

    @Override
    public void validate() throws IllegalAttributeException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AttributeDescriptor getDescriptor() {
        return null;
    }

    @Override
    public void setValue(Object newValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Name getName() {
        return null;
    }

    @Override
    public boolean isNillable() {
        return true;
    }

    @Override
    public Map<Object, Object> getUserData() {
        return null;
    }


    /**
     * Live collection backed directly on the value array
     */
    class AttributeList extends AbstractList<Property> {

        @Override
        public Attribute get(int index) {
            return new Attribute(index);
        }

        @Override
        public Attribute set(int index, Property element) {
            values[index] = element.getValue();
            return null;
        }

        @Override
        public int size() {
            return values.length;
        }
    }

    /**
     * Attribute that delegates directly to the value array
     */
    class Attribute implements org.opengis.feature.Attribute {

        private final int index;

        Attribute(int index) {
            this.index = index;
        }

        @Override
        public Identifier getIdentifier() {
            return null;
        }

        @Override
        public AttributeDescriptor getDescriptor() {
            return featureType.getDescriptor(index);
        }

        @Override
        public AttributeType getType() {
            return featureType.getType(index);
        }

        @Override
        public Name getName() {
            return getDescriptor().getName();
        }

        @Override
        public Map<Object, Object> getUserData() {
            // lazily create the user data holder
            if (attributeUserData == null) {
                attributeUserData = new HashMap[values.length];
            }
            // lazily create the attribute user data
            if (attributeUserData[index] == null) {
                attributeUserData[index] = new HashMap<Object, Object>();
            }
            return attributeUserData[index];
        }

        @Override
        public Object getValue() {
            return values[index];
        }

        @Override
        public boolean isNillable() {
            return getDescriptor().isNillable();
        }

        @Override
        public void setValue(Object newValue) {
            values[index] = newValue;
        }

        @Override
        public void validate() {
            //Types.validate(getDescriptor(), values[index]);
        }
    }

}
