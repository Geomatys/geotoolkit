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

import java.util.List;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.geotoolkit.internal.GeotkFX;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXMoveDownTableColumn extends TableColumn{

    public FXMoveDownTableColumn() {
        super("Down");
        setSortable(false);
        setResizable(false);
        setPrefWidth(24);
        setMinWidth(24);
        setMaxWidth(24);
        setGraphic(new ImageView(GeotkFX.ICON_MOVEDOWN));
        
        setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                @Override
                public ObservableValue call(TableColumn.CellDataFeatures param) {
                    return new SimpleObjectProperty<>(param.getValue());
                }
            });
        
        setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(final TableColumn param) {
                return new ButtonTableCell(
                    false,new ImageView(GeotkFX.ICON_MOVEDOWN), (Object t) -> true, new Function() {
                        @Override
                        public Object apply(Object t) {
                            final TableView uiTable = param.getTableView();
                            int index = identityIndex(t, uiTable.getItems());
                            if(index>=0 && index<uiTable.getItems().size()-1){
                                uiTable.getItems().remove(index);
                                index++;
                                uiTable.getItems().add(index, t);
                            }
                            return t;
                        }
                    }); 
            }
        });
        
    }
    
    private static int identityIndex(Object obj, List lst){
        for(int i=0,n=lst.size();i<n;i++){
            if(lst.get(i)==obj)return i;
        }
        return -1;
    }
    
}
