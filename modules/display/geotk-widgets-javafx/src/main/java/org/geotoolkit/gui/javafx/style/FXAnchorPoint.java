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
import javafx.scene.control.SpinnerValueFactory;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.AnchorPoint;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXAnchorPoint extends FXStyleElementController<AnchorPoint>{

    @FXML protected FXNumberExpression uiX;
    @FXML protected FXNumberExpression uiY;

    @Override
    public Class<AnchorPoint> getEditedClass() {
        return AnchorPoint.class;
    }

    @Override
    public AnchorPoint newValue() {
        return getStyleFactory().anchorPoint();
    }

    @Override
    public void initialize() {
        super.initialize();
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            value.set(getStyleFactory().anchorPoint(uiX.valueProperty().get(), uiY.valueProperty().get()));
        };

        uiX.getEditor().getSpinner().setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(-Double.MAX_VALUE, Double.MAX_VALUE, 0, 0.1));
        uiY.getEditor().getSpinner().setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(-Double.MAX_VALUE, Double.MAX_VALUE, 0, 0.1));
        uiX.valueProperty().addListener(changeListener);
        uiY.valueProperty().addListener(changeListener);
    }

    @Override
    protected void updateEditor(AnchorPoint styleElement) {
        uiX.valueProperty().setValue(styleElement.getAnchorPointX());
        uiY.valueProperty().setValue(styleElement.getAnchorPointY());
    }

    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiX.setLayer(layer);
        uiY.setLayer(layer);
    }

}
