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
import org.geotoolkit.gui.javafx.util.FXNumberSpinner;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.style.Stroke;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStroke extends FXStyleElementController<FXStroke,Stroke> {
 
    @FXML
    private FXColorExpression uiColor; 
    @FXML
    private FXNumberExpression uiWidth;
    @FXML
    private FXNumberExpression uiOpacity;      
    @FXML
    private FXNumberSpinner uiDash1;
    @FXML
    private FXNumberSpinner uiDash2;
    @FXML
    private FXNumberSpinner uiDash3;
    @FXML
    private FXLineCapExpression uiLineCap;
    @FXML
    private FXLineJoinExpression uiLineJoin;

            
    @Override
    public Class<Stroke> getEditedClass() {
        return Stroke.class;
    }
    
    @Override
    public Stroke newValue() {
        return StyleConstants.DEFAULT_STROKE;
    }
    
    @Override
    public void initialize() {
        super.initialize();     
        
        uiWidth.getNumberField().minValueProperty().set(0);
        uiOpacity.getNumberField().minValueProperty().set(0);
        uiOpacity.getNumberField().maxValueProperty().set(1);
        uiDash1.minValueProperty().set(0);
        uiDash2.minValueProperty().set(0);
        uiDash3.minValueProperty().set(0);
        
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
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
            final float d0 = uiDash1.valueProperty().get().floatValue();
            final float d1 = uiDash2.valueProperty().get().floatValue();
            final float d2 = uiDash3.valueProperty().get().floatValue();
            stroke = getStyleFactory().stroke(
                    uiColor.valueProperty().get(),
                    uiOpacity.valueProperty().get(),
                    uiWidth.valueProperty().get(),
                    uiLineJoin.valueProperty().get(),
                    uiLineCap.valueProperty().get(),
                    (d0!=0&&d1!=0&&d2!=0)?new float[]{d0,d1,d2}:null,
                    StyleConstants.DEFAULT_STROKE_OFFSET);
            //            }
            
            value.set(stroke);
        };
        
        uiWidth.valueProperty().addListener(changeListener);
        uiOpacity.valueProperty().addListener(changeListener);
        uiColor.valueProperty().addListener(changeListener);
        uiLineCap.valueProperty().addListener(changeListener);
        uiLineJoin.valueProperty().addListener(changeListener);
        uiDash1.valueProperty().addListener(changeListener);
        uiDash2.valueProperty().addListener(changeListener);
        uiDash3.valueProperty().addListener(changeListener);
    }
    
    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        uiWidth.setLayer(layer);
        uiOpacity.setLayer(layer);
        uiColor.setLayer(layer);
        uiLineCap.setLayer(layer);
        uiLineJoin.setLayer(layer);
    }
    
    @Override
    protected void updateEditor(Stroke styleElement) {
        uiWidth.valueProperty().setValue(styleElement.getWidth());
        uiOpacity.valueProperty().setValue(styleElement.getOpacity());
        uiColor.valueProperty().setValue(styleElement.getColor());
        uiLineCap.valueProperty().setValue(styleElement.getLineCap());
        uiLineJoin.valueProperty().setValue(styleElement.getLineJoin());
        
        final Double d = styleElement.getDashOffset().evaluate(null, Double.class);
        final float[] dashes = styleElement.getDashArray();
        float d0 = 0f;
        float d1 = 0f;
        float d2 = 0f;
        if(dashes!=null){
            if(dashes.length>0) d0 = dashes[0];
            if(dashes.length>1) d1 = dashes[1];
            if(dashes.length>2) d2 = dashes[2];
        }
        uiDash1.valueProperty().set(d0);
        uiDash2.valueProperty().set(d1);
        uiDash3.valueProperty().set(d2);
    }

}
