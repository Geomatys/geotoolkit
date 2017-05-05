/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.style.dynamicrange;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.display2d.ext.dynamicrange.DynamicRangeSymbolizer;
import org.geotoolkit.gui.javafx.style.FXStyleElementController;
import org.geotoolkit.gui.javafx.style.FXTextExpression;
import org.geotoolkit.map.MapBuilder;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXDRBound extends FXStyleElementController<DynamicRangeSymbolizer.DRBound> {

    @FXML
    private ChoiceBox<String> uiMode;
    @FXML
    private FXTextExpression uiValue;

    @Override
    public Class<DynamicRangeSymbolizer.DRBound> getEditedClass() {
        return DynamicRangeSymbolizer.DRBound.class;
    }

    @Override
    public DynamicRangeSymbolizer.DRBound newValue() {
        return new DynamicRangeSymbolizer.DRBound();
    }

    @Override
    public void initialize() {
        super.initialize();

        uiMode.setItems(FXCollections.observableArrayList(
                DynamicRangeSymbolizer.DRBound.MODE_EXPRESSION,
                DynamicRangeSymbolizer.DRBound.MODE_PERCENT
            ));
        value.set(new DynamicRangeSymbolizer.DRBound());

        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            final DynamicRangeSymbolizer.DRBound element = new DynamicRangeSymbolizer.DRBound();
            element.setMode(uiMode.valueProperty().get());
            element.setValue(uiValue.valueProperty().get());
            value.set(element);
        };

        uiMode.valueProperty().addListener(changeListener);
        uiValue.valueProperty().addListener(changeListener);

        //mimic a feature with coverage properties
        final FeatureType ft = DynamicRangeSymbolizer.buildBandType();
        uiValue.setLayer(MapBuilder.createFeatureLayer(FeatureStoreUtilities.collection("", ft)));
    }

    @Override
    protected void updateEditor(DynamicRangeSymbolizer.DRBound styleElement) {
        if(styleElement!=null){
            uiMode.valueProperty().setValue(styleElement.getMode());
            uiValue.valueProperty().setValue(styleElement.getValue());
        }
    }

}
