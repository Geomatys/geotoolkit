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

import java.util.function.Consumer;
import java.util.function.Function;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ButtonTableCell<S,T> extends TableCell<S,T> {
    
    private final Function<T,Boolean> visiblePredicate;
    private final Consumer<T> onAction;
    protected final Button button = new Button();
        
    public ButtonTableCell(boolean decorated, Node graphic, Function<T,Boolean> visiblePredicate, Consumer<T> onAction){
        this.visiblePredicate = visiblePredicate;
        this.onAction = onAction;
        if(!decorated){
            button.setBackground(Background.EMPTY);
            button.setBorder(Border.EMPTY);
            button.setPadding(Insets.EMPTY);
        }
        button.setGraphic(graphic);
                
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(onAction!=null)onAction.accept(getItem());
            }
        });
        setGraphic(button);
                
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        
        if(visiblePredicate!=null){
            button.setVisible(visiblePredicate.apply(getItem()));
        }
        
    }
    
}
