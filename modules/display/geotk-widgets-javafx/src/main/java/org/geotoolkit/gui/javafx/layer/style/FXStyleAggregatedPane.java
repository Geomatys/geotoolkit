/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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

package org.geotoolkit.gui.javafx.layer.style;

import java.io.File;
import org.geotoolkit.gui.javafx.style.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javax.xml.bind.JAXBException;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.gui.javafx.contexttree.TreeMenuItem;
import org.geotoolkit.gui.javafx.layer.FXLayerStylesPane;
import org.geotoolkit.gui.javafx.layer.FXPropertyPane;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.sld.MutableLayer;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.sld.xml.Specification;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableStyle;
import org.opengis.sld.LayerStyle;
import org.opengis.sld.NamedLayer;
import org.opengis.sld.UserLayer;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;
import org.opengis.util.FactoryException;

/**
 * TODO : find a better name.
 *
 * This panel regroups all other panels in a more friendly design.
 * But only display a main tree view.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStyleAggregatedPane extends FXPropertyPane{
    
    private static final MenuItem DUMMY = new CustomMenuItem();
    static {
        DUMMY.setVisible(false);
    }
    
    @FXML protected TreeTableView tree;
    @FXML protected BorderPane contentPane;

    private ObservableList<Object> menuItems;
    private FXStyleTree.ShowClassifRangeAction showClassifRangeItem = new FXStyleTree.ShowClassifRangeAction();
    private FXStyleTree.ShowClassifSingleAction showClassifSingleItem = new FXStyleTree.ShowClassifSingleAction();
    
    //current style element editor
    private TreeItem editorPath;
    private FXStyleElementController editor = null;
    private MapLayer layer;

    public FXStyleAggregatedPane() {
        GeotkFX.loadJRXML(this,this.getClass());
    }

    @Override
    public String getTitle() {
        return GeotkFX.getString(FXLayerStylesPane.class,"style");
    }

    @Override
    public boolean init(Object candidate) {
        if(!(candidate instanceof MapLayer)) return false;
        this.layer = (MapLayer) candidate;
        showClassifRangeItem.setMapLayer(layer);
        showClassifSingleItem.setMapLayer(layer);
        tree.setRoot(new FXStyleTree.StyleTreeItem(this.layer.getStyle()));
        tree.setPlaceholder(new Label(""));
        tree.setShowRoot(false);

        //this will cause the column width to fit the view area
        tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

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
                            editor.setLayer(layer);
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


        return true;
    }

    public void initialize() {
        menuItems = FXCollections.observableArrayList();
        menuItems.add(showClassifRangeItem);
        menuItems.add(showClassifSingleItem);
        menuItems.add(new SeparatorMenuItem());
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

    @FXML
    void addGroup(ActionEvent event) {
        if(layer==null) return;

        final MutableStyle style = layer.getStyle();

        final MutableFeatureTypeStyle fts = GO2Utilities.STYLE_FACTORY.featureTypeStyle();
        style.featureTypeStyles().add(fts);
    }

    @FXML
    void importStyle(ActionEvent event) {
        if(layer==null) return;

        final File result = new FileChooser().showOpenDialog(null);

        if(result==null) return;

        final StyleXmlIO tool = new StyleXmlIO();
        try {
            final MutableStyledLayerDescriptor sld = tool.readSLD(result, Specification.StyledLayerDescriptor.V_1_1_0);

            if(sld != null ){
                for(MutableLayer sldLayer : sld.layers()){
                    if(sldLayer instanceof NamedLayer){
                        final NamedLayer nl = (NamedLayer) sldLayer;
                        for(LayerStyle ls : nl.styles()){
                            if(ls instanceof MutableStyle){
                                layer.setStyle((MutableStyle) ls);
                            }
                        }
                    }else if(sldLayer instanceof UserLayer){
                        final UserLayer ul = (UserLayer) sldLayer;
                        for(Style ls : ul.styles()){
                            if(ls instanceof MutableStyle){
                                layer.setStyle((MutableStyle) ls);
                            }
                        }
                    }
                }
            }
            updateEditor(layer.getStyle());
            return;
        } catch (JAXBException | FactoryException ex) {
            Logging.getLogger(FXStyleXMLPane.class).log(Level.FINEST,ex.getMessage(),ex);
        }

        try {
            final MutableStyle style = tool.readStyle(result, Specification.SymbologyEncoding.V_1_1_0);
            layer.setStyle(style);
            updateEditor(layer.getStyle());
        } catch (JAXBException | FactoryException ex) {
            Logging.getLogger(FXStyleXMLPane.class).log(Level.FINEST,ex.getMessage(),ex);
        }

    }

    @FXML
    void exportStyle(ActionEvent event) {
        if(layer==null) return;

        final MutableStyle style = layer.getStyle();
        final File result = new FileChooser().showSaveDialog(null);
        if(result==null) return;

        final StyleXmlIO tool = new StyleXmlIO();
        try {
            tool.writeStyle(result, style, Specification.StyledLayerDescriptor.V_1_1_0);
        } catch (JAXBException ex) {
            Logging.getLogger(FXStyleXMLPane.class).log(Level.WARNING,ex.getMessage(),ex);
        }
    }

    protected void updateEditor(MutableStyle styleElement) {
        
        tree.setRoot(new FXStyleTree.StyleTreeItem(styleElement));
        //this will cause the column width to fit the view area
        tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        tree.getColumns().clear();
        
        final TreeTableColumn<Object, String> col = new TreeTableColumn<>();
        col.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Object, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object, String> param) {
                final Object obj = param.getValue().getValue();
                if(obj instanceof Style){
                    return FXUtilities.beanProperty(obj, "name", String.class);
                }else if(obj instanceof FeatureTypeStyle){
                    return FXUtilities.beanProperty(obj, "name", String.class);
                }else if(obj instanceof Rule){
                    return FXUtilities.beanProperty(obj, "name", String.class);
                }else if(obj instanceof Symbolizer){
                    return new SimpleObjectProperty<>(((Symbolizer)obj).getName());
                }else{
                    return new SimpleObjectProperty<>("");
                }
            }
        });
        col.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        col.setPrefWidth(200);
        col.setMinWidth(120);
        
                
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
                            editor.setLayer(layer);
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
