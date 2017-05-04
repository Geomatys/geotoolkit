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
import javafx.scene.control.CheckBox;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.style.ShadedRelief;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXShadedRelief extends FXStyleElementController<ShadedRelief>{

    @FXML
    private FXNumberExpression uiFactor;
    @FXML
    private CheckBox uiBrightness;

    @Override
    public Class<ShadedRelief> getEditedClass() {
        return ShadedRelief.class;
    }

    @Override
    public ShadedRelief newValue() {
        return StyleConstants.DEFAULT_SHADED_RELIEF;
    }

    @Override
    public void initialize() {
        super.initialize();
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            value.set(getStyleFactory().shadedRelief(uiFactor.valueProperty().get(), uiBrightness.isSelected()));
        };

        uiFactor.valueProperty().addListener(changeListener);
        uiBrightness.selectedProperty().addListener(changeListener);
    }

    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiFactor.setLayer(layer);
    }

    @Override
    protected void updateEditor(ShadedRelief styleElement) {
        uiFactor.valueProperty().setValue(styleElement.getReliefFactor());
        uiBrightness.selectedProperty().setValue(styleElement.isBrightnessOnly());
    }

}
