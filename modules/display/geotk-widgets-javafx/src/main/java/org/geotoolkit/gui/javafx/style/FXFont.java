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
import javafx.fxml.FXML;
import org.opengis.style.Font;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXFont extends FXStyleElementController<FXFont, Font>{

    @FXML
    protected FXNumberExpression uiSize;
    @FXML
    protected FXListExpression uiWeight;
    @FXML
    protected FXListExpression uiStyle;
    @FXML
    protected FXListExpression uiFamily;
        
    @Override
    public Class<Font> getEditedClass() {
        return Font.class;
    }

    @Override
    public Font newValue() {
        return getStyleFactory().font();
    }
    
    @Override
    public void initialize() {
        super.initialize();        
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            value.set(getStyleFactory().font(
                    uiFamily.valueProperty().get(), 
                    uiStyle.valueProperty().get(), 
                    uiWeight.valueProperty().get(), 
                    uiSize.valueProperty().get()));
        };
        
        uiSize.valueProperty().addListener(changeListener);
        uiWeight.valueProperty().addListener(changeListener);
        uiStyle.valueProperty().addListener(changeListener);
        uiFamily.valueProperty().addListener(changeListener);
    }
    
    @Override
    protected void updateEditor(Font styleElement) {
        uiSize.valueProperty().setValue(styleElement.getSize());
        uiWeight.valueProperty().setValue(styleElement.getWeight());
        uiStyle.valueProperty().setValue(styleElement.getStyle());
        uiFamily.valueProperty().setValue(styleElement.getFamily().get(0));
    }
    
}
