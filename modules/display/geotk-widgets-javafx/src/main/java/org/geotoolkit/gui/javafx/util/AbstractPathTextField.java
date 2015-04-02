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

import java.awt.Color;
import java.awt.Desktop;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import javafx.beans.DefaultProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.internal.Loggers;

/**
 * A custom component which contains a text field designed to contain a file path.
 * 
 * Note : Override {@link #chooseInputContent() } method, to allow user to choose a path
 * when he clicks on {@link #chooseFileButton}.
 * 
 * Note 2 : It's not its purpose, but you can also use distant URL as text field
 * content. No completion will be proposed, but you will be able to use system 
 * browser to visit specified address.
 * 
 * @author Alexis Manin (Geomatys)
 */
@DefaultProperty("text")
public abstract class AbstractPathTextField extends HBox {
    
    public static final Image ICON_FIND = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_FOLDER_OPEN, 16, Color.DARK_GRAY), null);
    public static final Image ICON_FORWARD = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_EXTERNAL_LINK, 16, Color.DARK_GRAY), null);

    protected final TextField inputText = new TextField();
    private final StringProperty textProperty = inputText.textProperty();
    
    protected final PathCompletor completor = new PathCompletor(inputText);
    
    protected final Button choosePathButton = new Button("", new ImageView(ICON_FIND));
    protected final Button openPathButton = new Button("", new ImageView(ICON_FORWARD));
    
    public AbstractPathTextField() {
        choosePathButton.setOnAction((ActionEvent e)-> {
            final String content = chooseInputContent();
            if (content != null) {
                setText(content);
            }
        });
        
        inputText.setMinSize(0, USE_PREF_SIZE);
        inputText.setMaxSize(Double.MAX_VALUE, USE_PREF_SIZE);
        
        // TODO : put style rules in CSS
        choosePathButton.setBackground(new Background(new BackgroundFill(null, CornerRadii.EMPTY, Insets.EMPTY)));
        choosePathButton.setBorder(Border.EMPTY);
        choosePathButton.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        
        openPathButton.setBackground(new Background(new BackgroundFill(null, CornerRadii.EMPTY, Insets.EMPTY)));
        openPathButton.setBorder(Border.EMPTY);
        openPathButton.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        
        choosePathButton.setTooltip(new Tooltip(
                GeotkFX.getString("org.geotoolkit.gui.javafx.util.AbstractPathTextField.choosePath.tooltip")
        ));
        
        openPathButton.setTooltip(new Tooltip(
                GeotkFX.getString("org.geotoolkit.gui.javafx.util.AbstractPathTextField.openPath.tooltip")
        ));
        
        final SimpleBooleanProperty notValidPath = new SimpleBooleanProperty(true);
        textProperty.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            notValidPath.set((textProperty.get() == null || textProperty.get().isEmpty()));
        });
        
        openPathButton.disableProperty().bind(notValidPath);
        
        setAlignment(Pos.CENTER);
        setSpacing(5);
        getChildren().addAll(inputText, choosePathButton);
        
        if (Desktop.isDesktopSupported()) {
            getChildren().add(openPathButton);
            openPathButton.setOnAction((ActionEvent e) -> {
                TaskManager.INSTANCE.submit(new OpenOnSystem(textProperty.get()));
            });
        }
    }
    
    public String getText() {
        return textProperty.get();
    }
    
    public void setText(final String input) {
        textProperty.set(input);
    }
    
    public StringProperty textProperty() {
        return textProperty;
    }
    
    /**
     * Operation to perform for {@link #choosePathButton} action. It should be 
     * used to display an easy-to-use wizard to help user to choose its wanted 
     * value.
     * 
     * @return the value chosen by the user, or null.
     */
    protected abstract String chooseInputContent();
    
    /**
     * Build a valid URI from text written in input text control. Designed to 
     * allow implementations of the current class to modify specified paths as 
     * they need.
     * 
     * @param inputText The current text value of {@link #inputText} control.
     * @return A valid URI which points on the location defined by input text.
     * @throws Exception 
     */
    protected abstract URI getURIForText(final String inputText) throws Exception;
    
    /**
     * Try to transform input text into a valid URI using {@link #getURIForText(java.lang.String) },
     * then ask the system to open it.
     */
    private class OpenOnSystem extends Task {

        private final String inputText;
        
        OpenOnSystem(final String inputText) {
            super();
            this.inputText = inputText;
            updateTitle(GeotkFX.getString("org.geotoolkit.gui.javafx.util.AbstractPathTextField.taskTitle"));
        }
        
        @Override
        protected Object call() throws Exception {
            final URI toOpen = getURIForText(inputText);
            // First, we try to open input file as a local file, to allow system to find best application to open it.
            try {
                Path local = Paths.get(toOpen.toString());
                Desktop.getDesktop().open(local.toFile());
                return null;
            } catch (Exception ex) {
                Loggers.JAVAFX.log(Level.FINE, "Input URI cannot be opened as a local file : " + toOpen, ex);
            }

            Desktop.getDesktop().browse(toOpen);
            return null;
        }
    }
    
}
