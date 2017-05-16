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

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import org.geotoolkit.internal.Loggers;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ButtonTableCell<S,T> extends TableCell<S,T> {

    private final Predicate<T> visiblePredicate;
    protected final Button button = new Button();

    public ButtonTableCell(final boolean decorated,
            final Node graphic,
            final Predicate<T> visiblePredicate,
            final Function<T,T> onAction){

        this.visiblePredicate = visiblePredicate;
        button.setGraphic(graphic);
        setGraphic(button);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        setAlignment(Pos.CENTER);

        if(!decorated){
            button.setBackground(Background.EMPTY);
            button.setBorder(Border.EMPTY);
            button.setPadding(Insets.EMPTY);
        }
        button.disableProperty().bind(editableProperty().not());

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final int rowIndex = getTableRow().getIndex();
                if(onAction!=null) {
                    if(!isEditing()) {
                        getTableView().edit(rowIndex, getTableColumn());
                    }

                    final T item = getTableColumn().getCellData(rowIndex);
                    final T res = onAction.apply(item);
                    if(!Objects.equals(res,item)){
                        try {
                            itemProperty().set(res);
                            commitEdit(res);
                        } catch(Throwable ex) {
                            Loggers.JAVAFX.log(Level.WARNING, "Button table cell edition failed.", ex);
                            cancelEdit();
                        }
                    }
                }
            }
        });

        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                TableRow row = ButtonTableCell.this.getTableRow();
                TableView table = ButtonTableCell.this.getTableView();
                table.getSelectionModel().clearSelection();
                table.requestFocus();
                table.getSelectionModel().select((S) row.getItem());
                table.getFocusModel().focus(row.getIndex());
            }
        });

    }


    @Override
    public void commitEdit(T newValue) {
        super.commitEdit(newValue);
        updateItem(newValue, false);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        button.setVisible(!empty && (visiblePredicate==null || visiblePredicate.test(item)));
    }

}
