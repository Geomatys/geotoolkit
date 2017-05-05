/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2015, Geomatys
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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFX;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapItemSelectableColumn extends TreeTableColumn{

    private static final Tooltip LOCK_TOOLTIP = new Tooltip(
            GeotkFX.getString(MapItemSelectableColumn.class, "lockTooltip"));
    private static final Tooltip UNLOCK_TOOLTIP = new Tooltip(
            GeotkFX.getString(MapItemSelectableColumn.class, "unlockTooltip"));

    public MapItemSelectableColumn() {
        setEditable(true);
        setPrefWidth(26);
        setMinWidth(26);
        setMaxWidth(26);

        setCellValueFactory((Object param) -> {
            try {
                return FXUtilities.beanProperty(((CellDataFeatures)param).getValue().getValue(), "selectable", Boolean.class);
            } catch (IllegalArgumentException e) {
                return null;
            }
        });
        setCellFactory((Object param) -> new SelectableCell());
    }

    private static final class SelectableCell extends TreeTableCell{

        public SelectableCell() {
            setFont(FXUtilities.FONTAWESOME);
            setOnMouseClicked(this::mouseClick);
            textProperty().addListener(new ChangeListener<String>() {

                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (FontAwesomeIcons.ICON_LOCK.equals(newValue)) {
                        setTooltip(UNLOCK_TOOLTIP);
                    } else if(FontAwesomeIcons.ICON_UNLOCK.equals(newValue)) {
                        setTooltip(LOCK_TOOLTIP);
                    }
                }
            });
            setTextAlignment(TextAlignment.CENTER);
            setAlignment(Pos.CENTER);
        }

        private void mouseClick(MouseEvent event){
            event.consume();
            if(!isEditing()){
                getTreeTableView().edit(getTreeTableRow().getIndex(), getTableColumn());
            }
            if(getItem()!=null){
                commitEdit(!Boolean.TRUE.equals(getItem()));
            }
        }

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if(!empty && item != null){
                setText(Boolean.TRUE.equals(item) ? FontAwesomeIcons.ICON_UNLOCK : FontAwesomeIcons.ICON_LOCK);
            }else{
                setText(null);
            }
        }
    }

}
