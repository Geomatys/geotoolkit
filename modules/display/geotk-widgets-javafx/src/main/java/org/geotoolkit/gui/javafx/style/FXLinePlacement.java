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
import org.opengis.style.LinePlacement;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLinePlacement extends FXStyleElementController<LinePlacement>{

    @FXML
    protected FXNumberExpression uiOffset;
    @FXML
    protected FXNumberExpression uiInitialGap;
    @FXML
    protected FXNumberExpression uiGap;
    @FXML
    protected CheckBox uiGeneralized;
    @FXML
    protected CheckBox uiAligned;
    @FXML
    protected CheckBox uiRepeated;

    @Override
    public Class<LinePlacement> getEditedClass() {
        return LinePlacement.class;
    }

    @Override
    public LinePlacement newValue() {
        return getStyleFactory().linePlacement(StyleConstants.DEFAULT_LINEPLACEMENT_OFFSET);
    }

    @Override
    public void initialize() {
        super.initialize();
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            value.set(getStyleFactory().linePlacement(
                    uiOffset.valueProperty().get(),
                    uiInitialGap.valueProperty().get(),
                    uiGap.valueProperty().get(),
                    uiRepeated.isSelected(),
                    uiAligned.isSelected(),
                    uiGeneralized.isSelected()));
        };

        uiGeneralized.selectedProperty().addListener(changeListener);
        uiAligned.selectedProperty().addListener(changeListener);
        uiRepeated.selectedProperty().addListener(changeListener);
        uiOffset.valueProperty().addListener(changeListener);
        uiInitialGap.valueProperty().addListener(changeListener);
        uiGap.valueProperty().addListener(changeListener);
    }

    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiOffset.setLayer(layer);
        uiInitialGap.setLayer(layer);
        uiGap.setLayer(layer);
    }

    @Override
    protected void updateEditor(LinePlacement styleElement) {
        if(styleElement==null) styleElement = StyleConstants.DEFAULT_LINEPLACEMENT;

        uiOffset.valueProperty().set(styleElement.getPerpendicularOffset());
        uiInitialGap.valueProperty().set(styleElement.getInitialGap());
        uiGap.valueProperty().set(styleElement.getGap());
        uiGeneralized.selectedProperty().set(styleElement.isGeneralizeLine());
        uiAligned.selectedProperty().set(styleElement.IsAligned());
        uiRepeated.selectedProperty().set(styleElement.isRepeated());
    }

}
