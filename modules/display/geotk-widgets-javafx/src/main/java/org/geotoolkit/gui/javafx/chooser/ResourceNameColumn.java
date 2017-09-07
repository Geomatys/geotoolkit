/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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

package org.geotoolkit.gui.javafx.chooser;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Resource;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.metadata.MetadataUtilities;
import org.geotoolkit.storage.coverage.CoverageResource;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ResourceNameColumn extends TreeTableColumn<Resource,String>{

    private static final Image ICON_RASTER = new Image("/org/geotoolkit/gui/javafx/icon/raster-icon.png");
    private static final Image ICON_VECTOR = new Image("/org/geotoolkit/gui/javafx/icon/vector-icon.png");
    private static final Image ICON_SERVICE = new Image("/org/geotoolkit/gui/javafx/icon/service-icon.png");
    private static final Image ICON_SENSOR = new Image("/org/geotoolkit/gui/javafx/icon/sensor-icon.png");
    private static final Image ICON_FOLDER = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_FOLDER_O,16,Color.GRAY),null);
    private static final Image ICON_STORE = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_ARCHIVE,16,Color.GRAY),null);

    public ResourceNameColumn() {
        super("Name");
        setCellValueFactory(new Callback<CellDataFeatures<Resource, String>, javafx.beans.value.ObservableValue<java.lang.String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<Resource, String> param) {
                try {
                    return new SimpleObjectProperty<>(MetadataUtilities.getIdentifier(param.getValue().getValue().getMetadata()));
                } catch (DataStoreException ex) {
                   return new SimpleObjectProperty<>(ex.getMessage());
                }
            }
        });
        setCellFactory((TreeTableColumn<Resource, String> param) -> new Cell());
        setEditable(true);
        setPrefWidth(200);
        setMinWidth(120);
    }

    public static class Cell extends TreeTableCell<Resource,String> {

        public Cell(){
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(item);
            setGraphic(null);
            setContentDisplay(ContentDisplay.LEFT);
            setAlignment(Pos.CENTER_LEFT);
            setTextAlignment(TextAlignment.LEFT);
            setWrapText(false);
            if(empty) return;

            final TreeTableRow<Resource> row = getTreeTableRow();
            if(row==null) return;
            final TreeItem<Resource> ti = row.getTreeItem();
            if(ti==null) return;

            final Resource resource = ti.getValue();
            setGraphic(new ImageView(getTypeIcon(resource)));
        }

    }

    private static Image getTypeIcon(Resource resource){
        if (resource instanceof FeatureSet) {
            return ICON_VECTOR;
        } else if(resource instanceof CoverageResource) {
            return ICON_RASTER;
        } else if(resource instanceof DataStore) {
            return ICON_STORE;
        } else if(resource instanceof Aggregate) {
            return ICON_FOLDER;
        } else {
            //unknown
            return ICON_SERVICE;
        }
    }

}
