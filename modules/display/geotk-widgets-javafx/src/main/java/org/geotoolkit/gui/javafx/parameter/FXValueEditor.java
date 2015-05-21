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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.geotoolkit.filter.visitor.DefaultFilterVisitor;
import org.opengis.feature.AttributeType;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.parameter.ParameterDescriptor;

/**
 * An editor which aim is to provide a javafx control adapted for setting of a property
 * of a given type. 
 * 
 * By setting a {@link ParameterDescriptor} or an {@link AttributeType} marked as 
 * compatible by {@link #canHandle(org.opengis.feature.AttributeType) } methods, 
 * the editor will adapt itself to provide a {@link #valueProperty() } compatible 
 * with the class given by input descriptor/type.
 * 
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
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
    
    protected final SimpleObjectProperty<ParameterDescriptor> currentParamDesc = new SimpleObjectProperty<>();
    protected final SimpleObjectProperty<AttributeType> currentAttributeType = new SimpleObjectProperty<>();
    
    public boolean canHandle(AttributeType property){
        return canHandle(property.getValueClass());
    }
        
    public boolean canHandle(ParameterDescriptor param){
        return canHandle(param.getValueClass());
    }
    
    public abstract boolean canHandle(Class binding);

    public void setAttributeType(AttributeType attType) {
        this.currentAttributeType.set(attType);
    }

    public void setParamDesc(ParameterDescriptor paramDesc) {
        this.currentParamDesc.set(paramDesc);
    }

    /**
     *   
     * @return Editor current input, compliant with given type/descriptor. Never null, but property value can be null.
     */
    public abstract Property valueProperty();
    
    protected Class getValueClass() {
        if (currentParamDesc.get() != null) {
            return currentParamDesc.get().getValueClass();
        } else if (currentAttributeType.get() != null) {
            return currentAttributeType.get().getValueClass();
        } else {
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
    
    /**
     * Search for a 'In' restriction filter.
     * return list of possible values if restriction exist. null otherwise
     */
    protected static List<Object> extractChoices(AttributeType at) {
        if(!(at instanceof org.geotoolkit.feature.type.PropertyType)) return null;
        
        final org.geotoolkit.feature.type.PropertyType candidate = (org.geotoolkit.feature.type.PropertyType) at;
        
        Class clazz = candidate.getBinding();
        final List choices = new ArrayList();
        final List<Filter> restrictions = candidate.getRestrictions();
        for(Filter f : restrictions){
            f.accept(new DefaultFilterVisitor() {
                @Override
                public Object visit(Function expression, Object data) {
                    if(expression.getName().equalsIgnoreCase("in")){
                        final List<Expression> values = expression.getParameters();
                        for(int i=1,n=values.size();i<n;i++){
                            //we expect values to be literals
                            choices.add(values.get(i).evaluate(null));
                        }
                    }
                    return data;
                }

            }, choices);
        }
        
        if(choices.isEmpty()){
            return null;
        }else{
            if (Comparable.class.isAssignableFrom(clazz)) {
                Collections.sort(choices);
            }
            return choices;
        }
    }
}
