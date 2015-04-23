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

package org.geotoolkit.gui.javafx.style;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import org.geotoolkit.gui.javafx.contexttree.TreeMenuItem;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.style.MutableStyle;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXUserStyle extends FXStyleElementController<MutableStyle>{
    
    private static final MenuItem DUMMY = new CustomMenuItem();
    static {
        DUMMY.setVisible(false);
    }
    
    @FXML
    protected TreeTableView tree;
    @FXML
    protected BorderPane contentPane;

    private ObservableList<Object> menuItems;
    
    //current style element editor
    private TreeItem editorPath;
    private FXStyleElementController editor = null;

    public FXUserStyle() {
    }

    @Override
    public void initialize() {
        super.initialize();
        
        menuItems = FXCollections.observableArrayList();
        menuItems.add(new FXStyleTree.NewFTSAction());
        menuItems.add(new FXStyleTree.NewRuleAction());
        final List<FXStyleElementController> editors = FXStyleElementEditor.findEditorsForType(Symbolizer.class);
        for(FXStyleElementController editor : editors){
            menuItems.add(new FXStyleTree.NewSymbolizerAction(editor));
        }
        menuItems.add(new SeparatorMenuItem());
        menuItems.add(new FXStyleTree.DuplicateAction());
        menuItems.add(new FXStyleTree.DeleteAction());

        FXUtilities.hideTableHeader(tree);
    }
    
    @Override
    public Class<MutableStyle> getEditedClass() {
        return MutableStyle.class;
    }

    @Override
    public MutableStyle newValue() {
        return getStyleFactory().style();
    }
    
    @Override
    protected void updateEditor(MutableStyle styleElement) {
        
        tree.setRoot(new FXStyleTree.StyleTreeItem(styleElement));
        //this will cause the column width to fit the view area
        tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        tree.setPlaceholder(new Label(""));

        final TreeTableColumn col = new FXStyleTree.NameColumn();
                
        final ContextMenu menu = new ContextMenu();
        tree.setContextMenu(menu);
        tree.getColumns().add(col);                
        tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //dummy item to ensure showing will be called
        menu.getItems().add(DUMMY);
                
        menu.setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {                
                //update menu items
                final ObservableList items = menu.getItems();
                items.clear();
                items.add(DUMMY);
                final List<TreeItem> selection = new ArrayList<>();
                for(Object i : tree.getSelectionModel().getSelectedCells()){
                    final TreeTablePosition ttp = (TreeTablePosition) i;                    
                    final TreeItem ti = tree.getTreeItem(ttp.getRow());
                    if(ti!=null && !selection.contains(ti)) selection.add(ti);
                }
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
                
        tree.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {                
                final TreeItem treeItem = (TreeItem) tree.getSelectionModel().getSelectedItem();
                
//                //we validate the previous edition pane
//                if(!applying){
//                    //we keep the same editor if we are currently applying changes
//                    applyEditor(editorPath);

                    contentPane.setCenter(null);

                    if(treeItem!=null){
                        final Object val = treeItem.getValue();
                        editorPath = treeItem;
                        editor = FXStyleElementEditor.findEditor(val);
                        if(editor != null){
                            editor.setLayer(getLayer());
                            editor.valueProperty().setValue(val);
                            
                            //listen to editor change
                            editor.valueProperty().addListener(new ChangeListener() {
                                @Override
                                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                                    FXStyleTree.applyTreeItemEditor(editor,editorPath);
                                }
                            });
                            contentPane.setCenter(editor);
                        }
                    }
//                }
            }
        });
        
        FXUtilities.expandAll(tree.getRoot());
    }
    
}
