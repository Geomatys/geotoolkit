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

import java.time.LocalDateTime;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLocalDateTimeCell<S> extends FXTableCell<S, Object> {
    public static final Image ICON_REMOVE = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_TIMES_CIRCLE, 16, FontAwesomeIcons.DEFAULT_COLOR), null);
    private final Button del = new Button(null, new ImageView(ICON_REMOVE));
    private final FXDateField field = new FXDateField();
    private final BorderPane pane = new BorderPane(field, null, del, null, null);

    public FXLocalDateTimeCell() {
        setGraphic(field);
        setAlignment(Pos.CENTER);
        setContentDisplay(ContentDisplay.CENTER);
        del.setPrefSize(16, 16);
        del.setFocusTraversable(false);
        del.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (isEditing()) {
                    commitEdit(null);
                }
            }
        });
        del.setStyle("-fx-background-color:transparent; -fx-focus-color: transparent;");
    }

    @Override
    public void terminateEdit() {
        commitEdit(field.getValue());
    }

    @Override
    public void startEdit() {
        LocalDateTime time = (LocalDateTime) getItem();
        if (time == null) {
            time = LocalDateTime.now();
        }
        field.setValue(time);
        super.startEdit();
        setText(null);
        setGraphic(pane);
        field.getField().requestFocus();
    }

    @Override
    public void commitEdit(Object newValue) {
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
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if (item != null) {
            setText(((LocalDateTime) item).toString());
        }
    }
    
}
