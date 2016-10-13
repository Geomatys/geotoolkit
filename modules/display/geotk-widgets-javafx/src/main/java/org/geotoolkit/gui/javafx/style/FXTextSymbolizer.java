/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2015, Geomatys
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

import java.awt.Color;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javax.measure.Unit;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.map.MapLayer;
import static org.geotoolkit.style.StyleConstants.*;
import org.opengis.filter.expression.Expression;
import org.opengis.style.Description;
import org.opengis.style.TextSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXTextSymbolizer extends FXStyleElementController<TextSymbolizer>{

    @FXML protected Label uiHaloTitle;
    @FXML private FXSymbolizerInfo uiInfo;
    @FXML protected FXTextExpression uiText;    
    @FXML protected FXFont uiFont;    
    @FXML protected FXFill uiFill;    
    @FXML protected FXHalo uiHalo;
    @FXML protected FXLabelPlacement uiPlacement;
        
    @Override
    public Class<TextSymbolizer> getEditedClass() {
        return TextSymbolizer.class;
    }

    @Override
    public TextSymbolizer newValue() {
        return getStyleFactory().textSymbolizer(
                getStyleFactory().fill(Color.BLACK),
                DEFAULT_FONT,
                DEFAULT_HALO,
                getFilterFactory().literal("Label"),
                DEFAULT_POINTPLACEMENT,
                null);
    }
    
    @Override
    public void initialize() {
        super.initialize();        
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            final String name = uiInfo.getName();
            final Description desc = uiInfo.getDescription();
            final Unit uom = uiInfo.getUnit();
            final Expression geom = uiInfo.getGeom();
            value.set(getStyleFactory().textSymbolizer(
                    name,geom,desc,uom,
                    uiText.valueProperty().get(),
                    uiFont.valueProperty().get(), 
                    uiPlacement.valueProperty().get(),
                    uiHalo.valueProperty().get(),
                    uiFill.valueProperty().get()
                    ));
        };
        
        uiFill.valueProperty().addListener(changeListener);
        uiFont.valueProperty().addListener(changeListener);
        uiHalo.valueProperty().addListener(changeListener);
        uiText.valueProperty().addListener(changeListener);
        uiInfo.valueProperty().addListener(changeListener);
        uiPlacement.valueProperty().addListener(changeListener);

        FXStyleElementController.configureAdvancedProperty(
                uiHalo,uiHaloTitle);
    }
    
    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiText.setLayer(layer);
        uiFont.setLayer(layer);
        uiFill.setLayer(layer);
        uiHalo.setLayer(layer);
        uiInfo.setLayer(layer);
        uiPlacement.setLayer(layer);
    }
    
    @Override
    protected void updateEditor(TextSymbolizer styleElement) {
        uiFill.valueProperty().setValue(styleElement.getFill());
        uiFont.valueProperty().setValue(styleElement.getFont());
        uiHalo.valueProperty().setValue(styleElement.getHalo());
        uiText.valueProperty().setValue(styleElement.getLabel());
        uiInfo.parse(styleElement);
        uiPlacement.valueProperty().setValue(styleElement.getLabelPlacement());
    }
    
}
