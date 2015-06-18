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
import org.apache.sis.util.ArgumentChecks;
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
    
    protected final SimpleObjectProperty<ParameterDescriptor> currentParamDesc = new SimpleObjectProperty<>();
    protected final SimpleObjectProperty<AttributeType> currentAttributeType = new SimpleObjectProperty<>();
    
    public final FXValueEditorSpi spi;
    
    protected FXValueEditor(final FXValueEditorSpi originatingSpi) {
        ArgumentChecks.ensureNonNull("Originating Spi", originatingSpi);
        spi = originatingSpi;
    }
            
    /**
     * Configure current editor data type, to specify what type of data it must provide.
     * @param attType An attribute type definning data type to work on.
     */
    public void setAttributeType(AttributeType attType) {
        if (!spi.canHandle(attType))
            throw new IllegalArgumentException("Given attribute type ("+attType+") cannot be handled by current editor !");
        this.currentAttributeType.set(attType);
    }

    /**
     * Configure current editor data type, to specify what type of data it must provide.
     * @param paramDesc A parameter descriptor definning data type to work on.
     */
    public void setParamDesc(ParameterDescriptor paramDesc) {
        if (!spi.canHandle(paramDesc))
            throw new IllegalArgumentException("Given descriptor ("+paramDesc+") cannot be handled by current editor !");
        this.currentParamDesc.set(paramDesc);
    }
    
    /**
     *   
     * @return Editor current input, compliant with given type/descriptor. Never null, but property value can be null.
     */
    public abstract Property valueProperty();
    
    /**
     * 
     * @return Type of object required by set type/descriptor.
     */
    protected Class getValueClass() {
        if (currentParamDesc.get() != null) {
            return currentParamDesc.get().getValueClass();
        } else if (currentAttributeType.get() != null) {
            return currentAttributeType.get().getValueClass();
        } else {
            return Object.class;
        }
    }

    /**
     * @return JavaFX node in which editor is displayed.
     */
    public abstract Node getComponent();
    
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
