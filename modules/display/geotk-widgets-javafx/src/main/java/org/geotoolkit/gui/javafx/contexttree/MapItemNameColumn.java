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

import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFXBundle;
import org.geotoolkit.map.MapItem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapItemNameColumn extends TreeTableColumn<MapItem,String>{

    public MapItemNameColumn() {
        super(GeotkFXBundle.getString(MapItemNameColumn.class,"layers"));
        setCellValueFactory(param -> FXUtilities.beanProperty(((CellDataFeatures)param).getValue().getValue(), "name", String.class));     
        setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        setEditable(true);
        setPrefWidth(200);
        setMinWidth(120);
    }
    
}
