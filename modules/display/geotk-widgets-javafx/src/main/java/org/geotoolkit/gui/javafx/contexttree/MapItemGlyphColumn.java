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
import java.util.EventObject;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.geotoolkit.display2d.service.DefaultGlyphService;
import org.geotoolkit.gui.javafx.layer.style.FXStyleAggregatedPane;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.LayerListener;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
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

    protected void openEditor(Node owner,MapLayer candidate) {
        final Pane panel = createEditor(candidate);
        panel.setPrefSize(900, 700);

        final Stage dialog = new Stage();
        dialog.initModality(Modality.NONE);
        dialog.initOwner(owner.getScene().getWindow());
        dialog.setTitle(GeotkFX.getString("org.geotoolkit.gui.javafx.contexttree.MapItemGlyphColumn.dialogTitle") + candidate.getName());
        dialog.setResizable(true);

        final Button cancelBtn = new Button(GeotkFX.getString("org.geotoolkit.gui.javafx.contexttree.menu.LayerPropertiesItem.close"));
        cancelBtn.setCancelButton(true);

        final ButtonBar bbar = new ButtonBar();
        bbar.setPadding(new Insets(5, 5, 5, 5));
        bbar.getButtons().addAll(cancelBtn);

        final BorderPane dialogContent = new BorderPane();
        dialogContent.setCenter(panel);
        dialogContent.setBottom(bbar);
        dialog.setScene(new Scene(dialogContent));
        dialog.getScene().getStylesheets().setAll(owner.getScene().getStylesheets());

        cancelBtn.setOnAction((ActionEvent e) -> {
            dialog.close();
        });

        dialog.show();
    }

    private class MapItemGlyphTableCell extends TreeTableCell implements LayerListener{

        private final LayerListener.Weak listener = new LayerListener.Weak(this);
        private MapLayer mapLayer;
        /** Image view contained in the cell. */
        private final ImageView cellContent = new ImageView();

        public MapItemGlyphTableCell() {
            setOnMouseClicked(this::mouseClick);
            setTooltip(new Tooltip(GeotkFX.getString(MapItemGlyphColumn.class, "tooltip")));
        }

        private void mouseClick(MouseEvent event){
            event.consume();
            Object candidate = getItem();
            if (candidate instanceof MapLayer) {
                openEditor(MapItemGlyphTableCell.this,(MapLayer)candidate);
            }
        }

        @Override
        protected void updateItem(Object mapItem, boolean empty) {
            super.updateItem(mapItem, empty);
            mapLayer = null;
            listener.dispose();
            setGraphic(null);

            if (!empty && mapItem instanceof MapLayer) {
                mapLayer = (MapLayer) mapItem;
                listener.registerSource(mapLayer);
                cellContent.setImage(createGlyph(mapLayer));
                setGraphic(cellContent);
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if(MapLayer.STYLE_PROPERTY.equals(evt.getPropertyName())){
                cellContent.setImage(createGlyph(mapLayer));
            }
        }

        @Override
        public void styleChange(MapLayer source, EventObject event) {
            cellContent.setImage(createGlyph(mapLayer));
        }

        @Override
        public void itemChange(CollectionChangeEvent<MapItem> event) {}

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
