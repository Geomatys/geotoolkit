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
import org.opengis.style.LabelPlacement;
import org.opengis.style.LinePlacement;
import org.opengis.style.PointPlacement;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLabelPlacement extends FXStyleElementController<FXLabelPlacement, LabelPlacement>{

    @FXML
    protected RadioButton uiPointCheck;
    @FXML
    protected RadioButton uiLineCheck;
    @FXML
    protected FXLinePlacement uiLine;
    @FXML
    protected FXPointPlacement uiPoint;
    
    private ToggleGroup group;
    
    @FXML
    void updateChoice(ActionEvent event){
        if(updating) return;
        if(uiLineCheck.isSelected()){
            uiLine.setVisible(true);
            uiPoint.setVisible(false);
        }else{
            uiLine.setVisible(false);
            uiPoint.setVisible(true);
        }
        rebuildValue();
    }
    
    private void rebuildValue(){
        if(uiLineCheck.isSelected()){
            value.set(uiLine.valueProperty().get());
        }else{
            value.set(uiPoint.valueProperty().get());
        }
    }
    
    @Override
    public Class<LabelPlacement> getEditedClass() {
        return LabelPlacement.class;
    }

    @Override
    public LabelPlacement newValue() {
        return getStyleFactory().labelPlacement();
    }
    
    @Override
    public void initialize() {
        super.initialize();   
        group = new ToggleGroup();
        uiPointCheck.setToggleGroup(group);
        uiLineCheck.setToggleGroup(group);
        
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            rebuildValue();
        };
        
        uiPoint.valueProperty().addListener(changeListener);
        uiLine.valueProperty().addListener(changeListener);
    }
    
    @Override
    protected void updateEditor(LabelPlacement styleElement) {
        uiLine.setVisible(styleElement instanceof LinePlacement);
        uiPoint.setVisible(!(styleElement instanceof LinePlacement));
        
        if(styleElement instanceof LinePlacement){
            uiLineCheck.setSelected(true);
            uiPoint.valueProperty().setValue((PointPlacement)null);
            uiLine.valueProperty().setValue((LinePlacement)styleElement);
        }else{
            uiPointCheck.setSelected(true);
            uiPoint.valueProperty().setValue((PointPlacement)styleElement);
            uiLine.valueProperty().setValue((LinePlacement)null);
        }
    }
    
}
