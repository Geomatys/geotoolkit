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

import java.awt.Color;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import org.geotoolkit.style.StyleConstants;
import org.opengis.style.Stroke;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStroke extends FXStyleElementController<FXStroke,Stroke> {

    @FXML
    private FXNumberExpression uiWidth;

    @FXML
    private FXNumberExpression uiOpacity;

    private final ChangeListener changeListener = new ChangeListener() {

        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            
            final Stroke stroke;
//            if (guiGraphicFill.isSelected() && graphicFill != null) {
//                stroke = getStyleFactory().stroke(
//                        graphicFill,
//                        GuiStrokeColor.create(),
//                        GuiStrokeAlpha.create(),
//                        GuiStrokeWidth.create(),
//                        GuiStrokeLineJoin.create(),
//                        GuiStrokeLineCap.create(),
//                        GuiStrokeDashes.getDashes(),
//                        GuiStrokeDashes.getOffset());
//            } else if (guiGraphicStroke.isSelected() && graphicStroke != null) {
//                stroke = getStyleFactory().stroke(
//                        graphicStroke,
//                        GuiStrokeColor.create(),
//                        GuiStrokeAlpha.create(),
//                        GuiStrokeWidth.create(),
//                        GuiStrokeLineJoin.create(),
//                        GuiStrokeLineCap.create(),
//                        GuiStrokeDashes.getDashes(),
//                        GuiStrokeDashes.getOffset());
//            } else {
//                stroke = getStyleFactory().stroke(
//                        GuiStrokeColor.create(),
//                        GuiStrokeAlpha.create(),
//                        GuiStrokeWidth.create(),
//                        GuiStrokeLineJoin.create(),
//                        GuiStrokeLineCap.create(),
//                        GuiStrokeDashes.getDashes(),
//                        GuiStrokeDashes.getOffset());
                stroke = getStyleFactory().stroke(
                        getFilterFactory().literal(Color.BLACK),
                        uiOpacity.valueProperty().get(),
                        uiWidth.valueProperty().get(),
                        StyleConstants.DEFAULT_STROKE_JOIN,
                        StyleConstants.DEFAULT_STROKE_CAP,
                        new float[0],
                        StyleConstants.DEFAULT_STROKE_OFFSET);
//            }
            
            value.set(stroke);
        }

    };
            
    @Override
    public void initialize() {
        super.initialize();
        uiWidth.valueProperty().addListener(changeListener);
        uiOpacity.valueProperty().addListener(changeListener);
    }
    
    
    
    @Override
    protected void updateEditor(Stroke styleElement) {
        super.updateEditor(styleElement);
        uiWidth.valueProperty().setValue(styleElement.getWidth());
        uiOpacity.valueProperty().setValue(styleElement.getOpacity());
        
    }

}
