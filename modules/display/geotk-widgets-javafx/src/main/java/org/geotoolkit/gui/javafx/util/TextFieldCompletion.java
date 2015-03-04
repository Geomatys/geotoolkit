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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;

/**
 * Auto-completion for textfield.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class TextFieldCompletion {
    
    private final TextField textField;
    private final Popup popup = new Popup();
    private final ListView<String> list = new ListView<>();
    private boolean moveCaretToPos = false;
    private int caretPos;

    public TextFieldCompletion(final TextField textField) {
        this.textField = textField;
        this.textField.setOnKeyPressed((KeyEvent t)->popup.hide());
        this.textField.setOnKeyReleased(this::onKeyPress);
        this.textField.setOnMousePressed((MouseEvent e)->onKeyPress(null));
        
        final ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        
        popup.setAutoHide(true);
        popup.getContent().add(scroll);
        
        // If user click or press enter on a popup item, we put selected value as text field value.
        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        list.setOnKeyPressed((KeyEvent event) -> {
            if(event.getCode()==KeyCode.ENTER){
                final String val = list.getSelectionModel().getSelectedItem();
                if(val!=null){
                    textField.setText(val);
                }
                popup.hide();
            }
        });
        
        list.setOnMouseClicked((MouseEvent event)-> {
            if (MouseButton.PRIMARY.equals(event.getButton())) {
                final String val = list.getSelectionModel().getSelectedItem();
                if(val!=null){
                    textField.setText(val);
                }
                popup.hide();
            }
        });
    }
    
    protected ObservableList<String> getChoices(String text){
        final ObservableList lst = FXCollections.observableArrayList();
        return lst;
    }
    
    private void onKeyPress(KeyEvent event) {
        final KeyCode code;
        if (event != null) {
            code = event.getCode();
        } else {
            code = null;
        }
        final String text = textField.getText();
        final ObservableList baseData = getChoices(text);
        
        final Point2D popupPos = textField.localToScreen(0, textField.getHeight());
        
        if (code == KeyCode.UP) {
            caretPos = -1;
            moveCaret(text.length());
            return;
        } else if (code == KeyCode.DOWN) {
            if (!popup.isShowing()) {
                popup.setWidth(list.getPrefWidth()+10);
                popup.show(textField,popupPos.getX(),popupPos.getY());
            }
            caretPos = -1;
            moveCaret(text.length());
            return;
        } else if (code == KeyCode.BACK_SPACE) {
            moveCaretToPos = true;
            caretPos = textField.getCaretPosition();
        } else if (code == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = textField.getCaretPosition();
        }
        if (code == KeyCode.RIGHT || code == KeyCode.LEFT || (event != null && event.isControlDown()) || code == KeyCode.HOME || code == KeyCode.END || code == KeyCode.TAB) {
            return;
        }
        
        final String searchText = textField.getText().toLowerCase();
        final ObservableList dataList;
        if (searchText == null || searchText.isEmpty()) {
            dataList = FXCollections.observableArrayList(baseData);
        } else {
            dataList = FXCollections.observableArrayList(
                baseData.filtered((Object t) -> String.valueOf(t).toLowerCase().startsWith(searchText)));
        }
        
        list.setItems(dataList);
        list.getSelectionModel().clearSelection();
        textField.setText(text);
        if (!moveCaretToPos) {
            caretPos = -1;
        }
        
        moveCaret(text.length());
        if (!dataList.isEmpty()) {
            popup.setWidth(list.getPrefWidth()+10);
            popup.show(textField,popupPos.getX(),popupPos.getY());
        }else{
            popup.hide();
        }
    }

    private void moveCaret(int textLength) {
        if (caretPos == -1) {
            textField.positionCaret(textLength);
        } else {
            textField.positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }
    
}
