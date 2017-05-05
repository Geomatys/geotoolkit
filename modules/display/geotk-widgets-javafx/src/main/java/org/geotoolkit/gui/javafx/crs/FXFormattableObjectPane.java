/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import org.apache.sis.io.wkt.Convention;
import org.apache.sis.io.wkt.FormattableObject;
import org.geotoolkit.gui.javafx.util.FXOptionDialog;

/**
 * Small panel to display an object as WKT in various conventions.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXFormattableObjectPane extends BorderPane{

    private final ChoiceBox<Convention> choice = new ChoiceBox<>(FXCollections.observableArrayList(Convention.values()));
    private final TextArea text = new TextArea();

    public FXFormattableObjectPane(final FormattableObject obj) {
        setTop(choice);
        setCenter(text);

        choice.valueProperty().addListener(new ChangeListener<Convention>() {
            @Override
            public void changed(ObservableValue<? extends Convention> observable, Convention oldValue, Convention newValue) {
                text.setText(obj.toString(newValue));
            }
        });
        choice.getSelectionModel().select(Convention.WKT1);
    }

    public static void showDialog(Object parent, FormattableObject candidate){
        final FXFormattableObjectPane chooser = new FXFormattableObjectPane(candidate);
        FXOptionDialog.showOkCancel(parent, chooser, "", false);
    }

}
