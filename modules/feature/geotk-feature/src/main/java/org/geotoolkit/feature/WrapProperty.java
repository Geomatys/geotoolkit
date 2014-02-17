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
import java.util.List;
import java.util.Map;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AssociationDescriptor;
import org.opengis.feature.type.AssociationType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.geometry.BoundingBox;

/**
 * Decorate a given property with a descriptor.
 * This can be used by features who encapsulate other features as properties.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
abstract class WrapProperty<P extends org.opengis.feature.Property> extends AbstractProperty {

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

        Property(final org.opengis.feature.Property prop, final PropertyDescriptor desc){
            super(prop,desc);
        }

    }

    static class Association<T extends org.opengis.feature.Association>
            extends WrapProperty<T> implements org.opengis.feature.Association{

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
        public org.opengis.feature.Attribute getValue() {
            return prop.getValue();
        }

        @Override
        public AttributeType getRelatedType() {
            return prop.getRelatedType();
        }

    }

    static class Attribute<T extends org.opengis.feature.Attribute>
            extends WrapProperty<T> implements org.opengis.feature.Attribute{

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
        public void validate() throws IllegalAttributeException {
            prop.validate();
        }
    }

    static class GeometryAttribute<T extends org.opengis.feature.GeometryAttribute>
            extends Attribute<T> implements org.opengis.feature.GeometryAttribute{

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
            return prop.getBounds();
        }

        @Override
        public void setBounds(final BoundingBox bounds) {
            prop.setBounds(bounds);
        }

    }

    static class ComplexAttribute<T extends org.opengis.feature.ComplexAttribute>
            extends Attribute<T> implements org.opengis.feature.ComplexAttribute{

        ComplexAttribute(final T prop, final AttributeDescriptor desc){
            super(prop,desc);
        }

        @Override
        public ComplexType getType() {
            return (ComplexType)super.getType();
        }

        @Override
        public void setValue(final Collection<org.opengis.feature.Property> values) {
            prop.setValue(values);
        }

        @Override
        public Collection<? extends org.opengis.feature.Property> getValue() {
            return prop.getValue();
        }

        @Override
        public Collection<org.opengis.feature.Property> getProperties(final Name name) {
            return prop.getProperties(name);
        }

        @Override
        public org.opengis.feature.Property getProperty(final Name name) {
            return prop.getProperty(name);
        }

        @Override
        public Collection<org.opengis.feature.Property> getProperties(final String name) {
            return prop.getProperties(name);
        }

        @Override
        public Collection<org.opengis.feature.Property> getProperties() {
            return prop.getProperties();
        }

        @Override
        public org.opengis.feature.Property getProperty(final String name) {
            return prop.getProperty(name);
        }

    }

    static class Feature<T extends org.opengis.feature.Feature>
            extends ComplexAttribute<T> implements org.opengis.feature.Feature{

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
            return prop.getBounds();
        }

        @Override
        public org.opengis.feature.GeometryAttribute getDefaultGeometryProperty() {
            return prop.getDefaultGeometryProperty();
        }

        @Override
        public void setDefaultGeometryProperty(final org.opengis.feature.GeometryAttribute geometryAttribute) {
            prop.setDefaultGeometryProperty(geometryAttribute);
        }

    }

    static class SimpleFeature extends Feature<org.opengis.feature.simple.SimpleFeature>
            implements org.opengis.feature.simple.SimpleFeature{

        SimpleFeature(final org.opengis.feature.simple.SimpleFeature prop, final AttributeDescriptor desc){
            super(prop,desc);
        }

        @Override
        public String getID() {
            return prop.getID();
        }

        @Override
        public SimpleFeatureType getType() {
            return prop.getType();
        }

        @Override
        public SimpleFeatureType getFeatureType() {
            return prop.getFeatureType();
        }

        @Override
        public List<Object> getAttributes() {
            return prop.getAttributes();
        }

        @Override
        public void setAttributes(List<Object> values) {
            prop.setAttributes(values);
        }

        @Override
        public void setAttributes(Object[] values) {
            prop.setAttributes(values);
        }

        @Override
        public Object getAttribute(String name) {
            return prop.getAttribute(name);
        }

        @Override
        public void setAttribute(String name, Object value) {
            prop.setAttribute(name, value);
        }

        @Override
        public Object getAttribute(Name name) {
            return prop.getAttribute(name);
        }

        @Override
        public void setAttribute(Name name, Object value) {
            prop.setAttribute(name, value);
        }

        @Override
        public Object getAttribute(int index) throws IndexOutOfBoundsException {
            return prop.getAttribute(index);
        }

        @Override
        public void setAttribute(int index, Object value) throws IndexOutOfBoundsException {
            prop.setAttribute(index, value);
        }

        @Override
        public int getAttributeCount() {
            return prop.getAttributeCount();
        }

        @Override
        public Object getDefaultGeometry() {
            return prop.getDefaultGeometry();
        }

        @Override
        public void setDefaultGeometry(Object geometry) {
            prop.setDefaultGeometry(geometry);
        }

    }


}
