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
import javafx.scene.control.Button;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXCRSButton extends Button{

    private final ObjectProperty<CoordinateReferenceSystem> crsProperty = new SimpleObjectProperty<>();
    
    public FXCRSButton() {
        setText("-");
        
        setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final CoordinateReferenceSystem crs = FXCRSChooser.showDialog(FXCRSButton.this, crsProperty.get());
                crsProperty.set(crs);
            }
        });
        
        //update button text when needed
        crsProperty.addListener((ObservableValue<? extends CoordinateReferenceSystem> observable, 
                CoordinateReferenceSystem oldValue, CoordinateReferenceSystem newValue) -> {
            if(newValue!=null){
                setText(newValue.getName().toString());
            }else{
                setText(" - ");
            }
        });
    }
    
    public ObjectProperty<CoordinateReferenceSystem> crsProperty(){
        return crsProperty;
    }
    
}
