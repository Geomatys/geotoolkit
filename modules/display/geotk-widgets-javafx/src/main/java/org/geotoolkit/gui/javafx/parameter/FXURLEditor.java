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
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.gui.javafx.util.FXFileTextField;
import org.geotoolkit.internal.Loggers;

/**
 * An editor allowing setting of a parameter of URL, Path or File value.
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public class FXURLEditor extends FXValueEditor {

    private static final Pattern PROTOCOL_START = Pattern.compile("(?i)^[A-Za-z]+://");
    private static final Class[] ALLOWED_CLASSES = new Class[]{URL.class, URI.class, Path.class, File.class};

    /**
     * Custom field for path edition.
     */
    private final PropertyPathEditor pathField = new PropertyPathEditor();
    /**
     * Converts value typed in input field to valid path or URI for target
     * property.
     */
    private ObjectConverter<String, Object> valueConverter;

    public FXURLEditor() {
        String previousPath = getPreviousPath();
        if (previousPath != null && !previousPath.isEmpty()) {
            try {
                final Path rootPath = Paths.get(previousPath);
                if (Files.isDirectory(rootPath)) {
                    pathField.rootPath.set(previousPath);
                } else if (Files.isDirectory(rootPath.getParent())) {
                    pathField.rootPath.set(rootPath.getParent().toAbsolutePath().toString());
                }
            } catch (Exception e) {
                Loggers.JAVAFX.log(Level.WARNING, "Cannot initialize root path for editor : "+previousPath, e);
            }
        }
        pathField.textProperty().addListener(this::updatePropertyValue);
    }

    /**
     * Update target property value when input field value changes.
     *
     * @param observable
     * @param oldValue
     * @param newValue New value to set for targete property.
     */
    protected void updatePropertyValue(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (currentValue != null) {
            if (newValue == null) {
                currentValue.setValue(null);
            } else {
                try {
                    currentValue.setValue(valueConverter.apply(checkAndAdaptPath(newValue)));
                } catch (Exception ex) {
                    // TODO : Set red borders on editor
                    Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
    }

    protected static String checkAndAdaptPath(final String input) {
        if (input == null || input.isEmpty())
            return input;
        if (PROTOCOL_START.matcher(input).find()) {
            return input;
        } else {
            Path target = Paths.get(input);
            if (Files.exists(target)) {
                return target.toAbsolutePath().toUri().toString();
            } else {
                return "http://" + input;
            }
        }
    }
    
    @Override
    public boolean canHandle(Class binding) {
        if (binding == null)
            return false;
        for (final Class current : ALLOWED_CLASSES) {
            if (current.isAssignableFrom(binding)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setValue(Property value) {
        super.setValue(value);
        try {
            valueConverter = ObjectConverters.find(String.class, getValueClass());
            pathField.setText(valueConverter.inverse().apply(value.getValue()));
        } catch (Exception ex) {
            Loggers.JAVAFX.log(Level.WARNING, "Cannot initialize editor value.", ex);
            pathField.setText("");
        }
    }

    @Override
    public Node getComponent() {
        return pathField;
    }

    public static String getPreviousPath() {
        final Preferences prefs = Preferences.userNodeForPackage(FXURLEditor.class);
        return checkAndAdaptPath(prefs.get("path", null));
    }

    public static void setPreviousPath(final String path) {
        if (path == null) return;
        final Preferences prefs = Preferences.userNodeForPackage(FXURLEditor.class);
        prefs.put("path", checkAndAdaptPath(path));
    }

    private static class PropertyPathEditor extends FXFileTextField {

        @Override
        protected String chooseInputContent() {
            final String chosenPath = super.chooseInputContent();
            setPreviousPath(chosenPath);
            return chosenPath;
        }        
    }
}
