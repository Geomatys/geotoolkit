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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.util.function.Function;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.gui.javafx.contexttree.menu.LayerPropertiesItem;
import org.geotoolkit.gui.javafx.layer.FXLayerStylesPane;
import org.geotoolkit.gui.javafx.layer.FXPropertiesPane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleAdvancedPane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleClassifRangePane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleClassifSinglePane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleColorMapPane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleSimplePane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleXMLPane;
import org.geotoolkit.gui.javafx.util.ButtonTreeTableCell;
import org.geotoolkit.gui.javafx.util.FXDialog;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.StyleListener;
import org.geotoolkit.util.collection.CollectionChangeEvent;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapItemGlyphColumn extends TreeTableColumn<MapItem, MapItem>{

    public MapItemGlyphColumn() {                
        setCellValueFactory(param -> ((CellDataFeatures)param).getValue().valueProperty());     
        setCellFactory(new Callback<TreeTableColumn<MapItem, MapItem>, TreeTableCell<MapItem, MapItem>>() {

            @Override
            public TreeTableCell<MapItem, MapItem> call(TreeTableColumn<MapItem, MapItem> p) {
                return new GlyphButton();
            }
        });
        setEditable(true);
        setPrefWidth(34);
        setMinWidth(34);
        setMaxWidth(34);
    }
    
    private static class GlyphButton extends ButtonTreeTableCell<MapItem, MapItem>{

        public GlyphButton() {
            super(false, null, new Function<MapItem, Boolean>() {

                @Override
                public Boolean apply(MapItem t) {
                    return t instanceof MapLayer;
                }
            },
                  (MapItem t) -> {openEditor(t);return t;});
        }
        
        @Override
        protected void updateItem(MapItem mapItem, boolean empty) {
            super.updateItem(mapItem, empty);
            if(mapItem instanceof MapLayer){
                
                ((MapLayer)mapItem).getStyle().addListener(new StyleListener() {

                    @Override
                    public void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> event) {
                        updateGlyph((MapLayer)mapItem);
                    }

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                    }
                });
                updateGlyph((MapLayer) mapItem);
            }
        }
        
        private void updateGlyph(final MapLayer mapItem){
            final BufferedImage img = new BufferedImage(24, 22, BufferedImage.TYPE_INT_ARGB);
            DefaultGlyphService.render(mapItem.getStyle(), 
                    new Rectangle(24, 22), img.createGraphics(), mapItem);
            button.setGraphic(new ImageView(SwingFXUtils.toFXImage(img, null)));
        }
        
        private static void openEditor(MapItem candidate){
            final FXPropertiesPane panel = new FXPropertiesPane(
                    candidate,
                    new FXLayerStylesPane(
                            new FXStyleSimplePane(),
                            new FXStyleColorMapPane(),
                            new FXStyleClassifSinglePane(),
                            new FXStyleClassifRangePane(),
                            new FXStyleAdvancedPane(),
                            new FXStyleXMLPane()
                    )
            );

            final FXDialog dialog = new FXDialog();
            dialog.setContent(panel);
            dialog.getActions().add(new LayerPropertiesItem.CloseAction(dialog));
            dialog.setModal(false);
            dialog.setVisible(null,true);
        }
        
    }
}
