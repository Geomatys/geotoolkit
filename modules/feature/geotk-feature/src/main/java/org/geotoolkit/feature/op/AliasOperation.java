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
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.feature.Attribute;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class AliasOperation extends AbstractOperationType {

    private final Name refName;

    public AliasOperation(Name newName, Name refName, AttributeType type) {
        super(newName, null, type, EMPTY_PARAMS);
        this.refName = refName;
    }

    @Override
    public Attribute invokeGet(ComplexAttribute feature, ParameterValueGroup parameters) {
        final Property prop = feature.getProperty(refName);
        return (prop==null)  ? null : (Attribute)prop;
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
                prop = (ComplexAttribute) FeatureUtilities.defaultProperty(desc,"noid");
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
