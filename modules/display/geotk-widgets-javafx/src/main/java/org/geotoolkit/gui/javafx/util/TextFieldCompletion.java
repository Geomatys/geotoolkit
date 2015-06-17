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

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;

/**
 * Auto-completion for textfield.
 * 
 * Note : To update completion list over input text change, you must override 
 * {@link #getChoices(java.lang.String) } method.
 * 
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public class TextFieldCompletion {
    
    private final TextInputControl textField;
    private final Popup popup = new Popup();
    private final ListView<String> list = new ListView<>();
    private int caretPos;

    public TextFieldCompletion(final TextInputControl textField) {
        this.textField = textField;
        
        list.setMinSize(0, 0);
        
        popup.setAutoHide(true);
        popup.getContent().add(list);
        
        // Event management
        this.textField.setOnKeyPressed(this::onKeyPress);
        this.textField.setOnMousePressed((MouseEvent e)->onKeyPress(null));
        this.textField.setOnMouseClicked((MouseEvent e)->updateChoices(textField.textProperty(), null, textField.textProperty().get()));
        this.textField.textProperty().addListener(this::updateChoices);
        
        // If user click or press enter on a popup item, we put selected value as text field value.
        final MultipleSelectionModel<String> sModel = list.getSelectionModel();
        sModel.setSelectionMode(SelectionMode.SINGLE);
        list.setOnKeyPressed((KeyEvent event) -> {
            if(event.getCode()==KeyCode.ENTER){
                final String val = sModel.getSelectedItem();
                if(val!=null){
                    textField.setText(val);
                }
                caretPos = -1;
                moveCaret(textField.getLength());
                popup.hide();
            } else if (KeyCode.TAB.equals(event.getCode())) {
                int selectedIndex = sModel.getSelectedIndex();
                if (selectedIndex < 0 || selectedIndex >= list.getItems().size()-1) {
                    sModel.selectFirst();
                } else {
                    sModel.selectNext();
                }
            }
        });
        
        list.setOnMouseClicked((MouseEvent event)-> {
            if (MouseButton.PRIMARY.equals(event.getButton())) {
                final String val = sModel.getSelectedItem();
                if(val!=null){
                    textField.setText(val);
                }
                caretPos = -1;
                moveCaret(textField.getLength());
                popup.hide();
            }
        });
    }
    
    /**
     * Return list of possible choices to complete input text.
     * @param text The text to find completion for. Can be null or empty.
     * @return A list of possible texts to replace the input one. Can be empty, 
     * but not null.
     */
    protected ObservableList<String> getChoices(String text){
        final ObservableList lst = FXCollections.observableArrayList();
        return lst;
    }
    
    private void onKeyPress(KeyEvent event) {
        if (event == null || event.getCode() == null) {
            return;
        }
        
        final KeyCode code = event.getCode();
        
        if (code == KeyCode.UP) {
            caretPos = -1;
            moveCaret(textField.getLength());
            
        } else if (code == KeyCode.DOWN) {
            if (!popup.isShowing()) {
                final Point2D popupPos = textField.localToScreen(0, textField.getHeight());
                if (popupPos != null) {
                    popup.sizeToScene();
                    popup.show(textField,popupPos.getX(),popupPos.getY());
                }
            }
            caretPos = -1;
            moveCaret(textField.getLength());
            
        } else if (code == KeyCode.BACK_SPACE) {
            caretPos = textField.getCaretPosition();
        } else if (code == KeyCode.DELETE) {
            caretPos = textField.getCaretPosition();
        }
    }

    private void updateChoices(final ObservableValue<? extends String> obs, String oldText, String newText) {
        ObservableList<String> choices = getChoices(newText);
        list.setItems(choices);
        if (choices.isEmpty() || (choices.size()==1 && choices.get(0).equals(newText))) {
            popup.hide();
        } else {
            final Point2D popupPos = textField.localToScreen(0, textField.getHeight());
            if (popupPos != null) {
                popup.sizeToScene();
                popup.show(textField, popupPos.getX(), popupPos.getY());
            }
        }
    }
    
    private void moveCaret(int textLength) {
        if (caretPos == -1) {
            textField.positionCaret(textLength);
        } else {
            textField.positionCaret(caretPos);
        }
    }
    
}
