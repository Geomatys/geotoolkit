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
import org.opengis.style.ExternalGraphic;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXExternalGraphic extends FXStyleElementController<ExternalGraphic>{

    @FXML
    protected TextField uiUrl;
    @FXML
    protected TextField uiMime;
    
    
    @Override
    public Class<ExternalGraphic> getEditedClass() {
        return ExternalGraphic.class;
    }

    @Override
    public ExternalGraphic newValue() {
        return getStyleFactory().externalGraphic("", "");
    }
    
    @Override
    public void initialize() {
        super.initialize();        
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            value.set(getStyleFactory().externalGraphic(uiUrl.getText(), uiMime.getText()));
        };
        
        uiUrl.textProperty().addListener(changeListener);
        uiMime.textProperty().addListener(changeListener);
    }
    
    @Override
    protected void updateEditor(ExternalGraphic styleElement) {
        uiMime.setText(styleElement.getFormat());
        uiUrl.setText(styleElement.getOnlineResource().toString());
    }
    
}
