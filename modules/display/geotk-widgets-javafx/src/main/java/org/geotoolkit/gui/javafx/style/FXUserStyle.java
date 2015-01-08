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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.gui.javafx.contexttree.TreeMenuItem;
import org.geotoolkit.gui.javafx.contexttree.menu.ActionMenuItem;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.style.FeatureTypeStyleListener;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.RandomStyleBuilder;
import org.geotoolkit.style.RuleListener;
import org.geotoolkit.style.StyleListener;
import org.geotoolkit.style.StyleUtilities;
import org.geotoolkit.util.collection.CollectionChangeEvent;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Rule;
import org.opengis.style.SemanticType;
import org.opengis.style.Style;
import org.opengis.style.Symbolizer;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXUserStyle extends FXStyleElementController<FXUserStyle, MutableStyle>{
    
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
    //used to dissociate selection and apply
    private volatile boolean applying = false;

    public FXUserStyle() {
    }

    @Override
    public void initialize() {
        super.initialize();
        
        menuItems = FXCollections.observableArrayList();
        menuItems.add(new NewFTSAction());
        menuItems.add(new NewRuleAction());
        final List<FXStyleElementController> editors = FXStyleElementEditor.findEditorsForType(Symbolizer.class);
        for(FXStyleElementController editor : editors){
            menuItems.add(new NewSymbolizerAction(editor));
        }
        menuItems.add(new SeparatorMenuItem());
        menuItems.add(new DuplicateAction());
        menuItems.add(new DeleteAction());
        menuItems.add(new SeparatorMenuItem());
        menuItems.add(new ExpandAction());
        menuItems.add(new CollapseAction());
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
        
        tree.setRoot(new StyleTreeItem(styleElement));
        //this will cause the column width to fit the view area
        tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        
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
                            editor.setLayer(getLayer());
                            editor.valueProperty().setValue(val);
                            
                            //listen to editor change
                            editor.valueProperty().addListener(new ChangeListener() {
                                @Override
                                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                                    applyEditor(editorPath);
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
    
    private void applyEditor(final TreeItem oldPath){
        if(editor == null) return;

        //create implies a call to apply if a style element is present
        final Object obj = editor.value.getValue();
        
        if(obj instanceof Symbolizer){
            //in case of a symbolizer we must update it.
            if(oldPath != null){
                final Symbolizer symbol = (Symbolizer) oldPath.getValue();

                if(!symbol.equals(obj) && oldPath.getParent()!=null){
                    oldPath.setValue(obj);
                
                    //new symbol created is different, update in the rule
                    final MutableRule rule = (MutableRule) oldPath.getParent().getValue();

                    final int index = oldPath.getParent().getChildren().indexOf(oldPath);
                    if(index >= 0){
                        rule.symbolizers().set(index, (Symbolizer) obj);
                    }
                }
            }
        }
    }
    
    private void hackClearSelection(){
        //bug in javafx JDK 8u20 : https://javafx-jira.kenai.com/browse/RT-24055
        //clear the selection rather then have an incorrect selection
        //tree.getSelectionModel().clearSelection();
    }
    
    private static class StyleTreeItem extends TreeItem<Object> implements StyleListener, FeatureTypeStyleListener, RuleListener{

        public StyleTreeItem(Object val) {
            valueProperty().addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
                    updateNode(oldValue, newValue);
                    updateChildren(null, CollectionChangeEvent.ITEM_ADDED);
                }
            });
            setValue(val);
        }
        
        private void updateNode(Object oldValue, Object newValue){
            if (oldValue instanceof MutableStyle) {
                final MutableStyle style = (MutableStyle) oldValue;
                style.removeListener(StyleTreeItem.this);
            } else if (oldValue instanceof MutableFeatureTypeStyle) {
                final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) oldValue;
                fts.removeListener(StyleTreeItem.this);
            } else if (oldValue instanceof MutableRule) {
                final MutableRule r = (MutableRule) oldValue;
                r.removeListener(StyleTreeItem.this);
            }

            final ImageView img = new ImageView();
            if (newValue instanceof MutableStyle) {
                final MutableStyle style = (MutableStyle) newValue;
                style.addListener(StyleTreeItem.this);
                img.setImage(GeotkFX.ICON_STYLE);
            } else if (newValue instanceof MutableFeatureTypeStyle) {
                final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) newValue;
                fts.addListener(StyleTreeItem.this);
                img.setImage(GeotkFX.ICON_FTS);
            } else if (newValue instanceof MutableRule) {
                final MutableRule r = (MutableRule) newValue;
                r.addListener(StyleTreeItem.this);
                img.setImage(GeotkFX.ICON_RULE);
            } else if (newValue instanceof Symbolizer) {
                final Symbolizer symb = (Symbolizer) newValue;
                final Dimension dim = DefaultGlyphService.glyphPreferredSize(symb, null, null);
                final BufferedImage imge = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
                DefaultGlyphService.render(symb, new Rectangle(dim),imge.createGraphics(),null);
                img.setImage(SwingFXUtils.toFXImage(imge, null));
            }
            //lbl.setTextAlignment(TextAlignment.CENTER);
            //lbl.setContentDisplay(ContentDisplay.LEFT);
            setGraphic(img);
        }
        
        private void updateChildren(CollectionChangeEvent event, int type){
            if(type != CollectionChangeEvent.ITEM_ADDED && type != CollectionChangeEvent.ITEM_REMOVED) return;
//            if(type == CollectionChangeEvent.ITEM_CHANGED){
//               if(event!=null && event.getChangeEvent()!=null) return;
//            }

            final Object item = getValue();

            //rebuild structure
            final Map<Object,StyleTreeItem> cache = new IdentityHashMap<>();
            for(TreeItem ti : getChildren()){
                cache.put(ti.getValue(), (StyleTreeItem)ti);
            }

            getChildren().clear();

            List itemChildren = Collections.EMPTY_LIST;
            if(item instanceof MutableStyle){
                itemChildren = ((MutableStyle)item).featureTypeStyles();
            }else if(item instanceof MutableFeatureTypeStyle){
                itemChildren = ((MutableFeatureTypeStyle)item).rules();
            }else if(item instanceof MutableRule){
                itemChildren = ((MutableRule)item).symbolizers();
            }
            
            for(Object child : itemChildren){
                StyleTreeItem tmi = cache.get(child);
                if(tmi==null) tmi = new StyleTreeItem(child);
                getChildren().add(tmi);
            }
        }
        
        @Override
        public void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> event) {
            updateChildren(event,event.getType());
        }

        @Override
        public void ruleChange(CollectionChangeEvent<MutableRule> event) {
            updateChildren(event,event.getType());
        }

        @Override
        public void symbolizerChange(CollectionChangeEvent<Symbolizer> event) {
            updateChildren(event,event.getType());
        }
        
        @Override
        public void featureTypeNameChange(CollectionChangeEvent<GenericName> event) {}

        @Override
        public void semanticTypeChange(CollectionChangeEvent<SemanticType> event) {}
        
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        }

    }
    
    class CollapseAction extends ActionMenuItem{

        CollapseAction() {
            super("Collapse sub nodes.",null);
        }

        @Override
        protected void handle(ActionEvent event) {
            super.handle(event);
            for(TreeItem ti : items){
                ti.setExpanded(false);
            }
        }
    }

    class ExpandAction extends ActionMenuItem{

        ExpandAction() {
            super("Expand sub nodes.",null);
        }

        @Override
        protected void handle(ActionEvent event) {
            for(TreeItem ti : items){
                ti.setExpanded(true);
            }
        }
    }

    class NewFTSAction extends ActionMenuItem{

        NewFTSAction() {
            super("new FTS",GeotkFX.ICON_NEW);
        }

        @Override
        public MenuItem init(List<? extends TreeItem> selectedItems) {
            super.init(selectedItems);
            return uniqueAndType(selectedItems, MutableStyle.class) ? menuItem : null;
        }

        @Override
        protected void handle(ActionEvent event) {
            final MutableStyle style = (MutableStyle) items.get(0).getValue();
            style.featureTypeStyles().add(getStyleFactory().featureTypeStyle(
                    RandomStyleBuilder.createRandomPointSymbolizer()));
            hackClearSelection();
        }
    }

    class NewRuleAction extends ActionMenuItem{

        NewRuleAction() {
            super("new Rule",GeotkFX.ICON_NEW);
        }

        @Override
        public MenuItem init(List<? extends TreeItem> selectedItems) {
            super.init(selectedItems);
            return uniqueAndType(selectedItems, MutableFeatureTypeStyle.class) ? menuItem : null;
        }
        
        @Override
        protected void handle(ActionEvent event) {
            final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) items.get(0).getValue();
            fts.rules().add(getStyleFactory().rule(
                    RandomStyleBuilder.createRandomPointSymbolizer()));
            hackClearSelection();
        }
    }

    class NewSymbolizerAction extends ActionMenuItem{
        private final FXStyleElementController editor;

        NewSymbolizerAction(final FXStyleElementController editor) {
            super(editor.getEditedClass().getSimpleName(),GeotkFX.ICON_NEW);
            this.editor = editor;
        }

        @Override
        public MenuItem init(List<? extends TreeItem> selectedItems) {
            super.init(selectedItems);
            return uniqueAndType(selectedItems, MutableRule.class) ? menuItem : null;
        }
        
        @Override
        protected void handle(ActionEvent event) {
            final MutableRule rule = (MutableRule) items.get(0).getValue();
            rule.symbolizers().add((Symbolizer)editor.newValue());
            hackClearSelection();
        }
    }

    class DuplicateAction extends ActionMenuItem {

        DuplicateAction() {
            super("Duplicate", GeotkFX.ICON_DUPLICATE);
        }

        @Override
        public MenuItem init(List<? extends TreeItem> selectedItems) {
            super.init(selectedItems);
            return uniqueAndType(selectedItems, Object.class) ? menuItem : null;
        }
        
        @Override
        protected void handle(ActionEvent event) {
            
            for(TreeItem ti : items){
                if(ti.getParent()==null) continue;
                
                final Object child = ti.getValue();
                final Object parent = ti.getParent().getValue();
                
                if (child instanceof MutableFeatureTypeStyle) {
                    final MutableFeatureTypeStyle fts = StyleUtilities.copy((MutableFeatureTypeStyle) child);
                    final int index = ((MutableStyle)parent).featureTypeStyles().indexOf(child) + 1;
                    ((MutableStyle) parent).featureTypeStyles().add(index, fts);
                } else if (child instanceof MutableRule) {
                    final MutableRule rule = StyleUtilities.copy((MutableRule) child);
                    final int index = ((MutableFeatureTypeStyle)parent).rules().indexOf(child) + 1;
                    ((MutableFeatureTypeStyle) parent).rules().add(index, rule);
                } else if (child instanceof Symbolizer) {
                    //no need to copy symbolizer, they are immutable
                    final Symbolizer symbol = (Symbolizer) child;
                    final int index = ((MutableRule)parent).symbolizers().indexOf(child) + 1;
                    ((MutableRule) parent).symbolizers().add(index, symbol);
                }
            }
            hackClearSelection();
        }
    }

    class DeleteAction extends ActionMenuItem {

        DeleteAction() {
            super("Delete",GeotkFX.ICON_DELETE);
        }

        @Override
        public MenuItem init(List<? extends TreeItem> selectedItems) {
            super.init(selectedItems);
            if(selectedItems.isEmpty()) return null;
            return menuItem;
        }

        @Override
        protected void handle(ActionEvent event) {
            for(TreeItem ti : items){
                if(ti.getParent()==null) continue;
                
                final Object child = ti.getValue();
                final Object parent = ti.getParent().getValue();
                
                if(parent instanceof MutableStyle){
                    ((MutableStyle)parent).featureTypeStyles().remove(child);
                }else if(parent instanceof MutableFeatureTypeStyle){
                    ((MutableFeatureTypeStyle)parent).rules().remove(child);
                }else if(parent instanceof MutableRule){
                    ((MutableRule)parent).symbolizers().remove(child);
                }
            }
            hackClearSelection();
        }
    }

    
}
