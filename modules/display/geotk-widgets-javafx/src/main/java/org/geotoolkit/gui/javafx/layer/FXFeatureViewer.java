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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXFeatureViewer extends BorderPane {

    private final SimpleObjectProperty<Feature> featureProp = new SimpleObjectProperty<Feature>();
    private final SimpleBooleanProperty operationVisible = new SimpleBooleanProperty();
    private final SimpleBooleanProperty nullVisible = new SimpleBooleanProperty();
    private final SimpleBooleanProperty fullNameVisible = new SimpleBooleanProperty();
    private final TreeTableView<FeatureBlock> treetable = new TreeTableView();

    public FXFeatureViewer() {
        setCenter(treetable);
        treetable.getColumns().add(new FeaturePropertyNameColumn());
        treetable.getColumns().add(new FeaturePropertyValueColumn());
        treetable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        operationVisible.set(false);
        fullNameVisible.set(false);
        nullVisible.set(false);

        featureProp.addListener((ObservableValue<? extends Feature> observable, Feature oldValue, Feature newValue) -> {
            updateTree(newValue);
        });
        nullVisible.addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            updateTree(featureProp.getValue());
        });
        operationVisible.addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            updateTree(featureProp.getValue());
        });
        fullNameVisible.addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            updateTree(featureProp.getValue());
        });
    }

    public Feature getFeature() {
        return featureProp.getValue();
    }

    public void setFeature(Feature feature) {
        featureProp.setValue(feature);
    }

    public Property<Feature> featureProperty() {
        return featureProp;
    }

    public boolean isOperationVisible() {
        return operationVisible.getValue();
    }

    public void setOperationVisible(boolean visible) {
        operationVisible.setValue(visible);
    }

    public BooleanProperty operationVisibleProperty() {
        return operationVisible;
    }

    public boolean isNullVisible() {
        return nullVisible.getValue();
    }

    public void setNullVisible(boolean visible) {
        nullVisible.setValue(visible);
    }

    public BooleanProperty nullVisibleProperty() {
        return nullVisible;
    }

    public boolean isFullNameVisible() {
        return fullNameVisible.getValue();
    }

    public void setFullNameVisible(boolean visible) {
        fullNameVisible.setValue(visible);
    }

    public BooleanProperty fullNameVisibleProperty() {
        return fullNameVisible;
    }

    private void updateTree(Feature feature) {
        boolean showOp = isOperationVisible();
        boolean showNull = isNullVisible();
        if (feature == null) {
            treetable.setRoot(null);
        } else {
            TreeItem<FeatureBlock> root = buildTree(feature, showOp, showNull, new IdentityHashMap<>());
            root.setExpanded(true);
            treetable.setRoot(root);
        }

    }

    private TreeItem<FeatureBlock> buildTree(Feature feature, boolean showOp, boolean showNull, Map<Feature,TreeItem<FeatureBlock>> visited) {

        TreeItem<FeatureBlock> cache = visited.get(feature);
        if (cache != null) {
            final TreeItem<FeatureBlock> item = new TreeItem<>();
            item.setValue(new FeatureBlock(feature, null, null, -1, null));
            return item;
        }

        final TreeItem<FeatureBlock> item = new TreeItem<>();
        item.setValue(new FeatureBlock(feature, null, null, -1, null));

        visited.put(feature, item);

        final FeatureType type = feature.getType();

        List<PropertyType> properties = new ArrayList(type.getProperties(true));
        properties.sort(new Comparator<PropertyType>() {
            @Override
            public int compare(PropertyType o1, PropertyType o2) {
                if (o1 instanceof Operation) {
                    if (o2 instanceof Operation) {
                        return o1.getName().compareTo(o2.getName());
                    } else if (o2 instanceof FeatureAssociationRole) {
                        return -1;
                    } else {
                        return -1;
                    }
                } else if (o1 instanceof FeatureAssociationRole) {
                    if (o2 instanceof Operation) {
                        return +1;
                    } else if (o2 instanceof FeatureAssociationRole) {
                        return o1.getName().compareTo(o2.getName());
                    } else {
                        return -1;
                    }
                } else {
                    if (o2 instanceof Operation) {
                        return +1;
                    } else if (o2 instanceof FeatureAssociationRole) {
                        return +1;
                    } else {
                        return o1.getName().compareTo(o2.getName());
                    }
                }
            }
        });

        for (PropertyType pt : properties) {

            if (!showOp && pt instanceof Operation) continue;

            org.opengis.feature.Property property = feature.getProperty(pt.getName().toString());
            Object value = feature.getPropertyValue(pt.getName().toString());

            if (pt instanceof FeatureAssociationRole) {
                if (value instanceof Collection) {
                    Collection col = (Collection) value;
                    if (showNull || (!showNull && !col.isEmpty())) {
                        final Iterator iterator = col.iterator();
                        TreeItem<FeatureBlock> ti = new TreeItem<>(new FeatureBlock(feature, pt, property, -1, value));
                        int i = 0;
                        while (iterator.hasNext()) {
                            TreeItem<FeatureBlock> sub = buildTree((Feature) iterator.next(), showOp, showNull, visited);
                            sub.getValue().index = i;
                            ti.getChildren().add(sub);
                            i++;
                        }
                        item.getChildren().add(ti);
                    }

                } else if (value != null) {
                    TreeItem<FeatureBlock> ti = new TreeItem<>(new FeatureBlock(feature, pt, property, -1, value));
                    TreeItem<FeatureBlock> sub = buildTree((Feature) value, showOp, showNull, visited);
                    ti.getChildren().add(sub);
                    item.getChildren().add(ti);
                } else if (showNull) {
                    TreeItem<FeatureBlock> ti = new TreeItem<>(new FeatureBlock(feature, pt, property, -1, value));
                    item.getChildren().add(ti);
                }
            } else {
                if (value instanceof Collection) {
                    Collection col = (Collection) value;
                    if (showNull || (!showNull && !col.isEmpty())) {
                        final Iterator iterator = col.iterator();
                        TreeItem<FeatureBlock> ti = new TreeItem<>(new FeatureBlock(feature, pt, property, -1, value));
                        int i = 0;
                        while (iterator.hasNext()) {
                            TreeItem<FeatureBlock> sub = new TreeItem<>(new FeatureBlock(feature, pt, property, i, iterator.next()));
                            ti.getChildren().add(sub);
                            i++;
                        }
                        item.getChildren().add(ti);
                    }
                } else if (value != null) {
                    TreeItem<FeatureBlock> ti = new TreeItem<>(new FeatureBlock(feature, pt, property, -1, value));
                    item.getChildren().add(ti);
                } else if (showNull) {
                    TreeItem<FeatureBlock> ti = new TreeItem<>(new FeatureBlock(feature, pt, property, -1, value));
                    item.getChildren().add(ti);
                }
            }
        }


        return item;
    }

    boolean isEditable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void setEditable(boolean editable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    public final class FeatureBlock {

        public final Feature feature;
        public final org.opengis.feature.PropertyType propertyType;
        public final org.opengis.feature.Property property;
        public int index;
        public final Object value;

        public FeatureBlock(Feature feature, org.opengis.feature.PropertyType propertyType, org.opengis.feature.Property property, int index, Object value) {
            this.feature = feature;
            this.propertyType = propertyType;
            this.property = property;
            this.index = index;
            this.value = value;
        }

        public String getTitle() {
            return getTitle(fullNameVisible.getValue());
        }

        private String getTitle(boolean fullName) {
            String name = "";
            if (index >= 0) {
                name += "["+index+"] ";
            }
            if (propertyType != null) {
                name += fullName ? propertyType.getName().toString() : propertyType.getName().tip().toString();
            } else {
                name += fullName ? feature.getType().getName().toString() : feature.getType().getName().tip().toString();
            }
            return name;
        }

    }
}
