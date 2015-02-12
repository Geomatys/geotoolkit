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

import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Auto-completion for combobox.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class ComboBoxCompletion {
    
    private final ComboBox comboBox;
    private final ObservableList baseData;
    private boolean moveCaretToPos = false;
    private int caretPos;

    public ComboBoxCompletion(final ComboBox comboBox) {
        this.comboBox = comboBox;
        this.baseData = comboBox.getItems();
        this.comboBox.setOnKeyPressed((KeyEvent t)->comboBox.hide());
        this.comboBox.setOnKeyReleased(this::onKeyPress);
    }

    private void onKeyPress(KeyEvent event) {
        final KeyCode code = event.getCode();
        final String text = comboBox.getEditor().getText();
        
        if (code == KeyCode.UP) {
            caretPos = -1;
            moveCaret(text.length());
            return;
        } else if (code == KeyCode.DOWN) {
            if (!comboBox.isShowing()) {
                comboBox.show();
            }
            caretPos = -1;
            moveCaret(text.length());
            return;
        } else if (code == KeyCode.BACK_SPACE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        } else if (code == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        }
        if (code == KeyCode.RIGHT || code == KeyCode.LEFT || event.isControlDown() || code == KeyCode.HOME || code == KeyCode.END || code == KeyCode.TAB) {
            return;
        }
        
        final Pattern searchPattern = Pattern.compile("(?i)"+comboBox.getEditor().getText());
        final ObservableList list = FXCollections.observableArrayList(
                baseData.filtered((Object t) -> searchPattern.matcher(comboBox.getConverter().toString(t)).find())
        );
        
        comboBox.setItems(list);
        comboBox.getEditor().setText(text);
        if (!moveCaretToPos) {
            caretPos = -1;
        }
        
        moveCaret(text.length());
        if (!list.isEmpty()) {
            comboBox.show();
        }
    }

    private void moveCaret(int textLength) {
        if (caretPos == -1) {
            comboBox.getEditor().positionCaret(textLength);
        } else {
            comboBox.getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }
    
}
