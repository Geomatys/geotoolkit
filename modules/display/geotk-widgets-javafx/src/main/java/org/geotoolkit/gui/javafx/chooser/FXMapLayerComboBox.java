/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.javafx.chooser;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import org.geotoolkit.gui.javafx.contexttree.MapItemNameColumn;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapItem;
import org.geotoolkit.map.MapLayer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXMapLayerComboBox extends ComboBox<MapLayer>{

    private final ObjectProperty<MapContext> mapcontextProperty = new SimpleObjectProperty<>();

    public FXMapLayerComboBox() {
        setEditable(false);
        setCellFactory((ListView<MapLayer> param) -> new MapItemCell());

        mapcontextProperty.addListener(new ChangeListener<MapContext>() {
            @Override
            public void changed(ObservableValue<? extends MapContext> observable, MapContext oldValue, MapContext newValue) {
                setItems(FXCollections.observableArrayList(newValue.layers()));
            }
        });

        setButtonCell(new MapItemCell<>());
    }

    public ObjectProperty<MapContext> getMapContextProperty() {
        return mapcontextProperty;
    }

    public MapContext getMapContext(){
        return mapcontextProperty.get();
    }

    public void setMapContext(MapContext context){
        mapcontextProperty.set(context);
    }

    private class MapItemCell<T extends MapItem> extends ListCell<T> {

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(null);
            textProperty().unbind();
            if(item!=null){
                setGraphic(new ImageView(MapItemNameColumn.getTypeIcon(item)));
                textProperty().bind(FXUtilities.beanProperty(item, "name", String.class));
            }
        }

    }

}
