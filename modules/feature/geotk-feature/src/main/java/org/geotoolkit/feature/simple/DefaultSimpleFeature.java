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
package org.geotoolkit.feature.simple;

import com.vividsolutions.jts.geom.Geometry;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotoolkit.feature.DefaultGeometryAttribute;
import org.geotoolkit.feature.SimpleIllegalAttributeException;
import org.geotoolkit.feature.type.Types;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;

import org.opengis.feature.GeometryAttribute;
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
 * An implementation of {@link SimpleFeature} geared towards speed and backed by an Object[].
 *
 * @author Justin
 * @author Andrea Aime
 */
public class DefaultSimpleFeature implements SimpleFeature {

    protected final FeatureId id;

    protected final SimpleFeatureType featureType;
    /**
     * The actual values held by this feature
     */
    protected final Object[] values;
    /**
     * The attribute name -> position index
     */
    protected final Map<String, Integer> index;
    /**
     * Wheter this feature is self validating or not
     */
    protected final boolean validating;
    /**
     * The set of user data attached to the feature (lazily created)
     */
    protected Map<Object, Object> userData;
    /**
     * The set of user data attached to each attribute (lazily created)
     */
    protected Map<Object, Object>[] attributeUserData;

    /**
     * Builds a new feature based on the provided values and feature type
     * @param values
     * @param featureType
     * @param id
     */
    public DefaultSimpleFeature(final List<Object> values, final SimpleFeatureType featureType, final FeatureId id) {
        this(values.toArray(), featureType, id, false);
    }

    /**
     * Fast construction of a new feature. The object takes owneship of the provided value array,
     * do not modify after calling the constructor
     * @param values
     * @param featureType
     * @param id
     * @param validating
     */
    public DefaultSimpleFeature(final Object[] values, final SimpleFeatureType featureType, final FeatureId id,
            final boolean validating){
        this.id = id;
        this.featureType = featureType;
        this.values = values;
        this.validating = validating;

        // in the most common case reuse the map cached in the feature type
        if (featureType instanceof DefaultSimpleFeatureType) {
            index = ((DefaultSimpleFeatureType) featureType).index;
        } else {
            // if we're not lucky, rebuild the index completely...
            // TODO: create a separate cache for this case?
            this.index = DefaultSimpleFeatureType.buildIndex(featureType);
        }

        // if we're self validating, do validation right now
        if (validating) {
            validate();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureId getIdentifier() {
        return id;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getID() {
        return id.getID();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getAttribute(final int index) throws IndexOutOfBoundsException {
        return values[index];
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getAttribute(final String name) {
        Integer idx = index.get(name);
        if (idx != null) {
            return getAttribute(idx);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getAttribute(final Name name) {
        return getAttribute(name.getLocalPart());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getAttributeCount() {
        return values.length;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Object> getAttributes() {
        return UnmodifiableArrayList.wrap(values);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getDefaultGeometry() {
        // should be specified in the index as the default key (null)
        final Integer indexGeom = index.get(null);

        Object defaultGeometry = indexGeom != null ? values[indexGeom] : null;

        // not found? do we have a default geometry at all?
        if (defaultGeometry == null) {
            final GeometryDescriptor geometryDescriptor = featureType.getGeometryDescriptor();
            if (geometryDescriptor != null) {
                final Integer defaultGeomIndex = index.get(geometryDescriptor.getName().getLocalPart());
                defaultGeometry = getAttribute(defaultGeomIndex.intValue());
            }
        }
//        // not found? Ok, let's do a lookup then...
//        if ( defaultGeometry == null ) {
//            for ( Object o : values ) {
//                if ( o instanceof Geometry ) {
//                    defaultGeometry = o;
//                    break;
//                }
//            }
//        }

        return defaultGeometry;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public SimpleFeatureType getType() {
        return featureType;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setAttribute(final int index, final Object value) throws IndexOutOfBoundsException {
        // first do conversion
        final Object converted = Converters.convert(value, featureType.getDescriptor(index).getType().getBinding());
        // if necessary, validation too
        if (validating) {
            Types.validate(featureType.getDescriptor(index), converted);
        }
        // finally set the value into the feature
        values[index] = converted;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setAttribute(final String name, final Object value) {
        final Integer idx = index.get(name);
        if (idx == null) {
            throw new SimpleIllegalAttributeException("Unknown attribute " + name);
        }
        setAttribute(idx, value);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setAttribute(final Name name, final Object value) {
        setAttribute(name.getLocalPart(), value);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setAttributes(final List<Object> values) {
        for (int i = 0; i < this.values.length; i++) {
            this.values[i] = values.get(i);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setAttributes(final Object[] values) {
        for (int i = 0; i < this.values.length; i++) {
            this.values[i] = values[i];
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setDefaultGeometry(final Object geometry) {
        Integer geometryIndex = index.get(null);
        if (geometryIndex != null) {
            setAttribute(geometryIndex, geometry);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public BoundingBox getBounds() {
        //TODO: cache this value
        final JTSEnvelope2D bounds = new JTSEnvelope2D(featureType.getCoordinateReferenceSystem());
        for (Object o : values) {
            if (o instanceof Geometry) {
                final Geometry g = (Geometry) o;
                //TODO: check userData for crs... and ensure its of the same
                // crs as the feature type
                if (bounds.isNull()) {
                    bounds.init(g.getEnvelopeInternal());
                } else {
                    bounds.expandToInclude(g.getEnvelopeInternal());
                }
            }
        }

        return bounds;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public GeometryAttribute getDefaultGeometryProperty() {
        final GeometryDescriptor geometryDescriptor = featureType.getGeometryDescriptor();
        GeometryAttribute geometryAttribute = null;
        if (geometryDescriptor != null) {
            Object defaultGeometry = getDefaultGeometry();
            geometryAttribute = new DefaultGeometryAttribute(defaultGeometry, geometryDescriptor, null);
        }
        return geometryAttribute;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setDefaultGeometryProperty(final GeometryAttribute geometryAttribute) {
        if (geometryAttribute != null) {
            setDefaultGeometry(geometryAttribute.getValue());
        } else {
            setDefaultGeometry(null);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Property> getProperties() {
        return new AttributeList();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Property> getProperties(final Name name) {
        return getProperties(name.getLocalPart());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<Property> getProperties(final String name) {
        final Integer idx = index.get(name);
        if (idx != null) {
            // cast temporarily to a plain collection to avoid type problems with generics
            Collection c = Collections.singleton(new Attribute(idx));
            return c;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Property getProperty(final Name name) {
        return getProperty(name.getLocalPart());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Property getProperty(final String name) {
        final Integer idx = index.get(name);
        if (idx == null) {
            return null;
        } else {
            final int index = idx;
            AttributeDescriptor descriptor = featureType.getDescriptor(index);
            if (descriptor instanceof GeometryDescriptor) {
                return new DefaultGeometryAttribute(values[index], (GeometryDescriptor) descriptor, null);
            } else {
                return new Attribute(index);
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Collection<? extends Property> getValue() {
        return getProperties();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setValue(final Collection<Property> values) {
        int i = 0;
        for (Property p : values) {
            this.values[i] = p.getValue();
            i++;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setValue(final Object newValue) {
        setValue((Collection<Property>) newValue);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AttributeDescriptor getDescriptor() {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Name getName() {
        return null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isNillable() {
        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<Object, Object> getUserData() {
        if (userData == null) {
            userData = new HashMap<Object, Object>();
        }
        return userData;
    }

    /**
     * returns a unique code for this feature
     *
     * @return A unique int
     */
    @Override
    public int hashCode() {
        return id.hashCode() * featureType.hashCode();
    }

    /**
     * override of equals.  Returns if the passed in object is equal to this.
     *
     * @param obj the Object to test for equality.
     *
     * @return <code>true</code> if the object is equal, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DefaultSimpleFeature)) {
            return false;
        }

        DefaultSimpleFeature feat = (DefaultSimpleFeature) obj;

        // this check shouldn't exist, by contract,
        //all features should have an ID.
        if (id == null) {
            if (feat.getIdentifier() != null) {
                return false;
            }
        }

        if (!id.equals(feat.getIdentifier())) {
            return false;
        }

        if (!feat.getFeatureType().equals(featureType)) {
            return false;
        }

        for (int i = 0, ii = values.length; i < ii; i++) {
            Object otherAtt = feat.getAttribute(i);

            if (values[i] == null) {
                if (otherAtt != null) {
                    return false;
                }
            } else {
                if (!values[i].equals(otherAtt)) {
                    if (values[i] instanceof Geometry && otherAtt instanceof Geometry) {
                        // we need to special case Geometry
                        // as JTS is broken Geometry.equals( Object )
                        // and Geometry.equals( Geometry ) are different
                        // (We should fold this knowledge into AttributeType...)
                        if (!((Geometry) values[i]).equals(
                                (Geometry) otherAtt)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void validate() {
        for (int i = 0; i < values.length; i++) {
            AttributeDescriptor descriptor = featureType.getDescriptor(i);
            Types.validate(descriptor, values[i]);
        }
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
    private class Attribute implements org.opengis.feature.Attribute {

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
            Types.validate(getDescriptor(), values[index]);
        }
    }
}
