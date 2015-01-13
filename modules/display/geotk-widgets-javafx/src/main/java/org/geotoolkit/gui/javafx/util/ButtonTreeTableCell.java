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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @param <S>
 * @param <T>
 */
public class ButtonTreeTableCell<S,T> extends TreeTableCell<S,T> {
    
    private final Function<T,Boolean> visiblePredicate;
    private final Function<T,T> onAction;
    protected final Button button = new Button();
        
    public ButtonTreeTableCell(boolean decorated, Node graphic, Function<T,Boolean> visiblePredicate, final Function<T,T> onAction){
        this.visiblePredicate = visiblePredicate;
        this.onAction = onAction;
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
                
                if(!isEditing()){
                    getTreeTableView().edit(getTreeTableRow().getIndex(), getTableColumn());
                }
                
                final T item = getItem();
                final T res = actionPerformed(item);
                if(!Objects.equals(res,item)){
                    try{
                        itemProperty().set(res);
                        commitEdit(res);
                    }catch(Throwable ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
        
    }

    public T actionPerformed(T candidate){
        if(onAction!=null){
            return onAction.apply(candidate);
        }
        return candidate;
    } 
    
    @Override
    public void commitEdit(T newValue) {
        super.commitEdit(newValue);
        updateItem(newValue, false);
    }
    
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        button.setVisible(!empty);
        if(visiblePredicate!=null){
            final boolean visible = visiblePredicate.apply(item);
            button.setVisible(visible);
        }
    }
}
