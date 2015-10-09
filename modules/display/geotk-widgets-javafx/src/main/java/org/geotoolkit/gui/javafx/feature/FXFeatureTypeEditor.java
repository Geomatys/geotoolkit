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
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.DefaultPropertyDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryType;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.feature.type.PropertyType;
import org.geotoolkit.gui.javafx.crs.FXCRSButton;
import org.geotoolkit.internal.GeotkFX;
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

        final PropertyDescriptor desc = new DefaultPropertyDescriptor(type, type.getName(), 1, 1, true);
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        adb.copy(desc);
        final Node item = new Node(adb);
        tree.setRoot(item);
    }

    private void changed(){
        final Node node = (Node) tree.getRoot();
        typeProperty.set((FeatureType)node.build().getType());
    }

    private class PropertyNameColumn extends TreeTableColumn<AttributeDescriptorBuilder,String>{

        public PropertyNameColumn() {
            super(GeotkFX.getString(FXFeatureTypeEditor.class, "name"));
            setCellValueFactory((CellDataFeatures<AttributeDescriptorBuilder, String> param) ->
                    new SimpleObjectProperty<>(param.getValue().getValue().getName().tip().toString()));
            setEditable(false);
        }

    }

    private class PropertyTypeColumn extends TreeTableColumn<AttributeDescriptorBuilder,String>{

        private PropertyTypeColumn(){
            super(GeotkFX.getString(FXFeatureTypeEditor.class, "type"));
            setCellValueFactory((CellDataFeatures<AttributeDescriptorBuilder, String> param) ->
                    new SimpleObjectProperty<>(param.getValue().getValue().getType().getBinding().getSimpleName()));
            setEditable(false);
        }

    }

    private class PropertyMinOccColumn extends TreeTableColumn<AttributeDescriptorBuilder,Integer>{

        private PropertyMinOccColumn(){
            super(GeotkFX.getString(FXFeatureTypeEditor.class, "minimum"));
            setCellValueFactory((CellDataFeatures<AttributeDescriptorBuilder, Integer> param) ->
                    new SimpleObjectProperty<>(param.getValue().getValue().getMinOccurs()));
            setEditable(false);
        }

    }

    private class PropertyMaxOccColumn extends TreeTableColumn<AttributeDescriptorBuilder,Integer>{

        private PropertyMaxOccColumn(){
            super(GeotkFX.getString(FXFeatureTypeEditor.class, "maximum"));
            setCellValueFactory((CellDataFeatures<AttributeDescriptorBuilder, Integer> param) ->
                    new SimpleObjectProperty<>(param.getValue().getValue().getMaxOccurs()));
            setEditable(false);
        }

    }

    private class PropertyCrsColumn extends TreeTableColumn<AttributeDescriptorBuilder,AttributeDescriptorBuilder>{

        private PropertyCrsColumn(){
            super(GeotkFX.getString(FXFeatureTypeEditor.class, "crs"));
            setEditable(true);
            setCellValueFactory((CellDataFeatures<AttributeDescriptorBuilder, AttributeDescriptorBuilder> param) -> param.getValue().valueProperty());
            setCellFactory((TreeTableColumn<AttributeDescriptorBuilder, AttributeDescriptorBuilder> param) -> new AttCrsCell());
        }

    }

    private class AttCrsCell extends TreeTableCell<AttributeDescriptorBuilder, AttributeDescriptorBuilder>{

        private final FXCRSButton button = new FXCRSButton();

        public AttCrsCell() {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            button.crsProperty().addListener(this::changed);
        }

        private void changed(ObservableValue<? extends CoordinateReferenceSystem> observable, CoordinateReferenceSystem oldValue, CoordinateReferenceSystem newValue){
            final AttributeDescriptorBuilder adb = getItem();
            if(adb==null) return;

            final GeometryType gt = (GeometryType) adb.getType();
            if(Objects.equals(gt.getCoordinateReferenceSystem(),newValue)) return;

            final AttributeTypeBuilder atb = new AttributeTypeBuilder();
            atb.copy(gt);
            atb.setCRS(newValue);
            adb.setType(atb.buildGeometryType());
            FXFeatureTypeEditor.this.changed();
        }

        @Override
        protected void updateItem(AttributeDescriptorBuilder item, boolean empty) {
            super.updateItem(item, empty);

            setGraphic(null);
            if(item!=null && item.getType() instanceof GeometryType){
                final GeometryType gt = (GeometryType) item.getType();
                button.crsProperty().set(gt.getCoordinateReferenceSystem());
                setGraphic(button);
            }
        }

    }

    private static class Node extends TreeItem<AttributeDescriptorBuilder>{

        private Node(AttributeDescriptorBuilder value){
            super(value);

            final PropertyType type = value.getType();
            if(type instanceof ComplexType){
                final ComplexType ct = (ComplexType) type;

                for(PropertyDescriptor pd : ct.getDescriptors()){
                    final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
                    adb.copy(pd);
                    getChildren().add(new Node(adb));
                }
            }
        }

        public PropertyDescriptor build(){
            final AttributeDescriptorBuilder adb = getValue();

            final PropertyType type = adb.getType();
            if(type instanceof ComplexType){
                final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
                ftb.copy((ComplexType) type);
                ftb.getProperties().clear();

                for(TreeItem ti : getChildren()){
                    final Node n = (Node) ti;
                    ftb.getProperties().add(n.build());
                }

                adb.setType(ftb.buildFeatureType());
            }
            return adb.buildDescriptor();
        }

    }

}
