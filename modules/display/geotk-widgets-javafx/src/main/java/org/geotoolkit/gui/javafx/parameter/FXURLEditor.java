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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
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

    private final SimpleObjectProperty valueProperty = new SimpleObjectProperty();

    public FXURLEditor(final Spi originatingSpi) {
        super(originatingSpi);

        currentAttributeType.addListener(this::updateConverter);
        currentParamDesc.addListener(this::updateConverter);

        Bindings.bindBidirectional(pathField.textProperty(), valueProperty,
                new StringConverter() {
                    @Override
                    public String toString(Object object) {
                        if (object == null || valueConverter == null || valueConverter.inverse() == null) return null;
                        return valueConverter.inverse().apply(object);
                    }

                    @Override
                    public Object fromString(String string) {
                        if (string == null || valueConverter == null) return null;
                        final URI uri = checkAndAdaptPath(string);
                        if (uri == null) return null;
                        return valueConverter.apply(uri.toString());
                    }
                });
    }

    @Override
    public Property valueProperty() {
        return valueProperty;
    }


    public void updateConverter(ObservableValue observable, Object oldValue, Object newValue) {
        valueConverter = ObjectConverters.find(String.class, getValueClass());
    }

    protected static URI checkAndAdaptPath(final String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        try {
            final URI uri = new URI(input);
            if (uri.getScheme()== null) {
                throw new URISyntaxException(input, "no scheme defined");
            }
            return uri;
        } catch (URISyntaxException ex) {
            //not an URI
            Path target = Paths.get(input);
            if (Files.exists(target)) {
                return target.toAbsolutePath().toUri();
            } else {
                //unsupported string
                return null;
            }
        }
    }

    @Override
    public FXFileTextField getComponent() {
        return pathField;
    }

    public FXURLEditor copy(){
        final FXURLEditor cp = new FXURLEditor((Spi)spi);
        cp.pathField.showOpenProperty().setValue(pathField.showOpenProperty().getValue());
        return cp;
    }

    public static URI getPreviousPath() {
        final Preferences prefs = Preferences.userNodeForPackage(FXURLEditor.class);
        return checkAndAdaptPath(prefs.get("path", null));
    }

    public static void setPreviousPath(final URI uri) {
        if (uri != null) {
            final Preferences prefs = Preferences.userNodeForPackage(FXURLEditor.class);
            prefs.put("path", uri.toString());
        }
    }

    private static class PropertyPathEditor extends FXFileTextField {

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
                Loggers.JAVAFX.log(Level.FINE, "Root path cannot be decoded.", e);
            }

            if (chooser.getInitialDirectory() == null) {
                //point to previous opened file
                URI previousPath = getPreviousPath();
                if (previousPath != null) {
                    try {
                        final Path rootPath = Paths.get(previousPath);
                        if (Files.isDirectory(rootPath)) {
                            chooser.setInitialDirectory(rootPath.toFile());
                        } else if (Files.isDirectory(rootPath.getParent())) {
                            chooser.setInitialDirectory(rootPath.getParent().toFile());
                        }
                    } catch (Exception e) {
                        Loggers.JAVAFX.log(Level.WARNING, "Cannot initialize root path for editor : " + previousPath, e);
                    }
                }
            }

            File returned = chooser.showOpenDialog(null);
            if (returned == null) {
                return null;
            }

            try {
                setPreviousPath(returned.toURI());
            } catch (Exception ex) {
                //not a valid uri, can not store it
                Loggers.JAVAFX.log(Level.WARNING, "Cannot store path in preferences : " + returned.toString(), ex);
            }
            return returned.toPath().toString();
        }
    }

    public static final class Spi extends FXValueEditorSpi {

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
        public FXValueEditor createEditor() {
            return new FXURLEditor(this);
        }
    }
}
