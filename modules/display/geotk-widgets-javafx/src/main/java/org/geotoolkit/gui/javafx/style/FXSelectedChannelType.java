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
import javafx.scene.control.TextField;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.SelectedChannelType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXSelectedChannelType extends FXStyleElementController<SelectedChannelType>{

    @FXML
    protected FXContrastEnhancement uiContrast;
    @FXML
    protected TextField uiName;
        
    @Override
    public Class<SelectedChannelType> getEditedClass() {
        return SelectedChannelType.class;
    }

    @Override
    public SelectedChannelType newValue() {
        return getStyleFactory().selectedChannelType("", (ContrastEnhancement)null);
    }
    
    @Override
    public void initialize() {
        super.initialize();        
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            value.set(getStyleFactory().selectedChannelType(uiName.getText(), uiContrast.valueProperty().get()));
        };
        
        uiContrast.valueProperty().addListener(changeListener);
        uiName.textProperty().addListener(changeListener);
    }
    
    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiContrast.setLayer(layer);
    }
    
    @Override
    protected void updateEditor(SelectedChannelType styleElement) {
        uiContrast.valueProperty().setValue(styleElement.getContrastEnhancement());
        uiName.textProperty().setValue(styleElement.getChannelName());
    }
    
}
