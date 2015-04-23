/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2015, Geomatys
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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import static javafx.beans.binding.Bindings.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.contexttree.menu.ActionMenuItem;
import org.geotoolkit.gui.javafx.contexttree.menu.LayerPropertiesItem;
import org.geotoolkit.gui.javafx.layer.style.FXStyleClassifRangePane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleClassifSinglePane;
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getStyleFactory;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapLayer;
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
 * Utility classes to build a style editor tree.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class FXStyleTree {

    public static final Image ICON_GROUP = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_FOLDER_O,24,FontAwesomeIcons.DEFAULT_COLOR),null);

    /**
     * Validate the editor for given path.
     * If edited object is a symbolizer this method will properly replace the symbolizer.
     *
     * @param editor
     * @param oldPath
     */
    public static void applyTreeItemEditor(final FXStyleElementController editor, final TreeItem oldPath){
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

    public static class StyleTreeItem extends TreeItem<Object> implements StyleListener, FeatureTypeStyleListener, RuleListener{

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
                setGraphic(img);
            } else if (newValue instanceof MutableFeatureTypeStyle) {
                final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) newValue;
                fts.addListener(StyleTreeItem.this);
                img.setImage(ICON_GROUP);
                setGraphic(img);
            } else if (newValue instanceof MutableRule) {
                final MutableRule r = (MutableRule) newValue;
                r.addListener(StyleTreeItem.this);
                img.setImage(GeotkFX.ICON_RULE);
                setGraphic(img);
            } else if (newValue instanceof Symbolizer) {
                //do nothing, the name column handle the graphic and label
                setGraphic(null);
            }
            //lbl.setTextAlignment(TextAlignment.CENTER);
            //lbl.setContentDisplay(ContentDisplay.LEFT);
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
    
    private static void hackClearSelection(){
        //bug in javafx JDK 8u20 : https://javafx-jira.kenai.com/browse/RT-24055
        //clear the selection rather then have an incorrect selection
        //tree.getSelectionModel().clearSelection();
    }

    public static class CollapseAction extends ActionMenuItem{

        public CollapseAction() {
            super(GeotkFX.getString(FXUserStyle.class, "collapse"),null);
        }

        @Override
        protected void handle(ActionEvent event) {
            super.handle(event);
            for(TreeItem ti : items){
                ti.setExpanded(false);
            }
        }
    }

    public static class ExpandAction extends ActionMenuItem{

        public ExpandAction() {
            super(GeotkFX.getString(FXUserStyle.class, "expand"),null);
        }

        @Override
        protected void handle(ActionEvent event) {
            for(TreeItem ti : items){
                ti.setExpanded(true);
            }
        }
    }

    public static class NewFTSAction extends ActionMenuItem{

        public NewFTSAction() {
            super(GeotkFX.getString(FXUserStyle.class, "newfts"),GeotkFX.ICON_NEW);
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

    public static class NewRuleAction extends ActionMenuItem{

        public NewRuleAction() {
            super(GeotkFX.getString(FXUserStyle.class, "newrule"),GeotkFX.ICON_NEW);
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

    public static class NewSymbolizerAction extends ActionMenuItem{
        private final FXStyleElementController editor;

        public NewSymbolizerAction(final FXStyleElementController editor) {
            super(getSymbolizerName(editor.getEditedClass().getSimpleName()),GeotkFX.ICON_NEW);
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

    public static class DuplicateAction extends ActionMenuItem {

        public DuplicateAction() {
            super(GeotkFX.getString(FXUserStyle.class, "duplicate"), GeotkFX.ICON_DUPLICATE);
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

    public static class DeleteAction extends ActionMenuItem {

        public DeleteAction() {
            super(GeotkFX.getString(FXUserStyle.class, "delete"),GeotkFX.ICON_DELETE);
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

    public static class NameColumn extends TreeTableColumn<Object,Object>{

        public NameColumn() {
            setCellValueFactory(new Callback<CellDataFeatures<Object, Object>, ObservableValue<Object>>() {
                @Override
                public ObservableValue<Object> call(CellDataFeatures<Object, Object> param) {
                    return new SimpleObjectProperty<>(param.getValue());
                }
            });
            setCellFactory((TreeTableColumn<Object, Object> param) -> new SymbolizerCell());
            setPrefWidth(200);
            setMinWidth(120);
        }

    }

    private static class SymbolizerCell extends TreeTableCell<Object, Object>{

        private final ImageView glyphView = new ImageView();

        public SymbolizerCell() {
        }

        @Override
        protected void updateItem(Object obj, boolean empty) {
            super.updateItem(obj, empty);
            textProperty().unbind();
            setText("");
            setGraphic(null);
            if(obj instanceof TreeItem) obj = ((TreeItem)obj).getValue();
            if(empty || obj==null) return;

            if(obj instanceof Style){
                ObservableStringValue beanProperty = (ObservableStringValue)FXUtilities.beanProperty(obj, "name", String.class);
                textProperty().bind(placeholderBinding(beanProperty, GeotkFX.getString(FXStyleTree.class, "defaultStyleName")));
            }else if(obj instanceof FeatureTypeStyle){
                ObservableStringValue beanProperty = (ObservableStringValue)FXUtilities.beanProperty(obj, "name", String.class);
                textProperty().bind(placeholderBinding(beanProperty, GeotkFX.getString(FXStyleTree.class, "defaultFTSName")));
            }else if(obj instanceof Rule){
                ObservableStringValue beanProperty = (ObservableStringValue)FXUtilities.beanProperty(obj, "name", String.class);
                textProperty().bind(placeholderBinding(beanProperty, GeotkFX.getString(FXStyleTree.class, "defaultRuleName")));
            }else if(obj instanceof Symbolizer){
                final Symbolizer symb = (Symbolizer) obj;
                final Dimension dim = DefaultGlyphService.glyphPreferredSize(symb, null, null);
                final BufferedImage imge = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
                DefaultGlyphService.render(symb, new Rectangle(dim),imge.createGraphics(),null);
                glyphView.setImage(SwingFXUtils.toFXImage(imge, null));
                setGraphic(glyphView);
                setText(symb.getName());
            }
        }

    }

    public static class ShowClassifRangeAction extends ActionMenuItem {

        private MapLayer mapLayer;

        public ShowClassifRangeAction() {
            super(GeotkFX.getString(FXStyleClassifRangePane.class,"title"), GeotkFX.ICON_DUPLICATE);
        }

        public MapLayer getMapLayer() {
            return mapLayer;
        }

        public void setMapLayer(MapLayer mapLayer) {
            this.mapLayer = mapLayer;
        }
        
        @Override
        public MenuItem init(List<? extends TreeItem> selectedItems) {
            super.init(selectedItems);
            if(!(mapLayer instanceof FeatureMapLayer)) return null;
            return uniqueAndType(selectedItems, MutableFeatureTypeStyle.class) ? menuItem : null;
        }

        @Override
        protected void handle(ActionEvent event) {

            for(TreeItem ti : items){
                if(ti.getParent()==null) continue;

                final Object child = ti.getValue();
                if (child instanceof MutableFeatureTypeStyle) {
                    final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle)child;
                    final FXStyleClassifRangePane stylePane = new FXStyleClassifRangePane();
                    stylePane.init(mapLayer, fts);

                    final DialogPane pane = new DialogPane();
                    pane.setContent(stylePane);
                    pane.getButtonTypes().addAll(ButtonType.CLOSE);

                    final Dialog dialog = new Dialog();
                    dialog.setTitle(stylePane.getTitle());
                    dialog.initModality(Modality.WINDOW_MODAL);
                    dialog.setResizable(true);
                    dialog.setDialogPane(pane);
                    dialog.resultProperty().addListener(new ChangeListener() {
                        @Override
                        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                            dialog.close();
                        }
                    });
                    dialog.show();
                }
            }
            hackClearSelection();
        }
    }

    public static class ShowClassifSingleAction extends ActionMenuItem {

        private MapLayer mapLayer;

        public ShowClassifSingleAction() {
            super(GeotkFX.getString(FXStyleClassifSinglePane.class,"title"), GeotkFX.ICON_DUPLICATE);
        }

        public MapLayer getMapLayer() {
            return mapLayer;
        }

        public void setMapLayer(MapLayer mapLayer) {
            this.mapLayer = mapLayer;
        }

        @Override
        public MenuItem init(List<? extends TreeItem> selectedItems) {
            super.init(selectedItems);
            if(!(mapLayer instanceof FeatureMapLayer)) return null;
            return uniqueAndType(selectedItems, MutableFeatureTypeStyle.class) ? menuItem : null;
        }

        @Override
        protected void handle(ActionEvent event) {

            for(TreeItem ti : items){
                if(ti.getParent()==null) continue;

                final Object child = ti.getValue();
                if (child instanceof MutableFeatureTypeStyle) {
                    final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle)child;
                    final FXStyleClassifSinglePane stylePane = new FXStyleClassifSinglePane();
                    stylePane.init(mapLayer, fts);

                    final DialogPane pane = new DialogPane();
                    pane.setContent(stylePane);
                    pane.getButtonTypes().addAll(ButtonType.CLOSE);

                    final Dialog dialog = new Dialog();
                    dialog.setTitle(stylePane.getTitle());
                    dialog.initModality(Modality.WINDOW_MODAL);
                    dialog.setResizable(true);
                    dialog.setDialogPane(pane);
                    dialog.resultProperty().addListener(new ChangeListener() {
                        @Override
                        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                            dialog.close();
                        }
                    });
                    dialog.show();
                }
            }
            hackClearSelection();
        }
    }

    private static ObservableValue<String> placeholderBinding(ObservableStringValue base, String placeholder){
        return when(or(equal(base,""),isNull(base))).then(placeholder).otherwise(base);
    }

    private static String getSymbolizerName(String name){
        String str = GeotkFX.getString(FXUserStyle.class, name);
        if(str.startsWith("Missing")){
            str = name.replace("Symbolizer", "");
        }
        return str;
    }

}
