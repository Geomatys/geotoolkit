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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.gui.javafx.layer.FXLayerStylesPane;
import org.geotoolkit.gui.javafx.layer.FXPropertiesPane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleAdvancedPane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleClassifRangePane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleClassifSinglePane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleColorMapPane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleSimplePane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleXMLPane;
import org.geotoolkit.gui.javafx.util.ButtonTreeTableCell;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.StyleListener;
import org.geotoolkit.util.collection.CollectionChangeEvent;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapItemGlyphColumn extends TreeTableColumn{

    public MapItemGlyphColumn() {

        setCellValueFactory(new Callback<CellDataFeatures, ObservableValue>() {

            @Override
            public ObservableValue call(CellDataFeatures cellData) {
                return ((CellDataFeatures) cellData).getValue().valueProperty();
            }
        });

        setCellFactory(new Callback<TreeTableColumn<Object, Object>, TreeTableCell<Object, Object>>() {

            @Override
            public TreeTableCell<Object, Object> call(TreeTableColumn<Object, Object> column) {
                return new MapItemGlyphTableCell();
            }
        });

        setEditable(true);
        setPrefWidth(34);
        setMinWidth(34);
        setMaxWidth(34);
    }

    protected FXPropertiesPane createEditor(MapItem candidate){
        return new FXPropertiesPane(
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
    }

    private void openEditor(Object candidate){
        if(candidate instanceof MapLayer){
            final FXPropertiesPane panel = createEditor((MapLayer)candidate);
            panel.setPrefSize(900, 700);

            final DialogPane pane = new DialogPane();
            pane.setContent(panel);
            pane.getButtonTypes().add(ButtonType.CLOSE);

            final Dialog dialog = new Dialog();
            dialog.initModality(Modality.NONE);
            dialog.setResizable(true);
            dialog.setDialogPane(pane);
            dialog.resultProperty().addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    //TODO add apply revert buttons
                    dialog.close();
                }
            });
            dialog.show();
        }
    }

    private class MapItemGlyphTableCell extends ButtonTreeTableCell<Object, Object> {

        private StyleListener currentStyleListener;
        
        public MapItemGlyphTableCell() {
            super(false, null, new Function<Object, Boolean>() {

                @Override
                public Boolean apply(Object mapItem) {
                    return mapItem instanceof MapLayer;
                }
            },
                    new Function<Object, Object>() {

                        @Override
                        public Object apply(Object candidate) {
                            openEditor(candidate);
                            return candidate;
                        }
                    });
            
            button.setTooltip(new Tooltip(GeotkFX.getString(MapItemGlyphColumn.class, "tooltip")));
        }
        
        @Override
        protected void updateItem(Object mapItem, boolean empty) {
            super.updateItem(mapItem, empty);
            if(mapItem instanceof MapLayer){
                final MapLayer mapLayer = (MapLayer) mapItem;
                
                // Remove old listener.
                if(currentStyleListener!=null){
                    mapLayer.getStyle().removeListener(currentStyleListener);
                }
                
                // Add new listener.
                currentStyleListener = new StyleListener() {

                    @Override
                    public void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> event) {
                        updateGlyph(mapLayer);
                    }

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                    }
                };
                mapLayer.getStyle().addListener(currentStyleListener);
                
                updateGlyph(mapLayer);
            }
        }
        
        private void updateGlyph(final MapLayer mapLayer){
            final BufferedImage img = new BufferedImage(24, 22, BufferedImage.TYPE_INT_ARGB);
            DefaultGlyphService.render(mapLayer.getStyle(), 
                    new Rectangle(24, 22), img.createGraphics(), mapLayer);
            button.setGraphic(new ImageView(SwingFXUtils.toFXImage(img, null)));
        }
        
    }
}
