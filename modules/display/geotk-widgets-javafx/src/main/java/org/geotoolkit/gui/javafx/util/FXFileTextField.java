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
package org.geotoolkit.gui.javafx.util;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.stage.FileChooser;

/**
 * Allow user to specify a file path using an auto-completed text field.
 * He can also choose a path using a system dialog, which open thanks to a button
 * on the right of the text field. Another button allow to open the chosen file 
 * with a system application.
 * 
 * User can specify a relative path if the {@link #rootPath} is initialized with
 * the base path to use.
 * 
 * Note : It's not its purpose, but you can also use distant URL as text field
 * content. No completion will be proposed, but you will be able to use system 
 * browser to visit specified URI.
 * 
 * @author Alexis Manin (Geomatys)
 */
public class FXFileTextField extends AbstractPathTextField {

    /**
     * A root path from which auto-completor will base on to display possible values to user.
     */
    public final SimpleStringProperty rootPath = new SimpleStringProperty();
    
    public FXFileTextField() {
        rootPath.addListener(this::updateRoot);      
    }
    
    /**
     * Update completor root path when {@link #rootPath} is updated.
     * @param obs
     * @param oldValue
     * @param newValue 
     */
    private void updateRoot(final ObservableValue<? extends String> obs, final String oldValue, final String newValue) {
        if (newValue == null || newValue.isEmpty()) {
            completor.root = null;
        } else {
            completor.root = Paths.get(newValue);
        }
    }
    
    /**
     * Open a file chooser from root path (or user home if the root is null), to
     * allow user to specify its path using system tools.
     * @return The file path chosen by the user, or null.
     */
    @Override
    protected String chooseInputContent() {
        final FileChooser chooser = new FileChooser();
        try {
            URI uriForText = getURIForText(getText());
            final Path basePath = Paths.get(uriForText);
            if (Files.isDirectory(basePath)) {
                chooser.setInitialDirectory(basePath.toFile());
            } else if (Files.isDirectory(basePath.getParent())) {
                chooser.setInitialDirectory(basePath.getParent().toFile());
            }
        } catch (Exception e) {
            // Well, we'll try without it...
        }
        File returned = chooser.showOpenDialog(null);
        if (returned == null) {
            return null;
        } else {
            return (completor.root != null)? 
                    completor.root.relativize(returned.toPath()).toString() : returned.getAbsolutePath();
        }
    }

    @Override
    protected URI getURIForText(String inputText) throws Exception {
        if (rootPath.get() == null) {
            return new URI(inputText);
        } else {
            return Paths.get(rootPath.get(), inputText == null? "" : inputText).toUri();
        }
    }
    
}
