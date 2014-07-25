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

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.map.MapItem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXMapContextTree extends BorderPane{

    private final List<TreeMenuItem> menuItems = new ArrayList<>();
    private final TreeTableView treetable = new TreeTableView();
    private final ScrollPane scroll = new ScrollPane(treetable);
    private MapItem mapItem;
    
    
    public FXMapContextTree() {
        this(null);
    }
    
    public FXMapContextTree(MapItem item){    
        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);
        setCenter(scroll);
        
        //configure treetable
        treetable.getColumns().add(new MapItemNameColumn());
        treetable.getColumns().add(new MapItemGlyphColumn());
        treetable.getColumns().add(new MapItemVisibleColumn());
        treetable.setTableMenuButtonVisible(false);
        treetable.setEditable(true);
        treetable.setContextMenu(new ContextMenu());
        
        //this will cause the column width to fit the view area
        treetable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
                
        final ContextMenu menu = new ContextMenu();
        treetable.setContextMenu(menu);
        
        //update context menu based on selected items
        treetable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                menu.getItems().clear();
                final List<? extends TreeItem> selection = treetable.getSelectionModel().getSelectedItems();
                for(TreeMenuItem mi : menuItems){
                    final MenuItem candidate = mi.init(selection);
                    if(candidate!=null) menu.getItems().add(candidate);
                }
            }
        });
                
        setMapItem(item);
    }
    
    public TreeTableView getTreetable() {
        return treetable;
    }

    public List<TreeMenuItem> getMenuItems() {
        return menuItems;
    }
    
    public MapItem getMapItem() {
        return mapItem;
    }

    public void setMapItem(MapItem mapItem) {
        if(this.mapItem == mapItem) return;
        this.mapItem = mapItem;
        
        if(mapItem==null){
            treetable.setRoot(null);
        }else{
            treetable.setRoot(new TreeMapItem(mapItem));
        }
        
        treetable.setShowRoot(true);
    }
    
    
    
}
