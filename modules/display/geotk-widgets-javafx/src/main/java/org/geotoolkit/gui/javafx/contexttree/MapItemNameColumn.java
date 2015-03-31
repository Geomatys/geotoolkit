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

package org.geotoolkit.gui.javafx.contexttree;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import javafx.util.converter.DefaultStringConverter;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFX;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapItemNameColumn<T> extends TreeTableColumn<T,String>{

    public MapItemNameColumn() {
        super(GeotkFX.getString(MapItemNameColumn.class,"layers"));
        setCellValueFactory(param -> FXUtilities.beanProperty(((CellDataFeatures)param).getValue().getValue(), "name", String.class));
        //setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        setCellFactory((TreeTableColumn<T, String> param) -> new Cell());
        setEditable(true);
        setPrefWidth(200);
        setMinWidth(120);
    }

    public static class Cell extends TextFieldTreeTableCell{

        public Cell(){
            super(new DefaultStringConverter());
        }

        @Override
        public void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            setContentDisplay(ContentDisplay.LEFT);
            setAlignment(Pos.CENTER_LEFT);
            setTextAlignment(TextAlignment.LEFT);
            setWrapText(false);
            if(empty) return;

            String str = (item==null)? " " : String.valueOf(item);
            if(str.isEmpty()) str = " ";
            setText(str);
            final TreeTableRow row = getTreeTableRow();
            if(row==null) return;
            final TreeItem ti = row.getTreeItem();
            if(ti==null) return;

            if(ti instanceof StyleMapItem){
                final ImageView view = new ImageView();
                view.imageProperty().bind(((StyleMapItem)ti).imageProperty());
                final BorderPane pane = new BorderPane(view);
                pane.setMaxSize(BorderPane.USE_COMPUTED_SIZE,Double.MAX_VALUE);
                pane.setPrefSize(BorderPane.USE_COMPUTED_SIZE, BorderPane.USE_COMPUTED_SIZE);
                setGraphic(pane);
            }

        }

    }

}
