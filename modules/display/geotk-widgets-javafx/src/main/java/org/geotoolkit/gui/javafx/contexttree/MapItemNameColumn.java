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

import java.awt.Color;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.Description;
import org.opengis.style.Style;

/**
 * Context tree column with name and type icon.
 * For FeatureMapLayer a small red asterik may be displayed when there are uncommited changes.
 *
 * @author Johann Sorel (Geomatys)
 */
public class MapItemNameColumn<T> extends TreeTableColumn<T,String>{

    private static final Image ICON_RASTER = new Image("/org/geotoolkit/gui/javafx/icon/raster-icon.png");
    private static final Image ICON_VECTOR = new Image("/org/geotoolkit/gui/javafx/icon/vector-icon.png");
    private static final Image ICON_SERVICE = new Image("/org/geotoolkit/gui/javafx/icon/service-icon.png");
    private static final Image ICON_SENSOR = new Image("/org/geotoolkit/gui/javafx/icon/sensor-icon.png");
    private static final Image ICON_FOLDER = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_FOLDER,16,Color.GRAY),null);

    public MapItemNameColumn() {
        super(GeotkFX.getString(MapItemNameColumn.class,"layers"));
        setCellValueFactory(new Callback<CellDataFeatures<T, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<T, String> param) {
                if (param instanceof CellDataFeatures) {
                    final TreeItem item = ((CellDataFeatures) param).getValue();
                    final Object value = item.getValue();
                    if (value instanceof MapItem) {
                        return FXUtilities.beanProperty(value, "name", String.class);
                    } else if (value instanceof Style) {
                        final Style style = (Style) value;
                        Description description = style.getDescription();
                        String name = style.getName();

                        String text = null;
                        if (description != null && description.getTitle() != null) {
                            text = description.getTitle().toString();
                        }
                        if (name != null && (text == null || text.isEmpty())) {
                            text = name;
                        }
                        return new SimpleObjectProperty<>(text);
                    } else {
                        throw new IllegalArgumentException("Expected a Map item object, but got "+value == null? "null" : value.getClass().getCanonicalName());
                    }

                }
                return null;
            }
        });
        setCellFactory((TreeTableColumn<T, String> param) -> new Cell());
        setEditable(true);
        setPrefWidth(200);
        setMinWidth(120);
        setMaxWidth(800);
        setSortable(false);
    }

    public static class Cell<T> extends TreeTableCell<T,String> implements ChangeListener<ChangeEvent>{

        private final TextField textField = new TextField();

        public Cell(){
            textField.setMaxWidth(Double.POSITIVE_INFINITY);
            textField.setOnAction((ActionEvent evt) -> commitEdit(textField.getText()));
        }

        @Override
        public void startEdit() {
            super.startEdit();
            setText(null);
            setGraphic(null);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            final BorderPane pane = new BorderPane();
            pane.setLeft(createIcon());
            pane.setCenter(textField);
            textField.textProperty().bindBidirectional(itemProperty());
            setGraphic(pane);
            textField.requestFocus();
        }

        @Override
        public void commitEdit(String newValue) {
            textField.textProperty().unbind();
            super.commitEdit(newValue);
            updateItem(getItem(), isEmpty());
        }

        @Override
        public void cancelEdit() {
            textField.commitValue();
            textField.textProperty().unbind();
            super.cancelEdit();
            updateItem(getItem(), isEmpty());
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            setTooltip(null);
            setContentDisplay(ContentDisplay.LEFT);
            setAlignment(Pos.CENTER_LEFT);
            setTextAlignment(TextAlignment.LEFT);
            setWrapText(false);
            if(empty) return;

            String str = (item==null)? " " : item;
            if(str.isEmpty()) str = " ";
            setText(str);
            final TreeTableRow row = getTreeTableRow();
            if(row==null) return;
            final TreeItem ti = row.getTreeItem();
            if(ti==null) return;

            if (ti instanceof StyleMapItem) {
                final BorderPane pane = new BorderPane(createIcon());
                pane.setMaxSize(BorderPane.USE_COMPUTED_SIZE,Double.MAX_VALUE);
                pane.setPrefSize(BorderPane.USE_COMPUTED_SIZE, BorderPane.USE_COMPUTED_SIZE);
                setGraphic(pane);
            } else if (ti instanceof TreeMapItem) {
                setGraphic(createIcon());

                final Object object = ti.getValue();
                if (object instanceof MapLayer) {
                    Description description = ((MapLayer) object).getDescription();
                    if (description != null && description.getAbstract() != null) {
                        String abs = description.getAbstract().toString();
                        if (abs != null && !abs.isEmpty()) {
                            setTooltip(new Tooltip(abs));
                        }
                    }
                }
            }
        }

        private ImageView createIcon() {
            final TreeTableRow row = getTreeTableRow();
            if (row == null) return null;
            final TreeItem ti = row.getTreeItem();
            if (ti == null) return null;

            if (ti instanceof StyleMapItem) {
                final ImageView view = new ImageView();
                view.imageProperty().bind(((StyleMapItem)ti).imageProperty());
                return view;
            } else if (ti instanceof TreeMapItem) {
                final MapItem mapItem = (MapItem) ((TreeMapItem)ti).getValue();
                return new ImageView(getTypeIcon(mapItem));
            }
            return null;
        }

        @Override
        public void changeOccured(ChangeEvent event) {
            //change the edition asteriks
            if(!isEditing()){
                Platform.runLater(()-> updateItem(getItem(), false));
            }
        }

    }

    public static Image getTypeIcon(MapItem mapItem) {
        if (mapItem instanceof MapLayer) {
            Resource resource = ((MapLayer) mapItem).getResource();
            return getTypeIcon(resource);
        } else {
            //container
            return ICON_FOLDER;
        }
    }

    public static Image getTypeIcon(Resource resource) {
        DataStore store = (resource instanceof StoreResource)? ((StoreResource)resource).getOriginator() : null;

        if (resource instanceof FeatureSet) {
            return ICON_VECTOR;
        } else if (resource instanceof GridCoverageResource) {
            return ICON_RASTER;
        }
        return ICON_FOLDER;
    }

}
