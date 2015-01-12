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
package org.geotoolkit.gui.javafx.style.graduation;

import org.geotoolkit.display2d.ext.graduation.GraduationSymbolizer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.gui.javafx.style.FXFont;
import org.geotoolkit.gui.javafx.style.FXListExpression;
import org.geotoolkit.gui.javafx.style.FXNumberExpression;
import org.geotoolkit.gui.javafx.style.FXStroke;
import org.geotoolkit.gui.javafx.style.FXStyleElementController;
import org.geotoolkit.gui.javafx.style.FXTextExpression;
import org.opengis.filter.expression.Literal;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXGraduation extends FXStyleElementController<FXGraduation,GraduationSymbolizer.Graduation> {
    
    @FXML
    private FXNumberExpression uiSize;
    @FXML
    private FXNumberExpression uiStart;
    @FXML
    private FXNumberExpression uiOffset;
    @FXML
    private FXNumberExpression uiStep;
    @FXML
    private FXListExpression uiReverse;
    @FXML
    private FXListExpression uiSide;
    @FXML
    private FXTextExpression uiUnit;
    @FXML
    private FXTextExpression uiFormat;
    @FXML
    private FXStroke uiStroke;
    @FXML
    private FXFont uiFont;
    
    @Override
    public Class<GraduationSymbolizer.Graduation> getEditedClass() {
        return GraduationSymbolizer.Graduation.class;
    }

    @Override
    public GraduationSymbolizer.Graduation newValue() {
        return new GraduationSymbolizer.Graduation();
    }

    @Override
    public void initialize() {
        super.initialize();
        
        uiSide.getChoiceBox().setItems(FXCollections.observableArrayList(
                GraduationSymbolizer.SIDE_LEFT,
                GraduationSymbolizer.SIDE_RIGHT,
                GraduationSymbolizer.SIDE_BOTH));
        uiReverse.getChoiceBox().setItems(FXCollections.observableArrayList(
                GraduationSymbolizer.DIRECTION_FORWARD,
                GraduationSymbolizer.DIRECTION_REVERSE));
        
        final ChangeListener changeListener = (ChangeListener) (ObservableValue observable, Object oldValue, Object newValue) -> {
            if(updating) return;
            final GraduationSymbolizer.Graduation element = new GraduationSymbolizer.Graduation();
            element.setSize(uiSize.valueProperty().get());
            element.setStart(uiStart.valueProperty().get());
            element.setOffset(uiOffset.valueProperty().get());
            element.setStep(uiStep.valueProperty().get());
            element.setReverse(uiReverse.valueProperty().get());
            element.setSide(uiSide.valueProperty().get());
            element.setUnit(uiUnit.valueProperty().get());
            element.setFormat(uiFormat.valueProperty().get());
            element.setStroke(uiStroke.valueProperty().get());
            element.setFont(uiFont.valueProperty().get());
            value.set(element);
        };
        uiSize.valueProperty().addListener(changeListener);
        uiStart.valueProperty().addListener(changeListener);
        uiOffset.valueProperty().addListener(changeListener);
        uiStep.valueProperty().addListener(changeListener);
        uiReverse.valueProperty().addListener(changeListener);
        uiSide.valueProperty().addListener(changeListener);
        uiUnit.valueProperty().addListener(changeListener);
        uiFormat.valueProperty().addListener(changeListener);
        uiStroke.valueProperty().addListener(changeListener);
        uiFont.valueProperty().addListener(changeListener);
    }
    
    @Override
    protected void updateEditor(GraduationSymbolizer.Graduation styleElement) {
        if(styleElement!=null){
            uiSize.valueProperty().setValue(styleElement.getSize());
            uiStart.valueProperty().setValue(styleElement.getStart());
            uiOffset.valueProperty().setValue(styleElement.getOffset());
            uiStep.valueProperty().setValue(styleElement.getStep());
            uiReverse.valueProperty().setValue(styleElement.getReverse());
            uiSide.valueProperty().setValue(styleElement.getSide());
            uiUnit.valueProperty().setValue(styleElement.getUnit());
            uiFormat.valueProperty().setValue(styleElement.getFormat());
            uiStroke.valueProperty().setValue(styleElement.getStroke());
            uiFont.valueProperty().setValue(styleElement.getFont());
        }
    }
    
}
