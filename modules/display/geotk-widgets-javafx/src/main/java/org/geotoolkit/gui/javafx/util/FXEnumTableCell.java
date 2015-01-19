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

import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;

/**
 *
 * @author Samuel Andrés (Geomatys)
 * @param <S>
 * @param <T>
 */
public class FXEnumTableCell<S, T extends Enum> extends FXTableCell<S, T> {
    private final ComboBox<T> field = new ComboBox<>();

    public FXEnumTableCell() {
        field.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                commitEdit(field.getValue());
            }
        });
        setGraphic(field);
        setAlignment(Pos.CENTER);
        setContentDisplay(ContentDisplay.CENTER);
    }

//    @Override
//    public void terminateEdit() {
//        commitEdit(comboField.getSelectionModel().getSelectedItem());
//    }

    @Override
    public void startEdit() {
        T value = getItem();
        T[] values = (T[]) value.getClass().getEnumConstants();
        field.setItems(FXCollections.observableArrayList(Arrays.asList(values)));
        field.getSelectionModel().select(value);
        super.startEdit();
        setText(null);
        setGraphic(field);
        field.requestFocus();
    }

    @Override
    public void commitEdit(T newValue) {
        itemProperty().set(newValue);
        super.commitEdit(newValue);
        updateItem(newValue, false);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        updateItem(getItem(), false);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if (item != null) {
            setText(item.toString());
        }
    }
    
}
