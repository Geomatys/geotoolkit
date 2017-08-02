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
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import org.apache.sis.referencing.crs.DefaultGeographicCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.geotoolkit.gui.javafx.util.FXOptionDialog;
import org.geotoolkit.internal.GeotkFX;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import org.apache.sis.referencing.crs.AbstractCRS;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXCRSChooser extends BorderPane {

    @FXML
    private CheckBox uiLongFirst;
    @FXML
    private CheckBox uiAxisConv;
    @FXML
    private BorderPane uiPane;
    @FXML
    private TextField uiSearch;
    @FXML
    private ChoiceBox<AxesConvention> uiChoice;

    private FXCRSTable uiTable;

    private final ObjectProperty<CoordinateReferenceSystem> crsProperty = new SimpleObjectProperty<>();
    private boolean updateText = false;

    public FXCRSChooser() {
        GeotkFX.loadJRXML(this,FXCRSChooser.class);

        uiSearch.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(updateText)return;
                uiTable.searchCRS(uiSearch.getText());
            }
        });

        uiTable = new FXCRSTable();
        uiPane.setCenter(uiTable);

        uiTable.crsProperty().bindBidirectional(crsProperty);

        crsProperty.addListener((ObservableValue<? extends CoordinateReferenceSystem> observable,
                              CoordinateReferenceSystem oldValue, CoordinateReferenceSystem newValue) -> {
            uiTable.crsProperty().set(newValue);
            if(newValue!=null){
                updateText = true;
                uiSearch.setText(newValue.getName().toString());
                updateText = false;
            }
        });

        uiChoice.setItems(FXCollections.observableArrayList(AxesConvention.values()));

    }

    public CoordinateReferenceSystem getCorrectedCRS(){
        CoordinateReferenceSystem crs = crsProperty.get();
        if(crs==null) return null;

        //fix longitude first
        try{
            Integer epsg = IdentifiedObjects.lookupEPSG(crs);
            if(epsg!=null){
                crs = CRS.forCode("EPSG:"+epsg);
                if (uiLongFirst.isSelected()) {
                    crs = AbstractCRS.castOrCopy(crs).forConvention(AxesConvention.RIGHT_HANDED);
                }
            }
        }catch(FactoryException ex){/*no important*/}

        //fix axes convention
        if(uiAxisConv.isSelected() && crs instanceof DefaultGeographicCRS && uiChoice.getValue()!=null){
            crs = ((DefaultGeographicCRS)crs).forConvention(uiChoice.getValue());
        }

        return crs;
    }

    public ObjectProperty<CoordinateReferenceSystem> crsProperty(){
        return crsProperty;
    }

    public static CoordinateReferenceSystem showDialog(Object parent, CoordinateReferenceSystem crs){
        final FXCRSChooser chooser = new FXCRSChooser();
        chooser.crsProperty.set(crs);
        FXOptionDialog.showOkCancel(parent, chooser, "", false);
        return chooser.getCorrectedCRS();
    }

}
