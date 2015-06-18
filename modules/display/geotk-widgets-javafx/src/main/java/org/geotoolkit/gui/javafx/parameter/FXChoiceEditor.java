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
import java.util.Set;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import static org.geotoolkit.gui.javafx.parameter.FXValueEditor.extractChoices;
import org.opengis.feature.AttributeType;
import org.opengis.parameter.ParameterDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXChoiceEditor extends FXValueEditor{

    private final BorderPane pane = new BorderPane();
    private final ComboBox guiCombo = new ComboBox();

    public FXChoiceEditor(Spi originatingSpi) {
        super(originatingSpi);
        currentAttributeType.addListener(this::updateChoices);
        currentParamDesc.addListener(this::updateChoices);
    }
    
    protected void updateChoices(ObservableValue observable, Object oldValue, Object newValue) {
        pane.getChildren().clear();
        final List choices;
        if (newValue instanceof AttributeType) {
            choices = extractChoices((AttributeType) newValue);
        } else if (newValue instanceof ParameterDescriptor) {
            Set validValues = ((ParameterDescriptor)newValue).getValidValues();
            if (validValues != null && !validValues.isEmpty()) {
                choices = new ArrayList(validValues);
            } else {
                choices = Collections.EMPTY_LIST;
            }
        }else{
            choices = Collections.EMPTY_LIST;
        }

        if(choices.size()==1){
            //do not show a combobox when we don't have a real choice
            guiCombo.setItems(FXCollections.observableList(choices));
            guiCombo.valueProperty().setValue(choices.get(0));
            pane.setCenter(new Label(String.valueOf(choices.get(0))));
        }else{
            guiCombo.setItems(FXCollections.observableList(choices));
            pane.setCenter(guiCombo);
        }
    }

    @Override
    public Node getComponent() {
        return pane;
    }

    @Override
    public Property valueProperty() {
        return guiCombo.valueProperty();
    }

    public static final class Spi extends FXValueEditorSpi {

        @Override
        public boolean canHandle(AttributeType property) {
            return extractChoices(property) != null;
        }

        @Override
        public boolean canHandle(ParameterDescriptor param) {
            Set validValues = param.getValidValues();
            return validValues != null && !validValues.isEmpty();
        }

        @Override
        public boolean canHandle(Class binding) {
            return false;
        }

        @Override
        public FXValueEditor createEditor() {
            return new FXChoiceEditor(this);
        }
    }
}
