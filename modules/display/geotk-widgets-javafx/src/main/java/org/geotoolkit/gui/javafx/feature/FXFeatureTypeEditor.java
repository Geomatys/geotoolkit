/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.feature;

import com.vividsolutions.jts.geom.Geometry;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import org.apache.sis.feature.DefaultAssociationRole;
import org.apache.sis.feature.SingleAttributeTypeBuilder;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.gui.javafx.crs.FXCRSButton;
import org.geotoolkit.internal.GeotkFX;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Display and edit a feature type.
 *
 * Limitations :
 * - only feature type geometry crs can be modified.
 *
 * TODO : make a full editor
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXFeatureTypeEditor extends BorderPane{

    private final ObjectProperty<FeatureType> typeProperty = new SimpleObjectProperty<>();

    private final TreeTableView tree = new TreeTableView();

    public FXFeatureTypeEditor(FeatureType type) {
        tree.getStylesheets().add("org/geotoolkit/gui/javafx/parameter/parameters.css");
        tree.setShowRoot(false);
        tree.getColumns().add(new PropertyNameColumn());
        tree.getColumns().add(new PropertyTypeColumn());
        tree.getColumns().add(new PropertyMinOccColumn());
        tree.getColumns().add(new PropertyMaxOccColumn());
        tree.getColumns().add(new PropertyCrsColumn());
        tree.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        typeProperty.addListener((ObservableValue<? extends FeatureType> observable, FeatureType oldValue, FeatureType newValue) -> {
            update(newValue);
        });

        setFeatureType(type);
        setCenter(tree);
    }

    public BooleanProperty editableProperty(){
        return tree.editableProperty();
    }

    public FeatureType getFeatureType(){
        return typeProperty.get();
    }

    public void setFeatureType(FeatureType type){
        typeProperty.set(type);
    }

    public ObjectProperty<FeatureType> featureTypeProperty(){
        return typeProperty;
    }

    private void update(FeatureType type){

        final FeatureTypeBuilder builder = new FeatureTypeBuilder(type);
        final FTNode item = new FTNode(builder);
        tree.setRoot(item);
    }

    private void changed(){
        final FTNode node = (FTNode) tree.getRoot();
        typeProperty.set(node.build());
    }

    private class PropertyNameColumn extends TreeTableColumn<SingleAttributeTypeBuilder,String>{

        public PropertyNameColumn() {
            super(GeotkFX.getString(FXFeatureTypeEditor.class, "name"));
            setCellValueFactory((CellDataFeatures<SingleAttributeTypeBuilder, String> param) ->
                    new SimpleObjectProperty<>(param.getValue().getValue().getName().tip().toString()));
            setEditable(false);
        }

    }

    private class PropertyTypeColumn extends TreeTableColumn<SingleAttributeTypeBuilder,String>{

        private PropertyTypeColumn(){
            super(GeotkFX.getString(FXFeatureTypeEditor.class, "type"));
            setCellValueFactory((CellDataFeatures<SingleAttributeTypeBuilder, String> param) ->
                    new SimpleObjectProperty<>(param.getValue().getValue().getValueClass().getSimpleName()));
            setEditable(false);
        }

    }

    private class PropertyMinOccColumn extends TreeTableColumn<SingleAttributeTypeBuilder,Integer>{

        private PropertyMinOccColumn(){
            super(GeotkFX.getString(FXFeatureTypeEditor.class, "minimum"));
            setCellValueFactory((CellDataFeatures<SingleAttributeTypeBuilder, Integer> param) ->
                    new SimpleObjectProperty<>(param.getValue().getValue().getMinimumOccurs()));
            setEditable(false);
        }

    }

    private class PropertyMaxOccColumn extends TreeTableColumn<SingleAttributeTypeBuilder,Integer>{

        private PropertyMaxOccColumn(){
            super(GeotkFX.getString(FXFeatureTypeEditor.class, "maximum"));
            setCellValueFactory((CellDataFeatures<SingleAttributeTypeBuilder, Integer> param) ->
                    new SimpleObjectProperty<>(param.getValue().getValue().getMaximumOccurs()));
            setEditable(false);
        }

    }

    private class PropertyCrsColumn extends TreeTableColumn<SingleAttributeTypeBuilder,SingleAttributeTypeBuilder>{

        private PropertyCrsColumn(){
            super(GeotkFX.getString(FXFeatureTypeEditor.class, "crs"));
            setEditable(true);
            setCellValueFactory((CellDataFeatures<SingleAttributeTypeBuilder, SingleAttributeTypeBuilder> param) -> param.getValue().valueProperty());
            setCellFactory((TreeTableColumn<SingleAttributeTypeBuilder, SingleAttributeTypeBuilder> param) -> new AttCrsCell());
        }

    }

    private class AttCrsCell extends TreeTableCell<SingleAttributeTypeBuilder, SingleAttributeTypeBuilder>{

        private final FXCRSButton button = new FXCRSButton();

        public AttCrsCell() {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            button.crsProperty().addListener(this::changed);
        }

        private void changed(ObservableValue<? extends CoordinateReferenceSystem> observable, CoordinateReferenceSystem oldValue, CoordinateReferenceSystem newValue){
            final SingleAttributeTypeBuilder adb = getItem();
            if(adb==null) return;

            adb.setCRS(newValue);
            FXFeatureTypeEditor.this.changed();
        }

        @Override
        protected void updateItem(SingleAttributeTypeBuilder item, boolean empty) {
            super.updateItem(item, empty);

            setGraphic(null);
            if(item!=null && Geometry.class.isAssignableFrom(item.getValueClass())){
                //TODO getCRS
                //button.crsProperty().set(gt.getCoordinateReferenceSystem());
                setGraphic(button);
            }
        }

    }

    private static class FTNode extends TreeItem{

        private FTNode(FeatureTypeBuilder value){
            super(value);

            final FeatureType type = value.build();
            for(PropertyType pd : type.getProperties(true)){
                if(pd instanceof AttributeType){
                    final SingleAttributeTypeBuilder adb = new SingleAttributeTypeBuilder();
                    adb.copy((AttributeType)pd);
                    getChildren().add(new Node(adb));
                }
            }
        }

        public FeatureType build(){
            final FeatureTypeBuilder builder = (FeatureTypeBuilder) getValue();
            builder.properties().clear();

            for(TreeItem ti : (List<TreeItem>)getChildren()){
                final Node n = (Node) ti;
                builder.addAttribute(n.build());
            }

            return builder.build();
        }

    }

    private static class Node extends TreeItem<SingleAttributeTypeBuilder>{

        private Node(SingleAttributeTypeBuilder value){
            super(value);
        }

        public AttributeType build(){
            final SingleAttributeTypeBuilder adb = getValue();
            return adb.build();
        }

    }

}
