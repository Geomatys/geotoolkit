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

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseEvent;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.gui.javafx.util.FXUtilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapItemSelectableColumn extends TreeTableColumn<Object,Boolean>{

    public MapItemSelectableColumn() {
        setEditable(true);
        setPrefWidth(26);
        setMinWidth(26);
        setMaxWidth(26);

        setCellValueFactory(param -> FXUtilities.beanProperty(((CellDataFeatures)param).getValue().getValue(), "selectable", Boolean.class));
        setCellFactory((TreeTableColumn<Object, Boolean> param) -> new SelectableCell());
    }

    private static final class SelectableCell extends TreeTableCell{
        
        public SelectableCell() {
            setFont(FXUtilities.FONTAWESOME);
            setOnMouseClicked(this::mouseClick);
        }

        private void mouseClick(MouseEvent event){
            if(isEditing()){
                final Boolean val = getText().equals(FontAwesomeIcons.ICON_LOCK);
                commitEdit(val);
            }
        }

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if(!empty){
                setText(Boolean.TRUE.equals(item) ? FontAwesomeIcons.ICON_UNLOCK : FontAwesomeIcons.ICON_LOCK);
            }else{
                setText(null);
            }
        }
    }

}
