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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.TableCell;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXTableCell<S,T> extends TableCell<S,T>{

    private final ChangeListener<TablePosition> terminateCellListener = new ChangeListener<TablePosition>() {
        @Override
        public void changed(ObservableValue observable, TablePosition oldValue, TablePosition pos) {
            if(pos!=null && pos.getRow()==getIndex() && pos.getTableColumn()==getTableColumn()){
                terminateEdit();
            }
        }
    };
    private final WeakChangeListener weak = new WeakChangeListener(terminateCellListener);
    
    public FXTableCell() {
        tableViewProperty().addListener(new ChangeListener<TableView<S>>() {
            @Override
            public void changed(ObservableValue<? extends TableView<S>> observable, TableView<S> oldValue, TableView<S> newValue) {
                if(oldValue instanceof FXTableView){
                    ((FXTableView)oldValue).terminatingCell().removeListener(weak);
                }
                if(newValue instanceof FXTableView){
                    ((FXTableView)newValue).terminatingCell().addListener(weak);
                }
            }
        }
        );
    }
    
    public void terminateEdit(){
        
    }
    
}
