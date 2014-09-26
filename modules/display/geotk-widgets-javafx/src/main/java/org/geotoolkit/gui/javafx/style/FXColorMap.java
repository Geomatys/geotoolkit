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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.opengis.style.ColorMap;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXColorMap extends FXStyleElementController<FXColorMap,ColorMap> {

    @FXML
    private Label uiNoData;
    @FXML
    private CheckBox uiInvert;
    @FXML
    private TextField uiMinimum;
    @FXML
    private CheckBox uiNaN;
    @FXML
    private ComboBox<?> uiMethod;
    @FXML
    private TextField uiBand;
    @FXML
    private TextField uiDivision;
    @FXML
    private TextField uiMaximum;
    @FXML
    private TableView<?> uiTable;
    @FXML
    private ComboBox<?> uiPalette;
    
    public FXColorMap() {
    }

    @FXML
    void addValue(ActionEvent event) {

    }

    @FXML
    void removeAll(ActionEvent event) {

    }

    @FXML
    void fitToData(ActionEvent event) {

    }

    @FXML
    void generate(ActionEvent event) {

    }
    
    @Override
    public Class<ColorMap> getEditedClass() {
        return ColorMap.class;
    }

    @Override
    public ColorMap newValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void updateEditor(ColorMap styleElement) {
    }

    @Override
    public void initialize() {
        super.initialize();
    }
    
    
    
}
