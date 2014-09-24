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
import org.opengis.style.PointPlacement;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXPointPlacement extends FXStyleElementController<FXPointPlacement, PointPlacement>{

    @FXML
    protected FXNumberExpression uiRotation;    
    @FXML
    protected FXAnchorPoint uiAnchor;
    @FXML
    protected FXDisplacement uiDisplacement;
        
    @Override
    public Class<PointPlacement> getEditedClass() {
        return PointPlacement.class;
    }

    @Override
    public PointPlacement newValue() {
        return getStyleFactory().pointPlacement();
    }
    
    @Override
    public void initialize() {
        super.initialize();        
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            value.set(getStyleFactory().pointPlacement(
                    uiAnchor.valueProperty().get(), 
                    uiDisplacement.valueProperty().get(), 
                    uiRotation.valueProperty().get()));
        };
        
        uiAnchor.valueProperty().addListener(changeListener);
        uiDisplacement.valueProperty().addListener(changeListener);
        uiRotation.valueProperty().addListener(changeListener);
    }
    
    @Override
    protected void updateEditor(PointPlacement styleElement) {
        uiAnchor.valueProperty().setValue(styleElement.getAnchorPoint());
        uiDisplacement.valueProperty().setValue(styleElement.getDisplacement());
        uiRotation.valueProperty().setValue(styleElement.getRotation());
    }
    
}
