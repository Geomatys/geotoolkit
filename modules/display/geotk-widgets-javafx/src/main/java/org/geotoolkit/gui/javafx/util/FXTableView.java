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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

/**
 * Subclass to JavaFX TableView to support the very important missing feature :
 * terminate edition on cell change / focus lost.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class FXTableView<T> extends TableView<T>{
    
    private final SimpleObjectProperty<TablePosition> terminatingCell = new SimpleObjectProperty<>();
    
    public ReadOnlyObjectProperty<TablePosition> terminatingCell(){
        return terminatingCell;
    }

    @Override
    public void edit(int row, TableColumn<T, ?> column) {
        terminatingCell.set(getEditingCell());
        super.edit(row, column);
        terminatingCell.set(null);
    }
    
}
