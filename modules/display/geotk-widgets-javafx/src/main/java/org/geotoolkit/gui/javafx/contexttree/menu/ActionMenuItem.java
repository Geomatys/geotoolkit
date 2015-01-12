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

package org.geotoolkit.gui.javafx.contexttree.menu;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.geotoolkit.gui.javafx.contexttree.TreeMenuItem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ActionMenuItem extends TreeMenuItem{

    protected List<? extends TreeItem> items;
    
    public ActionMenuItem(String title, Image icon) {
        super(new MenuItem(title));
        if(icon!=null){
            menuItem.setGraphic(new ImageView(icon));
        }
        
        menuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ActionMenuItem.this.handle(event);
            }
        });
    }

    @Override
    public MenuItem init(List<? extends TreeItem> selectedItems) {
        items = new ArrayList<>(selectedItems);
        return super.init(selectedItems);
    }
    
    protected void handle(ActionEvent event){
        
    }
    
}
