/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.feature.op;

import java.util.Collection;
import java.util.Map;
import org.geotoolkit.feature.Attribute;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.AbstractOperationType;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.AttributeType;
import org.geotoolkit.feature.type.ComplexType;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.feature.IdentifiedType;
import org.opengis.filter.identity.Identifier;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AliasOperation extends AbstractOperationType {

    private final PropertyDescriptor desc;
    private final GenericName refName;

    public AliasOperation(GenericName newName, GenericName refName, PropertyDescriptor type) {
        super(newName, null, (AttributeType)type.getType(), EMPTY_PARAMS);
        this.desc = type;
        this.refName = refName;
    }

    public GenericName getRefName() {
        return refName;
    }

    private Property createDefault(){
        return FeatureUtilities.defaultProperty(desc, "");
    }

    @Override
    public Attribute invokeGet(ComplexAttribute feature, ParameterValueGroup parameters) {
        Property prop = feature.getProperty(refName);
        if(prop==null) return null;
        
        //check the type, the alias may have a restricted type
        final IdentifiedType resType = getResult();
        if(resType instanceof ComplexType){
            if(prop.getType().equals(resType) ){
                if(resType instanceof ComplexType){
                    return new AliasComplexAttribute((ComplexAttribute) prop);
                }else{
                    return new AliasAttribute((Attribute) prop);
                }
            }
        }else if(resType instanceof AttributeType){
            final Class clazz = ((AttributeType)resType).getBinding();
            final Object value = prop.getValue();
            if(value==null) return null;
            if(clazz.isAssignableFrom(value.getClass())){
                prop = createDefault();
                prop.setValue(value);
                return (Attribute) prop;
            }
        }
        return null;
    }

    @Override
    public void invokeSet(ComplexAttribute feature, Object value) {
        if(value instanceof ComplexAttribute){
            final ComplexAttribute ca = (ComplexAttribute) value;

            ComplexAttribute prop = (ComplexAttribute) feature.getProperty(refName);
            if(prop!=null)feature.getValue().remove(prop);

            if(prop==null){
                PropertyDescriptor desc = feature.getType().getDescriptor(refName);
                //make a descriptor with same name and parameters but different type
                final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
                adb.copy(desc);
                adb.setType(ca.getType());
                desc = adb.buildDescriptor();
                prop = (ComplexAttribute) FeatureUtilities.defaultProperty(desc,"");
                feature.getProperties().add(prop);
            }
            prop.getValue().clear();
            ((Collection)prop.getValue()).addAll(ca.getValue());

        }else{
            Property prop = feature.getProperty(refName);
            if(prop==null){
                final PropertyDescriptor desc = feature.getType().getDescriptor(refName);
                prop = FeatureUtilities.defaultProperty(desc);
                feature.getProperties().add(prop);
            }
            prop.setValue(value);
        }
    }

    private class AliasAttribute implements Attribute {

        private final Attribute base;

        public AliasAttribute(Attribute base) {
            this.base = base;
        }

        @Override
        public GenericName getName() {
            return desc.getName();
        }

        @Override
        public AttributeType getType() {
            return (AttributeType) desc.getType();
        }

        @Override
        public Object getValue() throws IllegalStateException {
            return base.getValue();
        }

        @Override
        public void setValue(Object value) throws IllegalArgumentException {
            base.setValue(value);
        }

        @Override
        public Collection getValues() {
            return base.getValues();
        }

        @Override
        public void setValues(Collection values) throws IllegalArgumentException {
            base.setValues(values);
        }

        @Override
        public Map characteristics() {
            return base.characteristics();
        }

        @Override
        public AttributeDescriptor getDescriptor() {
            return (AttributeDescriptor) desc;
        }

        @Override
        public Identifier getIdentifier() {
            return base.getIdentifier();
        }

        @Override
        public void validate() {
            base.validate();
        }

        @Override
        public boolean isNillable() {
            return base.isNillable();
        }

        @Override
        public Map<Object, Object> getUserData() {
            return base.getUserData();
        }
        
    }

    private class AliasComplexAttribute implements ComplexAttribute {

        private final ComplexAttribute base;

        public AliasComplexAttribute(ComplexAttribute base) {
            this.base = base;
        }

        @Override
        public GenericName getName() {
            return desc.getName();
        }

        @Override
        public ComplexType getType() {
            return (ComplexType) desc.getType();
        }

        @Override
        public Collection getValue() throws IllegalStateException {
            return base.getValue();
        }

        @Override
        public void setValue(Object value) throws IllegalArgumentException {
            base.setValue(value);
        }

        @Override
        public Collection getValues() {
            return base.getValues();
        }

        @Override
        public void setValues(Collection values) throws IllegalArgumentException {
            base.setValues(values);
        }

        @Override
        public Map characteristics() {
            return base.characteristics();
        }

        @Override
        public AttributeDescriptor getDescriptor() {
            return (AttributeDescriptor) desc;
        }

        @Override
        public Identifier getIdentifier() {
            return base.getIdentifier();
        }

        @Override
        public void validate() {
            base.validate();
        }

        @Override
        public boolean isNillable() {
            return base.isNillable();
        }

        @Override
        public Map<Object, Object> getUserData() {
            return base.getUserData();
        }

        @Override
        public void setValue(Collection<Property> values) {
            base.setValue(values);
        }

        @Override
        public Collection<Property> getProperties(GenericName name) {
            return base.getProperties(name);
        }

        @Override
        public Property getProperty(GenericName name) {
            return base.getProperty(name);
        }

        @Override
        public Collection<Property> getProperties(String name) {
            return base.getProperties(name);
        }

        @Override
        public Collection<Property> getProperties() {
            return base.getProperties();
        }

        @Override
        public Property getProperty(String name) {
            return base.getProperty(name);
        }

        @Override
        public Object getPropertyValue(String string) throws IllegalArgumentException {
            return base.getPropertyValue(string);
        }

        @Override
        public void setPropertyValue(String string, Object o) throws IllegalArgumentException {
            base.setPropertyValue(string, o);
        }

    }

}
