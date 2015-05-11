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
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.ComplexAttribute;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.AbstractOperationType;
import org.geotoolkit.feature.type.AttributeType;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.feature.Attribute;
import org.opengis.feature.IdentifiedType;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AliasOperation extends AbstractOperationType {

    private final PropertyDescriptor desc;
    private final Name refName;

    public AliasOperation(Name newName, Name refName, PropertyDescriptor type) {
        super(newName, null, (AttributeType)type.getType(), EMPTY_PARAMS);
        this.desc = type;
        this.refName = refName;
    }

    public Name getRefName() {
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
            if(prop.getType().equals(resType)){
                return (Attribute) prop;
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
}
