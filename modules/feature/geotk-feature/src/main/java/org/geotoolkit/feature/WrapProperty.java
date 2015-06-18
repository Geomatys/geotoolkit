/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.geotoolkit.feature.type.AssociationDescriptor;
import org.geotoolkit.feature.type.AssociationType;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.AttributeType;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.feature.type.GeometryType;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.feature.type.PropertyType;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.opengis.util.GenericName;

/**
 * Decorate a given property with a descriptor.
 * This can be used by features who encapsulate other features as properties.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
abstract class WrapProperty<P extends org.geotoolkit.feature.Property> extends AbstractProperty {

    protected final P prop;
    protected final PropertyDescriptor desc;

    protected WrapProperty(final P prop, final PropertyDescriptor desc) {
        this.prop = prop;
        this.desc = desc;

        if (!(prop.getType().equals(desc.getType()) || FeatureTypeUtilities.isDecendedFrom(prop.getType(), desc.getType()))) {
            throw new IllegalArgumentException(
                    "Descriptor should have the same property type or subtype of the wrapped property.");
        }
    }

    @Override
    public Object getValue() {
        return prop.getValue();
    }

    @Override
    public void setValue(Object newValue) {
        prop.setValue(newValue);
    }

    public Collection<Object> getValues() {
        return Collections.<Object>singleton(getValue());
    }

    public void setValues(Collection<? extends Object> values) throws IllegalArgumentException {
        Object value = null;
        final Iterator<?> it = values.iterator();
        if (it.hasNext()) {
            value = it.next();
            if (it.hasNext()) {
                throw new IllegalArgumentException("Too many elements.");
            }
        }
        setValue(value);
    }

    @Override
    public PropertyType getType() {
        return prop.getType();
    }

    @Override
    public PropertyDescriptor getDescriptor() {
        return desc;
    }

    @Override
    public Map<Object, Object> getUserData() {
        return prop.getUserData();
    }

    static class Property extends WrapProperty {

        Property(final org.geotoolkit.feature.Property prop, final PropertyDescriptor desc){
            super(prop,desc);
        }

    }

    static class Association<T extends org.geotoolkit.feature.Association>
            extends WrapProperty<T> implements org.geotoolkit.feature.Association{

        Association(final T prop, final AssociationDescriptor desc){
            super(prop,desc);
        }

        @Override
        public AssociationDescriptor getDescriptor() {
            return (AssociationDescriptor)super.getDescriptor();
        }

        @Override
        public AssociationType getType() {
            return prop.getType();
        }

        @Override
        public org.geotoolkit.feature.Attribute getValue() {
            return prop.getValue();
        }

        @Override
        public AttributeType getRelatedType() {
            return prop.getRelatedType();
        }

    }

    static class Attribute<T extends org.geotoolkit.feature.Attribute>
            extends WrapProperty<T> implements org.geotoolkit.feature.Attribute{

        Attribute(final T prop, final AttributeDescriptor desc){
            super(prop,desc);
        }

        @Override
        public AttributeDescriptor getDescriptor() {
            return (AttributeDescriptor)super.getDescriptor();
        }

        @Override
        public AttributeType getType() {
            return (AttributeType)super.getType();
        }

        @Override
        public Identifier getIdentifier() {
            return prop.getIdentifier();
        }

        @Override
        public void validate() throws IllegalArgumentException {
            prop.validate();
        }
    }

    static class GeometryAttribute<T extends org.geotoolkit.feature.GeometryAttribute>
            extends Attribute<T> implements org.geotoolkit.feature.GeometryAttribute{

        GeometryAttribute(final T prop, final GeometryDescriptor desc){
            super(prop,desc);
        }

        @Override
        public GeometryType getType() {
            return (GeometryType)super.getType();
        }

        @Override
        public GeometryDescriptor getDescriptor() {
            return (GeometryDescriptor)super.getDescriptor();
        }

        @Override
        public BoundingBox getBounds() {
            return DefaultBoundingBox.castOrCopy(prop.getBounds());
        }

        @Override
        public void setBounds(final Envelope bounds) {
            prop.setBounds(bounds);
        }

    }

    static class ComplexAttribute<T extends org.geotoolkit.feature.ComplexAttribute>
            extends Attribute<T> implements org.geotoolkit.feature.ComplexAttribute{

        ComplexAttribute(final T prop, final AttributeDescriptor desc){
            super(prop,desc);
        }

        @Override
        public ComplexType getType() {
            return (ComplexType)super.getType();
        }

        @Override
        public void setValue(final Collection<org.geotoolkit.feature.Property> values) {
            prop.setValue(values);
        }

        @Override
        public Collection<? extends org.geotoolkit.feature.Property> getValue() {
            return prop.getValue();
        }

        @Override
        public Collection<org.geotoolkit.feature.Property> getProperties(final GenericName name) {
            return prop.getProperties(name);
        }

        @Override
        public org.geotoolkit.feature.Property getProperty(final GenericName name) {
            return prop.getProperty(name);
        }

        @Override
        public Collection<org.geotoolkit.feature.Property> getProperties(final String name) {
            return prop.getProperties(name);
        }

        @Override
        public Collection<org.geotoolkit.feature.Property> getProperties() {
            return prop.getProperties();
        }

        @Override
        public org.geotoolkit.feature.Property getProperty(final String name) {
            return prop.getProperty(name);
        }

        @Override
        public Object getPropertyValue(String string) throws IllegalArgumentException {
            return prop.getPropertyValue(string);
        }

        @Override
        public void setPropertyValue(String string, Object o) throws IllegalArgumentException {
            prop.setPropertyValue(string, o);
        }

    }

    static class Feature<T extends org.geotoolkit.feature.Feature>
            extends ComplexAttribute<T> implements org.geotoolkit.feature.Feature{

        Feature(final T prop, final AttributeDescriptor desc){
            super(prop,desc);
        }

        @Override
        public FeatureType getType() {
            return prop.getType();
        }

        @Override
        public FeatureId getIdentifier() {
            return prop.getIdentifier();
        }

        @Override
        public BoundingBox getBounds() {
            return DefaultBoundingBox.castOrCopy(prop.getBounds());
        }

        @Override
        public org.geotoolkit.feature.GeometryAttribute getDefaultGeometryProperty() {
            return prop.getDefaultGeometryProperty();
        }

        @Override
        public void setDefaultGeometryProperty(final org.geotoolkit.feature.GeometryAttribute geometryAttribute) {
            prop.setDefaultGeometryProperty(geometryAttribute);
        }

        @Override
        public void setProperty(org.opengis.feature.Property property) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object getPropertyValue(String name) throws IllegalArgumentException {
            return getProperty(name).getValue();
        }

        @Override
        public void setPropertyValue(String name, Object value) throws IllegalArgumentException {
            getProperty(name).setValue(value);
        }
    }

}
