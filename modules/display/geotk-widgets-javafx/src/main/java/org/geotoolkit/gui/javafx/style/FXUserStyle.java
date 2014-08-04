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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.style.FeatureTypeStyleListener;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.RuleListener;
import org.geotoolkit.style.StyleListener;
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
    
    @FXML
    protected TreeTableView tree;

    @FXML
    protected BorderPane contentPane;

    @Override
    protected void updateEditor(MutableStyle styleElement) {
        super.updateEditor(styleElement);
        
        tree.setRoot(new StyleTreeItem(styleElement));
        //this will cause the column width to fit the view area
        tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        
        final TreeTableColumn<StyleTreeItem, String> col = new TreeTableColumn<>();
        col.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<StyleTreeItem, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<StyleTreeItem, String> param) {
                final Object obj = param.getValue().getValue().getValue();
                if(obj instanceof Style){
                    return FXUtilities.beanProperty(obj, "name", String.class);
                }else if(obj instanceof FeatureTypeStyle){
                    return FXUtilities.beanProperty(obj, "name", String.class);
                }else if(obj instanceof Rule){
                    return FXUtilities.beanProperty(obj, "name", String.class);
                }else if(obj instanceof Symbolizer){
                    return FXUtilities.beanProperty(obj, "name", String.class);
                }else{
                    return new SimpleObjectProperty<>("");
                }
            }
        });
        col.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        col.setPrefWidth(200);
        col.setMinWidth(120);
        
        
        tree.getColumns().add(new TreeTableColumn<StyleTreeItem, String>());
                
    }
    
    private static class StyleTreeItem extends TreeItem<Object> implements StyleListener, FeatureTypeStyleListener, RuleListener{

        public StyleTreeItem(Object val) {
            super(val);
            
            if (val instanceof MutableStyle) {
                final MutableStyle style = (MutableStyle) val;
                style.addListener(this);
                setGraphic(new ImageView(Commons.ICON_STYLE));
            } else if (val instanceof MutableFeatureTypeStyle) {
                final MutableFeatureTypeStyle fts = (MutableFeatureTypeStyle) val;
                fts.addListener(this);
                setGraphic(new ImageView(Commons.ICON_FTS));
            } else if (val instanceof MutableRule) {
                final MutableRule r = (MutableRule) val;
                r.addListener(this);
                setGraphic(new ImageView(Commons.ICON_RULE));
            } else if (val instanceof Symbolizer) {
                final Symbolizer symb = (Symbolizer) val;
                final Dimension dim = DefaultGlyphService.glyphPreferredSize(symb, null, null);
                final BufferedImage img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
                DefaultGlyphService.render(symb, new Rectangle(dim),img.createGraphics(),null);
                setGraphic(new ImageView(SwingFXUtils.toFXImage(img, null)));
            }
            
            updateChildren(CollectionChangeEvent.ITEM_ADDED);
        }

        private void updateChildren(int type){
            if(type != CollectionChangeEvent.ITEM_ADDED && type != CollectionChangeEvent.ITEM_REMOVED) return;

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
            
            //reverse order
            for(int i=itemChildren.size()-1;i>=0;i--){
                final Object child = itemChildren.get(i);
                StyleTreeItem tmi = cache.get(child);
                if(tmi==null) tmi = new StyleTreeItem(child);
                getChildren().add(tmi);
            }
        }
        
        @Override
        public void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> event) {
            updateChildren(event.getType());
        }

        @Override
        public void ruleChange(CollectionChangeEvent<MutableRule> event) {
            updateChildren(event.getType());
        }

        @Override
        public void symbolizerChange(CollectionChangeEvent<Symbolizer> event) {
            updateChildren(event.getType());
        }
        
        @Override
        public void featureTypeNameChange(CollectionChangeEvent<GenericName> event) {}

        @Override
        public void semanticTypeChange(CollectionChangeEvent<SemanticType> event) {}
        
        @Override
        public void propertyChange(PropertyChangeEvent evt) {}

    }
    
}
