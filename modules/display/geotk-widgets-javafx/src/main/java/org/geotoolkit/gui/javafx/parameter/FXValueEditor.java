/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.gui.javafx.parameter;

import java.util.List;
import javafx.beans.property.Property;
import javafx.scene.Node;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.opengis.feature.AttributeType;
import org.opengis.parameter.ParameterDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class FXValueEditor {
    
    private static final List<FXValueEditor> DEFAULTS;
    static {
        DEFAULTS = UnmodifiableArrayList.wrap(new FXValueEditor[]{
            new FXChoiceEditor(),
            new FXBooleanEditor(),
            new FXStringEditor(),
            new FXNumberEditor(),
            new FXURLEditor()
        });
        
    }
    
    protected Property currentValue;
    protected ParameterDescriptor currentParamDesc;
    protected AttributeType currentAttributeType;
    
    
    public boolean canHandle(AttributeType property){
        return canHandle(property.getValueClass());
    }
        
    public boolean canHandle(ParameterDescriptor param){
        return canHandle(param.getValueClass());
    }
    
    public abstract boolean canHandle(Class binding);

    public void setAttributeType(AttributeType attType) {
        this.currentAttributeType = attType;
    }

    public void setParamDesc(ParameterDescriptor paramDesc) {
        this.currentParamDesc = paramDesc;
    }

    public void setValue(Property value) {
        this.currentValue = value;
    }
    
    protected Class getValueClass(){
        if(currentParamDesc!=null){
            return currentParamDesc.getValueClass();
        }else if(currentAttributeType!=null){
            return currentAttributeType.getValueClass();
        }else{
            return Object.class;
        }
    }

    public abstract Node getComponent();
    
    public FXValueEditor copy(){
        try {
            return this.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalStateException("Editor "+FXValueEditor.class+" can not be copied.");
        }
    }
    
    public static List<FXValueEditor> getDefaultEditors(){
        return DEFAULTS;
    }
    
}
