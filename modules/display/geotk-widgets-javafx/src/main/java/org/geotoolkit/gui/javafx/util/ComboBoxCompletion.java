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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Auto-completion for combobox.
 * Perform text auto-completion on textfields of editable combo-box. combo-box
 * {@link ComboBox#items } are adapted  based on user typing.
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public class ComboBoxCompletion {

    private final ComboBox comboBox;
    private ObservableList baseData;
    private ObservableList filteredData;

    public ComboBoxCompletion(final ComboBox comboBox) {
        this.comboBox = comboBox;
        this.comboBox.setOnKeyReleased(this::onKeyReleased);
        baseData = comboBox.getItems();
        comboBox.itemsProperty().addListener(this::updateBaseData);
    }

    /**
     * Update available {@link ComboBox#items} according to typed value.
     * @param event
     */
    private void onKeyReleased(KeyEvent event) {
        if (baseData == null || baseData.isEmpty()) {
            return;
        }

        final KeyCode code = event.getCode();
        if (event.isControlDown() || code == KeyCode.HOME || code == KeyCode.END || code == KeyCode.TAB) {
            return;
        }

        final TextField editor = comboBox.getEditor();
        final int caretPosition = editor.getCaretPosition();
        if (code == KeyCode.DOWN) {
            if (!comboBox.isShowing()) {
                comboBox.show();
                editor.positionCaret(caretPosition);
            }

        } else {
            final String completeText = editor.getText();
            final String currentText = editor.getText(0, caretPosition);
            if (currentText != null) {
                comboBox.hide(); // hide to force resize when we'll have to show it back.
                final Pattern searchPattern = buildPattern(currentText);
                filteredData = FXCollections.observableArrayList(
                        baseData.filtered((Object t) -> searchPattern.matcher(comboBox.getConverter().toString(t)).find()));
                comboBox.setItems(filteredData);
                if (!filteredData.isEmpty()) {
                    comboBox.show();
                }
                // we're forced to set back editor text, because change of items
                // also change it.
                editor.setText(completeText);
                editor.positionCaret(caretPosition);
            }
        }
    }

    private void updateBaseData(ObservableValue observable, Object oldValue, Object newValue) {
        if (comboBox.getItems() != filteredData) {
            baseData = comboBox.getItems();
        }
    }

    /**
     * Adapt text typed in editor to build a permissive regex to search for
     * correspondance in combo box items.
     *
     * @param fieldText The text to transform in regex pattern.
     * @return created pattern
     */
    public static Pattern buildPattern(final String fieldText) {
        final String pattern = fieldText
                // We do not need groups in this particular case, so we assume user has real parenthesis in his text.
                .replaceAll("(\\()|(\\))", "\\$1")
                // In case user mistype word separation (Ex : typed "Point10" instead of "Point 10" or "type-1" instead of "type - 1").
                .replaceAll("([a-zA-Z]+|\\d+|\\W+)\\s*", "$1\\\\s*");
        return Pattern.compile("(?i)"+pattern);
    }

    /**
     * Build a new combobox completion but does not return it to avoid warnings
     * "new instance ignored".
     *
     * @param combobox to apply completion on
     */
    public static void autocomplete(final ComboBox combobox){
        new ComboBoxCompletion(combobox);
    }
}
