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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.filter.visitor.DefaultFilterVisitor;
import org.opengis.feature.AttributeType;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.parameter.ParameterDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXChoiceEditor extends FXValueEditor{

    private final BorderPane pane = new BorderPane();
    private final ComboBox guiCombo = new ComboBox();
    private final Label guiLabel = new Label();

    public FXChoiceEditor() {
        guiCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                currentValue.setValue(newValue);
            }
        });
    }

    @Override
    public void setValue(Property value) {
        super.setValue(value);
        
        pane.getChildren().clear();
        
        List choices = null;
        if(currentAttributeType!=null){
            choices = extractChoices(currentAttributeType);
        }else{
            final org.geotoolkit.feature.type.PropertyType pt = FeatureTypeUtilities.toPropertyType(currentParamDesc);
            if(pt instanceof AttributeType){
                choices = extractChoices((AttributeType)pt);
            }
        }
        
        if(choices.size() == 1){
            guiLabel.setText(String.valueOf(choices.get(0)));
            pane.setCenter(guiLabel);
        }else{
            guiCombo.setItems(FXCollections.observableList(choices));
            guiCombo.getSelectionModel().select(value.getValue());
            pane.setCenter(guiCombo);
        }
    }
    
    @Override
    public boolean canHandle(AttributeType property) {
        return extractChoices(property) != null;
    }

    @Override
    public boolean canHandle(ParameterDescriptor param) {
        final org.geotoolkit.feature.type.PropertyType pt = FeatureTypeUtilities.toPropertyType(param);
        if(pt instanceof AttributeType){
            return canHandle((AttributeType)pt);
        }
        return false;
    }
    
    @Override
    public boolean canHandle(Class binding) {
        return false;
    }

    @Override
    public Node getComponent() {
        return pane;
    }
    
    /**
     * Search for a 'In' restriction filter.
     * return list of possible values if restriction exist. null otherwise
     */
    private static List<Object> extractChoices(AttributeType at){
        if(!(at instanceof org.geotoolkit.feature.type.PropertyType)) return null;
        
        final org.geotoolkit.feature.type.PropertyType candidate = (org.geotoolkit.feature.type.PropertyType) at;
        
        Class clazz = candidate.getBinding();
        final List choices = new ArrayList<Object>();
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
