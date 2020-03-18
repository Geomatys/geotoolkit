/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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

package org.geotoolkit.gui.javafx.layer;

import java.util.Collection;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociation;
import org.opengis.feature.Operation;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FeaturePropertyValueColumn<T> extends TreeTableColumn<T,FXFeatureViewer.FeatureBlock> {

    public FeaturePropertyValueColumn() {
        super("Value");
        setCellFactory(new Callback<TreeTableColumn<T, FXFeatureViewer.FeatureBlock>, TreeTableCell<T, FXFeatureViewer.FeatureBlock>>() {
            @Override
            public TreeTableCell<T, FXFeatureViewer.FeatureBlock> call(TreeTableColumn<T, FXFeatureViewer.FeatureBlock> param) {
                Cell cell = new Cell();
                return cell;
            }
        });
        setCellValueFactory(new Callback<CellDataFeatures<T, FXFeatureViewer.FeatureBlock>, ObservableValue<FXFeatureViewer.FeatureBlock>>() {
            @Override
            public ObservableValue<FXFeatureViewer.FeatureBlock> call(CellDataFeatures<T, FXFeatureViewer.FeatureBlock> param) {
                return new SimpleObjectProperty<>((FXFeatureViewer.FeatureBlock) param.getValue().getValue());
            }
        });
        setEditable(true);
        setPrefWidth(200);
        setMinWidth(120);
    }

    public static class Cell<T> extends TreeTableCell<T,FXFeatureViewer.FeatureBlock> {

        public Cell(){
        }

        @Override
        public void updateItem(FXFeatureViewer.FeatureBlock item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            setContentDisplay(ContentDisplay.LEFT);
            setAlignment(Pos.CENTER_LEFT);
            setTextAlignment(TextAlignment.LEFT);
            setWrapText(false);
            setBackground(Background.EMPTY);
            if (empty || item == null) {
                setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
                return;
            }


            if (item.propertyType instanceof Operation) {
                //setBackground(new Background(new BackgroundFill(Color.BLANCHEDALMOND, CornerRadii.EMPTY, Insets.EMPTY)));
            } else if (item.property instanceof FeatureAssociation) {
                //setBackground(new Background(new BackgroundFill(Color.POWDERBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            }

            if (item.value == null || item.value instanceof Feature || item.value instanceof Collection) {
                return;
            }

            String str = String.valueOf(item.value);
            if(str.isEmpty()) str = " ";
            setText(str);
        }

    }

}
