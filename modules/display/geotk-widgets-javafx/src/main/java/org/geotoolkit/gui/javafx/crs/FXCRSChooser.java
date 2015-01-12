/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.javafx.crs;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.gui.javafx.util.FXOptionDialog;
import org.geotoolkit.internal.GeotkFX;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXCRSChooser extends BorderPane {

    @FXML
    private CheckBox uiLongFirst;
    @FXML
    private BorderPane uiPane;
    @FXML
    private TextField uiSearch;

    private FXCRSTable uiTable;
    
    private final ObjectProperty<CoordinateReferenceSystem> crsProperty = new SimpleObjectProperty<>();
    private boolean updateText = false;
    
    public FXCRSChooser() {
        GeotkFX.loadJRXML(this);
        
        uiSearch.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(updateText)return;
                uiTable.searchCRS(uiSearch.getText());
            }
        });
        
        uiTable = new FXCRSTable();
        uiPane.setCenter(uiTable);
        
        uiTable.crsProperty().set(crsProperty.get());
        uiTable.crsProperty().addListener((ObservableValue<? extends CoordinateReferenceSystem> observable, 
                              CoordinateReferenceSystem oldValue, CoordinateReferenceSystem newValue) -> {
            crsProperty.set(newValue);
        });
        
        crsProperty.addListener((ObservableValue<? extends CoordinateReferenceSystem> observable, 
                              CoordinateReferenceSystem oldValue, CoordinateReferenceSystem newValue) -> {
            uiTable.crsProperty().set(newValue);
            if(newValue!=null){
                updateText = true;
                uiSearch.setText(newValue.getName().toString());
                updateText = false;
            }
        });
        
    }
    
    public ObjectProperty<CoordinateReferenceSystem> crsProperty(){
        return crsProperty;
    }
        
    public static CoordinateReferenceSystem showDialog(Object parent, CoordinateReferenceSystem crs){
        final FXCRSChooser chooser = new FXCRSChooser();
        chooser.crsProperty.set(crs);
        FXOptionDialog.showOkCancel(parent, chooser, "", false);
        return chooser.crsProperty.get();
    }
    
}
