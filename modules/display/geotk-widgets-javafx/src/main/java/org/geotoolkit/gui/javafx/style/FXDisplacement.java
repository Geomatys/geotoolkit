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
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.Displacement;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXDisplacement extends FXStyleElementController<Displacement>{

    @FXML protected FXNumberExpression uiX;    
    @FXML protected FXNumberExpression uiY;
        
    @Override
    public Class<Displacement> getEditedClass() {
        return Displacement.class;
    }

    @Override
    public Displacement newValue() {
        return getStyleFactory().displacement();
    }
    
    @Override
    public void initialize() {
        super.initialize();        
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            value.set(getStyleFactory().displacement(uiX.valueProperty().get(), uiY.valueProperty().get()));
        };
        
        uiX.valueProperty().addListener(changeListener);
        uiY.valueProperty().addListener(changeListener);
    }
    
    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiX.setLayer(layer);
        uiY.setLayer(layer);
    }
    
    @Override
    protected void updateEditor(Displacement styleElement) {
        uiX.valueProperty().setValue(styleElement.getDisplacementX());
        uiY.valueProperty().setValue(styleElement.getDisplacementY());
    }
    
}
