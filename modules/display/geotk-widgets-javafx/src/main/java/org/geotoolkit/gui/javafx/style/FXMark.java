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
import org.opengis.style.Mark;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXMark extends FXStyleElementController<FXMark, Mark>{

    @FXML
    protected FXListExpression uiWkt;    
    @FXML
    protected FXStroke uiStroke;    
    @FXML
    protected FXFill uiFill;
        
    @Override
    public Class<Mark> getEditedClass() {
        return Mark.class;
    }

    @Override
    public Mark newValue() {
        return getStyleFactory().mark();
    }
    
    @Override
    public void initialize() {
        super.initialize();        
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            value.set(getStyleFactory().mark(
                    uiWkt.valueProperty().get(), 
                    uiStroke.valueProperty().get(), 
                    uiFill.valueProperty().get()));
        };
        
        uiFill.valueProperty().addListener(changeListener);
        uiStroke.valueProperty().addListener(changeListener);
        uiWkt.valueProperty().addListener(changeListener);
    }
    
    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiWkt.setLayer(layer);
        uiStroke.setLayer(layer);
        uiFill.setLayer(layer);
    }
    
    @Override
    protected void updateEditor(Mark styleElement) {
        uiWkt.valueProperty().setValue(styleElement.getWellKnownName());
        uiFill.valueProperty().setValue(styleElement.getFill());
        uiStroke.valueProperty().setValue(styleElement.getStroke());
    }
    
}
