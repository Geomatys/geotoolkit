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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXMapContextTree extends BorderPane{

    private static final DataFormat MAPITEM_FORMAT = new DataFormat("contextItem");
    
    private final ObservableList<Object> menuItems = FXCollections.observableArrayList();
    private final TreeTableView<MapItem> treetable = new TreeTableView(){
        
        private void test(){
            Parent p = null;
        }
        
    };
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
        
        treetable.setRowFactory(new Callback<TreeTableView<MapItem>, TreeTableRow<MapItem>>() {
            public TreeTableRow<MapItem> call(TreeTableView<MapItem> param) {
                final TreeTableRow row = new TreeTableRow();
                initDragAndDrop(row);
                return row;
            }
        });
        
        //this will cause the column width to fit the view area
        treetable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
                
        final ContextMenu menu = new ContextMenu();
        treetable.setContextMenu(menu);
        
        //update context menu based on selected items
        treetable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        treetable.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {
                final ObservableList items = menu.getItems();
                items.clear();
                final List<? extends TreeItem> selection = treetable.getSelectionModel().getSelectedItems();
                for(int i=0,n=menuItems.size();i<n;i++){
                    final Object candidate = menuItems.get(i);
                    if(candidate instanceof TreeMenuItem){
                        final MenuItem mc = ((TreeMenuItem)candidate).init(selection);
                        if(mc!=null) items.add(mc);
                    }else if(candidate instanceof SeparatorMenuItem){
                        //special case, we don't want any separator at the start or end
                        //or 2 succesive separators
                        if(i==0 || i==n-1 || items.isEmpty()) continue;
                        
                        if(items.get(items.size()-1) instanceof SeparatorMenuItem){
                            continue;
                        }
                        items.add((SeparatorMenuItem)candidate);
                        
                    }else if(candidate instanceof MenuItem){
                        items.add((MenuItem)candidate);
                    }
                }
                
                //special case, we don't want any separator at the start or end
                if(!items.isEmpty()){
                    if(items.get(0) instanceof SeparatorMenuItem){
                        items.remove(0);
                    }
                    if(!items.isEmpty()){
                        final int idx = items.size()-1;
                        if(items.get(idx) instanceof SeparatorMenuItem){
                            items.remove(idx);
                        }
                    }
                }
                
                
            }
        });
                
        setMapItem(item);
    }
    
    private void initDragAndDrop(final TreeTableRow row){
        row.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                final int selection = treetable.getSelectionModel().getSelectedIndex();
                final Dragboard db = treetable.startDragAndDrop(TransferMode.MOVE);
                db.setContent(Collections.singletonMap(MAPITEM_FORMAT, selection));
                event.consume();
            }
        });

        row.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getDragboard().hasContent(MAPITEM_FORMAT)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            }
        });

        row.setOnDragEntered(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {                
                event.consume();
            }
        });

        row.setOnDragExited(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                event.consume();
            }
        });
        
        row.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                
                final Dragboard db = event.getDragboard();
                boolean success = false;
                
                conditions:
                if (db.hasContent(MAPITEM_FORMAT)) {
                    final int index = (Integer) db.getContent(MAPITEM_FORMAT);
                    if(index>=0){
                        final TreeMapItem targetRow = (TreeMapItem)row.getTreeItem();                        
                        final MapItem targetItem = targetRow.getValue();
                        final MapItem targetParent = targetRow.getParent().getValue();
                        
                        final TreeMapItem movedRow = (TreeMapItem) treetable.getSelectionModel().getSelectedItem();
                        final MapItem movedItem = movedRow.getValue();
                        final MapItem movedParent = movedRow.getParent().getValue();
                                                
                        if(movedParent!=null && targetItem!=null && movedItem!=targetItem 
                                && !FXUtilities.isParent(movedRow, targetRow)){
                            
                            movedParent.items().remove(movedItem);
                            if(targetItem instanceof MapLayer){
                                //insert as sibling
                                final int insertIndex = targetParent.items().indexOf(targetItem);
                                targetParent.items().add(insertIndex,movedItem);
                            }else{
                                //insert as children
                                targetItem.items().add(movedItem);
                            }
                        }                        
                    }                    
                    success = true;
                }
                
                event.setDropCompleted(success);                
                event.consume();
            }
        });

        row.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                event.consume();
            }
        });
    }
        
    public TreeTableView getTreetable() {
        return treetable;
    }

    /**
     * This list can contain MenuItem of TreeMenuItem.
     * 
     * @return ObservableList of contextual menu items.
     */
    public ObservableList<Object> getMenuItems() {
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
