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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.BorderPane;
import org.opengis.style.RasterSymbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXRasterSymbolizer extends FXStyleElementController<FXRasterSymbolizer, RasterSymbolizer>{
    
    @FXML
    private RadioButton uiChoiceColorNone;
    @FXML
    private BorderPane uiColorPane;
    @FXML
    private RadioButton uiChoiceOutlineNone;
    @FXML
    private RadioButton uiChoiceOutlineLine;
    @FXML
    private FXNumberExpression uiOpacity;
    @FXML
    private RadioButton uiChoiceColorRGB;
    @FXML
    private RadioButton uiChoiceColorMap;
    @FXML
    private FXShadedRelief uiReliefShading;
    @FXML
    private FXContrastEnhancement uiContrast;
    @FXML
    private BorderPane uiOutlinePane;
    @FXML
    private RadioButton uiChoiceOutlinePolygon;
    
    @FXML
    void updateColorChoice(ActionEvent event) {

    }

    @FXML
    void updateOutlineChoice(ActionEvent event) {

    }
    
    @Override
    public Class<RasterSymbolizer> getEditedClass() {
        return RasterSymbolizer.class;
    }

    @Override
    public RasterSymbolizer newValue() {
        return getStyleFactory().rasterSymbolizer();
    }

    @Override
    protected void updateEditor(RasterSymbolizer styleElement) {
    }
    
}
