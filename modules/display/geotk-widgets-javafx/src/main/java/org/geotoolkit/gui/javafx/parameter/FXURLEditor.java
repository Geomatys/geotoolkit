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

package org.geotoolkit.gui.javafx.parameter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.geotoolkit.internal.Loggers;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXURLEditor extends FXValueEditor{

    private final Button chooserButton = new Button("...");
    private final TextField textField = new TextField();

    public FXURLEditor() {
        chooserButton.setPadding(Insets.EMPTY);
        chooserButton.setMaxHeight(Double.MAX_VALUE);
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(currentValue!=null){
                    try {
                        currentValue.setValue(new URL(textField.getText()));
                    } catch (MalformedURLException ex) {
                        Loggers.JAVAFX.log(Level.WARNING,ex.getMessage(),ex);
                    }
                }
            }
        });
        chooserButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final FileChooser chooser = new FileChooser();
                
                final String prevPath = getPreviousPath();
                if (prevPath != null) {
                    final File f = new File(prevPath);
                    if(f.exists() && f.isDirectory()){
                        chooser.setInitialDirectory(f);
                    }
                }
                               
                File selectedFile = chooser.showOpenDialog(null);
                if(selectedFile!=null){
                    setPreviousPath(selectedFile.getParentFile().getAbsolutePath());
                    textField.setText(selectedFile.toURI().toString());
                }
            }
        });
    }
    
    @Override
    public boolean canHandle(Class binding) {
        return URL.class.isAssignableFrom(binding);
    }

    @Override
    public void setValue(Property value) {
        super.setValue(value);
        try {
            textField.setText(value.getValue()==null ? "" :  ((URL) value.getValue()).toURI().toString());
        } catch (URISyntaxException ex) {
            Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
            textField.setText("");
        }
    }
    
    @Override
    public Node getComponent() {
        return new BorderPane(textField, null, chooserButton, null, null);
    }
    
    
    public static String getPreviousPath() {
        final Preferences prefs = Preferences.userNodeForPackage(FXURLEditor.class);
        return prefs.get("path", null);
    }

    public static void setPreviousPath(final String path) {
        final Preferences prefs = Preferences.userNodeForPackage(FXURLEditor.class);
        prefs.put("path", path);
    }
    
}
