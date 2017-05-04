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

package org.geotoolkit.gui.javafx.style;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.style.StyleConstants;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.ContrastMethod;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXContrastEnhancement extends FXStyleElementController<ContrastEnhancement>{

    @FXML
    private RadioButton uiNone;
    @FXML
    private RadioButton uiNormalize;
    @FXML
    private RadioButton uiHistogram;
    @FXML
    private FXNumberExpression uiGamma;

    private final ToggleGroup group = new ToggleGroup();

    @FXML
    void updateType(ActionEvent event){
        rebuildValue();
    }

    @Override
    public Class<ContrastEnhancement> getEditedClass() {
        return ContrastEnhancement.class;
    }

    @Override
    public ContrastEnhancement newValue() {
        return StyleConstants.DEFAULT_CONTRAST_ENHANCEMENT;
    }

    private void rebuildValue(){
        ContrastMethod cm = ContrastMethod.NONE;
        if(uiHistogram.isSelected()){
            cm = ContrastMethod.HISTOGRAM;
        }else if(uiNormalize.isSelected()){
            cm = ContrastMethod.NORMALIZE;
        }
        value.set(getStyleFactory().contrastEnhancement(uiGamma.valueProperty().get(), cm));
    }

    @Override
    public void initialize() {
        super.initialize();

        uiNone.setToggleGroup(group);
        uiNormalize.setToggleGroup(group);
        uiHistogram.setToggleGroup(group);

        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            rebuildValue();
        };

        uiGamma.valueProperty().addListener(changeListener);
    }

    @Override
    protected void updateEditor(ContrastEnhancement styleElement) {
        uiGamma.valueProperty().set(styleElement.getGammaValue());
        final ContrastMethod cm = styleElement.getMethod();
        if(ContrastMethod.HISTOGRAM.equals(cm)){
            uiHistogram.setSelected(true);
        }else if(ContrastMethod.NORMALIZE.equals(cm)){
            uiNormalize.setSelected(true);
        }else{
            uiNone.setSelected(true);
        }
    }

}
