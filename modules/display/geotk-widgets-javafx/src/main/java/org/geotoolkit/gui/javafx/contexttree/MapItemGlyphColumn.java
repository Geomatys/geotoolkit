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
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.gui.javafx.layer.FXPropertiesPane;
import org.geotoolkit.gui.javafx.layer.style.FXStyleAggregatedPane;
import org.geotoolkit.gui.javafx.util.ButtonTreeTableCell;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.StyleListener;
import org.geotoolkit.util.collection.CollectionChangeEvent;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public class MapItemGlyphColumn extends TreeTableColumn {

    public MapItemGlyphColumn() {

        setCellValueFactory(new Callback<CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(CellDataFeatures cellData) {
                return cellData.getValue().valueProperty();
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

    protected Pane createEditor(MapLayer candidate) {
        FXStyleAggregatedPane pane = new FXStyleAggregatedPane();
        pane.init(candidate);
        return pane;
    }

    private void openEditor(MapLayer candidate) {
        final Pane panel = createEditor(candidate);
        panel.setPrefSize(900, 700);

        final Stage dialog = new Stage();
        dialog.setTitle(GeotkFX.getString("org.geotoolkit.gui.javafx.contexttree.MapItemGlyphColumn.dialogTitle") + candidate.getName());
        dialog.setResizable(true);
        dialog.initModality(Modality.NONE);
        dialog.initOwner(null);

        final Button cancelBtn = new Button(GeotkFX.getString("org.geotoolkit.gui.javafx.contexttree.menu.LayerPropertiesItem.close"));
        cancelBtn.setCancelButton(true);

        final ButtonBar bbar = new ButtonBar();
        bbar.setPadding(new Insets(5, 5, 5, 5));
        bbar.getButtons().addAll(cancelBtn);

        final BorderPane dialogContent = new BorderPane();
        dialogContent.setCenter(panel);
        dialogContent.setBottom(bbar);
        dialog.setScene(new Scene(dialogContent));

        cancelBtn.setOnAction((ActionEvent e) -> {
            dialog.close();
        });

        dialog.show();
    }

    private class MapItemGlyphTableCell extends ButtonTreeTableCell<Object, Object> implements StyleListener{

        private final StyleListener.Weak listener = new StyleListener.Weak(this);
        private MapLayer mapLayer;
        /** Image view contained in the cell. */
        private final ImageView cellContent = new ImageView();

        public MapItemGlyphTableCell() {
            super(false, null,
                    new Function<Object, Boolean>() {
                        public Boolean apply(Object mapItem) {
                            return (mapItem instanceof MapLayer);
                        }
                    },
                    new Function<Object, Object>() {
                        public Object apply(Object candidate) {
                            if (candidate instanceof MapLayer) {
                                openEditor((MapLayer)candidate);
                            }
                            return candidate;
                        }
                    });

            button.setTooltip(new Tooltip(GeotkFX.getString(MapItemGlyphColumn.class, "tooltip")));
        }
        
        @Override
        protected void updateItem(Object mapItem, boolean empty) {
            super.updateItem(mapItem, empty);
            mapLayer = null;
            button.setGraphic(null);
            listener.dispose();

            if (!empty && mapItem instanceof MapLayer) {
                mapLayer = (MapLayer) mapItem;
                listener.registerSource(mapLayer.getStyle());
                cellContent.setImage(createGlyph(mapLayer));
                button.setGraphic(cellContent);
            }
        }      

        @Override
        public void featureTypeStyleChange(CollectionChangeEvent<MutableFeatureTypeStyle> event) {
            cellContent.setImage(createGlyph(mapLayer));
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {}
        
    }

    private static Image createGlyph(final MapLayer mapLayer) {
        if (mapLayer != null) {
            final BufferedImage glyph = new BufferedImage(24, 16, BufferedImage.TYPE_INT_ARGB);
            DefaultGlyphService.render(mapLayer.getStyle(),
                    new Rectangle(24, 16), glyph.createGraphics(), mapLayer);
            return SwingFXUtils.toFXImage(glyph, null);
        }
        return null;
    }
}
